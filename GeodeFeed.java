package com.fidelity.dal.fundacct.feed.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;

import org.springframework.util.StopWatch;

import com.fidelity.dal.fundacct.feed.bean.DALFeedConfiguration;
import com.fidelity.dal.fundacct.feed.bean.FileWriter;
import com.fidelity.dal.fundacct.feed.framework.jms.JMSMessageProcessorBase;
import com.fidelity.dal.fundacct.feed.framework.util.Constants;
import com.fidelity.dal.fundacct.feed.framework.util.Util;

public class GeodeFeed extends JMSMessageProcessorBase {

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


	@Override
	protected void processMessage(int event) throws Exception {
		Util.log(this,"Received Event "+event);
		boolean status = false;
		StopWatch stopWatch = new StopWatch();
		int recordCount = 0;
		try{
			dfc = getConfiguration();
		}catch(Exception e){
			Util.log(this, "Exception thrown while constructing DALFeedConfiguration for the event "+event+"\n"+e.getMessage(), Constants.LOG_LEVEL_ERROR, e);
			throw e;
		}
		stopWatch.start(dfc.getAppName());
		try {
			Util.log(this, "App Name: "+ dfc.getAppName());
			String originalAppName = dfc.getAppName().replace("_GEODE", "");
			DALFeedConfiguration originaldfc = new DALFeedConfiguration(originalAppName);
			String originalFileName = originaldfc.getOutputLocalFileName();
			originaldfc.setOutputLocalFileName(originalFileName+".geode");
			FileWriter fw = new FileWriter(originaldfc);
			Integer fundNIndex = Integer.valueOf(Util.getValueFromResourceBundle(dfc.getAppName(), "PORT_FUND_N_INDEX", null));
			Integer fundNWidth = Integer.valueOf(Util.getValueFromResourceBundle(dfc.getAppName(), "PORT_FUND_N_WIDTH", null));
			BufferedReader bufferedReader = new BufferedReader(new FileReader(originaldfc.getOutputLocalDirectory()+Constants.SYSTEM_FILE_SEPARATOR+originalFileName));
			String currentLine;
			Util.log(this, "Start writing Geode Feed for feed app: "+ originaldfc.getAppName());
			while ((currentLine = bufferedReader.readLine()) != null) {
	
				if (currentLine.startsWith("IMAHDR") || currentLine.startsWith("IMATRL")) {
					fw.writeln(currentLine);
				} else {
					if (geodeFunds.contains(Long.valueOf(currentLine.substring(fundNIndex, fundNIndex+fundNWidth)))){
						fw.writeln(currentLine);
					}
				}
			}
			recordCount = fw.getNoOfRecordsWritten();
			Util.log(this, "Finished writing Geode Feed for feed app: "+ originaldfc.getAppName());
			bufferedReader.close();
			fw.close();
			status = true;
		} catch (Exception e){
			status = false;
			Util.log(this, "Exception occured while running feed "+ dfc.getAppName(), Constants.LOG_LEVEL_ERROR, e);
		}
		stopWatch.stop();
		logRunMetrics(this, Constants.LOG_TYPE_STATS,stopWatch.getTotalTimeSeconds(),recordCount,stopWatch.prettyPrint());
		logRunMetrics(this, Constants.LOG_TYPE_RESULT,stopWatch.getTotalTimeSeconds(), recordCount, status ?Constants.LOG_RESULT_SUCCESS:Constants.LOG_RESULT_FAIL);
	}

}
