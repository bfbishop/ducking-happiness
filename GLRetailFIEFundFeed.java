package com.fidelity.dal.fundacct.feed.app;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StopWatch;

import com.fidelity.dal.fundacct.dalapi.beans.FundPortfolioData;
import com.fidelity.dal.fundacct.dalapi.beans.GlAccountData;
import com.fidelity.dal.fundacct.dalapi.criteria.Criteria;
import com.fidelity.dal.fundacct.dalapi.criteria.FundAccountBalanceCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.GlAccountCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.Operator;
import com.fidelity.dal.fundacct.dalapi.criteria.OrderByCriteria;
import com.fidelity.dal.fundacct.feed.Exception.DALFeedException;
import com.fidelity.dal.fundacct.feed.admin.ApplicationContext;
import com.fidelity.dal.fundacct.feed.bean.DALCalendar;
import com.fidelity.dal.fundacct.feed.bean.DALFeedConfiguration;
import com.fidelity.dal.fundacct.feed.bean.FileWriter;
import com.fidelity.dal.fundacct.feed.bean.FundPortfolio;
import com.fidelity.dal.fundacct.feed.bean.GLAccount;
import com.fidelity.dal.fundacct.feed.framework.db.ConfigDBAccess;
import com.fidelity.dal.fundacct.feed.framework.feed.FeedsUtil;
import com.fidelity.dal.fundacct.feed.framework.jms.JMSMessageProcessorBase;
import com.fidelity.dal.fundacct.feed.framework.util.Constants;
import com.fidelity.dal.fundacct.feed.framework.util.Initialize;
import com.fidelity.dal.fundacct.feed.framework.util.Util;

/**
 * This Class will be invoked by the DAL Feed FrameWork as part of the EOD Orchestration.
 * It will generate the General Ledger feed file fpcmsdal.gl.fie, which contains the following GL related information
 *      FUND-NUMBER
 *  	GL-ACCT-NUMBER
 *  	CLASS-ID
 *  	CURRENT-DAYS-BALANCE
 *  	PRIOR-DAYS-BALANCE
 *  	ORIGINATING-SYSTEMS
 *      FISCAL-YEAR-END-INDICATOR
 *
 *  The Sample Data file looks as follows
 *  IMAHDR FPCMSDAL GLBALFIE 20120229 20120702 215216 000000000000000
 *  0000000000006951100-0000-0000-0000000+000014791951570.5400+000016377398357.9400I
 *  0000000000006951101-0000-0000-0000000+000000000375000.0000+000000000378150.0000I
 *  0000000000006951110-0000-0000-0000000+000002695743148.9700+000000657458023.6800I
 *  0000000000006951120-0000-0000-0000000+000000000000000.0000+000000000000000.0000I
 *  0000000000006951130-0000-0000-0000000-000000002281492.3000-000000002281492.3000I
 *  0000000000006951130-0000-1596-0000000+000000000023906.3100+000000000023906.3100I
 *  0000000000006951130-0000-1597-0000000+000000000107238.7000+000000000107238.7000I
 *  0000000000006951400-0000-1000-0000000+000000018833995.5700+000000023637505.1100I
 *  0000000000006951400-0000-2000-0000000+000000000001150.0000+000000000001150.0000I
 *  0000000000006951500-0000-0001-0000000+000000000000000.0000+000010549907000.0000I
 *  IMATRL FPCMSDAL GLBALFIE 20120229 20120702 215216 000000000000011
 *
 * @author A511426
 *
 */

public class GLRetailFIEFundFeed extends JMSMessageProcessorBase {

    private static final HashSet<Long> geodeFunds = new HashSet<Long>(Arrays.asList(new Long[]{
            2250L,
            2251L,
            2278L,
            2249L,
            2215L,
            1846L,
            2612L,
            2341L,
            2010L,
            1827L,
            1829L,
            1828L,
            2012L,
            1282L,
            1283L,
            2356L,
            2011L,
            2349L,
            2345L,
            398L,
            399L,
            2353L,
            397L,
            650L,
            1529L,
            157L
	 }));

	private final String HEADER_TRAILER_FORMAT = "%1s%8s%1s%8s%1s%8s%1s%8s%1s%6s%1s";
	private final String FILENAME = "GLBALFIE";
	private String asOfDate         = null;
	private String fundEndDate      = null;
	private Date prevBusDate      = null;
	private String yearEndIndicator = null;
	private String sourceSystem     = null;
	private String shareClassNumber = null;
	private String generalLedgerRecord = null;
	private String convDate    = null;
	private Date   currentDate      = null;
	private Date   currBusDate      = null;
	private Long fundNumber = null;

	private SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat timeFormat1 = new SimpleDateFormat("HHmmss");
	private DateTimeFormatter dft1       = DateTimeFormat.forPattern("yyyyMMdd");
	private DecimalFormat fundNumberFormat  = new DecimalFormat("000000000000000");
	private DecimalFormat amountFormat      = new DecimalFormat("000000000000000.0000");
	private DecimalFormat shareClassFormat  = new DecimalFormat("000");
	private DecimalFormat recordCountFormat = new DecimalFormat("000000000000000");
	
	private int recordCount = 0;
	private FileWriter fw   = null;
	private FileWriter fwGeode = null;
	private StopWatch stopWatch = null;
		

	@Override
	protected synchronized void processMessage(int event) throws Exception {

		boolean status = false;
		
		Util.log(this, "inside GLRetailFIEFundFeed.processMessage(): event published by Orchestration:" + event + ":");
		try{
			
			dfc = getConfiguration();
			recordCount=0;
			stopWatch = new StopWatch(dfc.getAppName());
			Util.log(this, "GLRetailFIEFundFeed configuration parameters : " + dfc);
			status = doFeed(getToday(),Constants.MSG_SUBTYPE_EOD,dfc.getSourceCode(),Constants.COST_BASIS_CODE_FA);
			if(status) {
				
				sendFile(fw);
				sendFile(fwGeode);
			}
			logRunMetrics(this, Constants.LOG_TYPE_STATS,stopWatch.getTotalTimeSeconds(),recordCount,stopWatch.prettyPrint());
			logRunMetrics(this, Constants.LOG_TYPE_RESULT,stopWatch.getTotalTimeSeconds(), recordCount, status ?Constants.LOG_RESULT_SUCCESS:Constants.LOG_RESULT_FAIL);
		}catch(Throwable e){
			
			status = false;
			Util.log(this, "Exception thrown for the event "+event+"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
			throw new DALFeedException(this,getMessageToken(),e,dfc.getAppName());
		}finally{
			
			recordCount = 0;
			stopWatch = null;
			fw   = null; //moved from cleanUp(), to facilitate FACDAL-2672
			fwGeode = null;
			Util.log(this,"Completed handling the event  "+event+"[STATUS,"+(status?"SUCCESS":"FAILURE")+"]");
		}
	}

	
	public boolean doFeed(Date asOfDate,String exttKndCode,String srcCode,String cstBasisCode) throws Exception {

		boolean status = false;
		try {
	    	initialize();
	    	DALCalendar dalCalendar	= (DALCalendar) ApplicationContext.getContext().getBean("DALCalendar");
			// Get the list of Fund #s.
			String[] fundNumberSet = {};
			DateTime asOfDay = new DateTime(asOfDate);
			String strAsOfDay = asOfDay.toString(dft1);
			Date priorDate = dalCalendar.getPriorBusinessDay(currBusDate, DALCalendar.SourceCalendar.IOFIE.id);
			DateTime prAsOfDay = new DateTime(priorDate);
			String strPrAsOfDay = prAsOfDay.toString(dft1);
			fw = new FileWriter(dfc);
			dfc.setOutputLocalFileName(dfc.getOutputLocalFileName()+".geode");
			fwGeode = new FileWriter(dfc);
			String hdrTrlCommon = getHdrTrlCommonPart(HEADER_TRAILER_FORMAT);
			
			stopWatch.start("getGeneralLedgerData for current day");
				Collection<GlAccountData> fundAccountBalanceDataList = getGeneralLedgerData(fundNumberSet,strAsOfDay,exttKndCode,srcCode,cstBasisCode);
				clearDuplicates(fundAccountBalanceDataList); //20140210 production issue
			stopWatch.stop();
			
			//For FACDAL-2091
			stopWatch.start("getGeneralLedgerPrevBusDayData");
				Collection<GlAccountData> fundAccountBalanceDataListPrevBusDay = getGeneralLedgerData(fundNumberSet,strPrAsOfDay,exttKndCode,srcCode,cstBasisCode);
				clearDuplicates(fundAccountBalanceDataListPrevBusDay);
			stopWatch.stop();
			
			if(CollectionUtils.isEmpty(fundAccountBalanceDataList) && CollectionUtils.isEmpty(fundAccountBalanceDataListPrevBusDay)){
				
				Util.log(this,"There are no funds available. Empty file will be created");
				createEmptyFile(fw,hdrTrlCommon,0);
				return true;
			}
			
			stopWatch.start("getFundPortfolioData");
				Collection<FundPortfolioData> fundPortfolioDataList = getFundPortfolioData(strAsOfDay,exttKndCode,srcCode,cstBasisCode);
				Collection<FundPortfolioData> fundPortfolioDataListPrevBusDay = getFundPortfolioData(strPrAsOfDay,exttKndCode,srcCode,cstBasisCode);
			stopWatch.stop();
			
			HashMap<String,GLRetailFundBean> fundDateMap = cacheDateDataFromFundPort(fundPortfolioDataList,fundPortfolioDataListPrevBusDay);
			
			//Map<String,Date[]> fiscalConvDateMap = new HashMap<String,Date[]>(); //0-FISCAL YEAR END DATE, 1 - CONVERSION DATE
			//cacheDataFrmFundPort(fundPortfolioDataList,fiscalConvDateMap);
			
			// Read the Collection and Write the Output to file.
			recordCount = fw.getNoOfRecordsWritten();
			stopWatch.start("writeGeneralLedgerDateToFile");
				fw.writeln(new StringBuilder().append("IMAHDR").append(hdrTrlCommon).append(recordCountFormat.format(recordCount)).toString());
				fwGeode.writeln(new StringBuilder().append("IMAHDR").append(hdrTrlCommon).append(recordCountFormat.format(fwGeode.getNoOfRecordsWritten())).toString());
				recordCount = writeGeneralLedgerDataToFile(fundAccountBalanceDataList, fundAccountBalanceDataListPrevBusDay, fundDateMap);
				fw.writeln(new StringBuilder().append("IMATRL").append(hdrTrlCommon).append(recordCountFormat.format(recordCount)).toString());
				fwGeode.writeln(new StringBuilder().append("IMATRL").append(hdrTrlCommon).append(recordCountFormat.format(fwGeode.getNoOfRecordsWritten())).toString());
			stopWatch.stop();			

			// Clear the Collections.
			fundAccountBalanceDataList = null;
			fundAccountBalanceDataListPrevBusDay = null;
			fundPortfolioDataList=null;
			//fiscalConvDateMap=null;
			status = true;
	    } catch (Exception e) {
	    	
	    	status = false;
	    	Util.log(this, e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
	    	throw e;
	    } finally {
	    	
			if(fw != null){
				
				fw.close(false);
				fwGeode.close(false);
			}
			cleanUp();
	    }
		return status;
	}


	private void clearDuplicates(
			Collection<GlAccountData> fundAccountBalanceDataList) {
		//if there is a duplicate, delete the one where port_fund_n = fund_n
		Iterator<GlAccountData> iter = fundAccountBalanceDataList.iterator();
		String prevKey = "";
		while (iter.hasNext()) {
			GlAccountData gl = iter.next();
			String key = gl.getPortfolioFundNumber() + "|" + gl.getShareClassNumber() + "|" + gl.getGeneralLedgerAccountNumber();
			if (key.equals(prevKey)) {
				iter.remove();
			}
			prevKey = key;
		}
	}

	/**
	 * Below method caches the required data from FundPort folio
	 * @param fundPortfolioDataList
	 * @param fundPortfolioDataListPrevBusD
	 */
	private HashMap<String, GLRetailFundBean> cacheDateDataFromFundPort(Collection<FundPortfolioData> fundPortfolioDataList,
			Collection<FundPortfolioData> fundPortfolioDataListPrevBusD) {
		HashMap<String, GLRetailFundBean> fundDateMap = new HashMap<String, GLRetailFundBean>();
		
		for(FundPortfolioData fundPortfolioData : fundPortfolioDataList) {
			Long fundNumber = fundPortfolioData.getFundNumber();
			if(fundNumber != null){
				String fundN = String.valueOf(fundNumber);
				GLRetailFundBean temp = new GLRetailFundBean();
				temp.setCnvD(fundPortfolioData.getConversionDate());
				temp.setCurrValD(fundPortfolioData.getVALD());
				fundDateMap.put(fundN,temp);
			}
		}
		
		for(FundPortfolioData fundPortfolioData : fundPortfolioDataListPrevBusD) {
			Long fundNumber = fundPortfolioData.getFundNumber();
			if(fundNumber != null){
				String fundN = String.valueOf(fundNumber);
				if (fundDateMap.containsKey(fundN)) {
					GLRetailFundBean temp = fundDateMap.get(fundN);
					temp.setEndFiscYrD(fundPortfolioData.getEndFiscalYearDate());
					temp.setPriorValD(fundPortfolioData.getVALD());
				}
			}
		}
		return fundDateMap;
	}

	private void initialize() throws Exception{
		
		convDate		 = null;
		yearEndIndicator = " ";
		sourceSystem     = "";
		shareClassNumber = "";
		generalLedgerRecord = "";
		currentDate      = new Date();
		fundNumber = null;
		fw   = null;
		fwGeode = null;
		DALCalendar dalCalendar	= (DALCalendar) ApplicationContext.getContext().getBean("DALCalendar");
		amountFormat.setPositivePrefix("+");
		DateTime asOfDay  = new DateTime(getToday());
		asOfDate          = asOfDay.toString(dft1);
		currBusDate  	  = dateFormat1.parse(asOfDate);
		prevBusDate  	  = dalCalendar.getPriorBusinessDay(currBusDate, DALCalendar.SourceCalendar.IOFIE.id);
	}
	
	private void cleanUp() {
		
		asOfDate         = null;
		convDate		 = null;
		yearEndIndicator = null;
		prevBusDate      = null;
		sourceSystem     = null;
		shareClassNumber = null;
		generalLedgerRecord = null;
		currentDate      = null;
		currBusDate      = null;
		fundNumber = null;
		asOfDate   = null;
		currBusDate = null;
		prevBusDate = null;
	}
	
	private void createEmptyFile(FileWriter fw, String hdrTrlCommon, int recordCount) {
		
		fw.writeln(new StringBuilder()
						.append("IMAHDR")
						.append(hdrTrlCommon)
						.append(recordCountFormat.format(recordCount)).toString());
		
		fw.writeln(new StringBuilder()
						.append("IMATRL")
						.append(hdrTrlCommon)
						.append(recordCountFormat.format(recordCount)).toString());
		
	}
	
	private String getHdrTrlCommonPart(String format) {
		
		String hostName = Util.getHostName();
		if(StringUtils.isNotBlank(hostName) && hostName.length() > 8){
			hostName = hostName.substring(0,8);
		}
		return String.format(format,"",hostName,"",FILENAME,"",dateFormat1.format(currBusDate),"",dateFormat1.format(currentDate),"",timeFormat1.format(currentDate),"");
	}

	private Collection<GlAccountData> getGeneralLedgerData(String[] fundNumberSet,String strAsOfDay,String exttKndCode,String srcCode, String cstBasisCode)  throws Exception {

		Criteria glAccountCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addAsOfDateFilter(strAsOfDay,Operator.OPERATOR_EQ));
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addSourceCodeFilter(srcCode,Operator.OPERATOR_IN));
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addCostBasisCodeFilter(cstBasisCode,Operator.OPERATOR_EQ));
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addExtractKindCodeFilter(exttKndCode,Operator.OPERATOR_EQ));
		
		Util.log(this, "General Ledger Data Retrieval : " + glAccountCriteria.toString());
		OrderByCriteria orderByCriteria = (OrderByCriteria)ApplicationContext.getContext().getBean("OrderByCriteria");
		orderByCriteria.addOrderByOn(GlAccountData.FIELD_portfolioFundNumber).asc();
		orderByCriteria.addOrderByOn(GlAccountData.FIELD_generalLedgerAccountNumber).asc();
		orderByCriteria.addOrderByOn(GlAccountData.FIELD_shareClassNumber).asc();
		orderByCriteria.addOrderByOn(GlAccountData.FIELD_fundNumber).asc();
		
		GLAccount glAccount = (GLAccount)ApplicationContext.getContext().getBean("GLAccount");
		Collection<GlAccountData> results = null;
		if(ArrayUtils.isEmpty(fundNumberSet)){
			
			results = glAccount.getGlAccountSetByFundNumber(new String[0], glAccountCriteria, orderByCriteria);
		}else{
			
			results = glAccount.getGlAccountSetByFundNumber(fundNumberSet, glAccountCriteria, orderByCriteria);
		}

		Util.log(this,"Total GL Account Data for current day = "+(results != null ? results.size() : 0));
		return results;
	}
	
	private Collection<FundPortfolioData> getFundPortfolioData(String asOfDay,String exttKndCode,String srcCode,String cstBasisCode) throws Exception {

		Util.log(this,"Begin gathering FundPortfolio Data");
		
		Criteria fundPortfolioCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		fundPortfolioCriteria.addCriterion(FundAccountBalanceCriteriaBuilder.addAsOfDateFilter(asOfDay,Operator.OPERATOR_EQ));
		fundPortfolioCriteria.addCriterion(FundAccountBalanceCriteriaBuilder.addSourceCodeFilter(srcCode,Operator.OPERATOR_IN));
		fundPortfolioCriteria.addCriterion(FundAccountBalanceCriteriaBuilder.addCostBasisCodeFilter(cstBasisCode,Operator.OPERATOR_EQ));
		fundPortfolioCriteria.addCriterion(FundAccountBalanceCriteriaBuilder.addExtractKindCodeFilter(exttKndCode,Operator.OPERATOR_EQ));
		
		Util.log(this, "Fund Portfolio Data Retrieval : " + fundPortfolioCriteria.toString());
		OrderByCriteria fundOrderByCriteria = (OrderByCriteria)ApplicationContext.getContext().getBean("OrderByCriteria");
		fundOrderByCriteria.addOrderByOn(FundPortfolioData.FIELD_fundNumber).asc();
		
		FundPortfolio fundPortfolio = (FundPortfolio)ApplicationContext.getContext().getBean("FundPortfolio");
		Collection<FundPortfolioData> funds = fundPortfolio.getFundPortfolioSet(new String[0], fundPortfolioCriteria, fundOrderByCriteria);
		
		Util.log(this,"Total funds retrieved = "+(funds != null ? funds.size() : 0));
		return funds;
				
	}
	
	private int writeGeneralLedgerDataToFile(Collection<GlAccountData> GlAccountDataList, Collection<GlAccountData> GlAccountDataPrevDayList,HashMap<String, GLRetailFundBean> fundDateMap) {
		
		
		generalLedgerRecord = "";
		Map<String, String> prevDayEndBalAmtMap = new HashMap<String, String>();
		//Start Gather Prev Day Data into a Map
		for (GlAccountData glAccountPrevDayData : GlAccountDataPrevDayList) {

			if(glAccountPrevDayData == null){
				
				continue;
			}
			Double prevEndBalAmt = glAccountPrevDayData.getEndBalanceAmount();
			if(glAccountPrevDayData.getGeneralLedgerAccountNumber() != null
					&& glAccountPrevDayData.getFundNumber() != null
					&& glAccountPrevDayData.getShareClassNumber() !=null
					&& glAccountPrevDayData.getPortfolioFundNumber() !=null) {
				String key = glAccountPrevDayData.getGeneralLedgerAccountNumber()+glAccountPrevDayData.getFundNumber()+glAccountPrevDayData.getPortfolioFundNumber()
						     +glAccountPrevDayData.getShareClassNumber();
				String value =  (prevEndBalAmt != null) ? amountFormat.format(prevEndBalAmt).toString().replaceAll("\\.", ""):
					amountFormat.format(new Double(0.0)).toString().replaceAll("\\.", "")	;
				if(!prevDayEndBalAmtMap.containsKey(key)){
					prevDayEndBalAmtMap.put(key, value);
				}
			}
		}
		//End Gather Prev Day Data into Map
		for(GlAccountData glAccountData: GlAccountDataList) {

			if(glAccountData == null){
				
				continue;
			}
			
			Double currStartBalAmt = glAccountData.getStartBalanceAmount();
			yearEndIndicator = " ";
			fundNumber       = glAccountData.getFundNumber();
			String fNumber = String.valueOf(fundNumber).trim();
			shareClassNumber = glAccountData.getShareClassNumber();
			String srcCode = glAccountData.getSourceCode();
			sourceSystem = (srcCode != null) ? srcCode.substring(0,1):null;	
			
			if(shareClassNumber == null || shareClassNumber.equalsIgnoreCase("null")){
				
				shareClassNumber = StringUtils.leftPad("", 3);
			}else{
				
				if("I".equals(sourceSystem)) {
					
					shareClassNumber = shareClassFormat.format(Integer.valueOf(shareClassNumber));
					
				}else{
					shareClassNumber = StringUtils.leftPad(shareClassNumber, 3);
				}
			}
			
			GLRetailFundBean dates = null;
			Date fiscalYrEndDate = null;
			Date mConvDate = null;
			Date priorValD = null;
			Date currValD = null;
			
			dates = fundDateMap.get(fNumber);
			if(dates != null){
				fiscalYrEndDate = dates.getEndFiscYrD();
				mConvDate = dates.getCnvD();
				priorValD = dates.getPriorValD();
				currValD = dates.getCurrValD();
			}

			
			if(priorValD != null && currValD != null){
				if(currValD.after(fiscalYrEndDate) && !priorValD.after(fiscalYrEndDate) ) {
					yearEndIndicator = "Y";
				} 
			} else if (fiscalYrEndDate != null && sourceSystem.equals("F")) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(fiscalYrEndDate);
				cal.add(Calendar.YEAR, -1);
				if (currBusDate.after(cal.getTime()) && !prevBusDate.after(cal.getTime())) {
					yearEndIndicator = "Y";
				}
			}
			
			//Added for FACDAL-2091 -Start
			boolean isConvDate = false;
			if(mConvDate != null) {
				
				convDate  = dateFormat1.format(mConvDate);
				if(convDate.endsWith(asOfDate)) {
						
					isConvDate= true;			
				}
			}
			
			String priorDayBalance=null;
			if ("I".equals(sourceSystem)) {
				
				if (isConvDate) {
					
					priorDayBalance = (currStartBalAmt != null) ? amountFormat.format(currStartBalAmt).toString().replaceAll("\\.", ""):
						  										  amountFormat.format(new Double(0.0)).toString().replaceAll("\\.", "");
				}else{
					
					if(glAccountData.getGeneralLedgerAccountNumber() != null
							&& glAccountData.getFundNumber() != null
							&& glAccountData.getShareClassNumber() !=null
							&& glAccountData.getPortfolioFundNumber() !=null) {
						String key = glAccountData.getGeneralLedgerAccountNumber()+glAccountData.getFundNumber()+glAccountData.getPortfolioFundNumber()
							    	+glAccountData.getShareClassNumber();
						if(prevDayEndBalAmtMap.get(key) != null) {
							priorDayBalance = prevDayEndBalAmtMap.get(key);
						} else {
							priorDayBalance = amountFormat.format(new Double(0.0)).toString().replaceAll("\\.", "");
						}
					}
				}
			}
			else{
				priorDayBalance =  (currStartBalAmt != null) ? amountFormat.format(currStartBalAmt).toString().replaceAll("\\.",   ""):
					   											amountFormat.format(new Double(0.0)).toString().replaceAll("\\.",   "");
			}
			
			// FACDAL-2753
			// null was printed in ITF for feed GENERAL_LEDGER_FIE_FUNDS
			if (priorDayBalance == null) {
				priorDayBalance = amountFormat.format(new Double(0.0)).toString().replaceAll("\\.", "");
			}
			
			Long portFundN = glAccountData.getPortfolioFundNumber() != null ? glAccountData.getPortfolioFundNumber() : 0L;
			String glAcctN = glAccountData.getGeneralLedgerAccountNumber() != null ? glAccountData.getGeneralLedgerAccountNumber() : " ";
			Double endBalA = glAccountData.getEndBalanceAmount() != null ? glAccountData.getEndBalanceAmount() : 0.0;
			String sourceCode = glAccountData.getSourceCode() != null ? glAccountData.getSourceCode().substring(0, 1): " ";
			
			generalLedgerRecord = FeedsUtil.createKeyFromTokens(new String[]{
																	fundNumberFormat.format(portFundN),
																	StringUtils.rightPad(glAcctN,16).replaceAll("-",  ""),
																	shareClassNumber,
																	amountFormat.format(endBalA).toString().replaceAll("\\.",   ""),
																	priorDayBalance,
																	sourceCode,
																	yearEndIndicator
																});
			fw.writeRecord(generalLedgerRecord);
			if (geodeFunds.contains(portFundN)) {
				fwGeode.writeRecord(generalLedgerRecord);
			}
		}
		//added for additional logic for FACDAL-2091
		//getMissingGLAccountFromCurrentDay(GlAccountDataList,GlAccountDataPrevDayList,fiscalConvDateMap);		

		return fw.getNoOfRecordsWritten();
	}
	
	/**
	 * logic to add accounts to the feed where GL_ACC_SET_VW.END_BAL_A from the prior business day <> 0 
	 * if they are absent from GL_ACC_SET_VW on the current business day.
	 *  CURRENT-DAYS-BALANCE should default to 0 for these records and PRIOR-DAYS-BALANCE will be equal to END_BAL_A from the prior business day. 
	 *  All other fields will follow the current mapping, except values need to be taken from GL_ACC_SET_VW as of the prior business day
	 *  since these accounts won't exist in the GL view for the current day.
	 * @param GlAccountDataList
	 * @param GlAccountDataPrevDayList
	 * @param fiscalConvDateMap
	 */
	private void getMissingGLAccountFromCurrentDay(Collection<GlAccountData> GlAccountDataList, 
												   Collection<GlAccountData> GlAccountDataPrevDayList,
												   Map<String,Date[]> fiscalConvDateMap){		
		Double currentDaysBalance = null;
		Double priorDayBalance = null;
		boolean isPresent=false;
		for (GlAccountData glAccountPrevDayData : GlAccountDataPrevDayList) {		
			
			String glAccountNumber = glAccountPrevDayData != null ? glAccountPrevDayData.getGeneralLedgerAccountNumber() : null;
			
			for (GlAccountData glAccountCurrDayData : GlAccountDataList) {
				
				isPresent=false;
				if( glAccountNumber !=null && glAccountCurrDayData != null &&
					glAccountCurrDayData.getGeneralLedgerAccountNumber()!=null && 
					glAccountCurrDayData.getGeneralLedgerAccountNumber().equals(glAccountNumber)){
					
					isPresent=true;
					break;
				}
			}	
			
			if(!isPresent){
				
				if(glAccountPrevDayData != null && glAccountPrevDayData.getEndBalanceAmount() != 0){
				
					currentDaysBalance= new Double(0.0);
					priorDayBalance = glAccountPrevDayData.getEndBalanceAmount();	
					if(priorDayBalance == null){
						
						priorDayBalance = new Double(0.0);
					}
					
					yearEndIndicator = " ";
					fundNumber       = glAccountPrevDayData.getFundNumber();
					String fNumber = String.valueOf(fundNumber).trim();
					shareClassNumber = glAccountPrevDayData.getShareClassNumber();
					sourceSystem = (glAccountPrevDayData.getSourceCode() != null) ? glAccountPrevDayData.getSourceCode().substring(0,1):"";
					
					if(shareClassNumber == null || shareClassNumber.equalsIgnoreCase("null")){
						
						shareClassNumber = StringUtils.leftPad("", 3);
					}else{
						
						if("I".equals(sourceSystem)) {
							
							shareClassNumber = shareClassFormat.format(Integer.valueOf(shareClassNumber));
						} else {
							
							shareClassNumber = StringUtils.leftPad(shareClassNumber, 3);
						}
					}
					
					if(fiscalConvDateMap != null){
						
						Date[] dates = fiscalConvDateMap.get(fNumber);
						if(dates != null){
							
							Date fiscalYrEndDate = dates[0];
							if(fiscalYrEndDate != null){
								
								fundEndDate = dateFormat1.format(fiscalYrEndDate);
								if(prevBusDate != null && prevBusDate.equals(fundEndDate)) {
									
									yearEndIndicator = "Y";
								}
							}
						}
					}
					
					Long portFundN = glAccountPrevDayData.getPortfolioFundNumber() != null ? glAccountPrevDayData.getPortfolioFundNumber() : 0L;
					String glAcctN = glAccountPrevDayData.getGeneralLedgerAccountNumber() != null ? glAccountPrevDayData.getGeneralLedgerAccountNumber():"";
					
					generalLedgerRecord = FeedsUtil.createKeyFromTokens(new String[] {
															fundNumberFormat.format(portFundN),
															StringUtils.rightPad(glAcctN,16).replaceAll("-",  ""),
															shareClassNumber,
															amountFormat.format(currentDaysBalance).toString().replaceAll("\\.",   ""),
															amountFormat.format(priorDayBalance).toString().replaceAll("\\.",   ""),
															sourceSystem,
															yearEndIndicator
															});	
					fw.writeRecord(generalLedgerRecord);
					if (geodeFunds.contains(portFundN)) {
						fwGeode.writeRecord(generalLedgerRecord);
					}
				}
			}	
		}		
	}
	
	public static void main(String[] args) throws Exception {
		String appName  = "GENERAL_LEDGER_FIE_FUNDS";
		new Initialize();
		GLRetailFIEFundFeed glRetailFIEFundFeed = new GLRetailFIEFundFeed();
		ConfigDBAccess.getStartupApps(appName, null);
		DALFeedConfiguration dfc = new DALFeedConfiguration(appName);
		glRetailFIEFundFeed.setDFC(dfc);
		glRetailFIEFundFeed.setFeedAppName(appName);
		//glRetailFIEFundFeed.doFeed(glRetailMMFundFeed.getToday(),Constants.MSG_SUBTYPE_EOD, dfc.getSourceCode(),Constants.COST_BASIS_CODE_FA);
		glRetailFIEFundFeed.processMessage(20026);
		System.exit(0);
	}

}