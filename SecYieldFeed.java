package com.fidelity.dal.fundacct.feed.app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fidelity.dal.fundacct.feed.bean.DALCalendar;
import com.fidelity.dal.fundacct.feed.bean.DALSecRefService;
import com.fidelity.dal.fundacct.feed.bean.FileWriter;
import com.fidelity.dal.fundacct.feed.framework.db.ConfigDBAccess;
import com.fidelity.dal.fundacct.feed.framework.feed.AbstractPlSqlFeed;
import com.fidelity.dal.fundacct.feed.framework.feed.DBUtil;
import com.fidelity.dal.fundacct.feed.framework.feed.DefaultRowFormatter;
import com.fidelity.dal.fundacct.feed.framework.feed.ErrorCheck;
import com.fidelity.dal.fundacct.feed.framework.feed.FeedProperties;
import com.fidelity.dal.fundacct.feed.framework.feed.RowFilter;
import com.fidelity.dal.fundacct.feed.framework.feed.RowFormatter;
import com.fidelity.dal.fundacct.feed.framework.feed.RowHandler;
import com.fidelity.dal.fundacct.feed.framework.util.Constants;
import com.fidelity.dal.fundacct.feed.framework.util.Initialize;
import com.fidelity.dal.fundacct.feed.framework.util.Util;

/**
 * This feed generates Security Yields file from DAL.
 * Then the feed file will be sent to FIMT application.
 * @author a505834 Moved the original Java implementation to a PL/SQL driven one
 *
 */
public class SecYieldFeed extends AbstractPlSqlFeed {

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

	
	
	public static String APP_NAME = "SECYIELDFEED";
	private SimpleDateFormat dd_MMM_yyyy_fmt = new SimpleDateFormat("dd-MMM-yyyy");
	private DateTimeFormatter yyyyMMdd_fmt = DateTimeFormat.forPattern("yyyyMMdd");
	private Date BODAsOfDate = null;
	private Date EODAsOfDate = null;
	private Map<String,BigDecimal> effMaturityYears = null; 
	private DALSecRefService dalSecRefService = null;
	private final String BLANKSPACE = "";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
	private Date businessDate = null;
	private Date todayDate = null;
	
	@Override
	protected void beforeDoFeed() throws Exception {
		businessDate = getToday();
		todayDate = new Date();
		dalCal = new DALCalendar();
		setEODAsOfDate(getToday());
		setBODAsOfDate(dalCal.getNextCalendarDay(getToday()));
		dalSecRefService = new DALSecRefService();
		effMaturityYears = dalSecRefService.getEffMaturityDatesFromSecRef(dd_MMM_yyyy_fmt.format(getBODAsOfDate()), "BOD", "FA");
		updateFetchSize();
	}
	
	@Override
	protected void afterDoFeed() throws Exception {
		dalCal = null;
		BODAsOfDate = null;
		EODAsOfDate = null;
		dalSecRefService = null;
		effMaturityYears = null;
		businessDate = null;
		todayDate = null;
	}
	
	@Override
	protected void createBody() throws Exception {
		Util.log(this, "Starting SecYield Feed", Constants.LOG_LEVEL_INFO, null);
		String asOfDateStr = dd_MMM_yyyy_fmt.format(getBODAsOfDate());
		String[] parameterFOCAS = { asOfDateStr };
		Util.log(this, "Starting SecYieldFeedNew: Sec Yield Data for FOCAS", Constants.LOG_LEVEL_INFO, null);
		String sql = "PKG_FEED_SECYIELD.GET_SECYIELD_FOCAS";
		processRecords(sql, parameterFOCAS, 
				null,
				new SecYieldFeedRowFormatter(), 
				new SecYieldFeedRowFormatter());
		Util.log(this, "End SecYieldFeedNew: Sec Yield Data for FOCAS", Constants.LOG_LEVEL_INFO, null);
		
		Util.log(this, "Starting SecYieldFeedNew: Sec Yield Data for IO", Constants.LOG_LEVEL_INFO, null);
		sql = "PKG_FEED_SECYIELD.GET_SECYIELD_IO";
		String[] parameterIO = { asOfDateStr , dd_MMM_yyyy_fmt.format(getEODAsOfDate())};
		processRecords(sql, parameterIO, 
				null,
				new SecYieldFeedRowFormatter(), 
				new SecYieldFeedRowFormatter());
		Util.log(this, "End SecYieldFeedNew: Sec Yield Data for IO", Constants.LOG_LEVEL_INFO, null);
		
		Util.log(this, "End of SecYieldFeedNew", Constants.LOG_LEVEL_INFO, null);
		
		
	}
	
	private class SecYieldFeedRowFormatter extends DefaultRowFormatter{

		private final String UNKNOWN = "?";
		private final float NUM_FILLER = 0; 
		private final String CHAR_FILLER = "";
		private final String REG_EX_PERIOD = "[.]";  
		private final String BLANK_STRING = "";
		private final int CUSIP_IDX = 2;
		private final int ORG_SYS_IDX = 13;
		private final int CL_TY_C_IDX = 14;
		
		@Override
		protected String formatCell(Object[] cols, int colIndex) throws Exception {
			String s = "";
			int columnOrder = colIndex + 1;
			try {
				switch (columnOrder) {
					case 1://ACC-NUMBER-01
						s = getFormattedValue("%15s", cols[colIndex]);
						break;
					case 2://INCOME-CATEGORY-CODE-02
						s = getFormattedValue("%3s", cols[colIndex]);
						break;
					case 3://CUSIP-03
						s = getFormattedValue("%9s", cols[colIndex]);
						break;
					case 4://EFFECTIVE-MATURITY-DATE-04
						if(cols[ORG_SYS_IDX].toString().equals("I")){
							DateTime effMaturityDate = null;
							if(cols[CL_TY_C_IDX] != null && cols[CL_TY_C_IDX].toString().equalsIgnoreCase("MR")){
								String fmrCusip = cols[CUSIP_IDX].toString();	
								if(!effMaturityYears.isEmpty() && effMaturityYears.containsKey(fmrCusip)) {
									BigDecimal expectedLifeYears = effMaturityYears.get(fmrCusip);
									effMaturityDate = calculateEffMaturityDate(expectedLifeYears, EODAsOfDate);
								} else {
									DateTime initialDate = new DateTime("1900-01-01");
									DateTime todaydate = new DateTime(EODAsOfDate);
									int diffdays = Days.daysBetween(initialDate, todaydate).getDays();
									effMaturityDate = initialDate.plusDays(diffdays);
								}
							}
							s = getFormattedValue("%8s",  effMaturityDate != null ? String.valueOf(yyyyMMdd_fmt.print(effMaturityDate)) : CHAR_FILLER);
						} else {
							s = getFormattedValue("%8s", cols[colIndex]);	
						}
						break;
					case 5://REDEMPTION-PRICE-05
						s = getFormattedValue("%019.7f", cols[colIndex], true);
						break;
					case 6://CURR-PRICE-06
						s = getFormattedValue("%019.7f", cols[colIndex], true);
						break;
					case 7://QTY-PAR-07
						s = getFormattedValue("%020.4f", cols[colIndex], true);
						break;
					case 8://ACCRUED-INTEREST-AMT-08
						s = getFormattedValue("%+019.2f", cols[colIndex], true);
						break;
					case 9://YIELD-09
						s = getFormattedValue("%+018.8f", cols[colIndex], true);
						break;
					case 10://DAILY-INCOME-AMT-10
						s = getFormattedValue("%+013.2f", cols[colIndex], true);
						break;
					case 11://YIELD-TYPE-FLAG-11
						s = getFormattedValue("%20s", cols[colIndex]);
						break;
					case 12://ERR-MSG-12
						s = getFormattedValue("%30s", cols[colIndex]);
						break;
					case 13://PDN-GAIN-LOSS-AMT-13
						s = getFormattedValue("%+019.2f", cols[colIndex], true);
						break;
					case 14://ORIGINATING-SYSTEM-14
						s = getFormattedValue("%1s", cols[colIndex].toString());
						break;
				default:
						s = cols[colIndex] == null ? "" : cols[colIndex].toString();
				}
			} catch (Exception ex) {
				Util.log(
						this.getClass().getName(),
						"Error occured in SecYieldFeedRowFormatter.formatCell method: Column Index: " + colIndex + " Data Value: " + cols[colIndex] + ex
								.getMessage(), Constants.LOG_LEVEL_ERROR, ex);
				throw ex;
			}
			
			return s;
		}
		
		private String getFormattedValue(String format, Object value, boolean includeDecimal) {
			String formattedValue = String.format(format, value != null ?  Double.valueOf(value.toString()).doubleValue() : NUM_FILLER );
			if(!includeDecimal)
				formattedValue = formattedValue.replaceAll(REG_EX_PERIOD, BLANK_STRING);
			return formattedValue;
		}
		
		private String getFormattedValue(String format, Object value) {
			if (value == null){
				value = "";
			}
			String formattedValue = String.format(format, value.toString());
			if(value.equals(UNKNOWN)) {
				formattedValue = formattedValue.replaceAll(" ", "?");
			} 
			return formattedValue;
		}
		
		private DateTime calculateEffMaturityDate(BigDecimal expectedLifeYears, Date todaysDate) {
			
			BigDecimal expectedLifeDays = expectedLifeYears.multiply(new BigDecimal("365.25")).setScale(0,RoundingMode.HALF_UP);
			DateTime initialDate = new DateTime("1900-01-01");
			DateTime today = new DateTime(todaysDate);
			int diffdays = Days.daysBetween(initialDate, today).getDays();
			BigDecimal daysFromInitialDateToToday = new BigDecimal(diffdays);
			BigDecimal daysFromInitialDateToLife = daysFromInitialDateToToday.add(expectedLifeDays);
			DateTime effMaturityDate = initialDate.plusDays(daysFromInitialDateToLife.intValue());
			return effMaturityDate;
		}
	}
	
	/**
     * Customize Header record
     */
	protected void printHeader(FileWriter fileWriter) {
		String fileId = feedProperties.getFileIdentifier();
		if(StringUtils.isNotBlank(fileId) && fileId.length() > 8){
			fileId = fileId.substring(0,8);
		}
		String hostName = Util.getHostName();
		if(StringUtils.isNotBlank(hostName) && hostName.length() > 8){
			hostName = hostName.substring(0,8);
		}
		String header = String.format(
				"%6s%1s%-8s%1s%8s%1s%8s%1s%8s%1s%6s%1s%015d%1s%3s", "IMAHDR",
				BLANKSPACE, hostName, BLANKSPACE, fileId, BLANKSPACE,
				dateFormat.format(businessDate), BLANKSPACE,
				dateFormat.format(todayDate), BLANKSPACE,
				timeFormat.format(todayDate), BLANKSPACE, 0, BLANKSPACE,
				BLANKSPACE);
		
		fileWriter.writeln(header);
		
	}
	 /**
     * Customize trailer record
     */
	protected void printTrailer(FileWriter fileWriter) {
		
		String fileId = feedProperties.getFileIdentifier();
		if(StringUtils.isNotBlank(fileId) && fileId.length() > 8){
			fileId = fileId.substring(0,8);
		}
		String hostName = Util.getHostName();
		if(StringUtils.isNotBlank(hostName) && hostName.length() > 8){
			hostName = hostName.substring(0,8);
		}
		String trailerPrefix = String.format(
				"%6s%1s%-8s%1s%8s%1s%8s%1s%8s%1s%6s%1s%015d%1s%3s", "IMATRL",
				BLANKSPACE,hostName, BLANKSPACE, fileId, BLANKSPACE,
				dateFormat.format(businessDate), BLANKSPACE,
				dateFormat.format(todayDate), BLANKSPACE,
				timeFormat.format(todayDate), " ",
				fileWriter.getNoOfRecordsWritten(), BLANKSPACE, BLANKSPACE);
		
		fileWriter.writeln(trailerPrefix);
	}
	
	public static void main(String[] args) throws Exception {
		new Initialize();
		SecYieldFeed secYieldFeed = new SecYieldFeed();
		secYieldFeed.setFeedAppName(APP_NAME);
		ConfigDBAccess.getStartupApps(APP_NAME, null);		
		secYieldFeed.processMessage(62022);
		System.out.println("its done processing");
		System.exit(0);
	}

	public Date getBODAsOfDate() {
		return BODAsOfDate;
	}

	public void setBODAsOfDate(Date bODAsOfDate) {
		BODAsOfDate = bODAsOfDate;
	}

	public Date getEODAsOfDate() {
		return EODAsOfDate;
	}

	public void setEODAsOfDate(Date eODAsOfDate) {
		EODAsOfDate = eODAsOfDate;
	}

	@Override
	protected void processRecords(String sql, String[] params, 
			final RowFilter rowFilter, 
			final RowFormatter recordFormatter, 
			final RowFormatter errorRecordFormatter) 
	throws Exception {
		final FeedProperties feedProperties = super.getFeedProperties();
		DBUtil.packageQuery(sql, params, feedProperties.getFetchSize(), 
		new RowHandler() {
			public void handleRow(ResultSet rs) throws SQLException {
				int columnCount = rs.getMetaData().getColumnCount();
				Object[] cols = new Object[columnCount];
				for (int i = 0; i < columnCount; i++) {
					cols[i] = rs.getObject(i + 1);
				}
				
				String delimitor = feedProperties.getDelimitor();
				if (rowFilter != null) {
					if (rowFilter.skipRow(cols)) return;
					ErrorCheck errorCheck = rowFilter.isErrorRow(cols);
					if (errorCheck != null) {
						if (errorCheck.hasErrorFileError()) {
							writeErrorRecord(cols, delimitor, errorRecordFormatter, getErrorFileWriter(), errorCheck.getReason());
						}
						return;
					}
				}

				try {
					writeRecord(cols, delimitor, recordFormatter, getFeedFileWriter());
					if (geodeFunds.contains(((BigDecimal)cols[0]).longValue())) {
						writeRecord(cols, delimitor, recordFormatter, fwGeode);
					}
				}
				catch(Exception ex) {
					if (new Object().toString()!= "b") {
						ex.printStackTrace();
						System.exit(1);
					}
					if (rowFilter != null) {
						writeErrorRecord(cols, delimitor, errorRecordFormatter, getErrorFileWriter(), "Error " + ex.getMessage());
					}
					Util.log(this.getClass().getName(), "Error occured in Write Record method." + ex.getMessage(), Constants.LOG_LEVEL_ERROR, ex);
				}
			}
		});
	}
	

}