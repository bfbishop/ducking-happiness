package com.fidelity.dal.fundacct.feed.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.util.StopWatch;

import com.fidelity.dal.fundacct.dalapi.beans.FundPortfolioData;
import com.fidelity.dal.fundacct.feed.Exception.DALFeedException;
import com.fidelity.dal.fundacct.feed.admin.ApplicationContext;
import com.fidelity.dal.fundacct.feed.app.beans.CurrencyHoldingVO;
import com.fidelity.dal.fundacct.feed.app.decorator.CurrencyHoldingFormatter;
import com.fidelity.dal.fundacct.feed.bean.DALCalendar;
import com.fidelity.dal.fundacct.feed.bean.DALFeedConfiguration;
import com.fidelity.dal.fundacct.feed.bean.FileWriter;
import com.fidelity.dal.fundacct.feed.framework.db.ConfigDBAccess;
import com.fidelity.dal.fundacct.feed.framework.feed.FeedsUtil;
import com.fidelity.dal.fundacct.feed.framework.util.Constants;
import com.fidelity.dal.fundacct.feed.framework.util.Initialize;
import com.fidelity.dal.fundacct.feed.framework.util.Util;

public class EODCurrencyHoldingsFeed extends AbstractCurrencyHoldingsFeed {
	
	private FileWriter fwGeode = null;
	
	@Override
	public synchronized void processMessage(int event) throws Exception {
		
		Util.log(this,"Received Event "+event);
		boolean status = false;
		
		try{
			
			dfc = getConfiguration();
		}catch(Exception e){
			
			Util.log(this, "Exception thrown while constructing DALFeedConfiguration for the event "+event+"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
			throw e;
		}
		
		if(dfc == null) {
			
			Exception e = new Exception("DALFeed configuration is null");
			Util.log(this, "DALFeedConfiguration is null "+event+"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
			throw e;
		}
		
		try{
			stopWatch = new StopWatch(dfc.getAppName());
			Util.log(this,"Configuration Parameters: "+dfc.toString());
			Util.log(this,"Target events from ConfigDataCache:\n "+dfc.getTargetEvents());
			FileWriter fw = new FileWriter(dfc);

			
			if(isMatchingEvent(Constants.EOD_FUND_CURRHDG_SEC_TXN_IO_MM_FUNDS)){
			
				status = processMoneyMarket(dfc.getSourceCode(),Constants.MSG_SUBTYPE_EOD,getToday(),fw);
			}else if(isMatchingEvent(Constants.EOD_FUND_CURRHDG_SEC_TXN_IO_FIE_FUNDS)){
				
				status = processFixedIncomeEquity(dfc.getSourceCode(),Constants.MSG_SUBTYPE_EOD,getToday(),fw);
			}else{
			
				Exception e = new Exception("Unregistered Event Received. Event = "+event);
				Util.log(this, "Unregistered Event received"+event+"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				status = true;
			}
			logRunMetrics(this, Constants.LOG_TYPE_STATS,stopWatch.getTotalTimeSeconds(),recordCount,stopWatch.prettyPrint());
			logRunMetrics(this, Constants.LOG_TYPE_RESULT,stopWatch.getTotalTimeSeconds(), recordCount, status ?Constants.LOG_RESULT_SUCCESS:Constants.LOG_RESULT_FAIL);
		}catch(Throwable e){
			status = false;
			Util.log(this, "Exception thrown for the event "+event+"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
			throw new DALFeedException(this,getMessageToken(),e,dfc.getAppName());
		}finally{
			recordCount = 0;
			totalUnsettledSpots = 0;
			totalUnsettledForwards = 0;
			totalSettledSpots = 0;
			totalSettledForwards = 0;
			totalCurrBalanaces = 0;
			totalUnsettledCC = 0;
			totalSettledCC = 0;
			stopWatch = null;
			Util.log(this,"Completed handling the event  "+event+"[STATUS,"+(status?"SUCCESS":"FAILURE")+"]");
		}
	}
		
	public boolean processFixedIncomeEquity(String srcCode, String exttKndCode, Date runDay, FileWriter fw) throws Exception {
	
		Util.log(this,"Begin Currency Holdings EOD Feed generation for Fixed Income Equity");
		
		boolean isFeedGenerated = false;
		
		try{
			
			isFeedGenerated = doFeed(srcCode, exttKndCode,runDay,fw);
			if(isFeedGenerated){
				
				sendFile(fw);
			}
		}catch(Exception e){
			isFeedGenerated = false;
			throw e;
		}finally{
			Util.log(this,"Completed Currency Holdings EOD Feed generation for Fixed Income Equity with "+(isFeedGenerated?"SUCCESS":"FAILURE"));
		}
	    return isFeedGenerated;
	}

	
	public boolean processMoneyMarket(String srcCode, String exttKndCode, Date runDay,FileWriter fw) throws Exception {
		
		Util.log(this,"Begin Currency Holdings EOD Feed generation for Money Market");

		boolean isFeedGenerated = false;

		try{
			
			isFeedGenerated = doFeed(srcCode, exttKndCode,runDay,fw);
			if(isFeedGenerated){
				
				sendFile(fw);
			}
		}catch(Exception e){
			isFeedGenerated = false;
			throw e;
		}finally{
			Util.log(this,"Completed Currency Holdings EOD Feed generation for Fixed Income Equity with "+(isFeedGenerated?"SUCCESS":"FAILURE"));
		}
	    return isFeedGenerated;
	}

	
	public boolean doFeed(String srcCodes,String exttCode,Date runDay, FileWriter fw) throws Exception {
		
		boolean success = false;
		dalCalendar = (DALCalendar)ApplicationContext.getContext().getBean("DALCalendar");
		dfc.setOutputLocalFileName(dfc.getOutputLocalFileName()+".geode");
		fwGeode = new FileWriter(dfc);
		try{
			DateTime asOfDay = new DateTime(runDay);
			DateTime systemProcessDay = asOfDay;
			DateTime priorBusnDay = FeedsUtil.getPriorBusinessDay(dalCalendar,runDay,srcCodes,exttCode);
			DateTime _90DaysPrior = asOfDay.minusDays(90);
			String fileID = getFileId(srcCodes);
			DateTime today = new DateTime();
		
			if(StringUtils.equals(srcCodes,Constants.FUND_TYPE_INVESTONE_MM)){
				Util.log(this, "EODCurrencyHoldingsFeed: for MM Data: generating Empty Feed");
				//For MM, feed will be empty and will have only header/Trailer
				generateEmptyFeed(fw,fileID,asOfDay,today);
				return true;
			}
			String[] fundN = new String[0];
			Util.log(this, "EODCurrencyHoldingsFeed: getFundPortfolios:START ");
			stopWatch.start("FundPortfolio");
				Collection<FundPortfolioData> funds = getFundPortfolios(fundN,srcCodes,exttCode,asOfDay);
			stopWatch.stop();
			Util.log(this, "EODCurrencyHoldingsFeed: getFundPortfolios:END ");
			if(CollectionUtils.isEmpty(funds)){
				Util.log(this, "EODCurrencyHoldingsFeed: Fund Portfolio List is Empty: generating Empty Feed ");
				generateEmptyFeed(fw,fileID,asOfDay,today);
				return true;
			}
			
			Map<String,String> shortNameMap = new HashMap<String,String>();
			List<String> fundNumbers = new ArrayList<String>();
			Map<String,String> valDateMap = new HashMap<String, String>();
			cacheFromFunds(funds,fundNumbers,shortNameMap,valDateMap);

			Map<String,String> spotCusipMap = cacheFmrIDForSpots();
			Util.log(this, "EODCurrencyHoldingsFeed: settledTxnMap: START ");
			Map<String,Map<String,Object>> settledTxnMap = new HashMap<String,Map<String,Object>>();
			cacheTxnData(settledTxnMap,_90DaysPrior,asOfDay,srcCodes,exttCode,Constants.COST_BASIS_CODE_FA);
			Util.log(this, "EODCurrencyHoldingsFeed: settledTxnMap: END ");
			
			Util.log(this, "EODCurrencyHoldingsFeed: gatherRequiredData call: START ");
			List<CurrencyHoldingVO> currencyHoldingsList = gatherRequiredData(fundNumbers,shortNameMap,valDateMap,spotCusipMap,settledTxnMap,asOfDay,priorBusnDay,systemProcessDay,srcCodes,exttCode);
			Util.log(this, "EODCurrencyHoldingsFeed: gatherRequiredData call: END ");
			stopWatch.start("Write to Feed");
				writeToFeed(fw,fileID,asOfDay,today,currencyHoldingsList);
			stopWatch.stop();
			success = true;
			Util.log(this, "EODCurrencyHoldingsFeed: END PROCESSING");
			Util.log(this, "EODCurrencyHoldingsFeed: StopWatch: "+stopWatch.prettyPrint());
		}catch (Exception e) {
			
			Util.log(this, "Exception thrown for the feed "+dfc.getOutputLocalFileName()+"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
			success = false;
			throw e;
		}finally{
			recordCount = fw.getNoOfRecordsWritten();
			Util.log(this,"Number of records written to Feed "+recordCount);
			Util.log(this,"Total Unsettled SPOT = "+totalUnsettledSpots);
			Util.log(this,"Total Unsettled FORWARD = "+totalUnsettledForwards);
			Util.log(this,"Total Settled SPOT = "+totalSettledSpots);
			Util.log(this,"Total Settled FORWARD = "+totalSettledForwards);
			Util.log(this,"Total Currency Balances = "+totalCurrBalanaces);
			Util.log(this,"Total UnSettled Currency Contracts = "+totalUnsettledCC);
			Util.log(this,"Total Settled Currency Contracts = "+totalSettledCC);
			fw.close(false);
			fw = null;
			dalCalendar = null;
		}
		return success;
	}
	
	private void generateEmptyFeed(FileWriter fw,String fileID,DateTime asOfDay,DateTime today) {
		
		fw.writeln(getHeaderFooter("IMAHDR",fileID,asOfDay,today,fw.getNoOfRecordsWritten()));
		fw.writeln(getHeaderFooter("IMATRL",fileID,asOfDay,today,fw.getNoOfRecordsWritten()));
	}

	
	private void writeToFeed(FileWriter fw, String fileID, DateTime asOfDay,DateTime today, List<CurrencyHoldingVO> currencyHoldingsList) {
		
		fw.writeln(getHeaderFooter("IMAHDR",fileID,asOfDay,today,fw.getNoOfRecordsWritten()));
		fwGeode.writeln(getHeaderFooter("IMAHDR",fileID,asOfDay,today,fwGeode.getNoOfRecordsWritten()));
		if(CollectionUtils.isNotEmpty(currencyHoldingsList)){
			
			Iterator<CurrencyHoldingVO> iterator = currencyHoldingsList.iterator();
			while(iterator.hasNext()){
				CurrencyHoldingVO record = iterator.next();
				fw.writeRecord(CurrencyHoldingFormatter.format(record));
				if (geodeFunds.contains(record.getAcctNumber()))
					fwGeode.writeRecord(CurrencyHoldingFormatter.format(record));
			}
		}
		
		fw.writeln(getHeaderFooter("IMATRL",fileID,asOfDay,today,fw.getNoOfRecordsWritten()));
		fwGeode.writeln(getHeaderFooter("IMATRL",fileID,asOfDay,today,fwGeode.getNoOfRecordsWritten()));

	}

	private String getFileId(String srcCodes) {
		
		String fileID = "chdeodmm";
		
		if(StringUtils.contains(srcCodes,Constants.FUND_TYPE_INVESTONE_FIE)){
			
			fileID = "chdeodfe";
		}
		
		return fileID;
	}

	public Date getAsOfDay(){
		return getToday();
	}
	
	public void setStopWatch(StopWatch stopWatch){
		this.stopWatch = stopWatch;
	}
	
	public static void main(String[] args) {

		String appName = "CURREODHOLDINGSFIEFEED";
		new Initialize();
		EODCurrencyHoldingsFeed bodholdfeed = new EODCurrencyHoldingsFeed();
		ConfigDBAccess.getStartupApps(appName, null);
		bodholdfeed.setDFC(new DALFeedConfiguration(appName));
		bodholdfeed.setFeedAppName(appName);
		
		bodholdfeed.setStopWatch(new StopWatch());

		try {
			bodholdfeed.doFeed(bodholdfeed.getConfiguration().getSourceCode(),Constants.MSG_SUBTYPE_EOD, bodholdfeed.getToday(), new FileWriter(bodholdfeed.getConfiguration()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
}
