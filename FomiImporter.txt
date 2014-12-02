package com.fidelity.dal.fundacct.loader.converter.mainframeimport.fomiimporter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.fidelity.dal.fundacct.loader.converter.mainframeimport.Importer;

public class FomiImporter extends Importer {

	FomiCodeDb fcdb;
	FomiCardParser fcp;
	FomiScriptGenerator fsg;
	String[] dateFieldsToCheck;
	HashMap<String,String> coalesceValues = new HashMap<String,String>();
	
	String itemList;
	String fomiCard;
	
	public FomiImporter(String basename, Properties importerProperties,
			String datafileName, String workingDirectory)
					throws Exception {
		super(basename, importerProperties, datafileName, workingDirectory);
		
		itemList = importerProperties.getProperty("itemList");
		fomiCard = importerProperties.getProperty("fomiCard");
		
		if (importerProperties.getProperty("coalesceValues") != null) {
			String coalesce = importerProperties.getProperty("coalesceValues");
			for (String val : coalesce.split("\\s*;\\s*")) {
				coalesceValues.put(StringUtils.substringBefore(val, ","), StringUtils.substringAfter(val, ","));
			}
		}
		
		String checkDates = importerProperties.getProperty("checkDates");
		dateFieldsToCheck = checkDates == null ? new String[0] : checkDates.split("\\s*;\\s*");
		
		if (itemList == null ||fomiCard == null)
			throw new Exception(
					String.format("Invalid properties file for %s.properties\n" +
			"itemList:%s\n"+
			"fomiCard:%s\n",
			basename, itemList, fomiCard));
		
		fcdb = FomiCodeDb.getFomiCodeDb();
		boolean allowDuplicatesHack = importerProperties.getProperty("allowDuplicatesHack") != null;
		fcp = new FomiCardParser(itemList, fomiCard, allowDuplicatesHack, dateFieldsToCheck);
		fsg = new FomiScriptGenerator(fcp, allowDuplicatesHack);
		
		this.initialize();
	}
	
	void initialize() {
	}
	
	@Override
	public void dumpCreateScript(PrintWriter out, String stagingTableName)
			throws Exception {
		out.print(fsg.generateCreateSql(stagingTableName));
	}

	@Override
	public void dumpLoaderScript(String workingDirectory, PrintWriter out, String importAsciiFilename,
			String stagingTableName) throws Exception {
		if (coalesceValues.isEmpty()) {
			out.print(fsg.generateSqlldrCtl(importAsciiFilename, stagingTableName));
		} else {
			out.print(fsg.generateSqlldrCtl(importAsciiFilename, stagingTableName, coalesceValues));
		}
	}

	@Override
	public void processData(String workingDirectory, int extractId, String extractAsOfDate, PrintWriter out) throws Exception {
		File file = new File(datafileName);
		long fileLength = file.length();
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		InputStreamReader isr = null;
		try {
			fis = new FileInputStream(datafileName);
			bis = new BufferedInputStream(fis, 1024*1024);
			isr = new InputStreamReader(bis, getPropertiesCharset());
			int recordLength = fcp.getInboundRecordByteLength() + endOfLineByteCount;
			logger.debug(String.format("recordSize:%d", recordLength));
			char[] buf;
			if (endOfLineByteCount == 0) {
				buf = new char[recordLength + 1];
				// slap a newline at the end of buf
				buf[buf.length - 1] = '\n';
			} else {
				// file lines already have end-of-line termination
				buf = new char[recordLength];
			}
			if (fileLength % recordLength != 0) {
				System.err.printf("file: %s\n", datafileName);
				System.err.printf("fileLength: %d", fileLength);
				System.err.printf("recordLength: %d\n", recordLength);
				System.err.printf("fileLength %% recordLength: %d\n", fileLength % recordLength);
				throw new Exception("fileLength not an integral multiple of recordLength");
			}
			int readCount;
			int recordCount = 0;
			while ((readCount = isr.read(buf, 0, recordLength)) == recordLength) {
				++recordCount;
				out.write(getFormattedExtractIdRowNum(extractId, extractAsOfDate, recordCount));
				substitutor.substituteInvalidChars(recordCount, buf);
				//log invalid dates
				for (FomiDate fd: fcp.fomiDatesToCheck) {
					dateLogger.checkDateValidityAndLog(recordCount, fd.index, 
							fd.fomiCodeRecord.fieldName, new String (Arrays.copyOfRange(buf, fd.index, fd.index+8)));
				}
				out.write(buf);
			}
			if (readCount > 0 && readCount != recordLength)
				throw new Exception(
						String.format("%d chars extra\n " +
								"file size of %s is not an integral multiple of record length:%d",
								readCount, datafileName, recordLength));
			setProcessedRecordCount(recordCount);
		} finally {
			if (isr != null)
				isr.close();
			if (bis != null)
				bis.close();
			if (fis != null)
				fis.close();
		}
		// TODO set header/trailer
	}	
	
	@Override
	public void close() throws Exception {
	}

	@Override
	protected void dumpMappingTableRecords(PrintWriter out, String stageTableName) {
		String filename = datafileName.substring(Math.max(datafileName.lastIndexOf('/'), datafileName.lastIndexOf('\\')) + 1, 
				datafileName.indexOf(".20120"));
		for (FomiCodeRecord fcr : fcp.fomiCodeRecords) {
			if (fcr.typeString.equals("n") || fcr.typeString.equals("i") || fcr.typeString.equals("d")) {
				out.println(String.format("%s,%s,\"%s(%s,%s)\",%s,%s_%s_%s",filename,fcr.codeString,
						"n",fcr.width,fcr.decimalCount,stageTableName,fcr.itemListString,fcr.codeString,fcr.fieldName));
			} else {
				out.println(String.format("%s,%s,\"%s(%s)\",%s,%s_%s_%s",filename,fcr.codeString,
						"c",fcr.width,stageTableName,fcr.itemListString,fcr.codeString,fcr.fieldName));
			}
		}
	}
	

	/**
	 * @param args
	 */
/*
	public static void main(String[] argv) {
		if (argv.length != 4)
			printUsage();
		String scriptBaseName = argv[0];
		String itemList = argv[1];
		String fomiCard = argv[2];
		String stageTableName = argv[3];
		System.out.printf("scriptBaseName: %s\n", scriptBaseName);
		System.out.printf("      itemList: %s\n", itemList);
		System.out.printf("      fomiCard: %s\n", fomiCard);
		System.out.printf("stageTableName: %s\n", stageTableName);
		FomiImporter fr = new FomiImporter();
		fr.generate(itemList, fomiCard, stageTableName, scriptBaseName);
	}
	
	static void printUsage() {
		System.err.print("usage: programName <ScriptBaseName> <ItemList> <FomiCard> <TableName>\n" +
				"ex: foo path/FomiSdHold SD ANCUSUSVSL3EACBN8I8J9LRBRECOP79ZAMINRFP8PB STAGE_FOMI_SD_HOLD\n");
		System.exit(1);
	}
	*/
}
