package com.fidelity.dal.fundacct.feed.app;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.StopWatch;

import com.fidelity.dal.fundacct.dalapi.beans.FundPortfolioData;
import com.fidelity.dal.fundacct.dalapi.beans.HoldingTransactionData;
import com.fidelity.dal.fundacct.dalapi.beans.PositionData;
import com.fidelity.dal.fundacct.dalapi.beans.SecurityData;
import com.fidelity.dal.fundacct.dalapi.criteria.Criteria;
import com.fidelity.dal.fundacct.dalapi.criteria.FundPortfolioCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.HoldingTransactionCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.Operator;
import com.fidelity.dal.fundacct.dalapi.criteria.OrderByCriteria;
import com.fidelity.dal.fundacct.dalapi.criteria.PositionCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.SecurityCriteriaBuilder;
import com.fidelity.dal.fundacct.feed.Exception.DALFeedException;
import com.fidelity.dal.fundacct.feed.admin.ApplicationContext;
import com.fidelity.dal.fundacct.feed.bean.DALCalendar;
import com.fidelity.dal.fundacct.feed.bean.FileWriter;
import com.fidelity.dal.fundacct.feed.bean.FundPortfolio;
import com.fidelity.dal.fundacct.feed.bean.HoldingsTransaction;
import com.fidelity.dal.fundacct.feed.bean.Position;
import com.fidelity.dal.fundacct.feed.bean.Security;
import com.fidelity.dal.fundacct.feed.framework.db.ConfigDBAccess;
import com.fidelity.dal.fundacct.feed.framework.db.mapper.FocasTransVal;
import com.fidelity.dal.fundacct.feed.framework.feed.DBUtil;
import com.fidelity.dal.fundacct.feed.framework.jms.JMSMessageProcessorBase;
import com.fidelity.dal.fundacct.feed.framework.util.Constants;
import com.fidelity.dal.fundacct.feed.framework.util.Initialize;
import com.fidelity.dal.fundacct.feed.framework.util.Util;
import com.fidelity.dal.fundacct.feed.bean.DALSecRefService;

/**
 * This feed generates Transaction History file from DAL.
 * Then the feed file will be sent to iFeeds application.
 * @author a515515
 *
 */
public class TransactionsFeed extends JMSMessageProcessorBase {

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
	
	public int bodTxnMM;
	public int eodTxnMM;	
	public int bodTxnFie;
	public int eodTxnFie;
	
	final String FILLER = " ";
	final String UNKNOWN = "?";
	final int NUM_FILLER = 0; 
	final String REG_EX_PERIOD = "[.]";
	final String BLANK_STRING = "";
	final String DELIMITER = "";
	final String SUMMARY = "2";
	final String DETAIL = "1";
	private FileWriter fw = null;
	private FileWriter fwGeode = null;
	//Define formatters
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat asOfDateFormat = new SimpleDateFormat("yyyy-MM-dd"); //2012-02-23
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");	
	private DecimalFormat recordCountFormat = new DecimalFormat("000000000000000"); //15 digits
	private Map<String, Double> positionDataMap,positionMapForUnitOfCalc = null;
	private Map<String, String> positionDataMapForPriceCode = null;
	private Map<String, FundPortfolioData> fundMap;
	private Map<String, String> taxlotMap;
	private String extractKindCode;
	private Security security;
	private Security securityEOD;
	private StopWatch stopWatch;
	private Map<String,String> fmrIdMap = null;
		
	/**
	 * Constructor function
	 */
	public TransactionsFeed() {
		super();
	}	

	@Override
	protected synchronized void processMessage(int event) throws Exception {
		
		Util.log(this, "Entering TransactionFeed.processMessage(event): Event published by Orchestration: " + event);
		dfc = getConfiguration();
		Util.log(this, "Got Configuration for TransactionsFeed : ");
		if(dfc == null ) {
			Util.log(this, "Could not get DALFeedConfiguration");
			throw new DALFeedException(this, getMessageToken(), new Exception("DalFeedConfiguration error "), dfc.getAppName()); 
		}
		Util.log(this, "Configuration Parameters: " + dfc.toString());
		Util.log(this, "Target events from ConfigDataCache:\n " + dfc.getTargetEvents());
		
		bodTxnMM =   Constants.BOD_FUND_SEC_TXN_IO_MM_FUNDS + dfc.getAppId() * 1000;
		eodTxnMM =   Constants.EOD_FUND_SEC_TXN_IO_MM_FUNDS + dfc.getAppId() * 1000;	
		bodTxnFie =  Constants.BOD_FUND_SEC_TXN_IO_FIE_FUNDS + dfc.getAppId() * 1000;
		eodTxnFie =  Constants.EOD_FUND_SEC_TXN_IO_FIE_FUNDS + dfc.getAppId() * 1000;
		
		if(event == bodTxnMM || event == eodTxnMM || 
				event == bodTxnFie ||event == eodTxnFie) {
			
			doFeed(event);
			
		} else {
			
			throw new DALFeedException(this, getMessageToken(), new Exception("Invalid Event received for TransactionsFeed"),dfc.getAppName());
		}
		
		Util.log(this, "Exiting TransactionFeed.processMessage(event). parameter event : " + event);
	}
	
	/**
	 * 
	 * @param event
	 * @return
	 * @throws Exception
	 */
	public void doFeed(int event) throws Exception {
		
		Util.log(this,"Entering TransactionFeed.doFeed(" + event +")");
		stopWatch = new StopWatch(dfc.getAppName());
		DALCalendar dalCalendar = (DALCalendar)ApplicationContext.getContext().getBean("DALCalendar");
		String fundType = Constants.FUND_TYPE_FIE;
		int sourceCalendarId = DALCalendar.SourceCalendar.IOFIE.id;
		if(event == bodTxnMM || event == eodTxnMM) {
			sourceCalendarId = DALCalendar.SourceCalendar.IOMM.id;
			fundType = Constants.FUND_TYPE_MM;
		}
		boolean isEOD = true;
		if(event == bodTxnFie || event == bodTxnMM){ 
			isEOD = false;
		}
		extractKindCode = isEOD ? Constants.MSG_SUBTYPE_EOD : Constants.MSG_SUBTYPE_BOD;
		doFeed(fundType, isEOD, dalCalendar, sourceCalendarId);
		Util.log(this,"Exiting TransactionFeed.doFeed(" + event +")");
		
	}

	/**
	 * This method handles feed generation and transport of the file
	 * @return
	 * @throws Exception
	 */
	public boolean doFeed(String fundType, boolean isEOD, DALCalendar dalCalendar, int sourceSystemId) throws Exception {

		
		boolean success = false;
		Date now = java.util.Calendar.getInstance().getTime();
		String sourceCode = dfc.getSourceCode();	
		Util.log(this,"Entering TransactionFeed.doFeed(" + fundType +", " + isEOD+")");
				
		try {
			Date asOfDate = getToday();
			String asOfDateHeader = dateFormat.format(getToday());
			String asOfDateStr = asOfDateFormat.format(asOfDate.getTime()); 
			if(!isEOD) { 
				asOfDate = dalCalendar.getNextCalendarDay(asOfDate);
				asOfDateStr = asOfDateFormat.format(asOfDate); 
				if(!dalCalendar.isBusinessDay(asOfDate, sourceSystemId)){
					
					asOfDate = dalCalendar.getNextBusinessDay(asOfDate, sourceSystemId);
				}				
			}			 				
			stopWatch.start("createFile");
			Util.log(this, "output file name : " + dfc.getOutputLocalFileName());
			fw = getFileWriter();
			dfc.setOutputLocalFileName(dfc.getOutputLocalFileName()+".geode");
			fwGeode = new FileWriter(dfc);
		    String sendingMachine = Util.getHostName();
		    
		    if(StringUtils.isNotBlank(sendingMachine) && sendingMachine.length() > 8){
		    	sendingMachine = sendingMachine.substring(0,8);
			}
		    
		    String fileIdentifier = populateFileIdentifier();				

			//Adding header record
			fw.writeln(getHeader(now, asOfDateHeader, sendingMachine,fileIdentifier));	
			fwGeode.writeln(getHeader(now, asOfDateHeader, sendingMachine,fileIdentifier));
			
			//Adding detail records				
			processFeed(isEOD, asOfDateStr, sourceCode, fundType, dalCalendar);
		
			//Adding trailer record
			String trailerPrefix = getTrailer(asOfDateHeader, sendingMachine,fileIdentifier);

			fw.writeln(trailerPrefix + recordCountFormat.format(fw.getNoOfRecordsWritten()));
			fwGeode.writeln(trailerPrefix + recordCountFormat.format(fwGeode.getNoOfRecordsWritten()));
			
			fw.close(false);
			fwGeode.close(false);
			sendFile(fw);
			success = true;
			stopWatch.stop();
			
			logRunMetrics(this, Constants.LOG_TYPE_STATS, stopWatch.getTotalTimeSeconds(), fw.getNoOfRecordsWritten(), stopWatch.prettyPrint());
			logRunMetrics(this, Constants.LOG_TYPE_RESULT, stopWatch.getTotalTimeSeconds(), fw.getNoOfRecordsWritten(), success ? Constants.LOG_RESULT_SUCCESS:Constants.LOG_RESULT_FAIL);
			

		} catch (Exception e) {
			e.printStackTrace();
	    	Util.log(this, "Exception thrown for the feed " + dfc.getOutputLocalFileName() 
	    			+ "\n" + e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
	    	success = false;
	    	throw new DALFeedException(this,getMessageToken(),e, dfc.getAppName());
		} finally {			
			if (fw != null){
				fw = null;
			}					
		}
		Util.log(this,"Exiting TransactionFeed.doFeed(" + fundType +", " + isEOD+")");
		return success;
	}

	private void cleanUp() {
		taxlotMap = null;
		fundMap = null;
		security = null;
		positionDataMap = null;
		positionDataMapForPriceCode = null;
		positionDataMapForPriceCode = null;
		positionMapForUnitOfCalc = null;
		fmrIdMap = null;
		
	}

	private String getTrailer(String asOfDateHeader, String sendingMachine,
			String fileIdentifier) {
		Date now;
		now = java.util.Calendar.getInstance().getTime();
		String trailerPrefix = String.format("%6s%1s%8s%1s%8s%1s%8s%1s%8s%1s%6s%1s", 
				"IMATRL", BLANK_STRING, 
				sendingMachine, BLANK_STRING, 
				fileIdentifier, BLANK_STRING,
				asOfDateHeader, BLANK_STRING, 
				dateFormat.format(now),	BLANK_STRING, 
				timeFormat.format(now), BLANK_STRING);
		return trailerPrefix;
	}
	
	private void processFeed(boolean isEOD, String asOfDateStr,
			String sourceCode, String fundType,DALCalendar dalCalendar)
			throws Exception {
		
		fundMap = new HashMap<String, FundPortfolioData>();
		positionDataMap = new HashMap<String, Double>();
		positionDataMapForPriceCode = new HashMap<String, String>();
		positionMapForUnitOfCalc = new HashMap<String, Double>();
		DALSecRefService secRefService = new DALSecRefService();
		fmrIdMap = secRefService.getFmrIdFrmSecRef();
		getTaxlot(asOfDateStr, sourceCode);
						
		List<FundPortfolioData> funds = getRetailFunds(asOfDateStr, fundType, sourceCode);
		List<String> fundNumbers = getUniqueFundNumbers(funds);
				
		while (fundNumbers!=null && fundNumbers.size()>0) {
			@SuppressWarnings("unchecked")
			List<String> subFundNumbersList = (List<String>)getNextBatch(fundNumbers, dfc.getBatchSize());
			String[] subFundNumbers = null;
			if (subFundNumbersList!=null && subFundNumbersList.size()>0) {				
				subFundNumbers = subFundNumbersList.toArray(new String[0]);						
				getPositionData(subFundNumbers,asOfDateStr, fundType, sourceCode);
				writeTransactions(subFundNumbers , sourceCode, asOfDateStr);
			}
		}				
		
		cleanUp();	
	}
	
	private List<String> getUniqueFundNumbers(List<FundPortfolioData> funds) {
		
		if (funds == null || funds.size() == 0)
			return null;
			
		Set<String> fundNumbers = new LinkedHashSet<String>();
		
		for (FundPortfolioData fundData : funds){
			fundNumbers.add(String.valueOf(fundData.getFundNumber().longValue()));
		}
		
		return new ArrayList<String>(fundNumbers);
	}

	private String getHeader(Date now, String asOfDateHeader,
			String sendingMachine, String fileIdentifier) {
		String header = String.format("%6s%1s%8s%1s%8s%1s%8s%1s%8s%1s%6s%1s%015d", 
				"IMAHDR", BLANK_STRING, 
				sendingMachine, BLANK_STRING, 
				fileIdentifier, BLANK_STRING,
				asOfDateHeader, BLANK_STRING, 
				dateFormat.format(now),	BLANK_STRING, 
				timeFormat.format(now), BLANK_STRING, 0);
		return header;
	}

	private String populateFileIdentifier() {
		String fileIdentifier = null;
		String fileName = dfc.getOutputLocalFileName();
		if("fpcmsdal.trans.mm.eod".equals(fileName)){		    	
			fileIdentifier = "txneodmm";
		}else if("fpcmsdal.trans.fie.eod".equals(fileName)){		    	
			fileIdentifier = "txneodfe";
		}else if("fpcmsdal.trans.mm.bod".equals(fileName)){		    	
			fileIdentifier = "txnbodmm";
		}else{		    	
			fileIdentifier = "txnbodfe";
		}
		return fileIdentifier;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	private void writeTransactions(String[] fundNumberSet, String sourceCode, String asOfDateStr) throws Exception {
		
		
		ArrayList<HoldingTransactionData> transactions = getHoldingsTransactions(fundNumberSet, sourceCode, asOfDateStr);//Get Transactions		
		
		if(transactions == null || transactions.isEmpty()){
			return; //No need to proceed further
		}		
		transactions = removeDuplicates(transactions);		
			
		HashMap<String, String> transactionDescMap = new HashMap<String, String>();
		populateTransDescMap(transactionDescMap);		
		writeAllTransactions(transactions, transactionDescMap, fundNumberSet, sourceCode, asOfDateStr);
		Util.log(this, "Exiting writeTransactions ....");
	}

	private void writeAllTransactions(ArrayList<HoldingTransactionData> transactions,
			HashMap<String, String> transactionDescMap, String[] fundNumberSet, String sourceCode, String asOfDateStr) throws Exception {
		
		String previousDetail = null;
		HoldingTransactionData previousRow = null;
		String commonFields = null;
		String fidTranTradedCurrencyCode = null;
		
		security = (Security) ApplicationContext.getContext().getBean("Security");
		SecurityData securityData = null;
		SecurityData previousSecurityData = null;
		
		
		if(!transactions.isEmpty()) {
			getSecurity(asOfDateStr, sourceCode, extractKindCode,transactions, security);
		}	
		//Start FIX for jira FACDAL-2229. For BOD transaction file, also get EOD SecurityData
		String fidEODTranTradedCurrencyCode = null;
		SecurityData securityDataEOD = null;
		if(extractKindCode.equals("BOD")) {
			securityEOD = (Security) ApplicationContext.getContext().getBean("Security");
			if(!transactions.isEmpty()) {
				
				getSecurity(asOfDateFormat.format(getToday().getTime()), sourceCode, "EOD", transactions, securityEOD);
			}
		}
        //End Fix for jira FACDAL-2229.
		
		for (HoldingTransactionData row : transactions) {
			
			StringBuilder sb = new StringBuilder();
			securityData = security.getSecurityByCusipNumber(row);
			
			//FIX for jira FACDAL-2229. For BOD transaction file, also get EOD SecurityData
			if(extractKindCode.equals("BOD")) {
				securityDataEOD = securityEOD.getSecurityByCusipNumber(row);
			}
			 //End Fix for jira FACDAL-2229.
			
			String key = getFundMapKey(row);
			FundPortfolioData fundPortfolioData = fundMap.get(key);
			//FID-TRAN-KEY is composed of FID-TRAN-TRAD-ORDER, FID-TRAN-INV1-ACCT-NUM, FID-TRAN-INV1-GRP-ACCT-NUM,FID-TRAN-FUND-NUM,FID-TRAN-SUBPORT-ID,FID-TRAN-CUSIP
			populateWithFormattedValue("%-8s", row.getTransactionMemoNumber() != null ? String.valueOf(row.getTransactionMemoNumber().longValue()) : FILLER , sb); // 1 -8
			//FID-TRAN-TRAD-ORDER
			populateWithFormattedValue("%015d", row.getPortfolioFundNumber() != null ? row.getPortfolioFundNumber().longValue() : NUM_FILLER , sb); //9-23 
			//FID-TRAN-INV1-GRP-ACCT-NUM  
     		populateWithFormattedValue("%015d", fundPortfolioData.getFundNumber() != null ?   fundPortfolioData.getFundNumber() : NUM_FILLER, sb); //24 - 38
			// FID-TRAN-FUND-NUM
     		populateWithFormattedValue("%06d",  fundPortfolioData.getFMRAccountNumber() != null ? Long.valueOf(fundPortfolioData.getFMRAccountNumber()) : NUM_FILLER , sb); //39 -44
		    //FID-TRAN-SUBPORT-ID .Based on comments FACDAL-1181, FID-TRAN-SUBPORT-ID should be spaces.
			populateWithFormattedValue("%-2s", FILLER , sb); // 45-46
			// FMR Cusip No from Sec Ref. FACDAL - 1663
			String cusip = getSecRefCUSIP(securityData, fmrIdMap, row, sb);
			populateWithFormattedValue("%-9s", cusip != null ? cusip : FILLER, sb);
			// write summary record
			if(!sb.toString().equals(commonFields)) {					
				if(commonFields != null) {
					if(extractKindCode.equals("BOD")) {
						if(fidTranTradedCurrencyCode == null) {
							fidTranTradedCurrencyCode = fidEODTranTradedCurrencyCode; 
						}
					}
					writeSummary(commonFields, previousRow, fidTranTradedCurrencyCode, previousSecurityData);
				}					    
					previousDetail = null;
					fidTranTradedCurrencyCode = null;
			} 			 
			commonFields = sb.toString();			
			StringBuilder detail = new StringBuilder(commonFields);
			//FID-TRAN-REC-TYPE           
			populateWithFormattedValue("%1s", DETAIL, detail); // 56			
			populateWithFormattedValue("%-10s", FILLER , detail); //58-67
			//SDEX-TRAN-ACCT              
			populateWithFormattedValue("%015d", row.getPortfolioFundNumber() != null ?  row.getPortfolioFundNumber().longValue() :  NUM_FILLER, detail); //68 - 82
			//SDEX-TRAN-SCTY-CUSIP        
			populateWithFormattedValue("%-9s", row.getInvestOneSecurityCusipNumber() != null ?  row.getInvestOneSecurityCusipNumber().trim() : FILLER , detail); // 83 - 91
			//SDEX-TRAN-SCTY-DATE         
			populateWithFormattedValue("%08d", row.getInvestOneSecurityDate() != null ?  row.getInvestOneSecurityDate().intValue() : NUM_FILLER,  detail); // 92 -- 99
			//SDEX-TRAN-SCTY-QUAL         
			populateWithFormattedValue("%05d", row.getInvestOneSecurityQualifier() != null ?  row.getInvestOneSecurityQualifier().intValue() : NUM_FILLER,  detail); //100 -104
			//SDEX-TRAN-CODE              
			populateWithFormattedValue("%-6s", row.getTransactionCode() != null ?  row.getTransactionCode().trim() : FILLER ,  detail); //105 -110			
			double shareParQuantityDetail = 0.0;
			if(row.getShareParQuantity() != null && row.getShareParQuantity().trim().length() > 0) {
				shareParQuantityDetail = Double.parseDouble(row.getShareParQuantity().trim());				
			}	
			//SDEX-TRAN-SHARES            
			populateWithFormattedValue("%+020.4f", Math.abs(shareParQuantityDetail),  detail); //111 -129
			//SDEX-TRAN-COST              
			populateWithFormattedValue("%+019.2f", row.getCostAmount() != null ?  Math.abs(row.getCostAmount()) : 0.0f,  detail); //130 -147			
			double principalAmountDetail = 0.0;
			if(row.getPrincipalAmount() != null )
				 principalAmountDetail =  row.getPrincipalAmount().doubleValue();
			//SDEX-TRAN-PRINCIPAL
			populateWithFormattedValue("%+019.2f", Math.abs(principalAmountDetail),  detail); //148 -165
			//SDEX-TRAN-BROKER
			populateWithFormattedValue("%-7s", row.getTransactionExecutedBrokerCode() != null ?  row.getTransactionExecutedBrokerCode().trim() : FILLER ,  detail); //166 -172 
			//SDEX-TRAN-BROK-CLR 
			String brokerClearing = row.getBrokerClearing();
			populateWithFormattedValue("%-7s", brokerClearing == null ? FILLER : brokerClearing.trim(), detail); //173 -179
			//SDEX-TRAN-TRADE-DATE        
			populateWithFormattedValue("%-8s", row.getTradeDate() != null ? dateFormat.format(row.getTradeDate()) : "00000000",  detail); //180 - 187
			//SDEX-TRAN-CSETL-DATE        
			populateWithFormattedValue("%-8s", row.getContractualSettlementDate() != null ? dateFormat.format(row.getContractualSettlementDate()) : "00000000" ,  detail); // 188 - 195
			//SDEX-TRAN-ASETL-DATE        
			populateWithFormattedValue("%-8s", row.getActualSettlementDate() != null ? dateFormat.format(row.getActualSettlementDate()) : "00000000" ,  detail); //196 - 203			
			double commissionAmountDetail = 0.0;			
			if(row.getCommissionAmount() != null){				
				commissionAmountDetail = row.getCommissionAmount().doubleValue();
			}			
			// SDEX-TRAN-COMMISSION        
			populateWithFormattedValue("%+015.2f", commissionAmountDetail,  detail); // 204 - 217
			//SDEX-TRAN-COMM-REASON       
			populateWithFormattedValue("%-2s", row.getCommissionReasonCode() != null ? row.getCommissionReasonCode().trim() : FILLER,  detail); // 218 -219
			//SDEX-TRAN-P-A-FLAG          
			populateWithFormattedValue("%1s", row.getPrincipalAgentFlag() == null ? FILLER : row.getPrincipalAgentFlag().trim(), detail); //220
			//SDEX-TRAN-EXCHANGE          
			populateWithFormattedValue("%-2s", row.getExchange() != null ? row.getExchange().trim() : FILLER, detail); // 221 - 222			
			double expenseAmountDetail = 0.0;
			if(row.getExpensesAmount() != null){				
				expenseAmountDetail = row.getExpensesAmount().doubleValue();
			}			
			//SDEX-TRAN-EXPENSES          
			populateWithFormattedValue("%+015.2f", expenseAmountDetail,  detail); //223 - 236
			//SDEX-TRAN-INCOME            
			populateWithFormattedValue("%+019.2f", row.getIncomeAmount() != null ?  Math.abs(row.getIncomeAmount()) : (float)NUM_FILLER ,  detail);
			//SDEX-TRAN-TRADE-FX-AMNT     
			populateWithFormattedValue("%+019.2f", row.getTradeAmount() != null ? row.getTradeAmount() : (float)NUM_FILLER ,  detail);
			//SDEX-TRAN-TRADE-FX-RATE     
			populateWithFormattedValue("%+017.9f", row.getTradedUnroundedBaseExchangeRate() != null ? row.getTradedUnroundedBaseExchangeRate() : (float)NUM_FILLER ,  detail);
			//SDEX-TRAN-SETLE-FX-AMNT     
			populateWithFormattedValue("%+019.2f", row.getSettledAmount() != null ? row.getSettledAmount() : (float)NUM_FILLER ,  detail);
			//SDEX-TRAN-SETLE-FX-RATE     
			populateWithFormattedValue("%+017.9f", row.getSettledCurrencyExchangeRate() != null ? row.getSettledCurrencyExchangeRate(): (float)NUM_FILLER,  detail);
			//SDEX-TRAN-EFFT-DATE         
			populateWithFormattedValue("%-8s", row.getEffectiveDate() != null ? dateFormat.format(row.getEffectiveDate()) : "00000000" ,  detail);
			//SDEX-TRAN-YIELD             
			populateWithFormattedValue("%+013.7f", row.getPurchaseYieldAmount() != null ? row.getPurchaseYieldAmount() : (float)NUM_FILLER ,  detail);
			//SDEX-TRAN-TRC-SETTLE-CC     
			populateWithFormattedValue("%-4s", row.getSettlementCurrencyCode() != null ? row.getSettlementCurrencyCode().trim() : FILLER ,  detail);
			//SDEX-TRAN-TRC-SETTLE-AMT
			populateWithFormattedValue("%+019.2f", row.getSettlementCurrencyAmount() != null ? Math.abs(row.getSettlementCurrencyAmount()) : (float)NUM_FILLER,  detail);
			//SDEX-TRAN-TRC-SETTLE-RAT - FACDAL - 1696
			Double posCurrExchRate = 0.0;
			String poskey = getPositionDataMapKey(row);
			posCurrExchRate = positionDataMap.get(poskey);
			populateWithFormattedValue("%+019.9f", posCurrExchRate != null ? posCurrExchRate : (float)NUM_FILLER,  detail);
			//SDEX-TRAN-LOC-CODE
			populateWithFormattedValue("%-2s", row.getLocationCode() != null ? row.getLocationCode().trim() : FILLER ,  detail);
			//SDEX-TRAN-COMMIT-FLAG
			populateWithFormattedValue("%-2s", row.getCommitIndicator() != null ? row.getCommitIndicator().trim() : FILLER ,  detail);
			//SDEX-TRAN-TRAD-ORDER
			populateWithFormattedValue("%08d", row.getTransactionMemoNumber() != null ? row.getTransactionMemoNumber().longValue() : NUM_FILLER,  detail);
			//SDEX-TRAN-DESC
			String transactionDesc = geTransactionDesc(transactionDescMap,row);			
			populateWithFormattedValue("%-50s", transactionDesc != null ? transactionDesc : FILLER ,  detail);			
			//SDEX-TRAN-FTP-PTR
			populateWithFormattedValue("%08d", row.getRelatedMemoNumber() != null ? Long.parseLong(row.getRelatedMemoNumber().trim()) : NUM_FILLER ,  detail);
			//SDEX-TRAN-TS-REV-FLAG
			populateWithFormattedValue("%1s", row.getReversalIndicator() != null ? row.getReversalIndicator().trim() : FILLER ,  detail);			
			//FACDAL-1697 - SDEX-TRAN-TS-REV-PTR
			if(Constants.MSG_SUBTYPE_BOD.equalsIgnoreCase(row.getExtractKindCode())){
				populateWithFormattedValue("%+09d", ((row.getReversalMemoNumber() != null) ? row.getReversalMemoNumber().longValue(): NUM_FILLER) ,  detail);						
			}else{
				populateWithFormattedValue("%+09d", ((row.getTransactionReversalPointer() != null) ? row.getTransactionReversalPointer(): NUM_FILLER) ,  detail);
			}
			//SDEX-TRAN-ADVISOR-CODE
			populateWithFormattedValue("%-4s", row.getAdvisorCode() != null ? row.getAdvisorCode().trim() : FILLER ,  detail);
			//SDEX-TRAN-TS-REVS-CLASS
			populateWithFormattedValue("%1s", row.getSameDayReversalIndicator() != null ? row.getSameDayReversalIndicator().trim() : FILLER ,  detail);
			//SDEX-TRAN-FEC-CAN-FLAG
			populateWithFormattedValue("%1s", FILLER, detail); //FACDAL - 1737.
			//SDEX-TRAN-TS-REBOOK-PTR
			populateWithFormattedValue("%08d", row.getRebookMemoNumber() != null ? row.getRebookMemoNumber().longValue() : NUM_FILLER ,  detail);
			//SDEX-TRAN-TS-XREF-MEMO
			populateWithFormattedValue("%-25s", row.getExternalMemoNumber() != null ? row.getExternalMemoNumber() : FILLER ,  detail);
			//SDEX-TRAN-TS-USER-ENTRY-DATE
			populateWithFormattedValue("%8d", row.getUserDateStamp() == null ? NUM_FILLER : row.getUserDateStamp().longValue(),  detail);
			//SDEX-TRAN-TS-USER-ENTRY-TIME
			populateWithFormattedValue("%8d", row.getUserDateTime() == null ? NUM_FILLER : row.getUserDateTime().longValue(),  detail);
			//SDEX-TRAN-TRADE-FX-RATE-7 - FACDAL - 1716
			if(Constants.MSG_SUBTYPE_EOD.equalsIgnoreCase(row.getExtractKindCode())){
				Double exchangeRate = row.getTradeForeignExchangeRate();
				populateWithFormattedValue("%+018.9f", exchangeRate != null ? exchangeRate: (float)NUM_FILLER,  detail);							
			} else {
				Double unroundedCurrExchRate = row.getTradedUnroundedBaseExchangeRate();
				populateWithFormattedValue("%+018.9f", unroundedCurrExchRate != null ? unroundedCurrExchRate: (float)NUM_FILLER,  detail);				
			}
			//SDEX-TRAN-SETLE-FX-RATE-7 - FACDAL - 1717
			Double settleForeignExchangeRate = Double.parseDouble(row.getSettleForeignExchangeRate());
			populateWithFormattedValue("%+018.9f", settleForeignExchangeRate != null ? settleForeignExchangeRate: (float)NUM_FILLER,  detail);			
			//LCL-ORIG-FACE-AMT
			populateWithFormattedValue("%+021.4f", row.getOriginalFaceAmount() != null ? Math.abs(row.getOriginalFaceAmount()) : (float)NUM_FILLER,  detail);			
			populateWithFormattedValue("%1s",FILLER,  detail);			
			String recordDetail = detail.toString();
			//FID-TRAN-REC-INDICATOR
			recordDetail = setTranRecIndicator(recordDetail, previousDetail);
			//Util.log("Detail End : -----------------------------------------------");			
			fw.writeRecord(recordDetail);		
			if (geodeFunds.contains(row.getPortfolioFundNumber()))
				fwGeode.writeRecord(recordDetail);
			previousRow = row;
			previousSecurityData = securityData;
			if(securityData != null && securityData.getIssueCurrencyAssignedOnSecurityDefinition() != null) {
				fidTranTradedCurrencyCode = securityData.getIssueCurrencyAssignedOnSecurityDefinition(); //FACDAL-1662
			}
			
			//FIX for jira FACDAL-2229. For BOD transaction file, get EOD IssueCurrencyCode
			if(extractKindCode.equals("BOD")) {
				if(securityDataEOD != null && securityDataEOD.getIssueCurrencyAssignedOnSecurityDefinition() != null) {
					fidEODTranTradedCurrencyCode = securityDataEOD.getIssueCurrencyAssignedOnSecurityDefinition(); 
				}
			}
			// End for fix jira FACDAL-2229.
			
			previousDetail = recordDetail;
		}
		if(previousRow != null) {
			if(extractKindCode.equals("BOD")) {
				if(fidTranTradedCurrencyCode == null) {
					fidTranTradedCurrencyCode = fidEODTranTradedCurrencyCode; 
				}
			}
			writeSummary(commonFields, previousRow, fidTranTradedCurrencyCode, securityData);
		}
	}

	private String getSecRefCUSIP(SecurityData securityData,
			Map<String, String> fmrIdMap, HoldingTransactionData row,
			StringBuilder sb) {
		String cusip = null;
		//For Currency Balances
		if(!fmrIdMap.isEmpty() && row.getInvestOneSecurityCusipNumber() != null && fmrIdMap.containsKey(row.getInvestOneSecurityCusipNumber())){
			cusip = fmrIdMap.get(row.getInvestOneSecurityCusipNumber());
			return cusip;			
		}
		// SPOT CONTRACTS
		else if(row.getCurrencyContractType() != null && row.getCurrencyContractType().equals("SPOT") && !fmrIdMap.isEmpty() && row.getInvestOneSecurityCusipNumber() != null &&
			fmrIdMap.containsKey(row.getInvestOneSecurityCusipNumber().substring(3,6))) {
			cusip = fmrIdMap.get(row.getInvestOneSecurityCusipNumber().substring(3,6));
			return cusip;			
		}
		else if(row.getCurrencyContractType() != null && row.getCurrencyContractType().equals("FWD")){
			cusip = row.getCurrencyContractCUSIPNumber();
			return cusip;			
		}
		else{
			if(securityData != null && securityData.getCUSIPNumber() != null) {
			cusip = securityData.getCUSIPNumber().trim();
			return cusip;
			}
		}		
		return cusip;
	}

	private String geTransactionDesc(
			HashMap<String, String> transactionDescMap,
			HoldingTransactionData row) {
		String transactionDesc = null;
		// FACDAL - 1719
		if(!transactionDescMap.isEmpty() && row.getTransactionCode() != null && transactionDescMap.containsKey(row.getTransactionCode()))
		{
			transactionDesc = transactionDescMap.get(row.getTransactionCode());				
		}
		if(StringUtils.isNotBlank(transactionDesc)){				
			if(transactionDesc.length()>50){					
				transactionDesc = transactionDesc.substring(0,50).trim();
			}else{					
				transactionDesc = transactionDesc.trim();
			}
		}
		return transactionDesc;
	}

	private void populateTransDescMap(HashMap<String, String> transactionDescMap) {
		List<FocasTransVal> transactionDescList = ApplicationContext.getFocasDataConverter().getMapping("TXN_C");
		
		for(FocasTransVal focasTransVal:transactionDescList){
			transactionDescMap.put(focasTransVal.getINVESTONE_VALUE(),focasTransVal.getINVESTONE_DESC());		
		}
	}
	
	private ArrayList<HoldingTransactionData> removeDuplicates(ArrayList<HoldingTransactionData> transactions) {
		LinkedList<HoldingTransactionData> list = new LinkedList<HoldingTransactionData>(transactions);
		
		for(int i=0;i<list.size()-1;){
			HoldingTransactionData h1 = list.get(i);
			String key1 = getKeyForDup(h1);

			for(int j=i+1;j<list.size();){
				
				HoldingTransactionData h2 = list.get(j);
				String key2 = getKeyForDup(h2);
				if(key1.equals(key2)){
					//FACDAL - 2391
					if (StringUtils.isNotBlank(h1.getTaxLotMemoNumber())){
						list.remove(j);
					} else {
						list.remove(i);
					}
				}else{
					i=j;
					break;
				}
			}
		}
		return new ArrayList<HoldingTransactionData>(list);
	}
	
	private String getFundMapKey(HoldingTransactionData hdgTransData){
		
		StringBuilder fundMapKey = new StringBuilder();
		fundMapKey.append(String.valueOf(hdgTransData.getSourceCode()));
		fundMapKey.append("-");
		fundMapKey.append(String.valueOf(hdgTransData.getFundNumber()));
		fundMapKey.append("-");
		fundMapKey.append(String.valueOf(hdgTransData.getPortfolioFundNumber()));
		fundMapKey.append("-");
		fundMapKey.append(String.valueOf(hdgTransData.getCostBasisCode()));
		return fundMapKey.toString();
	}

	private String getPositionDataMapKey(HoldingTransactionData hdgTransData){
			
		StringBuilder positionDataMapKey = new StringBuilder();
		positionDataMapKey.append(String.valueOf(hdgTransData.getPortfolioFundNumber()));
		positionDataMapKey.append("-");
		positionDataMapKey.append(String.valueOf(hdgTransData.getInvestOneSecurityCusipNumber()));
		positionDataMapKey.append("-");
		positionDataMapKey.append(String.valueOf(hdgTransData.getInvestOneSecurityDate()));
		positionDataMapKey.append("-");
		positionDataMapKey.append(String.valueOf(hdgTransData.getInvestOneSecurityQualifier()));
		return positionDataMapKey.toString();
	}

	private String getKeyForDup(HoldingTransactionData row){
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(String.valueOf(row.getFundNumber()));
		buffer.append("-");
		buffer.append(String.valueOf(row.getPortfolioFundNumber()));
		buffer.append("-");
		buffer.append(String.valueOf(row.getTransactionMemoNumber().longValue()));
		return buffer.toString();
	}
	
	private void getTaxlot(String asOfDateStr, String sourceCode) throws Exception {

		Util.log(this, "Entering  getTaxlot ...");
		String taxlotSql = "PKG_FEED_PROCS.GET_TAXLOT_MEMO_NO_LEGACY";
		taxlotMap = new HashMap<String, String>();
		Object[] params = new Object[4];
		params[0] = asOfDateStr;
		params[1] = extractKindCode;
		params[2] = "FA";
		params[3] = sourceCode;
		
		List<DynaBean> rows = null;
		try{
			rows = DBUtil.executePlQuery(taxlotSql, params, 10000);
		}catch(Exception e){
			
			Util.log(this,"Exception calling taxlotSql ",Constants.LOG_LEVEL_ERROR,e);
		}
		
		if(rows != null && rows.size() > 0 ) {
			
			Iterator<DynaBean> iter = rows.iterator();
			
			while(iter.hasNext()) {
				DynaBean dynaBean = iter.next();
				
				taxlotMap.put((dynaBean.get("FUND_N") != null ?  dynaBean.get("FUND_N").toString().trim() : null) + "-" + 
						      (dynaBean.get("PORT_FUND_N") != null ?  dynaBean.get("PORT_FUND_N").toString().trim() : null) + "-" + 
							  (dynaBean.get("CUSP_N") != null ?  dynaBean.get("CUSP_N").toString().trim() : null) + "-" +
						      (dynaBean.get("TL_MEMO_N") != null ?  dynaBean.get("TL_MEMO_N").toString().trim() : null), 
						      dynaBean.get("TL_DESC_4L") != null ?  dynaBean.get("TL_DESC_4L").toString().trim() : null);
			}
			
		}
		Util.log(this, "Taxlot Count : " + (taxlotMap != null ? taxlotMap.size():0));	
	    Util.log(this, "Exiting  getTaxlot ...");
	}	
	
	/**
	 * 
	 * @param currRecord
	 * @param prevRecord
	 * @return
	 */
	private String setTranRecIndicator(String currRecord, String prevRecord) {
		String tranRecIndicator = "A";
		int index = 56;
		if(DELIMITER.equals(","))
			index = 63;
		String cKey1 = currRecord.substring(0, index);
		String cKey2 = currRecord.substring(index);
		if(prevRecord != null) {
			
			String pKey1 = prevRecord.substring(0, index);
			String pKey2 = prevRecord.substring(index+1);
			if(pKey1.equals(cKey1) && pKey2.equals(cKey2))
				tranRecIndicator = "C";
			else {
				tranRecIndicator = "D";
			}
		}
		currRecord = cKey1  + tranRecIndicator + DELIMITER + cKey2;
		return currRecord;
	}
	
	private ArrayList<HoldingTransactionData> getHoldingsTransactions(String[] fundNumberSet, String sourceCode, String asOfDateStr) throws Exception {
		
		Util.log(this, "Entering  getHoldingsTransactions ...");
		Util.log(this, "Parameters : fundNumberSet : " + fundNumberSet.toString() +", sourceCode : " + sourceCode + ", asOfDateStr : " + asOfDateStr);
		HoldingsTransaction holdingsTransaction = (HoldingsTransaction) ApplicationContext.getContext().getBean("HoldingsTransaction");
		ArrayList<HoldingTransactionData> transactions = null;
		
    	Criteria holdingTransactionCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
    	holdingTransactionCriteria.addCriterion(HoldingTransactionCriteriaBuilder.addAsOfDateFilter(asOfDateStr, Operator.OPERATOR_EQ));
    	holdingTransactionCriteria.addCriterion(HoldingTransactionCriteriaBuilder.addExtractKindCodeFilter(extractKindCode, Operator.OPERATOR_EQ));
    	holdingTransactionCriteria.addCriterion(HoldingTransactionCriteriaBuilder.addSourceCodeFilter(sourceCode, Operator.OPERATOR_IN));
    	holdingTransactionCriteria.addCriterion(HoldingTransactionCriteriaBuilder.addCostBasisCodeFilter("FA",Operator.OPERATOR_EQ));
    	
    	// Sorting by Fund Number
		OrderByCriteria orderByCriteria = (OrderByCriteria)ApplicationContext.getContext().getBean("OrderByCriteria");
		orderByCriteria.addOrderByOn(HoldingTransactionData.FIELD_fundNumber).asc();
		orderByCriteria.addOrderByOn(HoldingTransactionData.FIELD_transactionMemoNumber).asc();
		orderByCriteria.addOrderByOn(HoldingTransactionData.FIELD_portfolioFundNumber).asc();
		orderByCriteria.addOrderByOn(HoldingTransactionData.FIELD_taxLotMemoNumber).asc();
		transactions = (ArrayList<HoldingTransactionData>) holdingsTransaction.getHoldingTransactionSetByFundNumber(fundNumberSet, holdingTransactionCriteria, orderByCriteria );
				    
	    Util.log(this, "Transaction record count: " + (transactions != null ? transactions.size():0));
	    Util.log(this, "Exiting  getHoldingsTransactions ...");
	    return transactions;
	}	
	
	private void getPositionData(String[] fundNumbers,String asOfDateStr, String fundType,String srcCodes) throws Exception {
		
		Util.log(this, "Entering  getPositionData ...");
		Util.log(this, "Parameters : fieMM : "+ fundType + ", asOfDateStr : " + asOfDateStr);
		Position position =  (Position)ApplicationContext.getContext().getBean("Position");
		
    	Criteria criteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
    	
    	criteria.addCriterion(PositionCriteriaBuilder.addSourceCodeFilter(srcCodes, Operator.OPERATOR_IN)); 
    	criteria.addCriterion(PositionCriteriaBuilder.addAsOfDateFilter(asOfDateStr, Operator.OPERATOR_EQ));
    	criteria.addCriterion(PositionCriteriaBuilder.addExtractKindCodeFilter( extractKindCode, Operator.OPERATOR_EQ));
    	criteria.addCriterion(PositionCriteriaBuilder.addCostBasisCodeFilter("FA",Operator.OPERATOR_EQ));
    	// Sorting by Fund Number
		OrderByCriteria orderByCriteria = (OrderByCriteria)ApplicationContext.getContext().getBean("OrderByCriteria");
			orderByCriteria.addOrderByOn(FundPortfolioData.FIELD_fundNumber).asc();
		orderByCriteria.addOrderByOn(FundPortfolioData.FIELD_portfolioFundNumber).asc();
		
		Collection<PositionData> positionDataList = position.getPositionSetByFundNumber(fundNumbers, criteria, orderByCriteria);
	
		Util.log(this, "position record count: " + (positionDataList != null ? positionDataList.size():0));
		
		if(positionDataList != null){
			for(PositionData data : positionDataList) {
				StringBuilder key = new StringBuilder();
				if(data.getPortfolioFundNumber() != null && data.getInvestOneSecurityCusipNumber() != null && data.getInvestOneSecurityDate() != null &&
						data.getInvestOneSecurityQualifier() != null ){
					key.append(String.valueOf(data.getPortfolioFundNumber()));
					key.append("-");
					key.append(String.valueOf(data.getInvestOneSecurityCusipNumber()));
					key.append("-");
					key.append(String.valueOf(data.getInvestOneSecurityDate()));
					key.append("-");
					key.append(String.valueOf(data.getInvestOneSecurityQualifier()));
					if(data.getCurrencyExchangeRate() != null )
						positionDataMap.put(key.toString(), data.getCurrencyExchangeRate());
					if(data.getPriceCode() != null )
						positionDataMapForPriceCode.put(key.toString(), data.getPriceCode());
					if(data.getUnitOfCalculationCode() != null)
						positionMapForUnitOfCalc.put(key.toString(),data.getUnitOfCalculationCode());
				}
			}
			Util.log(this, "positionDataList record count: " + positionDataList.size());
		}
		
		
	    Util.log(this, "Exiting  getPositionData ...");
	}    
	
	/**
	 * 
	 * @param fundNumberSet
	 * @param asOfDateStr
	 * @return
	 * @throws Exception
	 */
	private ArrayList<FundPortfolioData> getRetailFunds(String asOfDateStr, String fundType,String srcCodes) throws Exception {
		
		Util.log(this, "Entering  getRetailFunds ...");
		Util.log(this, "Parameters : fieMM : "+ fundType + ", asOfDateStr : " + asOfDateStr);
		FundPortfolio fundPortfolio = (FundPortfolio) ApplicationContext.getContext().getBean("FundPortfolio");
		ArrayList<FundPortfolioData>  fundPortfolioList = null;
		
    	Criteria fundPortfolioCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
    	
    	fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addSourceCodeFilter(srcCodes, Operator.OPERATOR_IN)); //FOCMM,FOCFIE, 
    	fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addAsOfDateFilter(asOfDateStr, Operator.OPERATOR_EQ));
    	fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addExtractKindCodeFilter( extractKindCode, Operator.OPERATOR_EQ));
    	fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addCostBasisCodeFilter("FA",Operator.OPERATOR_EQ));
    	// Sorting by Fund Number
		OrderByCriteria orderByCriteria = (OrderByCriteria)ApplicationContext.getContext().getBean("OrderByCriteria");
			orderByCriteria.addOrderByOn(FundPortfolioData.FIELD_fundNumber).asc();
		orderByCriteria.addOrderByOn(FundPortfolioData.FIELD_portfolioFundNumber).asc();
		
		String[] fundNumbers = new String[] {}; 
		//String[] fundNumbers = new String[] {"2277"}; 
		fundPortfolioList = (ArrayList<FundPortfolioData>) fundPortfolio.getFundPortfolioSet(fundNumbers, fundPortfolioCriteria, orderByCriteria);
		
		Util.log(this, "Fund Portfolio record count: " + (fundPortfolioList != null ?fundPortfolioList.size():0));
		
		if(fundPortfolioList == null || fundPortfolioList.isEmpty()){			
			return null;
		}
		
		for(FundPortfolioData data : fundPortfolioList) {
			StringBuilder sb = new StringBuilder();
			sb.append(String.valueOf(data.getSourceCode()));
			sb.append("-");
			sb.append(String.valueOf(data.getFundNumber()));
			sb.append("-");
			sb.append(String.valueOf(data.getPortfolioFundNumber()));
			sb.append("-");
			sb.append(String.valueOf(data.getCostBasisCode()));
			fundMap.put(sb.toString(), data);
		}
	    
	    Util.log(this, "Distinct Fund Portfolio record count: " + fundPortfolioList.size());
	    Util.log(this, "Exiting  getRetailFunds ...");
	    
	    return fundPortfolioList;
	}
	
	/**
	 * 
	 * @param cusipNo
	 * @return
	 * @throws Exception
	 */
	private void getSecurity(String asOfDate,  String sourceCode, String extractKindCode, ArrayList<HoldingTransactionData> transactions, Security security) throws Exception {
		
		Util.log(this, "Entering getSecurity(asOfDate, sourceCode, transactions ");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Security Data");
		Criteria securityCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
    	securityCriteria.addCriterion(SecurityCriteriaBuilder.addAsOfDateFilter(asOfDate, Operator.OPERATOR_EQ));
    	securityCriteria.addCriterion(SecurityCriteriaBuilder.addSourceCodeFilter(sourceCode, Operator.OPERATOR_IN));
    	securityCriteria.addCriterion(SecurityCriteriaBuilder.addExtractKindCodeFilter(extractKindCode, Operator.OPERATOR_EQ));
    	securityCriteria.addCriterion(SecurityCriteriaBuilder.addCostBasisCodeFilter("FA",Operator.OPERATOR_EQ));
    	
    	Collection<SecurityData>  securities = security.getSecuritySetByCusipNumber(transactions, securityCriteria);
		stopWatch.stop();
		Util.log(this, "Security retrieve time : " + stopWatch.getLastTaskTimeMillis());;
	    Util.log(this, "Security record count: " + (securities != null ? securities.size() : 0));
	    security.setSecurityDataCache((ArrayList<SecurityData>)securities);
	    Util.log(this, "Exiting  getSecurity ...");
	}
	
	/**
	 * 
	 * @param sb
	 * @param commonFields
	 * @param shareParQuantity
	 * @param principalAmount
	 * @param commissionAmount
	 * @param expenseAmount
	 * @param row
	 */
		
	private void writeSummary(String commonFields, HoldingTransactionData row, String currencyCode,SecurityData securityData) {
		
		StringBuilder summary = new StringBuilder(commonFields); //FID-TRAN-FIDELITY   REDEFINES FID-TRAN-DATA-REC.
		populateWithFormattedValue("%1s", SUMMARY, summary);
		populateWithFormattedValue("%1s", "A", summary); //FID-TRAN-REC-INDICATOR
		populateWithFormattedValue("%10s", FILLER, summary);
		//FID-TRAN-TRADED-CCY 
		populateWithFormattedValue("%-4s", (currencyCode != null  ? currencyCode : FILLER), summary);
		double  tranPrice = 0.0;
		tranPrice = row.getLocalTransactionPriceAmount() != null ? row.getLocalTransactionPriceAmount():0.0;
		//FID-TRAN-PRICE              
		populateWithFormattedValue("%+020.8f", Math.abs(tranPrice), summary);
		//FID-TRAN-BROKER
		populateWithFormattedValue("%-7s", row.getTransactionExecutedBrokerCode() != null ? row.getTransactionExecutedBrokerCode() : FILLER, summary);
		//FID-TRAN-BROK-CLR
		populateWithFormattedValue("%-7s", row.getBrokerClearing() != null ? row.getBrokerClearing() : FILLER, summary); // new field sdex-tran-brok-clr
		
		String legacyTaxLotId = getLegacyTaxlotId(row);
		//LEGACY_TAX LOT _ID
		populateWithFormattedValue("%-16s", legacyTaxLotId == null ? FILLER : legacyTaxLotId, summary); // legacy taxlot
		populateWithFormattedValue("%-450s", FILLER, summary);
		
		fw.writeRecord(summary.toString());
		if (geodeFunds.contains(row.getPortfolioFundNumber()))
			fwGeode.writeRecord(summary.toString());
	}
	
	/**
	 * 
	 * @param row
	 * @return
	 */
	private String getLegacyTaxlotId(HoldingTransactionData row) {
		String legacyTaxlotNo = null;
		String key = row.getFundNumber() + "-" + 
				row.getPortfolioFundNumber() + "-" + 
				row.getCUSIPNumber() + "-" +
				row.getTaxLotMemoNumber();
		legacyTaxlotNo  = taxlotMap.get(key);
		return legacyTaxlotNo;
	}

	private void populateWithFormattedValue(String format, Number value,  StringBuilder sb) {
		String formattedValue = String.format(format, value != null ?  value : (float)NUM_FILLER ).replaceAll(REG_EX_PERIOD, BLANK_STRING);
		sb.append(formattedValue + DELIMITER);
	}
	
	/**
	 * 
	 * @param value
	 * @param format
	 * @return 
	 */
	private void populateWithFormattedValue(String format, String value, StringBuilder sb) {
		String formattedValue = (value == null ? FILLER : String.format(format, value));
		if(UNKNOWN.equals(value)) {
			formattedValue = formattedValue.replaceAll(" ", "?");
		} 
		sb.append(formattedValue + DELIMITER); 
	}
	
	
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new Initialize();
		
		
		TransactionsFeed transactionFeed = new TransactionsFeed();
		Exception ex = new Exception("Testing logError");
		
		Util.log(transactionFeed, "error ...", Constants.LOG_LEVEL_ERROR, ex);

		//ConfigDBAccess.getStartupApps("TRANSACTIONSFEEDEODMM", null);
		//transactionFeed.setFeedAppName("TRANSACTIONSFEEDEODMM");
		//transactionFeed.processMessage(65057);
		
		ConfigDBAccess.getStartupApps("TRANSACTIONSFEEDEODFIE", null);		
		transactionFeed.setFeedAppName("TRANSACTIONSFEEDEODFIE");
		transactionFeed.processMessage(32058);
		
		//ConfigDBAccess.getStartupApps("TRANSACTIONSFEEDBODMM", null);
		//transactionFeed.setFeedAppName("TRANSACTIONSFEEDBODMM");
		//transactionFeed.processMessage(66055);
		
		//ConfigDBAccess.getStartupApps("TRANSACTIONSFEEDBODFIE", null);
		//transactionFeed.setFeedAppName("TRANSACTIONSFEEDBODFIE");
		//transactionFeed.processMessage(64056);
		System.out.println("Done :)");
		System.exit(0);
	}
	
	/**
	 * 
	 * @author a515515
	 *
	 */
	public class FundPortfolioComparator implements Comparator<FundPortfolioData> {

		@Override
		public int compare(FundPortfolioData arg0, FundPortfolioData arg1) {
			int returnValue = 0;
			if(arg0 == null && arg1 != null)
				returnValue =  -1;
			else  if(arg0 != null && arg1 == null)
				returnValue = 1;
			else if(arg1 != null && arg0 != null) {
				returnValue = arg0.getSourceCode().compareToIgnoreCase(arg1.getSourceCode());
				if(returnValue == 0) {
					String key0 = arg0.getFundNumber()+"-"+arg0.getPortfolioFundNumber()+"-"+arg0.getCostBasisCode();
					String key1 = arg1.getFundNumber()+"-"+arg1.getPortfolioFundNumber()+"-"+arg1.getCostBasisCode();
					returnValue = key0.compareTo(key1);
				} 
			}
			return returnValue;
		}
		
	}
}