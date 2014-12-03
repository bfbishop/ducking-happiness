package com.fidelity.dal.fundacct.feed.app;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.StopWatch;

import com.fidelity.dal.fundacct.dalapi.beans.FundAccountBalanceData;
import com.fidelity.dal.fundacct.dalapi.beans.FundPortfolioData;
import com.fidelity.dal.fundacct.dalapi.criteria.Criteria;
import com.fidelity.dal.fundacct.dalapi.criteria.FundAccountBalanceCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.FundPortfolioCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.Operator;
import com.fidelity.dal.fundacct.dalapi.criteria.OrderByCriteria;
import com.fidelity.dal.fundacct.feed.Exception.DALFeedException;
import com.fidelity.dal.fundacct.feed.admin.ApplicationContext;
import com.fidelity.dal.fundacct.feed.bean.DALCalendar;
import com.fidelity.dal.fundacct.feed.bean.DALFeedConfiguration;
import com.fidelity.dal.fundacct.feed.bean.FileWriter;
import com.fidelity.dal.fundacct.feed.bean.FundBalance;
import com.fidelity.dal.fundacct.feed.bean.FundPortfolio;
import com.fidelity.dal.fundacct.feed.bean.DALCalendar.SourceCalendar;
import com.fidelity.dal.fundacct.feed.framework.db.ConfigDBAccess;
import com.fidelity.dal.fundacct.feed.framework.jms.JMSMessageProcessorBase;
import com.fidelity.dal.fundacct.feed.framework.util.Constants;
import com.fidelity.dal.fundacct.feed.framework.util.Initialize;
import com.fidelity.dal.fundacct.feed.framework.util.Util;

/**
 * This feed generates Retail Fund List file from FIE and MM application.
 * Then the feed file will be sent to mainframe.
 * @author a511427
 *
 */
public class RetailFundListFeed extends JMSMessageProcessorBase {
	
    private final HashSet<Long> geodeFunds = new HashSet<Long>(Arrays.asList(new Long[]{
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

	
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
	private final DecimalFormat recordCountFormat = new DecimalFormat("000000000000000");
	private final DecimalFormat fundNumFormat = new DecimalFormat("000000000000000");
	private final String FILLER = "|";
	private final String blankSpace = "";
	private final String HEADER_TRAILER_PATTERN = "%1s%s%1s%8s%1s%8s%1s%8s%1s%6s%1s";
	private int recordCount = 0;
	private StopWatch stopWatch = null;
	private final String[] SUB_PORT_IND = {"P","N","V"};
	private final Long[] MERGED_INACTIVE_FUNDS = {24L,33L,129L,317L,348L,506L,612L};
	FileWriter fwGeode = null;
	
	public StopWatch getStopWatch() {
		return stopWatch;
	}

	public void setStopWatch(StopWatch stopWatch) {
		this.stopWatch = stopWatch;
	}

	@Override
	protected synchronized void processMessage(int event) throws Exception {

		DALCalendar dalCalendar = new DALCalendar();
		boolean status = false;
		Util.log("inside RetailFundListFeed.processMessage(): event published by Orchestration:" + event + ":");
		try{
			dfc = getConfiguration();
			Util.log("configuration parameters:" + dfc.toString());
			Util.log("target events from ConfigDataCache for appname=RETAILFUNDLISTFEED" + dfc.getTargetEvents() + ":");
			recordCount = 0;
			FileWriter fw = new FileWriter(dfc);
			dfc.setOutputLocalFileName(dfc.getOutputLocalFileName()+".geode");
			fwGeode = new FileWriter(dfc);
			stopWatch = new StopWatch(dfc.getAppName());
			boolean isFIEHoliday = dalCalendar.isBusinessDay(getToday(),2) && !dalCalendar.isBusinessDay(getToday(),3); 
			if((isMatchingEvent(Constants.EOD_FUND_LOAD_MM_FIE_FUNDS)) || 
			   (isMatchingEvent(Constants.EOD_FUND_LOAD_MM_FUNDS) && isFIEHoliday)){
				
				String[] fundNumberSet=	{};
				Date asOfDate = getToday();
				status = doFeed(fundNumberSet,asOfDate,Constants.MSG_SUBTYPE_EOD,Constants.COST_BASIS_CODE_FA,fw, isFIEHoliday);
				if(status){
					
					sendFile(fw);
				}
			}else{
				Util.log(this, "MM-only event received on non-Good Friday date.");
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
			stopWatch = null;
			Util.log(this,"Completed handling the event  "+event+"[STATUS,"+(status?"SUCCESS":"FAILURE")+"]");
		}
	}

	/**
	 * This method handles feed generation and transport of the file
	 * @return
	 * @throws Exception
	 */ 
	public boolean doFeed(String[] fundNumberSet, Date asOfDate,String exttKndCode,String cstBasisCode, FileWriter fw, boolean isFIEHoliday) throws Exception{
		
		boolean bSuccess = false;
		String hdrTrlCommon = getHdrTrlCommonPart(HEADER_TRAILER_PATTERN,asOfDate);
		
	    try {
	    	
	    	Util.log("configuration parameters:" + dfc);
			String asOfDateStr = dateFormat.format(asOfDate);
			FundPortfolio fundPortfolio = (FundPortfolio)ApplicationContext.getContext().getBean("FundPortfolio");
			HashMap<String,String> sourceCodeMap = getSourceCodes();
			
			String IOSourceCode = getFeedSourceCode(sourceCodeMap,"I","IOMM,IOFIE");
			String FOCASSourceCode = getFeedSourceCode(sourceCodeMap,"F","FOCMM,FOCFIE");

			
			stopWatch.start("Focas Active Fund Numbers");
				Collection <FundPortfolioData> focasFundPortfolioList = getFocasActiveFundNumbers(asOfDateStr,fundNumberSet,FOCASSourceCode,exttKndCode,cstBasisCode);
			stopWatch.stop();
			
			stopWatch.start("IO Active Fund Numbers");
				Collection<FundPortfolioData> ioFundPortfolioList = getIOActiveFundNumbers(asOfDateStr,fundNumberSet,IOSourceCode,exttKndCode,cstBasisCode);
			stopWatch.stop();
			
			String[] prevFundNumbers = null;
			
			Collection<FundAccountBalanceData> fundAccountBalanceDataList = new LinkedList<FundAccountBalanceData>();
			
			if (isFIEHoliday) {
			
				//System.out.println("It's an FIE holiday :>");
				
				DALCalendar dalCal = new DALCalendar();
				Date prevAsOfDate = dalCal.getPriorBusinessDay(asOfDate,SourceCalendar.IOFIE.id);
				
				String prevAsOfDateStr = dateFormat.format(prevAsOfDate);
								
				Collection<FundPortfolioData> prevFOCASFundPortfolioList = getFocasActiveFundNumbers(prevAsOfDateStr,fundNumberSet,"FOCFIE",exttKndCode,cstBasisCode);
				Collection<FundPortfolioData> prevIOFundPortfolioList = getIOActiveFundNumbers(prevAsOfDateStr,fundNumberSet,"IOFIE",exttKndCode,cstBasisCode);
			
				fundPortfolio.setFundPortfolioDataCache(cacheAllFunds(prevFOCASFundPortfolioList,prevIOFundPortfolioList,focasFundPortfolioList,ioFundPortfolioList));

				prevFundNumbers = getUniqueFundNumbers(prevFOCASFundPortfolioList,prevIOFundPortfolioList);
				
				fundAccountBalanceDataList.addAll(getFundAccountBalanceData(prevFundNumbers,Constants.MSG_SUBTYPE_EOD,prevAsOfDateStr,"FOCFIE,IOFIE",Constants.COST_BASIS_CODE_FA));

			} else {

				fundPortfolio.setFundPortfolioDataCache(cacheAllFunds(focasFundPortfolioList,ioFundPortfolioList));

			}
			
			String[] fnumbers = getUniqueFundNumbers(focasFundPortfolioList,ioFundPortfolioList) ;
		
			if(fnumbers == null || fnumbers.length == 0 || (prevFundNumbers != null && prevFundNumbers.length == 0))
			{	
				Util.log(this,"No data available to be written to feed. Empty file is created.");
				createEmptyFile(fw,hdrTrlCommon,0);
				return true;
			}
			// Get the FundBalance Data.

			
			stopWatch.start("FundBalance");
			fundAccountBalanceDataList.addAll(getFundAccountBalanceData(fnumbers,Constants.MSG_SUBTYPE_EOD,asOfDateStr,FOCASSourceCode+","+IOSourceCode,Constants.COST_BASIS_CODE_FA));
			stopWatch.stop();
			
			if(CollectionUtils.isEmpty(fundAccountBalanceDataList)){
				
				Util.log(this,"No data available to be written to feed. Empty file is created.");
				createEmptyFile(fw,hdrTrlCommon,0);
				return true;
			}
			
			if (isFIEHoliday) {
				//Sort again if we had to pull FIE from previous day
				
				Collections.sort((List<FundAccountBalanceData>)fundAccountBalanceDataList,new Comparator<FundAccountBalanceData>(){

					@Override
					public int compare(FundAccountBalanceData o1, FundAccountBalanceData o2) {
						if (o1.getPortfolioFundNumber()*127 + o1.getFundShareClassNumber() > 
							o2.getPortfolioFundNumber()*127 + o2.getFundShareClassNumber()) {
							return 1;
						} else if (o1.getPortfolioFundNumber()*127 + o1.getFundShareClassNumber() < 
							   o2.getPortfolioFundNumber()*127 + o2.getFundShareClassNumber()) {
							return -1;
						} else {
							return 0;
						}
					}
				
				});
			}
			
			stopWatch.start("filewriter");
				recordCount = fw.getNoOfRecordsWritten();
				fw.writeln("IMAHDR" + hdrTrlCommon + recordCountFormat.format(recordCount));
				fwGeode.writeln("IMAHDR" + hdrTrlCommon + recordCountFormat.format(0));
				recordCount = writeRecords(fw,fundAccountBalanceDataList,fundPortfolio);
				fw.writeln("IMATRL" + hdrTrlCommon + recordCountFormat.format(recordCount));
				fwGeode.writeln("IMATRL" + hdrTrlCommon + recordCountFormat.format(fwGeode.getNoOfRecordsWritten()));

			stopWatch.stop();
			bSuccess = true;
			
	    } catch (Exception e) {
	    	bSuccess = false;
			Util.log(this, "Exception thrown for the feed "+dfc.getOutputLocalFileName()+"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
			throw e;
   	    } finally {
   	    	Util.log(this,"Total records written to feed = "+fw.getNoOfRecordsWritten());
	    	if (fw != null) {
	    		fw.close(false);
	    	}
	    	fw = null;
   	    }
		return bSuccess;
	}
	
	
	private ArrayList<FundPortfolioData> cacheAllFunds(Collection<FundPortfolioData> focasFundPortfolioList,Collection<FundPortfolioData> ioFundPortfolioList) {

		ArrayList<FundPortfolioData> allFundsList = new ArrayList<FundPortfolioData>();
		
		if(CollectionUtils.isNotEmpty(focasFundPortfolioList)){
		
			allFundsList.addAll(focasFundPortfolioList);
		}
		
		if(CollectionUtils.isNotEmpty(ioFundPortfolioList)){
		
			allFundsList.addAll(ioFundPortfolioList);
		}
		
		return allFundsList;
	}

	private ArrayList<FundPortfolioData> cacheAllFunds(Collection<FundPortfolioData> focasFundPortfolioListFIE,Collection<FundPortfolioData> ioFundPortfolioListFIE,
			Collection<FundPortfolioData> focasFundPortfolioListMM,Collection<FundPortfolioData> ioFundPortfolioListMM) {

		ArrayList<FundPortfolioData> allFundsList = new ArrayList<FundPortfolioData>();
		
		if(CollectionUtils.isNotEmpty(focasFundPortfolioListMM)){
		
			allFundsList.addAll(focasFundPortfolioListMM);
		}
		
		if(CollectionUtils.isNotEmpty(ioFundPortfolioListMM)){
		
			allFundsList.addAll(ioFundPortfolioListMM);
		}
		
		if(CollectionUtils.isNotEmpty(focasFundPortfolioListFIE)){
			
			allFundsList.addAll(focasFundPortfolioListFIE);
		}
		
		if(CollectionUtils.isNotEmpty(ioFundPortfolioListFIE)){
		
			allFundsList.addAll(ioFundPortfolioListFIE);
		}
		
		
		return allFundsList;
	}
	
	/**
	 * 
	 * @param fundAccountBalanceDataList
	 * @param fundPortfolio
	 * @return
	 */
	private int writeRecords(FileWriter fw,Collection<FundAccountBalanceData> fundAccountBalanceDataList,FundPortfolio fundPortfolio) {
		
		FundPortfolioData portfolioData = null;
		for (FundAccountBalanceData row : fundAccountBalanceDataList) {

			StringBuilder sb = new StringBuilder();
			portfolioData = fundPortfolio.getFundPortfolioByFundPortfolioAccountNumber(row);
			
				
			if(portfolioData != null){
				
				Long fundNumber = portfolioData.getFundNumber();
				Long portFundNumber = portfolioData.getPortfolioFundNumber();
				
				if(!ObjectUtils.equals(fundNumber,portFundNumber)){
					
					continue; //FACDAL-2650. Exclude all sub funds
				}
				
				if(ArrayUtils.contains(MERGED_INACTIVE_FUNDS, fundNumber)){
					
					continue; //Merged and Inactive funds are not required. FACDAL-1199
				}
				
				String sbPortInd = portfolioData.getSubportfolioIndicator();
				if(StringUtils.isNotBlank(sbPortInd) && ArrayUtils.contains(SUB_PORT_IND, sbPortInd.trim())){
				
					//1. Originating System Identifier
					if(portfolioData.getSourceCode()!=null) {
			
						String srcCode = portfolioData.getSourceCode();
						if(Constants.FUND_TYPE_FOCAS_MM.equalsIgnoreCase(srcCode)){
							sb.append("F-MM");
						}else if(Constants.FUND_TYPE_FOCAS_FIE.equalsIgnoreCase(srcCode)){
							sb.append("F-FIE");
						}else if(Constants.FUND_TYPE_INVESTONE_MM.equalsIgnoreCase(srcCode)){
							sb.append("I-MM");
						}else if(Constants.FUND_TYPE_INVESTONE_FIE.equalsIgnoreCase(srcCode)){
							sb.append("I-FIE");
						}
					}
					//Pipe Deliminator 
					sb.append(String.format("%1s",FILLER));
			
					//2.Account Number
					sb.append(portfolioData.getFundNumber() != null ?  fundNumFormat.format(portfolioData.getFundNumber()) : blankSpace );
					//Pipe Deliminator
					sb.append(String.format("%1s",FILLER));				
			
					//3.Account Long Name
					sb.append(portfolioData.getLongName() != null ?  portfolioData.getLongName() : blankSpace );
					//Pipe Deliminator
					sb.append(String.format("%1s",FILLER));
					
					//4.Account Custodian Code
					sb.append(portfolioData.getCustodianCode() != null ?  portfolioData.getCustodianCode().trim() : blankSpace );
					//Pipe Deliminator
					sb.append(String.format("%1s",FILLER));			
			
					//5.Account Custodian Account Number
					String custodianAccountNumber = portfolioData.getCustodianAccountNumber();
					if(StringUtils.startsWith(row.getSourceCode(),"IO")){
						
						custodianAccountNumber = portfolioData.getCSDNACCNFEPA();
					}
					sb.append(custodianAccountNumber != null ?  custodianAccountNumber.trim() : blankSpace );
					//Pipe Deliminator
					sb.append(String.format("%1s",FILLER));
			
					//6.Account Fiscal Year End Date
					sb.append(portfolioData.getEndFiscalYearDate() != null ?  dateFormat2.format(portfolioData.getEndFiscalYearDate()) : blankSpace );
					//Pipe Deliminator
					sb.append(String.format("%1s",FILLER));
			
					//7.FOCAS Fund All Inclusive Indicator
					sb.append(portfolioData.getAllInclusiveIndicator() != null ?  portfolioData.getAllInclusiveIndicator() : blankSpace);
					//Pipe Deliminator
					sb.append(String.format("%1s",FILLER));
			
					//8.Account Multi-Class Indicator
					try {       
						int d = Integer.parseInt(portfolioData.getMultiClassIndicator());
						if(d>0){
							sb.append("Y");
						}else{
							sb.append("N");
						}
					}catch(NumberFormatException nfe)     {       
						if(portfolioData.getMultiClassIndicator() != null){
							if("Y".equalsIgnoreCase(portfolioData.getMultiClassIndicator())){
								sb.append(portfolioData.getMultiClassIndicator());
							}else{
								sb.append("N" );
							}
						}else{
							sb.append("N");
						}
					}   
					//Pipe Deliminator
					sb.append(String.format("%1s",FILLER));
			
					//9.Share Class Identifier
					sb.append((row.getFundShareClassNumber() != null) ?  String.format("%02d",(row.getFundShareClassNumber().intValue())) : blankSpace) ;
					//Pipe Deliminator
					sb.append(String.format("%1s",FILLER));
			
					//10.Fund of Funds Indicator
					sb.append(portfolioData.getFundOfFundIndicator() != null ?  portfolioData.getFundOfFundIndicator() : "N");
					//Pipe Deliminator
					sb.append(String.format("%1s",FILLER));
							
					//11.CIP Indicator
					sb.append(blankSpace);//FACDAL-2465
					
					fw.writeRecord(sb.toString());
					if (geodeFunds.contains(row.getPortfolioFundNumber())) {
						fwGeode.writeRecord(sb.toString());
					}
				}
			}
		}
		return fw.getNoOfRecordsWritten();
	}

	/**
	 * Below method gets the Fund Account Balance data
	 * @param fnumbers
	 * @param exttKndCode
	 * @param asOfDateStr
	 * @param srcCode
	 * @param cstBasisCode
	 * @return
	 * @throws Exception
	 */
	private Collection<FundAccountBalanceData> getFundAccountBalanceData(String[] fnumbers, 
																		 String exttKndCode, 
																		 String asOfDateStr,
																		 String srcCode, 
																		 String cstBasisCode) throws Exception {
		
		Util.log(this,"Getting Fund Account Balance data");
		FundBalance fundbalance = (FundBalance)ApplicationContext.getContext().getBean("FundBalance");
		Criteria fundBalanceCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		fundBalanceCriteria.addCriterion(FundAccountBalanceCriteriaBuilder.addExtractKindCodeFilter(exttKndCode, Operator.OPERATOR_EQ));
		fundBalanceCriteria.addCriterion(FundAccountBalanceCriteriaBuilder.addAsOfDateFilter(asOfDateStr, Operator.OPERATOR_EQ));
		fundBalanceCriteria.addCriterion(FundAccountBalanceCriteriaBuilder.addSourceCodeFilter(srcCode, Operator.OPERATOR_IN));
		fundBalanceCriteria.addCriterion(FundAccountBalanceCriteriaBuilder.addCostBasisCodeFilter(cstBasisCode, Operator.OPERATOR_EQ));
		
		OrderByCriteria fborderByCriteria = (OrderByCriteria)ApplicationContext.getContext().getBean("OrderByCriteria");
		fborderByCriteria.addOrderByOn(FundAccountBalanceData.FIELD_fundNumber).asc();
		fborderByCriteria.addOrderByOn(FundAccountBalanceData.FIELD_fundShareClassNumber).asc();
		
		Collection<FundAccountBalanceData> fundAccountBalanceDataList= null;
		if(fnumbers == null){
			
			fundAccountBalanceDataList = fundbalance.getFundBalanceSet(new String[0],fundBalanceCriteria,fborderByCriteria);
		}else{
			
			fundAccountBalanceDataList = fundbalance.getFundBalanceSet(fnumbers,fundBalanceCriteria,fborderByCriteria); 
		}
		
		Util.log(this,"Total Fund Account Balance records = "+(fundAccountBalanceDataList != null ? fundAccountBalanceDataList.size():0));
		return fundAccountBalanceDataList;
	}

	private String getFeedSourceCode(HashMap<String, String> sourceCodeMap,String key, String defaultValue) {

		String feedSrcCode = null;

		if(sourceCodeMap.get(key)!=null){
			
			feedSrcCode = sourceCodeMap.get(key);
		}else{
		
			if(dfc.getSourceCodes().length>1){
				feedSrcCode = defaultValue;
			}
		}
		
		return feedSrcCode;
	}

	private void createEmptyFile(FileWriter fw, String hdrTrlCommon, int recordCount) {

		fw.writeln("IMAHDR" + hdrTrlCommon + recordCountFormat.format(recordCount));
		fw.writeln("IMATRL" + hdrTrlCommon + recordCountFormat.format(recordCount));
	}

	private String getHdrTrlCommonPart(String HEADER_TRAILER_PATTERN,Date asOfDate) {
		
		Date today = new Date();
		String hostName = Util.getHostName();
		String fileName = dfc.getOutputLocalFileName();

		if(StringUtils.isNotBlank(hostName) && hostName.length() > 6){
			hostName = hostName.substring(0,6);
		}
		
		if(StringUtils.isNotBlank(fileName) && fileName.length() > 8){
			fileName = fileName.substring(0,8);
		}
		return String.format("%1s%s%1s%8s%1s%8s%1s%8s%1s%6s%1s", FILLER, hostName, FILLER, fileName, FILLER,
				dateFormat2.format(asOfDate),FILLER,dateFormat2.format(today),FILLER, timeFormat.format(today), FILLER);
	}

	private HashMap<String,String> getSourceCodes()throws Exception {
		String sourceCodeInPropertyFile = dfc.getSourceCode();
		HashMap<String,String> sourceCodeMap = new HashMap<String,String>();
		//For Good Friday, the Money Market files- FOCMM,IOMM files will be processed.
        //Code update on 04/04/2013 : Remove the Good Friday check as per John Sullivan's and Yeong's email today
		//if(isGoodFriday()) {
			//sourceCodeMap.put("I", Constants.FUND_TYPE_INVESTONE_MM);
			//sourceCodeMap.put("F", Constants.FUND_TYPE_FOCAS_MM);
		//} else
		if(sourceCodeInPropertyFile!=null){
			if(sourceCodeInPropertyFile.contains(Constants.FUND_TYPE_FOCAS_MM)||
					sourceCodeInPropertyFile.contains(Constants.FUND_TYPE_INVESTONE_MM)){
				if(dfc.getSourceCodes().length>1){
					if(dfc.getSourceCode(0).contains(Constants.FUND_TYPE_FOCAS_MM)||dfc.getSourceCode(0).contains(Constants.FUND_TYPE_FOCAS_FIE)){
						sourceCodeMap.put("F", dfc.getSourceCode(1));
						sourceCodeMap.put("I", dfc.getSourceCode(2));
					}
					if(dfc.getSourceCode(0).contains(Constants.FUND_TYPE_INVESTONE_MM)||dfc.getSourceCode(0).contains(Constants.FUND_TYPE_INVESTONE_FIE)){
						sourceCodeMap.put("I", dfc.getSourceCode(1));
						sourceCodeMap.put("F", dfc.getSourceCode(2));
					}
				}
				else {
					if(dfc.getSourceCode().contains(Constants.FUND_TYPE_FOCAS_MM)||dfc.getSourceCode().contains(Constants.FUND_TYPE_FOCAS_FIE)){
						sourceCodeMap.put("F", dfc.getSourceCode());
					}
					if(dfc.getSourceCode(0).contains(Constants.FUND_TYPE_INVESTONE_MM)||dfc.getSourceCode(0).contains(Constants.FUND_TYPE_INVESTONE_FIE)){
						sourceCodeMap.put("I", dfc.getSourceCode());
					}
				}
			}
		}
		return sourceCodeMap;
	}

	private Collection<FundPortfolioData> getIOActiveFundNumbers(String asOfDateStr, String[] fundNumberSet , 
																 String sourceCode, String exttKndCode, 
																 String cstBasisCode) throws Exception {
		
		Util.log("starting fundPortfolio.getFundPortfolioSet");
		
		if(StringUtils.isBlank(asOfDateStr) || StringUtils.isBlank(sourceCode)){
			
			return null;
		}
		// Get the fundPortfolio Data.
		FundPortfolio fundPortfolio = (FundPortfolio)ApplicationContext.getContext().getBean("FundPortfolio");
		
		//Criteria fundPortfolioCriteria = new Criteria();
		Criteria fundPortfolioCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addAsOfDateFilter(asOfDateStr, Operator.OPERATOR_EQ));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addExtractKindCodeFilter(exttKndCode, Operator.OPERATOR_EQ));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addSourceCodeFilter(sourceCode,Operator.OPERATOR_IN));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addCostBasisCodeFilter(cstBasisCode, Operator.OPERATOR_EQ));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addFundStatusIndicatorFilter("I", Operator.OPERATOR_NE));
		
		OrderByCriteria orderByCriteria = (OrderByCriteria)ApplicationContext.getContext().getBean("OrderByCriteria");
		orderByCriteria.addOrderByOn(FundPortfolioData.FIELD_fundNumber).asc();
		
		Collection<FundPortfolioData> fundPortfolioList = fundPortfolio.getFundPortfolioSet(fundNumberSet, fundPortfolioCriteria,orderByCriteria);
		
		
		Util.log(this,"Total IO FundPortfolio records = "+(fundPortfolioList != null ? fundPortfolioList.size() : 0));
		
		return fundPortfolioList;
	}
	
	private Collection<FundPortfolioData> getFocasActiveFundNumbers(String asOfDateStr, String[] fundNumberSet , 
																	String sourceCode, String exttKndCode, 
																	String cstBasisCode) throws Exception {

		Util.log("starting fundPortfolio.getFundPortfolioSet");
		if(StringUtils.isBlank(asOfDateStr) || StringUtils.isBlank(sourceCode)){
			
			return null;
		}
		
		FundPortfolio fundPortfolio = (FundPortfolio)ApplicationContext.getContext().getBean("FundPortfolio");
		
		//Criteria fundPortfolioCriteria = new Criteria();
		Criteria fundPortfolioCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addAsOfDateFilter(asOfDateStr, Operator.OPERATOR_EQ));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addExtractKindCodeFilter(exttKndCode, Operator.OPERATOR_EQ));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addSourceCodeFilter(sourceCode,Operator.OPERATOR_IN));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addCostBasisCodeFilter(cstBasisCode, Operator.OPERATOR_EQ));
		
		OrderByCriteria orderByCriteria = (OrderByCriteria)ApplicationContext.getContext().getBean("OrderByCriteria");
		orderByCriteria.addOrderByOn(FundPortfolioData.FIELD_fundNumber).asc();
		
		Collection<FundPortfolioData> fundPortfolioList = fundPortfolio.getFundPortfolioSet(fundNumberSet, fundPortfolioCriteria,orderByCriteria);
		
		Util.log(this,"Total Focas FundPortfolio records = "+(fundPortfolioList != null ? fundPortfolioList.size() : 0));
		
		return fundPortfolioList;
	}
	
	private String[] getUniqueFundNumbers(Collection<FundPortfolioData> focasFunds ,Collection<FundPortfolioData> ioFunds) {
		
		String[] fundNumbers = null;
		//if (focasFunds == null || focasFunds.isEmpty())
		//	return null;

		Set<String> fundNumberSet = new LinkedHashSet<String>();
		
		if (focasFunds != null) {
			for (FundPortfolioData fundData : focasFunds) {
				fundNumberSet.add(String.valueOf(fundData.getFundNumber().longValue()));
			}
		}
		
		if(ioFunds != null) {
			for (FundPortfolioData fundData : ioFunds) {
				fundNumberSet.add(String.valueOf(fundData.getFundNumber().longValue()));
			}
		}
		
		if (fundNumberSet != null && !fundNumberSet.isEmpty()) {
			fundNumbers = fundNumberSet.toArray(new String[fundNumberSet.size()]);
		}
		
		return fundNumbers;
	}
	
	public static void main(String[] args) throws Exception {

		String appName = "RETAILFUNDLISTFEED";
		new Initialize();
		RetailFundListFeed retailfundlistFeed = new RetailFundListFeed();
		ConfigDBAccess.getStartupApps(appName, null);
		DALFeedConfiguration dfc = new DALFeedConfiguration(appName);
		retailfundlistFeed.setDFC(dfc);
		System.out.println(dfc.getAppId());
		retailfundlistFeed.setFeedAppName(appName);
		StopWatch stopWatch = new StopWatch(dfc.getAppName());
		retailfundlistFeed.setStopWatch(stopWatch);
		retailfundlistFeed.event = 25076;
		try{
			
			//retailfundlistFeed.doFeed(new String[]{}, retailfundlistFeed.getToday(),Constants.MSG_SUBTYPE_EOD,Constants.COST_BASIS_CODE_FA,new FileWriter(dfc),false);

			retailfundlistFeed.processMessage(25076);
		}catch(Exception e){
			
			e.printStackTrace();
		}
		System.exit(0);
	}	
}
