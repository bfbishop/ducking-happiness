package com.fidelity.dal.fundacct.feed.app;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StopWatch;

import com.fidelity.dal.fundacct.dalapi.beans.CurrHldgData;
import com.fidelity.dal.fundacct.dalapi.beans.CurrHldgSetlData;
import com.fidelity.dal.fundacct.dalapi.beans.FundPortfolioData;
import com.fidelity.dal.fundacct.dalapi.beans.PositionData;
import com.fidelity.dal.fundacct.dalapi.beans.SecurityData;
import com.fidelity.dal.fundacct.dalapi.beans.TaxlotData;
import com.fidelity.dal.fundacct.dalapi.criteria.Criteria;
import com.fidelity.dal.fundacct.dalapi.criteria.CurrHldgCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.CurrHldgSetlCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.FundPortfolioCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.Operator;
import com.fidelity.dal.fundacct.dalapi.criteria.OrderByCriteria;
import com.fidelity.dal.fundacct.dalapi.criteria.PositionCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.SecurityCriteriaBuilder;
import com.fidelity.dal.fundacct.dalapi.criteria.TaxlotCriteriaBuilder;
import com.fidelity.dal.fundacct.feed.admin.ApplicationContext;
import com.fidelity.dal.fundacct.feed.app.beans.CurrencyHoldingVO;
import com.fidelity.dal.fundacct.feed.bean.CurrencyHolding;
import com.fidelity.dal.fundacct.feed.bean.DALCalendar;
import com.fidelity.dal.fundacct.feed.bean.DALSecRefService;
import com.fidelity.dal.fundacct.feed.bean.FundPortfolio;
import com.fidelity.dal.fundacct.feed.bean.Position;
import com.fidelity.dal.fundacct.feed.bean.Security;
import com.fidelity.dal.fundacct.feed.bean.SettledCurrencyHolding;
import com.fidelity.dal.fundacct.feed.bean.Taxlot;
import com.fidelity.dal.fundacct.feed.framework.feed.DBUtil;
import com.fidelity.dal.fundacct.feed.framework.feed.FeedsUtil;
import com.fidelity.dal.fundacct.feed.framework.feed.RowHandler;
import com.fidelity.dal.fundacct.feed.framework.jms.JMSMessageProcessorBase;
import com.fidelity.dal.fundacct.feed.framework.util.Constants;
import com.fidelity.dal.fundacct.feed.framework.util.Util;


/**
 * @author a512859
 */

public abstract class AbstractCurrencyHoldingsFeed extends JMSMessageProcessorBase{

    protected final HashSet<Long> geodeFunds = new HashSet<Long>(Arrays.asList(new Long[]{
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

	
	protected final DateTimeFormatter dft = DateTimeFormat.forPattern("yyyy-MM-dd");
	protected final DateTimeFormatter dft_ddMMMyyyy = DateTimeFormat.forPattern("dd-MMM-yyyy");
	private final String FORWARD = "FORWARD";
	private final String SPOT = "SPOT   ";
	private final String CURR_BALANCE = "CURRBAL";
	private final String VALUATAION_RATE_RECEIVE = "VALUATAION-RATE-RECEIVE";
	private final String VALUATAION_RATE_DELIVERY = "VALUATAION-RATE-DELIVERY";
	private final String MARKET_VALUE_RECEIVE = "MARKET-VALUE-RECEIVE";
	private final String MARKET_VALUE_DELIVERY = "MARKET-VALUE-DELIVERY";
	private final String UNREAL_GAIN_LOSS_RECEIVE  = "UNREAL-GAIN-LOSS-RECEIVE";
	private final String UNREAL_GAIN_LOSS_DELIVERY = "UNREAL-GAIN-LOSS-DELIVERY";
	private final String TRD_REF_ID = "TRD_REF_ID";
	private final String AS_OF_D = "AS_OF_D";
	private final String CURR_CTRT_CUSP = "CURR_CTRT_CUSP";
	private final String TRX_EXEC_BRKR_C = "TRX_EXEC_BRKR_C";
	private final String UNSETTLED = "UNSETTLED";
	private final String SETTLED = "SETTLED";
	
	protected StopWatch stopWatch = null;
	protected DALCalendar dalCalendar = null;
	protected int recordCount=0;
	protected int totalUnsettledSpots = 0;
	protected int totalUnsettledForwards = 0;
	protected int totalSettledSpots = 0;
	protected int totalSettledForwards = 0;
	protected int totalCurrBalanaces = 0;
	protected int totalUnsettledCC = 0;
	protected int totalSettledCC = 0;
	

	/**
	 * This method caches Fund numbers and Fund Short Names
	 * @param funds
	 * @param fundNumbers
	 * @param shortNameMap
	 * @param valDateMap 
	 */
	public void cacheFromFunds(Collection<FundPortfolioData> funds,List<String> fundNumbers, 
								  Map<String, String> shortNameMap, Map<String, String> valDateMap) {

		if(funds == null || funds.isEmpty())
			return ;

		Set<String> fundNumbersSet = new LinkedHashSet<String>();

		Iterator<FundPortfolioData> iterator = funds.iterator();
		while(iterator.hasNext()){

			FundPortfolioData fundData = iterator.next();
			Long fundN = fundData.getFundNumber();
			Long portFundN = fundData.getPortfolioFundNumber();

			if(fundN != null && portFundN != null){
				
				fundNumbersSet.add(String.valueOf(fundN).trim());
				String key = getKeyFrmFund(fundData);
				shortNameMap.put(key,fundData.getShortName());
				valDateMap.put(key, fundData.getReportRequestDate());
			}
		}

		fundNumbers.addAll(new ArrayList<String>(fundNumbersSet));
	}	
	
	/**
	 * This method caches Report Request Date, which is used for VAL-DATE
	 * @param funds
	 * @param valDateMap
	 */
	protected Map<String,String> getValDate(Collection<FundPortfolioData> funds){
		
		Map<String,String> valDateMap = new HashMap<String,String>();
		if(funds == null || funds.isEmpty())
			return null;

		Iterator<FundPortfolioData> iterator = funds.iterator();
		while(iterator.hasNext()){

			FundPortfolioData fundData = iterator.next();
			String key = getKeyFrmFund(fundData);
			valDateMap.put(key,fundData.getReportRequestDate());
		}
		
		return valDateMap;
	}
	
	
	/**
	 * This method calls Sec Ref service to get FMR ID for SPOTS
	 * @return
	 * @throws Exception
	 */
	protected Map<String, String> cacheFmrIDForSpots() throws Exception{
		
		Map<String,String> spotCusipMap = null;
		DALSecRefService secRefService = (DALSecRefService)ApplicationContext.getContext().getBean("DALSecRefService");
		try{
			spotCusipMap = secRefService.getFmrIdFrmSecRef();
		}catch(Exception e){
			
			Util.log(this,"SQL call to SECREF failed. FMRID for SPOTS are null",Constants.LOG_LEVEL_ERROR, e);
			throw e;
		}
		
		return spotCusipMap;
	}

	
	/**
	 * This method returns Collection of records from INV1_FUND_PORT_SET_VW
	 * @param fundNumbers 
	 * @param srcCodes - Indicates Focas and IO Retail funds
	 * @param exttCode - End Of Day vs Beginning of Day
	 * @param asOfDay - BusinessDay
	 * @return A collection of FundPortfolioData
	 * @throws Exception
	 */
	protected Collection<FundPortfolioData> getFundPortfolios(String[] fundNumbers, String srcCodes,
															  String exttCode,
															  DateTime asOfDay) throws Exception {
		
		Util.log(this,"Starting fundPortfolio.getFundPortfolioSet");
		
		FundPortfolio fundPortfolio = (FundPortfolio)ApplicationContext.getContext().getBean("FundPortfolio");
		
		Criteria fundPortfolioCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addAsOfDateFilter(asOfDay.toString(dft), Operator.OPERATOR_EQ));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addExtractKindCodeFilter(exttCode, Operator.OPERATOR_EQ));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addSourceCodeFilter(srcCodes,Operator.OPERATOR_IN));
		fundPortfolioCriteria.addCriterion(FundPortfolioCriteriaBuilder.addCostBasisCodeFilter("FA",Operator.OPERATOR_EQ));

		// Sorting by Fund Number
		OrderByCriteria orderByCriteria = (OrderByCriteria)ApplicationContext.getContext().getBean("OrderByCriteria");
		orderByCriteria.addOrderByOn(PositionData.FIELD_fundNumber).asc();
	    Collection<FundPortfolioData> funds = fundPortfolio.getFundPortfolioSet(fundNumbers, fundPortfolioCriteria,orderByCriteria);
		
    	Util.log(this,"Fund portfolio record count:" + (funds != null ? funds.size():0));
    
		return funds;
	}
	
	
	/**
	 * This method returns Collection of records from POSN_SET_BY_FUND_N_VW 
	 * @param fundNumbers - An array of unique FUND_N for which Positions are retrieved
	 * @param strAsOfDate - Business Day
	 * @param srcCodes - Indicates Focas and IO Retail funds
	 * @param exttCode - End Of Day vs Beginning of Day
	 * @param cstBasisCode 
	 * @return Collection of PositionData
	 * @throws Exception
	 */
	protected Collection<PositionData> getPositions(String[] fundNumbers,
													String strAsOfDate, 
													String srcCodes, 
													String exttCode, 
													String cstBasisCode) throws Exception {
		
		Criteria positionsCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		positionsCriteria.addCriterion(PositionCriteriaBuilder.addAsOfDateFilter(strAsOfDate, Operator.OPERATOR_EQ));
		positionsCriteria.addCriterion(PositionCriteriaBuilder.addExtractKindCodeFilter(exttCode,Operator.OPERATOR_EQ));
		positionsCriteria.addCriterion(PositionCriteriaBuilder.addSourceCodeFilter(srcCodes,Operator.OPERATOR_IN));
		positionsCriteria.addCriterion(PositionCriteriaBuilder.addCostBasisCodeFilter(cstBasisCode,Operator.OPERATOR_EQ));
		
		Position position = (Position)ApplicationContext.getContext().getBean("Position");
		
		Collection<PositionData> posns = null;
		if(fundNumbers == null || fundNumbers.length == 0){
			
			posns = position.getPositionSetByFundNumber(new String[0], positionsCriteria);
		}else{
			
			posns = position.getPositionSetByFundNumber(fundNumbers, positionsCriteria);
		} 
		
		Util.log(this,"Total positions records  "+(posns != null ? posns.size() : 0));
		
		return posns;
	}
	
	
	/**
	 * This method returns Collection of records from TXLT_SET_BY_FUND_N_VW
	 * @param fundNumbers
	 * @param strAsOfDate
	 * @param srcCodes
	 * @param exttCode
	 * @param cstBasisCode
	 * @return Collection of TaxlotData
	 * @throws Exception
	 */
	protected Collection<TaxlotData> getTaxLots(String[] fundNumbers,
												String strAsOfDate, 
												String srcCodes, 
												String exttCode, 
												String cstBasisCode) throws Exception {
		
		Collection<TaxlotData> taxLots = null;
		
		Criteria txLtCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		txLtCriteria.addCriterion(TaxlotCriteriaBuilder.addAsOfDateFilter(strAsOfDate, Operator.OPERATOR_EQ));
		txLtCriteria.addCriterion(TaxlotCriteriaBuilder.addSourceCodeFilter(srcCodes, Operator.OPERATOR_IN));
		txLtCriteria.addCriterion(TaxlotCriteriaBuilder.addExtractKindCodeFilter(exttCode, Operator.OPERATOR_EQ));
		txLtCriteria.addCriterion(TaxlotCriteriaBuilder.addCostBasisCodeFilter(cstBasisCode,Operator.OPERATOR_EQ));
		
		Taxlot taxLot = (Taxlot)ApplicationContext.getContext().getBean("Taxlot");
		
		if(fundNumbers == null || fundNumbers.length == 0){
			
			taxLots = taxLot.getTaxlotSetByFundNumber(new String[0], txLtCriteria);
		}else{
			taxLots = taxLot.getTaxlotSetByFundNumber(fundNumbers, txLtCriteria);
		}
		Util.log(this,"Total TaxLots returned "+ (taxLots != null ? taxLots.size():0));
		
		return taxLots;
	}
	
	
	/**
	 * This method returns Collection of records from CURR_HDG_SET_BY_FUND_N_VW view
	 * @param fundNumbers
	 * @param strAsOfDate
	 * @param srcCodes
	 * @param exttCode
	 * @param cstBasisCode
	 * @return Collection of CurrHldgData
	 * @throws Exception
	 */
	protected Collection<CurrHldgData> getCurrencyHoldings(String[] fundNumbers, 
															String strAsOfDate, 
															String srcCodes,
															String exttCode, 
															String cstBasisCode) throws Exception {
		
		String SQL = "PKG_FEED_ABSTRACTCURRHLD.GET_CURR_HLDG_UNSETTLED";
		String[] params = {strAsOfDate,srcCodes,exttCode,cstBasisCode};
		
		List<DynaBean> rows = DBUtil.executePlQuery(SQL,params,1000);
		if(CollectionUtils.isEmpty(rows)){
			return null;
		}
		List<CurrHldgData> currHldgList = new ArrayList<CurrHldgData>();
		Iterator<DynaBean> iterator = rows.iterator();
		while(iterator.hasNext()){
			
			DynaBean bean = iterator.next();
			CurrHldgData currHldgData = new CurrHldgData();
			BeanUtils.copyProperties(currHldgData, bean);
			//Special cases for param name with more than 30 chars. Rest will be copied through BeanUtils.copyProperties
			//Needed because SQL does not allow >30 length identifier names
			currHldgData.setCurrentSettleCurrencyToBaseFXRate(bean.get("currentSettleCurrencyToBaseFXR")!=null ? ((BigDecimal)bean.get("currentSettleCurrencyToBaseFXR")).doubleValue() : 0.0);
			currHldgData.setOPCCTReceivingSctyFXRateFromTrans(bean.get("oPCCTReceivingSctyFXRateFromTr")!=null ? ((BigDecimal)bean.get("oPCCTReceivingSctyFXRateFromTr")).doubleValue() : 0.0);
			currHldgData.setTransactionTradeBrokerDescription((String) bean.get("transactionTradeBrokerDescript"));
			currHldgList.add(currHldgData);
		}
		Util.log(this,"Total UnSettled Currency holding records  "+(currHldgList != null ? currHldgList.size() : 0));
		return currHldgList;
		
//		Collection<CurrHldgData> currHoldings = null;
//		
//		Criteria currHdgCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
//		currHdgCriteria.addCriterion(CurrHldgSetlCriteriaBuilder.addAsOfDateFilter(strAsOfDate, Operator.OPERATOR_EQ));
//		currHdgCriteria.addCriterion(CurrHldgSetlCriteriaBuilder.addExtractKindCodeFilter(exttCode,Operator.OPERATOR_EQ));
//		currHdgCriteria.addCriterion(CurrHldgSetlCriteriaBuilder.addSourceCodeFilter(srcCodes,Operator.OPERATOR_IN));
//		currHdgCriteria.addCriterion(CurrHldgSetlCriteriaBuilder.addCostBasisCodeFilter(cstBasisCode,Operator.OPERATOR_EQ));
//		
//		CurrencyHolding currencyHolding = (CurrencyHolding)ApplicationContext.getContext().getBean("CurrencyHolding");
//		
//		if(fundNumbers == null || fundNumbers.length == 0){
//			currHoldings = currencyHolding.getCurrHldgSetByFundNumber(new String[0], currHdgCriteria);
//		}else{
//			currHoldings = currencyHolding.getCurrHldgSetByFundNumber(fundNumbers, currHdgCriteria);
//		} 
//		
//		Util.log(this,"Total UnSettled Currency holding records  "+(currHoldings != null ? currHoldings.size() : 0));
//		return currHoldings;
	}
	
	
	/**
	 * This method returns Collection of Settled CurrencyHoldings from CURR_HDG_SETL_SET_BY_FUND_N_VW view
	 * @param subFundNumbers
	 * @param strAsOfDate
	 * @param srcCodes
	 * @param exttCode
	 * @param cstBasisCode 
	 * @return Collection of CurrHldgSetlData
	 */
	protected Collection<CurrHldgSetlData> getSettledCurrencyHoldings(String[] fundNumbers, 
																	  String strAsOfDate, 
																	  String srcCodes,
																	  String exttCode, 
																	  String cstBasisCode) throws Exception{
		String SQL = "PKG_FEED_ABSTRACTCURRHLD.GET_CURR_HLDG_SETTLED";
		String[] params = {strAsOfDate,srcCodes,exttCode,cstBasisCode};
		
		List<DynaBean> rows = DBUtil.executePlQuery(SQL,params,1000);
		if(CollectionUtils.isEmpty(rows)){
			return null;
		}
		List<CurrHldgSetlData> currHldgSetlList = new ArrayList<CurrHldgSetlData>();
		Iterator<DynaBean> iterator = rows.iterator();
		while(iterator.hasNext()){
			
			DynaBean bean = iterator.next();
			CurrHldgSetlData currHldgSetlData = new CurrHldgSetlData();
			BeanUtils.copyProperties(currHldgSetlData, bean);
			//CusipN and FMRCusipN are not getting auto-populated
			currHldgSetlData.setCUSIPNumber((String) bean.get("cUSIPNumber"));
			currHldgSetlData.setFMRCUSIPNumber((String) bean.get("fMRCUSIPNumber"));
			//Special cases for param name with more than 30 chars. Rest will be copied through BeanUtils.copyProperties
			//Needed because SQL does not allow >30 length identifier names
			currHldgSetlData.setTransactionTradeBrokerDescription((String) bean.get("transactionTradeBrokerDescrip"));
			currHldgSetlList.add(currHldgSetlData);
		}
		Util.log(this,"Total Settled Currency holding records  "+(currHldgSetlList != null ? currHldgSetlList.size() : 0));
		return currHldgSetlList;
		
//		Collection<CurrHldgSetlData> setlCurrHoldings = null;
//		
//		Criteria currHdgCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
//		currHdgCriteria.addCriterion(CurrHldgCriteriaBuilder.addAsOfDateFilter(strAsOfDate, Operator.OPERATOR_EQ));
//		currHdgCriteria.addCriterion(CurrHldgCriteriaBuilder.addExtractKindCodeFilter(exttCode,Operator.OPERATOR_EQ));
//		currHdgCriteria.addCriterion(CurrHldgCriteriaBuilder.addSourceCodeFilter(srcCodes,Operator.OPERATOR_IN));
//		currHdgCriteria.addCriterion(CurrHldgCriteriaBuilder.addCostBasisCodeFilter(cstBasisCode,Operator.OPERATOR_EQ));
//		
//		SettledCurrencyHolding settledCurrencyHolding = (SettledCurrencyHolding)ApplicationContext.getContext().getBean("SettledCurrencyHolding");
//		
//		if(fundNumbers == null || fundNumbers.length == 0){
//			setlCurrHoldings = settledCurrencyHolding.getSettledCurrHldgSetByFundNumber(new String[0], currHdgCriteria);
//		}else{
//			setlCurrHoldings = settledCurrencyHolding.getSettledCurrHldgSetByFundNumber(fundNumbers, currHdgCriteria);
//		} 
//		
//		Util.log(this,"Total Settled Currency holding records  "+(setlCurrHoldings != null ? setlCurrHoldings.size() : 0));
//		return setlCurrHoldings;
	}
	
	/**
	 * This method returns Collection of records from INV1_SEC_SET_VW
	 * @param collection
	 * @param strAsOfDate
	 * @param srcCodes
	 * @param exttCode
	 * @param cstBasisCode
	 * @return
	 * @throws Exception
	 */
	protected Collection<SecurityData> getSecurities(Collection<? extends Serializable> collection, 
													 String strAsOfDate,
													 String srcCodes, 
													 String exttCode, 
													 String cstBasisCode) throws Exception {
		
		Security security = (Security)ApplicationContext.getContext().getBean("Security");
		
		Criteria securityCriteria = (Criteria)ApplicationContext.getContext().getBean("Criteria");
		securityCriteria.addCriterion(SecurityCriteriaBuilder.addAsOfDateFilter(strAsOfDate, Operator.OPERATOR_EQ));
		securityCriteria.addCriterion(SecurityCriteriaBuilder.addSourceCodeFilter(srcCodes, Operator.OPERATOR_IN));
		securityCriteria.addCriterion(SecurityCriteriaBuilder.addExtractKindCodeFilter(exttCode, Operator.OPERATOR_EQ));
		securityCriteria.addCriterion(SecurityCriteriaBuilder.addCostBasisCodeFilter(cstBasisCode, Operator.OPERATOR_EQ));
		
		Collection<SecurityData> securities = security.getSecuritySetByCusipNumber(collection, securityCriteria);

		Util.log(this,"Total security records  "+(securities != null ? securities.size() : 0));
		
		return securities;
	}
	
	/**
	 * This method consolidates UnSettled Currency Contracts
	 * @param shortNameCache - Cache that holds Fund Short Name
	 * @param valDateMap 
	 * @param ctrtValueMap 
	 * @param subFundNumbers will always be null
	 * @param strAsOfDate
	 * @param srcCodes
	 * @param exttCode
	 * @return Collection
	 * @throws Exception
	 */
	protected Collection<CurrencyHoldingVO> processUnSettledSpotNForward(Map<String, String> shortNameMap, 
														  				 Map<String, String> valDateMap, 
														  				 Map<String,String> spotCusipMap,
														  				 String[] subFundNumbers, 
														  				 String strAsOfDate, 
														  				 String srcCodes,
														  				 String exttCode) throws Exception {
		
		Collection<CurrHldgData> currHoldings = null;
		//Map<String,SecurityData> securityMap = null;
		Map<String,Map<String,Double>> taxLotMap = null;
		Collection<CurrencyHoldingVO> currencyHoldingsList = null;
		Util.log(this, "processUnSettledSpotNForward:Currency Holdings:START");		
		stopWatch.start("processUnSettledSpotNForward:Currency Holdings");
			currHoldings = getCurrencyHoldings(subFundNumbers,strAsOfDate,srcCodes,exttCode,Constants.COST_BASIS_CODE_FA);
		stopWatch.stop();
		Util.log(this, "processUnSettledSpotNForward:Currency Holdings:END");
		if(CollectionUtils.isEmpty(currHoldings)){
			return null;
		}
		Util.log(this, "processUnSettledSpotNForward:TaxLot:START");
		stopWatch.start("processUnSettledSpotNForward:TaxLot");
			Collection<TaxlotData> txLots = getTaxLotsFromCurrHldg(strAsOfDate,null,srcCodes,exttCode,Constants.COST_BASIS_CODE_FA, UNSETTLED);
			taxLotMap = cacheTaxLotData(txLots);
			txLots = null;
		stopWatch.stop();
		Util.log(this, "processUnSettledSpotNForward:TaxLot:END");
		Util.log(this, "processUnSettledSpotNForward:consolidateUnSettledContracts:START");
		stopWatch.start("processUnSettledSpotNForward:consolidateUnSettledContracts");
		currencyHoldingsList = consolidateUnSettledContracts(currHoldings,null,taxLotMap,shortNameMap,valDateMap,
							   spotCusipMap,exttCode);
		stopWatch.stop();
		Util.log(this, "processUnSettledSpotNForward:consolidateUnSettledContracts:END");
		totalUnsettledCC = totalUnsettledCC+(currencyHoldingsList != null ? currencyHoldingsList.size():0);
		currHoldings=null;
		return currencyHoldingsList;
	}
		
	
	/**
	 * This method consolidates Settled Currency Contracts
	 * @param shortNameMap
	 * @param valDateMap
	 * @param spotCusipMap
	 * @param ctrtValueMap 
	 * @param subFundNumbers
	 * @param strAsOfDate
	 * @param srcCodes
	 * @param exttCode
	 * @param settledRecordTypeMap 
	 * @return
	 * @throws Exception
	 */
	protected Collection<CurrencyHoldingVO> processSettledSpotNForward(Map<String, String> shortNameMap, 
				 														Map<String, String> valDateMap, 
				 														Map<String,String> spotCusipMap,
				 														Map<String, Map<String, Object>> settledTxnMap, 
				 														String[] subFundNumbers, 
				 														String strAsOfDate, 
				 														String strPriorAsOfDate,
				 														String strSystemProcessDay,
				 														String srcCodes,
				 														String exttCode, 
				 														Map<String, String> settledRecordTypeMap,
				 														Map<String, String> contractTypeMap) throws Exception {

		Collection<CurrHldgSetlData> currSettledHoldings = null;
		//Map<String,SecurityData> securityMap = null;
		Collection<CurrencyHoldingVO> currencyHoldingsList = null;
		Map<String,Map<String,Double>> prevEODTaxLotMap = null;
		Util.log(this, "processSettledSpotNForward:getSettledCurrencyHoldings:START");
		stopWatch.start("processSettledSpotNForward:Currency Holdings");
		currSettledHoldings = getSettledCurrencyHoldings(subFundNumbers,strAsOfDate,srcCodes,exttCode,Constants.COST_BASIS_CODE_FA);
		stopWatch.stop();
		Util.log(this, "processSettledSpotNForward:getSettledCurrencyHoldings:END");
		if(CollectionUtils.isEmpty(currSettledHoldings)){

			return null;
		}

		// Fix for jira FACDAL-2240
		
		if(exttCode.equals("EOD")) {
			Util.log(this, "processSettledSpotNForward:getTaxLots:START");
			stopWatch.start("processSettledSpotNForward:TaxLot");
			Collection<TaxlotData> txLots = getTaxLotsFromCurrHldg(strAsOfDate,strPriorAsOfDate,srcCodes,exttCode,Constants.COST_BASIS_CODE_FA, SETTLED);
			prevEODTaxLotMap = cacheTaxLotData(txLots);
			txLots = null;
			stopWatch.stop();
			Util.log(this, "processSettledSpotNForward:getTaxLots:END");
		}
		
		Util.log(this, "processSettledSpotNForward:getTaxLots:START");
		stopWatch.start("processSettledSpotNForward:consolidateSettledContracts");
		currencyHoldingsList = consolidateSettledContracts(currSettledHoldings,null,shortNameMap,valDateMap,
							  spotCusipMap,settledTxnMap,settledRecordTypeMap,exttCode,prevEODTaxLotMap, contractTypeMap);
		stopWatch.stop();
		Util.log(this, "processSettledSpotNForward:getTaxLots:END");
		totalSettledCC = totalSettledCC+(currencyHoldingsList != null ? currencyHoldingsList.size():0);
		currSettledHoldings=null;
		return currencyHoldingsList;
	}
	
	/**
	 * 
	 * @param Map to hold values for UnSettled Currency Holding
	 * @param Map to hold values for Settled Currency Holding 
	 * @param fromDay 
	 * @param toDay 
	 * @param srcCodes
	 * @param exttCode
	 * @param costBasisCode
	 * @throws Exception 
	 */
	protected void cacheTxnData( Map<String, Map<String, Object>> settledTxnMap, 
								 DateTime fromDay, 
								 DateTime toDay, 
								 String srcCodes, 
								 String exttCode, 
								 String costBasisCode) throws Exception {
		
		if(toDay == null || fromDay == null || srcCodes == null || exttCode == null || costBasisCode == null){
			
			Util.log(this, "One ore More inputs for CacheContractValueAmount() is null", Constants.LOG_LEVEL_ERROR, new IllegalArgumentException());
			return;
		}
		
		String sql = "PKG_FEED_ABSTRACTCURRHLD.TXN_DATA";
		String[] params = {srcCodes,exttCode,costBasisCode,fromDay.toString(dft),toDay.toString(dft)};
		
		/*
		"SELECT H.PORT_FUND_N ,H.FMR_CUSP_N , H.TXN_MEMO_N ,H.TRD_APPL_TRD_ID, "+
					 "H.PRNC_A , H.SHR_Q, H.CURR_CTRT_CUSP,H.AS_OF_D,H.TRX_EXEC_BRKR_C "+
					 "FROM HDGTRX_SET_BY_FUND_N_VW H "+
					 "WHERE H.FMR_CUSP_N LIKE \'CCT%\' "+
					 "AND H.SRC_C = \'"+srcCodes+"\' "+
					 "AND H.EXTT_KND_C = \'"+exttCode+"\' "+
					 "AND H.CST_BASIS_C = \'"+costBasisCode+"\' "+
					 "AND H.AS_OF_D BETWEEN TO_DATE(\'"+fromDay.toString(dft)+"\','YYYY-MM-DD') AND TO_DATE(\'"+toDay.toString(dft)+"\','YYYY-MM-DD') ";
		
		Util.log(this,"SQL being executed "+sql);*/
		
		List<DynaBean> rows = DBUtil.executePlQuery(sql, params,-1);
		Iterator<DynaBean> iterator = rows.iterator();
		while(iterator.hasNext()){
			
			DynaBean row = iterator.next();
			
			String fmrCusip = (String)row.get("FMR_CUSP_N");
			BigDecimal portFundNDB = (BigDecimal)row.get("PORT_FUND_N");
			BigDecimal txnMemoNDB = (BigDecimal)row.get("TXN_MEMO_N");
			Long portFundN = portFundNDB != null ? portFundNDB.longValue():null;
			Long txnMemoN = txnMemoNDB != null ? txnMemoNDB.longValue():null;
			String key = createKey(portFundN,fmrCusip,txnMemoN);
			
			//Load the required data for Settled Currency Holding
			Map<String,Object> valueMap1 = new HashMap<String,Object>();
			if(settledTxnMap.containsKey(key)){
		
				Map<String,Object> map = settledTxnMap.get(key);
				Date date1 = (Date)map.get(AS_OF_D);
				Date date2 = (Date)row.get(AS_OF_D);
				if(date1 != null && date2 != null && daysDiff(date1,date2) >= 0){
					
					map.put(TRD_REF_ID,row.get("TRD_APPL_TRD_ID"));
					map.put(AS_OF_D,row.get("AS_OF_D"));
					map.put(CURR_CTRT_CUSP,row.get("CURR_CTRT_CUSP"));
					map.put(TRX_EXEC_BRKR_C, row.get("TRX_EXEC_BRKR_C"));
					settledTxnMap.put(key,map);
				}
			}else{
				
				valueMap1.put(TRD_REF_ID,row.get("TRD_APPL_TRD_ID"));
				valueMap1.put(AS_OF_D,row.get("AS_OF_D"));
				valueMap1.put(CURR_CTRT_CUSP,row.get("CURR_CTRT_CUSP"));
				valueMap1.put(TRX_EXEC_BRKR_C, row.get("TRX_EXEC_BRKR_C"));
				settledTxnMap.put(key,valueMap1);
			}
		}
	}
	
	/**
	 * Below method identifies the RecordType for Settled CurrencyHolding using UnsettledCurrencyHolding from Prior Day
	 * @param subFundNumbers
	 * @param strAsOfDate
	 * @param srcCodes
	 * @param exttCode
	 * @param ccType 
	 * @param costBasisCodeFa
	 * @return
	 * @throws Exception 
	 */
	protected Map<String, String> cacheRecordTypeForSettled(String[] fundNumbers, 
															String strAsOfDate, 
															String srcCodes,
															String exttCode, 
															String costBasisCode) throws Exception {
		
		String SQL = "PKG_FEED_ABSTRACTCURRHLD.SETTLED_REC_TYPE_NEW";
				
		String[] params = {strAsOfDate,srcCodes,exttCode,costBasisCode};
		
		List<DynaBean> rows = DBUtil.executePlQuery(SQL,params,-1);
		if(CollectionUtils.isEmpty(rows)){
			
			return null;
		}
		
		Map<String,String> settledRecordType = new HashMap<String,String>();
		Iterator<DynaBean> iterator = rows.iterator();
		while(iterator.hasNext()){
			
			DynaBean row = iterator.next();
			BigDecimal bd = (BigDecimal)row.get("PORT_FUND_N");
			Long portFundN = bd != null? bd.longValue() : 0;
			String inv1SctyCusip = (String)row.get("INV1_SCTY_CUSIP_N");
			bd = (BigDecimal)row.get("INV1_SCTY_QUAL_N");
			Double inv1SctyQual = (bd != null) ? bd.doubleValue() : 0.0;
			bd = (BigDecimal)row.get("INV1_SCTY_DATE_N");
			Double inv1SctyDate = (bd != null) ? bd.doubleValue() : 0.0 ;
			String txnMemoN = (String)row.get("TXN_MEMO_N");
			String srcCode = (String)row.get("SRC_C");
			String key = getKeyRecordTypeForUnSettled(portFundN,inv1SctyCusip,inv1SctyDate,inv1SctyQual,txnMemoN,srcCode);
			String currCtrtTy = (String)row.get("CURR_CTRT_TY");
			Date aclSetlD = (Date)row.get("ACL_SETL_D");
			Date trdD = (Date)row.get("TRD_D");
			String recordType = getRecordType(currCtrtTy,trdD,aclSetlD);
			
			settledRecordType.put(key,recordType);
		}
		return settledRecordType;
	}
	
	/**
	 * 
	 * @param shortNameCache
	 * @param valDateMap
	 * @param subFundNumbers
	 * @param strAsOfDate
	 * @param srcCodes
	 * @param exttCode
	 * @return Collection
	 * @throws Exception 
	 */
	protected Collection<CurrencyHoldingVO> processCurrBalances(Map<String, String> shortNameMap, 
																		  Map<String, String> valDateMap,
																		  String[] subFundNumbers, 
																		  String strAsOfDate, 
																		  String srcCodes,
																		  String exttCode) throws Exception {
		
		Collection<CurrencyHoldingVO> currencyHoldingsList = null;
		Collection<PositionData> positions = new ArrayList<PositionData>();
		
		String SQL = "PKG_FEED_ABSTRACTCURRHLD.POSITION_SECURITY_AS_CURR_HLDG";
		String[] params = {strAsOfDate,srcCodes,exttCode,Constants.COST_BASIS_CODE_FA};
		
		List<DynaBean> rows = DBUtil.executePlQuery(SQL,params,-1);
		if(CollectionUtils.isEmpty(rows)){
			return null;
		}
		Iterator<DynaBean> iterator = rows.iterator();
		while(iterator.hasNext()){
			
			DynaBean bean = iterator.next();
			PositionData positionData = new PositionData();
			BeanUtils.copyProperties(positionData, bean);
			positions.add(positionData);
		}
		if(CollectionUtils.isEmpty(positions)){
			return null;
		}
		currencyHoldingsList = consolidateCurrencyBalances(positions,shortNameMap,valDateMap);
		totalCurrBalanaces = totalCurrBalanaces+(currencyHoldingsList != null ? currencyHoldingsList.size():0);
		positions = null;
		return currencyHoldingsList;
	}
	
	/**
	 * This method consolidates the required data for Currency Balances
	 * @param currencyHoldingsList
	 * @param positions
	 * @param shortNameCache
	 * @param valDateMap 
	 * @param cbFmrIdmap
	 */
	protected Collection<CurrencyHoldingVO> consolidateCurrencyBalances(Collection<PositionData> currencyBalances,
																				  Map<String, String> shortNameCache, 
																				  Map<String, String> valDateMap) {
		
		DateTimeFormatter dft = DateTimeFormat.forPattern("MM/dd/yyyy");
		
		List<CurrencyHoldingVO> list = new ArrayList<CurrencyHoldingVO>();
		Iterator<PositionData> iterator = currencyBalances.iterator();
		
		while(iterator.hasNext()){
			
			PositionData positionData = iterator.next();
			String key = getKeyValDateFrmPosn(positionData);
			String valDateStr = valDateMap != null ? valDateMap.get(key):null;
			Long portFundN = positionData.getPortfolioFundNumber();
			CurrencyHoldingVO valueObject = new CurrencyHoldingVO();
			valueObject.setRecordType(CURR_BALANCE);
			valueObject.setAcctNumber(portFundN);
			valueObject.setFundShortName(key != null ? shortNameCache.get(key):null);
			valueObject.setFmrCusip(positionData.getInvestOneSecurityCusipNumber()); //FACDAL-1154
			valueObject.setMaturedPositionID(positionData.getMaturePositionIndicator());
			valueObject.setContractValue(positionData.getLocalOriginalBookCostAmount()); 
			//Start Fix for jira FACDAL-2303
			Double settledParSharesQty = 0.0;
			if(positionData.getSettledParSharesQuantity() != null) {
				settledParSharesQty = positionData.getSettledParSharesQuantity();
			}
			Double localMarketValueAmt = 0.0;
			if(positionData.getLocalMarketValueAmount() != null){
				localMarketValueAmt = positionData.getLocalMarketValueAmount();
			}
			Double currencyPrice = 0.0;
			if(localMarketValueAmt != 0.0) {
				currencyPrice = settledParSharesQty / localMarketValueAmt;
			}
			valueObject.setCurrPrice(currencyPrice);
			//End Fix for jira FACDAL-2303
			
			if(StringUtils.isNotBlank(valDateStr)){
				
				valueObject.setValDate(dft.parseDateTime(valDateStr).toDate());
			}
			valueObject.setMarketValueCurrBal(localMarketValueAmt);
			valueObject.setShares(positionData.getShareParQuantity());
			valueObject.setSettleShares(settledParSharesQty);
			list.add(valueObject);
		}
		
		return list;
	}
	
	/**
	 * 
	 * @author a512859
	 * Since the data written to feed is consolidated from different business views, a separate comparator is required to 
	 * keep all the records sorted.
	 */
	protected class Comparable implements Comparator<CurrencyHoldingVO>{

		@Override
		public int compare(CurrencyHoldingVO ch1, CurrencyHoldingVO ch2) {
		
			return ch1.compareTo(ch2);
		}   
	}
	
	protected String getHeaderFooter(String identifier,String fileID,DateTime runDay,DateTime today,int recordCount){
		
		DecimalFormat dfmt15 = new DecimalFormat("000000000000000");
		
		StringBuilder builder = new StringBuilder();
		builder.append(identifier);
		builder.append(getCommon(fileID,runDay,today));
		builder.append(dfmt15.format(recordCount));
		
		return builder.toString();
	}
	
	
	/**
	 * Below method processes the required records for Currency Holdings. 
	 * Removed batching implementation because of performance constraints
	 * @param fundNumbers
	 * @param shortNameMap
	 * @param valDateMap
	 * @param spotCusipMap
	 * @param unSettledTxnMap
	 * @param settledTxnMap
	 * @param asOfDay
	 * @param systemProcessDay 
	 * @param srcCodes
	 * @param exttCode
	 * @return Collection 
	 * @throws Exception
	 */
	protected List<CurrencyHoldingVO> gatherRequiredData(List<String> fundNumbers, 
														 Map<String, String> shortNameMap,
														 Map<String, String> valDateMap, 
														 Map<String, String> spotCusipMap,
														 Map<String, Map<String, Object>> settledTxnMap, 
														 DateTime asOfDay,
														 DateTime priorDay,
														 DateTime systemProcessDay, 
														 String srcCodes, 
														 String exttCode) throws Exception {

		String strAsOfDate = asOfDay.toString(dft);
		String strPriorAsOfDate = priorDay.toString(dft);
		String strSystemProcessDay = systemProcessDay.toString(dft);
		List<CurrencyHoldingVO> list = new ArrayList<CurrencyHoldingVO>();
		
		Util.log(this, "gatherRequiredData:contractTypeMap:START");
		stopWatch.start("Populate contractTypeMap");
		Map<String, String> contractTypeMap = getContractTypeMap(strAsOfDate,
				strPriorAsOfDate, strSystemProcessDay, srcCodes, exttCode);
		stopWatch.stop();
		Util.log(this, "gatherRequiredData:contractTypeMap:END");
		Util.log(this, "gatherRequiredData:cacheRecordTypeForSettled:START");
		stopWatch.start("cacheRecordTypeForSettled");
		Map<String,String> settledRecordTypeMap = cacheRecordTypeForSettled(null,strPriorAsOfDate,srcCodes,
												  exttCode,Constants.COST_BASIS_CODE_FA);
		stopWatch.stop();
		Util.log(this, "gatherRequiredData:cacheRecordTypeForSettled:END");
		//stopWatch is used inside this method. No need to start a new one.
		/* Begin gathering required UnSettled FORWARDS and SPOTS and consolidate the data */
		Util.log(this, "gatherRequiredData: processUnSettledSpotNForward:START");
		Collection<CurrencyHoldingVO> currHdgRecords = processUnSettledSpotNForward(shortNameMap,
							   														valDateMap,
							   														spotCusipMap,
							   														null,
							   														strAsOfDate,
							   														srcCodes,
							   														exttCode);
		Util.log(this, "gatherRequiredData: processUnSettledSpotNForward:END");
		if(CollectionUtils.isNotEmpty(currHdgRecords)){

			list.addAll(currHdgRecords);
		}
		//stopWatch is used inside this method. No need to start a new one.
		/* Begin gathering required Settled FORWARDS and SPOTS and consolidate the data */
		Util.log(this, "gatherRequiredData:processSettledSpotNForward:START");
		currHdgRecords = processSettledSpotNForward(shortNameMap,
													valDateMap,
													spotCusipMap,
													settledTxnMap,
													null,
													strAsOfDate,
													strPriorAsOfDate,
													strSystemProcessDay,
													srcCodes,
													exttCode,
													settledRecordTypeMap,
													contractTypeMap);
		Util.log(this, "gatherRequiredData:processSettledSpotNForward:END");
		if(CollectionUtils.isNotEmpty(currHdgRecords)){

			list.addAll(currHdgRecords);
		}
		Util.log(this, "gatherRequiredData:processCurrBalances from Positions:START");
		stopWatch.start("processCurrBalances from Positions");
		/* Begin gathering required CURRENCY BALANCES and consolidate the data */
		currHdgRecords = processCurrBalances(shortNameMap,
											 valDateMap,
											 null,
											 strAsOfDate,
											 srcCodes,
											 exttCode);
		stopWatch.stop();
		Util.log(this, "gatherRequiredData:processCurrBalances from Positions:END");
		if(CollectionUtils.isNotEmpty(currHdgRecords)){
			
			list.addAll(currHdgRecords);
		}
			
		if(CollectionUtils.isNotEmpty(list)){
			
			Collections.sort(list, new Comparable()); //Sort all records
		}
		
		return list;
	}	

	/**
	 * FACDAL-2211: Populating CurrencyContractType for BOD and EOD based on latest Requirements
	 * @param strAsOfDate
	 * @param strPriorAsOfDate
	 * @param strSystemProcessDay
	 * @param srcCodes
	 * @param exttCode
	 * @return
	 * @throws Exception
	 */
	protected Map<String, String> getContractTypeMap(String strAsOfDate,
			String strPriorAsOfDate, String strSystemProcessDay,
			String srcCodes, String exttCode) throws Exception {
		
		final Map<String, String> contractTypeMap = new HashMap<String, String>();
		String sql = "PKG_FEED_ABSTRACTCURRHLD.CONTRACT_TYPE";
		String[] params = new String[5];
		if(exttCode.equals("BOD")){
			 params[0] = dft.parseDateTime(strAsOfDate).toString(dft_ddMMMyyyy);
			 params[1] = dft.parseDateTime(strSystemProcessDay).toString(dft_ddMMMyyyy);
			 params[2] = "BOD";
			 params[3] = "EOD";
			 params[4] = srcCodes;
		} else if(exttCode.equals("EOD")) {
			 params[0] = dft.parseDateTime(strAsOfDate).toString(dft_ddMMMyyyy);
			 params[1] = dft.parseDateTime(strPriorAsOfDate).toString(dft_ddMMMyyyy);
			 params[2] = "EOD";
			 params[3] = "EOD";
			 params[4] = srcCodes;
		}
		
		DBUtil.packageQuery(sql, params, 1000, new RowHandler() {
			
			@Override
			public void handleRow(ResultSet rs) throws SQLException {
				
				String key = createKey(rs.getLong("PORT_FUND_N"), rs.getString("FMR_CUSP_N"), rs.getLong("TXN_MEMO_N"));
				String cntrctType = "";//Initializing to a blank String
				//1. Get the Prior Day Unsettled EOD Record and get CURR_CTRT_TY  
				//Get the Contract Type from Prior Day Unsettled Contract
				if(rs.getString("CTRT_TY_PRIOR_DAY") != null && StringUtils.isNotBlank(rs.getString("CTRT_TY_PRIOR_DAY"))) {
					cntrctType = getRecordType(rs.getString("CTRT_TY_PRIOR_DAY"), null, null);//Returns "FORWARD" or "SPOT" based on the string value
				}
				/********Removing Conditions 2 and 4 as per update from Steve**********/
				/*
				2.  If 1. is blank Get the Prior Day Unsettled EOD Record and if ACL_SETL_D - TRD_D > 3 Then FORWARD else SPOT 
				If contract type is still empty compute from Date Diff of Prior Day Unsettled Contract
				if (cntrctType == null || StringUtils.isBlank(cntrctType)){
					if(rs.getString("DATEDIFF_PRIOR_DAY") != null){//Cannot check getLong("DATEDIFF_PRIOR_DAY")=0, because 0 is a valid value
						cntrctType = rs.getLong("DATEDIFF_PRIOR_DAY") > 3 ? FORWARD : SPOT;
					}
				}
				*/
				//3.  If 2. is blank Get Current Day Settled record and if ACL_SETL_D - TRD_D > 3 Then FORWARD else SPOT 
				//If contract type is still empty compute from Date Diff of Current Day Settled Contract
				if (cntrctType == null || StringUtils.isBlank(cntrctType)){
					if(rs.getString("DATEDIFF_CURR_DAY") != null){//Cannot check getLong("DATEDIFF_PRIOR_DAY")=0, because 0 is a valid value
						cntrctType = rs.getLong("DATEDIFF_CURR_DAY") > 3 ? FORWARD : SPOT;
					}
				}
				/*
				4. If 3. is blank Get Current Day Settled record and get CURR_CTRT_TY 
				If contract type is still empty get the contract type from Current Day record
				if (cntrctType == null || StringUtils.isBlank(cntrctType)){
					if(rs.getString("CTRT_TY_CURR_DAY") != null && StringUtils.isNotBlank(rs.getString("CTRT_TY_CURR_DAY"))) {
						cntrctType = getRecordType(rs.getString("CTRT_TY_CURR_DAY"), null, null);//Returns "FORWARD" or "SPOT" based on the string value
					}
				}
				*/	
				contractTypeMap.put(key, cntrctType);
				
			}
		});
				
		return contractTypeMap;
	}
	
	
	private String getCommon(String fileID,DateTime runDay,DateTime today){
		
		DateTimeFormatter dft = DateTimeFormat.forPattern("yyyyMMdd");
		DateTimeFormatter tft = DateTimeFormat.forPattern("HHmmss");
		
		String hostName = Util.getHostName();
				
		if(StringUtils.isNotBlank(hostName) && hostName.length() > 8){
			hostName = hostName.substring(0,8);
		}
		
		return String.format("%1s%8s%1s%8s%1s%8s%1s%8s%1s%6s%1s"," ",hostName," ",fileID,
							 " ",runDay.toString(dft)," ",today.toString(dft)," ",today.toString(tft)," ");
	}
	
	/**
	 * 
	 * @param positions
	 * @param securityMap 
	 * @return CurrencyBalances whose assetTypeCode is "CU"
	 */
	
	private Collection<PositionData> getCurrencyBalances(Collection<PositionData> positions, Map<String, SecurityData> securityMap) {
		
		if(CollectionUtils.isEmpty(positions) || securityMap == null) return null;
		
		List<PositionData> currencyBalances = new ArrayList<PositionData>();
		
		Iterator<PositionData> iterator = positions.iterator();
		
		while(iterator.hasNext()){
			
			PositionData positionData = iterator.next();
			//String key = getSecurityKeyFrmPosition(positionData);
			String key = getSecurityKey(positionData);
			SecurityData securityData = (key != null)?securityMap.get(key):null;
			
			if(securityData != null && 
			   StringUtils.isNotBlank(securityData.getAssetTypeCode()) && 
			   "CU".equals(securityData.getAssetTypeCode())){
				
				currencyBalances.add(positionData);
			}
		}
		return currencyBalances;
	}
	
	/**
	 * 
	 * @param currencyHoldingsList
	 * @param currHoldings
	 * @param securityMap
	 * @param taxLotMap 
	 * @param shortNameCache
	 * @param valDateMap 
	 * @param ctrtValueMap 
	 * @throws Exception
	 */
	protected Collection<CurrencyHoldingVO> consolidateUnSettledContracts(Collection<CurrHldgData> currHoldings, 
																Map<String, SecurityData> securityMap,
																Map<String, Map<String, Double>> taxLotsMap, 
																Map<String, String> shortNameMap, 
																Map<String, String> valDateMap,
																Map<String,String> spotCusipMap,
																String exttCode) throws Exception {

		
		DateTimeFormatter dft = DateTimeFormat.forPattern("MM/dd/yyyy");
		List<CurrencyHoldingVO> list = new ArrayList<CurrencyHoldingVO>();
		
		Iterator<CurrHldgData> iterator = currHoldings.iterator();
		while(iterator.hasNext()){
			
			CurrHldgData currencyHolding = iterator.next();
			String key = getKeyValDateMap(currencyHolding);
			
			Long txnMemoNL = null;
			String txnMemoN = currencyHolding.getTransactionMemoNumber();
			if(StringUtils.isNotBlank(txnMemoN)){

				try{
					txnMemoNL = Long.parseLong(txnMemoN.trim());
				}catch(Exception nfe){
					
					Util.log(
							this,
							"Exception thrown while parsing Transaction Memo Number for [PORT_FUND_N = "
									+ currencyHolding.getPortfolioFundNumber()
									+ ", FMR_CUSP_N = "
									+ currencyHolding.getFMRCUSIPNumber()
									+ ", TXN_MEMO_N =" + txnMemoN + "]",
							Constants.LOG_LEVEL_ERROR, nfe);
					
					txnMemoNL =null;
				}
			}
			
			String taxLotKey = getTxLtKeyFrmCurrHdg(currencyHolding);
			Map<String,Double> txLotMap = taxLotsMap != null ? taxLotsMap.get(taxLotKey) : null;
			//Removed the use of Security Data. This will always be null									 
			//SecurityData securityData = securityMap != null? securityMap.get(securityKey):null;
			String fundName = (shortNameMap != null && key != null) ? shortNameMap.get(key):null;
			String valDateStr = (valDateMap != null && key != null) ? valDateMap.get(key):null;
			String recordType = getRecordType(currencyHolding);
			
			if(StringUtils.isBlank(recordType)){
				
				Util.log(this, "No RecordType found for [FUND_N = "+ currencyHolding.getFundNumber() + 
							   " , PORT_FUND_N = "+ currencyHolding.getPortfolioFundNumber() + 
							   " , FMR_CUSP_N  = "+ currencyHolding.getFMRCUSIPNumber()+"]");
			}
			
			boolean isCrossCurrency = isCrossCurrencyContract(currencyHolding);
			String[] cusips = getCusipsForUnSettled(recordType,currencyHolding,null,spotCusipMap,isCrossCurrency);

			//FMRID is required. If failed to get one, L1/L2 support need to be alerted.
			if(cusips == null || StringUtils.isBlank(cusips[0])){
				
				Util.log(this,"No RecordType found for [FUND_N = "+ currencyHolding.getFundNumber() + 
						   " , PORT_FUND_N = "+ currencyHolding.getPortfolioFundNumber() + 
						   " , FMR_CUSP_N  = "+ currencyHolding.getFMRCUSIPNumber()+"]");
				
				Exception e = new Exception("Unable to find FMRId for [FUND_N = "+ currencyHolding.getFundNumber() + 
			   								" , PORT_FUND_N = "+ currencyHolding.getPortfolioFundNumber() + 
			   								" , FMR_CUSP_N  = "+ currencyHolding.getFMRCUSIPNumber()+
			   								" , RECORD TYPE = "+ recordType+"]");
				
				//Util.log(this, e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				Util.log(this,e.getMessage());
				/*throw new Exception("Unable to find FMRId for [FUND_N = "+ currencyHolding.getFundNumber() + 
						   			" , PORT_FUND_N = "+ currencyHolding.getPortfolioFundNumber() + 
						   			" , FMR_CUSP_N  = "+ currencyHolding.getFMRCUSIPNumber()+
						   			" , RECORD TYPE = "+ recordType+"]");
				*/		   			
			}
			CurrencyHoldingVO valueObject = new CurrencyHoldingVO();
			valueObject.setRecordType(recordType);
			valueObject.setAcctNumber(currencyHolding.getPortfolioFundNumber());
			valueObject.setFundShortName(fundName);
			valueObject.setMemoNum(txnMemoNL!=null?String.valueOf(txnMemoNL):null);
			valueObject.setSourceSystemCode(getSourceSystemCode(currencyHolding.getTradeReferenceIdentifier())); //FACDAL-764 & FACDAL-824
			valueObject.setExternalTradeID(currencyHolding.getTradeReferenceIdentifier());
			valueObject.setDeliveryDate(currencyHolding.getActualSettlementDate());
			valueObject.setFmrCusip(cusips != null ? cusips[0]:null); //FACDAL-1154,FACDAL-1532,FACDAL-1608
			valueObject.setTradableCusip(cusips != null ? cusips[1] : null);//FACDAL-1154,FACDAL-1532,FACDAL-1608
			//valueObject.setSecurityDesc(securityData!=null?securityData.getShortName():null);
			valueObject.setSecurityDesc(currencyHolding.getShortName());
			//valueObject.setMaturedPositionID(doDatesMatch(currencyHolding.getActualSettlementDate(),currencyHolding.getAsOfDate())?"Y":"N");//FACDAL-1153
			valueObject.setMaturedPositionID("N");//FACDAL-2342
			valueObject.setCurrencyReceived(currencyHolding.getTradingCurrency());
			valueObject.setCurrencyDelivered(currencyHolding.getSellSideCurrencyCode());
			//valueObject.setTradingCurrency(currencyHolding.getSellSideCurrencyCode());
			//FACDAL-2248
			valueObject.setTradingCurrency(currencyHolding.getTradingCurrency());
			valueObject.setReceiveAmt(currencyHolding.getReceivableLocal());
			valueObject.setDeliveryAmt(currencyHolding.getPayableLocal());
			//FACDAL-2097
			valueObject.setContractValue(StringUtils.equals(currencyHolding.getSellSideCurrencyCode(),"USD")?
										 (currencyHolding.getCurrencyPayBase() != null ? Math.abs(currencyHolding.getCurrencyPayBase()) : 0.0):
										 (currencyHolding.getReceivableLocal() != null ? Math.abs(currencyHolding.getReceivableLocal()) : 0.0));
			valueObject.setContractRateReceive(currencyHolding.getOPCCTReceivingSctyFXRateFromTrans());
			valueObject.setContractRateDelivery(currencyHolding.getTransactionExchangeRate());
			
			//FACDAL-1394 , FACDAL - 1396
			if(txLotMap != null){
				valueObject.setValuationRateReceive(txLotMap.get(VALUATAION_RATE_RECEIVE));
				valueObject.setValuationRateDelivery(txLotMap.get(VALUATAION_RATE_DELIVERY));
				valueObject.setMarketValueReceive(txLotMap.get(MARKET_VALUE_RECEIVE)); 
				valueObject.setMarketValueDelivery(txLotMap.get(MARKET_VALUE_DELIVERY)); 
				valueObject.setUnrealGainLossReceive(txLotMap.get(UNREAL_GAIN_LOSS_RECEIVE)); 
				valueObject.setUnrealGainLossDelivery(txLotMap.get(UNREAL_GAIN_LOSS_DELIVERY)); 
			}
			
			if(StringUtils.isNotBlank(valDateStr)){
				
				valueObject.setValDate(dft.parseDateTime(valDateStr).toDate());
			}
			
			/*
			 * Below three fields are applicable only for RepositoryCurrencyHolding Feeds.
			 * Refer : com.fidelity.dal.fundacct.feed.app.RepoCurrencyHoldingFeed
			 * brokerId, tradeDate, ioSecCusipId
			 */
			//Begin
			valueObject.setBrokerId(currencyHolding.getTransactionExecutedBrokerCode());
			valueObject.setTradeDate(currencyHolding.getTradeDate());
			//valueObject.setIoSecCusipId(securityData != null?securityData.getInvestOneCUSIPNumber():null);
			valueObject.setIoSecCusipId(currencyHolding.getInvestOneSecurityCusipNumber());
			//End
			
			list.add(valueObject);
			
			if(StringUtils.equals(recordType, FORWARD)){
				totalUnsettledForwards++;
			}else if(StringUtils.equals(recordType, SPOT)){
				totalUnsettledSpots++;
			}
		}
		
		return list;
	}
	
	
	/**
	 * This method creates a Collection of Settled Currency Holding records
	 * @param currSettledHoldings
	 * @param securityMap
	 * @param shortNameMap
	 * @param valDateMap
	 * @param spotCusipMap
	 * @param settledRecordTypeMap 
	 * @param contractTypeMap 
	 * @param ctrtValueMap 
	 * @return Collection
	 * @throws Exception 
	 */
	private Collection<CurrencyHoldingVO> consolidateSettledContracts(Collection<CurrHldgSetlData> currSettledHoldings,
																	  Map<String, SecurityData> securityMap,
																	  Map<String, String> shortNameMap, 
																	  Map<String, String> valDateMap,
																	  Map<String, String> spotCusipMap, 
																	  Map<String, Map<String, Object>> settledTxnMap, 
																	  Map<String, String> settledRecordTypeMap,
																	  String exttCode,
																	  Map<String,Map<String,Double>> prevEODTaxLotMap,
																	  Map<String, String> contractTypeMap) throws Exception {
		
		DateTimeFormatter dft = DateTimeFormat.forPattern("MM/dd/yyyy");
		List<CurrencyHoldingVO> list = new ArrayList<CurrencyHoldingVO>();
		
		Iterator<CurrHldgSetlData> iterator = currSettledHoldings.iterator();
		while(iterator.hasNext()){
			
			CurrHldgSetlData setlCurrencyHolding = iterator.next();
			String srcCode = setlCurrencyHolding.getSourceCode();
			int sourceCodeInt = FeedsUtil.getSourceId(srcCode);
			String payCurrCode = setlCurrencyHolding.getPayableCurrencyCode();
			String recvCurrCode = setlCurrencyHolding.getReceivableCurrencyCode();
			String key = getKeyValDateMap(setlCurrencyHolding);
			Long txnMemoNL = null;
			String txnMemoN = setlCurrencyHolding.getTransactionMemoNumber();
			if(StringUtils.isNotBlank(txnMemoN)){
				try{
					txnMemoNL = Long.parseLong(txnMemoN.trim());
				}catch(Exception nfe){
					
				}
			}
			
			String ctrtValKey = createKey(setlCurrencyHolding.getPortfolioFundNumber(),
										  setlCurrencyHolding.getFMRCUSIPNumber(),
										  txnMemoNL);
			
			Map<String,Object> valueMap = (settledTxnMap != null && ctrtValKey != null) ? settledTxnMap.get(ctrtValKey):null;
			String trdRefId = null;
			String fmrCusip = null;
			String brokerId = null;
			if(valueMap != null){
			
				trdRefId = (String)valueMap.get(TRD_REF_ID);
				fmrCusip = (String)valueMap.get(CURR_CTRT_CUSP);
				brokerId = (String)valueMap.get(TRX_EXEC_BRKR_C);
			}
			//Removed the use of securityMap
			//SecurityData securityData = securityMap != null? securityMap.get(securityKey):null;
			String fundName = shortNameMap != null ? shortNameMap.get(key):null;
			String valDateStr = valDateMap != null ? valDateMap.get(key):null;
			String recordType;
			if (!contractTypeMap.isEmpty()) {
				recordType = contractTypeMap.get(ctrtValKey);
			} else {
				recordType = getRecordType(setlCurrencyHolding,settledRecordTypeMap,sourceCodeInt);
			}
			
			// Fix for jira FACDAL-2240
			String taxLotKey = getTxLtKeyFrmCurrHdg(setlCurrencyHolding);
			Map<String,Double> prevEODtxLotMap = prevEODTaxLotMap != null && exttCode.equals("EOD") ? 
					 prevEODTaxLotMap.get(taxLotKey) : null;
					 
			if(StringUtils.isBlank(recordType)){
				
				Util.log(this, "No RecordType found for [FUND_N = "+ setlCurrencyHolding.getFundNumber() + 
							   " , PORT_FUND_N = "+ setlCurrencyHolding.getPortfolioFundNumber() + 
							   " , FMR_CUSP_N  = "+ setlCurrencyHolding.getFMRCUSIPNumber()+"]");
			}
			
			boolean isCrossCurrency = isCrossCurrencyContract(setlCurrencyHolding);
			String[] cusips = getCusipsForSettled(recordType,fmrCusip,setlCurrencyHolding,null,spotCusipMap,isCrossCurrency);

			//FMRID is required. If failed to get one, L1/L2 support need to be alerted.
			if(cusips == null || StringUtils.isBlank(cusips[0])){
				
				Util.log(this,"No RecordType found for [FUND_N = "+ setlCurrencyHolding.getFundNumber() + 
						   " , PORT_FUND_N = "+ setlCurrencyHolding.getPortfolioFundNumber() + 
						   " , FMR_CUSP_N  = "+ setlCurrencyHolding.getFMRCUSIPNumber()+"]");
				
				Exception e = new Exception("Unable to find FMRId for [FUND_N = "+ setlCurrencyHolding.getFundNumber() + 
			   								" , PORT_FUND_N = "+ setlCurrencyHolding.getPortfolioFundNumber() + 
			   								" , FMR_CUSP_N  = "+ setlCurrencyHolding.getFMRCUSIPNumber()+
			   								" , RECORD TYPE = "+ recordType+"]");
				
				//Util.log(this, e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
				Util.log(this,e.getMessage());
				/*throw new Exception("Unable to find FMRId for [FUND_N = "+ currencyHolding.getFundNumber() + 
						   			" , PORT_FUND_N = "+ currencyHolding.getPortfolioFundNumber() + 
						   			" , FMR_CUSP_N  = "+ currencyHolding.getFMRCUSIPNumber()+
						   			" , RECORD TYPE = "+ recordType+"]");
				*/		   			
			}
			CurrencyHoldingVO valueObject = new CurrencyHoldingVO();
			valueObject.setRecordType(recordType);
			valueObject.setAcctNumber(setlCurrencyHolding.getPortfolioFundNumber());
			valueObject.setFundShortName(fundName);
			valueObject.setMemoNum(txnMemoNL!=null?String.valueOf(txnMemoNL):null);
			valueObject.setSourceSystemCode(getSourceSystemCode(trdRefId)); //FACDAL-764 & FACDAL-824
			valueObject.setExternalTradeID(trdRefId);
			valueObject.setDeliveryDate(setlCurrencyHolding.getActualSettlementDate());
			valueObject.setFmrCusip(cusips != null ? cusips[0]:null); //FACDAL-1154,FACDAL-1532,FACDAL-1608
			valueObject.setTradableCusip(cusips != null ? cusips[1] : null);//FACDAL-1154,FACDAL-1532,FACDAL-1608
			//valueObject.setSecurityDesc(securityData != null ? securityData.getShortName():null);
			valueObject.setSecurityDesc(setlCurrencyHolding.getShortName());
			//valueObject.setMaturedPositionID(doDatesMatch(setlCurrencyHolding.getActualSettlementDate(),setlCurrencyHolding.getAsOfDate())?"Y":"N");//FACDAL-1153
			valueObject.setMaturedPositionID("Y");//FACDAL-2342
			valueObject.setCurrencyReceived(setlCurrencyHolding.getReceivableCurrencyCode()); 
			valueObject.setCurrencyDelivered(setlCurrencyHolding.getPayableCurrencyCode()); 
            //FACDAL-2248
			//valueObject.setTradingCurrency(setlCurrencyHolding.getPayableCurrencyCode()); 
			valueObject.setTradingCurrency(setlCurrencyHolding.getReceivableCurrencyCode());

			valueObject.setReceiveAmt(setlCurrencyHolding.getReceivableLocal());
			valueObject.setDeliveryAmt(setlCurrencyHolding.getPayableLocal());
			valueObject.setContractValue(setlCurrencyHolding.getReceivableBookValueBase()); 
		
			
			//FACDAL-2103
			if(StringUtils.isNotBlank(recvCurrCode) && !StringUtils.equals("USD",recvCurrCode.trim())){
			    //Fix for jira FACDAL-2240
				if(exttCode.equals("EOD") && prevEODtxLotMap != null && prevEODtxLotMap.get(VALUATAION_RATE_RECEIVE) != null) {
					valueObject.setValuationRateReceive(prevEODtxLotMap.get(VALUATAION_RATE_RECEIVE));
				} else {
					valueObject.setValuationRateReceive(setlCurrencyHolding.getReceivableSpotRate());
				}
				valueObject.setContractRateReceive(setlCurrencyHolding.getReceivableExchangeRate());
				
			} else {
				//Fix for jira FACDAL-2240
				if(exttCode.equals("EOD") && prevEODtxLotMap != null && prevEODtxLotMap.get(VALUATAION_RATE_RECEIVE) != null) {
					valueObject.setValuationRateReceive(prevEODtxLotMap.get(VALUATAION_RATE_RECEIVE));
				} else {
					valueObject.setValuationRateReceive(setlCurrencyHolding.getReceivableExchangeRate());					
				}
				valueObject.setContractRateReceive(setlCurrencyHolding.getReceivableSpotRate());
			}
			
			if(StringUtils.isNotBlank(payCurrCode) && !StringUtils.equals("USD", payCurrCode.trim())){
				//Fix for jira FACDAL-2240
				if(exttCode.equals("EOD") && prevEODtxLotMap != null && prevEODtxLotMap.get(VALUATAION_RATE_DELIVERY) != null) {
					valueObject.setValuationRateDelivery(prevEODtxLotMap.get(VALUATAION_RATE_DELIVERY));
				} else {
					valueObject.setValuationRateDelivery(setlCurrencyHolding.getPayableSpotRate());	
				}
				valueObject.setContractRateDelivery(setlCurrencyHolding.getPayableExchangeRate());
			}else{
				//Fix for jira FACDAL-2240
				if(exttCode.equals("EOD") && prevEODtxLotMap != null && prevEODtxLotMap.get(VALUATAION_RATE_DELIVERY) != null) {
					valueObject.setValuationRateDelivery(prevEODtxLotMap.get(VALUATAION_RATE_DELIVERY));
				} else {
					valueObject.setValuationRateDelivery(setlCurrencyHolding.getPayableExchangeRate());
				}
				valueObject.setContractRateDelivery(setlCurrencyHolding.getPayableSpotRate());
			}
			
			
			if(StringUtils.isNotBlank(valDateStr)){
				
				valueObject.setValDate(dft.parseDateTime(valDateStr).toDate());
			}

			//FIx for jira FACDAL-2347
			if (exttCode.equals("EOD") && prevEODtxLotMap != null
					&& prevEODtxLotMap.get(MARKET_VALUE_RECEIVE) != null) {
				valueObject.setMarketValueReceive(prevEODtxLotMap.get(MARKET_VALUE_RECEIVE));
			} else {
				valueObject.setMarketValueReceive(setlCurrencyHolding
						.getReceivableBase());
			}
			//FIx for jira FACDAL-2347
			if (exttCode.equals("EOD") && prevEODtxLotMap != null
					&& prevEODtxLotMap.get(MARKET_VALUE_DELIVERY) != null) {
				valueObject.setMarketValueDelivery(prevEODtxLotMap.get(MARKET_VALUE_DELIVERY));
			} else {
				valueObject.setMarketValueDelivery(setlCurrencyHolding
						.getPayableBase());
			}
			
			//Fix for FACDAL-2727. Same mapping for both EOD and BOD
			valueObject.setUnrealGainLossReceive(setlCurrencyHolding.getReceivableRealizedFXGainLoss());
			valueObject.setUnrealGainLossDelivery(setlCurrencyHolding.getPayableRealizedFXGainLoss());
			
			/*
			 * Below three fields are applicable only for RepositoryCurrencyHolding Feeds.
			 * Refer : com.fidelity.dal.fundacct.feed.app.RepoCurrencyHoldingFeed
			 * brokerId, tradeDate, ioSecCusipId
			 */
			//Begin
			valueObject.setBrokerId(brokerId);
			valueObject.setTradeDate(setlCurrencyHolding.getTradeDate());
			//valueObject.setIoSecCusipId(securityData != null?securityData.getInvestOneCUSIPNumber():null);
			valueObject.setIoSecCusipId(setlCurrencyHolding.getInvestOneSecurityCusipNumber());
			//End
			
			list.add(valueObject);
			
			if(StringUtils.equals(recordType, FORWARD)){
				totalSettledForwards++;
			}else if(StringUtils.equals(recordType, SPOT)){
				totalSettledSpots++;
			}
		}
		
		return list;
	}
	
	/**
	 * This method returns the record type of Settled currency holding.
	 * @param setlCurrencyHolding
	 * @param settledRecordTypeMap
	 * @param sourceCodeInt 
	 * @return String
	 * @throws Exception 
	 */
	private String getRecordType(CurrHldgSetlData setlCurrencyHolding,Map<String, String> settledRecordTypeMap, int sourceCodeInt) throws Exception {
		
		String recordType = null;
		DateTime aclSetlDateTime = null;
		String aclSetlDateTimeStr = null;
		DateTime trdDateTime = null;
		String trdDateTimeStr = null;
		DateTime trdDateTimePlusOne = null;
		String trdDateTimePlusOneStr = null;
		
		if(setlCurrencyHolding.getPortfolioFundNumber() == 334 && StringUtils.equals("00015215",setlCurrencyHolding.getTransactionMemoNumber().trim())){
			
			System.out.println("Hi");
		}
		
		if(setlCurrencyHolding.getActualSettlementDate() != null){
		
			aclSetlDateTime = new DateTime(setlCurrencyHolding.getActualSettlementDate());
			aclSetlDateTimeStr = aclSetlDateTime.toString(dft);
		}
		
		if(setlCurrencyHolding.getTradeDate() != null){
			
			trdDateTime = new DateTime(setlCurrencyHolding.getTradeDate());
			trdDateTimeStr = trdDateTime.toString(dft);
			Date nxtBusnDate = dalCalendar.getNextBusinessDay(setlCurrencyHolding.getTradeDate(), sourceCodeInt);
			if(nxtBusnDate != null){
			
				trdDateTimePlusOne = new DateTime(nxtBusnDate);  
				trdDateTimePlusOneStr = trdDateTimePlusOne.toString(dft);
			}
		}
		
		if((StringUtils.isNotBlank(aclSetlDateTimeStr) && StringUtils.isNotBlank(trdDateTimeStr) && StringUtils.isNotBlank(trdDateTimePlusOneStr)) && 
			(StringUtils.equals(aclSetlDateTimeStr, trdDateTimeStr) || StringUtils.equals(aclSetlDateTimeStr, trdDateTimePlusOneStr))){
			
			recordType = SPOT;
		}else{
			
			String recordTypeKey = getKeyRecordTypeForSettled(setlCurrencyHolding);
			if(settledRecordTypeMap != null && StringUtils.isNotBlank(recordTypeKey)){
				
				recordType = settledRecordTypeMap.get(recordTypeKey);
			}
		}
		return recordType;
	}


	/**
	 * 
	 * @param recordType - FORWARD vs SPOT
	 * @param CurrHldgData 
	 * @param SecurityData
	 * @param spotCusipMap - Cache of FMR ID for SPOT
	 * @param isCrossCurrency 
	 * @return
	 */
	private String[] getCusipsForUnSettled(String recordType, CurrHldgData currencyHolding, SecurityData securityData, 
							   Map<String, String> spotCusipMap, boolean isCrossCurrency) {
		
		String[] cusips = null;
		
		if(StringUtils.equals(recordType,FORWARD)){
			
			cusips = getForwardCusips(currencyHolding,isCrossCurrency);
		}else{
			
			cusips = getSpotCusips(spotCusipMap,currencyHolding,isCrossCurrency);
		}
		
		return cusips;
	}

	
	/**
	 * 
	 * @param recordType
	 * @param fmrId 
	 * @param CurrHldgSetlData
	 * @param SecurityData
	 * @param spotCusipMap
	 * @param isCrossCurrency
	 * @return
	 */
	private String[] getCusipsForSettled(String recordType, String fmrId, CurrHldgSetlData setlCurrencyHolding, SecurityData securityData, 
			   				   Map<String, String> spotCusipMap, boolean isCrossCurrency) {

		String[] cusips = null;

		if(StringUtils.equals(recordType,FORWARD)){
			
			cusips = getForwardCusips(fmrId,setlCurrencyHolding.getInvestOneSecurityCusipNumber(),isCrossCurrency);
		}else{

			cusips = getSpotCusips(spotCusipMap,fmrId,setlCurrencyHolding,isCrossCurrency);
		}

		return cusips;
	}

	/**
	 * 
	 * @param Map of Currency <-> CUSIP
	 * @param CurrHldgData
	 * @param isCrossCurrency
	 * @return
	 */
	private String[] getSpotCusips(Map<String, String> spotCusipMap,CurrHldgData currencyHolding,boolean isCrossCurrency) {
		
		return getSpotCusips(spotCusipMap,
							 currencyHolding.getCurrencyContractCUSIPNumber(),
							 currencyHolding.getTradingCurrency(),
							 currencyHolding.getSellSideCurrencyCode(),
							 isCrossCurrency);
	}
	
	
	/**
	 * 
	 * @param Map of Currency <-> CUSIP
	 * @param CurrHldgSetlData
	 * @param isCrossCurrency
	 * @return
	 */
	private String[] getSpotCusips(Map<String, String> spotCusipMap,String fmrId,CurrHldgSetlData setlCurrencyHolding,boolean isCrossCurrency) {
		
		return getSpotCusips(spotCusipMap,
							 fmrId,
							 setlCurrencyHolding.getReceivableCurrencyCode(),
							 setlCurrencyHolding.getPayableCurrencyCode(),
							 isCrossCurrency);
	}

	
	/**
	 *
	 * @param spotCusipMap
	 * @param currCtrtCusip
	 * @param currencyReceived
	 * @param currencyDelivered
	 * @param isCrossCurrency
	 * @return
	 */
	
	private String[] getSpotCusips(Map<String, String> spotCusipMap,
								   String currCtrtCusip,
								   String currencyReceived,
								   String currencyDelivered,
								   boolean isCrossCurrency) {
		
		//0 - FMRID, 1 - TRADEABLE CUSIP
		String[] cusips = new String[2];
		
		if(isCrossCurrency){
			
			String cusip = spotCusipMap.get(currencyReceived);
			cusips[0] = cusip;
			cusips[1] = cusip;
		}else{
			
			if(StringUtils.isNotBlank(currCtrtCusip)){
				
				cusips[0] = currCtrtCusip;
			}else{
				String currency = currencyReceived;
				if(StringUtils.equals(currency,"USD")){
					
					currency = currencyDelivered;
				}
				cusips[0] = spotCusipMap.get(currency);
			}
		}

		return cusips;
	}

	/**
	 * This method returns FMR Id and TradeableID for FORWARDS
	 * @param CurrHldgData
	 * @param boolean
	 * @return FMRID and TradeableCusip
	 */
	private String[] getForwardCusips(CurrHldgData currencyHolding, boolean isCrossCurrency) {
		
		return getForwardCusips(currencyHolding.getCurrencyContractCUSIPNumber(),
								currencyHolding.getInvestOneSecurityCusipNumber(),
								isCrossCurrency);
	}
	
	/**
	 * This method returns FMR Id and TradeableID for FORWARDS
	 * @param CurrHldgSetlData
	 * @param boolean
	 * @return FMRID and TradeableCusip
	 */
	/*private String[] getForwardCusips(CurrHldgSetlData setlCurrencyHolding, boolean isCrossCurrency) {
		
		return getForwardCusips(setlCurrencyHolding.getCurrencyContractCUSIPNumber(),
								setlCurrencyHolding.getInvestOneSecurityCusipNumber(),
								isCrossCurrency);
	}*/
	
	
	private String[] getForwardCusips(String currCtrtCusip,String invCusip,boolean isCrossCurrency) {
		
		//0 - FMRID, 1 - TRADEABLE CUSIP
		String[] cusips = new String[2];
		
		cusips[0] = StringUtils.isNotBlank(currCtrtCusip) ? currCtrtCusip : invCusip;
		cusips[1] = isCrossCurrency ? cusips[0] : null;
		
		return cusips;
	}
	
	/*
	 * This method identifies if an Settled Currency Contract is cross currency or not
	 */
	private boolean isCrossCurrencyContract(CurrHldgSetlData setlCurrencyHolding) {
		
		return isCrossCurrencyContract(setlCurrencyHolding.getReceivableCurrencyCode(),setlCurrencyHolding.getPayableCurrencyCode());
	}
	
	/*
	 * This method identifies if an UnSettled Currency Contract is cross currency or not
	 */
	private boolean isCrossCurrencyContract(CurrHldgData currencyHolding) {
		
		return isCrossCurrencyContract(currencyHolding.getTradingCurrency(),currencyHolding.getSellSideCurrencyCode());
	}
	
	/*
	 * If SELL side and BUY side currency is USD, then Cross Currency
	 */
	private boolean isCrossCurrencyContract(String buySideCurrency, String sellSideCurrency) {
		
		if(!StringUtils.equals(buySideCurrency,"USD") && 
		   !StringUtils.equals(sellSideCurrency,"USD")){
			
			return true;
		}
		return false;
	}

	/**
	 * This method returns the recordType of Unsettled Currency Contracts
	 * @param CurrHldgData
	 * @return FORWARD or SPOT
	 */
	private String getRecordType(CurrHldgData currencyHolding) {
		
		return getRecordType(currencyHolding.getCurrencyContractType(),
							 currencyHolding.getTradeDate(),
							 currencyHolding.getActualSettlementDate());
	}
	
	/**
	 * This method returns the record type of Settled Currency Contracts
	 * @param CurrHldgSetlData
	 * @return FORWARD or SPOT
	 */
/*	private String getRecordType(CurrHldgSetlData setlCurrencyHolding) {
		
		return getRecordType(setlCurrencyHolding.getCurrencyContractType(),
							 setlCurrencyHolding.getTradeDate(),
							 setlCurrencyHolding.getActualSettlementDate());
	}
*/	
	
	/**
	 * 
	 * @param currCtrtType
	 * @param tradeDate
	 * @param settlementDate
	 * @return FORWARD or SPOT
	 */
	protected String getRecordType(String currCtrtType, Date tradeDate, Date settlementDate){
	
		String recordType = null;
		if(StringUtils.isNotBlank(currCtrtType)){
			
			currCtrtType = currCtrtType.trim();
			if(StringUtils.contains(currCtrtType, "FORWARD") || StringUtils.equals(currCtrtType, "FWD")){
				
				recordType = FORWARD;
			}else if(StringUtils.contains(currCtrtType, "SPOT") || StringUtils.equals(currCtrtType, "SPOT")){
				
				recordType = SPOT;
			}
		}else{
			
			if(settlementDate == null || 
			   tradeDate == null){
				
				return null;
			}
			
			int days = daysDiff(tradeDate,settlementDate);
			if(Math.abs(days) > 3){
					
				recordType = FORWARD;
			}else{
					
				recordType = SPOT;
			}
		}
		return recordType;
	}
	
	/**
	 * This method calculates Source System code for FORWARD and SPOT
	 * @param TRD_REF_ID
	 * @return SOURCE_SYSTEM_CODE
	 *  Refer mapping document for the business rule	 
	 */
	private String getSourceSystemCode(String trdRefId) {
		
		String srcSysCode = "User Input";
		
		if(StringUtils.isNotBlank(trdRefId)){
			
			if(StringUtils.startsWith(trdRefId, "AM") || StringUtils.startsWith(trdRefId, "AX")){
				
				return "AIM";
			}
			
			int length = trdRefId.length();
			
			switch(length){
			
				case 7:{
					
					if(trdRefId.startsWith("A")){
						return "User Input";
					}else{
						return "GEODE";
					}
				}
				
				case 8:{
					
					return "MFS/SAI";
				}
				
				case 9:{
					if(trdRefId.startsWith("G")){
						
						return "GEODE";
					}
				}
				
				case 11:{
					
					return "Bank";
				}
				
				case 12:{
					
					return "AS400";
				}
				
				case 13:{
					
					if((trdRefId.startsWith("G") && trdRefId.charAt(1) == 'T') || trdRefId.startsWith("T")) return "MFS/SAI"; 
				}
				
				default:{
					return srcSysCode;
				}
			}
		}
		
		return srcSysCode;
	}
	
	
	/**
	 * This method finds the number of days in between two days
	 * @param date1 - Trade Date
	 * @param date2 - Settlement Date
	 * @return
	 */
	private int daysDiff(Date date1, Date date2){
		
		if (date1 == null || date2 == null) throw new IllegalArgumentException("Either of the dates passed to daysDiff method is null");
		
		DateTime d1 = new DateTime(date1);
		DateTime d2 = new DateTime(date2);

		Days days = Days.daysBetween(d1, d2);
		int noOfDays = days != null?days.getDays():0;
		return noOfDays;
	}
	
	
	
	/**
	 * ***********************************************************************************************
	 * All the caching of data is being done below													 *
	 * ***********************************************************************************************
	 */
	
	protected Map<String, Map<String, Double>> cacheTaxLotData(Collection<TaxlotData> txLots) {
		
		if(CollectionUtils.isEmpty(txLots)) return null;
		
		Map<String,Map<String,Double>> map = new HashMap<String,Map<String,Double>>();
		
		Iterator<TaxlotData> iterator = txLots.iterator();
		while(iterator.hasNext()){
			
			TaxlotData txLot = iterator.next();

			String key = getTxLtKeyFrmTaxLot(txLot);
			if(map.containsKey(key)){
				
				Map<String,Double> interimMap = map.get(key);
				loadToMap(interimMap,txLot);
				map.put(key, interimMap);
			}else{
				Map<String,Double> interimMap = new HashMap<String,Double>();
				loadToMap(interimMap,txLot);
				map.put(key,interimMap);
			}
		}
		return map;
	}

	private void loadToMap(Map<String, Double> interimMap, TaxlotData txLot) {
		
		Double shrParQ = txLot.getShareParQuantity();
		if(shrParQ >= 0.0){
			
			interimMap.put(VALUATAION_RATE_RECEIVE, txLot.getUnderlyingFaceAmount());
			interimMap.put(MARKET_VALUE_RECEIVE, txLot.getTradeMarketValue());
			interimMap.put(UNREAL_GAIN_LOSS_RECEIVE , txLot.getBookUnrealizedGainAmount());
		}else{
			
			interimMap.put(VALUATAION_RATE_DELIVERY, txLot.getUnderlyingFaceAmount());
			interimMap.put(MARKET_VALUE_DELIVERY, txLot.getTradeMarketValue());
			interimMap.put(UNREAL_GAIN_LOSS_DELIVERY, txLot.getBookUnrealizedGainAmount());
		}
	}

	protected Map<String, SecurityData> cacheSecurities(Collection<SecurityData> securities) {
		
		Map<String,SecurityData> map = new HashMap<String,SecurityData>();
		Iterator<SecurityData> iter = securities.iterator();
		while (iter.hasNext()) {
			SecurityData securityData = iter.next();
			//String key = getSecurityKeyFrmSecurity(securityData);
			String key = getSecurityKey(securityData);
			map.put(key,securityData);
		}
		return map;
	}
	
	/**
	 * All keys used across the feed program are constructed below
	 * 
	 */
	private String createKey(Long portFundN, String fmrCusip, Long txnMemoN) {
		String[] tokens = new String[3];
		
		if(portFundN != null){
			tokens[0] = String.valueOf(portFundN.longValue());
		}
		tokens[1] = String.valueOf(fmrCusip).trim();
		if(txnMemoN != null){
		
			tokens[2] = String.valueOf(txnMemoN.longValue());
		}
		 
		/*StringBuilder builder = new StringBuilder();
		builder.append(portFundN != null ? String.valueOf(portFundN.longValue()).trim():"null");
		builder.append(fmrCusip != null  ? fmrCusip.trim():"null");
		builder.append(txnMemoN != null  ? String.valueOf(txnMemoN.longValue()).trim():"null");*/
		return constructKey(tokens);
	}
			
	protected String getKeyFrmFund(FundPortfolioData fundData) {
			
		StringBuilder builder = new StringBuilder();
		builder.append(String.valueOf(fundData.getFundNumber()).trim());
		builder.append(String.valueOf(fundData.getPortfolioFundNumber()).trim());
		return builder.toString();
	}
		
	/**
	 * This method generates Key based on INV1_SCTY_CUSP_N, INV1_SCTY_DATE_N, INV1_SCTY_QUAL_N for various Business domain objects
	 * @param domainObject
	 * @return
	 */
	private String getSecurityKey(Object domainObject){
		
		String key = null;
		if(domainObject instanceof SecurityData){
			
			key = Util.createIOSecurityKey(((SecurityData)domainObject).getInvestOneCUSIPNumber(),
										   ((SecurityData)domainObject).getInvestOneSecurityDate(), 
										   ((SecurityData)domainObject).getInvestOneSecurityQualifier());
			
		}else if(domainObject instanceof CurrHldgData){
			
			key = Util.createIOSecurityKey(((CurrHldgData)domainObject).getInvestOneSecurityCusipNumber(),
					   					   ((CurrHldgData)domainObject).getInvestOneSecurityDate(), 
					   					   ((CurrHldgData)domainObject).getInvestOneSecurityQualifier());
			
		}else if(domainObject instanceof CurrHldgSetlData){
			
			key = Util.createIOSecurityKey(((CurrHldgSetlData)domainObject).getInvestOneSecurityCusipNumber(),
										   ((CurrHldgSetlData)domainObject).getInvestOneSecurityDate(), 
										   ((CurrHldgSetlData)domainObject).getInvestOneSecurityQualifier());
			
		}else if(domainObject instanceof PositionData){
			key = Util.createIOSecurityKey(((PositionData)domainObject).getInvestOneSecurityCusipNumber(),
					   					   ((PositionData)domainObject).getInvestOneSecurityDate(), 
					   					   ((PositionData)domainObject).getInvestOneSecurityQualifier());
		}
		return key;
	}
	
	private String getKeyValDateMap(Object domainObject) {
		
		StringBuilder builder = new StringBuilder();
		
		if(domainObject instanceof CurrHldgData){
			builder.append(String.valueOf(((CurrHldgData)domainObject).getFundNumber()).trim());
			builder.append(String.valueOf(((CurrHldgData)domainObject).getPortfolioFundNumber()).trim());
		}else if(domainObject instanceof CurrHldgSetlData){
			builder.append(String.valueOf(((CurrHldgSetlData)domainObject).getFundNumber()).trim());
			builder.append(String.valueOf(((CurrHldgSetlData)domainObject).getPortfolioFundNumber()).trim());
		}
		return builder.toString();
	}
	
	private String getKeyValDateFrmPosn(PositionData p) {
		
		String[] tokens = new String[2];
		tokens[0] = String.valueOf(p.getFundNumber());
		tokens[1] = String.valueOf(p.getPortfolioFundNumber());
		return constructKey(tokens);
	}
	
	private String getTxLtKeyFrmTaxLot(TaxlotData txLot) {
		
		String[] tokens = new String[2];
		tokens[0] = String.valueOf(txLot.getPortfolioFundNumber());
		tokens[1] = String.valueOf(txLot.getTaxLotMemoNumber());
		return constructKey(tokens);
	}
	
	private String getTxLtKeyFrmCurrHdg(CurrHldgData c) {
		String[] tokens = new String[2];
		tokens[0] = String.valueOf(c.getPortfolioFundNumber());
		String txnMemoN = c.getTransactionMemoNumber();
		if(StringUtils.isNotBlank(txnMemoN)){
			
			tokens[1] = String.valueOf(Long.parseLong(txnMemoN.trim()));
		}
		return constructKey(tokens);
	}
	// Fix for jira FACDAL-2240
	private String getTxLtKeyFrmCurrHdg(CurrHldgSetlData c) {
		String[] tokens = new String[2];
		tokens[0] = String.valueOf(c.getPortfolioFundNumber());
		String txnMemoN = c.getTransactionMemoNumber();
		if(StringUtils.isNotBlank(txnMemoN)){
			
			tokens[1] = String.valueOf(Long.parseLong(txnMemoN.trim()));
		}
		return constructKey(tokens);
	}
	
	private String getKeyRecordTypeForSettled(CurrHldgSetlData setlCurrencyHolding) {
		
		String[] tokens = new String[6];
		tokens[0] = String.valueOf(setlCurrencyHolding.getPortfolioFundNumber());
		tokens[1] = String.valueOf(setlCurrencyHolding.getInvestOneSecurityCusipNumber());
		tokens[2] = String.valueOf(setlCurrencyHolding.getInvestOneSecurityDate());
		tokens[3] = String.valueOf(setlCurrencyHolding.getInvestOneSecurityQualifier());
		String txnMemoN = setlCurrencyHolding.getTransactionMemoNumber();
		if(StringUtils.isNotBlank(txnMemoN)){
			
			tokens[4] = String.valueOf(Long.parseLong(txnMemoN.trim()));
		}
		tokens[5] = String.valueOf(setlCurrencyHolding.getSourceCode());
		
		return constructKey(tokens);
	}
	
	protected String getKeyRecordTypeForUnSettled(Long portFundN,String inv1SctyCusip, Double inv1SctyDate,Double inv1SctyQual,String txnMemoN, String srcCode) {
		
		String[] tokens = new String[6];
		tokens[0] = String.valueOf(portFundN);
		tokens[1] = String.valueOf(inv1SctyCusip);
		tokens[2] = String.valueOf(inv1SctyDate);
		tokens[3] = String.valueOf(inv1SctyQual);
		if(StringUtils.isNotBlank(txnMemoN)){
			
			tokens[4] = String.valueOf(Long.parseLong(txnMemoN.trim()));
		}
		tokens[5] = String.valueOf(srcCode);
		
		return constructKey(tokens);
	}
	
	private String constructKey(String[] tokens){
		
		if(tokens == null || tokens.length == 0) return null;
		
		StringBuilder builder = new StringBuilder();
		for(String token : tokens){
			
			builder.append(StringUtils.isNotBlank(token)?token.trim():"NULL");
		}

		return builder.toString();
	}
	
	/**
	 * Below method populates the TaxlotMap with the required values
	 * @param subFundNumbers
	 * @param strAsOfDate
	 * @param srcCodes
	 * @param exttCode
	 * @param  
	 * @param costBasisCodeFa
	 * @return
	 * @throws Exception 
	 */
	protected Collection<TaxlotData> getTaxLotsFromCurrHldg(String strAsOfDate,
															String strPriorAsOfDate,
															String srcCodes,
															String exttCode, 
															String costBasisCode,
															String ccType) throws Exception {
		String SQL = "PKG_FEED_ABSTRACTCURRHLD.TAXLOT_CURR_HLDG_"+ccType;
		List<DynaBean> rows = null;
		if(ccType.equals(UNSETTLED)){
			String[] params = {strAsOfDate,srcCodes,exttCode,costBasisCode};
			rows = DBUtil.executePlQuery(SQL,params,-1);
		}else if(ccType.equals(SETTLED)){
			String[] params = {strAsOfDate,strPriorAsOfDate,srcCodes,exttCode,costBasisCode};
			rows = DBUtil.executePlQuery(SQL,params,-1);
		}else{
			return null;
		}
		if(rows == null || CollectionUtils.isEmpty(rows)){
			return null;
		}
		List<TaxlotData> taxlotList = new ArrayList<TaxlotData>();
		Iterator<DynaBean> iterator = rows.iterator();
		while(iterator.hasNext()){
			
			DynaBean bean = iterator.next();
			TaxlotData taxlotData = new TaxlotData();
			BeanUtils.copyProperties(taxlotData, bean);
			taxlotList.add(taxlotData);
		}
		return taxlotList;
	}

}
