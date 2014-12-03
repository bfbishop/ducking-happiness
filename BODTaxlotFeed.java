package com.fidelity.dal.fundacct.feed.app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import oracle.jdbc.internal.OracleTypes;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StopWatch;

import com.fidelity.dal.fundacct.dalapi.beans.SecRefDatesData;
import com.fidelity.dal.fundacct.dalapi.beans.TaxlotData;
import com.fidelity.dal.fundacct.feed.Exception.DALFeedException;
import com.fidelity.dal.fundacct.feed.admin.ApplicationContext;
import com.fidelity.dal.fundacct.feed.bean.DALCalendar;
import com.fidelity.dal.fundacct.feed.bean.DALCalendar.SourceCalendar;
import com.fidelity.dal.fundacct.feed.bean.DALFeedConfiguration;
import com.fidelity.dal.fundacct.feed.bean.FileWriter;
import com.fidelity.dal.fundacct.feed.bean.SecRefDates;
import com.fidelity.dal.fundacct.feed.framework.db.ConfigDBAccess;
import com.fidelity.dal.fundacct.feed.framework.db.mapper.FocasTransVal;
import com.fidelity.dal.fundacct.feed.framework.feed.DBUtil;
import com.fidelity.dal.fundacct.feed.framework.feed.FeedsUtil;
import com.fidelity.dal.fundacct.feed.framework.jms.JMSMessageProcessorBase;
import com.fidelity.dal.fundacct.feed.framework.util.Constants;
import com.fidelity.dal.fundacct.feed.framework.util.Initialize;
import com.fidelity.dal.fundacct.feed.framework.util.SellLikeTransactions;
import com.fidelity.dal.fundacct.feed.framework.util.Util;


public class BODTaxlotFeed extends JMSMessageProcessorBase{
	
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
		private final String APP_NAME_MM = "BODTAXLOTFEEDMM";
		private final String APP_NAME_FIE = "BODTAXLOTFEEDFIE";
		private Date today = null;	
		private final String SPACE = "";
		private static String[] reportOutputFileName = null;
		private static String[] reportHeaderFileName = {"taxlotmm","taxlotfi"};
		private int taxlotCounter = 0;
		private DateTime asOfDay = null;
		private Date todayDate = null;
		private Date asOfDate = null;
		private DALCalendar dalCalendar = new DALCalendar();
		private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
		private SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		private DecimalFormat recordCountFormat = new DecimalFormat("000000000000000");
		private DecimalFormat dfmt09_09 = new DecimalFormat("000000000.000000000");
		private DecimalFormat dfmt09_4 = new DecimalFormat("000000000.0000");
		private DecimalFormat dfmt03  = new DecimalFormat("000");
		private DecimalFormat dfmt08  = new DecimalFormat("00000000");
		private DecimalFormat dfmt15  = new DecimalFormat("000000000000000");
		private DecimalFormat dfmt09_pos_temp  = new DecimalFormat("000000000");
		private DecimalFormat dfmt09_neg_temp  = new DecimalFormat("000000000");
		private DecimalFormat dfmt10_pos_temp  = new DecimalFormat("0000000000");
		private DecimalFormat dfmt10_neg_temp  = new DecimalFormat("0000000000");
		private DecimalFormat dfmt09_09_neg = new DecimalFormat("000000000.000000000");
		private DecimalFormat dfmt15_2_neg = new DecimalFormat("000000000000000.00");
		private DecimalFormat dfmt09_4_neg = new DecimalFormat("000000000.0000");
		private DecimalFormat dfmt01_8_neg = new DecimalFormat("0.00000000");
		private DecimalFormat dfmt01_9_neg = new DecimalFormat("0.000000000");
		private DecimalFormat dfmt11_8_neg = new DecimalFormat("00000000000.00000000");
		private DecimalFormat dfmt15_4_neg = new DecimalFormat("000000000000000.0000");
		private DecimalFormat dfmt09_09_pos = new DecimalFormat("000000000.000000000");
		private DecimalFormat dfmt15_2_pos = new DecimalFormat("000000000000000.00");
		private DecimalFormat dfmt09_4_pos = new DecimalFormat("000000000.0000");
		private DecimalFormat dfmt01_8_pos = new DecimalFormat("0.00000000");
		private DecimalFormat dfmt01_9_pos = new DecimalFormat("0.000000000");
		private DecimalFormat dfmt11_8_pos = new DecimalFormat("00000000000.00000000");
		private DecimalFormat dfmt15_4_pos = new DecimalFormat("000000000000000.0000");
		private StopWatch stopWatch = null;
		protected DateTimeFormatter dft1 = DateTimeFormat.forPattern("dd-MMM-yyyy");
		protected DateTimeFormatter dft2 = DateTimeFormat.forPattern("yyyyMMdd");
		protected DateTimeFormatter dft3 = DateTimeFormat.forPattern("yyyy-MM-dd");//yyyy-MM-dd
		private SimpleDateFormat fmt2 = new SimpleDateFormat("yyyyMMdd");
		private String exttCode = "BOD";
		private String bookView = "FA";
		private String taxView = "A1";
		private String FOCASMM = "FOCMM";
		private String FOCASFIE = "FOCFIE";
		private String FOCAS = "";
		private String IO = "";
		private String subAcctCodeIO = ""; 
		FileWriter fw = null;
		FileWriter fwGeode = null;
		private Connection conn = null;
		ArrayList<FocasTransVal> focasTransValueList = null;
		HashMap<String,FocasTransVal> as400ToISOMap = null;
		Date headerDate = null;
		DateTime headerDateTime = null;
		Double A1_AMRTD_BK_COST_A , A1_LOCL_AMRTD_BK_COST_A ,SEC_NXT_CALL_PUT_PRC_A,UNIT_MARKET_VALUE,POS_NXT_CALL_PUT_PRC_A = null;
		String POS_NEW_BUY_I , POS_MATU_POSN_I , POS_SB_ACC_C ,INV1_INT_SOLD_A,SEC_ASET_TY_C  = null;
		HashMap<String, Double> shrParQSumMap,accrDailyMatMap,positionMap,positionMapForCurrExch,rlvdCarrMap,lkupPrncMap, lkupBaseCostMap = null;
		HashMap<String, Double> intSoldAmtTransMap,intSoldAmtBaseTransMap,intPayTransMap,incmASumTransMap,usAccrCurrTransMap = null;
		HashMap<String, DateTime> accrualDaysFundPortMap,priordayAccrDaysFundPortMap = null;		
		HashMap<String, Timestamp> contStlmDateMap = null;
		HashSet<String> maturedTaxlotKey,matTaxlotKeys = null;
		String previousCurrencyCode , previousCurrencyISOCode = null;				
		private HashMap<String,Boolean> tempMatHashMap = new HashMap<String,Boolean>();
		private final String[] prcCodes = {"B","E","F","H","K","R"};
		private final String[] prcCodesList = {"B","E","F","H","K","R","C","Z"};
		private final String[] txnCodes = {"BINTOP", "BINT -", "INT", "INT +", "INT -", "INTOP", "INTOP+", "INTOP-", "RBINT", "RINT", "RINT -", "RINTOP", "IOINT",
				"IOINT-", "RBIONT", "RIONT", "INTIC", "INTIC+", "INTIC-", "RBSWNP", "RBSWNR", "RBSWP+", "RBSWP-", "RBSWR+","RBSWR-",
				"RSWNPD", "RSWNP+", "RSWNP-", "RSWNRC", "RSWNR+", "RSWNR-", "RBSWRN", "RBSWPN", "RSWRN", "SWINRC", "SWINR+", "SWINR-", "SWINRN", "SWINPD",
				"SWINP+", "SWINP-", "SWINPN"};
		private DateTime invalidDate = new DateTime("9999-01-01");
		private final String USR_STRG_KEY_ARRAY = "USR_STRG_KEY_ARRAY";
		private final String[] includeDummyCusipList = {"FUTINDEXG","CCPINDEXG","CNYCNHADJ"};
				
		public synchronized void processMessage(int event) throws Exception {
			
			try{
			Util.log(this,"Inside FMRTaxlotFeed.processMessage(): " +
					"										Event published by Orchestration : "+event);
			dfc = getConfiguration();
			reportOutputFileName = new String[]{"fpcmsdal.taxlots.mm","fpcmsdal.taxlots.fie"};
			if(event == (dfc.getAppId() * 1000 + Constants.BOD_TAXLOT_MM_FUNDS)){
				setFeedAppName(APP_NAME_MM);
				taxlotCounter=0;
			}
			if(event == (dfc.getAppId() * 1000 + Constants.BOD_TAXLOT_FIE_FUNDS)){
					setFeedAppName(APP_NAME_FIE);
					taxlotCounter=1;
			}
			stopWatch = new StopWatch(dfc.getAppName()+" for "+dfc.getOutputLocalFileName());
			stopWatch.start("TAXLOTFileGeneration");
			Util.log(this,"Configuration Parameters: "+dfc.toString());
			Util.log(this,"Target events from ConfigDataCache:\n "+dfc.getTargetEvents());
			todayDate = getToday();
			asOfDate = new DALCalendar().getNextCalendarDay(todayDate);
			asOfDay = new DateTime(asOfDate);
			headerDate = getToday();
			headerDateTime = new DateTime(headerDate);			
			doFeed();
			}catch(Exception e){
				throw new DALFeedException(this,getMessageToken(),e,dfc.getAppName());
			}
		}
		
		public boolean doFeed() throws Exception {
			boolean success = false;
			int recordCount = 0;
			String strAsOfDate = null;
			String hostName = Util.getHostName();
			Util.log(this,"Beginning Feed for FMR TAXLOT");
			
			try{
				today = new Date();
				setPrefixForFormatters(); 
				strAsOfDate = asOfDay.toString(dft3);
				if (null != reportOutputFileName)
					dfc.setOutputLocalFileName(reportOutputFileName[taxlotCounter]);
				populateAS400ToISOMap();
				fw = new FileWriter(dfc);
				dfc.setOutputLocalFileName("fpcmsdal.taxlots.fie.geode");
				fwGeode = new FileWriter(dfc);
				Util.log(this,"this is the new filewriter " + ": " + fw.toString());
				writeHeader(hostName);
				conn = ApplicationContext.getConnection();
				Util.log("Free Memory before calling gather data in BODTaxlotFeed.java "+Runtime.getRuntime().freeMemory()/1000000);
				for(int k=1;k<=dfc.getSourceCodes().length ; k++){					
					if(dfc.getSourceCode(k).startsWith("IO"))
			    	 {
			    		 IO = dfc.getSourceCode(k);
			    		 gatherDataforIO(strAsOfDate,recordCount,IO);
			    		 gatherDataforMaturedTaxlots(IO);
			    		 cleanup();
			    	 }
			    	 if(dfc.getSourceCode(k).startsWith("FOC")){
			    		 FOCAS = dfc.getSourceCode(k);
			    		 gatherDataforFOCAS(strAsOfDate,recordCount,FOCAS);
			    	 } 
				}
				writeTrailer(hostName);			
				fw.close(false);	
				fwGeode.close(false);
				sendFile(fw,dfc);
				success=true;
				stopWatch.stop();
				logRunMetrics(this,Constants.LOG_TYPE_STATS,stopWatch.getTotalTimeSeconds(), fw.getNoOfRecordsWritten(),
						stopWatch.prettyPrint());
				logRunMetrics(this,Constants.LOG_TYPE_RESULT,stopWatch.getTotalTimeSeconds(), fw.getNoOfRecordsWritten(),
						Constants.LOG_RESULT_SUCCESS);
				}catch (Exception e) {
					Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
					success = false;
					throw e;
				}finally{
				if(fw != null){
					fw = null;	
				}
				if(conn!=null){
					conn.close();
				}
			}
				Util.log(this.getClass(),"End Feed development for "+ dfc.getOutputLocalFileName());
				return success;
		}

		private void cleanup() {
		
			accrualDaysFundPortMap = null;	
			shrParQSumMap = null;
			priordayAccrDaysFundPortMap = null;			
			intPayTransMap = null;
			intSoldAmtBaseTransMap = null;
			intSoldAmtTransMap = null;
			incmASumTransMap = null;
			usAccrCurrTransMap = null;
			accrDailyMatMap = null;
			positionMap = null;
			positionMapForCurrExch = null;
			contStlmDateMap = null;
			rlvdCarrMap = null;
			matTaxlotKeys = null;
			lkupPrncMap = null;
			lkupBaseCostMap = null; 
		}

		private void writeTrailer(String hostName) {
			String tPrefix = String.format("%6s%1s%8s%1s%8s%1s%8s%1s%8s%1s%6s%1s","IMATRL",SPACE,
																			(hostName== null ? "fpcms004":
																			(hostName).substring(0,8)),
																			SPACE,
																			reportHeaderFileName[taxlotCounter],
																		SPACE,
																		headerDateTime.toString(dft2)
																		,SPACE,
																		dateFormat2.format(today),SPACE,
																		timeFormat.format(today),SPACE);
			fw.writeln(tPrefix + recordCountFormat.format(fw.getNoOfRecordsWritten()));
			fwGeode.writeln(tPrefix + recordCountFormat.format(fwGeode.getNoOfRecordsWritten()));			
		}

		private void writeHeader(String hostName) {
			String hPrefix = String.format("%6s%1s%8s%1s%8s%1s%8s%1s%8s%1s%6s%1s","IMAHDR", SPACE, 
																			   (hostName== null ? "fpcms004":
																				(hostName).substring(0,8)),
																			   SPACE,
																			   reportHeaderFileName[taxlotCounter],
																			   SPACE,
																			   headerDateTime.toString(dft2)
																			   ,SPACE,
																			   dateFormat2.format(today),SPACE,
																			   timeFormat.format(today), SPACE);
			recordCountFormat.format(fw.getNoOfRecordsWritten());
			fw.writeln(hPrefix + recordCountFormat.format(fw.getNoOfRecordsWritten()));
			fwGeode.writeln(hPrefix + recordCountFormat.format(fwGeode.getNoOfRecordsWritten()));			
		}
		
		private void populateAS400ToISOMap() {
			focasTransValueList = (ArrayList<FocasTransVal>)ApplicationContext.getFocasDataConverter().getMapping("CURR_C");
			as400ToISOMap = new HashMap<String, FocasTransVal>();
			if(focasTransValueList!=null && !focasTransValueList.isEmpty()){
				for(FocasTransVal row:focasTransValueList){
					if(row.getAS400_VALUE()!=null)
						as400ToISOMap.put(row.getAS400_VALUE(), row);
				}
			}
		}
		
		private void setPrefixForFormatters() {
			dfmt09_09_neg.setNegativePrefix("-");
			dfmt15_2_neg.setNegativePrefix("-");
			dfmt09_4_neg.setNegativePrefix("-");
			dfmt01_8_neg.setNegativePrefix("-");
			dfmt11_8_neg.setNegativePrefix("-");
			dfmt15_4_neg.setNegativePrefix("-");
			dfmt09_neg_temp.setNegativePrefix("-");
			dfmt10_neg_temp.setNegativePrefix("-");
			dfmt01_9_pos.setPositivePrefix("+");
			dfmt01_9_neg.setNegativePrefix("-");
			dfmt09_09_pos.setPositivePrefix("+");
			dfmt09_pos_temp.setPositivePrefix("+");
			dfmt10_pos_temp.setPositivePrefix("+");
			dfmt15_2_pos.setPositivePrefix("+"); 
			dfmt09_4_pos.setPositivePrefix("+"); 
			dfmt01_8_pos.setPositivePrefix("+"); 
			dfmt11_8_pos.setPositivePrefix("+"); 
			dfmt15_4_pos.setPositivePrefix("+");
		}
		
		private void gatherDataforFOCAS(String strAsOfDate,int recordCount, String srcCode) throws Exception{
			CallableStatement stmt = null;
			ResultSet results = null;
			try{								
			
				strAsOfDate = asOfDay.toString(dft1);
				String taxlotQueryforfocas = "BEGIN PKG_FEED_BODTAXLOTFEED.QUERY_FOR_FOCUS(?,?,?,?,?,?); END;";
				
				stmt = conn.prepareCall(taxlotQueryforfocas);
				stmt.setString(1, exttCode);
				stmt.setString(2, srcCode);
				stmt.setString(3, bookView);
				stmt.setString(4, strAsOfDate);
				stmt.setString(5, taxView);		
				stmt.registerOutParameter(6, OracleTypes.CURSOR); //REF CURSOR
				stmt.setFetchSize(50000);
				Util.log("Taxlot Query is : " + taxlotQueryforfocas);
				long startTime = System.currentTimeMillis();
				stmt.execute();
				results = (ResultSet) stmt.getObject(6);
				results.setFetchSize(50000);
				long endTime = System.currentTimeMillis();
				Util.log("Time taken for Query Execution: " + (endTime - startTime)	/ 1000 + " seconds");
				startTime = System.currentTimeMillis();
		  	    while (results.next()) {
				 	TaxlotData taxlotFOCASData = new TaxlotData();			 			
				 	taxlotFOCASData = setTaxlotForFOCAS(results, taxlotFOCASData,false);
					if(taxlotFOCASData != null ){
						recordCount = writeContentforFOCAS(taxlotFOCASData ,recordCount);
					}
					taxlotFOCASData = null;
				}
		  	  	fw.flushRecordBuffer();		
		  	  	fwGeode.flushRecordBuffer();
		  	  	endTime = System.currentTimeMillis();
		  	  	Util.log("Time taken for populating objects from the query: "+(endTime-startTime)/1000 + " seconds");							
			}catch(Exception e){
				Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
																			"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				throw e;
			}
			finally{
				if(stmt != null)
				 stmt.close();
				if(results != null)
				 results.close();
			 }				
		}

	private TaxlotData setTaxlotForFOCAS(ResultSet results, TaxlotData taxlotFOCASRow,boolean isMatured) throws SQLException {
		A1_AMRTD_BK_COST_A = 0.0;
		A1_AMRTD_BK_COST_A = results.getDouble("A1_AMRTD_BK_COST_A");
		A1_LOCL_AMRTD_BK_COST_A = 0.0;
		A1_LOCL_AMRTD_BK_COST_A = results.getDouble("A1_LOCL_AMRTD_BK_COST_A");
		POS_NEW_BUY_I = results.getString("POS_NEW_BUY_I");
		POS_MATU_POSN_I = results.getString("POS_MATU_POSN_I");
		if(isMatured){
			taxlotFOCASRow.setFocasMaturedPositionFlag("Y");
		}
		else{
			taxlotFOCASRow.setFocasMaturedPositionFlag(POS_MATU_POSN_I);
		}
		POS_NXT_CALL_PUT_PRC_A = 0.0;
		POS_NXT_CALL_PUT_PRC_A = results.getDouble("POS_NXT_CALL_PUT_PRC_A");
		POS_SB_ACC_C = results.getString("POS_SB_ACC_C");
		
		taxlotFOCASRow.setUnitOfCalculation(results.getDouble("UNIT_OF_CALC"));
		taxlotFOCASRow.setDailyInterestAcruedAmount(results.getDouble("DLY_INT_ACRU_A"));
		taxlotFOCASRow.setLocalInterestSoldAmount(results.getString("LOCL_INT_SLD_A"));
		taxlotFOCASRow.setTradingApplicationTradeId(results.getString("TRD_APPL_TRD_ID"));
		taxlotFOCASRow.setBeginFiscalYearCumulativeAmortizationAmount(results.getDouble("BG_FISC_YR_CUM_AMRT_A"));
		taxlotFOCASRow.setPriorDayCumulativeAmort(results.getDouble("PR_DAY_CUM_AMRT"));
		taxlotFOCASRow.setInterestPaymentAmountLocal(results.getDouble("LOCL_INT_PMT_A"));
		taxlotFOCASRow.setTransactionCurrencyCode(results.getString("TRX_CURR_C"));
		taxlotFOCASRow.setDailyAmortizationAmount(results.getDouble("DAY_AMRT_A"));
		taxlotFOCASRow.setInterestPrincipalCode(results.getDouble("INT_PRNC_C"));
		taxlotFOCASRow.setFocasSecurityDescription(results.getString("FCS_SEC_DS"));
		taxlotFOCASRow.setToBeAnnouncedIndicator(results.getString("TBA_I"));
		taxlotFOCASRow.setAsOfDate(results.getTimestamp("AS_OF_D"));
		taxlotFOCASRow.setFundNumber(results.getLong("FUND_N"));
		taxlotFOCASRow.setPortfolioFundNumber(results.getLong("PORT_FUND_N"));
		taxlotFOCASRow.setLongShortCode(results.getString("LNG_SHRT_C"));
		taxlotFOCASRow.setShareParQuantity(results.getDouble("SHR_PAR_Q"));
		taxlotFOCASRow.setCurrentFaceAmountAdjustedByUnsettledSells(results.getDouble("FACE_ADJ_UNSTL_SELL_A"));
		taxlotFOCASRow.setAmortizedBookCostAmount(results.getDouble("AMRTD_BK_COST_A"));
		taxlotFOCASRow.setLocalAmortizedBookCostAmount(results.getDouble("LOCL_AMRTD_BK_COST_A"));
		taxlotFOCASRow.setAccrualDayCount(results.getLong("ACRU_DAY_CNT"));
		taxlotFOCASRow.setLocalInterestPurchasedAmount(results.getDouble("LOCL_INT_PUR_A"));
		taxlotFOCASRow.setCUSIPNumber(results.getString("CUSP_N"));
		taxlotFOCASRow.setFMRCUSIPNumber(results.getString("FMR_CUSP_N"));
		taxlotFOCASRow.setTaxLotMemoNumberLegacy(results.getString("TL_DESC_4L"));
		taxlotFOCASRow.setExtractKindCode(results.getString("EXTT_KND_C"));
		taxlotFOCASRow.setCostBasisCode(results.getString("CST_BASIS_C"));
		taxlotFOCASRow.setTradeBasisCode(results.getDouble("TRD_BASIS_C"));
		taxlotFOCASRow.setSourceCode(results.getString("SRC_C"));
		taxlotFOCASRow.setLocalCurrentPayPeriodNetInterestAccruedAmount(results.getDouble("LOCL_CUR_PAY_PRD_NETINT_ACRU_A"));
		taxlotFOCASRow.setTotalCumulativeAmortization(results.getDouble("TT_CUM_AMRT"));
		taxlotFOCASRow.setTaxlotAdjustedOIDBasisAtPurchaseTaxLocal(results.getDouble("ADJ_BSE_AT_PUR_TX_LOCL"));
		taxlotFOCASRow.setEffectiveYieldToMaturityRate(results.getDouble("EFF_YLD_TO_MTY_R"));
		taxlotFOCASRow.setTaxLotMemoNumber(results.getString("TL_MEMO_N"));
		taxlotFOCASRow.setInvestOneSecurityCusipNumber(results.getString("INV1_SCTY_CUSIP_N"));
		taxlotFOCASRow.setInvestOneSecurityQualifier(results.getDouble("INV1_SCTY_QUAL_N"));
		taxlotFOCASRow.setInvestOneSecurityDate(results.getDouble("INV1_SCTY_DATE_N"));
		taxlotFOCASRow.setAccruedInterestAmountBase(results.getDouble("ACRU_INT_BS"));
		taxlotFOCASRow.setTaxlotCurrentBookCostYieldToWorst(results.getDouble("CUR_BK_CST_YLD_WRST"));
		taxlotFOCASRow.setInterestAmortizationAccretionThruDate(results.getTimestamp("INT_AMRT_ACRTN_D"));
		taxlotFOCASRow.setTaxlotCurrentNetDailyInterestAccruedAmountBase(results.getDouble("CUR_NET_DAY_INT_ACRU_A_BASE"));
		taxlotFOCASRow.setTaxlotCurrentInterestPurchasedAmountBase(results.getDouble("CUR_INT_PUR_A_BASE"));
		taxlotFOCASRow.setOriginalTradeDate(results.getTimestamp("ORGNL_TRD_D"));
		taxlotFOCASRow.setTaxlotPurchaseSettlementDate(results.getTimestamp("ORGNL_SETL_D"));
		taxlotFOCASRow.setAccountingPricingCurrencyExchangeRate(results.getDouble("ACC_PRCG_CURR_EXCH_R"));
		taxlotFOCASRow.setPriceAmount(results.getDouble("PRC_A"));
		taxlotFOCASRow.setCostAmountBase(results.getDouble("CST_BS_A"));
		taxlotFOCASRow.setCostAmount(results.getDouble("CST_A"));
		taxlotFOCASRow.setFocasUnitMarketValuePrice(results.getDouble("FCS_CUR_MKT_PRC_BASE"));
		taxlotFOCASRow.setTaxlotOriginalBookCostYieldToMaturity(results.getDouble("ORIG_BK_COST_YLD_MTY"));
		taxlotFOCASRow.setSecurityCallPutCode(results.getString("SEC_CALL_PUT_C"));
		taxlotFOCASRow.setCallPutNextIndicator(results.getString("CALL_PUT_NXT_I"));
		taxlotFOCASRow.setTaxlotDescription1(results.getString("TXLT_DS_1"));
		
		return taxlotFOCASRow;
	}
		
	

		
		private void gatherDataforIO(String strAsOfDate,int recordCount, String srcCode) throws Exception{
			CallableStatement stmt = null;
			ResultSet results = null;
			try{		
				strAsOfDate = asOfDay.toString(dft1);
				String strPriorBusinessDay = getPriorAsOfDay();
				DateTime EODAsOfDay = new DateTime(todayDate);			
				String strEODAsOfDay = EODAsOfDay.toString(dft1);
				String taxlotQueryforIO = "BEGIN PKG_FEED_BODTAXLOTFEED.GET_IO(?,?,?,?,?,?,?); END;";
				setSecRefValues(srcCode, strAsOfDate, bookView,exttCode);
				setAccrualDaysFromFundPort(bookView, exttCode, srcCode,strPriorBusinessDay);	
				getIntPaymentFromTrans(srcCode,strAsOfDate,exttCode);
				setIntSoldAmtBaseFromTrans(bookView, exttCode, srcCode,strAsOfDate);	
				/*if(srcCode.contains("FIE")){
					getLookupDataForFocas(Constants.FUND_TYPE_FOCAS_FIE);
				}*/
				stmt = conn.prepareCall(taxlotQueryforIO);
				stmt.setString(1, taxView);
				stmt.setString(2, srcCode);
				stmt.setString(3, bookView);
				stmt.setString(4, exttCode);
				stmt.setString(5, strAsOfDate);
				stmt.setString(6, strEODAsOfDay);
				stmt.registerOutParameter(7, OracleTypes.CURSOR); //REF CURSOR
				stmt.setFetchSize(50000);
				
				Util.log("Taxlot Query is : " + taxlotQueryforIO);
				long startTime = System.currentTimeMillis();
				stmt.execute();
				results = (ResultSet) stmt.getObject(7);

				results.setFetchSize(50000);
				long endTime = System.currentTimeMillis();
				Util.log("Time taken for Query Execution: " + (endTime - startTime)	/ 1000 + " seconds");
				tempHashMap = new HashMap<String,Boolean>();  //FACDAL 2131
				startTime = System.currentTimeMillis();
				while (results.next()) {
					TaxlotData taxlotIOData = new TaxlotData();
					taxlotIOData = setTaxlotForIO(results, taxlotIOData, false);						
					if (taxlotIOData != null) {
							String cusip = taxlotIOData.getCUSIPNumber();
							boolean dummyCusipRow = ApplicationContext.getFocasDataConverter().isDummyCusip(cusip);
							if(dummyCusipRow && !ArrayUtils.contains(includeDummyCusipList, cusip)){
								continue;
							}
							recordCount = writeContentforIO(taxlotIOData,recordCount);
					}
					taxlotIOData = null;
				}		
				fw.flushRecordBuffer();	
				fwGeode.flushRecordBuffer();
				endTime = System.currentTimeMillis();
				Util.log("Time taken for populating objects from the query: "+ (endTime - startTime) / 1000 + " seconds");					
		}catch(Exception e){
			Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
																		"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
			throw e;
		}
		finally{
			 if(stmt != null)
				 stmt.close();
			 if(results != null)
				 results.close();
		 }		
	}
		
		//This method will get the cut-over funds.
		private HashSet<String> getCutOverFunds(String strAsOfDate,String srcCode) throws Exception{
			CallableStatement stmt = null;
			ResultSet results = null;
			long startTime, endTime = 0;
			HashSet<String> cutOverportFundSet = new HashSet<String>();	
			try{
				String queryforCutOverFunds = "BEGIN PKG_FEED_BODTAXLOTFEED.GET_CUTOVERFUNDS(?,?,?,?,?); END;";
				int i = 1;	
				stmt = conn.prepareCall(queryforCutOverFunds);
				stmt.setString(i++, exttCode);
				stmt.setString(i++, srcCode);	
				stmt.setString(i++, bookView);
				stmt.setString(i++, strAsOfDate);				
				stmt.registerOutParameter(5, OracleTypes.CURSOR);
				stmt.setFetchSize(50000);
				
				Util.log("queryforCutOverFunds is : " + queryforCutOverFunds);
				startTime = System.currentTimeMillis();
				stmt.execute();
				results = (ResultSet) stmt.getObject(5);
				results.setFetchSize(1000);
				endTime = System.currentTimeMillis();
				Util.log("Time taken for Query Execution: " + (endTime - startTime)	/ 1000 + " seconds");
				startTime = System.currentTimeMillis();			
							
				while (results.next()) {
					Long PORT_FUND_N = results.getLong("PORT_FUND_N");										
					if(PORT_FUND_N != null){
						cutOverportFundSet.add(PORT_FUND_N.toString());															
					}
				}
			}catch(Exception e){
				Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
						"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				throw e;
			}
			finally{
				if(stmt != null)
					stmt.close();
				if(results != null)
					results.close();			
				}			
			return cutOverportFundSet;
		}
		
		//Construct Matured Taxlots for cutover funds.
		private void gatherDataforMaturedTaxlots(String srcCode) throws Exception{			
			String srcCode1 = "";
			String queryforMatCase1 = "BEGIN PKG_FEED_BODTAXLOTFEED.MATURED_TAX_LOT_CASE1(?,?,?,?,?,?,?,?); END;";
			String queryforCutOverMatCase1 = "BEGIN PKG_FEED_BODTAXLOTFEED.MAT_TAXLOT_CUTOVER_CASE1(?,?,?,?,?,?,?,?); END;";
			String queryforMatCase2 = "BEGIN PKG_FEED_BODTAXLOTFEED.MATURED_TAX_LOT_CASE2(?,?,?,?,?,?); END;";
			String queryforCutOverMatCase2 = "BEGIN PKG_FEED_BODTAXLOTFEED.MAT_TAXLOT_FOR_CUTOVER_CASE2(?,?,?,?,?,?); END;";
			String queryforAllMaturity = "BEGIN PKG_FEED_BODTAXLOTFEED.GET_ALL_MATURED(?,?,?,?,?,?); END;";
			String strAsOfDate = asOfDay.toString(dft1);
			Date nxtBusnDate = dalCalendar.getNextBusinessDay(asOfDate, srcCode.contains("FIE")?	SourceCalendar.IOFIE.id:SourceCalendar.IOMM.id);
			DateTime nxtBusnDay = new DateTime(nxtBusnDate);
			String strNxtBusnDay = nxtBusnDay.toString(dft1);
			HashSet<String> cutOverportFundSet = getCutOverFunds(strAsOfDate,srcCode);
			
			if(!cutOverportFundSet.isEmpty()){
				Util.log("Enter cutover funds methods");
				srcCode1 = getSrcCodeForFOCAS(srcCode);
				GetDataForFirstCaseMatTaxlots(strAsOfDate,strNxtBusnDay, srcCode, srcCode1,queryforCutOverMatCase1, true);				
			}
			GetDataForFirstCaseMatTaxlots(strAsOfDate,strNxtBusnDay,srcCode, srcCode, queryforMatCase1, false);
			//moving 2nd case cutover matured txlts outside the if statement since we can get these on non-cutover days
			populateSecondCaseMatTaxlots(srcCode,strAsOfDate,strNxtBusnDay,queryforCutOverMatCase2);  // This will get data from view: HDGTRX_DLTD_SET_BY_FUND_N_VW 
			populateSecondCaseMatTaxlots(srcCode,strAsOfDate,strNxtBusnDay,queryforMatCase2);
			getAllMatTaxlots(srcCode,strAsOfDate,strNxtBusnDay,queryforAllMaturity);
		}

		private String getSrcCodeForFOCAS(String srcCode) {
			String focasSrcCode = "";
			if(srcCode.contains("FIE")){
				focasSrcCode = Constants.FUND_TYPE_FOCAS_FIE;
			}else if(srcCode.contains("MM")){
				focasSrcCode = Constants.FUND_TYPE_FOCAS_MM;
			}
			return focasSrcCode;
		}
		
		//This method constructs Matured taxlots, from the first case where Prior day Taxlots have MATU, SMATU in current day transactions 
		private void GetDataForFirstCaseMatTaxlots(String strAsOfDate,String strNxtBusnDay,String srcCode, String srcCode1,String query, boolean cutOverFlag) throws Exception{
			CallableStatement stmt = null;
			ResultSet results = null;
			String strDateCase1 = "";
			long startTime, endTime = 0;
			//Date trdDate = null;
			maturedTaxlotKey = new HashSet<String>();
			matTaxlotKeys = new HashSet<String>();
			try{
				//trdDate = dalCalendar.getNextBusinessDay(asOfDate, srcCode.contains("FIE")?	SourceCalendar.IOFIE.id:SourceCalendar.IOMM.id);
				//DateTime trdDateDT = new DateTime(trdDate);
				//String strtrdDate = trdDateDT.toString(dft1);				
				DateTime EODAsOfDay = new DateTime(todayDate);			
				Date prEODAsOfDay = dalCalendar.getPriorBusinessDay(todayDate,dfc.getAppName().contains("FIE")?SourceCalendar.IOFIE.id:SourceCalendar.IOMM.id);
				String strEODAsOfDay = EODAsOfDay.toString(dft1);
				DateTime prBODAsOfDay = new DateTime(dalCalendar.getNextCalendarDay(prEODAsOfDay));
				String strPriorBODAsOfDay = prBODAsOfDay.toString(dft1);
				if(cutOverFlag){
					strDateCase1 = strPriorBODAsOfDay;
				}else{
					strDateCase1 = strEODAsOfDay;
				}										
				Util.log("GetDataForFirstCaseMatTaxlots,strtrdDate " + strDateCase1 + strNxtBusnDay + strAsOfDate );
				int i = 1;	
				stmt = conn.prepareCall(query);
				stmt.setString(i++, strDateCase1);
				stmt.setString(i++, strNxtBusnDay);
				stmt.setString(i++, srcCode);
				stmt.setString(i++, exttCode);				
				stmt.setString(i++, bookView);
				stmt.setString(i++, srcCode1);
				stmt.setString(i++, strAsOfDate);
				stmt.registerOutParameter(i++, OracleTypes.CURSOR);
				stmt.setFetchSize(50000);
				
				Util.log("queryforMatTaxlots is : " + query);
				Util.log(String.format("Parameters for queryforMatTaxlots: 1:%s, 2:%s, 3:%s, 4:%s, 5:%s, 6:%s, 7:%s",
						strDateCase1, strNxtBusnDay, srcCode, exttCode, bookView, srcCode1, strAsOfDate));
				startTime = System.currentTimeMillis();
				stmt.execute();
				results = (ResultSet) stmt.getObject(--i);
				results.setFetchSize(50000);
				endTime = System.currentTimeMillis();
				Util.log("Time taken for Query Execution: " + (endTime - startTime)	/ 1000 + " seconds");
				startTime = System.currentTimeMillis();				
				HashSet<String> portFundSet = new HashSet<String>();
				HashSet<String> tlMemoSet = new HashSet<String>();
				HashSet<String> fmrCuspSet = new HashSet<String>();
				while (results.next()) {			
					Long PORT_FUND_N = results.getLong("PORT_FUND_N");
					String TL_MEMO_N = results.getString("TL_MEMO_N");
					String FMR_CUSP_N = results.getString("FMR_CUSP_N");
					if(PORT_FUND_N != null && TL_MEMO_N != null && FMR_CUSP_N != null){
						portFundSet.add(PORT_FUND_N.toString());						
						tlMemoSet.add(TL_MEMO_N);
						fmrCuspSet.add(FMR_CUSP_N);
						maturedTaxlotKey.add(getMaturedTaxlotKey(PORT_FUND_N, TL_MEMO_N, FMR_CUSP_N));						
					}
				}
				matTaxlotKeys.addAll(maturedTaxlotKey);
				endTime = System.currentTimeMillis();
				Util.log("Time taken for populating objects from the query: "+ (endTime - startTime) / 1000 + " seconds");	
				
				ArrayList<String> portFundList = new ArrayList<String>(portFundSet);
				ArrayList<String> tlMemoList = new ArrayList<String>(tlMemoSet);
				ArrayList<String> fmrCuspList = new ArrayList<String>(fmrCuspSet);
				
				String[] portFundArray = portFundList.toArray(new String[portFundList.size()]);				
				String[] fmrCuspArray = fmrCuspList.toArray(new String[fmrCuspList.size()]);
				String[] tlMemoArray = tlMemoList.toArray(new String[tlMemoList.size()]);
				
				if(cutOverFlag && portFundList != null && !portFundList.isEmpty() &&  fmrCuspList != null && !fmrCuspList.isEmpty()){
					populateCase1MatFromFocas(portFundArray, fmrCuspArray, srcCode, strPriorBODAsOfDay);
				}
				if(!cutOverFlag && portFundList != null && !portFundList.isEmpty() &&  fmrCuspList != null && !fmrCuspList.isEmpty() 
						&& tlMemoList != null && !tlMemoList.isEmpty()){			
					setPositionData(bookView, exttCode, srcCode,strAsOfDate);
					populateFirstCaseMatTaxlots(srcCode, portFundArray, tlMemoArray,fmrCuspArray,prEODAsOfDay, strEODAsOfDay,strAsOfDate);		
				}
	
		}catch(Exception e){
			Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
																		"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
			throw e;
		}
		finally{
			 if(stmt != null)
				 stmt.close();
			 if(results != null)
				 results.close();			
		 }		
	}	
		
		private void populateCase1MatFromFocas(String[] portFundArray, String[] fmrCuspArray, String srcCode, String strPrBODAsOfDate) throws Exception{
			CallableStatement stmt = null;
			ResultSet results = null;
			int recordCount = 0;
			try{
				
				String focasSrcCode = getSrcCodeForFOCAS(srcCode);
				Util.log("populateCase1MatFromFocas: strPrBODAsOfDate " +  strPrBODAsOfDate );
				String query = "BEGIN PKG_FEED_BODTAXLOTFEED.MATURED_FROM_FOCAS(?,?,?,?,?,?,?,?); END;";
				
				stmt = conn.prepareCall(query);
				stmt.setString(1, exttCode);
				stmt.setString(2, focasSrcCode);
				stmt.setString(3, bookView);
				stmt.setString(4, strPrBODAsOfDate);
				stmt.setString(5, taxView);
				stmt.setString(6, FeedsUtil.arrayToString(portFundArray));		
				stmt.setString(7, FeedsUtil.arrayToString(fmrCuspArray));	
												
				stmt.registerOutParameter(8, OracleTypes.CURSOR); //REF CURSOR
				stmt.setFetchSize(50000);
				Util.log("Query in populateCase1MatFromFocas is : " + query);
				long startTime = System.currentTimeMillis();
				stmt.execute();
				results = (ResultSet) stmt.getObject(8);
				results.setFetchSize(50000);
				long endTime = System.currentTimeMillis();
				Util.log("Time taken for Query Execution: " + (endTime - startTime)	/ 1000 + " seconds");
				startTime = System.currentTimeMillis();
		  	    while (results.next()) {
				 	TaxlotData taxlotFOCASData = new TaxlotData();			 			
				 	taxlotFOCASData = setTaxlotForFOCAS(results, taxlotFOCASData,true);
				 	if(taxlotFOCASData != null ){
						recordCount = writeContentforFOCAS(taxlotFOCASData ,recordCount);
					}
					taxlotFOCASData = null;
				}
		  	  	fw.flushRecordBuffer();		
		  	  	fwGeode.flushRecordBuffer();
		  	  	endTime = System.currentTimeMillis();
		  	  	Util.log("Time taken for populating objects from the query: "+(endTime-startTime)/1000 + " seconds");							
			}catch(Exception e){
				Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
																			"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				throw e;
			}
			finally{
				if(stmt != null)
				 stmt.close();
				if(results != null)
				 results.close();
			 }				
		}

		private String getMaturedTaxlotKey(Long PORT_FUND_N, String TL_MEMO_N,
				String FMR_CUSP_N) {
			StringBuilder sb = new StringBuilder("");			
			if(PORT_FUND_N != null && TL_MEMO_N != null && FMR_CUSP_N != null){
				sb.append(PORT_FUND_N.toString());
				sb.append(TL_MEMO_N);
				sb.append(FMR_CUSP_N);
			}
			
			return sb.toString();
		}

		private void populateFirstCaseMatTaxlots(String srcCode, String[] portFundArray,String[] tlMemoArray, String[] fmrCuspArray, Date prEODAsOfDay,
				String strEODAsOfDay,String strAsOfDate)
				throws SQLException, Exception {
		CallableStatement stmt = null;
		ResultSet results1 = null;
		int recordCount = 0;
		
		try{				
			long startTime;
			long endTime;
			String exttCodeForEOD = "EOD";
			String taxlotQueryforMatIO = "BEGIN PKG_FEED_BODTAXLOTFEED.MATURED_GET_IO(?,?,?,?,?,?,?,?,?,?); END;";
			DateTime EODAsOfDay = new DateTime(prEODAsOfDay);			
			String strPriorEODAsOfDay = EODAsOfDay.toString(dft1);		
			
			stmt = conn.prepareCall(taxlotQueryforMatIO);
			stmt.setString(1, taxView);
			stmt.setString(2, srcCode);
			stmt.setString(3, bookView);
			stmt.setString(4, exttCodeForEOD);
			stmt.setString(5, strEODAsOfDay);
			stmt.setString(6, strPriorEODAsOfDay);			
			stmt.setArray(7, DBUtil.createOracleArray(USR_STRG_KEY_ARRAY, portFundArray));		
			stmt.setArray(8, DBUtil.createOracleArray(USR_STRG_KEY_ARRAY, fmrCuspArray));	
			stmt.setArray(9, DBUtil.createOracleArray(USR_STRG_KEY_ARRAY, tlMemoArray));			
			
			stmt.registerOutParameter(10, OracleTypes.CURSOR); //REF CURSOR
			stmt.setFetchSize(50000);	
		//	results = (ResultSet) stmt.getObject(7);
			
			Util.log("taxlotQueryforMatIO Query is : " + taxlotQueryforMatIO);
			Util.log(String.format("Parameters for taxlotQueryforMatIO Query are 1:%s, 2:%s, 3:%s, 4:%s, 5:%s, 6:%s, 7:%s, 8:%s, 9:%s ",
					taxView, srcCode, bookView, exttCodeForEOD, strEODAsOfDay, strPriorEODAsOfDay,
					FeedsUtil.arrayToString(portFundArray),FeedsUtil.arrayToString(fmrCuspArray),FeedsUtil.arrayToString(tlMemoArray)
					));
			startTime = System.currentTimeMillis();
			stmt.execute();
			results1 = (ResultSet) stmt.getObject(10);
			results1.setFetchSize(50000);
			endTime = System.currentTimeMillis();
			Util.log("Time taken for Query Execution: " + (endTime - startTime)	/ 1000 + " seconds");
			tempHashMap = new HashMap<String,Boolean>();  //FACDAL 2131
			startTime = System.currentTimeMillis();				
			if(results1 != null){					
				//setPriorDayAmortValues(bookView,srcCode,prEODAsOfDay);
				setSecRefValues(srcCode, strEODAsOfDay, bookView,exttCodeForEOD);
				//setAS400IDFromTXLTLookUp(bookView, exttCodeForEOD, srcCode,strEODAsOfDay);					
				setIntSoldAmtBaseFromTrans(bookView, exttCode, srcCode,strAsOfDate);	// FACDAL - 2474.Need to get BOD current day data , if we are using from transactions.
				setIntPayFromTrans(bookView, exttCode, srcCode,strAsOfDate);
				
			}
			while (results1.next()) {
				TaxlotData taxlotIOData = new TaxlotData();
				taxlotIOData = setTaxlotForIO(results1, taxlotIOData,true);
					if (taxlotIOData != null) {					
						boolean dummyCusipRow = ApplicationContext.getFocasDataConverter().isDummyCusip(taxlotIOData.getCUSIPNumber());
							if (!dummyCusipRow) {
								tempMatHashMap.put(String.valueOf(taxlotIOData.getFundNumber())+String.valueOf(taxlotIOData.getPortfolioFundNumber())+
										taxlotIOData.getTaxLotMemoNumber()+taxlotIOData.getFMRCUSIPNumber(),true);	
								Long PORT_FUND_N = taxlotIOData.getPortfolioFundNumber();
								String TL_MEMO_N = taxlotIOData.getTaxLotMemoNumber();
								String FMR_CUSP_N = taxlotIOData.getFMRCUSIPNumber();
								String key = getMaturedTaxlotKey(PORT_FUND_N, TL_MEMO_N, FMR_CUSP_N);
								if(maturedTaxlotKey.contains(key)) {
									recordCount = writeContentforIO(taxlotIOData,recordCount);
									maturedTaxlotKey.remove(key);
							}
								
						}
					taxlotIOData = null;						
				}
			}
			fw.flushRecordBuffer();
			fwGeode.flushRecordBuffer();
		
		}catch(Exception e){
			Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
					"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
			throw e;
			}
			finally{			
				if(stmt != null)
					stmt.close();
				if(results1 != null)
					results1.close();
			}		
	}	
		
		/* case 2 - overnight Maturities, which are constructed from Transactions */
		private void populateSecondCaseMatTaxlots(String srcCode, String strAsOfDate,String strNxtBusnDay,String query) throws SQLException, Exception {
			CallableStatement stmt1 = null;
			ResultSet results1 = null;
			int recordCount = 0;
			try{				
				long startTime;
				long endTime;
				
				int ii = 1;	
				stmt1 = conn.prepareCall(query);
				stmt1.setString(ii++, strAsOfDate);
				stmt1.setString(ii++, strNxtBusnDay);
				stmt1.setString(ii++, srcCode);
				stmt1.setString(ii++, exttCode);
				stmt1.setString(ii++, bookView);
				stmt1.registerOutParameter(ii++, OracleTypes.CURSOR);
				stmt1.setFetchSize(50000);
				
				Util.log("queryforMatIOSecondCase Query is : " + query);
				startTime = System.currentTimeMillis();
				stmt1.execute();
				results1 = (ResultSet) stmt1.getObject(--ii);
				results1.setFetchSize(50000);
				endTime = System.currentTimeMillis();
				Util.log("Time taken for Query Execution: " + (endTime - startTime)	/ 1000 + " seconds");
				//tempMatHashMap = new HashMap<String,Boolean>();  //FACDAL 2131			
				while (results1.next()) {
					TaxlotData taxlotMatIOData = new TaxlotData();
					taxlotMatIOData = setMaturedTaxlotForIO(results1, taxlotMatIOData);
						if (taxlotMatIOData != null) {
							if (!validateDuplicationForMatured(taxlotMatIOData)) {
								boolean dummyCusipRow = ApplicationContext.getFocasDataConverter().isDummyCusip(taxlotMatIOData.getCUSIPNumber());
								if (!dummyCusipRow) {	
									Long PORT_FUND_N = taxlotMatIOData.getPortfolioFundNumber();
									String TL_MEMO_N = taxlotMatIOData.getTaxLotMemoNumber();
									String FMR_CUSP_N = taxlotMatIOData.getCUSIPNumber();									
									matTaxlotKeys.add(getMaturedTaxlotKey(PORT_FUND_N, TL_MEMO_N, FMR_CUSP_N));
									recordCount = writeContentforIO(taxlotMatIOData,recordCount);													
								}
							}
							taxlotMatIOData = null;
						}
				}				
				fw.flushRecordBuffer();
				fwGeode.flushRecordBuffer();
			}catch(Exception e){
				Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
						"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				throw e;
				}
				finally{			
					if(stmt1 != null)
						stmt1.close();
					if(results1 != null)
						results1.close();
				}		
		}
		
		/* To determine, if we have any case 3 Maturities, missed from case 1 and case 2: which account for partially washed lots */
		private void getAllMatTaxlots(String srcCode, String strAsOfDate,String strNxtBusnDay,String query) throws SQLException, Exception {
			CallableStatement stmt1 = null;
			ResultSet results1 = null;
			rlvdCarrMap = new HashMap<String, Double>();
			HashSet<String> portFundSet = new HashSet<String>();
			HashSet<String> tlMemoSet = new HashSet<String>();
			HashSet<String> fmrCuspSet = new HashSet<String>();
			try{				
				long startTime;
				long endTime;
				
				int ii = 1;	
				stmt1 = conn.prepareCall(query);
				stmt1.setString(ii++, strAsOfDate);
				stmt1.setString(ii++, strNxtBusnDay);
				stmt1.setString(ii++, srcCode);
				stmt1.setString(ii++, exttCode);
				stmt1.setString(ii++, bookView);
				stmt1.registerOutParameter(ii++, OracleTypes.CURSOR);
				stmt1.setFetchSize(50000);
				
				Util.log("getAllMatTaxlots Query is : " + query + "stmt1 is: " + stmt1);
				startTime = System.currentTimeMillis();
				stmt1.execute();
				results1 = (ResultSet) stmt1.getObject(--ii);
				results1.setFetchSize(50000);
				endTime = System.currentTimeMillis();
				Util.log("Time taken for Query Execution: " + (endTime - startTime)	/ 1000 + " seconds");
				//tempMatHashMap = new HashMap<String,Boolean>();  //FACDAL 2131			
				while (results1.next()) {					
					Long PORT_FUND_N = results1.getLong("PORT_FUND_N");
					String TL_MEMO_N = results1.getString("TL_MEMO_N");
					String FMR_CUSP_N = results1.getString("FMR_CUSP_N");
					String TXN_C = results1.getString("TXN_C");
				    String TRX_CL = results1.getString("TRX_CL");
					Double RLVD_CARR = results1.getDouble("RLVD_CARR");				
					String key = getMaturedTaxlotKey(PORT_FUND_N, TL_MEMO_N, FMR_CUSP_N);
					SellLikeTransactions sellTxn = SellLikeTransactions.getSellLikeTransaction(TXN_C,TRX_CL);
					if(!matTaxlotKeys.contains(key)) {
								if(sellTxn != null || TXN_C.equals("WSADJ-") || TXN_C.equals("MATU") || TXN_C.equals("SMATU") ){
									if(!rlvdCarrMap.isEmpty() && rlvdCarrMap.containsKey(key)){
										Double tempRLVDCARR = rlvdCarrMap.get(key);
										rlvdCarrMap.put(key, (RLVD_CARR + tempRLVDCARR));
									}else{
										rlvdCarrMap.put(key, RLVD_CARR);
										portFundSet.add(PORT_FUND_N.toString());						
										tlMemoSet.add(TL_MEMO_N);
										fmrCuspSet.add(FMR_CUSP_N);
									}
								}						
							}																
						}
					ArrayList<String> portFundList = new ArrayList<String>(portFundSet);
					ArrayList<String> tlMemoList = new ArrayList<String>(tlMemoSet);
					ArrayList<String> fmrCuspList = new ArrayList<String>(fmrCuspSet);
					
					String[] portFundArray = portFundList.toArray(new String[portFundList.size()]);				
					String[] fmrCuspArray = fmrCuspList.toArray(new String[fmrCuspList.size()]);
					String[] tlMemoArray = tlMemoList.toArray(new String[tlMemoList.size()]);
					
					if(portFundList != null && !portFundList.isEmpty() &&  fmrCuspList != null && !fmrCuspList.isEmpty() && tlMemoList != null && !tlMemoList.isEmpty()){
						populateCase3Maturities(portFundArray, fmrCuspArray, tlMemoArray, strAsOfDate, strNxtBusnDay,srcCode);
					}
				}catch(Exception e){
				Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
						"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				throw e;
				}
				finally{			
					if(stmt1 != null)
						stmt1.close();
					if(results1 != null)
						results1.close();
				}		
		}

		/* construct case 3 maturities from priod day EOD Taxlot and Transactions.*/
		private void populateCase3Maturities(String[] portFundArray, String[] fmrCuspArray, String[] tlMemoArray,String strAsOfDate, String strNxtBusnDay,String srcCode) throws Exception{
			CallableStatement stmt = null;
			ResultSet results = null;
			int recordCount = 0;
			try{			
				Util.log("populateCase3Maturities:  "  );
				DateTime EODAsOfDay = new DateTime(todayDate);			
				String strEODAsOfDay = EODAsOfDay.toString(dft1);
				String query = "BEGIN PKG_FEED_BODTAXLOTFEED.MATURED_TAXLOT_CASE3(?,?,?,?,?,?,?,?,?); END;";
				
				stmt = conn.prepareCall(query);
				stmt.setString(1, exttCode);
				stmt.setString(2, srcCode);
				stmt.setString(3, bookView);
				stmt.setString(4, strAsOfDate);
				stmt.setString(5, strEODAsOfDay);	
				stmt.setString(6, FeedsUtil.arrayToString(portFundArray));
				stmt.setString(7, FeedsUtil.arrayToString(fmrCuspArray));
				stmt.setString(8, FeedsUtil.arrayToString(tlMemoArray));
								
				stmt.registerOutParameter(9, OracleTypes.CURSOR); //REF CURSOR
				stmt.setFetchSize(50000);
				Util.log("Query in populateCase3Maturities is : " + query);
				long startTime = System.currentTimeMillis();
				stmt.execute();
				results = (ResultSet) stmt.getObject(9);
				results.setFetchSize(50000);
				long endTime = System.currentTimeMillis();
				Util.log("Time taken for Query Execution: " + (endTime - startTime)	/ 1000 + " seconds");
				startTime = System.currentTimeMillis();
		  	    while (results.next()) {
		  	    	Long PORT_FUND_N = results.getLong("PORT_FUND_N");
					String TL_MEMO_N = results.getString("TL_MEMO_N");
					String FMR_CUSP_N = results.getString("FMR_CUSP_N");
					Double SHR_PAR_Q = results.getDouble("SHR_PAR_Q");
					String key = getMaturedTaxlotKey(PORT_FUND_N, TL_MEMO_N, FMR_CUSP_N);
					if(!rlvdCarrMap.isEmpty() && rlvdCarrMap.containsKey(key)){
						Double SUM_RLVD_CARR = rlvdCarrMap.get(key);
						if(SHR_PAR_Q.doubleValue() == SUM_RLVD_CARR.doubleValue()){
							TaxlotData taxlotMat3Data = new TaxlotData();			 
						 	taxlotMat3Data = setMatTaxlotForIOForCase3(results, taxlotMat3Data);
						 	if(taxlotMat3Data != null ){
								recordCount = writeContentforIO(taxlotMat3Data ,recordCount);
							}
						 	taxlotMat3Data = null;
						}
					}				 	
				}
		  	  	fw.flushRecordBuffer();	
		  	  	fwGeode.flushRecordBuffer();
		  	  	endTime = System.currentTimeMillis();
		  	  	Util.log("Time taken for populating objects from the query: "+(endTime-startTime)/1000 + " seconds");							
			}catch(Exception e){
				Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
																			"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				throw e;
			}
			finally{
				if(stmt != null)
				 stmt.close();
				if(results != null)
				 results.close();
			 }				
		}
		
		/* Get Lookup data for FOCFIE for cutover funds as per FACDAL - 3259.*/
		/*private void getLookupDataForFocas(String srcCode) throws Exception{
			CallableStatement stmt = null;
			ResultSet results = null;
			lkupPrncMap = new HashMap<String, Double>();
			lkupBaseCostMap = new HashMap<String, Double>();
			try{			
				Util.log("getLookupDataForFocas:  "  );
				String query = "BEGIN PKG_FEED_BODTAXLOTFEED.LOOKUP_FOR_CUTOVER(?,?,?,?); END;";
				
				stmt = conn.prepareCall(query);
				stmt.setString(1, srcCode);
				stmt.setString(2, exttCode);
				stmt.setString(3, bookView);
												
				stmt.registerOutParameter(4, OracleTypes.CURSOR); //REF CURSOR
				stmt.setFetchSize(50000);
				Util.log("Query in getLookupDataForFocas : " + query);
				long startTime = System.currentTimeMillis();
				stmt.execute();
				results = (ResultSet) stmt.getObject(4);
				results.setFetchSize(50000);
				long endTime = System.currentTimeMillis();
				Util.log("Time taken for Query Execution: " + (endTime - startTime)	/ 1000 + " seconds");
				startTime = System.currentTimeMillis();
		  	    while (results.next()) {
		  	    	Long PORT_FUND_N = results.getLong("PORT_FUND_N");
					String TXN_MEMO_N = results.getString("TXN_MEMO_N");
					String CUSP_N = results.getString("CUSP_N");
					if(PORT_FUND_N != null && TXN_MEMO_N != null && CUSP_N != null){
						String key = PORT_FUND_N.toString() + TXN_MEMO_N + CUSP_N;
						Double PRNC_A = results.getDouble("PRNC_A");
						Double 	LKUP_BASE_COST = results.getDouble("LKUP_BASE_COST");
						lkupPrncMap.put(key, PRNC_A);
						lkupBaseCostMap.put(key, LKUP_BASE_COST);
						}
		  	    	}				 	
				endTime = System.currentTimeMillis();
		  	  	Util.log("Time taken for populating objects from getLookupDataForFocas: "+(endTime-startTime)/1000 + " seconds");							
			}catch(Exception e){
				Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
																			"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				throw e;
			}
			finally{
				if(stmt != null)
				 stmt.close();
				if(results != null)
				 results.close();
			 }				
		}*/
		
		private boolean dateMinusOne(Date matuDate, Date buyDate) throws Exception{

			boolean result = false;
			int sourceCode = SourceCalendar.IOMM.id;
			if(StringUtils.contains(dfc.getSourceCode(),"FIE")){
				sourceCode = SourceCalendar.IOFIE.id;
			}
			Date priorBusnDay = dalCalendar.getPriorBusinessDay(matuDate, sourceCode);
			if(FeedsUtil.daysDiff(priorBusnDay, buyDate) == 0){
				result = true;
			}
			return result;
		}
		
		
		private TaxlotData setTaxlotForIO(ResultSet results, TaxlotData taxlotIORow, boolean isMatured) throws SQLException {
			
		subAcctCodeIO = "00";	
		String priceCode = results.getString("POS_PRC_C");	
		String SEC_CL_TY_C = results.getString("SEC_CL_TY_C");
		Double UNIT_OF_CALC = 0.0;
		Double INV1_SCTY_QUAL_N = results.getDouble("INV1_SCTY_QUAL_N");
		Double SEC_IO_CTG_C_LV_SEG = results.getDouble("SEC_IO_CTG_C_LV_SEG");
		Double POS_UNIT_OF_CALC = results.getDouble("POS_UNIT_OF_CALC");
		Double CTRT_SIZE = results.getDouble("CTRT_SIZE");
		if(SEC_CL_TY_C != null && SEC_CL_TY_C.equals("O") && priceCode != null && priceCode.equals("D")) {
			UNIT_OF_CALC = CTRT_SIZE; //FACDAL - 3274
		}
		else if(SEC_CL_TY_C != null && SEC_CL_TY_C.equals("FT") && SEC_IO_CTG_C_LV_SEG != null && SEC_IO_CTG_C_LV_SEG.doubleValue() != 60) {			
			UNIT_OF_CALC = CTRT_SIZE * POS_UNIT_OF_CALC;  // FACDAL - 2989.
		} else {
			UNIT_OF_CALC = POS_UNIT_OF_CALC;
		}
		taxlotIORow.setUnitOfCalculation(UNIT_OF_CALC);
		Double TXLT_SHR_PAR_Q = results.getDouble("SHR_PAR_Q");
		Long PORT_FUND_N = results.getLong("PORT_FUND_N");
		String TL_MEMO_N = results.getString("TL_MEMO_N");
		String TXLT_DS_1 = results.getString("TXLT_DS_1");
		String TXLT_DS_2 = results.getString("TXLT_DS_2");
		String tradingApplicationTradeId = null;
		//FACDAL- 2516. AS400 ID			
		if((TXLT_DS_1 != null && TXLT_DS_2 != null && TXLT_DS_1.length() == 16  && 
				TXLT_DS_1.substring(0,8).matches("[0-9]+")) && TXLT_DS_2.length() > 2 && !TXLT_DS_2.contains(" ")) {
			String tempDate = TXLT_DS_1.substring(0,8);
			if(isValidDate(tempDate)){				
				tradingApplicationTradeId = TXLT_DS_2;		
				}
			} else{			
				tradingApplicationTradeId = results.getString("LKUP_TRD_APPL_TRD_ID");
			}
		taxlotIORow.setTradingApplicationTradeId(tradingApplicationTradeId);
		taxlotIORow.setBeginFiscalYearCumulativeAmortizationAmount(results.getDouble("TXLT_AMRT_CUR_ACRU_A"));
		Long FUND_N = results.getLong("FUND_N");		
		String LNG_SHRT_C = results.getString("LNG_SHRT_C");		
		Double PR_DAY_CUM_AMRT = 0.0;
		Double POS_IO_FUND_TY_ADJ_ERND_INCM  = results.getDouble("POS_IO_FUND_TY_ADJ_ERND_INCM");
		Double POS_CURR_EXCH_R = results.getDouble("POS_CURR_EXCH_R");
		PR_DAY_CUM_AMRT = results.getDouble("PRIOR_AMRT_BS_A");
		taxlotIORow.setPriorDayCumulativeAmort(PR_DAY_CUM_AMRT);
		//FACDAL - 2571. INT-PAYMENT
		Double INCM_A_SUM = 0.0;;
		Double INT_PAYMENT = 0.0;
		Double shrParQTotal = 0.0;
		String FMR_CUSP_N = results.getString("FMR_CUSP_N");
		Double POS_SHR_Q = results.getDouble("POS_SHR_Q");
		Double usIntSoldForCalc = 0.0;
		Double denIntSoldForCalc = 0.0;
		//FACDAL - 2136 FOR DEN_ACCR_DAILY
		Double IO_DLY_INT_ACRU_A = 0.0;
		Double accrDailyMatMapValue = 0.0;
		//FACDAL - 2617. US_ACCR_DAILY
		Double US_ACCR_DAILY = 0.0;	
		// FACDAL - 2146. INT-NORMAL-ACCR-DAYS
		Long ACRU_DAY_CNT = new Long(0);
		Timestamp ORGNL_SETL_D = results.getTimestamp("ORGNL_SETL_D");
		DateTime ORGNL_SETL_D_DT = ORGNL_SETL_D != null ? new DateTime(ORGNL_SETL_D):null;	
		DateTime AS_OF_D_DT = results.getTimestamp("AS_OF_D") != null ? new DateTime(results.getTimestamp("AS_OF_D")):null;
		POS_MATU_POSN_I = results.getString("POS_MATU_POSN_I");
		if(isMatured){
			if(PORT_FUND_N != null && FMR_CUSP_N != null && TL_MEMO_N != null){
				String key = PORT_FUND_N.toString() + FMR_CUSP_N.toString() + TL_MEMO_N.trim();
				if(intPayTransMap != null && intPayTransMap.containsKey(key)){
					INT_PAYMENT = intPayTransMap.get(key);
				}
				if(accrDailyMatMap !=  null && accrDailyMatMap.containsKey(key)){
					accrDailyMatMapValue = accrDailyMatMap.get(key);					
				}
			}
			if(!positionMap.isEmpty()){
				String posKey = PORT_FUND_N + FMR_CUSP_N;
				Double CURR_POS_IO_FUND_TY_ADJ_ERND_INCM = positionMap.get(posKey);
				if(accrDailyMatMapValue != null && CURR_POS_IO_FUND_TY_ADJ_ERND_INCM != null){
					IO_DLY_INT_ACRU_A = CURR_POS_IO_FUND_TY_ADJ_ERND_INCM * accrDailyMatMapValue;			
				}
			}
			//FACDAL - 2638
			if(POS_CURR_EXCH_R != null && POS_CURR_EXCH_R != 0){
				US_ACCR_DAILY = IO_DLY_INT_ACRU_A / POS_CURR_EXCH_R;
			}
			else{
				String posKey = PORT_FUND_N + FMR_CUSP_N;
				Double CURR_POS_CURR_EXCH_R = positionMapForCurrExch.get(posKey);
				if(CURR_POS_CURR_EXCH_R != null && CURR_POS_CURR_EXCH_R != 0)
					US_ACCR_DAILY = IO_DLY_INT_ACRU_A / CURR_POS_CURR_EXCH_R;
			}	
			POS_NEW_BUY_I ="N";
			taxlotIORow.setFocasMaturedPositionFlag("Y");			
			//FACDAL - 2638
			if(FUND_N != null && PORT_FUND_N != null && FMR_CUSP_N != null && TL_MEMO_N != null){
				String key = PORT_FUND_N.toString() + FMR_CUSP_N.toString() + TL_MEMO_N;
				String fundKey = FUND_N.toString();	
				Timestamp CONT_STLM_D = null;
				if(!contStlmDateMap.isEmpty())
					CONT_STLM_D = contStlmDateMap.get(key);
				DateTime CONT_STLM_D_DT = CONT_STLM_D != null ? new DateTime(CONT_STLM_D):null;
				if(!accrualDaysFundPortMap.isEmpty() && accrualDaysFundPortMap.containsKey(fundKey) 
							&& !priordayAccrDaysFundPortMap.isEmpty() && priordayAccrDaysFundPortMap.containsKey(fundKey)){
						DateTime ACRU_THR_D = accrualDaysFundPortMap.get(fundKey);
						DateTime PRIORDAY_ACRU_THR_D = priordayAccrDaysFundPortMap.get(fundKey);
						
						if((ACRU_THR_D != null && invalidDate.toDateMidnight().isEqual(ACRU_THR_D.toDateMidnight())) ||
								(PRIORDAY_ACRU_THR_D != null && invalidDate.toDateMidnight().isEqual(PRIORDAY_ACRU_THR_D.toDateMidnight()))){
					    	ACRU_DAY_CNT = new Long(1);
					    }						
						else if(CONT_STLM_D_DT != null && !CONT_STLM_D_DT.isAfter(ACRU_THR_D)){
							CONT_STLM_D_DT = CONT_STLM_D_DT.minusDays(1);
							ACRU_DAY_CNT = new Long(Math.abs(Days.daysBetween(CONT_STLM_D_DT,PRIORDAY_ACRU_THR_D).getDays()));;					
						}
						else {
							ACRU_DAY_CNT = new Long(0);					
							}					
					}				
			}
			taxlotIORow.setLocalCurrentPayPeriodNetInterestAccruedAmount(INT_PAYMENT); // FACDAL - 2622. DEN-ACCR-CURR-PERIOD
			
			Double US_ACCR_CURR_PERIOD = 0.0;
			if(PORT_FUND_N != null && FMR_CUSP_N != null && TL_MEMO_N != null){
				String key = PORT_FUND_N.toString() + FMR_CUSP_N.toString() + TL_MEMO_N;
				if(usAccrCurrTransMap != null && usAccrCurrTransMap.containsKey(key)){
					US_ACCR_CURR_PERIOD = usAccrCurrTransMap.get(key); // FACDAL - 2622. US-ACCR-CURR-PERIOD
				}				
			}
			taxlotIORow.setAccruedInterestAmountBase(US_ACCR_CURR_PERIOD);
		}else{
			if(ORGNL_SETL_D_DT.isAfter(AS_OF_D_DT)){   //FACDAL - 3003
				INT_PAYMENT = 0.0;
			}
			else if(PORT_FUND_N != null && FMR_CUSP_N != null){
				String key = PORT_FUND_N.toString() + FMR_CUSP_N.toString();
				if(incmASumTransMap !=  null && incmASumTransMap.containsKey(key)){
					INCM_A_SUM = incmASumTransMap.get(key); 
				}				
				if(INCM_A_SUM != 0 && shrParQSumMap != null && shrParQSumMap.containsKey(key)){
					shrParQTotal = shrParQSumMap.get(key);
					if(shrParQTotal != null && shrParQTotal != 0)
						INT_PAYMENT = INCM_A_SUM *  ( TXLT_SHR_PAR_Q / shrParQTotal );					
				}			
			}
			if (PORT_FUND_N != null && TL_MEMO_N != null && FMR_CUSP_N != null) {
				String key = PORT_FUND_N.toString() + FMR_CUSP_N.trim() + TL_MEMO_N.trim(); 
				if(intSoldAmtTransMap !=  null && intSoldAmtTransMap.containsKey(key)){
					denIntSoldForCalc = intSoldAmtTransMap.get(key);  
				}	
				if(intSoldAmtBaseTransMap !=  null && intSoldAmtBaseTransMap.containsKey(key)){
					usIntSoldForCalc = intSoldAmtBaseTransMap.get(key); 
				}		
			}
			if(priceCode != null && ArrayUtils.contains(prcCodes, priceCode)) {				
				if(POS_IO_FUND_TY_ADJ_ERND_INCM != null && POS_SHR_Q != null && POS_SHR_Q != 0 && TXLT_SHR_PAR_Q != null)
					IO_DLY_INT_ACRU_A = POS_IO_FUND_TY_ADJ_ERND_INCM * (TXLT_SHR_PAR_Q / POS_SHR_Q);
			 
			if(POS_IO_FUND_TY_ADJ_ERND_INCM != null && POS_SHR_Q != null && POS_SHR_Q != 0 && TXLT_SHR_PAR_Q != null &&
						POS_CURR_EXCH_R != null && POS_CURR_EXCH_R != 0){
					US_ACCR_DAILY = (POS_IO_FUND_TY_ADJ_ERND_INCM / POS_CURR_EXCH_R) * (TXLT_SHR_PAR_Q / POS_SHR_Q);				
				}
			}
			POS_NEW_BUY_I = results.getString("POS_NEW_BUY_I");
			taxlotIORow.setFocasMaturedPositionFlag(POS_MATU_POSN_I);
			
			if(priceCode != null && ArrayUtils.contains(prcCodes, priceCode)) {
				if (FUND_N != null && PORT_FUND_N != null && TL_MEMO_N != null) {
					String fundKey = FUND_N.toString();				
					if(!accrualDaysFundPortMap.isEmpty() && accrualDaysFundPortMap.containsKey(fundKey) 
							&& !priordayAccrDaysFundPortMap.isEmpty() && priordayAccrDaysFundPortMap.containsKey(fundKey)){
						DateTime ACRU_THR_D = accrualDaysFundPortMap.get(fundKey);
						DateTime PRIORDAY_ACRU_THR_D = priordayAccrDaysFundPortMap.get(fundKey);
					
					if((ACRU_THR_D != null && invalidDate.toDateMidnight().isEqual(ACRU_THR_D.toDateMidnight())) ||
							(PRIORDAY_ACRU_THR_D != null && invalidDate.toDateMidnight().isEqual(PRIORDAY_ACRU_THR_D.toDateMidnight()))){				    	
				    	ACRU_DAY_CNT = new Long(1);
				    }
					else if(ORGNL_SETL_D_DT != null && ORGNL_SETL_D_DT.isAfter(ACRU_THR_D)){
						ACRU_DAY_CNT = new Long(0);					
					}
					else if(ORGNL_SETL_D_DT != null && !ORGNL_SETL_D_DT.isAfter(ACRU_THR_D) && ORGNL_SETL_D_DT.isAfter(PRIORDAY_ACRU_THR_D)){
						ACRU_DAY_CNT = new Long(Math.abs(Days.daysBetween(ACRU_THR_D,ORGNL_SETL_D_DT).getDays()));
						ACRU_DAY_CNT = ACRU_DAY_CNT + 1;					
					}
					else {
						ACRU_DAY_CNT =  new Long(Math.abs(Days.daysBetween(ACRU_THR_D,PRIORDAY_ACRU_THR_D).getDays()));					
						}
					}				
				}
			
				taxlotIORow.setLocalCurrentPayPeriodNetInterestAccruedAmount(results.getDouble("LOCL_CUR_PAY_PRD_NETINT_ACRU_A")); // FACDAL - 2127. DEN-ACCR-CURR-PERIOD
				taxlotIORow.setAccruedInterestAmountBase(results.getDouble("ACRU_INT_BS"));		// FACDAL - 2140.US-ACCR-CURR-PERIOD	
			}

		}
		taxlotIORow.setInterestPaymentAmountLocal(INT_PAYMENT);
		taxlotIORow.setTransactionCurrencyCode(results.getString("DEN_CURR_C"));		
		taxlotIORow.setLocalInterestSoldAmount(denIntSoldForCalc.toString());		//FACDAL - 2419. DEN-INT-SOLD-FOR-CALC
		taxlotIORow.setInterestPrincipalCode(usIntSoldForCalc);  					//FACDAL - 2539. US-INT-SOLD-FOR-CALC		
		taxlotIORow.setDailyInterestAcruedAmount(IO_DLY_INT_ACRU_A);		// FACDAL - 2617.DEN_ACCR_DAILY 		
		taxlotIORow.setTaxlotCurrentNetDailyInterestAccruedAmountBase(US_ACCR_DAILY); 
		
		taxlotIORow.setFocasSecurityDescription(results.getString("SHT_NM"));
		taxlotIORow.setToBeAnnouncedIndicator(results.getString("TBA_INDICATOR"));
		SEC_NXT_CALL_PUT_PRC_A = 0.0;
		SEC_NXT_CALL_PUT_PRC_A = results.getDouble("SEC_NXT_CALL_PUT_PRC_A");
		SEC_ASET_TY_C = results.getString("SEC_ASET_TY_C");		
		POS_SB_ACC_C = results.getString("POS_SB_ACC_C");
		UNIT_MARKET_VALUE = 0.0;
		String SRC_C = results.getString("SRC_C");
		Double PRC_FROM_PRC_FILE  = results.getDouble("POS_PRC_FROM_PRC_FILE");
		Double LOCL_AMRTD_BK_COST_A = results.getDouble("LOCL_AMRTD_BK_COST_A");
		if(SRC_C.equals(Constants.FUND_TYPE_INVESTONE_FIE)){
			if(PRC_FROM_PRC_FILE != null && POS_CURR_EXCH_R != null && POS_CURR_EXCH_R != 0)
			UNIT_MARKET_VALUE = PRC_FROM_PRC_FILE / POS_CURR_EXCH_R;
			if(UNIT_MARKET_VALUE.doubleValue() == 0){
				Double CST_BS_A = results.getDouble("CST_BS_A");
				Double SHR_PAR_Q = results.getDouble("SHR_PAR_Q");
				Double forUnitMktValue = 0.0;
				if(!isMatured && StringUtils.isNotBlank(SEC_CL_TY_C) && ("IW".equals(SEC_CL_TY_C.trim()) || "TW".equals(SEC_CL_TY_C.trim()) || "DW".equals(SEC_CL_TY_C.trim()) ||
						 "CW".equals(SEC_CL_TY_C.trim())) && SHR_PAR_Q != null && SHR_PAR_Q.doubleValue() != 0){
					forUnitMktValue = (SHR_PAR_Q + Math.abs(LOCL_AMRTD_BK_COST_A)) / POS_CURR_EXCH_R;
					forUnitMktValue = forUnitMktValue / SHR_PAR_Q;  //FACDAL - 3117
				}
				else if(CST_BS_A != null && SHR_PAR_Q != null && SHR_PAR_Q.doubleValue() != 0){
					forUnitMktValue = CST_BS_A / SHR_PAR_Q;
				}
				if(POS_UNIT_OF_CALC != null && POS_UNIT_OF_CALC.doubleValue() != 0){
					UNIT_MARKET_VALUE = forUnitMktValue / POS_UNIT_OF_CALC;			// FACDAL - 2862. 
				}
			}	
		}else if(SRC_C.equals(Constants.FUND_TYPE_INVESTONE_MM)){			
			Double amrtdBookCostAmt = results.getDouble("AMRTD_BK_COST_A");
			if(amrtdBookCostAmt != null && TXLT_SHR_PAR_Q != null && TXLT_SHR_PAR_Q != 0 && POS_UNIT_OF_CALC != null && POS_UNIT_OF_CALC != 0)
			UNIT_MARKET_VALUE = (amrtdBookCostAmt / TXLT_SHR_PAR_Q ) / POS_UNIT_OF_CALC;			
		}
		taxlotIORow.setAsOfDate(results.getTimestamp("AS_OF_D"));
		taxlotIORow.setFundNumber(results.getLong("FUND_N"));
		taxlotIORow.setPortfolioFundNumber(results.getLong("PORT_FUND_N"));
		
		String longShortCode = "";
		if(StringUtils.isNotBlank(SEC_CL_TY_C) && ("IW".equals(SEC_CL_TY_C.trim()) || "TW".equals(SEC_CL_TY_C.trim()) || "DW".equals(SEC_CL_TY_C.trim()) ||
				 "CW".equals(SEC_CL_TY_C.trim())) && INV1_SCTY_QUAL_N == 1){
			longShortCode = "S";
		}else{
			longShortCode = LNG_SHRT_C;
		}		
		taxlotIORow.setLongShortCode(longShortCode); // FACDAL - 2341 ( SWAP Logic)
		taxlotIORow.setShareParQuantity(TXLT_SHR_PAR_Q);
		taxlotIORow.setCurrentFaceAmountAdjustedByUnsettledSells(results.getDouble("FACE_ADJ_UNSTL_SELL_A"));		
		taxlotIORow.setAccrualDayCount(ACRU_DAY_CNT);		
		taxlotIORow.setCUSIPNumber(results.getString("CUSP_N"));
		taxlotIORow.setFMRCUSIPNumber(results.getString("FMR_CUSP_N"));
		taxlotIORow.setTaxLotMemoNumberLegacy(results.getString("TL_DESC_4L"));
		taxlotIORow.setExtractKindCode(results.getString("EXTT_KND_C"));
		taxlotIORow.setCostBasisCode(results.getString("CST_BASIS_C"));
		taxlotIORow.setTradeBasisCode(results.getDouble("TRD_BASIS_C"));
		//String CUSP_N = results.getString("CUSP_N");
		Double US_INT_BOUGHT_FOR_CALC = 0.0;
		//FACDAL-2197
		if(priceCode != null && ArrayUtils.contains(prcCodesList, priceCode)) {			
			Double EFF_YLD_TO_MTY_R = results.getDouble("EFF_YLD_TO_MTY_R");
			taxlotIORow.setEffectiveYieldToMaturityRate(EFF_YLD_TO_MTY_R != null ? (EFF_YLD_TO_MTY_R/100) : null);
			Double TXLT_AMRT_COST_YLD_MKT = results.getDouble("TXLT_AMRT_COST_YLD_MKT");
			Double CUR_BK_CST_YLD_WRST = results.getDouble("CUR_BK_CST_YLD_WRST");
			taxlotIORow.setTaxLotAmortCostYieldMarket(TXLT_AMRT_COST_YLD_MKT !=null ? (TXLT_AMRT_COST_YLD_MKT/100):null);
			taxlotIORow.setTaxlotCurrentBookCostYieldToWorst(CUR_BK_CST_YLD_WRST != null ? (CUR_BK_CST_YLD_WRST / 100):null);
			Double CUR_INT_PUR_A_BASE = results.getDouble("CUR_INT_PUR_A_BASE");
			if(CUR_INT_PUR_A_BASE != null && CUR_INT_PUR_A_BASE != 0){
				US_INT_BOUGHT_FOR_CALC = CUR_INT_PUR_A_BASE * -1;
			}
			taxlotIORow.setTaxlotCurrentInterestPurchasedAmountBase(US_INT_BOUGHT_FOR_CALC); // FACDAL - 2705
		}
		taxlotIORow.setTaxLotMemoNumber(results.getString("TL_MEMO_N"));
		taxlotIORow.setInvestOneSecurityCusipNumber(results.getString("INV1_SCTY_CUSIP_N"));
		taxlotIORow.setInvestOneSecurityQualifier(INV1_SCTY_QUAL_N);
		taxlotIORow.setInvestOneSecurityDate(results.getDouble("INV1_SCTY_DATE_N"));
		taxlotIORow.setInterestAmortizationAccretionThruDate(results.getTimestamp("INT_AMRT_ACRTN_D"));
		// FACDAL - 2275
		Double DEN_INT_BOUGHT_FOR_CALC = 0.0;
		Double LOCL_INT_PUR_A = results.getDouble("LOCL_INT_PUR_A");
		if(priceCode != null && ArrayUtils.contains(prcCodes,priceCode)){
			if(LOCL_INT_PUR_A != null && LOCL_INT_PUR_A != 0){
				DEN_INT_BOUGHT_FOR_CALC = -1 * (LOCL_INT_PUR_A);
			}						
		}
		taxlotIORow.setLocalInterestPurchasedAmount(DEN_INT_BOUGHT_FOR_CALC); //FACDAL - 2408
		taxlotIORow.setOriginalTradeDate(results.getTimestamp("ORGNL_TRD_D"));
		taxlotIORow.setTaxlotPurchaseSettlementDate(ORGNL_SETL_D);
		taxlotIORow.setAccountingPricingCurrencyExchangeRate(results.getDouble("ACC_PRCG_CURR_EXCH_R"));
		
		Double costAmount = 0.0;
		Double costAmtBase = 0.0;
		Double lclBookCDayBasis = 0.0;
		Double bookCdayBasis = 0.0;
		Double POS_A1_CURR_EXCH_R = results.getDouble("POS_A1_CURR_EXCH_R");
		Double ADJ_BSE_AT_PUR_TX_LOCL = results.getDouble("ADJ_BSE_AT_PUR_TX_LOCL");
		
		Double TXLT_A1_LOCL_AMRTD_BK_COST_A = results.getDouble("A1_LOCL_AMRTD_BK_COST_A");
		Double A1_SHR_PAR_Q = results.getDouble("A1_SHR_PAR_Q");
		Double priceAmount = 0.0;
		Double forPriceAmt = 0.0;
		A1_LOCL_AMRTD_BK_COST_A = 0.0;
		Double PRC_A = results.getDouble("PRC_A");
		Double FRN_EXCH_R = results.getDouble("FRN_EXCH_R");
		if(StringUtils.isNotBlank(SEC_CL_TY_C) && ("IW".equals(SEC_CL_TY_C.trim()) || "TW".equals(SEC_CL_TY_C.trim()) || "DW".equals(SEC_CL_TY_C.trim()) ||
				 "CW".equals(SEC_CL_TY_C.trim()))){
			costAmount = TXLT_SHR_PAR_Q  + Math.abs(ADJ_BSE_AT_PUR_TX_LOCL) ;
			lclBookCDayBasis = TXLT_SHR_PAR_Q + Math.abs(LOCL_AMRTD_BK_COST_A);
			A1_LOCL_AMRTD_BK_COST_A = Math.abs(TXLT_A1_LOCL_AMRTD_BK_COST_A) + A1_SHR_PAR_Q;			
						
			if(POS_CURR_EXCH_R != 0){
				costAmtBase = costAmount / POS_CURR_EXCH_R;
				bookCdayBasis = lclBookCDayBasis / POS_CURR_EXCH_R;
			}
			
			A1_AMRTD_BK_COST_A = 0.0;
			if(POS_A1_CURR_EXCH_R != 0){
				A1_AMRTD_BK_COST_A = A1_LOCL_AMRTD_BK_COST_A / POS_A1_CURR_EXCH_R;
			}			
			if(TXLT_SHR_PAR_Q != 0){
				forPriceAmt =  (TXLT_SHR_PAR_Q + Math.abs(ADJ_BSE_AT_PUR_TX_LOCL)) / TXLT_SHR_PAR_Q;
			}
			if(POS_UNIT_OF_CALC != 0){
				priceAmount = forPriceAmt / POS_UNIT_OF_CALC;
			}					
			taxlotIORow.setForeignExchangeRate(results.getDouble("POS_CURR_EXCH_R")); // FACDAL - 2852. ACQ-EXCH-RATE			
		}
		else{
			if(StringUtils.isNotBlank(SEC_CL_TY_C) && ("II".equals(SEC_CL_TY_C.trim()))){
				costAmount = PRC_A * TXLT_SHR_PAR_Q * POS_UNIT_OF_CALC;
				if(FRN_EXCH_R != null && FRN_EXCH_R != 0){
					costAmtBase = costAmount / FRN_EXCH_R;
				}				
			}
			else{
				costAmount = results.getDouble("ME_ORGNL_CST_A");
				costAmtBase = results.getDouble("CST_BS_A");
			}			
						
			lclBookCDayBasis = results.getDouble("LOCL_AMRTD_BK_COST_A"); // FACDAL - 2852. LCL-BOOK-CDAY-BASIS
			bookCdayBasis = results.getDouble("AMRTD_BK_COST_A");
			A1_LOCL_AMRTD_BK_COST_A = TXLT_A1_LOCL_AMRTD_BK_COST_A; // FACDAL - 2852. LCL-TAX-CDAY-BASIS
			A1_AMRTD_BK_COST_A = results.getDouble("A1_AMRTD_BK_COST_A"); // FACDAL - 2852. BS-TAX-CDAY-BASIS
			priceAmount = results.getDouble("PRC_A");		// FACDAL - 2763.	
			taxlotIORow.setForeignExchangeRate(results.getDouble("FRN_EXCH_R")); // FACDAL - 2852. ACQ-EXCH-RATE
		}		
		taxlotIORow.setCostAmountBase(costAmtBase); //FACDAL - 2341. BASE-COST-BASIS
		taxlotIORow.setCostAmount(costAmount); //FACDAL - 2311, 2341. LCL-COST-BASIS
		taxlotIORow.setAmortizedBookCostAmount(bookCdayBasis); // FACDAL- 2341. BS-BOOK-CDAY-BASIS		
		taxlotIORow.setLocalAmortizedBookCostAmount(lclBookCDayBasis);// FACDAL- 2341. LCL-BOOK-CDAY-BASIS	
		taxlotIORow.setPriceAmount(priceAmount); //FACDAL - 2341. LCL_EXEC_PRICE
		
		taxlotIORow.setFocasUnitMarketValuePrice(results.getDouble("FCS_CUR_MKT_PRC_BASE"));
		taxlotIORow.setSecurityCallPutCode(results.getString("SEC_CALL_PUT_C"));
		taxlotIORow.setCallPutNextIndicator(results.getString("CALL_PUT_NXT_I"));
		
		if(TXLT_DS_1 !=null && TXLT_DS_1.length() == 16 && TXLT_DS_1.substring(0,8).matches("[0-9]+")){
			String tempDate = TXLT_DS_1.substring(0,8);
			if(isValidDate(tempDate))
				taxlotIORow.setTaxlotDescription1(TXLT_DS_1);
		}
		else{
			taxlotIORow.setTaxlotDescription1(null);
		}
		
		taxlotIORow.setSourceCode(SRC_C);
		Double txltamrta = results.getDouble("TXLT_AMRT_A");
		Timestamp reportRequestedDate = null;
		if(txltamrta != 0)
		{			
			if(priceCode != null){
			if(priceCode.equals("B") || priceCode.equals("K") || priceCode.equals("R") || priceCode.equals("Z")){
				reportRequestedDate = results.getTimestamp("RPT_RQST_D");				
			}
		  }
		}
		taxlotIORow.setReportRequestedDate(reportRequestedDate);
		//FACDAL - 2428
		Double SEC_ISS_PRC = results.getDouble("SEC_ISS_PRC");
		if(SEC_ISS_PRC != null && SEC_ISS_PRC != 0 && SEC_ISS_PRC < 100) {			
			taxlotIORow.setTaxlotAdjustedOIDBasisAtPurchaseTaxLocal(ADJ_BSE_AT_PUR_TX_LOCL);
		}else{
			taxlotIORow.setTaxlotAdjustedOIDBasisAtPurchaseTaxLocal(Double.valueOf("0.0"));
		}
		
		taxlotIORow.setDailyAmortizationAmount(results.getDouble("AMRT_BS_A")); // CURRENT-DAY-CUMUL-AMORT. FACDAL - 2417
		taxlotIORow.setTotalCumulativeAmortization(results.getDouble("AMRT_BS_A")); //TOTAL-CUMUL-AMORT. FACDAL - 2417
		
		return taxlotIORow;
		}
		
		public boolean isValidDate(String dateString) {
		    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		    try {
		    	df.setLenient(false);
		        df.parse(dateString);
		        return true;
		    } catch (Exception e) {
		        return false;
		    }
		}
		
		
		private TaxlotData setMaturedTaxlotForIO(ResultSet results, TaxlotData taxlotMatIORow) throws SQLException {
			
			subAcctCodeIO = "00";
			Long fundNo = results.getLong("FUND_N");
			Long portFundNo = results.getLong("PORT_FUND_N");
			Double UNIT_OF_CALC = 0.01;
			taxlotMatIORow.setUnitOfCalculation(UNIT_OF_CALC);				
			Long PORT_FUND_N = results.getLong("PORT_FUND_N");
			Long FUND_N = results.getLong("FUND_N");
			String TXN_MEMO_N = results.getString("TXN_MEMO_N");
			String tradingApplicationTradeId = results.getString("LKUP_TRD_APPL_TRD_ID");
			String INV1_SCTY_CUSIP_N = results.getString("INV1_SCTY_CUSIP_N");			
			taxlotMatIORow.setTradingApplicationTradeId(tradingApplicationTradeId);
			taxlotMatIORow.setBeginFiscalYearCumulativeAmortizationAmount(0.0);
			Double PR_DAY_CUM_AMRT = 0.0;
			taxlotMatIORow.setPriorDayCumulativeAmort(PR_DAY_CUM_AMRT);
			taxlotMatIORow.setCUSIPNumber(INV1_SCTY_CUSIP_N);		
			taxlotMatIORow.setTransactionCurrencyCode(results.getString("CURR_C"));						
			taxlotMatIORow.setOriginalTradeDate(results.getTimestamp("TRD_D"));					
			taxlotMatIORow.setFocasSecurityDescription(results.getString("SEC_SHT_NM"));			
			taxlotMatIORow.setAsOfDate(results.getTimestamp("AS_OF_D"));
			taxlotMatIORow.setFundNumber(fundNo);
			taxlotMatIORow.setPortfolioFundNumber(portFundNo);
			taxlotMatIORow.setLongShortCode(results.getString("LNG_SHT_I"));
			taxlotMatIORow.setShareParQuantity(results.getDouble("SHR_Q"));
			taxlotMatIORow.setCurrentFaceAmountAdjustedByUnsettledSells(results.getDouble("ORIG_FACE_A"));
			taxlotMatIORow.setLocalAmortizedBookCostAmount(results.getDouble("COST_BASE_A"));
			// FACDAL - 2638. INT-NORMAL-ACCR-DAYS			
			Long ACRU_DAY_CNT = new Long(0);			
			Timestamp CONT_STLM_D = results.getTimestamp("MATU_CONT_STLM_D");			
			DateTime CONT_STLM_D_DT = CONT_STLM_D != null ? new DateTime(CONT_STLM_D):null;
			if(FUND_N != null && PORT_FUND_N != null && TXN_MEMO_N != null){
				String acrualdaysKey = FUND_N.toString() + PORT_FUND_N.toString() + TXN_MEMO_N;
				String fundKey = FUND_N.toString();
				if(acrualdaysKey != null && fundKey != null) {
					if(!accrualDaysFundPortMap.isEmpty() && accrualDaysFundPortMap.containsKey(fundKey) 
							&& !priordayAccrDaysFundPortMap.isEmpty() && priordayAccrDaysFundPortMap.containsKey(fundKey)){
						DateTime ACRU_THR_D = accrualDaysFundPortMap.get(fundKey);
						DateTime PRIORDAY_ACRU_THR_D = priordayAccrDaysFundPortMap.get(fundKey);
						if((ACRU_THR_D != null && invalidDate.toDateMidnight().isEqual(ACRU_THR_D.toDateMidnight())) ||
							(PRIORDAY_ACRU_THR_D != null && invalidDate.toDateMidnight().isEqual(PRIORDAY_ACRU_THR_D.toDateMidnight()))){
					    	ACRU_DAY_CNT = new Long(1);
						}
						else if(CONT_STLM_D_DT != null && !CONT_STLM_D_DT.isAfter(ACRU_THR_D)){
							CONT_STLM_D_DT = CONT_STLM_D_DT.minusDays(1);
							ACRU_DAY_CNT = new Long(Math.abs(Days.daysBetween(CONT_STLM_D_DT,PRIORDAY_ACRU_THR_D).getDays()));;					
						}
						else {
							ACRU_DAY_CNT = new Long(0);					
							}					
						}				
				}
			}
			taxlotMatIORow.setAccrualDayCount(ACRU_DAY_CNT);				
			taxlotMatIORow.setEffectiveYieldToMaturityRate(results.getDouble("PUR_YLD_A"));
			taxlotMatIORow.setTaxLotMemoNumber(results.getString("TXN_MEMO_N"));			
			taxlotMatIORow.setTaxLotAmortCostYieldMarket(results.getDouble("PUR_YLD_A"));
			Double US_INT_BOUGHT_FOR_CALC = 0.0;
			Double DEN_INT_BOUGHT_FOR_CALC = 0.0;
			taxlotMatIORow.setLocalInterestPurchasedAmount(DEN_INT_BOUGHT_FOR_CALC);
			taxlotMatIORow.setTaxlotCurrentInterestPurchasedAmountBase(US_INT_BOUGHT_FOR_CALC); 
			taxlotMatIORow.setForeignExchangeRate(results.getDouble("CURR_EXCH_R"));
			taxlotMatIORow.setPriceAmount(results.getDouble("LOCL_TRX_PRC_A"));
			taxlotMatIORow.setCostAmountBase(results.getDouble("COST_BASE_A"));
			taxlotMatIORow.setTaxlotAdjustedOIDBasisAtPurchaseTaxLocal(Double.valueOf("0.0"));			
			// FACDAL - 3289. BS-TAX-CDAY-BASIS, LCL-TAX-CDAY-BASIS
			if(fundNo != null && portFundNo != null && !(fundNo.toString().equals(portFundNo.toString()))){ 
				A1_LOCL_AMRTD_BK_COST_A = 0.0;
				A1_AMRTD_BK_COST_A = 0.0;				
			}else{
				A1_LOCL_AMRTD_BK_COST_A = results.getDouble("COST_A");
				A1_AMRTD_BK_COST_A = results.getDouble("COST_BASE_A");
			}
			taxlotMatIORow.setAmortizedBookCostAmount(results.getDouble("COST_BASE_A"));
			UNIT_MARKET_VALUE = results.getDouble("LOCL_TRX_PRC_A");
			taxlotMatIORow.setSourceCode(results.getString("SRC_C"));
			taxlotMatIORow.setFocasMaturedPositionFlag("Y");
			POS_NEW_BUY_I = "N";
			taxlotMatIORow.setTaxlotPurchaseSettlementDate(results.getTimestamp("CONT_STLM_D"));
			taxlotMatIORow.setCostAmount(results.getDouble("COST_A"));		
			//FACDAL - 2474. INT-PAYMENT
			Double INT_PAYMENT = 0.0;
			Double US_ACCR_CURR_PERIOD = 0.0;
  	  	    Double MATU_INCM_A = results.getDouble("MATU_INCM_A");
  	  	    Double MATU_RLVD_CARR = results.getDouble("MATU_RLVD_CARR");
  	  	    Double MATU_SHR_Q = results.getDouble("MATU_SHR_Q");
  	  	    Double MATU_INCM_BASE_A = results.getDouble("MATU_INCM_BASE_A");
			if(MATU_INCM_A != null && MATU_RLVD_CARR != null && MATU_SHR_Q != null && MATU_SHR_Q != 0){
				BigDecimal bd1 = new BigDecimal(MATU_RLVD_CARR.toString());
				BigDecimal bd2 = new BigDecimal(MATU_SHR_Q.toString());
				BigDecimal bd3 = bd1.divide(bd2, 4,RoundingMode.HALF_UP);
				INT_PAYMENT = Math.abs(bd3.doubleValue()) * MATU_INCM_A;
			}
			if(MATU_RLVD_CARR != null && MATU_SHR_Q != null && MATU_SHR_Q != 0 && MATU_INCM_BASE_A != null){
				US_ACCR_CURR_PERIOD = (MATU_RLVD_CARR / Math.abs(MATU_SHR_Q)) * MATU_INCM_BASE_A;				
			}
			taxlotMatIORow.setInterestPaymentAmountLocal(INT_PAYMENT);		
			taxlotMatIORow.setLocalCurrentPayPeriodNetInterestAccruedAmount(INT_PAYMENT);  // FACDAL - 2622. DEN-ACCR-CURR-PERIOD
			taxlotMatIORow.setAccruedInterestAmountBase(US_ACCR_CURR_PERIOD);	// FACDAL - 2622. US-ACCR-CURR-PERIOD			
			taxlotMatIORow.setDailyInterestAcruedAmount(MATU_INCM_A);	//DEN-ACCR-DAILY. FACDAL - 2878
			taxlotMatIORow.setTaxlotCurrentNetDailyInterestAccruedAmountBase(MATU_INCM_BASE_A);	//US-ACCR-DAILY. FACDAL - 2878
			String TXN_C = results.getString("TXN_C");			
			if(TXN_C.equals("WSADJ+")){
				Double MATU_RLVD_COST = results.getDouble("MATU_RLVD_COST");
				Double COST_BASE_A = results.getDouble("COST_BASE_A");
				Double CUMUL_AMORT = 0.0;
				if(MATU_RLVD_COST != null && COST_BASE_A != null){
					CUMUL_AMORT = MATU_RLVD_COST - COST_BASE_A;
				}
				taxlotMatIORow.setDailyAmortizationAmount(CUMUL_AMORT); // CURRENT-DAY-CUMUL-AMORT. FACDAL - 3177
				taxlotMatIORow.setTotalCumulativeAmortization(CUMUL_AMORT); //TOTAL-CUMUL-AMORT. FACDAL - 3177
			}
			return taxlotMatIORow;
			}		
		
		
		private TaxlotData setMatTaxlotForIOForCase3(ResultSet results, TaxlotData taxlotMatIORow) throws SQLException {
			
			subAcctCodeIO = "00";
			taxlotMatIORow.setCUSIPNumber(results.getString("FMR_CUSP_N"));		
			taxlotMatIORow.setTransactionCurrencyCode(results.getString("SEC_ISS_CURR_C"));					
			taxlotMatIORow.setOriginalTradeDate(results.getTimestamp("ORGNL_TRD_D"));					
			taxlotMatIORow.setFocasSecurityDescription(results.getString("SEC_SHT_NM"));			
			taxlotMatIORow.setFundNumber(results.getLong("FUND_N"));
			taxlotMatIORow.setPortfolioFundNumber(results.getLong("PORT_FUND_N"));
			taxlotMatIORow.setLongShortCode(results.getString("LNG_SHT_I"));
			taxlotMatIORow.setTaxLotMemoNumber(results.getString("TL_MEMO_N"));			
			taxlotMatIORow.setForeignExchangeRate(results.getDouble("FRN_EXCH_R")); 
			Double BASE_COST_BASIS = 0.0;
			Double RLVD_COST = results.getDouble("RLVD_COST");
			Double CURR_EXCH_R = results.getDouble("CURR_EXCH_R");
			Double BS_BOOK_CDAY_BASIS = 0.0;
			Double RLVD_CARR = results.getDouble("RLVD_CARR");
			if(CURR_EXCH_R != null && CURR_EXCH_R != 0){
				BASE_COST_BASIS = RLVD_COST / CURR_EXCH_R;
				BS_BOOK_CDAY_BASIS = RLVD_CARR / CURR_EXCH_R;
			}
			taxlotMatIORow.setCostAmountBase(BASE_COST_BASIS); //BASE_COST_BASIS 
			taxlotMatIORow.setCostAmount(RLVD_COST);		 
			taxlotMatIORow.setAmortizedBookCostAmount(BS_BOOK_CDAY_BASIS); //BS_BOOK_CDAY_BASIS 
			taxlotMatIORow.setLocalAmortizedBookCostAmount(RLVD_CARR); 
			taxlotMatIORow.setShareParQuantity(RLVD_CARR);
			taxlotMatIORow.setTaxlotAdjustedOIDBasisAtPurchaseTaxLocal(Double.valueOf("0.0"));
			A1_LOCL_AMRTD_BK_COST_A = RLVD_CARR; 
			A1_AMRTD_BK_COST_A = BS_BOOK_CDAY_BASIS; 
			UNIT_MARKET_VALUE = results.getDouble("LOCL_TRX_PRC_A"); 
			taxlotMatIORow.setSourceCode(results.getString("SRC_C"));
			taxlotMatIORow.setFocasMaturedPositionFlag("Y");
			POS_NEW_BUY_I = "N";
			taxlotMatIORow.setTaxlotPurchaseSettlementDate(results.getTimestamp("ORGNL_SETL_D")); 
			taxlotMatIORow.setDailyAmortizationAmount(results.getDouble("TL_GNLS_A")); // CURRENT-DAY-CUMUL-AMORT. 
			taxlotMatIORow.setTotalCumulativeAmortization(results.getDouble("TL_GNLS_A")); //TOTAL-CUMUL-AMORT. 
			return taxlotMatIORow;
		}		

		@SuppressWarnings("unchecked")
		private void setSecRefValues(String srcCode,String strAsOfDate,String bookView, String exttCode) throws Exception{
			 CallableStatement stmt = null;
			 ResultSet results = null;
			 try{
			 String securityQuery = "BEGIN PKG_FEED_BODTAXLOTFEED.GET_SECS(?,?,?,?,?); END;";
 			 stmt = conn.prepareCall(securityQuery);
 			 stmt.setFetchSize(50000);
			 int i = 1;
		     int ii = i;
	  		 stmt.setString(ii++,strAsOfDate);
		     stmt.setString(ii++,exttCode);
	  	  	 stmt.setString(ii++,bookView);
	  	  	 stmt.setString(ii++, srcCode);
	 	  	 Util.log("Taxlot Query is : " + securityQuery);
	 	  	 stmt.registerOutParameter(5, OracleTypes.CURSOR);
	 	  	 long startTime = System.currentTimeMillis();
	 	  	 stmt.execute();
	  	   	 results = (ResultSet) stmt.getObject(5);
	  	  	 results.setFetchSize(50000);
	  	  	 long endTime = System.currentTimeMillis();
	  	  	 Util.log("Time taken for Security Query Execution: "+(endTime-startTime)/1000 + " seconds");
	  	  	 ArrayList<String> cusipNumbersList = new ArrayList<String>();
	  	  	 while (results.next()) {
	  	  		cusipNumbersList.add(results.getString("CUSP_N"));
	  	  	 }
	  	  	 Util.log("size of the cusip numbers list Object : " + cusipNumbersList.size());
	  	  	 
	  	  	int secrefBatchSize = Integer.valueOf(Util.getValueFromResourceBundle(dfc.getAppName(), "SECREF_BATCH_SIZE", "15000"));
	  	  	
 			 while(cusipNumbersList != null && !cusipNumbersList.isEmpty()){
 				
	 			ArrayList<String> subCusipNumbersList = (ArrayList<String>) getNextBatch(cusipNumbersList,secrefBatchSize);
	 			String[] subCuspNumberArray =  subCusipNumbersList.toArray(new String[subCusipNumbersList.size()]);
	 			Util.log("size of the SUB cusip numbers list Object : " + subCusipNumbersList.size());
	 			ArrayList<SecRefDatesData> secRefDataList = (ArrayList<SecRefDatesData>) SecRefDates.
																		getNextDatesFromSecRefByFMRId(subCuspNumberArray,
																				new java.sql.Timestamp(headerDate.getTime()));
																				//headerDateTime.toString(dft3));
				 Util.log("size of the secRefDataList List : " + secRefDataList.size());
				 populateSecRefMap(cusipNumbersList,secRefDataList);
 			}
			 }catch(Exception e){
					Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
							"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
					throw e;
			 }
			 finally{
				 if(stmt != null)
					 stmt.close();
				 if(results != null)
					 results.close();
			 }
		}			
				
		private String getPriorAsOfDay() throws Exception {
			Date priorBusinessDay = null;
			String strPriorBusinessDay = null;
			priorBusinessDay = dalCalendar.getPriorBusinessDay(todayDate,dfc.getAppName().contains("FIE")?SourceCalendar.IOFIE.id:SourceCalendar.IOMM.id);
			priorBusinessDay = dalCalendar.getNextCalendarDay(priorBusinessDay);			
			DateTime priorAsOfDay = new DateTime(priorBusinessDay);			
			strPriorBusinessDay = priorAsOfDay.toString(dft1);
			return strPriorBusinessDay;
		}
		
		private void setAccrualDaysFromFundPort(String bookView, String exttCode, String srcCode,String strPriorBusinessDay) throws Exception{
			CallableStatement stmt = null;
			ResultSet results = null;
			accrualDaysFundPortMap = new HashMap<String, DateTime>();
			priordayAccrDaysFundPortMap = new HashMap<String, DateTime>();
		    String strAsOfDate = asOfDay.toString(dft1);		    
		    String srcCode1 = "";
		    if(srcCode.equals(Constants.FUND_TYPE_INVESTONE_MM)){
		    	srcCode1 = Constants.FUND_TYPE_FOCAS_MM;
			}
			else if(srcCode.equals(Constants.FUND_TYPE_INVESTONE_FIE)){
				srcCode1 = Constants.FUND_TYPE_FOCAS_FIE;
			}		    
			try{				
			 String fundPortQuery = "BEGIN PKG_FEED_BODTAXLOTFEED.FUND_PORT_QUERY(?,?,?,?,?,?,?); END;";					   
			 stmt = conn.prepareCall(fundPortQuery);
			 stmt.setFetchSize(50000);
			 int i = 1;
		     int ii = i;
		     stmt.setString(ii++,strPriorBusinessDay);
	  		 stmt.setString(ii++,exttCode);
	  	  	 stmt.setString(ii++,srcCode);
	  	  	 stmt.setString(ii++,srcCode1);
		     stmt.setString(ii++,bookView);
		     stmt.setString(ii++,strAsOfDate);
		     stmt.registerOutParameter(7, OracleTypes.CURSOR);
	 	  	 long startTime = System.currentTimeMillis();
	 	  	 Util.log("buytransQuery : : " + fundPortQuery);
	 	  	 stmt.execute();
	  	   	 results = (ResultSet) stmt.getObject(7);
	  	  	 results.setFetchSize(50000);
	  	  	 long endTime = System.currentTimeMillis();
	  	  	 Util.log("Time taken for fundPortQuery Execution: "+(endTime-startTime)/1000 + " seconds");
	  	  	 	  	  	 
	  	  	 while (results.next()) {
	  	  		Timestamp t1 = results.getTimestamp("ACRU_THR_D");
	  	  		Timestamp t2 = results.getTimestamp("PRIOR_ACRU_THR_D");	  	  		
	  	  		DateTime acruThrDate = null;
	  	  		DateTime priorAcruThrDate  = null;
	  	  		if( t1 != null)
	  	  			acruThrDate = new DateTime(results.getTimestamp("ACRU_THR_D"));
	  	  		if( t2 != null)
	  	  			priorAcruThrDate = new DateTime(results.getTimestamp("PRIOR_ACRU_THR_D"));
	  	  		Long fundno = results.getLong("FUND_N");
	  	  		Long portFundNo = results.getLong("PORT_FUND_N");	  	  		 	
	  	  		if( fundno != null && portFundNo != null && acruThrDate != null){	
	  	  			 String key = fundno.toString();	  	  			
	  	  			if(!accrualDaysFundPortMap.containsKey(key)){
	  	  				accrualDaysFundPortMap.put(key, acruThrDate);	  	  				
	  	  			}else if(accrualDaysFundPortMap.containsKey(key) && fundno.longValue() != portFundNo.longValue()){
	  	  				accrualDaysFundPortMap.put(key, acruThrDate);
	  	  			}	  	  			  	  	
	  	  		}	  	
	  	  		if( fundno != null && portFundNo != null && priorAcruThrDate != null){	
 	  			 String key = fundno.toString(); 	  			
 	  			if(!priordayAccrDaysFundPortMap.containsKey(key)){
 	  				priordayAccrDaysFundPortMap.put(key, priorAcruThrDate);	  	  				
 	  			}else if(priordayAccrDaysFundPortMap.containsKey(key) && fundno.longValue() != portFundNo.longValue()){
 	  				priordayAccrDaysFundPortMap.put(key, priorAcruThrDate);
 	  				}	  			
	  	  		}	  	  
	  	  	 }
	  	    Util.log("size of the accrualDaysFundPortMap : " + accrualDaysFundPortMap.size());			
			 }catch(Exception e){
					Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
							"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
					throw e;
			 }
			 finally{
				 if(stmt != null)
					 stmt.close();
				 if(results != null)
					 results.close();
			 }
		}		
		
		private void setIntSoldAmtBaseFromTrans(String bookView, String exttCode, String srcCode,String strAsOfDate) throws Exception{
			CallableStatement stmt = null;
			ResultSet results = null;
		    intSoldAmtBaseTransMap = new HashMap<String, Double>();
		    intSoldAmtTransMap = new HashMap<String, Double>();
		    incmASumTransMap = new HashMap<String, Double>();
		    
		    try{
				String intSoldAmtBaseQuery = "BEGIN PKG_FEED_BODTAXLOTFEED.INT_SOLD_AMT_BASE(?,?,?,?,?); END;";
				
			 stmt = conn.prepareCall(intSoldAmtBaseQuery);
			 stmt.setFetchSize(50000);
			 int ii = 1;
		     stmt.setString(ii++,strAsOfDate);
		     stmt.setString(ii++,srcCode);
	  		 stmt.setString(ii++,exttCode);	  	  	
		     stmt.setString(ii++,bookView);
		     stmt.registerOutParameter(5, OracleTypes.CURSOR);
		     
	 	  	 long startTime = System.currentTimeMillis();
	 	  	 Util.log("intSoldAmtBaseQuery : : " + intSoldAmtBaseQuery);
	 	  	 stmt.execute();
	  	   	 results = (ResultSet) stmt.getObject(5);
	  	  	 results.setFetchSize(50000);
	  	  	 long endTime = System.currentTimeMillis();
	  	  	 Util.log("Time taken for intSoldAmtBaseQuery Execution: "+(endTime-startTime)/1000 + " seconds");
	  	  	 	  	  	 
	  	  	 while (results.next()) {
	  	  		Long portFundNo = results.getLong("PORT_FUND_N");
	  	  		String FMR_CUSP_N = results.getString("FMR_CUSP_N");
	  	  	    String TL_MEMO_N = results.getString("TL_MEMO_N");
	  	  	    String TXN_C = results.getString("TXN_C");
	  	  	    String TRX_CL = results.getString("TRX_CL");
	  	  	    Double INCM_BASE_A = results.getDouble("INCM_BASE_A");
	  	  	    Double INCM_A = results.getDouble("INCM_A");
	  	  	    Double RLVD_CARR = results.getDouble("RLVD_CARR");
	  	  	    Double SHR_Q = results.getDouble("SHR_Q");
	  	  	    String TXN_MEMO_N = results.getString("TXN_MEMO_N");
	  	  	    String RVSL_MEMO_N = results.getString("RVSL_MEMO_N");
	  	  	   	
	  	  	    
	  	  		SellLikeTransactions sellTxn = SellLikeTransactions.getSellLikeTransaction(TXN_C,TRX_CL);
	  	  		
	  	  		if(portFundNo != null && TL_MEMO_N != null && FMR_CUSP_N != null){
	  	  		String key = portFundNo.toString() + FMR_CUSP_N + TL_MEMO_N;
	  	  			  	  		
	  	  		if(SHR_Q != null && SHR_Q != 0 && sellTxn != null && INCM_A != null && RLVD_CARR!= null ){
	  	  			BigDecimal bd1 = new BigDecimal(RLVD_CARR.toString());
	  	  			BigDecimal bd2 = new BigDecimal(SHR_Q.toString());
	  	  			BigDecimal bd3 = bd1.divide(bd2, 8,RoundingMode.HALF_UP);
	  	  			Double finalValue = Math.abs(bd3.doubleValue()) * INCM_A;
	  	  			if(intSoldAmtTransMap.containsKey(key)){
	  	  				Double tempAmt = intSoldAmtTransMap.get(key);
	  	  			    intSoldAmtTransMap.put(key, tempAmt + finalValue);	  	  				
	  	  			}else {
	  	  				intSoldAmtTransMap.put(key, finalValue);	  	  				
	  	  			}	 
	  	  		}
	  	  		
	  	  		if(INCM_BASE_A != null && SHR_Q != null && SHR_Q != 0 && sellTxn != null && RLVD_CARR!= null){
	  	  			
	  	  			BigDecimal bd1 = new BigDecimal(RLVD_CARR.toString());
	  	  			BigDecimal bd2 = new BigDecimal(SHR_Q.toString());
	  	  			BigDecimal bd3 = bd1.divide(bd2, 8,RoundingMode.HALF_UP);
		  	  		Double finalValue = Math.abs(bd3.doubleValue()) * INCM_BASE_A;
	  	  			if(intSoldAmtBaseTransMap.containsKey(key)){
	  	  				Double tempAmt = intSoldAmtBaseTransMap.get(key);
	  	  				intSoldAmtBaseTransMap.put(key, tempAmt + finalValue);   	  	  				
	  	  			}else {
	  	  				intSoldAmtBaseTransMap.put(key, finalValue);  	  				
	  	  				}	
	  	  			}  	  		 	 	 
  	  		 	
	  	  		}
  	  		 	if( portFundNo != null && FMR_CUSP_N != null && INCM_A != null && TXN_C != null) {
  	  		 	String key = portFundNo.toString() + FMR_CUSP_N;	
  	  		 		if(ArrayUtils.contains(txnCodes, TXN_C)){  	  		
  	  		 			if(TXN_MEMO_N != null && RVSL_MEMO_N != null && RVSL_MEMO_N.equals(TXN_MEMO_N)){
  	  		 				INCM_A = -1 * INCM_A;
  	  		 			}
	  	  		 		if(incmASumTransMap.containsKey(key)){
	  	  		 			Double tempAmt = incmASumTransMap.get(key);
	  	  		 			incmASumTransMap.put(key, tempAmt + INCM_A);   	  	  				
	  	  		 		}else {
	  	  		 			incmASumTransMap.put(key, INCM_A);  	  				
	  	  				}	  	  		 		
		  		 	}		  	  		
  	  		 	  }	  	  		
		      }
		    }
	  	    catch(Exception e){
					Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
							"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
					throw e;
			 }
			 finally{
				 if(stmt != null)
					 stmt.close();
				 if(results != null)
					 results.close();
			 }
		}	
		
		private void setIntPayFromTrans(String bookView, String exttCode, String srcCode,String strAsOfDate) throws Exception{
			CallableStatement stmt = null;
			ResultSet results = null;
		    intPayTransMap = new HashMap<String, Double>();
		    accrDailyMatMap = new HashMap<String, Double>(); 
		    contStlmDateMap = new HashMap<String, Timestamp>();
		    usAccrCurrTransMap = new HashMap<String, Double>();
		    try{
				String intPayQuery = "BEGIN PKG_FEED_BODTAXLOTFEED.INT_PAYMENT(?,?,?,?,?); END;";
				
			 stmt = conn.prepareCall(intPayQuery);
			 stmt.setFetchSize(50000);
			 int ii = 1;
		     stmt.setString(ii++,strAsOfDate);
		     stmt.setString(ii++,srcCode);
	  		 stmt.setString(ii++,exttCode);	  	  	
		     stmt.setString(ii++,bookView);
		     stmt.registerOutParameter(5, OracleTypes.CURSOR);
		     
	 	  	 long startTime = System.currentTimeMillis();
	 	  	 Util.log("intPayQuery : : " + intPayQuery);
	 	  	 stmt.execute();
	  	   	 results = (ResultSet) stmt.getObject(5);
	  	  	 results.setFetchSize(50000);
	  	  	 long endTime = System.currentTimeMillis();
	  	  	 Util.log("Time taken for intSoldAmtBaseQuery Execution: "+(endTime-startTime)/1000 + " seconds");
	  	  	 	  	  	 
	  	  	 while (results.next()) {
	  	  		Long portFundNo = results.getLong("PORT_FUND_N");
	  	  		String FMR_CUSP_N = results.getString("FMR_CUSP_N");
	  	  	    String TL_MEMO_N = results.getString("TL_MEMO_N");
	  	  	    Double INCM_A = results.getDouble("INCM_A");
	  	  	    Double INCM_BASE_A = results.getDouble("INCM_BASE_A");
	  	  	    Double RLVD_CARR = results.getDouble("RLVD_CARR");
	  	  	    Double SHR_Q = results.getDouble("SHR_Q"); 	   	
	  	  	    Timestamp CONT_STLM_D = results.getTimestamp("CONT_STLM_D");
	  	  
	  	  		
  	  		 	if( portFundNo != null && FMR_CUSP_N != null && TL_MEMO_N != null){
  	  		 	 	String key = portFundNo.toString() + FMR_CUSP_N + TL_MEMO_N;
	  	  			key = portFundNo.toString() + FMR_CUSP_N + TL_MEMO_N;
	  	  			if(CONT_STLM_D != null)
	  	  				contStlmDateMap.put(key,CONT_STLM_D);
			  	  	if(RLVD_CARR!= null && SHR_Q != null && SHR_Q != 0) {
			  	  			Double finalValue = (RLVD_CARR / Math.abs(SHR_Q)) * INCM_A;	  	
			  	  			Double usAccrCurr = (RLVD_CARR / Math.abs(SHR_Q)) * INCM_BASE_A;	 
			  	  			Double denAccrCurrMatu = (RLVD_CARR / Math.abs(SHR_Q));
			  	  			
			  	  			if(intPayTransMap.containsKey(key)){
			  	  				Double tempAmt = intPayTransMap.get(key);
			  	  				Double tempUSAccrCurrAmt = usAccrCurrTransMap.get(key);
			  	  				Double tempDenAccrMatAmt = accrDailyMatMap.get(key);
			  	  				intPayTransMap.put(key, tempAmt + finalValue);   	  
			  	  				usAccrCurrTransMap.put(key, tempUSAccrCurrAmt + usAccrCurr);  
			  	  				accrDailyMatMap.put(key,denAccrCurrMatu + tempDenAccrMatAmt);
			  	  			}else {
			  	  				intPayTransMap.put(key, finalValue);  
			  	  				usAccrCurrTransMap.put(key, usAccrCurr);  
			  	  				accrDailyMatMap.put(key,denAccrCurrMatu);
			  	  			}	  	  			
		  	  		 	}
  	  		 	}
	  	  	 }		   	
  	  	     Util.log("size of the intPayTransMap : " + intPayTransMap.size());	
			 }catch(Exception e){
					Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
							"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
					throw e;
			 }
			 finally{
				 if(stmt != null)
					 stmt.close();
				 if(results != null)
					 results.close();
			 }
		}	
		
		private void setPositionData(String bookView, String exttCode, String srcCode,String strAsOfDate) throws Exception{
			CallableStatement stmt = null;
			ResultSet results = null;
			positionMap = new HashMap<String, Double>();
			positionMapForCurrExch = new HashMap<String, Double>();		    
		    try{
				String posQuery = "BEGIN PKG_FEED_BODTAXLOTFEED.GET_POSITIONDATA(?,?,?,?,?); END;";
				
			 stmt = conn.prepareCall(posQuery);
			 stmt.setFetchSize(50000);
			 int ii = 1;
		     stmt.setString(ii++,strAsOfDate);
		     stmt.setString(ii++,srcCode);
	  		 stmt.setString(ii++,exttCode);	  	  	
		     stmt.setString(ii++,bookView);
		     stmt.registerOutParameter(5, OracleTypes.CURSOR);
		     
	 	  	 long startTime = System.currentTimeMillis();
	 	  	 Util.log("posQuery : : " + posQuery);
	 	  	 stmt.execute();
	  	   	 results = (ResultSet) stmt.getObject(5);
	  	  	 results.setFetchSize(50000);
	  	  	 long endTime = System.currentTimeMillis();
	  	  	 Util.log("Time taken for posQuery Execution: "+(endTime-startTime)/1000 + " seconds");
	  	  	 	  	  	 
	  	  	 while (results.next()) {
	  	  		Long portFundNo = results.getLong("PORT_FUND_N");
	  	  		String FMR_CUSP_N = results.getString("FMR_CUSP_N");
	  	  	    Double IO_FUND_TY_ADJ_ERND_INCM = results.getDouble("IO_FUND_TY_ADJ_ERND_INCM");
	  	  	    Double CURR_EXCH_R = results.getDouble("CURR_EXCH_R");	  	  	    	  	  
	  	  		
  	  		 	if( portFundNo != null && FMR_CUSP_N != null) {
	  	  		 	String key = portFundNo.toString() + FMR_CUSP_N;
	  	  		 	if(CURR_EXCH_R != null){
	  	  				positionMapForCurrExch.put(key, CURR_EXCH_R);   	  
	  	  		 	}
	  	  		 	if(IO_FUND_TY_ADJ_ERND_INCM != null) {
	  	  		 		positionMap.put(key, IO_FUND_TY_ADJ_ERND_INCM);  	  				
	  	  			}  	  			
  	  		 	}	  	  		
	  	  	 }		   	
  	  	     Util.log("size of the positionMap : " + positionMap.size());	
			 }catch(Exception e){
					Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
							"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
					throw e;
			 }
			 finally{
				 if(stmt != null)
					 stmt.close();
				 if(results != null)
					 results.close();
			 }
		}	
		
		
		private void getIntPaymentFromTrans(String srcCode,String strAsOfDate,String exttCode) throws Exception{
			CallableStatement stmt = null;
			ResultSet results = null;
		    shrParQSumMap = new HashMap<String, Double>();
		    try{
				String transINTQuery = "BEGIN PKG_FEED_BODTAXLOTFEED.INT_PAY_FROM_TRANS(?,?,?,?); END;";
			 stmt = conn.prepareCall(transINTQuery);
			 stmt.setFetchSize(50000);
			 int ii = 1;
			 stmt.setString(ii++,exttCode);
	  		 stmt.setString(ii++,srcCode);
		     stmt.setString(ii++,strAsOfDate);
		     stmt.registerOutParameter(4, OracleTypes.CURSOR);
	 	  	 long startTime = System.currentTimeMillis();
	 	  	 Util.log("transINTQuery : : " + transINTQuery);
	 	  	 stmt.execute();
	  	   	 results = (ResultSet) stmt.getObject(4);;
	  	  	 results.setFetchSize(50000);
	  	  	 long endTime = System.currentTimeMillis();
	  	  	 Util.log("Time taken for transINTQuery Execution: "+(endTime-startTime)/1000 + " seconds");
	  	  	 	  	  	 
	  	  	 while (results.next()) {
	  	  		Long portFundNo = results.getLong("PORT_FUND_N");
	  	  		String fmrCusip = results.getString("FMR_CUSP_N");
	  	  		Double shrParQSum = results.getDouble("SUM_SHR_PAR_Q");
	  	  			  	  		 
	  	  		if(portFundNo != null && fmrCusip != null) {
	  	  			String key = portFundNo.toString() + fmrCusip.toString();
	  	  			shrParQSumMap.put(key, shrParQSum);  	  			
	  	  		}
	  	  	 }
	  	    Util.log("size of the shrParQSumMap : " + shrParQSumMap.size());			
			 }catch(Exception e){
					Util.log(this.getClass(), "Exception thrown for the feed "+dfc.getOutputLocalFileName()+
							"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
					throw e;
			 }
			 finally{
				 if(stmt != null)
					 stmt.close();
				 if(results != null)
					 results.close();
			 }
		}
		
		protected List<? extends Object> getNextBatch(
				List<? extends Object> collection, int batchSize) {

			if (collection == null || collection.size() == 0)
				return null;

			List<? extends Object> subCollection = null;
			int size = collection.size();
			if (size <= batchSize) {
				subCollection = new ArrayList<Object>(collection.subList(0, size));
			} else {
				subCollection = new ArrayList<Object>(collection.subList(0,
						batchSize));
			}

			collection.removeAll(subCollection);
			Util.log(subCollection, "The subCollection is "+subCollection);
			return subCollection;
		}
		private HashMap<String,SecRefDatesData> secRefMap = new HashMap<String,SecRefDatesData>();
		private void populateSecRefMap(ArrayList<String> cusipNumbersList , ArrayList<SecRefDatesData> secRefDataList){
			for(SecRefDatesData row:secRefDataList){
				secRefMap.put(row.getFMRId(), row);
			}
			Util.log("SecRefMap size is : " + secRefMap.size());
			
		}
		
		
		private int writeContentforFOCAS(TaxlotData row ,int recordCount ) throws Exception{
			try{
				
				StringBuilder builder = new StringBuilder();
				
				builder.append(getTaxLotMemoNumber(row));
				builder.append(getLegacyFocasId(row));
				builder.append(getPortfolioFundNumber(row));
				builder.append(getSubAccountCode(row));
				builder.append(getCusipNumber(row));
				builder.append(getLongShortCode(row));
				builder.append(getMaturedPositionIndicator(row));
				builder.append(getSecurityDescription(row));
				builder.append(getOriginalTradeDate(row));
				builder.append(getOriginalSettlementDate(row));
				builder.append(getForeignExchangeRate(row));
				builder.append(getMultiplyDivideIndicatorForExchangeRate(row));
				builder.append(getExecutionPrice(row));
				builder.append(getBaseCostBasis(row));
				builder.append(getLocalCostBasis(row));
				builder.append(getShareParQuantity(row));
				builder.append(getLocalOriginalFaceAmount(row));
				builder.append(getAmortizedBookCostAmount(row));
				builder.append(getAmortizedTaxCostAmount(row));
				builder.append(getNewBuyIndicator(row));
				builder.append(getUnitMarketValue(row));
				builder.append(getUnitOfCalculation(row));
				builder.append(getCurrencyCode(row));
				builder.append(getToBeAnnouncedIndicator(row));
				builder.append(getLocalAmortizedBookCostAmount(row));
				builder.append(getLocalAmortizedTaxCostAmount(row));
				builder.append(getAS400TradeId(row));
				builder.append(getNextCallPutPriceAmount(row));
				builder.append(getCallPutSecurityIndicator1(row));
				builder.append(getCallPutSecurityIndicator2(row));
				builder.append(getLocalCurrentPayPeriodNetInterestAccruedAmount(row));
				builder.append(getDailyInterestAcruedAmount(row));
				builder.append(getAccrualPeriodToDate(row));
				builder.append(getAccrualTotalDaily(row));
				builder.append(getAccrualDayCount(row));
				builder.append(getLocalInterestPurchasedAmount(row));
				builder.append(getLocalInterestPurchasedAmountR(row));
				builder.append(getLocalInterestSoldAmount(row));
				builder.append(getLocalInterestSoldAmountR(row));
				builder.append(getAmortizationThruDate(row));
				builder.append(getBeginFiscalYearCumulativeAmortizationAmount(row));
				builder.append(getPriorDayCummulativeAmortization(row));
				builder.append(getDailyAmortizationAmount(row));
				builder.append(getTotalCummulativeAmortization(row));
				builder.append(getOIDBasisAtPurchase(row));
				builder.append(getYieldToWorst(row));
				builder.append(getEffectiveYieldToMaturityRate(row));
				builder.append(getInterestPaymentAmountLocal(row));

				fw.writeToRecordBuffer(builder.toString());
				if (geodeFunds.contains(row.getPortfolioFundNumber())) {
					fwGeode.writeToRecordBuffer(builder.toString());
				}
				recordCount++;
				if(recordCount == 100000 || recordCount == 500000 || recordCount == 1000000 || 
				   recordCount == 1500000 || recordCount == 2000000 || recordCount == 2500000 || 
				   recordCount == 3000000){
									
					Util.log("Written "+recordCount+" rows in the file : " + dfc.getOutputLocalFileName());
				}
				
				row = null;			
			}catch (Exception e) {
				Util.log(this.getClass(), e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				throw e;
			}
			return recordCount;
		}
		
		private int writeContentforIO(TaxlotData row ,int recordCount ) throws Exception{
		
			
			try{
				
				StringBuilder builder = new StringBuilder();
				builder.append(getTaxLotMemoNumberforIO(row));
				builder.append(getLegacyFocasId(row));
				builder.append(getPortfolioFundNumber(row));
				builder.append(getSubAccountCodeforIO(row));
				builder.append(getCusipNumber(row));
				builder.append(getLongShortCode(row));
				builder.append(getMaturedPositionIndicator(row));
				builder.append(getSecurityDescription(row));
				builder.append(getOriginalTradeDate(row));
				builder.append(getOriginalSettlementDate(row));
				builder.append(getForeignExchangeRateforIO(row));
				builder.append(getMultiplyDivideIndicatorForExchangeRateforIO(row));
				builder.append(getExecutionPrice(row));
				builder.append(getBaseCostBasis(row));
				builder.append(getLocalCostBasis(row));
				builder.append(getShareParQuantity(row));
				builder.append(getLocalOriginalFaceAmount(row));
				builder.append(getAmortizedBookCostAmount(row));
				builder.append(getAmortizedTaxCostAmount(row));
				builder.append(getNewBuyIndicator(row));		
				builder.append(getUnitMarketValueforIO(row));
				builder.append(getUnitOfCalculation(row));
				builder.append(getCurrencyCodeforIO(row));
				builder.append(getToBeAnnouncedIndicator(row));
				builder.append(getLocalAmortizedBookCostAmount(row));
				builder.append(getLocalAmortizedTaxCostAmount(row));
				builder.append(getAS400TradeId(row));
				builder.append(getNextCallPutPriceAmountforIO(row));
				builder.append(getCallPutSecurityIndicator1forIO(row));
				builder.append(getCallPutSecurityIndicator2forIO(row));
				builder.append(getLocalCurrentPayPeriodNetInterestAccruedAmount(row));
				builder.append(getDailyInterestAcruedAmount(row));
				builder.append(getAccrualPeriodToDate(row));
				builder.append(getAccrualTotalDaily(row));
				builder.append(getAccrualDayCount(row));
				builder.append(getLocalInterestPurchasedAmount(row));
				builder.append(getLocalInterestPurchasedAmountRforIO(row));
				builder.append(getLocalInterestSoldAmount(row));
				builder.append(getLocalInterestSoldAmountR(row));
				builder.append(getAmortizationThruDateforIO(row));
				builder.append(getBeginFiscalYearCumulativeAmortizationAmount(row));
				builder.append(getPriorDayCummulativeAmortization(row));
				builder.append(getDailyAmortizationAmount(row));
				builder.append(getTotalCummulativeAmortization(row));
				builder.append(getOIDBasisAtPurchase(row));									
				builder.append(getYieldToWorstforIO(row)); 
				builder.append(getEffectiveYieldToMaturityRateforIO(row));
				builder.append(getInterestPaymentAmountLocal(row));
				String record = builder.toString();
				//int RecordLength = fw.writeRecord(record);
				int RecordLength = record.length();
			    fw.writeToRecordBuffer(record);
			    if (geodeFunds.contains(row.getPortfolioFundNumber())) {
			    	fwGeode.writeToRecordBuffer(record);
			    }
				if(!dfc.isOldFormatEnabled() && RecordLength!=652){
					
					Util.log("THIS RECORD HAS HIGHER LENGTH : Fund_n" + row.getFundNumber() + "port_fund_n : " + row.getPortfolioFundNumber()+
							"tl_memo_n : " + row.getTaxLotMemoNumber());
								//throw new DALFeedException(this,getMessageToken(),new Exception(),dfc.getAppName());
				}
				recordCount++;
				if(recordCount == 100000 || recordCount == 500000 || recordCount == 1000000 || 
				   recordCount == 1500000 || recordCount == 2000000 || recordCount == 2500000 || 
				   recordCount == 3000000){
					
					Util.log("Written "+recordCount+" rows in the file : " + dfc.getOutputLocalFileName());
				}
				row = null;			
			}catch (Exception e) {
				Util.log(this.getClass(), e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				throw e;
			}
			return recordCount;
		}
		
		// hack method to work around bad IO data
		HashMap<String,Boolean> tempHashMap = new HashMap<String,Boolean>();
		
		private boolean validateDuplicationForMatured (TaxlotData row){
			if(tempMatHashMap.get(String.valueOf(row.getFundNumber())+String.valueOf(row.getPortfolioFundNumber())+
						row.getTaxLotMemoNumber()+row.getCUSIPNumber())!=null)
					return true;
			return false;
		}
		
		
		//TLE-UNIQUE-IDENT -TXLT_ME_BY_FUND_N_VW - TL_MEMO_N - PIC X(16) [Record Length : 16]
		private String getTaxLotMemoNumber(TaxlotData row){
			return getFormattedText(row.getTaxLotMemoNumber(),"%-16s"); 
		}
		
		//TLE-UNIQUE-IDENT -TXLT_ME_BY_FUND_N_VW - TL_MEMO_N FOR IO - PIC X(16) [Record Length : 16]
		private String getTaxLotMemoNumberforIO(TaxlotData row) {
			String fundno;
			String tlmemono = "00000000";
			if (row.getPortfolioFundNumber() != null) {
				fundno = getFormattedLong(row.getPortfolioFundNumber(), dfmt08);
			} else
				fundno = getFormattedLong(Long.valueOf("0"), dfmt08);
	
			if (row.getTaxLotMemoNumber() != null) {
				tlmemono = getFormattedLong(
						Long.parseLong(row.getTaxLotMemoNumber().trim()), dfmt08);
			} else {
				tlmemono = getFormattedLong(Long.valueOf("0"), dfmt08);
			}
			return fundno + tlmemono;
		}

		//Once a fund is converted, this will hold the FOCAS tax lot Identifier. TXLT_ME_BY_FUND_N_VW - TL_DESC_4L - PIC X(16) [Record Length : 32]
		private String getLegacyFocasId(TaxlotData row){
			return getFormattedText(row.getTaxlotDescription1()!=null?
								(row.getTaxlotDescription1().length()>16 ?row.getTaxlotDescription1().substring(0,16):row.getTaxlotDescription1())
								:(getFormattedText(row.getTaxlotDescription1(),"%-16s")),"%-16s");			
		}
		//TLE-ACCT-NUMBER - TXLT_SET_BY_FUND_N_VW - PORT_FUND_N - PIC 9(15) [Record Length : 47]
		private String getPortfolioFundNumber(TaxlotData row){
			if (row.getPortfolioFundNumber()!=null)
				return getFormattedLong(row.getPortfolioFundNumber(),dfmt15);
			else
				return getFormattedLong(Long.valueOf("0"),dfmt15);
		}
		//TLE-SUB-ACCT-CODE- POSN_SET_BY_FUND_N_VW	SB_ACC_C - PIC X(2).) [Record Length : 49]
		private String getSubAccountCode(TaxlotData row){
			String subAccountCode =null;
			subAccountCode = POS_SB_ACC_C;
			return getFormattedText(subAccountCode,"%-2s");			
		}
		
		//TLE-SUB-ACCT-CODE- POSN_SET_BY_FUND_N_VW	SB_ACC_C for IO - PIC X(2).) [Record Length : 49]
		private String getSubAccountCodeforIO(TaxlotData row){
			//return getFormattedText("01","%-2s");
			return getFormattedText(subAcctCodeIO,"%-2s"); //updated for FACDAL-1335.
		}

		// TLE-CUSIP - TXLT_SET_BY_FUND_N_VW -	INV1_SCTY_CUSIP_N - PIC X(9)  [Record Length : 58]
		private String getCusipNumber(TaxlotData row){
			return getFormattedText(row.getCUSIPNumber(),"%-9s");
		}
		
		// TLE-LONG-SHORT-IND - TXLT_SET_BY_FUND_N_VW	- LNG_SHRT_C - PIC X  [Record Length : 59]
		private String getLongShortCode(TaxlotData row){
			return getFormattedText(row.getLongShortCode(),"%-1s");
		}
		
		//TLE-MATURED-IND - POSN_SET_BY_FUND_N_VW	- MATU_POSN_I - PIC X [Record Length : 60]
		private String getMaturedPositionIndicator(TaxlotData row){
			if(row.getFocasMaturedPositionFlag() != null)
				return getFormattedText(row.getFocasMaturedPositionFlag(),"%-1s");
			else
				return getFormattedText("N","%-1s");
		}
		
		//TLE-SEC-DESCRIPTION - INV1_SEC_SET_VW -	SHT_NM - PIC X(30)[Record Length : 90]
		private String getSecurityDescription(TaxlotData row){
			
			return getFormattedText(row.getFocasSecurityDescription()!=null?
					(row.getFocasSecurityDescription().length()>30 ?row.getFocasSecurityDescription().substring(0,30):row.getFocasSecurityDescription())
					:(getFormattedText(row.getFocasSecurityDescription(),"%-30s")),"%-30s");			
		}
		
		// TLE-TRADE-DATE - TXLT_ORGNL_BY_FUND_N_VW - ORGNL_TRD_D - PIC 9(8)[Record Length : 98]
		private String getOriginalTradeDate(TaxlotData row){
			String tradeDateFormatted  = null;
			Timestamp tradeDate = row.getOriginalTradeDate();
			if(tradeDate!=null){
				try{
					tradeDateFormatted = fmt2.format(tradeDate);
				
				}catch(Exception e){
					Util.log(this.getClass(), e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
					
				}
			}else
				return getFormattedLong(Long.valueOf("0"),dfmt08);
			return tradeDateFormatted;
		}
		
		//TLE-SETTLE-DATE - TXLT_ORGNL_BY_FUND_N_VW - ORGNL_SETL_D - PIC 9(8)[Record Length : 106]

		private String getOriginalSettlementDate(TaxlotData row){
			String settlementDateFormatted  = null;
			Timestamp settlementDate = row.getTaxlotPurchaseSettlementDate();
			
			if(settlementDate!=null){
				try{
					settlementDateFormatted = fmt2.format(settlementDate);
					}catch(Exception e){
					Util.log(this.getClass(), e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
					
				}
			}else
				return getFormattedLong(Long.valueOf("0"),dfmt08);
			return settlementDateFormatted;
		}
		
		
		// TLE-ACQ-EXCH-RATE - TXLT_ORGNL_BY_FUND_N_VW	- FRN_EXCH_R - PIC 9(9)V9(9)[Record Length : 124]
		private String getForeignExchangeRate(TaxlotData row){
			Double transactionExchangeRate = null;
			transactionExchangeRate = row.getAccountingPricingCurrencyExchangeRate();
			String tempTransactionExchangeRate = null;
			if (transactionExchangeRate !=null && transactionExchangeRate != 0){
				tempTransactionExchangeRate = getUnsignedDouble(transactionExchangeRate,dfmt09_09);
			}else{
				tempTransactionExchangeRate = getUnsignedDouble(Double.valueOf("0.0"),dfmt09_09);
			}
			return tempTransactionExchangeRate;
		}
		
		// TLE-ACQ-EXCH-RATE - TXLT_ORGNL_BY_FUND_N_VW	- FRN_EXCH_R - PIC 9(9)V9(9)[Record Length : 124]
		private String getForeignExchangeRateforIO(TaxlotData row) {
			Double transactionExchangeRate = null;
			transactionExchangeRate = row.getForeignExchangeRate();
			String tempTransactionExchangeRate = null;
			if (transactionExchangeRate != null && transactionExchangeRate != 0) {
				tempTransactionExchangeRate = getUnsignedDouble(
						transactionExchangeRate, dfmt09_09);
			} else {
				tempTransactionExchangeRate = getUnsignedDouble(
						Double.valueOf("0.0"), dfmt09_09);
			}
			return tempTransactionExchangeRate;
		}
		
		// TLE-EXCH-RATE-MULT-DIV-IND - Always "M" for FOCAS transactions. Always "D" for Invest One transactions - PIC X(01) [Record Length : 125]
		private String getMultiplyDivideIndicatorForExchangeRate(TaxlotData row){
			if(row.getSourceCode().equalsIgnoreCase(FOCASMM)||
					row.getSourceCode().equalsIgnoreCase(FOCASFIE)){
				return getFormattedText("M","%-1s");
			}
			else
				return getFormattedText("D","%-1s");							
		}
		
		// TLE-EXCH-RATE-MULT-DIV-IND FOR IO- Always "M" for FOCAS transactions. Always "D" for Invest One transactions - PIC X(01) [Record Length : 125]
		private String getMultiplyDivideIndicatorForExchangeRateforIO(TaxlotData row) {
			return getFormattedText("D", "%-1s");
		}
		
		// TLE-LCL-EXEC-PRICE - TXLT_ORGNL_BY_FUND_N_VW - PRC_A - PIC -9(11)V9(8)[Record Length : 145]
		private String getExecutionPrice(TaxlotData row){
			Double executionPrice = row.getPriceAmount();
			if(executionPrice!=null)
				return getIOSignedDouble(executionPrice,dfmt11_8_pos,dfmt11_8_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt11_8_pos,dfmt11_8_neg);
		}
		// TLE-BASE-COST-BASIS - TXLT_ORGNL_BY_FUND_N_VW - CST_BS_A - PIC -9(15)V9(2)[Record Length : 163]
		private String getBaseCostBasis(TaxlotData row){
			Double baseCostBasis = row.getCostAmountBase();
			if (baseCostBasis != null)
				return getIOSignedDouble(baseCostBasis, dfmt15_2_pos,dfmt15_2_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		// TLE-LCL-COST-BASIS - TXLT_ORGNL_BY_FUND_N_VW - CST_A - PIC -9(15)V9(2)[Record Length : 181]
		private String getLocalCostBasis(TaxlotData row){
			Double localCostBasis = row.getCostAmount();
			if(localCostBasis != null)
				return getIOSignedDouble(localCostBasis,dfmt15_2_pos,dfmt15_2_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		//TLE-QTY-PAR - TXLT_SET_BY_FUND_N_VW	- SHR_PAR_Q - PIC -9(15)V9(4). [Record Length : 201]
		private String getShareParQuantity(TaxlotData row){
			if(row.getShareParQuantity()!=null)
				return getIOSignedDouble(row.getShareParQuantity(),dfmt15_4_pos,dfmt15_4_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_4_pos,dfmt15_4_neg);
		}
		//TLE-LCL-ORIG-FACE-AMT - TXLT_SET_BY_FUND_N_VW	- FACE_ADJ_UNSTL_SELL_A - PIC -9(15)V9(4). [Record Length : 221]
		private String getLocalOriginalFaceAmount(TaxlotData row){
			if(row.getCurrentFaceAmountAdjustedByUnsettledSells()!=null)
				return getIOSignedDouble(row.getCurrentFaceAmountAdjustedByUnsettledSells(),dfmt15_4_pos,dfmt15_4_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_4_pos,dfmt15_4_neg);
		}
		//TLE-BS-BOOK-CDAY-BASIS - TXLT_SET_BY_FUND_N_VW	- AMRTD_BK_COST_A - PIC -9(15)V9(2)[Record Length : 239]
		private String getAmortizedBookCostAmount(TaxlotData row){
			if(row.getAmortizedBookCostAmount()!=null){
				return getIOSignedDouble(row.getAmortizedBookCostAmount(),dfmt15_2_pos,dfmt15_2_neg);
			}else{
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
			}
		}
		//TLE-BS-TAX-CDAY-BASIS - TXLT_SET_BY_FUND_N_VW	- AMRTD_BK_COST_A - PIC -9(15)V9(2)[Record Length : 257]
		private String getAmortizedTaxCostAmount(TaxlotData row){
			if(A1_AMRTD_BK_COST_A!=null){
				return getIOSignedDouble(A1_AMRTD_BK_COST_A,dfmt15_2_pos,dfmt15_2_neg);
			}else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		
		//FACDAL-2193
		//TLE-NEW-BUY-IND - POSN_SET_BY_FUND_N_VW	- NEW_BUY_I - PIC X. [Record Length : 258]
		private String getNewBuyIndicator(TaxlotData row){
			if(POS_NEW_BUY_I != null){
				return getFormattedText(POS_NEW_BUY_I,"%-1s");
			}else {
				return getFormattedText("N","%-1s");
			}			
		}
		
		//TLE-UNIT-MKT-VALUE - POSN_SET_BY_FUND_N_VW	- MKPR_A - PIC -9(11)V9(8) .[Record Length : 278]
		private String getUnitMarketValue(TaxlotData row){
			if(row.getFocasUnitMarketValuePrice() != null)
				return getIOSignedDouble(row.getFocasUnitMarketValuePrice(),dfmt11_8_pos,dfmt11_8_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt11_8_pos,dfmt11_8_neg);
		}
		
		//TLE-UNIT-MKT-VALUE - POSN_SET_BY_FUND_N_VW	- MKPR_A for IO- PIC -9(11)V9(8) .[Record Length : 278]
		private String getUnitMarketValueforIO(TaxlotData row) {
			if (UNIT_MARKET_VALUE != null)
					return getIOSignedDouble(UNIT_MARKET_VALUE, dfmt11_8_pos,
							dfmt11_8_neg);
				else
					return getIOSignedDouble(Double.valueOf("0.0"), dfmt11_8_pos,
							dfmt11_8_neg);
			}
				
		//TLE-UNIT-OF-CALC - TXLT_SET_BY_FUND_N_VW	- UNIT_OF_CALC - PIC S9(9)V9(4)[Record Length : 291]
		private String getUnitOfCalculation(TaxlotData row){
			if(row.getUnitOfCalculation()!=null)
				return getUnsignedDouble(row.getUnitOfCalculation(),dfmt09_4);
			return getUnsignedDouble(Double.valueOf("0.0"),dfmt09_4);
		}
		//TLE-CURRENCY-CODE - HDGTRX_SET_BY_FUND_N_VW - CURR_C - PIC X(4).[Record Length : 295]
		private String getCurrencyCode(TaxlotData row){
				String currencyCode = null;
			if(row.getTransactionCurrencyCode()!=null && row.getTransactionCurrencyCode().equals(previousCurrencyCode))
					return getFormattedText(previousCurrencyISOCode,"%-4s");
				else if(row.getTransactionCurrencyCode()!=null){
					previousCurrencyCode = row.getTransactionCurrencyCode();
					FocasTransVal objFocasTransValue = 
						(FocasTransVal)as400ToISOMap.get(row.getTransactionCurrencyCode());
					if(objFocasTransValue!=null)
						currencyCode = objFocasTransValue.getISO_VALUE();
					previousCurrencyISOCode = currencyCode;
					return getFormattedText(currencyCode,"%-4s");
					//Util.log("its entered in the else if of currency Code :" + row.getTransactionCurrencyCode() + " ISO Code : " + currencyCode);
			}
				return getFormattedText(row.getTransactionCurrencyCode(),"%-4s");
		}
		
		//TLE-CURRENCY-CODE - HDGTRX_SET_BY_FUND_N_VW - CURR_C FOR IO- PIC X(4).[Record Length : 295]
		private String getCurrencyCodeforIO(TaxlotData row) {
			return getFormattedText(row.getTransactionCurrencyCode(), "%-4s");
		}
				
		//TLE-TBA-TRADE-IND - INV1_SEC_SET_VW - TBA_I - PIC X. [Record Length : 296]
		private String getToBeAnnouncedIndicator(TaxlotData row){
			String tradeIndicator = null;
			String tobeAnnouncedIndicator = row.getToBeAnnouncedIndicator();
			  if(tobeAnnouncedIndicator != null)
				  tradeIndicator = tobeAnnouncedIndicator;
			  if(tradeIndicator==null)
				  tradeIndicator="N";
			  return getFormattedText(tradeIndicator,"%-1s");
		}
		//TLE-LCL-BOOK-CDAY-BASIS - TXLT_SET_BY_FUND_N_VW	- LOCL_AMRTD_BK_COST_A - PIC -9(15)V9(2)[Record Length : 314]
		private String getLocalAmortizedBookCostAmount(TaxlotData row){
			if(row.getLocalAmortizedBookCostAmount()!=null){
				return getIOSignedDouble(row.getLocalAmortizedBookCostAmount(),dfmt15_2_pos,dfmt15_2_neg);
			}else{
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
			}
		}
		//TLE-LCL-TAX-CDAY-BASIS - TXLT_SET_BY_FUND_N_VW	- LOCL_AMRTD_BK_COST_A - PIC -9(15)V9(2)[Record Length : 332]
		private String getLocalAmortizedTaxCostAmount(TaxlotData row){
			if(A1_LOCL_AMRTD_BK_COST_A != null){
				return getIOSignedDouble(A1_LOCL_AMRTD_BK_COST_A,dfmt15_2_pos,dfmt15_2_neg);
			}else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
			//}
		}
		//TLE-AS400-TRADE-ID - HDGTRX_SET_BY_FUND_N_VW  - TRD_APPL_TRD_ID - PIC X(13)[Record Length : 345]
		private String getAS400TradeId(TaxlotData row){
				//return getFormattedText(row.getTradingApplicationTradeId(),"%-13s");
				
				return getFormattedText(row.getTradingApplicationTradeId()!=null?
						(row.getTradingApplicationTradeId().length()>13 ?row.getTradingApplicationTradeId().substring(0,13):row.getTradingApplicationTradeId())
						:(getFormattedText(row.getTradingApplicationTradeId(),"%-13s")),"%-13s");
		}
		
		//TLE-CALL-PUT1-PRICE - POSN_SET_BY_FUND_N_VW - NXT_CALL_PUT_PRC_A  - PIC -9(11)V9(8)[Record Length : 365] for FOCAS
		private String getNextCallPutPriceAmount(TaxlotData row){
			if(POS_NXT_CALL_PUT_PRC_A != null)
				return getIOSignedDouble(POS_NXT_CALL_PUT_PRC_A,dfmt11_8_pos,dfmt11_8_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt11_8_pos,dfmt11_8_neg);			
			}
				
		//TLE-CALL-PUT1-PRICE - INV1_SEC_SET_VW - NXT_CALL_PUT_PRC_A  - PIC -9(11)V9(8)[Record Length : 365]
		private String getNextCallPutPriceAmountforIO(TaxlotData row){
			if(SEC_NXT_CALL_PUT_PRC_A != null) {
				Double SEC_NXT_CALL_PUT_PRC_A_ABS = Math.abs(SEC_NXT_CALL_PUT_PRC_A);
				return getIOSignedDouble(SEC_NXT_CALL_PUT_PRC_A_ABS,dfmt11_8_pos,dfmt11_8_neg);
			}
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt11_8_pos,dfmt11_8_neg);
		}
		
		//TLE-CALL-PUT-SECURITY-IND1 - TXLT_SET_BY_FUND_N_VW  - SEC_CALL_PUT_C - PIC X.[Record Length : 366]
		private String getCallPutSecurityIndicator1(TaxlotData row) {			
			return getFormattedText(row.getSecurityCallPutCode(), "%-1s");
			
		}
				
		//TLE-CALL-PUT-SECURITY-IND1 - TXLT_SET_BY_FUND_N_VW  - SEC_CALL_PUT_C for IO- PIC X.[Record Length : 366]
		private String getCallPutSecurityIndicator1forIO(TaxlotData row){
				SecRefDatesData secRefDatesData = (SecRefDatesData)secRefMap.get(row.getCUSIPNumber());
				String callPutSecurityIndicator1 = null;
				if(secRefDatesData!=null){
				Timestamp nextCallDate = secRefDatesData.getNextCallDate();
				Timestamp nextPutDate = secRefDatesData.getNextPutDate();
				Timestamp nextSinkDate = secRefDatesData.getNextSinkDate();
				if(nextCallDate!=null && nextPutDate !=null){
					long nextCallDateLong = nextCallDate.getTime();
					long nextPutDateLong = nextPutDate.getTime();
						if(nextCallDateLong<nextPutDateLong || nextCallDateLong == nextPutDateLong){
							if(nextSinkDate!=null){
								if(nextCallDateLong<nextSinkDate.getTime())
									callPutSecurityIndicator1 = "C";
								else
									callPutSecurityIndicator1 = "S";
							}
							else
								callPutSecurityIndicator1 ="C";
								
						}else{
							if(nextSinkDate!=null){
								if(nextPutDateLong<nextSinkDate.getTime())
									callPutSecurityIndicator1 = "P";
								else
									callPutSecurityIndicator1 = "S";
							}else
								callPutSecurityIndicator1 = "P";
						}
				}
				if(nextCallDate!=null && nextPutDate ==null){
					long nextCallDateLong = nextCallDate.getTime();
					if(nextSinkDate!=null){
								if(nextCallDateLong<nextSinkDate.getTime())
									callPutSecurityIndicator1 = "C";
								else
									callPutSecurityIndicator1 = "S";
							}
							else
								callPutSecurityIndicator1 ="C";
				}
				if(nextCallDate==null && nextPutDate !=null){
					long nextPutDateLong = nextPutDate.getTime();
					if(nextSinkDate!=null){
						if(nextPutDateLong<nextSinkDate.getTime())
							callPutSecurityIndicator1 = "P";
						else
							callPutSecurityIndicator1 = "S";
						}
					else
						callPutSecurityIndicator1 ="P";
					}
				}
				return getFormattedText(callPutSecurityIndicator1,"%-1s");
		}
		
		//TLE-CALL-PUT-SECURITY-IND2 - TXLT_SET_BY_FUND_N_VW  - CALL_PUT_NXT_I - PIC X.[Record Length : 367]
		private String getCallPutSecurityIndicator2(TaxlotData row) {
			return getFormattedText(row.getCallPutNextIndicator(), "%-1s");			
		}

		//TLE-CALL-PUT-SECURITY-IND2 - TXLT_SET_BY_FUND_N_VW  - CALL_PUT_NXT_I - PIC X.[Record Length : 367]
		private String getCallPutSecurityIndicator2forIO(TaxlotData row){
			SecRefDatesData secRefDatesData = (SecRefDatesData)secRefMap.get(row.getCUSIPNumber());
			String callPutSecurityIndicator2 = null;
			if(secRefDatesData!=null){
			Timestamp nextCallDate = secRefDatesData.getNextNextCallDate();
			Timestamp nextPutDate = secRefDatesData.getNextNextPutDate();
			Timestamp nextSinkDate = secRefDatesData.getNextNextSinkDate();
			if(nextCallDate!=null && nextPutDate !=null){
				long nextCallDateLong = nextCallDate.getTime();
				long nextPutDateLong = nextPutDate.getTime();
					if(nextCallDateLong<nextPutDateLong || nextCallDateLong == nextPutDateLong){
						if(nextSinkDate!=null){
							if(nextCallDateLong<nextSinkDate.getTime())
								callPutSecurityIndicator2 = "C";
							else
								callPutSecurityIndicator2 = "S";
						}
						else
							callPutSecurityIndicator2 ="C";
							
					}else{
						if(nextSinkDate!=null){
							if(nextPutDateLong<nextSinkDate.getTime())
								callPutSecurityIndicator2 = "P";
							else
								callPutSecurityIndicator2 = "S";
						}else
							callPutSecurityIndicator2 = "P";
					}
			}
			if(nextCallDate!=null && nextPutDate ==null){
				long nextCallDateLong = nextCallDate.getTime();
				if(nextSinkDate!=null){
							if(nextCallDateLong<nextSinkDate.getTime())
								callPutSecurityIndicator2 = "C";
							else
								callPutSecurityIndicator2 = "S";
						}
						else
							callPutSecurityIndicator2 ="C";
			}
			if(nextCallDate==null && nextPutDate !=null){
				long nextPutDateLong = nextPutDate.getTime();
					if(nextSinkDate!=null){
						if(nextPutDateLong<nextSinkDate.getTime())
							callPutSecurityIndicator2 = "P";
						else
							callPutSecurityIndicator2 = "S";
					}
					else
						callPutSecurityIndicator2 ="P";
				}
			}
			return getFormattedText(callPutSecurityIndicator2,"%-1s");
		}
		// TLE-ACCR-PERIOD-TO-DATE - TXLT_SET_BY_FUND_N_VW - LOCL_CUR_PAY_PRD_NETINT_ACRU_A - PIC -9(15)V9(2). [Record Length : 385]
		private String getLocalCurrentPayPeriodNetInterestAccruedAmount(TaxlotData row){
			if(row.getLocalCurrentPayPeriodNetInterestAccruedAmount() !=null && row.getLocalCurrentPayPeriodNetInterestAccruedAmount()!=0)
				return getIOSignedDouble(row.getLocalCurrentPayPeriodNetInterestAccruedAmount(),dfmt15_2_pos,dfmt15_2_neg);
			else
				return getIOSignedDouble(Double.valueOf(0.00),dfmt15_2_pos,dfmt15_2_neg);
		}
		// TLE-ACCR-TOTAL-DAILY - TXLT_SET_BY_FUND_N_VW - DLY_INT_ACRU_A  - PIC -9(15)V9(2). [Record Length : 403]
		private String getDailyInterestAcruedAmount(TaxlotData row){
			if(row.getDailyInterestAcruedAmount() !=null)
				return getIOSignedDouble(row.getDailyInterestAcruedAmount(),dfmt15_2_pos,dfmt15_2_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		// TLE-US-ACCR-PERIOD-TO-DATE - TXLT_SET_BY_FUND_N_VW - ACRU_INT_BS   - PIC -9(15)V9(2). [Record Length : 421]
		private String getAccrualPeriodToDate(TaxlotData row){
			if(row.getAccruedInterestAmountBase() !=null)
				return getIOSignedDouble(row.getAccruedInterestAmountBase(),dfmt15_2_pos,dfmt15_2_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		// TLE-US-ACCR-TOTAL-DAILY - TXLT_SET_BY_FUND_N_VW - CUR_NET_DAY_INT_ACRU_A_BASE   - PIC -9(15)V9(2). [Record Length : 439]
		private String getAccrualTotalDaily(TaxlotData row){
			Double totalDailyAccrual =null;
			totalDailyAccrual = row.getTaxlotCurrentNetDailyInterestAccruedAmountBase();
			if(totalDailyAccrual != null)
				return getIOSignedDouble(totalDailyAccrual,dfmt15_2_pos,dfmt15_2_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		
		// TLE-INT-NORMAL-ACCR-DAYS - TXLT_SET_BY_FUND_N_VW - ACRU_DAY_CNT - PIC 9(3). [Record Length : 442]
		private String getAccrualDayCount(TaxlotData row){
			return replaceNegativeWithBlank(getFormattedLong(row.getAccrualDayCount(),dfmt03));
		}
		private String replaceNegativeWithBlank (String strObj){
			return strObj.replace("-", "");
		}
		// TLE-INT-BOUGHT-FOR-CALC - TXLT_SET_BY_FUND_N_VW - LOCL_INT_PUR_A - PIC -9(15)V9(2). [Record Length : 460]
		private String getLocalInterestPurchasedAmount(TaxlotData row){
			if(row.getLocalInterestPurchasedAmount() !=null)
				return getIOSignedDouble(row.getLocalInterestPurchasedAmount(),dfmt15_2_pos,dfmt15_2_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		
		//TLE-US-INT-BOUGHT-FOR-CALC - TXLT_SET_BY_FUND_N_VW - CUR_INT_PUR_A_BASE - PIC -9(15)V9(2). [Record Length : 478]
		private String getLocalInterestPurchasedAmountR(TaxlotData row){
			if(row.getTaxlotCurrentInterestPurchasedAmountBase() !=null)
					return getIOSignedDouble(row.getTaxlotCurrentInterestPurchasedAmountBase(),dfmt15_2_pos,dfmt15_2_neg);
				else
					return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		
		//TLE-US-INT-BOUGHT-FOR-CALC -HDGTRX.INT_PUR_SLD divided by POSN_SET_BY_FUND_N_VW.CURR_EXCH_R - PIC -9(15)V9(2). [Record Length : 478]
		private String getLocalInterestPurchasedAmountRforIO(TaxlotData row){
			if(row.getTaxlotCurrentInterestPurchasedAmountBase() !=null) {
					return getIOSignedDouble(row.getTaxlotCurrentInterestPurchasedAmountBase(),dfmt15_2_pos,dfmt15_2_neg);
				}
				else
					return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
			}			
		
				
		// TLE-INT-SOLD-FOR-CALC - TXLT_SET_BY_FUND_N_VW - LOCL_INT_SLD_A -PIC -9(15)V9(2). [Record Length : 496]
		private String getLocalInterestSoldAmount(TaxlotData row){
			if(row.getLocalInterestSoldAmount() !=null) 
				return getIOSignedDouble(Double.valueOf(row.getLocalInterestSoldAmount()),dfmt15_2_pos,dfmt15_2_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		//TLE-INT-SOLD-FOR-CALC - TXLT_SET_BY_FUND_N_VW - INT_PRNC_C - PIC -9(15)V9(2). [Record Length : 514]
		private String getLocalInterestSoldAmountR(TaxlotData row){
			if(row.getInterestPrincipalCode() !=null) 
				return getIOSignedDouble(row.getInterestPrincipalCode(),dfmt15_2_pos,dfmt15_2_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		//TLE-AMORT-THRU-DATE - TXLT_SET_BY_FUND_N_VW - INT_AMRT_ACRTN_D  - PIC -9(8). [Record Length : 522]
		private String getAmortizationThruDate(TaxlotData row){
			if(row.getInterestAmortizationAccretionThruDate()!=null)
				return fmt2.format(row.getInterestAmortizationAccretionThruDate());
			else
				return getFormattedLong(Long.valueOf("0"),dfmt08);			
		}
		
		//TLE-AMORT-THRU-DATE - TXLT_SET_BY_FUND_N_VW - INT_AMRT_ACRTN_D for IO - PIC -9(8). [Record Length : 522]
		private String getAmortizationThruDateforIO(TaxlotData row) {
			if (row.getReportRequestedDate() != null)
				return fmt2.format(row.getReportRequestedDate());
			else
				return getFormattedLong(Long.valueOf("0"), dfmt08);	
		}
				
		//TLE-BEG-FISCAL-YR-CUMUL-AMORT - TXLT_SET_BY_FUND_N_VW - BG_FISC_YR_CUM_AMRT_A  - PIC -9(15)V9(2). [Record Length : 540]
		private String getBeginFiscalYearCumulativeAmortizationAmount(TaxlotData row){
			if(row.getBeginFiscalYearCumulativeAmortizationAmount()!=null)
				return getIOSignedDouble(row.getBeginFiscalYearCumulativeAmortizationAmount(),dfmt15_2_pos,dfmt15_2_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		//TLE-PRIOR-DAY-CUMUL-AMORT - TXLT_SET_BY_FUND_N_VW - PR_DAY_CUM_AMRT - PIC -9(15)V9(2).[Record Length : 558]
		private String getPriorDayCummulativeAmortization(TaxlotData row){
			if(row.getPriorDayCumulativeAmort()!=null)
				return getIOSignedDouble(row.getPriorDayCumulativeAmort(),dfmt15_2_pos,dfmt15_2_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		
		//TLE-CURRENT-DAY-CUMUL-AMORT - TXLT_SET_BY_FUND_N_VW - DAY_AMRT_A - PIC -9(15)V9(2). [Record Length : 576]
		private String getDailyAmortizationAmount(TaxlotData row){	
			
			if(row.getDailyAmortizationAmount() != null)
				return getIOSignedDouble(row.getDailyAmortizationAmount(),dfmt15_2_pos,dfmt15_2_neg);
			else 
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}

		//TLE-TOTAL-CUMUL-AMORT - TXLT_SET_BY_FUND_N_VW - TT_CUM_AMRT (as of Business Date in header)  - PIC -9(15)V9(2). -[Record Length : 594]
		private String getTotalCummulativeAmortization(TaxlotData row){
			if (row.getTotalCumulativeAmortization() != null)
				return getIOSignedDouble(row.getTotalCumulativeAmortization(),dfmt15_2_pos,dfmt15_2_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
			
		}	
				
		//TLE-OID-BASIS-AT-PURCH - TXLT_SET_BY_FUND_N_VW - ADJ_BSE_AT_PUR_TX_LOCL - PIC -9(15)V9(2). - [Record Length : 612]
		private String getOIDBasisAtPurchase(TaxlotData row){
			if (row.getTaxlotAdjustedOIDBasisAtPurchaseTaxLocal() != null)
					return getIOSignedDouble(row.getTaxlotAdjustedOIDBasisAtPurchaseTaxLocal(),dfmt15_2_pos,dfmt15_2_neg);
			else
					return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		
		//TLE-CST-YIELD-TO-WORST - TXLT_SET_BY_FUND_N_VW - CUR_BK_COST_YLD_WRST - PIC -9(1)V9(8). [Record Length : 622]
		private String getYieldToWorst(TaxlotData row){		
			if(dfc.isOldFormatEnabled()){
				if (row.getTaxlotCurrentBookCostYieldToWorst() !=null)
					return getIOSignedDouble(Double.valueOf(row.getTaxlotCurrentBookCostYieldToWorst()),dfmt01_8_pos,dfmt01_8_neg);
				else
					return getIOSignedDouble(Double.valueOf("0.0"),dfmt01_8_pos,dfmt01_8_neg);
			}else{
				if (row.getTaxlotCurrentBookCostYieldToWorst() !=null)
					return getIOSignedDouble(Double.valueOf(row.getTaxlotCurrentBookCostYieldToWorst()),dfmt01_9_pos,dfmt01_9_neg);
				else
					return getIOSignedDouble(Double.valueOf("0.0"),dfmt01_9_pos,dfmt01_9_neg);
			}			
		}
		
		//TLE-CST-YIELD-TO-WORST - TXLT_SET_BY_FUND_N_VW - CUR_BK_COST_YLD_WRST - PIC -9(1)V9(8). [Record Length : 622]
		private String getYieldToWorstforIO(TaxlotData row){	
			//Double effYieldToMaturity = row.getTaxlotCurrentBookCostYieldToWorst();
			
			
			//FACDAL-2027
			if(dfc.isOldFormatEnabled()){
				if(StringUtils.equals(Constants.FUND_TYPE_INVESTONE_FIE,row.getSourceCode())){

					if(row.getTaxLotAmortCostYieldMarket() != null){
						return getIOSignedDouble(Double.valueOf(row.getTaxLotAmortCostYieldMarket()),dfmt01_8_pos,dfmt01_8_neg);
					} else {
						return getIOSignedDouble(Double.valueOf("0.0"),dfmt01_8_pos,dfmt01_8_neg);
					}
				} else{
					if (row.getTaxlotCurrentBookCostYieldToWorst() !=null) {
						return getIOSignedDouble(Double.valueOf(row.getTaxlotCurrentBookCostYieldToWorst()),dfmt01_8_pos,dfmt01_8_neg);
					} else{
						return getIOSignedDouble(Double.valueOf("0.0"),dfmt01_8_pos,dfmt01_8_neg);
					}
				}
			}else{
				if(StringUtils.equals(Constants.FUND_TYPE_INVESTONE_FIE,row.getSourceCode())){

					if(row.getTaxLotAmortCostYieldMarket() != null){
						return getIOSignedDouble(Double.valueOf(row.getTaxLotAmortCostYieldMarket()),dfmt01_9_pos,dfmt01_9_neg);
					} else {
						return getIOSignedDouble(Double.valueOf("0.0"),dfmt01_9_pos,dfmt01_9_neg);
					}
				} else{
					if (row.getTaxlotCurrentBookCostYieldToWorst() !=null) {
						return getIOSignedDouble(Double.valueOf(row.getTaxlotCurrentBookCostYieldToWorst()),dfmt01_9_pos,dfmt01_9_neg);
					} else{
						return getIOSignedDouble(Double.valueOf("0.0"),dfmt01_9_pos,dfmt01_9_neg);
					}
				}
			}				
		}
		
		//TLE-ORIG-YIELD-TO-MATURITY - TXLT_SET_BY_FUND_N_VW - ORIG_BK_COST_YLD_MTY  - PIC -9(1)V9(8). [Record Length : 632]
		//Added isOldFormatEnabled() to produce 651 record length for PROD.
		private String getEffectiveYieldToMaturityRate(TaxlotData row){
			if(dfc.isOldFormatEnabled()){
				Double originalYieldToMaturity = row.getTaxlotOriginalBookCostYieldToMaturity();
				if(originalYieldToMaturity!=null)
					return getIOSignedDouble(originalYieldToMaturity,dfmt01_8_pos,dfmt01_8_neg);
				else
					return getIOSignedDouble(Double.valueOf("0.0"),dfmt01_8_pos,dfmt01_8_neg);
			}else{
				Double originalYieldToMaturity = row.getTaxlotOriginalBookCostYieldToMaturity();
				if(originalYieldToMaturity!=null)
					return getIOSignedDouble(originalYieldToMaturity,dfmt01_9_pos,dfmt01_9_neg);
				else
					return getIOSignedDouble(Double.valueOf("0.0"),dfmt01_9_pos,dfmt01_9_neg);
			}
		}		
		
		//TLE-ORIG-YIELD-TO-MATURITY - TXLT_SET_BY_FUND_N_VW - EFF_YLD_TO_MTY_R  - PIC -9(1)V9(8). [Record Length : 632]
		//Added isOldFormatEnabled() to produce 651 record length for PROD.
		private String getEffectiveYieldToMaturityRateforIO(TaxlotData row){
			if(dfc.isOldFormatEnabled()){
				Double effYieldToMaturity = row.getEffectiveYieldToMaturityRate();
				if(effYieldToMaturity!=null)
					return getIOSignedDouble(effYieldToMaturity,dfmt01_8_pos,dfmt01_8_neg);
				else
					return getIOSignedDouble(Double.valueOf("0.0"),dfmt01_8_pos,dfmt01_8_neg);
			}else{
				Double effYieldToMaturity = row.getEffectiveYieldToMaturityRate();
				if(effYieldToMaturity!=null)
					return getIOSignedDouble(effYieldToMaturity,dfmt01_9_pos,dfmt01_9_neg);
				else
					return getIOSignedDouble(Double.valueOf("0.0"),dfmt01_9_pos,dfmt01_9_neg);
			}
		}		
		
		//TLE-INT-PAYMENT - TXLT_SET_BY_FUND_N_VW - LOCL_INT_PMT_A - PIC -9(15)V9(2). [Record Length : 650]
		private String getInterestPaymentAmountLocal(TaxlotData row){
			if (row.getInterestPaymentAmountLocal() !=null)
				return getIOSignedDouble(row.getInterestPaymentAmountLocal(),dfmt15_2_pos,dfmt15_2_neg);
			else
				return getIOSignedDouble(Double.valueOf("0.0"),dfmt15_2_pos,dfmt15_2_neg);
		}
		private String getFormattedLong(Long value, DecimalFormat fmt) {
			
			return (value!= null?fmt.format(value):fmt.format(0));
		}
		
		private String getFormattedText(String value,String pattern){
			if(StringUtils.isBlank(value))
				return String.format(pattern," ");
			else
				return String.format(pattern,value.trim());
		}
		private String getIOSignedDouble(Double value,DecimalFormat dft1,DecimalFormat dft2){
			String result = "";
			if(value < 0.0){
				result =  replaceDecimalWithZero(dft2.format(value));
			}else{
				result = replaceDecimalWithZero(dft1.format(value));
			}
			// This is to address the final result:0 showing -ve sign. This comes when we get the neg value, but since it is taken only until specified places after decimal, the final result is 0.
			if(result.matches("[+-]0+")){
		       	result = result.replaceFirst("-", "+");		    	
		    }
			return result;		
		}
		private String getUnsignedDouble(Double value,DecimalFormat dft){
			return replaceDecimalWithZero(dft.format(value));
		}
		private String replaceDecimalWithZero(String strObj){
			return strObj.replace(".", "");
		}		
				
		

		public static void main(String args[]) {
			System.out.println("in retail funds BOD");
			System.out.println(Runtime.getRuntime().freeMemory()/1024);
			//String appName = "BODTAXLOTFEEDMM";
			String appName = "BODTAXLOTFEEDFIE";
			new Initialize();
			BODTaxlotFeed bodTaxlotFeed = new BODTaxlotFeed();
			ConfigDBAccess.getStartupApps(appName, null);
			bodTaxlotFeed.setDFC(new DALFeedConfiguration(appName));
			bodTaxlotFeed.setFeedAppName(appName);
			
			try{				
				long startTime = System.currentTimeMillis();
				//bodTaxlotFeed.processMessage(60013); // MM
				bodTaxlotFeed.processMessage(61024);//FIE 
				long endTime = System.currentTimeMillis();
				Util.log("Time taken for TAXLOT feed Generation : "+(endTime-startTime)/1000 + " seconds");
				System.out.println("its done processing");
				System.exit(0);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
	}

