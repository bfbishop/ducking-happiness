package com.fidelity.dal.fundacct.feed.app;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.Message;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StopWatch;

import com.fidelity.dal.fundacct.dalapi.beans.FundAccountBalanceData;
import com.fidelity.dal.fundacct.dalapi.beans.FundPortfolioData;
import com.fidelity.dal.fundacct.dalapi.beans.GlAccountData;
import com.fidelity.dal.fundacct.dalapi.beans.PositionData;
import com.fidelity.dal.fundacct.dalapi.criteria.Criteria;
import com.fidelity.dal.fundacct.dalapi.criteria.FundAccountBalanceCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.FundPortfolioCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.GlAccountCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.Operator;
import com.fidelity.dal.fundacct.dalapi.criteria.OrderByCriteria;
import com.fidelity.dal.fundacct.feed.Exception.DALFeedException;
import com.fidelity.dal.fundacct.feed.admin.ApplicationContext;
import com.fidelity.dal.fundacct.feed.bean.DALCalendar;
import com.fidelity.dal.fundacct.feed.bean.DALCalendar.SourceCalendar;
import com.fidelity.dal.fundacct.feed.bean.FileWriter;
import com.fidelity.dal.fundacct.feed.bean.FundBalance;
import com.fidelity.dal.fundacct.feed.bean.FundPortfolio;
import com.fidelity.dal.fundacct.feed.bean.GLAccount;
import com.fidelity.dal.fundacct.feed.framework.db.ConfigDBAccess;
import com.fidelity.dal.fundacct.feed.framework.feed.DBUtil;
import com.fidelity.dal.fundacct.feed.framework.feed.FeedsUtil;
import com.fidelity.dal.fundacct.feed.framework.feed.RowHandler;
import com.fidelity.dal.fundacct.feed.framework.jms.JMSMessageProcessorBase;
import com.fidelity.dal.fundacct.feed.framework.util.Constants;
import com.fidelity.dal.fundacct.feed.framework.util.Initialize;
import com.fidelity.dal.fundacct.feed.framework.util.Util;
import com.fidelity.dal.fundacct.feed.framework.util.XMLHelper;

import oracle.jdbc.OracleTypes;

public class AssetsFeed extends JMSMessageProcessorBase{

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

	
	private final String SPACE = "";
	private FileWriter fw;
	private FileWriter fwGeode;
	int fieRecordCount = 0;
	public String reportOutputFileName = null;
	private Date today = null;
	private Date asOfDate = null;
	private DateTime asOfDay = null;
	private String todayStr = null;
	private String[] fundNumberSet = {};
	protected DateTimeFormatter dft1 = DateTimeFormat.forPattern("yyyy-MM-dd");	
	protected DateTimeFormatter dft2 = DateTimeFormat.forPattern("yyyyMMdd");
	protected DateTimeFormatter dft3 = DateTimeFormat.forPattern("dd-MMM-yyyy");
	private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
	private DecimalFormat recordCountFormat = new DecimalFormat("000000000000000");
	private DecimalFormat fmt15 = new DecimalFormat("000000000000000");
	private DecimalFormat fmt18_decimal = new DecimalFormat("000000000000000.00");
	private DecimalFormat fmt18_decimal_positive = new DecimalFormat("000000000000000.00");
	private DecimalFormat fmt19_decimal_positive = new DecimalFormat("00000000000000.0000");
	private StopWatch stopWatch = null;
	private FundBalance assetsBalance = null;	
	private Criteria fundBalanceCriteria = null;
	private OrderByCriteria orderByCriteria = null; 
	private Date headerDate = null;
	private DateTime headerDateTime = null;
	private String extTypeCode = null;
	private String feedDescription = null;
	private Map<String,Date> fiscalYrMap = null;
    private final int taxlotFetchSize = 20000;
    private int sourceId;
    
	private final String[] GLAccountNumberFundMarketValue = {"1100-0000-0000-0000","1101-0000-0000-0000"};
	
	private final String[] GLAccountNumberAccruedIncomeValue =  {"4000-0000-1000-0000","4000-0000-1002-0000","4000-0000-1003-0000","4000-0000-1004-0000",
																	 "4000-0000-1400-0000","4000-0000-2000-0000","4000-0000-2001-0000","4200-0000-1000-0000",
																	 "4200-0000-2000-0000","4200-0000-0000-0000","4000-0000-0000-0000","4000-0000-2022-0000",
																	 "4000-0000-2023-0000","4200-0000-2022-0000"};
	
	private final String[] GLAccountNumberAccruedInterestValue = {"1400-0000-1000-0000","1400-0000-1002-0000"};
	//FACDAL - 2821.
	private final String[] GLAccNumberForTotalAssets = {"2600-0000-2000-0000","2600-0000-1000-0000"};
	
	//FACDAL-2054
	private final String[] GLAccountNumberInvestableCashValue_1 = {"1110-0000-0000-0000","1110-0000-0010-0000","1111-0000-0000-0000","1111-0000-0010-0000",
																		"1120-0000-0000-0000","1130-0000-0000-0000","1320-0000-0000-0000","1400-0000-1004-0000",
																		"1400-0000-1201-0000","1400-0000-1450-0000","1400-0000-2003-0000","1500-0000-0000-0000",
																		"1500-0000-0001-0000","1500-0000-0001-0090","1500-0000-0001-0096","1500-0000-0001-0098",
																		"1500-0000-0002-0000","1500-0000-0002-0090","1500-0000-0002-0096","1500-0000-0002-0098",
																		"1600-0000-0000-0000","1600-0000-0070-0000","1600-0000-0071-0000","1600-0000-0072-0000",
																		"1600-0000-0073-0000","1600-0000-0074-0000","1600-0000-0075-0000","1600-0000-0076-0000",
																		"1600-0000-0077-0000","1600-0000-0078-0000","1600-0000-0079-0000","1600-0000-0080-0000",
																		"1600-0000-0081-0000","1600-0000-0082-0000","1720-0000-0000-0000"};
		
	private final String[] GLAccountNumberInvestableCashValue_2 = {"2120-0000-0000-0000","2300-0000-0000-0000","2300-0000-0001-0000","2300-0000-0001-0090",
																		"2300-0000-0001-0096","2300-0000-0001-0098","2300-0000-0002-0000","2300-0000-0002-0090",
																		"2300-0000-0002-0096","2300-0000-0002-0098","2400-0000-0000-0000","2400-0000-0070-0000",
																		"2400-0000-0071-0000","2400-0000-0072-0000","2400-0000-0073-0000","2400-0000-0074-0000",
																		"2400-0000-0075-0000","2400-0000-0076-0000","2400-0000-0077-0000","2400-0000-0078-0000",
																		"2400-0000-0079-0000","2400-0000-0080-0000","2400-0000-0081-0000","2400-0000-0082-0000",
																		"2600-0000-1004-0000","2600-0000-1450-0000","2620-0000-0000-0000"};
		
	//FACDAL-1234, FACDAL-1711, FACDAL - 1955
	private final String[] GLAccountNumberCurrencyValue = {"1110-0000-0000-0000","1110-0000-0010-0000","1111-0000-0000-0000","1111-0000-0010-0000"};
	
	private final String GLAccountNumberShortPosnMktValue = "2710-0000-0000-0000";//FACDAL-2934

	//FACDAL-1707
	private String GLExpenseYTDTotal_RegEx = "^5000*"; 
	private String GLExpenseYTDTotal = "5000"; 
	
	//FACDAL-1734
	private String GLAccountNumberTotalAssets = "1";
	
	private MathContext mc = new MathContext(0,RoundingMode.HALF_UP);
	
	private HashMap<String,BigDecimal> fundMarketValueHashMap,fundAccruedIncomeHashMap,fundAccruedInterestHashMap,adjTotalAssetsMap,
									   fundInvestableCashHashMap1,fundInvestableCashHashMap2,fundCurrencyHashMap,fundExpenseYTDTotal,
									   fundTotalAssetsMap,currencyFWDMap,currencySpotsMap,shortPosnMktValueMap = null;
	private Set<String> newConvFundNumbersSet = null;	
	private Map<String,Double[]> fundExpDailyTotal;
	private Map<String,BigDecimal> fiscalGLDataMap,currHldgSPOTMap = null;	
	private DALCalendar dalCalendar = null;
	private static final String ZERO_SHR_CLASS_NUMBER = "^0+$";

	/**
	 * This method prevents daily feeds to be generated on T1M and T3M date. 
	 */
	@Override
	public void onMessage(Message message) {
		
		sourceId = appName.contains("FIE") ? DALCalendar.SourceCalendar.IOFIE.id : DALCalendar.SourceCalendar.IOMM.id;
		Date todayDate = getToday();		
		try {
			int event = XMLHelper.parseEventXMLMessage(message).getEvent();
						
			if (FeedHelper.isToday1stBusinessDay(todayDate, sourceId)
					|| FeedHelper.isTodayLastBusinessDayOfTheMonth(todayDate, sourceId)) {
				
				if (event == 52120 || event == 53121 || event == 50118
						|| event == 51119 || event == 94118 || event == 95119) {
		
					Util.log("Today is First Business day/Last Business Day. Processing First Business day/Last Business day event "
							+ event);
					super.onMessage(message);
				} else {
					Util.log("Today is First Business day/Last Business Day. Received daily Feed JMS event "
							+ event
							+ " This event will be ignored "
							+ "and will be waiting for First Business day/Last Business Day jms event");
				}
			} else {
				Util.log("Today is neither First Business day or Last Business day of Month. So running daily Feed.");
				super.onMessage(message);
			}
		} catch (Exception e) {
			Util.log(this.getClass().getName(), "AssetFeed.onMessage():Exception from " + 
	    			dfc.getOutputLocalFileName() + "\n" + e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
		}
	}
	
	@Override
	protected synchronized void processMessage(int event) throws Exception {
		
		try{
			Util.log(this,"Inside AssetsFeed.processMessage():" +"Event published by Orchestration : "+event);
		
			dfc = getConfiguration();
			
						
			if (isMatchingEvent(Constants.BOD_FUND_LOAD_FIE_FUNDS) || event==52120 || event==52019) {
				extTypeCode = "BOD";
				asOfDate = new DALCalendar().getNextCalendarDay(getToday());
				feedDescription = "AssetsBODFileGeneration";
			} else if (isMatchingEvent(Constants.BOD_FUND_LOAD_MM_FUNDS) ||event==53121 || event==53008) {

				extTypeCode = "BOD";
				asOfDate = new DALCalendar().getNextCalendarDay(getToday());
				feedDescription = "AssetsBODFileGeneration";
			} else if (isMatchingEvent(Constants.EOD_FUND_LOAD_FIE_FUNDS) || event==50118 ||  event==50018 || event == 94118 ) {
 
				extTypeCode = "EOD";
				asOfDate = getToday();
				feedDescription = "AssetsEODFileGeneration";
			} else if (isMatchingEvent(Constants.EOD_FUND_LOAD_MM_FUNDS) || event==51119 ||  event==51007 || event == 95119) {

				extTypeCode = "EOD";
				asOfDate = getToday();
				feedDescription = "AssetsEODFileGeneration";
			} else {
				Util.log("appId:" + dfc.getAppId() + ":");
				Util.log(this, "invalid event received: " + event + ": feed genertion skipped");
				logRunMetrics(this,Constants.LOG_TYPE_RESULT,0, 0,Constants.LOG_RESULT_SUCCESS);
				return;

			}
		
			stopWatch = new StopWatch(dfc.getAppName()+" for "+dfc.getOutputLocalFileName());
			stopWatch.start(feedDescription);
			Util.log(this,"Configuration Parameters: "+dfc.toString());
			Util.log(this,"Target events from ConfigDataCache:\n "+dfc.getTargetEvents());

			asOfDay = new DateTime(asOfDate);
			headerDate = getToday();
			headerDateTime = new DateTime(headerDate);
			todayStr = asOfDay.toString(dft1);
			doFeed();
			
		}catch(Exception e){
			throw new DALFeedException(this,getMessageToken(),e,dfc.getAppName());
		}
	}
	
	private String getPriorAsOfDay(DALCalendar dalCalendar) throws Exception {
		Date priorBusinessDay = null;
		String strPriorBusinessDay = null;
		priorBusinessDay = dalCalendar.getPriorBusinessDay(getToday(),dfc.getAppName().contains("FIE")?SourceCalendar.IOFIE.id:SourceCalendar.IOMM.id);
		priorBusinessDay = dalCalendar.getNextCalendarDay(priorBusinessDay);			
		DateTime priorAsOfDay = new DateTime(priorBusinessDay);			
		strPriorBusinessDay = priorAsOfDay.toString(dft1);
		return strPriorBusinessDay;
	}
	
	public boolean doFeed() throws Exception {
	
		fmt18_decimal_positive.setPositivePrefix("+");
		fmt19_decimal_positive.setPositivePrefix("+");
		boolean bSuccess = false;
		dalCalendar = new DALCalendar();
		String priorDayStr="";
		String costBasis = Constants.COST_BASIS_CODE_FA;
		
		if(extTypeCode.equals("BOD")){
			priorDayStr = getPriorAsOfDay(dalCalendar);			
			
		}else if(extTypeCode.equals("EOD")){
			DateTime priorDate = new DateTime(new DALCalendar().getPriorBusinessDay(asOfDay.toDate(),dfc.getAppName().contains("FIE")?SourceCalendar.IOFIE.id:SourceCalendar.IOMM.id));
			priorDayStr = priorDate.toString(dft1);			
		}
		DateTime priordt = new DateTime(priorDayStr);		
		String priorDay = priordt.toString(dft3);
	
		try {
			today = new Date();
			int recordCount = 0;
			reportOutputFileName = dfc.getOutputLocalFileName();			
			Collection<FundPortfolioData> funds = getFundPortfolios(todayStr,extTypeCode,dfc.getSourceCode());
			cacheUniqueFundNumbers(funds);
			ArrayList<FundAccountBalanceData> fundBalance = new ArrayList<FundAccountBalanceData>();			
			ArrayList<FundAccountBalanceData> focasIOFunds = getAssetsBalance();			 
			
			if(focasIOFunds != null){
				
				fundBalance.addAll(focasIOFunds);
			}
			
			if(fundBalance != null){
			
				Collections.sort(fundBalance,new Comparable());
			}
			Set<String> fundNumbersSet = new HashSet<String>();
			
			if(fundBalance != null){
				for (FundAccountBalanceData rowFund : fundBalance) {	
					if(rowFund.getFundNumber() != null){
						fundNumbersSet.add(String.valueOf(rowFund.getFundNumber()));						
					}
				}
			}
			
			
			Util.log(this,Arrays.toString(fundNumberSet));
			List<String> fundNumberList = new ArrayList<String>(fundNumbersSet);	
			
			fundMarketValueHashMap = new HashMap<String,BigDecimal>();
			fundAccruedIncomeHashMap = new HashMap<String,BigDecimal>();
			fundAccruedInterestHashMap = new HashMap<String,BigDecimal>();
			fundInvestableCashHashMap1 = new HashMap<String,BigDecimal>();
			fundInvestableCashHashMap2 = new HashMap<String,BigDecimal>();
			fundCurrencyHashMap = new HashMap<String,BigDecimal>();
			fundExpenseYTDTotal = new HashMap<String,BigDecimal>();
			fundTotalAssetsMap = new HashMap<String,BigDecimal>();
			currencyFWDMap = new HashMap<String,BigDecimal>();
			currencySpotsMap = new HashMap<String,BigDecimal>();
			fundExpDailyTotal =  new HashMap<String,Double[]>();	
			adjTotalAssetsMap = new HashMap<String,BigDecimal>();
			shortPosnMktValueMap = new HashMap<String,BigDecimal>();
			
			if (fundNumberList.size() > 0) {
				if(StringUtils.contains(dfc.getSourceCode(),Constants.FUND_TYPE_INVESTONE_MM) || StringUtils.contains(dfc.getSourceCode(),Constants.FUND_TYPE_INVESTONE_FIE)){
					String srcCode = computeSrcCode();
					populateCurrHldg(srcCode);  // This method will populate the map, where the values are used in calculation of TOTAL_ASSETS,ADJUSTED_TOTAL_ASSETS.
					if(extTypeCode.equals("EOD")){
						cacheFiscalYrDataFrmFundPortfolio(priorDay, srcCode); // FACDAL - 2726. To populate Fiscal Year data from Fund Portfolio.		
						cacheGLDataForExpDailyTotal(srcCode);	// FACDAL - 2726	
					}
					cacheTaxlotCurrHldgFWDsData(todayStr, srcCode, costBasis, extTypeCode); // FACDAL - 2837
					cacheTaxlotCurrHldgSPOTsData(todayStr, srcCode, costBasis, extTypeCode); // FACDAL - 2837					
					do{
						@SuppressWarnings("unchecked")
						ArrayList<String> subFundNumbersList = (ArrayList<String>) getNextBatch(fundNumberList,dfc.getBatchSize());
						String[] fundNumbers = subFundNumbersList.toArray(new String[subFundNumbersList.size()]);						
						Collection<GlAccountData> glAccountDataList = getGLAccountData(fundNumbers,todayStr,srcCode,costBasis,extTypeCode,ZERO_SHR_CLASS_NUMBER);											
						populateAllReqMaps(glAccountDataList);		
					
						// FACDAL - 2185.
						Collection<GlAccountData> glAcctDataListCurrDay = gatherGLDataFromRegEx(subFundNumbersList,GLExpenseYTDTotal_RegEx,todayStr,srcCode,costBasis, extTypeCode);
						gatherDataFromGLAcct(glAcctDataListCurrDay,newConvFundNumbersSet,false);					
											
						Collection<GlAccountData> glAcctDataListPriorDay = gatherGLDataFromRegEx(subFundNumbersList,GLExpenseYTDTotal_RegEx,priorDayStr,srcCode,costBasis, extTypeCode);
						gatherDataFromGLAcct(glAcctDataListPriorDay,newConvFundNumbersSet,true);						

					}while(fundNumberList != null && !fundNumberList.isEmpty());
				}
			}		
			fw = new FileWriter(dfc);
			dfc.setOutputLocalFileName(dfc.getOutputLocalFileName()+".geode");
			fwGeode = new FileWriter(dfc);
			Util.log(this,"this is the new filewriter " + ": " + fw.toString());
			
			writeHeader();
			recordCount = (writeContent(fundBalance ,recordCount, todayStr));
			writeTrailer();
			fw.close(false);
			fwGeode.close(false);
			stopWatch.stop();
		
			bSuccess = true;
			sendFile(fw);
			logRunMetrics(this,Constants.LOG_TYPE_STATS,stopWatch.getTotalTimeSeconds(), (fw != null ? fw.getNoOfRecordsWritten() : 0),
			stopWatch.prettyPrint());
		
			logRunMetrics(this,Constants.LOG_TYPE_RESULT,stopWatch.getTotalTimeSeconds(), (fw != null ? fw.getNoOfRecordsWritten() : 0),Constants.LOG_RESULT_SUCCESS);
		}
	
		catch (Exception e) {		
			Util.log(this, "Error while generating Assets " + extTypeCode + " feed: " + e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
			bSuccess = false;
			throw e;
		}
		finally{
			if (fw != null){
				fw = null;
				cleanup();
			}
		}

		return bSuccess;
	}

	private void writeTrailer() {
		String tPrefix = String.format("%6s%1s%8s%1s%8s%1s%8s%1s%8s%1s%6s%1s",
										"IMATRL",
										SPACE,
										(Util.getHostName()== null ? "fpcms004":(Util.getHostName()).substring(0,8)),
										SPACE,
										reportOutputFileName.substring(14,22),
										SPACE,
										headerDateTime.toString(dft2),
										SPACE,
										dateFormat2.format(today),
										SPACE,
										timeFormat.format(today),SPACE);

		fw.writeln(tPrefix + recordCountFormat.format(fw.getNoOfRecordsWritten()));
		fwGeode.writeln(tPrefix + recordCountFormat.format(fwGeode.getNoOfRecordsWritten()));
	}

	private void writeHeader() {
		String hPrefix = String.format("%6s%1s%8s%1s%8s%1s%8s%1s%8s%1s%6s%1s",
										"IMAHDR", 
										SPACE, 
										(Util.getHostName()== null ? "fpcms004":(Util.getHostName()).substring(0,8)),
										SPACE,
										reportOutputFileName.substring(14,22),
										SPACE,
										headerDateTime.toString(dft2)
										,SPACE,
										dateFormat2.format(today),SPACE,
										timeFormat.format(today), SPACE);

		

		fw.writeln(hPrefix + recordCountFormat.format(fw.getNoOfRecordsWritten()));
		fwGeode.writeln(hPrefix + recordCountFormat.format(fwGeode.getNoOfRecordsWritten()));
	}
	
	private void populateAllReqMaps(Collection<GlAccountData> glAccountDataList){
		
		Iterator<GlAccountData> iterator = glAccountDataList.iterator();
		while(iterator.hasNext()){
			
			GlAccountData glAccountData = iterator.next();
			String glAccountN = glAccountData.getGeneralLedgerAccountNumber();
			if(StringUtils.isNotBlank(glAccountN)){
				
				glAccountN = glAccountN.trim();
				String key = getKeyFromGLAccount(glAccountData);
				Double endBalA = glAccountData.getEndBalanceAmount();
				//FundMarket Value
				if(ArrayUtils.contains(GLAccountNumberFundMarketValue, glAccountN)){					
					addToMap(fundMarketValueHashMap,key,endBalA);
				}
				
				//Accrued Income Value
				if(ArrayUtils.contains(GLAccountNumberAccruedIncomeValue, glAccountN)){					
					addToMap(fundAccruedIncomeHashMap,key,endBalA);
				}
				
				//Accrued Interest Value
				if(ArrayUtils.contains(GLAccountNumberAccruedInterestValue, glAccountN)){				
					addToMap(fundAccruedInterestHashMap,key,endBalA);
				}
				
				//Investable Cash Value
				if(ArrayUtils.contains(GLAccountNumberInvestableCashValue_1, glAccountN)){					
					addToMap(fundInvestableCashHashMap1,key,endBalA);
				}
				
				if(ArrayUtils.contains(GLAccountNumberInvestableCashValue_2, glAccountN)){					
					addToMap(fundInvestableCashHashMap2,key,endBalA);
				}
				
				//Total Assets
				if(StringUtils.startsWith(glAccountN, GLAccountNumberTotalAssets)){					
					addToMap(fundTotalAssetsMap,key,endBalA);
				}
				
				if(ArrayUtils.contains(GLAccNumberForTotalAssets,glAccountN)){					
					addToMap(adjTotalAssetsMap,key,endBalA);
				}
				
				if(ArrayUtils.contains(GLAccountNumberCurrencyValue,glAccountN)){					
					addToMap(fundCurrencyHashMap,key,endBalA);
				}
				
				//EXP_YTD_TOTAL
				if(StringUtils.startsWith(glAccountN, GLExpenseYTDTotal)){					
					addToMap(fundExpenseYTDTotal,key,endBalA);
				}
				
				//FACDAL-2934
				if(StringUtils.equals(glAccountN, GLAccountNumberShortPosnMktValue)){					
					addToMap(shortPosnMktValueMap,key,endBalA);
				}
			}
		}
	}
	
	private void addToMap(Map<String, BigDecimal> map,String key, Double amount) {
		
		if(map.containsKey(key)){
			
			BigDecimal bd1 = map.get(key);
			BigDecimal bd2 = new BigDecimal(amount!=null?amount.toString():"0.0");
			map.put(key, bd1.add(bd2,mc));
		}else{
						
			BigDecimal bd = new BigDecimal(amount!=null?amount.toString():"0.0");
			map.put(key, bd);
		}
	}

	
	private void cleanup() {
		fundMarketValueHashMap = null;
		fundAccruedIncomeHashMap = null;
		fundAccruedInterestHashMap = null;
		fundInvestableCashHashMap1 = null;			
		fundInvestableCashHashMap2 = null;	
		fundCurrencyHashMap = null;
		fundExpenseYTDTotal = null;
		fundTotalAssetsMap = null;
		currencyFWDMap = null;
		currencySpotsMap = null;
		fundExpDailyTotal = null;
		fiscalGLDataMap = null;
		newConvFundNumbersSet = null;
		fiscalYrMap = null;
		adjTotalAssetsMap = null;
		currHldgSPOTMap = null;
		shortPosnMktValueMap = null;
	}

	
	private void gatherDataFromGLAcct(Collection<GlAccountData> glAccountDataList, Set<String> day1OnlyFundNumbers, boolean isPrior) {
		
		if(CollectionUtils.isEmpty(glAccountDataList)) return;
		
		Iterator<GlAccountData> iterator = glAccountDataList.iterator();
		
		while(iterator.hasNext()){			
			GlAccountData glAcctData = iterator.next();
			String portFundN = null;
			if(glAcctData.getPortfolioFundNumber() != null){				
				portFundN = String.valueOf(glAcctData.getPortfolioFundNumber().longValue()).trim();
			}

			String glAccN = glAcctData.getGeneralLedgerAccountNumber();
			if(StringUtils.isBlank(glAccN)){				
				continue;
			}
			
			boolean day1 = false;
			if(portFundN != null){
				
				day1 = day1OnlyFundNumbers.contains(portFundN)?true:false;
			}
		
			if(isPrior){
				cacheForGLBal(glAcctData, true, fundExpDailyTotal, day1);
			}
			else{				
				cacheForGLBal(glAcctData, false, fundExpDailyTotal, day1);
			}
		}
	}
	
	private void cacheFiscalYrDataFrmFundPortfolio(String priorDay,String srcCode) throws Exception{
        
        String sql = "PKG_FEED_ASSETS.GET_REQ_FUND_DATA";
	    String asOfDate = asOfDay.toString(dft3);
	    String[] params = new String[5];
        params[0] = asOfDate;
        params[1] = priorDay;
        params[2] = extTypeCode;
        params[3] = srcCode;
        params[4] = Constants.COST_BASIS_CODE_FA;
           
        fiscalYrMap = new HashMap<String,Date>();
                
        DBUtil.packageQuery(sql, params, 1000, new RowHandler() {
                            
                     @Override
                     public void handleRow(ResultSet rs) throws SQLException {
                            
                            Date asOfDate = rs.getDate("AS_OF_D");
                            Date currValDate = rs.getDate("CURR_VAL_D");
                            Date priorValDate = rs.getDate("PRIOR_VAL_D");
                            Date endFiscalYrDate = rs.getDate("PRIOR_END_FISC_YR_D");
                            String portFundNumber = String.valueOf(rs.getLong("PORT_FUND_N"));
                            String ioFundTypeCode = rs.getString("IO_FUND_TY");
                            //int sourceId = FeedsUtil.getSourceId(dfc.getSourceCode());
                            if((currValDate != null && priorValDate != null && endFiscalYrDate != null) &&
                               isFiscal(currValDate,priorValDate,endFiscalYrDate)){
                                   if(StringUtils.equals(ioFundTypeCode, "F")){
                                          
                                          DateTime currVal = new DateTime(currValDate);
                                          DateTime prevMonth = currVal.minusMonths(1);
                                          try{                                                 
                                                 Date monthEndBusnDate = dalCalendar.getPeriodEndBusinessDay(prevMonth.toDate(), sourceId);
                                                 fiscalYrMap.put(portFundNumber,monthEndBusnDate);                                               
                                          }catch(Exception e){
                                        	  fiscalYrMap.put(portFundNumber, null);
                                          }
                                          
                                   }else if(StringUtils.equals(ioFundTypeCode, "M")){
                                          
                                          try{                 
                                        	     Date startBusDate = dalCalendar.getPeriodStartBusinessDay(asOfDate, sourceId);
                                        	     fiscalYrMap.put(portFundNumber,startBusDate);                
                                                 
                                          }catch(Exception e){
                                        	  fiscalYrMap.put(portFundNumber, null);
                                          }
                                   }
                            }                            
                     }
        });        
 }
	
	private String getDatesInString(Map<String,Date> fiscalYrMap) {

		HashSet<String> fiscalDateSet = new HashSet<String>();
		String asOfDates = "";
		if(!fiscalYrMap.isEmpty()){
			for (Map.Entry<String, Date> entry : fiscalYrMap.entrySet()){
				Date fiscalDate = entry.getValue();
				DateTime fiscalDateDT = new DateTime(fiscalDate);
				fiscalDateSet.add(fiscalDateDT.toString(dft3));							
			}
		}
		String[] fiscalDates = fiscalDateSet.toArray(new String[fiscalDateSet.size()]);
		fiscalDates.toString();
		asOfDates = FeedsUtil.arrayToString(fiscalDates);
		
		return asOfDates;			
	}
	
	private String getFundNosInString(Map<String,Date> fiscalYrMap) {

		StringBuilder fundNos = new StringBuilder();		
		if(!fiscalYrMap.isEmpty()){
			for (Map.Entry<String, Date> entry : fiscalYrMap.entrySet()){
				String fund = entry.getKey();
				fundNos.append(fund);
				fundNos.append(",");				
			}
		}
		return StringUtils.removeEnd(fundNos.toString(), ",");			
	}


	
	private void cacheGLDataForExpDailyTotal(String srcCode) throws Exception{
	        
		    String sql = "PKG_FEED_ASSETS.GET_GL_DATA";
		    String[] params = new String[6];
	        params[0] = getFundNosInString(fiscalYrMap);
	        params[1] = getDatesInString(fiscalYrMap);
	        params[2] = Constants.MSG_SUBTYPE_ME;
	        params[3] = srcCode;
	        params[4] = Constants.COST_BASIS_CODE_FA;
	        params[5] = GLExpenseYTDTotal_RegEx;
	           
	       fiscalGLDataMap = new HashMap<String,BigDecimal>();        
	        DBUtil.packageQuery(sql, params, 1000, new RowHandler() {
	                            
	                     @Override
	                     public void handleRow(ResultSet rs) throws SQLException {
	                            
	                            Date asOfDate = rs.getDate("AS_OF_D");
	                            String portFundNumber = String.valueOf(rs.getLong("PORT_FUND_N"));
	                            BigDecimal endBalA= rs.getBigDecimal("END_BAL_A");
	                            if(portFundNumber != null && asOfDate != null){
	                            	if(!fiscalYrMap.isEmpty() && fiscalYrMap.containsKey(portFundNumber)){
	                            		Date fiscalDate = fiscalYrMap.get(portFundNumber);
	                            		if(asOfDate != null && fiscalDate != null){
		                            		if(FeedsUtil.daysDiff(asOfDate, fiscalDate) == 0){
		                            		
		                            			if(fiscalGLDataMap.containsKey(portFundNumber)){
		                            				BigDecimal bd = fiscalGLDataMap.get(portFundNumber);
		                            				fiscalGLDataMap.put(portFundNumber, bd.add(endBalA,mc));		                            			
		                            			}else{
		                            				fiscalGLDataMap.put(portFundNumber, endBalA);
		                            			}                            			
		                            		}
	                            		}
	                            	}
	                            }
	                     }                            
	        });        
	 }

	private boolean isFiscal(Date currValDate, Date priorValDate,Date prEndFiscalYrDate) {

		boolean isFiscal = false;

		if(FeedsUtil.daysDiff(prEndFiscalYrDate, currValDate) > 0 && 
		   FeedsUtil.daysDiff(priorValDate, prEndFiscalYrDate) >= 0){
			
			isFiscal = true;			
		}
		return isFiscal;
	}	
	
	private Collection<FundPortfolioData> getFundPortfolios(String strAsOfDay,String exttKndCode, String srcCode) throws Exception {
		
		Util.log(this,"Starting fundPortfolio.getFundPortfolioSet");
		
		FundPortfolio fundPortfolio = (FundPortfolio)ApplicationContext.getContext().getBean("FundPortfolio");
		
		Criteria fundPortfolioCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addAsOfDateFilter(strAsOfDay, Operator.OPERATOR_EQ));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addExtractKindCodeFilter(exttKndCode, Operator.OPERATOR_EQ));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addSourceCodeFilter(srcCode,Operator.OPERATOR_IN));

		// Sorting by Fund Number
		OrderByCriteria orderByCriteria = (OrderByCriteria)ApplicationContext.getContext().getBean("OrderByCriteria");
		orderByCriteria.addOrderByOn(PositionData.FIELD_fundNumber).asc();
	    Collection<FundPortfolioData> funds = fundPortfolio.getFundPortfolioSet(new String[0], fundPortfolioCriteria,orderByCriteria);
		
    	Util.log(this,"Fund portfolio record count:" + (funds != null ? funds.size():0));
    
		return funds;
	}	

	private ArrayList<FundAccountBalanceData> getAssetsBalance() throws Exception {
		assetsBalance = (FundBalance)ApplicationContext.getContext().getBean("FundBalance");
		fundBalanceCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		fundBalanceCriteria.addCriterion(FundAccountBalanceCriteriaBuilder.addExtractKindCodeFilter(extTypeCode,Operator.OPERATOR_EQ));
		fundBalanceCriteria.addCriterion(FundAccountBalanceCriteriaBuilder.addAsOfDateFilter(todayStr, Operator.OPERATOR_EQ));
		fundBalanceCriteria.addCriterion(FundAccountBalanceCriteriaBuilder.addCostBasisCodeFilter("FA", Operator.OPERATOR_EQ));
		fundBalanceCriteria.addCriterion(FundAccountBalanceCriteriaBuilder.addFundShareClassNumberFilter("0", Operator.OPERATOR_EQ));
		fundBalanceCriteria.addCriterion(FundAccountBalanceCriteriaBuilder.addSourceCodeFilter(dfc.getSourceCode(), Operator.OPERATOR_IN));
		orderByCriteria = (OrderByCriteria)ApplicationContext.getContext().getBean("OrderByCriteria");
		orderByCriteria.addOrderByOn(FundAccountBalanceData.FIELD_fundNumber).asc();
		orderByCriteria.addOrderByOn(FundAccountBalanceData.FIELD_portfolioFundNumber).asc();

		ArrayList<FundAccountBalanceData> focasIOFunds = (ArrayList<FundAccountBalanceData>) assetsBalance.getFundBalanceSet(fundNumberSet, fundBalanceCriteria, orderByCriteria);
		return focasIOFunds;
	}
	
	private void cacheUniqueFundNumbers(Collection<FundPortfolioData> funds) throws Exception {
		
		if(funds == null || funds.isEmpty()) return;
		
		Set<String> fundNumbersSet = new LinkedHashSet<String>();
		newConvFundNumbersSet = new LinkedHashSet<String>();
		for(FundPortfolioData fundData : funds){
			
			if(fundData.getFundNumber() != null){
				
				String fundNumber = String.valueOf(fundData.getFundNumber().longValue());
				Date cnvDate = fundData.getConversionDate();
				if(cnvDate != null){
	
					DateTime cnvDay = new DateTime(cnvDate);
					DateTime asOfDay = new DateTime(fundData.getAsOfDate());
					if(StringUtils.equals(cnvDay.toString(dft1),asOfDay.toString(dft1))){
	
						newConvFundNumbersSet.add(fundNumber);
					}
				}
				fundNumbersSet.add(fundNumber);
			}		
		}		
	}				
	
	/**
	 * 
	 * @param funds
	 * @param recordCount
	 * @param asOfDateStr
	 * @return recordCount
	 */
	private int writeContent(Collection<FundAccountBalanceData> funds ,int recordCount , String asOfDateStr){
			
		try{
			for (FundAccountBalanceData row : funds) {		
				
				String record = getFormattedFundNumber(row)+ 											//FUND-NUMBER
						   getNAVAmount(row)+   														//NET-ASSET-AMOUNT
						   getFundMarketValueAmt(row)+													//FUND-MARKET-VALUE
						   getGrossTotalAssetAmount(row)+												//TOTAL-ASSETS
						   getOtherAssetAmount(row)+													//NET-OTHER-ASSETS
						   getCapitalSharesOutstandingQuantity(row)+									//SHARES-OUTSTANDING
						   getInvestableCashAmount(row)+												//INVESTABLE-CASH
						   getTotalAccruedIncomeAmount(row)+											//ACCRUED-INCOME
						   getAccruedInterestAmountBase(row)+											//ACCRUED-INTEREST
						   getGrossTotalLiabilityAmount(row)+											//TOTAL-LIABILITY
						   getAdjustedTotalAssetAmount(row)+											//ADJUSTED-TOTAL-ASSETS
						   getAccruedExpenseAmountBase(row)+											//EXP_YTD_TOTAL
						   getExpenseBaseValueFormat(row) + 											//EXP_DAILY_TOTAL . FACDAL - 2185.
						   getAccountLevelTradedMarketValue(row)+										//MV_CURRENCY
						   getAccountLevelForeignForwardCurrency(row)+									//MV_CURRENCY_CONTRACTS
						   getOriginatingSystemIdentifier(row);											//ORIGINATING-SYSTEM
				fw.writeRecord(record);
				if (geodeFunds.contains(row.getPortfolioFundNumber())) {
					fwGeode.writeRecord(record);
				}
				recordCount++;
				
			}
		}catch (Exception e) {
				Util.log(this.getClass(), e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				e.printStackTrace();
		}
		return recordCount;
	}
	
	private Double calculateCurrentGL(String key,Map<String, Double[]> map) {	
		
		if(StringUtils.isBlank(key) || map == null) return null;
		
		Double result = 0.0;
		
		Double [] endBalAArray = map.get(key);
		
		if(endBalAArray == null){
			
			return null;
		}
		
		Double currEndBalA  = endBalAArray[0];
		Double priorEndBalA = endBalAArray[1];
		BigDecimal bd2 = new BigDecimal("0.0");
		
		if(!fiscalGLDataMap.isEmpty() && fiscalGLDataMap.containsKey(key) ){
			
			bd2 = fiscalGLDataMap.get(key);
		}
		
		BigDecimal bd1 = new BigDecimal(currEndBalA!=null?currEndBalA.toString():"0.0");
		BigDecimal bd3 = new BigDecimal(priorEndBalA!=null?priorEndBalA.toString():"0.0");
		result = bd1.add(bd2.subtract(bd3)).doubleValue();
				
		return result;
	}	
	
	
	private String getFormattedFundNumber(FundAccountBalanceData row){		
		if(row.getPortfolioFundNumber() != null && row.getPortfolioFundNumber().longValue() == 0 && row.getFundNumber() != null)
			return fmt15.format(row.getFundNumber());
		
		return fmt15.format(row.getPortfolioFundNumber() != null ? row.getPortfolioFundNumber():new Long(0));
	}
	
	private String getFundNumber(FundAccountBalanceData row){		
		if(row.getPortfolioFundNumber() != null && row.getPortfolioFundNumber().longValue() == 0 && row.getFundNumber() != null)
			return (row.getFundNumber().toString());
		
		return (row.getPortfolioFundNumber() != null ? row.getPortfolioFundNumber():new Long(0)).toString();
	}
	
	private String getKeyFromGLAccount(GlAccountData row) {			
		return (row.getPortfolioFundNumber() != null ?row.getPortfolioFundNumber():new Long(0)).toString();
	}
	
	private String getNAVAmount(FundAccountBalanceData row){
		
		if(row.getTotalNetAssetsAmount() == null){
			
			return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));
		}
		
		Double value = row.getTotalNetAssetsAmount();
		return value<0.0?replaceDecimalWithZero(fmt18_decimal.format(value)):replaceDecimalWithZero(fmt18_decimal_positive.format(value));
	}
	
	private String getFundMarketValueAmt(FundAccountBalanceData row) throws Exception{
		
		//FACDAL - 1710 , FACDAL - 2220 , FACDAL-2934
		if(row.getSourceCode().equals(Constants.FUND_TYPE_INVESTONE_FIE)){
			String key = getFundNumber(row);	
			if(fundMarketValueHashMap.containsKey(key)){				
				Double value = fundMarketValueHashMap.get(key).doubleValue();
				BigDecimal shortPosnMktValue = shortPosnMktValueMap.get(key);
				if(shortPosnMktValue != null){
					
					BigDecimal bd1 = value != null ? new BigDecimal(value.toString()) : new BigDecimal("0.0");
					value = bd1.subtract(shortPosnMktValue).doubleValue();
				}
				return value<0.0?replaceDecimalWithZero(fmt18_decimal.format(value)):replaceDecimalWithZero(fmt18_decimal_positive.format(value));
			}				
				return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));
			
		} else if(row.getSourceCode().equals(Constants.FUND_TYPE_INVESTONE_MM)){
			BigDecimal bd1 = new BigDecimal(row.getNetAssetAtMarketValue()!=null?row.getNetAssetAtMarketValue().toString():"0.0");
			BigDecimal bd2 = new BigDecimal(row.getTotalNetAssetsAmount()!=null?row.getTotalNetAssetsAmount().toString():"0.0");
			String key = getFundNumber(row);
			BigDecimal value = new BigDecimal("0.0");
			if(fundMarketValueHashMap.containsKey(key)){				
				value = fundMarketValueHashMap.get(key);
			}
			BigDecimal bd3 = new BigDecimal("0.0");
			bd3 = bd2.subtract(value);
			Double marketValueAmountBase = bd1.subtract(bd3).doubleValue();
			BigDecimal shortPosnMktValue = shortPosnMktValueMap.get(key);
			if(shortPosnMktValue != null){
				
				BigDecimal bd4 = marketValueAmountBase != null ? new BigDecimal(marketValueAmountBase.toString()) : new BigDecimal("0.0");
				marketValueAmountBase = bd4.subtract(shortPosnMktValue).doubleValue();
			}
			return marketValueAmountBase<0.0?replaceDecimalWithZero(fmt18_decimal.format(marketValueAmountBase)):
			     							 replaceDecimalWithZero(fmt18_decimal_positive.format(marketValueAmountBase));
		}	else{	
			if(row.getMarketValueAmountBase() != null){				
				return row.getMarketValueAmountBase()<0.0?replaceDecimalWithZero(fmt18_decimal.format(row.getMarketValueAmountBase())):replaceDecimalWithZero(fmt18_decimal_positive.format(row.getMarketValueAmountBase()));
			}
			return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));			
		}
	}	

	private Collection<GlAccountData> getGLAccountData(String[] fundNumbers,String asOfDate, String srcCode, String costBasis, String exttCode,String shrClNumber) throws Exception {
		
		Criteria glAccountCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addAsOfDateFilter(asOfDate,Operator.OPERATOR_EQ));
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addSourceCodeFilter(srcCode,Operator.OPERATOR_IN));
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addCostBasisCodeFilter(costBasis, Operator.OPERATOR_EQ));
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addExtractKindCodeFilter(exttCode, Operator.OPERATOR_EQ));
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addShareClassNumberFilter(shrClNumber, Operator.OPERATOR_REGEX));
		
		OrderByCriteria orderByCriteria = (OrderByCriteria)ApplicationContext.getContext().getBean("OrderByCriteria");
		orderByCriteria.addOrderByOn(GlAccountData.FIELD_fundNumber).asc();
		orderByCriteria.addOrderByOn(GlAccountData.FIELD_portfolioFundNumber).asc();
		
		GLAccount glAccount = (GLAccount)ApplicationContext.getContext().getBean("GLAccount");
		Collection<GlAccountData> glAccountDataList = glAccount.getGlAccountSetByFundNumber(fundNumbers, glAccountCriteria, null);
		Util.log(this,"Total GL Account Data records = "+(glAccountDataList != null ? glAccountDataList.size():0));
		return glAccountDataList;
	}
	
	private Collection<GlAccountData> gatherGLDataFromRegEx(ArrayList<String> fundNumberList , String GLAccountNumber, String runDay,String srcCode, String costBasis, String exttCode) throws Exception{
		
		String[] fNumbers = fundNumberList.toArray(new String[fundNumberList.size()]);
				
		Criteria glAccountCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addAsOfDateFilter(runDay,Operator.OPERATOR_EQ));
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addSourceCodeFilter(srcCode,Operator.OPERATOR_IN));
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addCostBasisCodeFilter(costBasis, Operator.OPERATOR_EQ));
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addExtractKindCodeFilter(exttCode, Operator.OPERATOR_EQ));
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addGeneralLedgerAccountNumberFilter(GLAccountNumber,Operator.OPERATOR_REGEX));
		glAccountCriteria.addCriterion(GlAccountCriteriaBuilder.addShareClassNumberFilter("00",Operator.OPERATOR_EQ));
			
		GLAccount glAccount = (GLAccount)ApplicationContext.getContext().getBean("GLAccount");
		Collection<GlAccountData> glAccountDataList = glAccount.getGlAccountSetByFundNumber(fNumbers, glAccountCriteria, null);			
				 
		return glAccountDataList;
	}

	//Below method aggregates amounts from GL Account Data for every PORT_FUND_N
	private void cacheForGLBal(GlAccountData glAcctData, boolean priorDay, Map<String, Double[]> map, boolean day1) {
			
		
		if (priorDay && day1) {
			return;
		}
		
		String key = String.valueOf(glAcctData.getPortfolioFundNumber().longValue()).trim();
		Double endBalA = glAcctData.getEndBalanceAmount();
		Double startBalA = glAcctData.getStartBalanceAmount();
		Double result = null;
		Double[] endBalAArray = map.get(key); 

		if(endBalAArray == null){

			endBalAArray = new Double[2]; 
		}
		
		if(priorDay && (!day1)){
			Double pdEndBalA = endBalAArray[1];
			BigDecimal bd1 = new BigDecimal(pdEndBalA != null?pdEndBalA.toString():"0.0");
			BigDecimal bd2 = new BigDecimal(endBalA != null?endBalA.toString():"0.0");
			result = bd1.add(bd2).doubleValue();
			endBalAArray[1] = result;			
		} else {
			
			Double currEndBalA = endBalAArray[0];
			BigDecimal bd1 = new BigDecimal(currEndBalA != null?currEndBalA.toString():"0.0");
			BigDecimal bd2 = new BigDecimal(endBalA != null?endBalA.toString():"0.0");
			result = bd1.add(bd2).doubleValue();
			endBalAArray[0] = result;
			
			if(day1){
				Double pdStrtBalA = endBalAArray[1];
				BigDecimal bd3 = new BigDecimal(pdStrtBalA != null?pdStrtBalA.toString():"0.0");
				BigDecimal bd4 = new BigDecimal(startBalA != null ? startBalA.toString():"0.0");
				result = bd3.add(bd4).doubleValue();
				endBalAArray[1] = result;
			}			
		}
		
		map.put(key, endBalAArray);
	}	
	
	private String getGrossTotalAssetAmount(FundAccountBalanceData row){
		//FACDAL- 1734, FACDAL-2934
		if(isIOFund(row)){			
			Double grossTA = getTotalAsset(row);
			String key = getFundNumber(row);
			BigDecimal shortPosnMktValue = shortPosnMktValueMap.get(key);
			if(shortPosnMktValue != null){
								
				BigDecimal bd1 = grossTA != null ? new BigDecimal(grossTA.toString()) : new BigDecimal("0.0");
				grossTA = bd1.subtract(shortPosnMktValue).doubleValue();
			}
			return grossTA<0.0?replaceDecimalWithZero(fmt18_decimal.format(grossTA)):
				  replaceDecimalWithZero(fmt18_decimal_positive.format(grossTA));
		}	
			if(row.getGrossTotalAssetAmount() != null)
				return row.getGrossTotalAssetAmount()<0.0?replaceDecimalWithZero(fmt18_decimal.format(row.getGrossTotalAssetAmount())):replaceDecimalWithZero(fmt18_decimal_positive.format(row.getGrossTotalAssetAmount()));				
				
			return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));		
	}

	private Double getTotalAsset(FundAccountBalanceData row) {
		//Double totalAssets = 0.0;
		Double grossTA = 0.0;
		BigDecimal bd1 = new BigDecimal("0.0");
		BigDecimal bd2 = new BigDecimal("0.0");
		BigDecimal bd3 = new BigDecimal("0.0");
		String key = getFundNumber(row);			
		
		if(fundTotalAssetsMap.containsKey(key)){				
			bd1 = fundTotalAssetsMap.get(key) != null ? fundTotalAssetsMap.get(key):new BigDecimal("0.0");			
		}
		if(adjTotalAssetsMap.containsKey(key)){
			bd2 = adjTotalAssetsMap.get(key) != null ? adjTotalAssetsMap.get(key):new BigDecimal("0.0");			
		}
		if(!currHldgSPOTMap.isEmpty() && currHldgSPOTMap.containsKey(key)){			
			bd3 = currHldgSPOTMap.get(key) != null ? currHldgSPOTMap.get(key):new BigDecimal("0.0");			
		}			
		grossTA = bd1.subtract(bd2).subtract(bd3).doubleValue();		
		return grossTA;
	}
	
		
	private String getOtherAssetAmount(FundAccountBalanceData row){
		
		if(isIOFund(row)){
				
			Double netOtherAssets = 0.0;
			if(row.getTotalNetAssetsAmount() != null && row.getMarketValueAmountBase() != null) {
				//netOtherAssets = row.getTotalNetAssetsAmount() - row.getMarketValueAmountBase();
				//FACDAL - 1709
				String key = getFundNumber(row);
				Double value = 0.0;
				if(fundMarketValueHashMap.containsKey(key)){				
					value = fundMarketValueHashMap.get(key).doubleValue();
				}
				netOtherAssets = row.getTotalNetAssetsAmount() - value;
				BigDecimal shortPosnMktValue = shortPosnMktValueMap.get(key);
				if(shortPosnMktValue != null){
									
					BigDecimal bd1 = netOtherAssets != null ? new BigDecimal(netOtherAssets.toString()) : new BigDecimal("0.0");
					netOtherAssets = bd1.add(shortPosnMktValue).doubleValue();
				}
			}
			return netOtherAssets < 0.0 ? replaceDecimalWithZero(fmt18_decimal.format(netOtherAssets)) :
												  replaceDecimalWithZero(fmt18_decimal_positive.format(netOtherAssets));
		}				
			if(row.getPrepricingTotalNetOtherAssetsAmount() != null){
						
				return row.getPrepricingTotalNetOtherAssetsAmount().doubleValue() < 0.0 ? 
						replaceDecimalWithZero(fmt18_decimal.format(row.getPrepricingTotalNetOtherAssetsAmount())) : 
						replaceDecimalWithZero(fmt18_decimal_positive.format(row.getPrepricingTotalNetOtherAssetsAmount())); 
			}
						
				return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));		
	}
	
	private String getCapitalSharesOutstandingQuantity(FundAccountBalanceData row){
		
		if(row.getCapitalSharesOutstandingQuantity() != null){
			return	replaceNegativeWithPositive(replaceDecimalWithZero(fmt19_decimal_positive.format(row.getCapitalSharesOutstandingQuantity())));		
		}
		
		return replaceDecimalWithZero(fmt19_decimal_positive.format(0.0));
	}
	
	
	private String getInvestableCashAmount(FundAccountBalanceData row){
		
		if(isIOFund(row)){
			
			Double value = 0.0;
			BigDecimal bd1 = null;
			BigDecimal bd2 = null;
			String key = getFundNumber(row);
			
			if(StringUtils.isNotBlank(key)){				
				if(fundInvestableCashHashMap1 != null){					
					bd1 = fundInvestableCashHashMap1.get(key);					
				}
				if(fundInvestableCashHashMap2 != null){					
					bd2 = fundInvestableCashHashMap2.get(key);					
				}
				if(bd1 == null){					
					bd1 = new BigDecimal("0.0");
				}
				if(bd2 == null){					
					bd2 = new BigDecimal("0.0");
				}
				value = bd1.subtract(bd2).doubleValue();
			}
			return value < 0.0 ?replaceDecimalWithZero(fmt18_decimal.format(value)):replaceDecimalWithZero(fmt18_decimal_positive.format(value));
		}
		if(row.getInvestableCashAmount() != null){
			return  row.getInvestableCashAmount()<0.0?replaceDecimalWithZero(fmt18_decimal.format(row.getInvestableCashAmount())):
				  replaceDecimalWithZero(fmt18_decimal_positive.format(row.getInvestableCashAmount()));				
		}
		return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));		
	}
	
	private String getTotalAccruedIncomeAmount(FundAccountBalanceData row){
		
		if(isIOFund(row)){					
			String key = getFundNumber(row);
					
			if(fundAccruedIncomeHashMap.containsKey(key)){						
				Double value = fundAccruedIncomeHashMap.get(key).doubleValue();
				return value<0.0?replaceDecimalWithZero(fmt18_decimal.format(value)):replaceDecimalWithZero(fmt18_decimal_positive.format(value));
			}						
				return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));			
		}
		if(row.getTotalAccruedIncomeAmount() != null){
			return  row.getTotalAccruedIncomeAmount()<0.0?replaceDecimalWithZero(fmt18_decimal.format(row.getTotalAccruedIncomeAmount())):
														  replaceDecimalWithZero(fmt18_decimal_positive.format(row.getTotalAccruedIncomeAmount()));
		}
		return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));
		
	}
	
	private String getAccruedInterestAmountBase(FundAccountBalanceData row){
		
		if(isIOFund(row)){
					
			String key = getFundNumber(row);
					
			if(fundAccruedInterestHashMap.containsKey(key)){
						
				Double value = fundAccruedInterestHashMap.get(key).doubleValue();
				return value<0.0?replaceDecimalWithZero(fmt18_decimal.format(value)):replaceDecimalWithZero(fmt18_decimal_positive.format(value));
			}					
				return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));
			
		}				
			if(row.getGeneralLedgerAccountCurrentBalanceAmountBase() != null){
				return row.getGeneralLedgerAccountCurrentBalanceAmountBase()<0.0?replaceDecimalWithZero(fmt18_decimal.format(row.getGeneralLedgerAccountCurrentBalanceAmountBase())):
					 replaceDecimalWithZero(fmt18_decimal_positive.format(row.getGeneralLedgerAccountCurrentBalanceAmountBase()));				
			}
			return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));		
	}
	
	private String getGrossTotalLiabilityAmount(FundAccountBalanceData row){
		
		Double grossTotalLiability = row.getGrossTotalLiabilityAmount() != null ? row.getGrossTotalLiabilityAmount():new Double(0.0);
		//FACDAL - 2821
		if(isIOFund(row)){
			BigDecimal bd1 = new BigDecimal(grossTotalLiability.toString());
			BigDecimal bd2 = new BigDecimal("0.0");
			BigDecimal bd3 = new BigDecimal("0.0");
			String key = getFundNumber(row);	
			if(adjTotalAssetsMap.containsKey(key)){
				bd2 = adjTotalAssetsMap.get(key) != null ? adjTotalAssetsMap.get(key):new BigDecimal("0.0");
			}
			if(!currHldgSPOTMap.isEmpty() && currHldgSPOTMap.containsKey(key)){				
				bd3 = currHldgSPOTMap.get(key) != null ? currHldgSPOTMap.get(key):new BigDecimal("0.0");
			}	
			grossTotalLiability = bd1.subtract(bd2).subtract(bd3).doubleValue();
			
			BigDecimal shortPosnMktValue = shortPosnMktValueMap.get(key);
			if(shortPosnMktValue != null){
								
				BigDecimal bd4 = grossTotalLiability != null ? new BigDecimal(grossTotalLiability.toString()) : new BigDecimal("0.0");
				grossTotalLiability = bd4.subtract(shortPosnMktValue).doubleValue();
			}
			return grossTotalLiability<0.0?replaceDecimalWithZero(fmt18_decimal.format(grossTotalLiability)):
				  replaceDecimalWithZero(fmt18_decimal_positive.format(grossTotalLiability));
		}	
		
		return grossTotalLiability<0.0?replaceDecimalWithZero(fmt18_decimal.format(grossTotalLiability)):
													replaceDecimalWithZero(fmt18_decimal_positive.format(grossTotalLiability));
	}
	
	private String getAdjustedTotalAssetAmount(FundAccountBalanceData row){
	
		//FACDAL - 1734, FACDAL-2934
		if(isIOFund(row)){
					
			Double grossTA = getTotalAsset(row);
			String key = getFundNumber(row);
			BigDecimal shortPosnMktValue = shortPosnMktValueMap.get(key);
			if(shortPosnMktValue != null){
								
				BigDecimal bd1 = grossTA != null ? new BigDecimal(grossTA.toString()) : new BigDecimal("0.0");
				grossTA = bd1.subtract(shortPosnMktValue).doubleValue();
			}
			return grossTA<0.0?replaceDecimalWithZero(fmt18_decimal.format(grossTA)):
				  replaceDecimalWithZero(fmt18_decimal_positive.format(grossTA));
		}					
			if(row.getAccountLevelCurrentAdjustedTotalAssetsAmountComplianceBase() != null){
				Double value = row.getAccountLevelCurrentAdjustedTotalAssetsAmountComplianceBase();
				return value<0.0?replaceDecimalWithZero(fmt18_decimal.format(value)):replaceDecimalWithZero(fmt18_decimal_positive.format(value));		
			}
			return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));		
	}
	
	private String getAccruedExpenseAmountBase(FundAccountBalanceData row){
		
		Double accruedExpenseAmountBase = 0.0;
		
		if(isIOFund(row)){
			try{
				String key = getFundNumber(row);
				if(fundExpenseYTDTotal.containsKey(key)){
					accruedExpenseAmountBase = fundExpenseYTDTotal.get(key).doubleValue();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return replaceDecimalWithZero(fmt18_decimal_positive.format(accruedExpenseAmountBase));
		
	}
	
	private String getExpenseBaseValueFormat(FundAccountBalanceData row){
		
		if(isIOFund(row)){			
			//FACDAL-2357 - Always 0 for BOD
			if(StringUtils.isNotBlank(row.getExtractKindCode()) && StringUtils.equals(row.getExtractKindCode().trim(),"BOD")){				
				return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));
			}			
			if(row.getPortfolioFundNumber()!=null){		
				Double result = calculateCurrentGL(row.getPortfolioFundNumber().toString(),fundExpDailyTotal);
				if(result != null){
					return result<0.0?replaceDecimalWithZero(fmt18_decimal.format(result)):
												   replaceDecimalWithZero(fmt18_decimal_positive.format(result));
					}
				return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));				
			} 
			return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));			
		}		
		return replaceDecimalWithZero(fmt18_decimal_positive.format(0.0));
		
	}
	
	private String getAccountLevelTradedMarketValue(FundAccountBalanceData row){
		
		Double mvCurrency = 0.0;
		
		if(isIOFund(row)){
		
			try{
				String key = getFundNumber(row);
				
				if(StringUtils.isNotEmpty(key)){
					
					BigDecimal bd1 = fundCurrencyHashMap.get(key);
					BigDecimal bd2 = currencySpotsMap.get(key);
					bd1 = (bd1 != null) ? bd1 : new BigDecimal("0.0");
					bd2 = (bd2 != null) ? bd2 : new BigDecimal("0.0");
					mvCurrency = bd1.add(bd2).doubleValue();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}		
		return mvCurrency<0?replaceDecimalWithZero(fmt18_decimal.format(mvCurrency)):replaceDecimalWithZero(fmt18_decimal_positive.format(mvCurrency));
	}

	private String getAccountLevelForeignForwardCurrency(FundAccountBalanceData row){
	
		Double acctFrgnFwdCurrAmount = 0.0;
		BigDecimal bd1 = new BigDecimal("0.0");
		if(isIOFund(row)){
			try{
				
				String key = getFundNumber(row);
				if(currencyFWDMap != null && StringUtils.isNotBlank(key)){
					
					bd1 = currencyFWDMap.get(key);
					acctFrgnFwdCurrAmount = bd1 != null ? bd1.doubleValue() : 0.0;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}				
		return acctFrgnFwdCurrAmount<0?replaceDecimalWithZero(fmt18_decimal.format(acctFrgnFwdCurrAmount)):replaceDecimalWithZero(fmt18_decimal_positive.format(acctFrgnFwdCurrAmount));		
		
	}
	
	private String getOriginatingSystemIdentifier(FundAccountBalanceData row){
		
		String originatingSystemIdentifier = row.getSourceCode();
		if(StringUtils.isBlank(originatingSystemIdentifier)){
		
			Util.log(this,"Originating system for account = "+row.getPortfolioFundNumber()+" not found",Constants.LOG_LEVEL_WARNING,null);
			return "";
		}
		return originatingSystemIdentifier.substring(0,1); 
	}
	
	private String replaceDecimalWithZero(String strObj){
		return strObj.replace(".", "");
	}
	
	private String replaceNegativeWithPositive (String strObj){
		return strObj.replace("-", "+");
	}
	
	private class Comparable implements Comparator<FundAccountBalanceData>{  
		private Long getFundNumber(FundAccountBalanceData obj){
			return obj.getPortfolioFundNumber() != null ? obj.getPortfolioFundNumber():new Long(0);			
		}
		
        @Override    
        public int compare(FundAccountBalanceData f1, FundAccountBalanceData f2) {         
        	int result = 0;
           	Long firstFundNumber  = getFundNumber(f1);
        	Long secondFundNumber = getFundNumber(f2);
        	if(firstFundNumber.equals(secondFundNumber)){
        	 			result = 0;
        		}else
        			result = firstFundNumber.compareTo(secondFundNumber);
           	return result;
        		} 
		}
	
	private boolean isIOFund (FundAccountBalanceData row) {
		boolean bIOFund = false;
		String src = null;

		if (row != null) {
			src = row.getSourceCode();
			if(src.equalsIgnoreCase(Constants.FUND_TYPE_INVESTONE_MM)||
					src.equalsIgnoreCase(Constants.FUND_TYPE_INVESTONE_FIE)){
				bIOFund = true;
			} 
		}
		return bIOFund;
	}
	
	private String computeSrcCode() {

		String srcCode = Constants.FUND_TYPE_INVESTONE_MM;

		if(StringUtils.contains(dfc.getSourceCode(), Constants.FUND_TYPE_INVESTONE_FIE)){

			srcCode = Constants.FUND_TYPE_INVESTONE_FIE;
		} else {

			srcCode = Constants.FUND_TYPE_INVESTONE_MM;
		}

		return srcCode;
	}
	
	private void cacheTaxlotCurrHldgFWDsData(String todayStr,String srcCode,String costBasis,String extTypeCode) throws Exception {
		
		currencyFWDMap = new HashMap<String,BigDecimal>();
				
		String sql = "PKG_FEED_ASSETS.GET_TAXLOT_CURR_HDG_FWDS";
		String[] params = new String[4];
		params[0] = todayStr;
		params[1] = extTypeCode;
		params[2] = srcCode;
		params[3] = Constants.COST_BASIS_CODE_FA;
		List<DynaBean> list = null;

		try {
			Util.log(this,"calling sql "+sql);
			for(int index = 0 ; index < params.length; index ++) {
				Util.log(this,"input parameters params["+index+"] "+params[index]);
			}
			long startTime = System.currentTimeMillis();
			list = DBUtil.executePlQuery(sql, params,taxlotFetchSize);
			long endTime = System.currentTimeMillis();
			Util.log(this,"Time taken to execute sql "+sql+ " is "+(endTime-startTime)/1000 +" seconds");
			
			Util.log(this,"Total records for GET_TAXLOT_CURR_HDG_FWDS "+ (list != null ? list.size() : 0));
			
			if(list != null && list.size() >= 80000) {
				Util.log(this, "Number of taxlot records for FWD's has crossed 80000. " +
						"Please monitor jvm-memory to check whether there is threat to heap space ", 
						Constants.LOG_LEVEL_WARNING,null);
			}
			
			if (list != null && list.size() > 0) {
				Iterator<DynaBean> iterator = list.iterator();
				while (iterator.hasNext()) {
					DynaBean currHldgFWDData = iterator.next();
					String portFundNo = String.valueOf(currHldgFWDData.get("PORT_FUND_N") != null ? currHldgFWDData.get("PORT_FUND_N"):"").trim();
					if(StringUtils.isNotBlank(portFundNo)) {
						if(currencyFWDMap.get(portFundNo) != null){				
							
							BigDecimal bd = new BigDecimal( currHldgFWDData.get("TRD_MKT_VAL") != null ? currHldgFWDData.get("TRD_MKT_VAL").toString() : "0.0");
							BigDecimal finalvalue = currencyFWDMap.get(portFundNo);
							finalvalue = finalvalue.add(bd);
							currencyFWDMap.put(portFundNo,finalvalue);
						}else{
							BigDecimal bd = new BigDecimal(currHldgFWDData.get("TRD_MKT_VAL") != null ? currHldgFWDData.get("TRD_MKT_VAL").toString() : "0.0");
							currencyFWDMap.put(portFundNo,bd);					
						}			
					}
				}
			}
			list = null;
			
		} catch (Exception e) {
			Util.log(this.getClass().getName(), "Error loading data for SQL: " + sql, Constants.LOG_LEVEL_ERROR, e);
			throw e;
		}		
	}
	
	private void cacheTaxlotCurrHldgSPOTsData(String todayStr,String srcCode,String costBasis,String extTypeCode) throws Exception {
		
		currencySpotsMap = new HashMap<String,BigDecimal>();
				
		String sql = "PKG_FEED_ASSETS.GET_TAXLOT_CURR_HDG_SPOTS";
		String[] params = new String[4];
		params[0] = todayStr;
		params[1] = extTypeCode;
		params[2] = srcCode;
		params[3] = Constants.COST_BASIS_CODE_FA;
		List<DynaBean> list = null;

		try {
			Util.log(this,"calling sql "+sql);
			for(int index = 0 ; index < params.length; index ++) {
				Util.log(this,"input parameters params["+index+"] "+params[index]);
			}
			long startTime = System.currentTimeMillis();
			list = DBUtil.executePlQuery(sql, params,taxlotFetchSize);
			long endTime = System.currentTimeMillis();
			Util.log(this,"Time taken to execute sql "+sql+ " is "+(endTime-startTime)/1000 +" seconds");
			
			Util.log(this,"Total records for GET_TAXLOT_CURR_HDG_SPOTS "+ (list != null ? list.size() : 0));
			
			if(list != null && list.size() >= 80000) {
				Util.log(this, "Number of taxlot records for SPOT'S has crossed 80000. " +
						"Please monitor jvm-memory to check whether there is threat to heap space ", 
						Constants.LOG_LEVEL_WARNING,null);
			}
			
			if (list != null && list.size() > 0) {
				Iterator<DynaBean> iterator = list.iterator();
				while (iterator.hasNext()) {
					DynaBean currHldgFWDData = iterator.next();
					String portFundNo = String.valueOf(currHldgFWDData.get("PORT_FUND_N") != null ? currHldgFWDData.get("PORT_FUND_N"):"").trim();
					if(StringUtils.isNotBlank(portFundNo)) {
						if(currencySpotsMap.get(portFundNo) != null){				
							
							BigDecimal bd = new BigDecimal( currHldgFWDData.get("TRD_MKT_VAL") != null ? currHldgFWDData.get("TRD_MKT_VAL").toString() : "0.0");
							BigDecimal finalvalue = currencySpotsMap.get(portFundNo);
							finalvalue = finalvalue.add(bd);
							currencySpotsMap.put(portFundNo,finalvalue);
						}else{
							BigDecimal bd = new BigDecimal(currHldgFWDData.get("TRD_MKT_VAL") != null ? currHldgFWDData.get("TRD_MKT_VAL").toString() : "0.0");
							currencySpotsMap.put(portFundNo,bd);					
						}			
					}				
				}
			}
			list = null;
			
		} catch (Exception e) {
			Util.log(this.getClass().getName(), "Error loading data for SQL: " + sql, Constants.LOG_LEVEL_ERROR, e);
			throw e;
		}		
	}
	
	public void populateCurrHldg(String srcCode) throws Exception {

		Util.log(this.getClass(),"Start executing populateCurrHldg ....");		
		String asOfDate = asOfDay.toString(dft3);
		currHldgSPOTMap = new HashMap<String, BigDecimal>(); 
		try {
			
			String sql = "{call  PKG_FEED_ASSETS_CASHAVAIL.GET_BASE_PAYABLE_AMT (?,?,?,?,?)}";
			Map<Integer, Object> inputs = new HashMap<Integer, Object>();
			inputs.put(1, asOfDate);
			inputs.put(2, extTypeCode);
			inputs.put(3, srcCode);
			inputs.put(4,Constants.COST_BASIS_CODE_FA);
			Map<Integer, Object> outputs = new HashMap<Integer, Object>();
			outputs.put(5, OracleTypes.CURSOR);
					
			long startTime = System.currentTimeMillis();
			DBUtil.executeStoredProcedure(sql , inputs , outputs);
			long endTime = System.currentTimeMillis();
			Util.log(this,"Time taken to execute sql "+sql+ " is "+(endTime-startTime)/1000 +" seconds");			
			
			@SuppressWarnings("unchecked")
			List<DynaBean> beanList = (List<DynaBean>) outputs.get(5);
			Util.log(this,"Total records for GET_BASE_PAYABLE_AMT "+ (beanList != null ? beanList.size() : 0));
			for (DynaBean dynaBean : beanList) {
				if (dynaBean.get("PORT_FUND_N") != null && dynaBean.get("BASE_PAYABLE") != null) {
					String portFundN = dynaBean.get("PORT_FUND_N").toString();
					BigDecimal basePayable = (BigDecimal) dynaBean.get("BASE_PAYABLE");					
					
					if(portFundN != null){
						currHldgSPOTMap.put(portFundN,basePayable);
					}
				}				
			}
			beanList = null;
			Util.log(this.getClass(),"End executing populateCurrHldg ....");	
		}
		catch (Exception e) {
			Util.log(this.getClass(), "Exception in populateCurrHldg(). Error occured during populateCurrHldg()", Constants.LOG_LEVEL_ERROR, e);
			throw e;
		}		
	}	
	
	public static void main(String[] args) throws Exception {

		String FEED_NAME = "BODFUNDASSETFEEDFIE";
		new Initialize();
		AssetsFeed feedApp = new AssetsFeed();
		feedApp.setFeedAppName(FEED_NAME);
		ConfigDBAccess.getStartupApps(FEED_NAME, null);

		feedApp.processMessage(50118);
		
		System.out.println("Feed generation completed");
		System.exit(0);
	}
	
}
