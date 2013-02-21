package com.barchart.feed.ddf.instrument.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TestVersionParser {

	private static final String S3_URL = "https://s3.amazonaws.com/instrument-def/";
	
	private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	public static void main(final String[] args) throws Exception {
		
		
		System.out.println(getLatestVersion());
		
	}
	
	private static String getLatestVersion() throws Exception {
		
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		Element element = db.parse(S3_URL).getDocumentElement();
		NodeList nodeList = element.getChildNodes();
		
		List<DateTime> versionTimes = new ArrayList<DateTime>();
		
		for(int i = 0; i < nodeList.getLength(); i++) {
			
			if(nodeList.item(i).getNodeName().equals("Contents")) {
				
				NodeList contentNodes = nodeList.item(i).getChildNodes();
				for(int n = 0; n < contentNodes.getLength(); n++) {
					
					if(contentNodes.item(n).getNodeName().equals("LastModified")) {
						versionTimes.add(TIMESTAMP_FORMATTER.parseDateTime(
								contentNodes.item(n).getTextContent().split("\\.")[0]));
					}
					
				}
				
			}
		}
		
		Collections.sort(versionTimes);
		
		return TIMESTAMP_FORMATTER.print(versionTimes.get(versionTimes.size() - 1));
		
	}
	
}
