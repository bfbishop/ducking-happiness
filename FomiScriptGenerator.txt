package com.fidelity.dal.fundacct.loader.converter.mainframeimport.fomiimporter;

import java.util.HashMap;

import com.fidelity.dal.fundacct.loader.converter.mainframeimport.Importer;

public class FomiScriptGenerator {

	FomiCardParser fomiCardParser;
	boolean allowDuplicates;
	
	FomiScriptGenerator(FomiCardParser fomiCardParser, boolean allowDuplicates) {
		this.fomiCardParser = fomiCardParser;
		this.allowDuplicates = allowDuplicates;
	}
	
	String generateCreateSql(String tableName) {
		resetDuplicateLineItemNameMap();
		
		String header = 
			String.format("WHENEVER SQLERROR CONTINUE;\nDROP TABLE %s;\nCREATE TABLE %s (\n",
							tableName, tableName);
		String body = getCreateSqlBody();
		String footer = ");\n";
		resetDuplicateLineItemNameMap();
		String columnComments = getColumnComments(tableName);
		return header + body + footer + "\n" + columnComments + "\n";
	}
	
	private String getCreateSqlBody() {
		int codeCount = fomiCardParser.getOutputFieldCount();
		String pendingComment = "";
		StringBuffer sbBody = new StringBuffer();
		sbBody.append(Importer.getCreateSqlExtractIdRowNum());
		for (int i = 0; i < codeCount; ++i) {
			if (i > 0) {
				sbBody.append(",");
				sbBody.append(pendingComment);
				sbBody.append("\n");
			}
			sbBody.append(getCreateSqlLineItem(fomiCardParser.getFomiCodeRecord(i)));
			pendingComment = getLineItemComment(fomiCardParser.getFomiCodeRecord(i));
		}
		sbBody.append(" ");
		sbBody.append(pendingComment);
		sbBody.append("\n");
		return "" + sbBody;
	}
	
	private String getCreateSqlLineItem(FomiCodeRecord fcr) {
		return formatCreateSqlLineItem(getLineItemName(fcr), getCreateSqlLineItemType(fcr));
	}
	
	private String formatCreateSqlLineItem(String lineItemName, String lineItemType) {
		return String.format("%-30s %15s", lineItemName, lineItemType);
	}
	
	HashMap<String, Integer> duplicateLineItemNameMap;
	
	private void resetDuplicateLineItemNameMap() {
		duplicateLineItemNameMap = new HashMap<String, Integer>();
	}
	
	private String getLineItemName(FomiCodeRecord fcr) {
		String lineItemName = fcr.getLineItemNamePrefix() + fcr.getFieldName();
		String lineItemNameT = lineItemName;
		Integer boxedDuplicateCount = duplicateLineItemNameMap.get(lineItemName);
		int duplicateCount = 0;
		if (boxedDuplicateCount != null)
			duplicateCount = boxedDuplicateCount.intValue(); 
		if (duplicateCount > 0)
			lineItemName += "_" + duplicateCount;
		if (lineItemName.length() > 30)
			throw new RuntimeException(String.format("line item name too long %d > 30 %s", lineItemName.length(), lineItemName));
		++duplicateCount;
		duplicateLineItemNameMap.put(lineItemNameT, new Integer(duplicateCount));
		return lineItemName;
	}
	
	private String getLineItemComment(FomiCodeRecord fcr) {
		String comment = "";
		String description = fcr.getDescription();
		if (description != null && description.length() != 0)
			comment = " -- " + description;
		return comment;
	}
	
	private String getCreateSqlLineItemType(FomiCodeRecord fcr) {
		String typeString = fcr.getTypeString();
		int width = fcr.getWidth();
		int decimalCount = fcr.getDecimalCount();
		
		if (typeString.equals("c"))
			return String.format("varchar2(%d)", width);
		
		if (typeString.equals("i") || typeString.equals("d")) {
			if (decimalCount != 0)
				throw new RuntimeException("Integer type should not have a decimal count: " + fcr);
			return String.format("number(%d)", width);
		}
		
		if (typeString.equals("n"))
			if (decimalCount == 0)
				return String.format("number(%d)", width);
			else
				return String.format("number(%d,%d)", width, decimalCount);

		throw new RuntimeException(String.format("unrecognized type string:%s in fomi code record:%s", typeString, fcr));
	}
	
	String generateSqlldrCtl(String datafileName, String tableName) {
		/*
		String header = 
		out.print("options (silent=(feedback))");
			String.format("load data\ninfile %s \"fix %d\"\nreplace into table %s \n(\n", 
					datafileName, fomiCardParser.getByteLength(), tableName);
					*/
		resetDuplicateLineItemNameMap();
		
		String header = "options (silent=(feedback),\n" +
				"bindsize=50000000, readsize=50000000,\n" +
				"direct=true, parallel=true)\n" +
				"unrecoverable load data\n" +
				String.format("infile %s\nappend into table %s \n(\n", 
								datafileName, tableName);
		String body = getSqlldrCtlBody();
		String footer = ")\n";
		return header + body + footer;
	}
	
	String generateSqlldrCtl(String datafileName, String tableName, HashMap<String,String> coalesceValues) {
		/*
		String header = 
		out.print("options (silent=(feedback))");
			String.format("load data\ninfile %s \"fix %d\"\nreplace into table %s \n(\n", 
					datafileName, fomiCardParser.getByteLength(), tableName);
					*/
		resetDuplicateLineItemNameMap();
		
		String header = "options (silent=(feedback),\n" +
				"bindsize=50000000, readsize=50000000,\n" +
				"direct=true, parallel=true)\n" +
				"unrecoverable load data\n" +
				String.format("infile %s\nappend into table %s \n(\n", 
								datafileName, tableName);
		String body = getSqlldrCtlBody(coalesceValues);
		String footer = ")\n";
		return header + body + footer;
	}
	
	private String getSqlldrCtlBody() {
		int codeCount = fomiCardParser.getOutputFieldCount();
		String pendingComment = "";
		StringBuffer sbBody = new StringBuffer();
		sbBody.append(Importer.getSqlldrCtlExtractIdRowNum());
		for (int i = 0; i < codeCount; ++i) {
			if (i > 0) {
				sbBody.append(",");
				sbBody.append(pendingComment);
				sbBody.append("\n");
			}
			sbBody.append(getSqlldrCtlLineItem(i));
			pendingComment = getLineItemComment(fomiCardParser.getFomiCodeRecord(i));
		}
		sbBody.append(" ");
		sbBody.append(pendingComment);
		sbBody.append("\n");
		return "" + sbBody;
	}
	
	private String getSqlldrCtlBody(HashMap<String,String> coalesceValues) {
		int codeCount = fomiCardParser.getOutputFieldCount();
		String pendingComment = "";
		StringBuffer sbBody = new StringBuffer();
		sbBody.append(Importer.getSqlldrCtlExtractIdRowNum());
		for (int i = 0; i < codeCount; ++i) {
			if (i > 0) {
				sbBody.append(",");
				sbBody.append(pendingComment);
				sbBody.append("\n");
			}
			if (coalesceValues.containsKey(fomiCardParser.getFomiCodeRecord(i).codeString)) {
				sbBody.append(getSqlldrCtlLineItemCoalesce(i,
						coalesceValues.get(fomiCardParser.getFomiCodeRecord(i).codeString)));
			} else {
				sbBody.append(getSqlldrCtlLineItem(i));
			}
			pendingComment = getLineItemComment(fomiCardParser.getFomiCodeRecord(i));
		}
		sbBody.append(" ");
		sbBody.append(pendingComment);
		sbBody.append("\n");
		return "" + sbBody;
	}
	
	private String getSqlldrCtlLineItem(int i) {
		FomiCodeRecord fcr = fomiCardParser.getFomiCodeRecord(i);
		return formatSqlldrCtlLineItem(
				getLineItemName(fcr), fomiCardParser.getPositionStart(i), fomiCardParser.getPositionEnd(i), fcr.getWidth());
				
	}
	
	private String getSqlldrCtlLineItemCoalesce(int i, String coalesceValues) {
		FomiCodeRecord fcr = fomiCardParser.getFomiCodeRecord(i);
		return formatSqlldrCtlLineItemCoalesce(
				getLineItemName(fcr), fomiCardParser.getPositionStart(i), fomiCardParser.getPositionEnd(i), fcr.getWidth(), coalesceValues);
				
	}
	
	private String formatSqlldrCtlLineItem(String lineItemName,
			int positionStart, int positionEnd, int width) {
		return String.format("%-30s position(%03d:%03d) char(%2d)", 
				lineItemName, positionStart, positionEnd, width);
	}
	
	private String formatSqlldrCtlLineItemCoalesce(String lineItemName,
			int positionStart, int positionEnd, int width, String coalescedVal) {
		return String.format("%-30s position(%03d:%03d) char(%2d) \"NVL(:%-30s,'%s')\"", 
				lineItemName, positionStart, positionEnd, width, lineItemName, coalescedVal);
	}
	
	private String getColumnComments(String tableName) {
		int codeCount = fomiCardParser.getOutputFieldCount();
		StringBuffer columnComments = new StringBuffer();
		for (int i = 0; i < codeCount; ++i) {
			FomiCodeRecord fcr = fomiCardParser.getFomiCodeRecord(i);
			String fieldName = getLineItemName(fcr);
			String description = fcr.getDescription();
			String columnComment;
			columnComment = String.format("COMMENT ON COLUMN %s.%s IS\n  '%s';\n",
					tableName, fieldName, description);
			columnComments.append(columnComment);
		}
		return "" + columnComments;
	}
}
