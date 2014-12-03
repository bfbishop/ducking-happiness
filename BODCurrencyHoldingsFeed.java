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
//import com.fidelity.dal.fundacct.feed.framework.util.GeodeHelper;
import com.fidelity.dal.fundacct.feed.framework.util.Initialize;
import com.fidelity.dal.fundacct.feed.framework.util.Util;

public class BODCurrencyHoldingsFeed extends AbstractCurrencyHoldingsFeed {
	
	private int recordCount = 0;
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
			//dfc.setOutputLocalFileName(dfc.getOutputLocalFileName()+".geode");
			//fwGeode = new FileWriter(dfc);
			if(isMatchingEvent(Constants.BOD_FUND_CURRHDG_SEC_TXN_IO_MM_FUNDS)){
				
				status = processMoneyMarket(dfc.getSourceCode(),Constants.MSG_SUBTYPE_BOD,getToday(),fw);
			}else if(isMatchingEvent(Constants.BOD_FUND_CURRHDG_SEC_TXN_IO_FIE_FUNDS)){
				
				status = processFixedIncomeEquity(dfc.getSourceCode(),Constants.MSG_SUBTYPE_BOD,getToday(),fw);
			}else{
				
				Exception e = new Exception("Unregistered Event Received. Event = "+event);
				Util.log(this, "Unregistered Event received"+event+"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				status = true;
			}
			/*try{
				if(isMatchingEvent(Constants.BOD_FUND_CURRHDG_SEC_TXN_IO_FIE_FUNDS)){
					GeodeHelper.initiateGeodeFeed(dfc.getAppName(), 97001);
				}
			} catch(Exception e){
				Util.log(dfc.getAppName() +" Exception occured while triggering Geode feed.", Constants.LOG_LEVEL_ERROR, e);
			}*/
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
	
		Util.log(this,"Begin Currency Holdings BOD Feed generation for Fixed Income Equity");
		
		boolean isFeedGenerated = false;
		try{
			isFeedGenerated = doFeed(srcCode, exttKndCode, runDay,fw);
			if(isFeedGenerated){
			
				sendFile(fw);
			}
		}catch(Exception e){
			
			isFeedGenerated = false;
			throw e;
		}finally{
			
			Util.log(this,"Completed Currency Holdings BOD Feed generation for Fixed Income Equity with "+(isFeedGenerated?"SUCCESS":"FAILURE"));
		}
	    return isFeedGenerated;
	}

	public boolean processMoneyMarket(String srcCode, String exttKndCode, Date runDay, FileWriter fw) throws Exception {
		
		Util.log(this,"Begin Currency Holdings BOD Feed generation for Money Market");

		boolean isFeedGenerated = false;
		
		try{
			
			srcCode = dfc.getSourceCode();
			isFeedGenerated = doFeed(srcCode, exttKndCode, runDay,fw);
			if(isFeedGenerated){
			
				sendFile(fw);
			}
		}catch(Exception e){
			
			isFeedGenerated = false;
			throw e;
		}finally{
			
			Util.log(this,"Completed Currency Holdings BOD Feed generation for Money Market with "+(isFeedGenerated?"SUCCESS":"FAILURE"));
		}
		return isFeedGenerated;
	}

	public boolean doFeed(String srcCodes, String exttCode, Date runDay, FileWriter fw) throws Exception {
		dfc.setOutputLocalFileName(dfc.getOutputLocalFileName()+".geode");
		fwGeode = new FileWriter(dfc);
		boolean success = false;
		dalCalendar = (DALCalendar)ApplicationContext.getContext().getBean("DALCalendar");
		try{
			Date asOfDate = dalCalendar.getNextCalendarDay(runDay);
			DateTime asOfDay = new DateTime(asOfDate);
			DateTime priorBusnDay = FeedsUtil.getPriorBusinessDay(dalCalendar,runDay,srcCodes,exttCode);
			DateTime _90DaysPrior = asOfDay.minusDays(90);
			DateTime systemProcessDay = new DateTime(runDay);
			String fileID = getFileId(srcCodes);
			DateTime headerDay = new DateTime(runDay);
			DateTime today = new DateTime();	
			
			if(StringUtils.equals(srcCodes,Constants.FUND_TYPE_INVESTONE_MM)){
				
				//For MM, feed will be empty and will have only header/Trailer
				generateEmptyFeed(fw,fileID,headerDay,today);
				return true;
			}
			
			String[] fundN = new String[0];
			Util.log(this, "BODCurrencyHoldingsFeed: getFundPortfolios:START ");
			stopWatch.start("FundPortfolio");
				Collection<FundPortfolioData> funds = getFundPortfolios(fundN,srcCodes,exttCode,asOfDay);
			stopWatch.stop();
			Util.log(this, "BODCurrencyHoldingsFeed: getFundPortfolios:END ");
			if(CollectionUtils.isEmpty(funds)){
				
				generateEmptyFeed(fw,fileID,headerDay,today);
				return true;
			}
   		
			Map<String,String> shortNameMap = new HashMap<String,String>();
			List<String> fundNumbers = new ArrayList<String>();
			Map<String,String> valDateMap = new HashMap<String, String>();
			cacheFromFunds(funds,fundNumbers,shortNameMap, valDateMap);
			
			//Map<String,String> valDateMap = getValDate(funds);

			Map<String,String> spotCusipMap = cacheFmrIDForSpots();
			Util.log(this, "BODCurrencyHoldingsFeed: settledTxnMap: START ");
			Map<String,Map<String,Object>> settledTxnMap = new HashMap<String,Map<String,Object>>();
			cacheTxnData(settledTxnMap,_90DaysPrior,asOfDay,srcCodes,exttCode,Constants.COST_BASIS_CODE_FA);
			Util.log(this, "BODCurrencyHoldingsFeed: settledTxnMap: END ");
			
			Util.log(this, "BODCurrencyHoldingsFeed: gatherRequiredData call: START ");
			List<CurrencyHoldingVO> currencyHoldingsList = gatherRequiredData(fundNumbers,shortNameMap,valDateMap,
																			  spotCusipMap,settledTxnMap,asOfDay,
																			  priorBusnDay,systemProcessDay,srcCodes,exttCode);
			Util.log(this, "BODCurrencyHoldingsFeed: gatherRequiredData call: END ");
			stopWatch.start("Write to Feed");
				writeToFeed(fw,fileID,headerDay,today,currencyHoldingsList);
			stopWatch.stop();
			success = true;
			Util.log(this, "BODCurrencyHoldingsFeed: END PROCESSING");
			Util.log(this, "BODCurrencyHoldingsFeed: StopWatch: "+stopWatch.prettyPrint());
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
		
		String fileID = "chdbodmm";
		
		if(StringUtils.contains(srcCodes,Constants.FUND_TYPE_INVESTONE_FIE)){
			
			fileID = "chdbodfe";
		}
		
		return fileID;
	}

	public Date getAsOfDay(){
		return getToday();
	}
	
	public void setStopWatch(StopWatch stopWatch){
		this.stopWatch = stopWatch;
	}
	
	public static void main(String args[]) {
		String appName = "CURRBODHOLDINGSFIEFEED";
		new Initialize();
		BODCurrencyHoldingsFeed bodCurrencyHoldings = new BODCurrencyHoldingsFeed();
		bodCurrencyHoldings.stopWatch = new StopWatch();
		ConfigDBAccess.getStartupApps(appName, null);
		bodCurrencyHoldings.setDFC(new DALFeedConfiguration(appName));
		bodCurrencyHoldings.setFeedAppName(appName);
		try {
			bodCurrencyHoldings.doFeed("IOFIE","BOD",bodCurrencyHoldings.getToday(),new FileWriter(new DALFeedConfiguration(appName)) );
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
}
