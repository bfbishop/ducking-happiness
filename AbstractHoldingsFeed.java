package com.fidelity.dal.fundacct.feed.app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StopWatch;

import com.fidelity.dal.fundacct.dalapi.beans.FundPortfolioData;
import com.fidelity.dal.fundacct.dalapi.beans.PositionData;
import com.fidelity.dal.fundacct.dalapi.criteria.Criteria;
import com.fidelity.dal.fundacct.dalapi.criteria.FundPortfolioCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.Operator;
import com.fidelity.dal.fundacct.dalapi.criteria.OrderByCriteria;
import com.fidelity.dal.fundacct.feed.admin.ApplicationContext;
import com.fidelity.dal.fundacct.feed.app.beans.HoldingsVO;
import com.fidelity.dal.fundacct.feed.app.decorator.HoldingsDecorator;
import com.fidelity.dal.fundacct.feed.bean.DALCalendar;
import com.fidelity.dal.fundacct.feed.bean.DALFeedConfiguration;
import com.fidelity.dal.fundacct.feed.bean.FileWriter;
import com.fidelity.dal.fundacct.feed.bean.FundPortfolio;
import com.fidelity.dal.fundacct.feed.bean.DALCalendar.SourceCalendar;
import com.fidelity.dal.fundacct.feed.framework.feed.DBUtil;
import com.fidelity.dal.fundacct.feed.framework.feed.FeedsUtil;
import com.fidelity.dal.fundacct.feed.framework.feed.RowHandler;
import com.fidelity.dal.fundacct.feed.framework.jms.JMSMessageProcessorBase;
import com.fidelity.dal.fundacct.feed.framework.util.Constants;
import com.fidelity.dal.fundacct.feed.framework.util.TranslationConstants;
import com.fidelity.dal.fundacct.feed.framework.util.Util;

public abstract class AbstractHoldingsFeed extends JMSMessageProcessorBase {

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

	
	private final String[] prcCodes = {"B","E","F","H","K","R"};
	private final String[] prcCodes1 = {"B","C","E","F","H","K","R","Z"};
	private final String[] IWTWDW = {"IW","TW","DW"};
	private final String[] CWDWIWTW = {"CW","DW","IW","TW"};
	protected final DateTimeFormatter dft1 = DateTimeFormat.forPattern("yyyy-MM-dd");
	protected final DateTimeFormatter dft2 = DateTimeFormat.forPattern("yyyyMMdd");
	protected final DateTimeFormatter dft3 = DateTimeFormat.forPattern("dd-MMM-yyyy");
	protected final String BAD_DATE = "99990101";
	protected final String PRNC_A = "PRNC-A";
	protected final String ACRU_INCM = "ACRU-INCM";
	protected final String ORGNL_SETL_D = "ORGNL-SETL-D";
	protected final String TOT_BS_TAX_CDAY_BASIS = "TOT-BS-TAX-CDAY-BASIS";
	protected final String TOT_LCL_TAX_CDAY_BASIS = "TOT-LCL-TAX-CDAY-BASIS";
	protected final String TX_TOT_OID_BASIS_AT_PURCH = "TX-TOT-OID-BASIS-AT-PURCH";
	protected final String TX_SUM_CURR_D_CUM_AMORT = "TX-SUM-CURR-D-CUM-AMORT";
	protected final String TX_SUM_BEG_FIS_CUM_AMORT = "TX-SUM-BEG-FIS-CUM-AMORT";
	protected final String CURR_EXCH_R = "CURR-EXCH-R";
	protected final String AMORTIZATION_ACTIVE_IND  = "AMORTIZATION-ACTIVE-IND";
	protected final String TOT_QTY_PAR = "TOT-QTY-PAR";
	protected final String SETL_SHRS = "SETL-SHRS";
	protected final String CST_YIELD_TO_WORST_MM = "CST-YIELD-TO-WORST-MM";
	protected final String CST_YIELD_TO_WORST_FIE = "CST-YIELD-TO-WORST-FIE";
	protected final String CST_YIELD_TO_MAT = "CST-YIELD-TO-MAT";
	protected final String CST_YIELD_TO_WRST = "CST-YIELD-TO-WRST";
	protected final String TOT_LCL_ORIG_FACE_AMT = "TOT-LCL-ORIG-FACE-AMT";
	protected final String CST_BS_A = "CST-BS-A";
	protected final String BK_PRIOR_AMRT_AMT = "BK-SUM-PRIOR-D-CUM-AMORT";
	protected final String TX_PRIOR_AMRT_AMT = "TX-SUM-PRIOR-D-CUM-AMORT";
	protected final String GROS_INT_ACRU_BS_A = "GROS-INT-ACRU-BS-A";
	protected final String GROSS_ACRD_INT_A = "GROSS-ACRD-INT-A";
	protected final String INT_NORMAL_ACCR_DAYS = "ACRU_DAYS";
	protected final String CURR_ACR_THR_DAY = "CURR_ACR_THR_DAY";
	protected final String PRIOR_ACR_THR_DAY = "PRIOR_ACR_THR_DAY";
	protected final String HEADER_IDENTIFIER = "IMAHDR";
	protected final String TRAILER_IDENTIFIER = "IMATRL";
	protected final int POSITION_BATCH_SIZE = 50000;
	protected final int FETCH_SIZE = 50000;
	protected final String[] NO_FUND_N = {"-1"};
	protected final String[] excludedAssetType = {"CC","CU","CW","FL","FC"};
	protected final String[] INCLUDED_DUMMY_CUSIPS = {"FUTINDEXG","CCPINDEXG","CNYCNHADJ"};
	protected final String GET_ALL_HOLDINGS = "PKG_FEED_HOLDINGS.GET_ALL_HOLDINGS";
	protected final String GET_ALL_MATU_HOLDINGS = "PKG_FEED_HOLDINGS.GET_ALL_MATU_HOLDINGS";
	protected final String GET_PRIOR_AMRT_VALUES = "PKG_FEED_HOLDINGS.GET_PRIOR_AMRT_VALUES";
	protected final String GET_PRIOR_AMRT_VALUES_NODAY1 = "PKG_FEED_HOLDINGS.GET_PRIOR_AMRT_VALUES_NODAY1";
	protected final String GET_ACR_THR_DAYS = "PKG_FEED_HOLDINGS.GET_ACR_THR_DAYS";
	protected final String GET_ALL_OVERNIGHT_MAT_HOLDINGS = "PKG_FEED_HOLDINGS.GET_ALL_OVERNIGHT_MAT_HOLDINGS";
	protected final String GET_DATA_FRM_TXLT = "PKG_FEED_HOLDINGS.GET_DATA_FRM_TXLT";
	protected final String GET_TRD_ID_DATA = "PKG_FEED_HOLDINGS.GET_TRD_ID_DATA";
	protected final String USR_STRG_KEY_ARRAY = "USR_STRG_KEY_ARRAY";
	protected HoldingsDecorator focasDecorator = null;
	protected HoldingsDecorator ioDecorator = null;
	protected StopWatch stopWatch = null;
	protected DALCalendar dalCalendar = null;
	protected int recordCount = 0;
	protected int asetTypeSkippedCount=0;
	protected int dummyCusipCount=0;
	protected int eodMaturedRecordCount=0;
	protected int buyMaturedRecordCount=0;
	protected int excludedMaturedRecordCount=0;
	protected Double cFLegShrQ = null;
	protected Double cFLegSetlShares = null;
	protected String cFLegKey = null;
	protected Double mcFLegShrQ = null;
	protected Double mcFLegSetlShares = null;
	protected String mcFLegKey = null;
	protected FileWriter fwGeode;
	
	public Date getAsOfDay(){
		return getToday();
	}
	
	public void setStopWatch(StopWatch stopWatch){
		this.stopWatch = stopWatch;
	}
	
	public void setDalCalendar(DALCalendar dalCalendar){
		this.dalCalendar = dalCalendar;
	}

	/* Cleans all Instance variables */
	protected void cleanUp(){
		
		focasDecorator = null;
		ioDecorator = null;
		stopWatch = null;
		dalCalendar = null;
		recordCount = 0;
		asetTypeSkippedCount=0;
		dummyCusipCount=0;
		eodMaturedRecordCount=0;
		buyMaturedRecordCount=0;
		excludedMaturedRecordCount=0;
		cFLegShrQ = null;
		cFLegSetlShares = null;
		cFLegKey = null;
		mcFLegShrQ = null;
		mcFLegSetlShares = null;
		mcFLegKey = null;
	}
	/**
	 * @param - Event ID the feed program is tuned to listen
	 */
	protected abstract void processMessage(int event) throws Exception ;
	
	/**
	 * 
	 * @param srcCode
	 * @param exttKndCode
	 * @param runDay
	 * @return
	 * @throws Exception
	 */
	protected abstract boolean doFeed(String srcCode, String exttKndCode, Date runDay,FileWriter fw) throws Exception;
	
	protected Map<String, Double> getPriorPosnAmrtData(String SQL,Object[] params) throws Exception{
		
		Util.log(this,"Begin calling "+ SQL);
		Util.log(this,"Parametres being passed to "+SQL+ " = "+FeedsUtil.dump(params));
		Long start = System.currentTimeMillis();
		final Map<String,Double> map = new HashMap<String,Double>();
		try{
			
			DBUtil.packageQueryResultSet(SQL, params, FETCH_SIZE,
									new RowHandler() {
										@Override
										public void handleRow(ResultSet rs) throws SQLException {
											try{
												while(rs.next()){
												
													String[] tokens = {
															Util.createFixedLengthString(rs.getLong("FUND_N"), Constants.LEN_FUND_NUMBER),
															Util.createFixedLengthString(rs.getLong("PORT_FUND_N"),Constants.LEN_PORTFOLIO_ACCOUNT_NUMBER),
															Util.createFixedLengthString(rs.getString("LNG_SHT_C"),1),
															Util.createFixedLengthString(rs.getString("FMR_CUSP_N"), Constants.LEN_IO_CUSIP_NUMBER),
															Util.createFixedLengthString(rs.getBigDecimal("INV1_SCTY_DATE_N")!=null ? rs.getBigDecimal("INV1_SCTY_DATE_N").doubleValue():0.0, Constants.LEN_IO_CUSIP_DATE),
															Util.createFixedLengthString(rs.getBigDecimal("INV1_SCTY_QUAL_N")!=null ? rs.getBigDecimal("INV1_SCTY_QUAL_N").doubleValue():0.0, Constants.LEN_IO_CUSIP_QUAL),
															Util.createFixedLengthString(rs.getString("SRC_C"),7),
															Util.createFixedLengthString(rs.getString("CST_BASIS_C"),2)
														};
													String key = FeedsUtil.createKeyFromTokens(tokens);
													Double priorAmrtAmt = (rs.getBigDecimal("PRIOR_AMRT_AMT")!=null) ? rs.getBigDecimal("PRIOR_AMRT_AMT").doubleValue():0.0;
													map.put(key, priorAmrtAmt);
												}
											}catch(Exception e){
											
												throw new SQLException(e);
											}
										}
									});
		}catch(Exception e){
			
			Util.log(this,"Exception calling "+SQL+" with parameters "+FeedsUtil.dump(params),Constants.LOG_LEVEL_ERROR,e);
			throw e;
		}
		
		Long stop = System.currentTimeMillis();
		Util.log(this,"Total key/value pairs for Priorday Positions "+map.size()+" in "+(stop-start)+" milliseconds");
		return map;
	}
	
	/**
	 * 
	 * @param srcCode
	 * @param exttKndCode
	 * @param today
	 * @return
	 * @throws Exception
	 */
	protected boolean generateAndSendFeed(String srcCode, String exttKndCode, Date today) throws Exception{

		Util.log(this,"Begin Feed for "+srcCode+" and "+exttKndCode);
		
		boolean isFileGenerated = false;
		FileWriter fw = new FileWriter(dfc);
		dfc.setOutputLocalFileName(dfc.getOutputLocalFileName()+".geode");
		fwGeode = new FileWriter(dfc);
		try{
			isFileGenerated = doFeed(srcCode,exttKndCode,today,fw);
			if(isFileGenerated){
				sendFile(fw,dfc);
			}
		}catch(Exception e){
			
			isFileGenerated = false;
			throw e;
		}finally{
			Util.log(this,"Completed Feed generation for "+srcCode+" and "+exttKndCode+" with status "+(isFileGenerated?"SUCCESS":"FAILURE"));
			if(fw != null){
				fw.close(false);
				fwGeode.close(false);
			}
			fw=null;
		}
	    return isFileGenerated;
	}

	/**
	 * EOD and BOD has their own implementation
	 * @param argContainer
	 * @throws Exception
	 */
	protected abstract void callAndCacheFundNumbers(ArgumentContainer argContainer) throws Exception;

		
	
	
	/**
	 * 
	 * @param srcCodes
	 * @param exttCode
	 * @param strAsOfDate
	 * @return
	 * @throws Exception
	 */
	protected Collection<FundPortfolioData> getFundPortfolios(String srcCodes,String exttCode,String strAsOfDate) throws Exception {
		
		Util.log(this,"Starting fundPortfolio.getFundPortfolioSet");
		
		FundPortfolio fundPortfolio = (FundPortfolio)ApplicationContext.getContext().getBean("FundPortfolio");
		
		Criteria fundPortfolioCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addAsOfDateFilter(strAsOfDate, Operator.OPERATOR_EQ));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addExtractKindCodeFilter(exttCode, Operator.OPERATOR_EQ));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addSourceCodeFilter(srcCodes,Operator.OPERATOR_IN));

		// Sorting by Fund Number
		OrderByCriteria orderByCriteria = (OrderByCriteria)ApplicationContext.getContext().getBean("OrderByCriteria");
		orderByCriteria.addOrderByOn(PositionData.FIELD_fundNumber).asc();
	    Collection<FundPortfolioData> funds = fundPortfolio.getFundPortfolioSet(new String[0], fundPortfolioCriteria,orderByCriteria);
		
    	Util.log(this,"Fund portfolio record count:" + (funds != null ? funds.size():0));
    
		return funds;
	}
	
	
	/**
	 * 
	 * @param asOfDay
	 * @param srcCodes
	 * @param exttCode
	 * @return
	 * @throws Exception
	 */
	protected List<String> getFundNumbersFromPositions(DateTime asOfDay, String srcCodes,String exttCode) throws Exception{
		
		if(asOfDay == null || srcCodes == null || exttCode == null){
			
			Util.log(this,"One ore More required inputs missing for getFundNumbersFromPositions",Constants.LOG_LEVEL_ERROR,new IllegalArgumentException());
			return null;
		}
		
		List<String> fundNumbers = new ArrayList<String>();
		
		String sql = "SELECT DISTINCT P.FUND_N FROM POSN_SET_BY_FUND_N_VW P "+
				  "WHERE P.AS_OF_D = TO_DATE('"+asOfDay.toString(dft1)+"','YYYY-MM-DD') "+
				  "AND   P.EXTT_KND_C = '"+exttCode+"' "+
				  "AND   P.SRC_C = '"+srcCodes+"' "+
				  "ORDER BY P.FUND_N";
		
		List<DynaBean> rows = DBUtil.executeQuery(sql,null);
		Iterator<DynaBean> iterator = null;
		if(rows != null){
			iterator = rows.iterator();
			while(iterator.hasNext()){
				
				DynaBean row = iterator.next();
				Object obj = row.get("FUND_N");
				if(obj != null){
					
					fundNumbers.add(String.valueOf(obj).trim());
				}
			}
		}
			
		return fundNumbers;
	}
	
	protected Map<String,Double> getPriorPosnValues(Collection<DynaBean> results) throws Exception{

		Map<String,Double> map = new HashMap<String,Double>();
		Iterator<DynaBean> iterator = results.iterator();
		
		while(iterator.hasNext()){
			
			DynaBean row = (DynaBean)iterator.next();
			
			String[] tokens = {
								Util.createFixedLengthString(row.get("FUND_N"), Constants.LEN_FUND_NUMBER),
								Util.createFixedLengthString(row.get("PORT_FUND_N"),Constants.LEN_PORTFOLIO_ACCOUNT_NUMBER),
								Util.createFixedLengthString(row.get("LNG_SHT_C"),1),
								Util.createFixedLengthString(row.get("FMR_CUSP_N"), Constants.LEN_IO_CUSIP_NUMBER),
								Util.createFixedLengthString(row.get("INV1_SCTY_DATE_N")!=null ? ((BigDecimal)row.get("INV1_SCTY_DATE_N")).doubleValue():0.0, Constants.LEN_IO_CUSIP_DATE),
								Util.createFixedLengthString(row.get("INV1_SCTY_QUAL_N")!=null ? ((BigDecimal)row.get("INV1_SCTY_QUAL_N")).doubleValue():0.0, Constants.LEN_IO_CUSIP_QUAL),
								Util.createFixedLengthString(row.get("SRC_C"),7),
								Util.createFixedLengthString(row.get("CST_BASIS_C"),2)
							};
			String key = FeedsUtil.createKeyFromTokens(tokens);
			Double priorAmrtAmt = (row.get("PRIOR_AMRT_AMT")!=null) ? ((BigDecimal)row.get("PRIOR_AMRT_AMT")).doubleValue():0.0;
			map.put(key, priorAmrtAmt);
			iterator.remove();
		}
		return map;
	}
	
	/**
	 * Below method constructs Regular holdings for EOD and BOD
	 * @param argContainer
	 * @param acrThrDaysMap
	 * @param isBOD
	 * @param isMatured
	 * @return
	 * @throws Exception
	 */
	protected List<HoldingsVO> getRegularHoldings(ArgumentContainer argContainer,
												  Map<String,Map<String,Object>> acrThrDaysMap,
												  Map<String, Double> priorPosnMap,
												  Map<String,Map<String,Object>> txLtDataMap,
												  Map<String,Map<String,BigDecimal>> trdIdLookupData,
												  boolean isBOD,
												  boolean isMatured) throws Exception {
		
		stopWatch.start("Getting All Position Data");
			Collection<DynaBean> results = getAllPositions(argContainer.getSubFundNumbers(),
													   	   argContainer.getSubIOFundNumbers(),
													   	   argContainer.getRunDay(),
													   	   argContainer.getSrcCodes(),
													   	   argContainer.getIoSrcCodes(),
													   	   argContainer.getExttKndCode());
		stopWatch.stop();
	
		if(CollectionUtils.isEmpty(results)){
			
			return null;
		}
		stopWatch.start("Capture Long and Short Leg data");
			Map<String,Map<String,Object>> shortLegMap = captureShortLegData(results);
		stopWatch.stop();
		stopWatch.start("Construct Regular Holdings");
			List<HoldingsVO> holdings = constructHoldingsRecords(results,priorPosnMap,acrThrDaysMap,txLtDataMap,shortLegMap,trdIdLookupData,isBOD,isMatured);
		stopWatch.stop();		
		return holdings;
	}
	
	//FACDAL-2853
	protected Map<String,Map<String,Object>> captureShortLegData(Collection<DynaBean> results) {
		
		Map<String,Map<String,Object>> shortLegMap = new HashMap<String,Map<String,Object>>();
		Iterator<DynaBean> iterator = results.iterator();
		while(iterator.hasNext()){
			
			DynaBean row = iterator.next();
			Object obj = row.get("sourceCode");
			String sourceCode = (obj != null)?String.valueOf(obj).trim():null;
			if(isFocas(sourceCode)){
				
				continue; //Skip focas records
			}
			
			String key = createKeyForLeg(row);
			obj = row.get("investOneSecurityQualifier");
			if(obj != null){
				
				Double inv1SctyQual = ((BigDecimal)obj).doubleValue();
				if(inv1SctyQual == 1.0){

					Map<String,Object> tempMap = new HashMap<String,Object>();
					tempMap.put("LOCL_ORIG_BK_COST_A", row.get("localOriginalBookCostAmount"));
					tempMap.put("AMRT_COST_BS_A",row.get("amortizedCostBase"));
					tempMap.put("LOCL_AMRTD_TX_COST_A", row.get("localAmortizedTaxCostAmount"));
					tempMap.put("TX_AMRT_COST_BS_A",row.get("TOT-BS-TAX-CDAY-BASIS"));
					tempMap.put("TX_LOCL_AMRTD_TX_COST_A", row.get("TOT-LCL-TAX-CDAY-BASIS"));
					tempMap.put("ERND_AMRT_A",row.get("earnedAmortizationAmount"));
					tempMap.put("BK_SUM_BEG_FIS_CUM_AMORT",row.get("BK_SUM_BEG_FIS_CUM_AMORT"));
					shortLegMap.put(key,tempMap);
				}
			}
		}
		
		return shortLegMap;
	}

	/**
	 * Calls PKG_FEED_HOLDINGS.GET_ALL_HOLDINGS to get all the data required to construct Holdings
	 * @param allFundNumbers
	 * @param allIOFundNumbers
	 * @param runDay
	 * @param srcCodes
	 * @param IoSrcCodes
	 * @param exttKndCode
	 * @return
	 * @throws Exception
	 */
	protected Collection<DynaBean> getAllPositions(String[] allFundNumbers, String[] allIOFundNumbers, 
			 									 DateTime runDay, String srcCodes, String IoSrcCodes,String exttKndCode) throws Exception{		 

		Util.log(this,"Calling PKG_FEED_HOLDINGS.GET_ALL_HOLDINGS");
		String[] subFundNumbers = ArrayUtils.isNotEmpty(allFundNumbers)?allFundNumbers:NO_FUND_N;
		String[] subIOFundNumbers = ArrayUtils.isNotEmpty(allIOFundNumbers)?allIOFundNumbers:NO_FUND_N;
		Object[] params = new Object[6];
		params[0] = FeedsUtil.arrayToString(subFundNumbers); 
		params[1] = FeedsUtil.arrayToString(subIOFundNumbers);
		params[2] = runDay != null ? runDay.toString(dft3) : null;
		params[3] = srcCodes;
		params[4] = IoSrcCodes;
		params[5] = exttKndCode;
		Util.log(this,"Parameters for PKG_FEED_HOLDINGS.GET_ALL_HOLDINGS are "+FeedsUtil.dump(params));
		Collection<DynaBean> results = null;
		try{
			results = DBUtil.executePlQuery(GET_ALL_HOLDINGS, params, FETCH_SIZE);
		}catch(Exception e){
			
			Util.log(this,"Exception thrown calling GET_ALL_HOLDINGS with parameters "+FeedsUtil.dump(params),Constants.LOG_LEVEL_ERROR,e);
			throw e;
		}

		Util.log(this,"Total Positions returned = "+((results != null) ? results.size() : 0));
		return results;
	}

	/**
	 * Below method constructs Holdings value Objects
	 * @param results
	 * @param priorPosnMap
	 * @param acrThrDaysMap
	 * @param isBOD
	 * @param isMatured
	 * @return
	 * @throws Exception
	 */
	protected List<HoldingsVO> constructHoldingsRecords(Collection<DynaBean> results,
		  												Map<String, Double> priorPosnMap,
		  												Map<String,Map<String,Object>> acrThrDaysMap,
		  												Map<String,Map<String,Object>> txLtDataMap,
		  												Map<String,Map<String,Object>> shortLegMap,
		  												Map<String,Map<String,BigDecimal>> trdIdLookupData,
		  												boolean isBOD,boolean isMatured) throws Exception {

		List<HoldingsVO> holdings = new ArrayList<HoldingsVO>();
		
		Iterator<DynaBean> iterator = results.iterator();
		while(iterator.hasNext()){
			
			DynaBean row = iterator.next();
			if(row == null) continue;
			
			Object obj = row.get("sourceCode");
			String sourceCode = (obj != null)?String.valueOf(obj).trim():null;
			
			if(isFocas(sourceCode)){
			
				HoldingsVO focasRecord = buildFocasRecord(row,isMatured);
				if(focasRecord != null){
			
					holdings.add(focasRecord);
				}
			}else{
				HoldingsVO ioRecord = buildIORecord(row,priorPosnMap,acrThrDaysMap,txLtDataMap,shortLegMap,trdIdLookupData,isBOD,isMatured);
				if(ioRecord != null){
			
					holdings.add(ioRecord);
				}
			}
			iterator.remove();
		}
		
		return holdings;
	}
	
	protected HoldingsVO buildFocasRecord(DynaBean row,boolean maturedPosition) throws Exception{
		
		Object object = row.get("FMRCUSIPNumber");
		String fmrCusipN = (object != null) ? object.toString().trim():null;
//		if(ApplicationContext.getFocasDataConverter().isDummyCusip(fmrCusipN)){
//			dummyCusipCount++;
//			return null; //Skip the dummy cusips
//		}
		
		String issueCurrencyCode=null;
		
		object = row.get("FOCASSecurityDescription");
		String longName = (object != null) ? object.toString().trim():null; 
		if(StringUtils.isNotBlank(longName) && longName.length() > 30){
			
			longName = longName.substring(0,30);
		}
	
		object = row.get("FOCASTransactionCurrencyCode");
		if(object != null){
		
			issueCurrencyCode = ApplicationContext.getFocasDataConverter().convertAS400ToISO("CURR_C", String.valueOf(object).trim());
		}
		
		if(TranslationConstants.RETURN_VALUE_BADDATA.equals(issueCurrencyCode)){
			
			issueCurrencyCode = null;
		}
		
		//FACDAL-1432
		if(StringUtils.isBlank(issueCurrencyCode)){
			
			Util.log(this, "No matching CURRENCY-CODE found in FocasTranslator", Constants.LOG_LEVEL_WARNING,null);
		}
		
		HoldingsVO record = new HoldingsVO();
		// ----------- Below fields are not written to feed --------------
		record.setMaturedPosition(false);
		record.setFocas(true);
		object = row.get("shareParQuantity");
		Double shrQ = (object != null)?((BigDecimal)object).doubleValue():null;
		record.setOriginalShrParQnty(shrQ);
		object = row.get("fundNumber");
		Long fundNumber = (object != null)?((BigDecimal)object).longValue():null;
		record.setFundNumber(fundNumber);
		//---------------------------------------------------------------- 
		object = row.get("portfolioFundNumber");
		Long portFundNumber = (object != null)?((BigDecimal)object).longValue():null;
		record.setAcctNumber(portFundNumber); //ACCT-NUMBER
		
		object = row.get("FOCASSubAccountCode");
		String subAcctCode = (object != null)?object.toString().trim():null;
		record.setSubAcctCode(subAcctCode); //SUB-ACCT-CODE
		
		record.setFmrCusip(fmrCusipN); //FMR-CUSIP
		record.setSecondaryCusip(null); //SECONDARY-CUSIP. Always blank for FOCAS fund
		record.setTradeableCusip(null); //TRADEABLE-CUSIP. Place holder for future field. Always Null.
		
		object = row.get("taxlotLongShortCode");
		record.setLongShortInd((object != null)?object.toString().trim():null); //LONG-SHORT-IND
		
		object = row.get("maturePositionIndicator");
		record.setMaturedInd(maturedPosition ? "Y":(object != null ? object.toString().trim():null)); // MATURED-POSITION-FLAG
		record.setSecurityDesc(longName); // SECURITY-DESC
		
		object = row.get("originalBookCostAmount");
		Double totBaseCostBasis = (object != null) ? ((BigDecimal)object).doubleValue():null;
		record.setTotalBaseCostBasis(totBaseCostBasis); //TOT-BASE-COST-BASIS
		
		object = row.get("localOriginalBookCostAmount");
		Double totLclCostBasis = (object != null) ? ((BigDecimal)object).doubleValue():null;
		record.setTotalLCLCostBasis(totLclCostBasis);  //TOT-LCL-COST-BASIS
		
		record.setTotalQtyPar(shrQ); //TOT-QTY-PAR
		
		object = row.get("currentFaceAmount");
		Double totLclOrigFaceAmt = (object != null) ? ((BigDecimal)object).doubleValue():null;
		record.setTotalLCLOrigFaceAmt(totLclOrigFaceAmt); //TOT-LCL-ORIG-FACE-AMT
		
		object = row.get("amortizedCostBase");
		Double totBsBkCdayBasis = (object != null) ? ((BigDecimal)object).doubleValue():null;
		record.setTotalBsBookCDayBasis(totBsBkCdayBasis); //TOT-BS-BOOK-CDAY-BASIS
		
		
		object = row.get(TOT_BS_TAX_CDAY_BASIS);
		Double totBsTaxCdayBasis = (object != null) ? ((BigDecimal)object).doubleValue():null;
		record.setTotalBsTaxCDayBasis(totBsTaxCdayBasis);
		
		object = row.get("newBuyIndicator");
		record.setNewBuyInd((object != null)?object.toString().trim():null); //NEW-BUY-I
		
		object = row.get("marketPriceAmount");
		Double unitMktValue = (object != null) ? ((BigDecimal)object).doubleValue():null;
		record.setUnitMktValue(unitMktValue); //UNIT-MKT-VALUE
		
		object = row.get("unitOfCalculationCode");
		Double unitOfCalc = (object != null) ? ((BigDecimal)object).doubleValue():null;
		record.setUnitOfCalc(unitOfCalc); //UNIT-OF-CALC
		
		record.setCurrencyCode(issueCurrencyCode); //CURRENCY-CODE
		
		object = row.get("amortizationActiveIndicator");
		record.setAmortActiveInd((object != null)?object.toString().trim():null); //AMORT-ACTIVE-IND
		
		object = row.get("localAmortizedTaxCostAmount");
		Double totLclBkCDayBasis = (object != null) ? ((BigDecimal)object).doubleValue():null;
		record.setTotalLCLBookCDayBasis(totLclBkCDayBasis); //TOT-LCL-BOOK-CDAY-BASIS
		
		object = row.get(TOT_LCL_TAX_CDAY_BASIS);
		Double totLclTaxCdayBasis = (object != null) ? ((BigDecimal)object).doubleValue():null;
		record.setTotalLCLTaxCDayBasis(totLclTaxCdayBasis);
		
		object = row.get("nextCallPutPriceAmount");
		Double callPutPrice = (object != null) ? ((BigDecimal)object).doubleValue():null;
		record.setCallPutPrice(callPutPrice); //CALL-PUT1-PRICE
		
		if(maturedPosition){
		
			object = row.get("INCM_A");
			Double matGrossIntAcruAmt = (object != null) ? ((BigDecimal)object).doubleValue() : null;
			object = row.get("INCM_BASE_A");
			Double matGrossIntAcruAmtBase = (object != null) ? ((BigDecimal)object).doubleValue() : null;
			record.setTotalDenAccrCurrPeriod(matGrossIntAcruAmt); //TOT-DEN-ACCR-CURR-PERIOD
			record.setTotalUSAccrCurrPeriod(matGrossIntAcruAmtBase); // //TOT-US-ACCR-CURR-PERIOD
		}else{
		
			object = row.get("grossInterestAcruedAmount");
			Double totDenAccrCurrPeriod = (object != null) ? ((BigDecimal)object).doubleValue() : null;
			record.setTotalDenAccrCurrPeriod(totDenAccrCurrPeriod); //TOT-DEN-ACCR-CURR-PERIOD
			
			object = row.get("grossInterestAcruedAmountBase");
			Double totUsAccrCurrPeriod = (object != null) ? ((BigDecimal)object).doubleValue() : null;
			record.setTotalUSAccrCurrPeriod(totUsAccrCurrPeriod); // //TOT-US-ACCR-CURR-PERIOD
		}
		
		object = row.get("netDailyInterestAccruedAmount");
		Double totDenAccrDaily = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setTotalDenAccrDaily(totDenAccrDaily); //TOT-DEN-ACCR-DAILY
		
		object = row.get("TOT_US_ACCR_DAILY");
		Double totUsAccrDaily = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setTotalUSAccrDaily(totUsAccrDaily); //TOT-US-ACCR-DAILY
		
		object = row.get("INT_NORMAL_ACCR_DAYS");
		Double intNormAccrDays = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setIntNormalAccrDays(intNormAccrDays); //INT-NORMAL-ACCR-DAYS
		
		object = row.get("localInterestPurchasedAmount");
		Double totDenBoughtForCalc = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setTotalDenBoughtForCalc(totDenBoughtForCalc); //TOT-DEN-BOUGHT-FOR-CALC
		
		object = row.get("interestPurchaseAmountBase");
		Double totUsBoughtForCalc = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setTotalUSBoughtForCalc(totUsBoughtForCalc);  //TOT-US-BOUGHT-FOR-CALC
		
		object = row.get("amortizationAmountBase");
		Double bkSumCurrDCumAmrt = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setBookSumCurrDCumAmrt(bkSumCurrDCumAmrt);  //BK-SUM-CURR-D-CUM-AMORT
		
		object = row.get("BK_SUM_PRIOR_D_CUM_AMORT");
		Double bkSumPrDCumAmrt = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setBookSumPriorDCumAmrt(bkSumPrDCumAmrt); //BK-SUM-PRIOR-D-CUM-AMORT
		
		object = row.get("BK_SUM_BEG_FIS_CUM_AMORT");
		Double bkSumBegFiscCumAmrt = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setBookSumFiscalCumAmrt(bkSumBegFiscCumAmrt);//BK-SUM-BEG-FIS-CUM-AMORT
		
		object = row.get(TX_TOT_OID_BASIS_AT_PURCH);
		Double txTotOidBasisAtPurch = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setTaxTotOIDBasisAtPurch(txTotOidBasisAtPurch); //TX-TOT-OID-BASIS-AT-PURCH
		
		object = row.get(TX_SUM_CURR_D_CUM_AMORT);
		Double txSumCumDCumAmrt = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setTaxSumCurrDCumAmrt(txSumCumDCumAmrt); //TX-SUM-CURR-D-CUM-AMORT
		
		object = row.get("TX_SUM_PRIOR_D_CUM_AMORT");
		Double txSumPrDCumAmrt = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setTaxSumPriorDCumAmrt(txSumPrDCumAmrt);//TX-SUM-PRIOR-D-CUM-AMORT
		
		object = row.get(TX_SUM_BEG_FIS_CUM_AMORT);
		Double txSumBegFisCumAmrt = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setTaxSumFiscalCumAmrt(txSumBegFisCumAmrt); //TX-SUM-BEG-FIS-CUM-AMORT
		
		object = row.get("CST_YIELD_TO_WORST");
		Double cstYldToWrst = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setCstYieldToWorst(cstYldToWrst); //CST-YIELD-TO-WORST
		
		object = row.get("CST_YIELD_TO_MAT");
		Double cstYldToMat = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		record.setCstYieldToMat(cstYldToMat); //CST-YIELD-TO-MAT

		return record;	
	}
	
	protected HoldingsVO buildIORecord(DynaBean row,Map<String, Double> priorPosnMap,
			   						   Map<String,Map<String,Object>> acrThrDaysMap,
			   						   Map<String,Map<String,Object>> txLtDataMap,
			   						   Map<String,Map<String,Object>> shortLegMap,
			   						   Map<String,Map<String,BigDecimal>> trdIdLookupData,
			   						   boolean isBOD,
			   						   boolean maturedPosition) throws Exception {

		
		Object object = row.get("FMRCUSIPNumber");
		String fmrCusipN = (object != null) ? object.toString().trim():null;
		//FACDAL-3470 - Exclude all Dummy cusips, except FUTINDEXG, CCPINDEXG, and CNYCNHADJ 
		if(ApplicationContext.getFocasDataConverter().isDummyCusip(fmrCusipN) && !ArrayUtils.contains(INCLUDED_DUMMY_CUSIPS, fmrCusipN)){
			
			dummyCusipCount++;
			return null; //Skip the dummy cusips
		}
		
		object = row.get("shareParQuantity");
		Double shrQ = (object != null)?((BigDecimal)object).doubleValue():null;
		//FACDAL-2086
		if(!maturedPosition && (shrQ != null && shrQ == 0.0)){

			return null;
		}

		object = row.get("fundNumber");
		Long fundNumber = object != null ? ((BigDecimal)object).longValue():null;
		
		object = row.get("portfolioFundNumber");
		Long portFundNumber = object != null ? ((BigDecimal)object).longValue():null;
		
		object = row.get("sourceCode");
		String srcCode = object != null ? object.toString().trim():null;
		
		object = row.get("IOFUNDTYADJERNDINCM");
		Double ioFundTyAdjErndIncm = object != null ? ((BigDecimal)object).doubleValue():null;
		
		object = row.get("grossInterestAcruedAmount");
		Double grossIntAcruedAmt = object != null ? ((BigDecimal)object).doubleValue():null;
		
		object = row.get("grossInterestAcruedAmountBase");
		Double grossIntAccrAmtBase = object != null ? ((BigDecimal)object).doubleValue():null;
		//All local variables declared here
		object = row.get("priceCode");
		String prcCode = object != null ? object.toString().trim():null;
		
		object = row.get("SHT_NM");
		String secDesc = object != null ? object.toString().trim():null;
		if(StringUtils.isNotBlank(secDesc) && secDesc.length() > 30){
		
			secDesc = secDesc.substring(0,30);
		}
		
		object = row.get("ISS_CURR_C");
		String issueCurrencyCode = object != null ? object.toString().trim():null;
		
		object = row.get("investOneSecurityQualifier");
		Double IOSecQual = object != null ? ((BigDecimal)object).doubleValue():null;
		
		object = row.get("DEN_CURR_C");
		String denominatedCurrencyCode = (object != null)? object.toString().trim():null;
		
		object = row.get("INCM_CURR_C");
		String incomeCurrencyCode = (object != null) ? object.toString().trim() : null;
		
		object = row.get("SECD_ISMT_C");
		String secdIsmtCode = object != null ? object.toString().trim() : null;
		
		object = row.get("ASET_TY_C");
		String assetTypeCode = object != null ? object.toString().trim() : null;
		
		object = row.get("CL_TY_C");
		String clTyCode = object != null ? object.toString().trim() : null;
		
		object = row.get("NXT_CALL_PUT_PRC_A");
		Double callPutPrice = (object != null) ? Math.abs(((BigDecimal)object).doubleValue()):null; // FIX for jira FACDAL-2313
		
		object = row.get("CTRT_SIZE_N");
		Double ctrtSizeN = (object != null) ? ((BigDecimal)object).doubleValue():null;
		
		object = row.get("IO_CTG_C_LV_SEG");
		Long iOCTGCLVSEG = (object != null) ? ((BigDecimal)object).longValue():null;
		
		if(isExcludedAsetGroup(assetTypeCode)){
			asetTypeSkippedCount++;
			return null;
		}
		
		//FACDAL-2468
		if((StringUtils.isNotBlank(clTyCode) && ArrayUtils.contains(CWDWIWTW,clTyCode))&&(IOSecQual==1.0)){
		
			return null; //Exclude
		}
		
		//FACDAL-2853
		String key = createKeyForLeg(row);
		BigDecimal shortLclOrgBkCstA = null;
		BigDecimal shortLclAmrtdTxCstA = null;
		BigDecimal shortTxLclAmrtdTxCstA = null;
		BigDecimal shortErnAmort = null;
		BigDecimal shortBkSumCurrDCumAmort = null;
		BigDecimal shortAmortizedCostBase = null;
		if(shortLegMap != null){
			Map<String,Object> tempMap = shortLegMap.get(key);
			if(tempMap != null){
				
				object = tempMap.get("LOCL_ORIG_BK_COST_A");
				shortLclOrgBkCstA = object != null ? (BigDecimal)object : new BigDecimal("0.0");
				object = tempMap.get("LOCL_AMRTD_TX_COST_A");
				shortLclAmrtdTxCstA = object != null ? (BigDecimal)object : new BigDecimal("0.0");
				object = tempMap.get("TX_LOCL_AMRTD_TX_COST_A");
				shortTxLclAmrtdTxCstA = object != null ? (BigDecimal)object : new BigDecimal("0.0");
				object = tempMap.get("ERND_AMRT_A");
				shortErnAmort = object != null ? (BigDecimal)object : null;
				object = tempMap.get("BK_SUM_BEG_FIS_CUM_AMORT");
				shortBkSumCurrDCumAmort = object != null ? (BigDecimal)object : null;
				object = tempMap.get("AMRT_COST_BS_A");
				shortAmortizedCostBase = object != null ? (BigDecimal)object : null;
			}
		}
		object = row.get("localAmortizedTaxCostAmount");
		BigDecimal longLclAmrtdTxCstA = (object != null) ? (BigDecimal)object:null;
		
		object = row.get(TOT_LCL_TAX_CDAY_BASIS);
		BigDecimal longTxLclAmrtdTxCstA = (object != null) ? (BigDecimal)object : null;
		
		//Below fields used for FACDAL-2853
		object = row.get("localOriginalBookCostAmount");
		BigDecimal longLclOrgnlBkCstAmt = (object != null) ? ((BigDecimal)object):new BigDecimal("0.0");
		object  = row.get("amortizedCostBase");
		BigDecimal longAmortizedCostBase = (object != null) ? ((BigDecimal)object):new BigDecimal("0.0");
		
		//Below keys are used for various lookups
		Date orgnlSetlDate = null;
		BigDecimal cstYieldToMatBD = null;
		BigDecimal cstYldToWrstBD = null;
		BigDecimal totalLCLOrigFaceAmtBD = null;
		BigDecimal cstBsABD = null;
		BigDecimal partialTotLclCostBasis = null;
		BigDecimal partialTotBaseCostBasis = null;
		String txltKey = getTxltKeyFrmPosition(row);
		if(txLtDataMap != null){
			
			Map<String,Object> map = txLtDataMap.get(txltKey);
			if(map != null){
				
				object = map.get(ORGNL_SETL_D);
				orgnlSetlDate = object != null ? (Date)object : null;
				
				object = map.get("CST-YLD-TO-MAT");
				cstYieldToMatBD = object != null ? (BigDecimal)object : null;
				
				object = map.get("CST-YLD-TO-WRST");
				cstYldToWrstBD = object != null ? (BigDecimal)object : null;
				
				object = map.get(TOT_LCL_ORIG_FACE_AMT);
				totalLCLOrigFaceAmtBD = object != null ? (BigDecimal)object : null;
				
				object = map.get(CST_BS_A);
				cstBsABD = object != null ? (BigDecimal)object : null;
				
				object = map.get("TOT-LCL-COST-BASIS-PARTIAL");
				partialTotLclCostBasis = object != null ? (BigDecimal)object : null;
				
				object = map.get("TOT-BASE-COST-BASIS-PARTIAL");
				partialTotBaseCostBasis = object != null ? (BigDecimal)object : null;
			}
		}
		//FACDAL-1726
		Double cstYieldToMat = null;
		if(cstYieldToMatBD != null){
						
			cstYieldToMat = getCstYieldToMat(shrQ,cstYieldToMatBD);
		}
		Double cstBsA = cstBsABD != null ? cstBsABD.doubleValue() : null;
		
		String swapKey = getSwapLegKey(row);
		Double fLegShrQ = shrQ;
		object = row.get("settledParSharesQuantity");
		Double fLegSetlShares = object != null ? ((BigDecimal)object).doubleValue():null;
		
		if(maturedPosition){
		
			if(StringUtils.equals(mcFLegKey, swapKey)){
		
				fLegShrQ = mcFLegShrQ;
				fLegSetlShares = mcFLegSetlShares;
			}else{
				mcFLegKey = swapKey;
				mcFLegShrQ = fLegShrQ;
				mcFLegSetlShares = fLegSetlShares;
		}}else{
		
			if(StringUtils.equals(cFLegKey, swapKey)){
		
				fLegShrQ = cFLegShrQ;
				fLegSetlShares = cFLegSetlShares;
			}else{
		
				cFLegKey = swapKey;
				cFLegShrQ = fLegShrQ;
				cFLegSetlShares = fLegSetlShares;
			}
		}
		
		Double sdexHoldShares = (fLegShrQ != null)? Math.abs(fLegShrQ):0.0;
		String posnKey = getPositionKey(row);
		String shortLegPosnKey = getShortLegPosnKey(row);
		String faKey = FeedsUtil.createKeyFromTokens(new String[]{posnKey,Constants.COST_BASIS_CODE_FA});
		String shortFaKey = FeedsUtil.createKeyFromTokens(new String[]{shortLegPosnKey,Constants.COST_BASIS_CODE_FA});
		String a1Key = FeedsUtil.createKeyFromTokens(new String[]{posnKey,Constants.COST_BASIS_CODE_A1});
		Double longBkPriorAmrtValue = calculatePriorPosnValue(priorPosnMap,faKey);
		Double shortBkPriorAmrtValue = calculatePriorPosnValue(priorPosnMap,shortFaKey);
		Double txPriorAmrtValue = calculatePriorPosnValue(priorPosnMap,a1Key);
		
		//FACDAL-2029
		Date currAcrThrDay = null;
		Date priorAcrThrDay = null;
		if(acrThrDaysMap != null){
		
			Map<String,Object> tempMap = acrThrDaysMap.get(String.valueOf(fundNumber).trim());
			if(tempMap != null){
		
				currAcrThrDay = (tempMap.get(CURR_ACR_THR_DAY) != null) ? (Date)tempMap.get(CURR_ACR_THR_DAY):null;
				priorAcrThrDay = (tempMap.get(PRIOR_ACR_THR_DAY) != null) ? (Date)tempMap.get(PRIOR_ACR_THR_DAY) : null;
			}
		}

		object = row.get("taxlotLongShortCode");
		String longShortCode = (object != null)?object.toString().trim():null;

		object = row.get("holdingDate");
		Date holdingDate = object != null ? (Date)object : null;
		
		object = row.get("extractKindCode");
		String exttCode = object != null ? object.toString().trim():null;
		
		object = row.get("currencyExchangeRate");
		Double currencyExchangeRate = object != null ? ((BigDecimal)object).doubleValue() : null;
		
		//FACDAL-2639
		Double matIOFUNDTYADJERNDINCM = null;
		Double matCurrExchR = null;
		Date contStlmD = null;
		Double matGrossIntAcruAmt = null;
		Double matGrossIntAcruAmtBase= null;
		if(maturedPosition){
		
			object = row.get("IO_FUND_TY_ADJ_ERND_INCM");
			matIOFUNDTYADJERNDINCM = (object != null) ? ((BigDecimal)object).doubleValue() : null;
			object = row.get("CURR_EXCH_R");
			matCurrExchR = (object != null) ? ((BigDecimal)object).doubleValue() : null;
			object = row.get("CONT_STLM_D");
			contStlmD = (object != null) ? ((Date)object) : null;
			object = row.get("INCM_A");
			matGrossIntAcruAmt = (object != null) ? ((BigDecimal)object).doubleValue() : null;
			object = row.get("INCM_BASE_A");
			matGrossIntAcruAmtBase = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		}
		
		object = row.get("unitOfCalculationCode");
		Double unitOfCalcC = (object != null) ? ((BigDecimal)object).doubleValue():null;
		
		//FACDAL-[2019,2477,2988],3275
		if(StringUtils.isNotBlank(clTyCode)){
					
			if(StringUtils.equals("FT",clTyCode.trim())){
						
				if(iOCTGCLVSEG != null && iOCTGCLVSEG.longValue() != 60){
							
					BigDecimal bd1 = ctrtSizeN != null ? new BigDecimal(ctrtSizeN.toString()) : new BigDecimal("0.0");
					BigDecimal bd2 = unitOfCalcC != null ? new BigDecimal(unitOfCalcC.toString()) : new BigDecimal("0.0");
					unitOfCalcC = bd1.multiply(bd2).doubleValue();
				}
			}else if(StringUtils.equals("O", clTyCode.trim())){
						
				if(StringUtils.isNotBlank(prcCode) && StringUtils.equals("D",prcCode.trim())){
						
					unitOfCalcC = ctrtSizeN;
				}
			}
		}
		
		HoldingsVO record = new HoldingsVO();
		//-------- Below fields are NOT written to feed. These are used for calculations only ----------------
		object = row.get("originalBookCostAmount");
		Double orgnLBkCostAmt = object != null ? ((BigDecimal)object).doubleValue() : null;
		record.setOriginalBookCostAmount(orgnLBkCostAmt);
		
		object = row.get("currentFaceAmountBase");
		Double currFaceAmtBs = object != null ? ((BigDecimal)object).doubleValue() : null;
		record.setCurrentFaceAmountBase(currFaceAmtBs);
		record.setOriginalLSInd(longShortCode);
		record.setMaturedPosition(maturedPosition);
		record.setFocas(false);
		record.setOriginalShrParQnty(shrQ);
		record.setFundNumber(fundNumber);
		//-----------------------------------------------------------------------------------------------------
		
		record.setAcctNumber(portFundNumber); //ACCT-NUMBER
		
		record.setSubAcctCode("00") ; //SUB-ACCT-CODE
		record.setFmrCusip(fmrCusipN); //FMR-CUSIP
		record.setSecondaryCusip(secdIsmtCode); //SECONDAY-CUSIP
		record.setLongShortInd(getLongShortInd(assetTypeCode,shrQ,longShortCode)); //LONG-SHORT-IND
		
		object = row.get("maturePositionIndicator");
		String matPosnInd = object != null ? object.toString().trim():null;
		record.setMaturedInd(maturedPosition ? "Y":matPosnInd); //MATURED-POSITION-FLAG
		record.setSecurityDesc(secDesc); //SECURITY-DESC
		record.setTotalBaseCostBasis(calculateTotalBaseCostBasis(shrQ,currencyExchangeRate,clTyCode,cstBsA,longLclOrgnlBkCstAmt,shortLclOrgBkCstA,partialTotBaseCostBasis,unitOfCalcC)); //TOT-BASE-COST-BASIS
		record.setTotalLCLCostBasis(calculateTotLCLCostBasis(clTyCode,shrQ,longLclOrgnlBkCstAmt,shortLclOrgBkCstA,partialTotLclCostBasis,unitOfCalcC)); //TOT-LCL-COST-BASIS
		Double shareParQuantity = calculateShrParQnty(shrQ,assetTypeCode,sdexHoldShares);
		shareParQuantity = negateForSwapShortLeg(IOSecQual,assetTypeCode,shareParQuantity); // Fix for jira FACDAL-2314
		record.setTotalQtyPar(shareParQuantity);  //TOT-QTY-PAR
		//FACDAL-2046 
		Double totalLCLOrigFaceAmt = totalLCLOrigFaceAmtBD != null ? totalLCLOrigFaceAmtBD.doubleValue():null;
		record.setTotalLCLOrigFaceAmt(totalLCLOrigFaceAmt);//TOT-LCL-ORIG-FACE-AMT
		record.setTotalBsBookCDayBasis(calculateBSCDayBasisAmount(clTyCode,
				  												  shrQ,
				  												  currencyExchangeRate,
				  												  longLclAmrtdTxCstA,
				  												  shortLclAmrtdTxCstA,
				  												  longAmortizedCostBase)); //TOT-BS-BOOK-CDAY-BASIS
		
		object = row.get(TOT_BS_TAX_CDAY_BASIS);
		BigDecimal cDayTxBasisAmt = (object != null) ? ((BigDecimal)object) : null;
 
		object = row.get(TOT_QTY_PAR);
		Double txShrQ = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		
		object = row.get(CURR_EXCH_R);
		Double txCurrExchR = (object != null) ? ((BigDecimal)object).doubleValue() : null;
		
		
		//FACDAL-2201
		if(maturedPosition){
		
			record.setTotalBsTaxCDayBasis(cDayTxBasisAmt!=null ? cDayTxBasisAmt.doubleValue():null);
		}else if(StringUtils.equals(exttCode,Constants.MSG_SUBTYPE_BOD)){
		
			
			record.setTotalBsTaxCDayBasis(calculateBSCDayBasisAmount(clTyCode,
																 	txShrQ,
																 	txCurrExchR,
																 	longTxLclAmrtdTxCstA,
																 	shortTxLclAmrtdTxCstA,
																 	cDayTxBasisAmt));//TOT-BS-TAX-CDAY-BASIS
		}
		
		object = row.get("newBuyIndicator");
		String newBuyInd = (object != null)?object.toString().trim():null;
		record.setNewBuyInd(maturedPosition ? "N":newBuyInd); //NEW-BUY-IND. FACDAL - 2053,2510
		
		object = row.get("originalBookCostAmount");
		Double orgnlBkCstAmt = (object != null) ? ((BigDecimal)object).doubleValue():null;
		
		object = row.get("pricePriceFile");
		Double pricePriceFile = (object != null) ? ((BigDecimal)object).doubleValue():null;
		record.setUnitMktValue(getUnitMktValue(srcCode,
											   longAmortizedCostBase,shortAmortizedCostBase,
											   shrQ,
											   unitOfCalcC,currencyExchangeRate,pricePriceFile,
											   orgnlBkCstAmt,clTyCode,maturedPosition));//UNIT-MKT-VALUE
		
		record.setUnitOfCalc(unitOfCalcC); //UNIT-OF-CALC-C
		record.setCurrencyCode(issueCurrencyCode); //CURRENCY-CODE
		
		object = row.get("amortizationActiveIndicator");
		record.setAmortActiveInd((object != null)?object.toString().trim():null); //AMORT-ACTIVE-IND

		//TOT-LCL-BOOK-CDAY-BASIS
		Double totalBkLclAmt = calculateTotLclCdayBasis(clTyCode,shrQ,longLclAmrtdTxCstA,shortLclAmrtdTxCstA);
		totalBkLclAmt = negateForSwapShortLeg(IOSecQual,assetTypeCode,totalBkLclAmt); //Fix for jira FACDAL-2314. Negate Swap Short Leg
		record.setTotalLCLBookCDayBasis(totalBkLclAmt); 
		
		//TOT-LCL-TAX-CDAY-BASIS
		//FACDAL-2201
		if(maturedPosition){
		
			record.setTotalLCLTaxCDayBasis(longTxLclAmrtdTxCstA != null ? longTxLclAmrtdTxCstA.doubleValue():null);
		}else if(StringUtils.equals(exttCode,Constants.MSG_SUBTYPE_BOD)){
		
			Double lclTxCdayBasisAmt = calculateTotLclCdayBasis(clTyCode,txShrQ,longTxLclAmrtdTxCstA,shortTxLclAmrtdTxCstA);
			Double lclTxCDayBasisAmt = negateForSwapShortLeg(IOSecQual, assetTypeCode, lclTxCdayBasisAmt);
			record.setTotalLCLTaxCDayBasis(lclTxCDayBasisAmt);
		}
		
		record.setCallPutPrice(callPutPrice); //CALL-PUT1-PRICE
		
		//FACDAL-2621 - TOT-DEN-ACCR-CURR-PERIOD, TOT-US-ACCR-CURR-PERIOD
		if(maturedPosition){
		
			record.setTotalDenAccrCurrPeriod(matGrossIntAcruAmt);
			record.setTotalUSAccrCurrPeriod(matGrossIntAcruAmtBase);
		}else if((StringUtils.isNotBlank(prcCode) && ArrayUtils.contains(prcCodes, prcCode))){
		
			record.setTotalDenAccrCurrPeriod(grossIntAcruedAmt); //TOT-DEN-ACCR-CURR-PERIOD
			record.setTotalUSAccrCurrPeriod(grossIntAccrAmtBase); //TOT-US-ACCR-CURR-PERIOD
		}
		
		//FACDAL-2028,2051,2339,2201,2453,2614,2639
		if(maturedPosition){
					
			record.setTotalDenAccrDaily(matIOFUNDTYADJERNDINCM); //TOT-DEN-ACCR-DAILY
			record.setTotalUSAccrDaily(calculateTotalUSAccrDaily(matIOFUNDTYADJERNDINCM,
																(currencyExchangeRate!=null)?currencyExchangeRate:matCurrExchR)); //TOT-US-ACCR-DAILY
			record.setCstYieldToMat(null); //CST-YIELD-TO-MAT
			record.setCstYieldToWorst(null); //CST-YIELD-TO-WORST
		}else{
		
			if(StringUtils.isNotBlank(prcCode) && ArrayUtils.contains(prcCodes, prcCode)){

				record.setTotalDenAccrDaily(ioFundTyAdjErndIncm); //TOT-DEN-ACCR-DAILY
				record.setTotalUSAccrDaily(calculateTotalUSAccrDaily(ioFundTyAdjErndIncm,currencyExchangeRate)); //TOT-US-ACCR-DAILY
		}else{
						
			record.setTotalDenAccrDaily(0.0); //TOT-DEN-ACCR-DAILY
			record.setTotalUSAccrDaily(0.0); //TOT-US-ACCR-DAILY
		}
		
		//FACDAL-2712
		if(StringUtils.isNotBlank(prcCode) && ArrayUtils.contains(prcCodes1, prcCode)){
		
			record.setCstYieldToMat(cstYieldToMat); //CST-YIELD-TO-MAT
			if(cstYldToWrstBD != null){
				
				record.setCstYieldToWorst(getCstYieldToWorst(shrQ,cstYldToWrstBD)); //CST-YIELD-TO-WORST
			}
		}else{
						
			record.setCstYieldToMat(null); //CST-YIELD-TO-MAT 
			record.setCstYieldToWorst(null); //CST-YIELD-TO-WORST
			}
		}
		
		//FACDAL-2028,2051,2339,2201,2453,2614,2639
		if(StringUtils.isNotBlank(prcCode) && ArrayUtils.contains(prcCodes, prcCode)){
			
			record.setCurrentBaseAccrIncome(0.0);//CURR-ACC-INC-BASE
			record.setLocalDailyDivIncome(0.0); //DLY-DIV-INC-LCL 
			record.setLocalTotalDivIncome(0.0); //TOT-DIV-INC-LCL
		}else{
			
			record.setCurrentBaseAccrIncome(grossIntAccrAmtBase); //CURR-ACC-INC-BASE
			record.setLocalDailyDivIncome(ioFundTyAdjErndIncm); //DLY_DIV_INC_LCL
			record.setLocalTotalDivIncome(grossIntAcruedAmt); //TOT-DIV-INC-LCL
		}
		
		// FACDAL-2029
		if(maturedPosition){
			try{
				record.setIntNormalAccrDays(calcIntNormDaysForMat(contStlmD,currAcrThrDay,priorAcrThrDay));//FACDAL-2639
			}catch(IllegalArgumentException e){
				StringBuilder builder = new StringBuilder();
				builder.append("FundNumber = "+fundNumber)
					   .append(",PortFundNumber = "+portFundNumber)
					   .append(",FmrCuspN = "+fmrCusipN)
					   .append(",PriorDay Matured Position");
					   
				Util.log(this, "For ["+builder.toString()+",], One or more arguments are null", Constants.LOG_LEVEL_WARNING,e);
				record.setIntNormalAccrDays(null);
			}
		}else{
			if(StringUtils.isNotBlank(prcCode) && !ArrayUtils.contains(prcCodes, prcCode)){
		
				record.setIntNormalAccrDays(null);
			}else{
				try{
					record.setIntNormalAccrDays(getNormalAcrDays(currAcrThrDay,priorAcrThrDay,orgnlSetlDate));
				}catch(IllegalArgumentException e){
		
					StringBuilder builder = new StringBuilder();
					builder.append("FundNumber = "+fundNumber)
						   .append(",PortFundNumber = "+portFundNumber)
						   .append(",FmrCuspN = "+fmrCusipN);
						   
					Util.log(this, "For ["+builder.toString()+",], One or more arguments are null", Constants.LOG_LEVEL_WARNING,e);
					record.setIntNormalAccrDays(null);
				}
			}
		}
		
		object = row.get("BK_SUM_BEG_FIS_CUM_AMORT");
		BigDecimal longBkSumCurrDCumAmort = object != null ? (BigDecimal)object : null;
		record.setBookSumCurrDCumAmrt(addShortNLongAmounts(clTyCode,longBkSumCurrDCumAmort,shortBkSumCurrDCumAmort));// BK-SUM-CURR-D-CUM-AMORT

		BigDecimal longBkPriorAmrtValueBd = longBkPriorAmrtValue != null ? new BigDecimal(longBkPriorAmrtValue.toString()) : null;
		BigDecimal shortBkPriorAmrtValueBd = shortBkPriorAmrtValue != null ? new BigDecimal(shortBkPriorAmrtValue.toString()) : null;
		record.setBookSumPriorDCumAmrt(addShortNLongAmounts(clTyCode,longBkPriorAmrtValueBd,shortBkPriorAmrtValueBd));//BK-SUM-PRIOR-D-CUM-AMORT
		
		if(maturedPosition || StringUtils.equals(exttCode,Constants.MSG_SUBTYPE_BOD)){
		
			record.setTaxSumPriorDCumAmrt(txPriorAmrtValue); //TX-SUM-PRIOR-D-CUM-AMORT
			object = row.get(TX_SUM_BEG_FIS_CUM_AMORT);
			Double txSumBegFisCumAmrt = (object != null) ? ((BigDecimal)object).doubleValue():null;
			record.setTaxSumCurrDCumAmrt(txSumBegFisCumAmrt);//TX-SUM-CURR-D-CUM-AMORT
		}
		
		//FACDAL-2544
		object = row.get("localMarketValueAmount");
		Double localMktValueAmt = object != null ? ((BigDecimal)object).doubleValue():null;
		
		object = row.get("localAmortizedTaxCostAmount");
		Double localAmrtTxCstAmt = object != null ? ((BigDecimal)object).doubleValue():null;
		record.setCurrentLocalMktValue(calculateLocalMktValue(localMktValueAmt,localAmrtTxCstAmt,clTyCode,iOCTGCLVSEG)); //CURR-LCL-MV
		record.setCurrentLocalPrice(calculateCurrentLocalPrice(srcCode,pricePriceFile,localAmrtTxCstAmt,shrQ,unitOfCalcC)); //CURR-LCL-PRC
		record.setCurrentExchRate(currencyExchangeRate); //CURR-FX-RATE
		record.setSecurityQual(IOSecQual); //SCTY-QUAL
		record.setValuationDate(holdingDate); //VAL-DATE
		
		object = row.get("earnedAmortizationAmount");
		BigDecimal longErnAmort = object != null ? ((BigDecimal)object) : null;
		record.setEarnedAmount(addShortNLongAmounts(clTyCode,longErnAmort,shortErnAmort));//FACDAL-2955
		
		object = row.get("LOCL_NRT");
		Double loclNrt = object != null ? ((BigDecimal)object).doubleValue():null;
		record.setLocalNrt(loclNrt); //LOCL-NRT
		
		object = row.get("settledParSharesQuantity");
		Double settledSharesParQuantity = object != null ? ((BigDecimal)object).doubleValue():null;
		if(StringUtils.isNotBlank(assetTypeCode) && ("IW".equals(assetTypeCode.trim()) || "TW".equals(assetTypeCode.trim()))){
		
			settledSharesParQuantity = fLegSetlShares != null?Math.abs(fLegSetlShares):null;
		}else if (StringUtils.isNotBlank(assetTypeCode) && "DW".equals(assetTypeCode.trim())){
		
			settledSharesParQuantity = settledSharesParQuantity != null?Math.abs(settledSharesParQuantity):null;
		}
		//Fix for jira FACDAL-2314. Negate Swap Short Leg
		settledSharesParQuantity = negateForSwapShortLeg(IOSecQual, assetTypeCode, settledSharesParQuantity);
		record.setSettledShares(settledSharesParQuantity); //SETL-SHRS
		record.setExpandedExchRate(currencyExchangeRate); //FX-RATE-7
		
		object = row.get("withholdingRate");
		Double withHoldingRate = object != null ? ((BigDecimal)object).doubleValue():null;
		record.setWithholdingRate(withHoldingRate); //WHT-RATE
		
		object = row.get("reclaimRate");
		Double reclaimRate = object != null ? ((BigDecimal)object).doubleValue() : null;
		record.setReclaimRate(reclaimRate); //RECLAIM-RATE
		
		Double fidHoldMarket = calculateFIDHoldMarket(row,assetTypeCode,clTyCode,iOCTGCLVSEG,shareParQuantity);//FACDAL-2859
		record.setFidHoldMarketValue(fidHoldMarket); //FID-HOLD-MARKET
		record.setFidHoldTradedCurrency(denominatedCurrencyCode); //FID-HOLD-TRADED-CCY
		record.setFidHoldIncomeCurrency(incomeCurrencyCode); //FID-HOLD-INCOME-CCY
		
		return record;
	}
	
	//FACDAL-2955
	private Double addShortNLongAmounts(String clTyCode,BigDecimal longLegAmt, BigDecimal shortLegAmt) {
		
		Double result = null;
		
		if(StringUtils.isNotBlank(clTyCode) && (ArrayUtils.contains(CWDWIWTW,clTyCode))){
			
			BigDecimal bd1 = new BigDecimal("0.0");
			if(longLegAmt != null){
				
				bd1 = longLegAmt;
			}
			BigDecimal bd2 = new BigDecimal("0.0");
			if(shortLegAmt != null){
				
				bd2 = shortLegAmt;
			}
			result = bd1.add(bd2).doubleValue();
		}else{
			
			result = longLegAmt != null ? longLegAmt.doubleValue() : null;
		}
		return result;
	}
	
	//FACDAL-2853
	private Double calculateTotLclCdayBasis(String clTyCode, Double shrQ,
											BigDecimal longLclAmrtdTxCstA, 
											BigDecimal shortLclAmrtdTxCstA) {
		
		Double totLclCdayBasis = null;
		
		if(StringUtils.isNotBlank(clTyCode) && (ArrayUtils.contains(CWDWIWTW,clTyCode))){
			
			BigDecimal bd1 = new BigDecimal(shrQ != null ? shrQ.toString():"0.0");
			BigDecimal bd2 = new BigDecimal("0.0");
			if(longLclAmrtdTxCstA != null){
			
				bd2 = longLclAmrtdTxCstA;
			}
			BigDecimal bd3 = new BigDecimal("0.0");
			if(shortLclAmrtdTxCstA != null){
				
				bd3 = shortLclAmrtdTxCstA;
			}
			BigDecimal bd4 = bd1.add(bd2.add(bd3));
			totLclCdayBasis = bd4.doubleValue();
		}else{
		
			totLclCdayBasis = longLclAmrtdTxCstA != null ? longLclAmrtdTxCstA.doubleValue():null;
		}
		return totLclCdayBasis;
	}

	//FACDAL-[2853,3162],3311
	private Double calculateTotalBaseCostBasis(Double shrQ,Double currencyExchangeRate, String clTyCode, Double cstBsA,
											   BigDecimal longLclOrgBkCstA, BigDecimal shortLclOrgBkCstA, 
											   BigDecimal partialTotBaseCostBasis,Double unitOfCalcC) {
		Double originalBookCost = null;
		
		
		if(StringUtils.isNotBlank(clTyCode) && StringUtils.equals(clTyCode, "II")){
			
			if(partialTotBaseCostBasis == null || unitOfCalcC == null){
				
				return null;
			}
			
			return (partialTotBaseCostBasis.multiply(new BigDecimal(unitOfCalcC.toString()))).doubleValue();
		}
		
		if(StringUtils.isNotBlank(clTyCode) && ArrayUtils.contains(CWDWIWTW,clTyCode)){
			
			if(currencyExchangeRate == null || currencyExchangeRate == 0.0){
					
				return null;
			}
			BigDecimal bd1 = new BigDecimal(shrQ != null ? shrQ.toString():"0.0");
			BigDecimal bd2 = new BigDecimal("0.0");
			if(longLclOrgBkCstA != null){
			
				bd2 = longLclOrgBkCstA;
			}
			BigDecimal bd3 = new BigDecimal("0.0");
			if(shortLclOrgBkCstA != null){
				
				bd3 = shortLclOrgBkCstA;
			}
			BigDecimal bd4 = bd1.add(bd2.add(bd3));
			BigDecimal bd5 = new BigDecimal(currencyExchangeRate.toString());
			originalBookCost = bd4.divide(bd5, 2, RoundingMode.HALF_UP).doubleValue();
		}else{
		
				originalBookCost = cstBsA;
		}
		return originalBookCost;
	}

	/**
	 * Refer FACDAL-[2639],2843 for business rule
	 * @param contStlmD
	 * @param currAcrThrDay
	 * @param priorAcrThrDay
	 * @return
	 */
	protected Double calcIntNormDaysForMat(Date contStlmD, Date currAcrThrDay,Date priorAcrThrDay) {
		
		Double intNormalAccrDays = null;
		if((currAcrThrDay != null && StringUtils.equals(BAD_DATE, new DateTime(currAcrThrDay).toString(dft2))) ||
		   (priorAcrThrDay!= null && StringUtils.equals(BAD_DATE, new DateTime(priorAcrThrDay).toString(dft2)))){
					
			return 1.0;
		}
				
		//Most unlikely to happen in production, but just in case.
		if(currAcrThrDay == null  || priorAcrThrDay == null){
					
			return 1.0;
		}
				
		if(contStlmD == null){
					
			return new Double(Math.abs(FeedsUtil.daysDiff(currAcrThrDay,priorAcrThrDay)));
		}
		
		if(FeedsUtil.daysDiff(contStlmD, currAcrThrDay)>=0){
			
			Date d1 = new DateTime(contStlmD).minusDays(1).toDate();
			
			if(priorAcrThrDay != null){
				
				intNormalAccrDays = new Double(Math.abs(FeedsUtil.daysDiff(d1, priorAcrThrDay)));
			}
		}
		return intNormalAccrDays;
	}
	
	/**
	 * This method writes the Holdings records to feed file.
	 * @param FileWriter
	 * @param HoldingsVO - Collection of Holdings Records 
	 */
	protected void writeToFeed(FileWriter fw, List<HoldingsVO> holdings) {

		Iterator<HoldingsVO> iterator = holdings.iterator();
		String record;
		while(iterator.hasNext()){
			
			HoldingsVO holdingsRecord = iterator.next();
			if(holdingsRecord.isFocas()){
				record = focasDecorator.format(holdingsRecord);
			}else{
				record = ioDecorator.format(holdingsRecord);
			}
			fw.writeRecord(record);
			if (geodeFunds.contains(holdingsRecord.getAcctNumber())) {
				fwGeode.writeRecord(record);
			}
		}
	}

	/**
	 * 
	 * This comparable compares HoldingsVO records based on 
	 *
	 */
	protected class HoldingsComparable implements Comparator<HoldingsVO>{

		@Override
		public int compare(HoldingsVO o1, HoldingsVO o2) {
			
			return o1.compareTo(o2);
		}
	}
	
	/**
	 * This method returns Header and Trailer for the feed
	 * @param dfc
	 * @param identifier
	 * @param fileID
	 * @param runDay
	 * @param today
	 * @param noOfRecords
	 * @return
	 */
	protected String getHeaderTrailer(String identifier,String commonPart,int noOfRecords){
		
		DecimalFormat dfmt15 = new DecimalFormat("000000000000000");
		StringBuilder builder = new StringBuilder();
		
		builder.append(identifier);
		builder.append(commonPart);
		builder.append(dfmt15.format(noOfRecords));
		
		return builder.toString();
	}
	

	@SuppressWarnings("unchecked")
	protected void setAdditionalArguments(ArgumentContainer argContainer,List<String> subFundNumbersList) {
		
		List<String> newlyConvFundNumbers = argContainer.getNewConvFundNumbers();
		List<String> allIOFundNumbersList = argContainer.getAllIOFundNumbers();
		List<String> day1OnlyFundNumbersList = null;
		List<String> day2OnwardsFundNumbersList = null;
		List<String> ioOnlyFundNumbersList = null;
		
		if(CollectionUtils.isNotEmpty(subFundNumbersList)){
			
			if(CollectionUtils.isNotEmpty(newlyConvFundNumbers)){
				
				day1OnlyFundNumbersList = (List<String>)CollectionUtils.removeAll(subFundNumbersList,newlyConvFundNumbers);
				day2OnwardsFundNumbersList = (List<String>)CollectionUtils.subtract(subFundNumbersList,newlyConvFundNumbers);
				argContainer.setDay1OnlyFundNumbers(convertListToArray(day1OnlyFundNumbersList));
				argContainer.setDay2OnwardFundNumbers(convertListToArray(day2OnwardsFundNumbersList));
			}else{
				argContainer.setDay1OnlyFundNumbers(null);
				argContainer.setDay2OnwardFundNumbers(null);
			}
			
			if(CollectionUtils.isNotEmpty(allIOFundNumbersList)){
				
				ioOnlyFundNumbersList = (List<String>)CollectionUtils.removeAll(subFundNumbersList,allIOFundNumbersList);
				argContainer.setSubIOFundNumbers(convertListToArray(ioOnlyFundNumbersList));
			}else{
				argContainer.setSubIOFundNumbers(null);
			}
		}
		
		argContainer.setSubFundNumbers(convertListToArray(subFundNumbersList));
	}
	
	protected void resetAdditionalArguments(ArgumentContainer argContainer,List<String> subFundNumbersList) {
		
		List<String> newlyConvFundNumbers = argContainer.getNewConvFundNumbers();
		String[] subIOFundNumbers = argContainer.getSubIOFundNumbers();
		String[] day1OnlyFundNumbersArray = argContainer.getDay1OnlyFundNumbers();
		
		if(CollectionUtils.isNotEmpty(newlyConvFundNumbers) && ArrayUtils.isNotEmpty(day1OnlyFundNumbersArray)){
			
			newlyConvFundNumbers.removeAll(convertArrayToList(day1OnlyFundNumbersArray));//Remove the consumed day 1 funds
		}
		
		if(CollectionUtils.isNotEmpty(subFundNumbersList) && ArrayUtils.isNotEmpty(subIOFundNumbers)){
			
			subFundNumbersList.removeAll(convertArrayToList(subIOFundNumbers));//Remove the consumed IO funds
		}
	}
	
	/**
	 * 
	 * @param funds
	 * @param argContainer
	 * This method identifies all fund numbers and also funds that are on their first day of conversion
	 * @throws Exception 
	 */
	protected void cacheUniqueFundNumbers(Collection<FundPortfolioData> funds,ArgumentContainer argContainer) throws Exception {

		if(funds == null || funds.isEmpty()) return;

		Set<String> fundNumbersSet = new LinkedHashSet<String>();
		Set<String> newlyConvfundNumbersSet = new LinkedHashSet<String>();
		Set<String> focasOnlyFundNumbersSet = new LinkedHashSet<String>();
		Set<String> ioOnlyFundNumbersSet = new LinkedHashSet<String>();
		Map<String,Date> day1Map = new HashMap<String,Date>();
		//int ioFundsCount = 0;
		if(funds != null){
			
			Iterator<FundPortfolioData> iterator = funds.iterator();
			while(iterator.hasNext()){
				
				FundPortfolioData fundData = iterator.next();
				String fundNumber = String.valueOf(fundData.getFundNumber().longValue());
				if(StringUtils.startsWith(fundData.getSourceCode(),"IO")){
					
					//ioFundsCount++;
					ioOnlyFundNumbersSet.add(fundNumber);
				}else{
					
					focasOnlyFundNumbersSet.add(fundNumber);
				}
				
				Date cnvDate = fundData.getConversionDate();
				DateTime cnvDay = new DateTime(cnvDate);
				if(StringUtils.equals(cnvDay.toString(dft1),argContainer.runDay.toString(dft1))){

					DateTime asOfDay = new DateTime(fundData.getAsOfDate());
					if(StringUtils.equals(cnvDay.toString(dft1),asOfDay.toString(dft1))){
						
						newlyConvfundNumbersSet.add(fundNumber);
						day1Map.put(fundNumber, fundData.getConversionDate());
					}
				}
				fundNumbersSet.add(String.valueOf(fundData.getFundNumber().longValue()));
			}
		}
		
		argContainer.setAllFundNumbers(new ArrayList<String>(fundNumbersSet));
		argContainer.setNewConvFundNumbers(new LinkedList<String>(newlyConvfundNumbersSet));
		argContainer.setAllIOFundNumbers(new LinkedList<String>(ioOnlyFundNumbersSet));
		argContainer.setAllFocasFundNumbers(new LinkedList<String>(focasOnlyFundNumbersSet));
		argContainer.setDay1Map(day1Map);
	}
	
	protected String[] convertListToArray(Collection<String> list) {
		
		String[] fundNumbers = null;
		if(CollectionUtils.isNotEmpty(list)){
			
			fundNumbers = list.toArray(new String[list.size()]);
		}
		return fundNumbers;
	}
	
	protected Collection<String> convertArrayToList(String[] array) {
		
		Collection<String> list = null;
		if(ArrayUtils.isNotEmpty(array)){
			
			list = Arrays.asList(array);
		}
		return list;
	}

	protected DateTime getPriorDay(DALCalendar dalCalendar, DateTime day, String srcCode, String exttCode, boolean day1) throws Exception{
		
		DateTime priorDate = new DateTime(dalCalendar.getPriorBusinessDay(day.toDate(),getSourceId(srcCode,day1)));
		
		if(StringUtils.equals(exttCode,Constants.MSG_SUBTYPE_BOD)){
			
			Date date = dalCalendar.getNextCalendarDay(priorDate.toDate());
			priorDate = new DateTime(date);
		}
		
		return priorDate;
	}
	
	protected String getFocasSrcCode(String srcCodes) {
		
		String focasSrcCode = Constants.FUND_TYPE_FOCAS_MM;

		if(StringUtils.contains(srcCodes,Constants.FUND_TYPE_INVESTONE_FIE)){
			
			focasSrcCode = Constants.FUND_TYPE_FOCAS_FIE;
		}
			
		return focasSrcCode;
	}
	
	protected String getIOSourceCodes(String srcCodes) {
		
		String ioSrcCode = Constants.FUND_TYPE_INVESTONE_MM;
		
		if(StringUtils.contains(srcCodes,Constants.FUND_TYPE_INVESTONE_FIE)){
			
			ioSrcCode = Constants.FUND_TYPE_INVESTONE_FIE;
		}
		
		return ioSrcCode;
	}
	
	protected String getCommonHF(DALFeedConfiguration dfc,String fileID,DateTime runDay,DateTime today){
		
		DateTimeFormatter dft = DateTimeFormat.forPattern("yyyyMMdd");
		DateTimeFormatter tft = DateTimeFormat.forPattern("HHmmss");
		String hostName = Util.getHostName();
				
		if(StringUtils.isNotBlank(hostName) && hostName.length() > 8){
			hostName = hostName.substring(0,8);
		}
		
		return String.format("%1s%8s%1s%8s%1s%8s%1s%8s%1s%6s%1s"," ",hostName," ",fileID,
							 " ",runDay.toString(dft)," ",today.toString(dft)," ",today.toString(tft)," ");
	}
	
	protected int getSourceId(String srcCode, boolean day1) {
		
		int sourceSystemId = -1;
		
		if(day1){
			
			sourceSystemId = SourceCalendar.FOCMM.id;
			if(StringUtils.contains(srcCode,Constants.FUND_TYPE_INVESTONE_FIE)){
				
				sourceSystemId = SourceCalendar.FOCFIE.id;
			}
		}else{
			
			sourceSystemId = SourceCalendar.IOMM.id;
			if(StringUtils.contains(srcCode,Constants.FUND_TYPE_INVESTONE_FIE)){
				
				sourceSystemId = SourceCalendar.IOFIE.id;
			}
		}
		
		return sourceSystemId;
	}
	
	/**
	 * Refer FACDAL-2029 for rules related to the dates used in the calculation
	 * This method calculates the difference between ACR_THR_D from current and prior Fund Port records.
	 * @param argContainer
	 * @param maturedPositions 
	 * @return
	 * @throws Exception
	 */
	protected Map<String,Map<String,Object>> getAcrThrDaysMap(String[] fundNumbers,String srcCodes,String exttKndCode,
															  DateTime runDay,DateTime systemProcessDay,DateTime priorDay,
															  boolean maturedPosition) throws Exception {
		
		DateTime day1 = null;
		DateTime day2 = null;
		
		if(maturedPosition){
			day1 = new DateTime(dalCalendar.getNextCalendarDay(runDay.toDate()));
			day2 = getPriorDay(dalCalendar,runDay,srcCodes,Constants.MSG_SUBTYPE_BOD,false);
		}else{
			
			if(StringUtils.equals(Constants.MSG_SUBTYPE_EOD, exttKndCode)){
				
				DateTime tPriorDay =	getPriorDay(dalCalendar,
													systemProcessDay,
													srcCodes,
													exttKndCode,
													false);
				
				day1 = new DateTime(dalCalendar.getNextCalendarDay(tPriorDay.toDate()));
				
				tPriorDay = getPriorDay(dalCalendar,
									   tPriorDay,
									   srcCodes,
									   exttKndCode,
									   false);
				
				day2 = new DateTime(dalCalendar.getNextCalendarDay(tPriorDay.toDate()));
				
			}else{
				
				day1 = runDay;
				day2 = priorDay;
			}
		}
		
		return getAcrThrDaysMap(fundNumbers,
								srcCodes,
								Constants.MSG_SUBTYPE_BOD,
								Constants.COST_BASIS_CODE_FA,
								day1,
								day2);
	}
	
	/**
	 * Refer FACDAL-2029 for rules related to the dates used in the calculation
	 * This method calculates the difference between ACR_THR_D from current and prior Fund Port records.
	 * @param argContainer
	 * @param maturedPositions 
	 * @return
	 * @throws Exception
	 */
	protected Map<String,Map<String,Object>> getAcrThrDaysMap(ArgumentContainer argContainer,boolean maturedPosition) throws Exception {
		
		DateTime day1 = null;
		DateTime day2 = null;
		
		if(maturedPosition){
			day1 = new DateTime(dalCalendar.getNextCalendarDay(argContainer.getRunDay().toDate()));
			day2 = getPriorDay(dalCalendar,argContainer.getRunDay(),argContainer.getSrcCodes(),Constants.MSG_SUBTYPE_BOD,false);
		}else{
			
			if(StringUtils.equals(Constants.MSG_SUBTYPE_EOD, argContainer.getExttKndCode())){
				
				DateTime priorDay =	getPriorDay(dalCalendar,
												argContainer.getSystemProcessDay(),
												argContainer.getSrcCodes(),
												argContainer.getExttKndCode(),
												false);
				
				day1 = new DateTime(dalCalendar.getNextCalendarDay(priorDay.toDate()));
				
				priorDay = getPriorDay(dalCalendar,
									   priorDay,
									   argContainer.getSrcCodes(),
									   argContainer.getExttKndCode(),
									   false);
				
				day2 = new DateTime(dalCalendar.getNextCalendarDay(priorDay.toDate()));
				
			}else{
				
				day1 = argContainer.getRunDay();
				day2 = argContainer.getPriorDay();
			}
		}
		
		return getAcrThrDaysMap(argContainer.getSubFundNumbers(),
								 argContainer.getSrcCodes(),
								 Constants.MSG_SUBTYPE_BOD,
								 Constants.COST_BASIS_CODE_FA,
								 day1,
								 day2);
	}

	/**
	 * FACDAL-2029
	 * @param fundNumbers
	 * @param srcCodes
	 * @param exttKndCode
	 * @param cstBasisCode
	 * @param currentDay
	 * @param priorDay
	 * @return
	 * @throws Exception
	 */
	protected Map<String,Map<String,Object>> getAcrThrDaysMap(String[] fundNumbers,
			  												  String srcCodes, 
			  												  String exttKndCode, 
			  												  String cstBasisCode,
			  												  DateTime currentDay, 
			  												  DateTime priorDay) throws Exception {
		
		Util.log(this,"Begin calling PKG_FEED_HOLDINGS.GET_ACR_THR_DAYS");
		Long start = System.currentTimeMillis();
		final Map<String,Map<String,Object>> map = new HashMap<String,Map<String,Object>>();
		Object[] params = new Object[6];
		params[0] = DBUtil.createOracleArray(USR_STRG_KEY_ARRAY, fundNumbers);
		params[1] = priorDay != null ? priorDay.toString(dft3):null;
		params[2] = currentDay != null ? currentDay.toString(dft3):null;
		params[3] = exttKndCode;
		params[4] = cstBasisCode;
		params[5] = srcCodes;
		
		Util.log(this,"Parameters being passed to PKG_FEED_HOLDINGS.GET_ACR_THR_DAYS are "+FeedsUtil.dump(params));
		try{
		
			DBUtil.packageQueryResultSet(GET_ACR_THR_DAYS, params, 1000,
										new RowHandler() {
										@Override
										public void handleRow(ResultSet rs) throws SQLException {
											try{
												while(rs.next()){
													
													Date currAcrThrDay = rs.getDate("ACRU_THR_D");
													Date priorAcrThrDay = rs.getDate("PRIOR_ACRU_THR_D");
													String fundN = String.valueOf(rs.getLong("FUND_N")).trim();
													String portFundN = String.valueOf(rs.getLong("PORT_FUND_N")).trim();
														
													if(map.containsKey(fundN)){
														
														if(!StringUtils.equals(fundN, portFundN)){
														
															//When FUND_N <> PORT_FUND_N then, take values for ACRU_THR_D from one of its subs 
															//and apply it to the parent fund
															Map<String,Object> tempMap = map.get(fundN);
															tempMap.put(CURR_ACR_THR_DAY,currAcrThrDay);
															tempMap.put(PRIOR_ACR_THR_DAY,priorAcrThrDay);
															map.put(fundN,tempMap); 
														}
													}else{
														
														Map<String,Object> tempMap = new HashMap<String,Object>();
														tempMap.put(CURR_ACR_THR_DAY,currAcrThrDay);
														tempMap.put(PRIOR_ACR_THR_DAY,priorAcrThrDay);
														map.put(fundN,tempMap);
													}
												}
											}catch(Exception e){
											
												throw new SQLException(e);
											}
										}
									});
		}catch(Exception e){
			
			Util.log(this,"Exception calling PKG_FEED_HOLDINGS.GET_ACR_THR_DAYS with parameters "+FeedsUtil.dump(params),Constants.LOG_LEVEL_ERROR,e);
			throw e;
		}
		Long stop = System.currentTimeMillis();
		Util.log(this,"Total key/value pairs for Acr thr Days from FundPort = "+map.size()+" in "+(stop-start)+" milliseconds");
		return map;
	}
	
	protected Map<String, Map<String, Object>> getDataFrmTxlot(String[] fundNumbers,DateTime runDay, String exttKndCode,
			   													String cstBasisCode, String srcCodes) throws Exception {

		Util.log(this,"Begin calling PKG_FEED_HOLDINGS.GET_DATA_FRM_TXLT");
		Long start = System.currentTimeMillis();
		final Map<String,Map<String,Object>> map = new HashMap<String,Map<String,Object>>();
		Object[] params = new Object[5];
		params[0] = DBUtil.createOracleArray(USR_STRG_KEY_ARRAY, fundNumbers);
		params[1] = runDay != null ? runDay.toString(dft3):null;
		params[2] = exttKndCode;
		params[3] = cstBasisCode;
		params[4] = srcCodes;

		Util.log(this,"Parameters being passed to PKG_FEED_HOLDINGS.GET_DATA_FRM_TXLT are "+FeedsUtil.dump(params));
		try{

			DBUtil.packageQueryResultSet(GET_DATA_FRM_TXLT, params, FETCH_SIZE,
										new RowHandler() {
							@Override
							public void handleRow(ResultSet rs) throws SQLException {
								try{
										while(rs.next()){
											
											String key = createKeyFrmTxlt(rs);
											Map<String,Object> valueMap = new HashMap<String,Object>();
											valueMap.put("ORGNL-SETL-D", rs.getDate("ORGNL-SETL-D"));
											valueMap.put("CST-YLD-TO-MAT",rs.getBigDecimal("CST-YLD-TO-MAT"));
											valueMap.put("CST-YLD-TO-WRST",rs.getBigDecimal("CST-YLD-TO-WRST"));
											valueMap.put("TOT-LCL-ORIG-FACE-AMT", rs.getBigDecimal("TOT-LCL-ORIG-FACE-AMT"));
											valueMap.put("CST-BS-A", rs.getBigDecimal("CST-BS-A"));
											valueMap.put("TOT-LCL-COST-BASIS-PARTIAL", rs.getBigDecimal("TOT-LCL-COST-BASIS-PARTIAL"));
											valueMap.put("TOT-BASE-COST-BASIS-PARTIAL", rs.getBigDecimal("TOT-BASE-COST-BASIS-PARTIAL"));
											map.put(key, valueMap);
										}
									}catch(Exception e){
									
										throw new SQLException(e);
									}
							}
			});
		}catch(Exception e){

			Util.log(this,"Exception calling PKG_FEED_HOLDINGS.GET_DATA_FRM_TXLT with parameters "+FeedsUtil.dump(params),Constants.LOG_LEVEL_ERROR,e);
			throw e;
		}

		Long stop = System.currentTimeMillis();
		Util.log(this,"Total key/value pairs for Taxlot Data = "+map.size()+" in "+(stop-start)+" milliseconds");
		return map;
	}
	
	protected ArgumentContainer constructBasicArgContainer(String srcCodes,String exttCode,Date systemProcessDate) throws Exception {

		ArgumentContainer container = new ArgumentContainer();
		container.setSrcCodes(srcCodes);
		container.setIoSrcCodes(getIOSourceCodes(srcCodes));
		container.setExttKndCode(exttCode);

		Date asOfDate = null;
		if(StringUtils.equals(exttCode,Constants.MSG_SUBTYPE_BOD)){
			
			asOfDate = dalCalendar.getNextCalendarDay(systemProcessDate);
		}else{
			
			asOfDate = systemProcessDate;
		}
		DateTime asOfDay = new DateTime(asOfDate);
		DateTime systemProcessDay = new DateTime(systemProcessDate);
		DateTime priorDay = getPriorDay(dalCalendar,new DateTime(systemProcessDate),srcCodes,exttCode,false);
		container.setSystemProcessDay(systemProcessDay);
		container.setRunDay(asOfDay);
		container.setPriorDay(priorDay);

		return container;
	}
	
	protected class ArgumentContainer{
		
		private String srcCodes;
		private String focasSrcCodes;
		private String ioSrcCodes;
		private String exttKndCode;
		private DateTime systemProcessDay;
		private DateTime runDay;
		private DateTime priorDay;
		private DateTime focasPriorDay;
		private List<String> allFundNumbers;
		private List<String> newConvFundNumbers;
		private List<String> allIOFundNumbers;
		private List<String> allFocasFundNumbers;
		private String[] focasOnlyFundNumbers;
		private String[] ioOnlyFundNumbers;
		private String[] subFundNumbers;
		private String[] subIOFundNumbers;
		private String[] subFocasFundNumbers;
		private String[] day1OnlyFundNumbers;
		private String[] day2OnwardFundNumbers;
		private Map<String,Date> day1Map;
		
		public String getSrcCodes() {
			return srcCodes;
		}
		public void setSrcCodes(String srcCodes) {
			this.srcCodes = srcCodes;
		}
		public String getFocasSrcCodes() {
			return focasSrcCodes;
		}
		public void setFocasSrcCodes(String focasSrcCodes) {
			this.focasSrcCodes = focasSrcCodes;
		}
		public String getIoSrcCodes() {
			return ioSrcCodes;
		}
		public void setIoSrcCodes(String ioSrcCodes) {
			this.ioSrcCodes = ioSrcCodes;
		}
		public String getExttKndCode() {
			return exttKndCode;
		}
		public void setExttKndCode(String exttKndCode) {
			this.exttKndCode = exttKndCode;
		}
		public DateTime getRunDay() {
			return runDay;
		}
		public void setRunDay(DateTime runDay) {
			this.runDay = runDay;
		}
		public DateTime getPriorDay() {
			return priorDay;
		}
		public void setPriorDay(DateTime priorDay) {
			this.priorDay = priorDay;
		}
		public String[] getFocasOnlyFundNumbers() {
			return focasOnlyFundNumbers;
		}
		public void setFocasOnlyFundNumbers(String[] focasOnlyFundNumbers) {
			this.focasOnlyFundNumbers = focasOnlyFundNumbers;
		}
		public String[] getIoOnlyFundNumbers() {
			return ioOnlyFundNumbers;
		}
		public void setIoOnlyFundNumbers(String[] ioOnlyFundNumbers) {
			this.ioOnlyFundNumbers = ioOnlyFundNumbers;
		}
		public String[] getSubFundNumbers() {
			return subFundNumbers;
		}
		public void setSubFundNumbers(String[] subFundNumbers) {
			this.subFundNumbers = subFundNumbers;
		}
		public String[] getDay1OnlyFundNumbers() {
			return day1OnlyFundNumbers;
		}
		public void setDay1OnlyFundNumbers(String[] day1OnlyFundNumbers) {
			this.day1OnlyFundNumbers = day1OnlyFundNumbers;
		}
		public String[] getDay2OnwardFundNumbers() {
			return day2OnwardFundNumbers;
		}
		public void setDay2OnwardFundNumbers(String[] day2OnwardFundNumbers) {
			this.day2OnwardFundNumbers = day2OnwardFundNumbers;
		}
		public DateTime getFocasPriorDay() {
			return focasPriorDay;
		}
		public void setFocasPriorDay(DateTime focasPriorDay) {
			this.focasPriorDay = focasPriorDay;
		}
		public List<String> getAllFundNumbers() {
			return allFundNumbers;
		}
		public void setAllFundNumbers(List<String> allFundNumbers) {
			this.allFundNumbers = allFundNumbers;
		}
		public List<String> getNewConvFundNumbers() {
			return newConvFundNumbers;
		}
		public void setNewConvFundNumbers(List<String> newConvFundNumbers) {
			this.newConvFundNumbers = newConvFundNumbers;
		}
		public DateTime getSystemProcessDay() {
			return systemProcessDay;
		}
		public void setSystemProcessDay(DateTime systemProcessDay) {
			this.systemProcessDay = systemProcessDay;
		}
		public Map<String, Date> getDay1Map() {
			return day1Map;
		}
		public void setDay1Map(Map<String, Date> day1Map) {
			this.day1Map = day1Map;
		}
		public List<String> getAllIOFundNumbers() {
			return allIOFundNumbers;
		}
		public void setAllIOFundNumbers(List<String> allIOFundNumbers) {
			this.allIOFundNumbers = allIOFundNumbers;
		}
		public List<String> getAllFocasFundNumbers() {
			return allFocasFundNumbers;
		}
		public void setAllFocasFundNumbers(List<String> allFocasFundNumbers) {
			this.allFocasFundNumbers = allFocasFundNumbers;
		}
		public String[] getSubIOFundNumbers() {
			return subIOFundNumbers;
		}
		public void setSubIOFundNumbers(String[] subIOFundNumbers) {
			this.subIOFundNumbers = subIOFundNumbers;
		}
		public String[] getSubFocasFundNumbers() {
			return subFocasFundNumbers;
		}
		public void setSubFocasFundNumbers(String[] subFocasFundNumbers) {
			this.subFocasFundNumbers = subFocasFundNumbers;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ArgumentContainer [srcCodes=").append(srcCodes)
					.append(", focasSrcCodes=").append(focasSrcCodes)
					.append(", ioSrcCodes=").append(ioSrcCodes)
					.append(", exttKndCode=").append(exttKndCode)
					.append(", systemProcessDay=").append(systemProcessDay)
					.append(", runDay=").append(runDay)
					.append(", priorDay=").append(priorDay)
					.append(", focasPriorDay=").append(focasPriorDay)
					.append(", allFundNumbers=").append(allFundNumbers)
					.append(", newConvFundNumbers=").append(newConvFundNumbers)
					.append(", allIOFundNumbers=").append(allIOFundNumbers)
					.append(", allFocasFundNumbers=").append(allFocasFundNumbers)
					.append(", focasOnlyFundNumbers=").append(Arrays.toString(focasOnlyFundNumbers))
					.append(", ioOnlyFundNumbers=").append(Arrays.toString(ioOnlyFundNumbers))
					.append(", subFundNumbers=").append(Arrays.toString(subFundNumbers))
					.append(", subIOFundNumbers=").append(Arrays.toString(subIOFundNumbers))
					.append(", subFocasFundNumbers=").append(Arrays.toString(subFocasFundNumbers))
					.append(", day1OnlyFundNumbers=").append(Arrays.toString(day1OnlyFundNumbers))
					.append(", day2OnwardFundNumbers=").append(Arrays.toString(day2OnwardFundNumbers))
					.append(", day1Map=").append(day1Map)
					.append("]");
			return builder.toString();
		}
	}
	
	/**************  ALL PRIVATE METHODS ***********************/
	private boolean isFocas(String sourceCode) {
		
		boolean isFocas = true;
		
		if(StringUtils.equals(sourceCode,Constants.FUND_TYPE_INVESTONE_MM) ||
		   StringUtils.equals(sourceCode,Constants.FUND_TYPE_INVESTONE_FIE)){
			
			isFocas = false;
		}
		return isFocas;
	}
	
	//FACDAL-2614
	protected Double calculateTotalUSAccrDaily(Double ioFundTyAdjErnIncm, Double currExchRate) {
		
		if(ioFundTyAdjErnIncm == null || currExchRate == null || currExchRate == 0.0){
			
			return null;
		}
		
		BigDecimal bd1 = new BigDecimal(ioFundTyAdjErnIncm.toString());
		BigDecimal bd2 = new BigDecimal(currExchRate.toString());
		BigDecimal result = bd1.divide(bd2, 2,RoundingMode.HALF_UP);
		return result.doubleValue();
	}
	
	//FACDAL-2859
	private Double calculateFIDHoldMarket(DynaBean row, String assetTypeCode,String clTyCode, Long iOCTGCLVSEG, Double shareParQuantity) {
		
		Double fidHoldMarket = null;
		
		Object object = row.get("currencyExchangeRate");
		Double currencyExchangeRate = object != null ? ((BigDecimal)object).doubleValue():null;
		
		object = row.get("localMarketValueAmount");
		Double localMarketValueAmount = object != null ? ((BigDecimal)object).doubleValue():null;
		
		object = row.get("localOriginalBookCostAmount");
		Double localOriginalBookCostAmount = object != null ? ((BigDecimal)object).doubleValue():null;
		
		object = row.get("marketValueAmount");
		Double marketValueAmountBase = object != null ? ((BigDecimal)object).doubleValue():null;
		
		object = row.get("originalBookCostAmount");
		Double originalBookCostAmount = object != null ? ((BigDecimal)object).doubleValue():null;
		
		object = row.get("localAmortizedTaxCostAmount");
		Double localAmrtTxCostAmt = object != null ? ((BigDecimal)object).doubleValue():null;
		
		if(StringUtils.equals(clTyCode, "FT") && (iOCTGCLVSEG != null && iOCTGCLVSEG.longValue() != 60)){
			
			if(currencyExchangeRate == null || currencyExchangeRate == 0.0){
				
				fidHoldMarket = 0.0;
			}else{
				
				BigDecimal bd1 = (localMarketValueAmount != null) ? new BigDecimal(localMarketValueAmount.toString()):new BigDecimal("0.0");
				BigDecimal bd2 = (localAmrtTxCostAmt != null) ? new BigDecimal(localAmrtTxCostAmt.toString()):new BigDecimal("0.0");
				BigDecimal bd3 = new BigDecimal(currencyExchangeRate.toString());
				BigDecimal result = (bd1.subtract(bd2)).divide(bd3, 2,RoundingMode.HALF_UP);
				fidHoldMarket = result.doubleValue();
			}
		}else{
			
			if(StringUtils.isNotBlank(assetTypeCode) && ("F".equals(assetTypeCode.trim()) || "FT".equals( assetTypeCode.trim()))){
				
				if(currencyExchangeRate != null && currencyExchangeRate.intValue()==1){
					
					BigDecimal bd1 = (localMarketValueAmount != null) ? new BigDecimal(localMarketValueAmount.toString()):new BigDecimal("0.0");
					BigDecimal bd2 = (localOriginalBookCostAmount != null) ? new BigDecimal(localOriginalBookCostAmount.toString()):new BigDecimal("0.0");
					fidHoldMarket = bd1.subtract(bd2).setScale(2,RoundingMode.HALF_UP).doubleValue();
				}else{
					BigDecimal bd1 = (marketValueAmountBase != null) ? new BigDecimal(marketValueAmountBase.toString()): new BigDecimal("0.0");
					BigDecimal bd2 = (originalBookCostAmount != null) ? new BigDecimal(originalBookCostAmount.toString()):new BigDecimal("0.0");
								
					fidHoldMarket = bd1.subtract(bd2).setScale(2,RoundingMode.HALF_UP).doubleValue();
				}
			}else{
				
				if(currencyExchangeRate == null || currencyExchangeRate == 0.0){
						
					fidHoldMarket = 0.0;
				}else{
						
					BigDecimal bd1 = (localMarketValueAmount != null) ? new BigDecimal(localMarketValueAmount.toString()) : 
																		new BigDecimal("0.0");
					BigDecimal bd2 = new BigDecimal(currencyExchangeRate.toString());
					fidHoldMarket = bd1.divide(bd2, 2,RoundingMode.HALF_UP).doubleValue();
				}
			}
		}
		
		return fidHoldMarket;
	}
	
	//FACDAL-2547
	private Double calculateLocalMktValue(final Double localMktValue,final Double localAmrtTxCostAmt,
										  final String clTyCode,final Long iOCTGCLVSEG) {
		
		Double lclMktValue = localMktValue;
		
		if(StringUtils.equals(clTyCode, "FT") && (iOCTGCLVSEG != null && iOCTGCLVSEG.longValue() != 60)){
			
			BigDecimal bd1 = (localMktValue != null) ? new BigDecimal(localMktValue.toString()):new BigDecimal("0.0");
			BigDecimal bd2 = (localAmrtTxCostAmt != null) ? new BigDecimal(localAmrtTxCostAmt.toString()):new BigDecimal("0.0");
			BigDecimal result = bd1.subtract(bd2);
			lclMktValue = result.doubleValue();
		}
		return lclMktValue;
	}

	//FACDAL-2544
	private Double calculateCurrentLocalPrice(final String sourceCode, final Double pricePriceFile,
											  final Double localAmrtTxCstAmt,final Double shrQ,
											  final Double unitOfCalcC) {
		
		Double currentLocalPrice = null;
		if (StringUtils.equals(Constants.FUND_TYPE_INVESTONE_FIE,sourceCode)){
			
			currentLocalPrice = pricePriceFile;
			
		} else if(StringUtils.equals(Constants.FUND_TYPE_INVESTONE_MM, sourceCode)) {
			
			if( localAmrtTxCstAmt != null &&
			   (shrQ != null && shrQ != 0.0) && 
			   (unitOfCalcC != null && unitOfCalcC != 0.0)){
		
				BigDecimal bd1 = new BigDecimal(localAmrtTxCstAmt.toString());
				BigDecimal bd2 = new BigDecimal(shrQ.toString());
				BigDecimal bd3 = new BigDecimal(unitOfCalcC.toString());
				currentLocalPrice = (bd1.divide(bd2, 10,RoundingMode.HALF_UP)).divide(bd3, 10,RoundingMode.HALF_UP).doubleValue();
			}
		}
		
		return currentLocalPrice;
	}
	
	private Double calculatePriorPosnValue(Map<String,Double> priorPosnMap,String posnKey) {
		
		Double amount = null;
		String focasKey = getFocasPriorAmrtKey(posnKey);
		if(priorPosnMap != null){
			
			if(priorPosnMap.containsKey(posnKey)){
				
				amount = priorPosnMap.get(posnKey);
			}else if(priorPosnMap.containsKey(focasKey)){
				
				amount = priorPosnMap.get(focasKey);
			}
		}

		return amount;
	}

	//FACDAL-[2853,3162],3311
	private Double calculateTotLCLCostBasis(final String clTyCode,final Double shrQ, 
											final BigDecimal longLclOrgnlBkCstAmt,
											final BigDecimal shortLclOrgBkCstA,
											final BigDecimal partialTotLclCostBasis,
											final Double unitOfCalcC) {
		
		Double totLCLCstBasis = null;
		if(StringUtils.isNotBlank(clTyCode) && StringUtils.equals(clTyCode, "II")){
			
			if(partialTotLclCostBasis == null || unitOfCalcC == null){
				
				return null;
			}
			
			return (partialTotLclCostBasis.multiply(new BigDecimal(unitOfCalcC.toString()))).doubleValue();
		}
		
		if(StringUtils.isNotBlank(clTyCode) && ArrayUtils.contains(CWDWIWTW, clTyCode)){
			
			BigDecimal bd1 = new BigDecimal("0.0");
			if(longLclOrgnlBkCstAmt != null){
				
				bd1 = longLclOrgnlBkCstAmt;
			}
			BigDecimal bd2 = new BigDecimal("0.0");
			if(shortLclOrgBkCstA != null){
				
				bd2 = shortLclOrgBkCstA;
			}
			BigDecimal bd3 = shrQ != null ? new BigDecimal(shrQ.toString()):new BigDecimal("0.0");
			totLCLCstBasis = bd3.add(bd1.add(bd2)).doubleValue();
		}else{
			
			totLCLCstBasis = longLclOrgnlBkCstAmt != null ? longLclOrgnlBkCstAmt.doubleValue():null;
		}
		
		return totLCLCstBasis;
	}

	
	//FACDAL-2853
	private Double calculateBSCDayBasisAmount(String clTyCode,Double shareParQuantity,Double currencyExchangeRate,
											  BigDecimal longLclAmrtdTxCstA,BigDecimal shortLclAmrtdTxCstA,
			  								  BigDecimal longAmortizedCostBase) {
		
		Double totBsCdayBasis = null;
		if(StringUtils.isNotBlank(clTyCode) && ArrayUtils.contains(CWDWIWTW, clTyCode)){

			if(currencyExchangeRate == null || currencyExchangeRate == 0.0){
				
				totBsCdayBasis = null;
			}else{
				
				BigDecimal bd1 = (shareParQuantity != null) ? new BigDecimal(shareParQuantity.toString()):new BigDecimal("0.0");
				BigDecimal bd2 = (longLclAmrtdTxCstA != null) ? new BigDecimal(longLclAmrtdTxCstA.toString()):new BigDecimal("0.0");
				BigDecimal bd3 = (shortLclAmrtdTxCstA != null) ? new BigDecimal(shortLclAmrtdTxCstA.toString()):new BigDecimal("0.0");
				BigDecimal bd4 = new BigDecimal(currencyExchangeRate.toString());
				BigDecimal bd5 = (bd1.add(bd2.add(bd3))).divide(bd4, 2,RoundingMode.HALF_UP);
				totBsCdayBasis = bd5.doubleValue();
			}
		}else{
			
			totBsCdayBasis = longAmortizedCostBase != null ? longAmortizedCostBase.doubleValue():null;
		}
		
		return totBsCdayBasis;
	}

	private String getLongShortInd(String assetTypeCode, Double shrQ ,String lsInd) {
		
		//FACDAL-1406
		String lsCode = lsInd;
		if(StringUtils.isNotBlank(assetTypeCode) && ArrayUtils.contains(IWTWDW,assetTypeCode)){
			
			if(shrQ != null){
			
				if(shrQ > 0.0){
					
					lsCode = "L";
				}else if(shrQ < 0.0){
					
					lsCode = "S";
				}
			}
		}
		return lsCode;
	}

	private Double calculateShrParQnty(final Double shrQ ,final String assetTypeCode,final Double sdexHoldShares) {
		
		Double shareParQuantity = null;
		
		if(StringUtils.isNotBlank(assetTypeCode) && 
			("IW".equals(assetTypeCode.trim()) || "TW".equals(assetTypeCode.trim()))){
			
			shareParQuantity = sdexHoldShares;
		
		}else if (StringUtils.isNotBlank(assetTypeCode) && "DW".equals(assetTypeCode.trim())){
				
			shareParQuantity = shrQ != null?Math.abs(shrQ):0.0;
		}else{
			
			shareParQuantity = shrQ;
		}
		
		return shareParQuantity;
	}

	/**
	 * Negate Short leg Swaps
	 * Fix jira FACDAL-2314.
	 */
	private Double negateForSwapShortLeg(Double inv1SctyQualN, String assetTypeCode, Double value) {
		
		if(value == null){
			
			return value;
		}
		//Swaps security
		if (StringUtils.isNotBlank(assetTypeCode) && ArrayUtils.contains(CWDWIWTW, assetTypeCode.trim())){
			
			if (inv1SctyQualN == 1.0) {
				value = -1 * Math.abs(value); //Short Legs
			}
		}

		return value;
	}
	
	private boolean isExcludedAsetGroup(String assetTypeCode) {
		
		boolean isValid = false;
		
		if(ArrayUtils.contains(excludedAssetType, assetTypeCode)){
			
			isValid = true;
		}
		
		return isValid;
	}
	
	//FACDAL-2191,2746, 3118
	private Double getUnitMktValue(String srcCode,BigDecimal longAmortizedCostBase,BigDecimal shortAmortizedCostBase,
								   Double shrQ,Double unitOfCalc,Double currExchR,Double pricePriceFile,Double orgnlBkCstAmt, 
								   String clTyCode,boolean maturedPosition) {
		
		double result = 0.0;
		if(StringUtils.equals(Constants.FUND_TYPE_INVESTONE_MM,srcCode)){
				
			if(longAmortizedCostBase == null || longAmortizedCostBase.doubleValue() == 0.0 ||
			   shrQ == null || shrQ == 0.0 || 
			   unitOfCalc == null || unitOfCalc == 0.0){
					
					return 0.0;
			}else{
					
				BigDecimal bd1 = new BigDecimal(shrQ.toString());
				BigDecimal bd2 = new BigDecimal(unitOfCalc.toString());
					
				result = longAmortizedCostBase.divide(bd1.multiply(bd2),8,RoundingMode.HALF_UP).doubleValue(); 
			}
		}else{
				
			if(pricePriceFile != null && currExchR != null && currExchR != 0.0){
					
				BigDecimal bd1 = new BigDecimal(pricePriceFile.toString());
				BigDecimal bd2 = new BigDecimal(currExchR.toString());
				BigDecimal bd3 = bd1.divide(bd2, 8, RoundingMode.HALF_UP);
				result = bd3.doubleValue();
			}
				
			//FACDAL-2746,3118
			if(result == 0.0 && !maturedPosition){
			
				if(StringUtils.isNotBlank(clTyCode) && ArrayUtils.contains(CWDWIWTW,clTyCode)){
					
					if((shrQ == null || shrQ == 0.0) || (unitOfCalc == null || unitOfCalc == 0.0)){
						
						result = 0.0;
					}else{
						
						Double aggrAmt = addShortNLongAmounts(clTyCode,longAmortizedCostBase,shortAmortizedCostBase);
						BigDecimal aggrAmrtCostBsA = aggrAmt != null ? new BigDecimal(aggrAmt.toString()): new BigDecimal("0.0");
						BigDecimal shrQExchRatio = new BigDecimal("0.0");
						BigDecimal shrQBD = new BigDecimal(shrQ.toString());
						if(currExchR != null && currExchR != 0.0){
							
							BigDecimal currExchRBD = new BigDecimal(currExchR.toString());
							shrQExchRatio = shrQBD.divide(currExchRBD, 8,RoundingMode.HALF_UP);
						}
						BigDecimal bd1 = shrQExchRatio.add(aggrAmrtCostBsA);
						BigDecimal bd2 = bd1.divide(shrQBD, 8,RoundingMode.HALF_UP);
						BigDecimal unitOfCalcCDB = new BigDecimal(unitOfCalc.toString());
						BigDecimal bd3 = bd2.divide(unitOfCalcCDB, 8,RoundingMode.HALF_UP);
						result = bd3.doubleValue();
					}
				}else{
					
					if(orgnlBkCstAmt == null ||(shrQ == null || shrQ == 0.0) || (unitOfCalc == null || unitOfCalc == 0.0)){
								
						result = 0.0;
					}else{

						BigDecimal bd1 = new BigDecimal(orgnlBkCstAmt.toString());
						BigDecimal bd2 = new BigDecimal(shrQ.toString());
						BigDecimal bd3 = bd1.divide(bd2, 8, RoundingMode.HALF_UP);
						BigDecimal bd4 = new BigDecimal(unitOfCalc.toString());
						BigDecimal bd5 = bd3.divide(bd4, 8,RoundingMode.HALF_UP);
						result = bd5.doubleValue();
					}
				}
			}
		}
			
		return result;
	}
	
	//FACDAL-1726
	private Double getCstYieldToMat(Double shrQ, BigDecimal aggreCstYldMat) {
			
		Double cstYieldToMat = null;
		if(shrQ == null || shrQ == 0.0){
				
			return null;
		}
			
		BigDecimal bd100 = new BigDecimal("100");
		BigDecimal shrQBD = new BigDecimal(shrQ.toString());
		BigDecimal result = aggreCstYldMat.divide(shrQBD.multiply(bd100), 8,RoundingMode.HALF_UP);
		cstYieldToMat = result.doubleValue();
				
		return cstYieldToMat;
	}
		
	private Double getCstYieldToWorst(Double shrQ,BigDecimal cstYieldToWorst) {
		
		Double result = null;
		
		if(shrQ == null || shrQ == 0.0) return null;
		
		BigDecimal bd1 = new BigDecimal(shrQ.toString());
		BigDecimal bd2 = new BigDecimal("100");
		result = cstYieldToWorst.divide(bd1.multiply(bd2), 8,RoundingMode.HALF_UP).doubleValue();
		
		return result;
	}
	
	//FACDAL-[2029],2843,2984
	// IF ORIGINAL_SETL_DATE > CURRENT_ACR_THR_DAY THEN 0
	// ELSE IF_ORIGINAL_SETL_DATE <= CURRENT_ACR_THR_DAY AND ORIGINAL_SETL_DATE > PRIOR_ACR_THR_DAY THEN (CURRENT_ACR_THR_DAY - ORIGINAL_SETL_DAY) + 1
	// ELSE CURRENT_ACR_THR_DAY - PRIOR_ACR_THR_DAY
	private Double getNormalAcrDays(Date currAcrThrDay,Date priorAcrThrDay, Date orgnlSetlDate) {
		
		if((currAcrThrDay != null && StringUtils.equals(new DateTime(currAcrThrDay).toString(dft2),BAD_DATE)) ||
		   (priorAcrThrDay != null && StringUtils.equals(new DateTime(priorAcrThrDay).toString(dft2),BAD_DATE))){
					
			return 1.0;
		}
				
		//Below condition most unlikely to happen in production. But just in case.
		if(currAcrThrDay == null  || priorAcrThrDay == null){
					
			return 1.0;
		}
				
		if(orgnlSetlDate == null){
					
			return new Double(Math.abs(FeedsUtil.daysDiff(currAcrThrDay, priorAcrThrDay)));
		}
		
		int normalAcrThrDays = 0;
		
		if(FeedsUtil.daysDiff(currAcrThrDay,orgnlSetlDate) > 0){ 
			
			normalAcrThrDays = 0;
		}else if((FeedsUtil.daysDiff(orgnlSetlDate, currAcrThrDay) >=0) && (FeedsUtil.daysDiff(priorAcrThrDay, orgnlSetlDate) > 0)){
			
			normalAcrThrDays = Math.abs(FeedsUtil.daysDiff(currAcrThrDay, orgnlSetlDate))+1;
		}else{
			
			normalAcrThrDays = Math.abs(FeedsUtil.daysDiff(currAcrThrDay,priorAcrThrDay));
		}
		
		return new Double(normalAcrThrDays);
	}
	
	// All keys are constructed below
	private String getFocasPriorAmrtKey(String key) {
			
		String returnKey = null;
			
		if(StringUtils.contains(key,Constants.FUND_TYPE_INVESTONE_MM)){
				
			returnKey = StringUtils.replace(key,
											Util.createFixedLengthString(Constants.FUND_TYPE_INVESTONE_MM,7),
											Util.createFixedLengthString(Constants.FUND_TYPE_FOCAS_MM,7));

		}else if(StringUtils.contains(key,Constants.FUND_TYPE_INVESTONE_FIE)){
				
			returnKey = StringUtils.replace(key,
											Util.createFixedLengthString(Constants.FUND_TYPE_INVESTONE_FIE,7),
											Util.createFixedLengthString(Constants.FUND_TYPE_FOCAS_FIE,7));

		}
		return returnKey;
	}

	private String getPositionKey(DynaBean row){

		Object object = row.get("investOneSecurityDate");
		Double inv1SctyDate = object != null ? ((BigDecimal)object).doubleValue():0.0;
		
		object = row.get("investOneSecurityQualifier");
		Double inv1SctyQual = object != null ? ((BigDecimal)object).doubleValue():0.0;
		
		String[] tokens = {
				Util.createFixedLengthString(row.get("fundNumber"), Constants.LEN_FUND_NUMBER),
				Util.createFixedLengthString(row.get("portfolioFundNumber"),Constants.LEN_PORTFOLIO_ACCOUNT_NUMBER),
				Util.createFixedLengthString(row.get("taxlotLongShortCode"),1),
				Util.createFixedLengthString(row.get("FMRCUSIPNumber"), Constants.LEN_IO_CUSIP_NUMBER),
				Util.createFixedLengthString(inv1SctyDate, Constants.LEN_IO_CUSIP_DATE),
				Util.createFixedLengthString(inv1SctyQual, Constants.LEN_IO_CUSIP_QUAL),
				Util.createFixedLengthString(row.get("sourceCode"),7)
		};
		return FeedsUtil.createKeyFromTokens(tokens);
	}
	
	private String getShortLegPosnKey(DynaBean row) {
		Object object = row.get("investOneSecurityDate");
		Double inv1SctyDate = object != null ? ((BigDecimal)object).doubleValue():0.0;
		
		String[] tokens = {
				Util.createFixedLengthString(row.get("fundNumber"), Constants.LEN_FUND_NUMBER),
				Util.createFixedLengthString(row.get("portfolioFundNumber"),Constants.LEN_PORTFOLIO_ACCOUNT_NUMBER),
				Util.createFixedLengthString(row.get("taxlotLongShortCode"),1),
				Util.createFixedLengthString(row.get("FMRCUSIPNumber"), Constants.LEN_IO_CUSIP_NUMBER),
				Util.createFixedLengthString(inv1SctyDate, Constants.LEN_IO_CUSIP_DATE),
				Util.createFixedLengthString(1.0, Constants.LEN_IO_CUSIP_QUAL),
				Util.createFixedLengthString(row.get("sourceCode"),7)
		};
		return FeedsUtil.createKeyFromTokens(tokens);
	}
	
	private String getSwapLegKey(DynaBean row){
		
		String[] tokens = {
				Util.createFixedLengthString(row.get("fundNumber"), Constants.LEN_FUND_NUMBER),
				Util.createFixedLengthString(row.get("portfolioFundNumber"),Constants.LEN_PORTFOLIO_ACCOUNT_NUMBER),
				Util.createFixedLengthString(row.get("taxlotLongShortCode"),1),
				Util.createFixedLengthString(row.get("investOneSecurityCusipNumber"), Constants.LEN_IO_CUSIP_NUMBER),
				Util.createFixedLengthString(row.get("sourceCode"),7)
			};

		return FeedsUtil.createKeyFromTokens(tokens);
	}
	
	private String createKeyForLeg(DynaBean row) {
		
		Object object = row.get("investOneSecurityDate");
		Double inv1SctyDate = object != null ? ((BigDecimal)object).doubleValue():0.0;
		
		String[] tokens = {
				Util.createFixedLengthString(row.get("fundNumber"), Constants.LEN_FUND_NUMBER),
				Util.createFixedLengthString(row.get("portfolioFundNumber"),Constants.LEN_PORTFOLIO_ACCOUNT_NUMBER),
				Util.createFixedLengthString(row.get("FMRCUSIPNumber"), Constants.LEN_IO_CUSIP_NUMBER),
				Util.createFixedLengthString(row.get("investOneSecurityCusipNumber"), Constants.LEN_IO_CUSIP_NUMBER),
				Util.createFixedLengthString(inv1SctyDate, Constants.LEN_IO_CUSIP_DATE),
				Util.createFixedLengthString(row.get("taxlotLongShortCode"),1),
		};
		return FeedsUtil.createKeyFromTokens(tokens);
	}
	
	protected String createKeyFrmTxlt(ResultSet rs) throws SQLException{
		
		BigDecimal object = rs.getBigDecimal("INV1_SCTY_DATE_N");
		Double inv1SctyDate = object != null ? object.doubleValue():0.0;
		
		object = rs.getBigDecimal("INV1_SCTY_QUAL_N");
		Double inv1SctyQual = object != null ? object.doubleValue():0.0;
		
		String[] tokens = {
				Util.createFixedLengthString(rs.getBigDecimal("FUND_N"), Constants.LEN_FUND_NUMBER),
				Util.createFixedLengthString(rs.getBigDecimal("PORT_FUND_N"),Constants.LEN_PORTFOLIO_ACCOUNT_NUMBER),
				Util.createFixedLengthString(rs.getString("FMR_CUSP_N"), Constants.LEN_IO_CUSIP_NUMBER),
				Util.createFixedLengthString(inv1SctyDate, Constants.LEN_IO_CUSIP_DATE),
				Util.createFixedLengthString(inv1SctyQual, Constants.LEN_IO_CUSIP_QUAL),
				Util.createFixedLengthString(rs.getString("LNG_SHRT_C"),1),
				Util.createFixedLengthString(rs.getInt("SIGN"),1),
				Util.createFixedLengthString(rs.getString("SRC_C"),7)
		};
		return FeedsUtil.createKeyFromTokens(tokens);
	}
	
	protected String getTxltKeyFrmPosition(DynaBean row) throws SQLException{
		
		Object object = row.get("investOneSecurityDate");
		Double inv1SctyDate = object != null ? ((BigDecimal)object).doubleValue():0.0;
		
		object = row.get("investOneSecurityQualifier");
		Double inv1SctyQual = object != null ? ((BigDecimal)object).doubleValue():0.0;
		
		String[] tokens = {
				Util.createFixedLengthString(row.get("fundNumber"), Constants.LEN_FUND_NUMBER),
				Util.createFixedLengthString(row.get("portfolioFundNumber"),Constants.LEN_PORTFOLIO_ACCOUNT_NUMBER),
				Util.createFixedLengthString(row.get("FMRCUSIPNumber"), Constants.LEN_IO_CUSIP_NUMBER),
				Util.createFixedLengthString(inv1SctyDate, Constants.LEN_IO_CUSIP_DATE),
				Util.createFixedLengthString(inv1SctyQual, Constants.LEN_IO_CUSIP_QUAL),
				Util.createFixedLengthString(row.get("taxlotLongShortCode"),1),
				Util.createFixedLengthString(row.get("POSN_SIGN"),1),
				Util.createFixedLengthString(row.get("sourceCode"),7)
		};
		return FeedsUtil.createKeyFromTokens(tokens);
	}
}
