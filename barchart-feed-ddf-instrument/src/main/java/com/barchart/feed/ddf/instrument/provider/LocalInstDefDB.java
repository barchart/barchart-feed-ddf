package com.barchart.feed.ddf.instrument.provider;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LocalInstDefDB {
	
	private static final Logger log = LoggerFactory.getLogger(LocalInstDefDB.class);
	
	private static final String INST_DEF = "instrumentDef.zip";
	private static final String URL = "https://s3.amazonaws.com/instrument-def/active/instrumentDef.zip";
	private static final String S3_URL = "https://s3.amazonaws.com/instrument-def/";
	
	private static final String DB_DIR = "persist";
	
	private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	private final LocalInstrumentDBMap dbMap;
	
	public LocalInstDefDB(final String resourceRoot) throws ZipException, IOException {
		
		final File resourceFolder = new File(resourceRoot);
		
		final File dbFolder = getDBFolder(resourceRoot);
		
		/* Resolve and update local instrument definition */
		final String localInstDefVersion = getLocalInstDefVersion(resourceFolder);
		final String latestRemoteInstDefVersion = getLatestS3InstDefVersion();
		
		if(latestRemoteInstDefVersion != null) {
			
			if(!latestRemoteInstDefVersion.equals(localInstDefVersion)) {
				
				try {
					populateLocalInstDef(resourceRoot);
				} catch (IOException e) {
					e.printStackTrace();
				}
				dbMap = new LocalInstrumentDBMap(dbFolder, getLatestInstDefZipStream(
						getLocalInstDef(resourceFolder)));
			} else {
				dbMap = new LocalInstrumentDBMap(dbFolder);
			}
			
		} else {
			
			if(localInstDefVersion != null) {
				
				if(hasDB(resourceFolder)) {
					dbMap = new LocalInstrumentDBMap(dbFolder);
				} else {
					dbMap = new LocalInstrumentDBMap(dbFolder, getLatestInstDefZipStream(
							getLocalInstDef(resourceFolder)));
				}
				
			} else {
				throw new RuntimeException("No local instrument def and unable to retrieve remote def");
			}
			
		}
		
	}
	
	private String getLatestS3InstDefVersion() {
		
		try {
		
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
		
		} catch (Exception e) {
			
			log.error("Exception getting latest s3 version {}" + e.getMessage());
			
			return null;
		}
		
	}
	
	/* Returns null if none found at resource location */
	private String getLocalInstDefVersion(final File resourceFolder) {
		
		String version = null;
		final File[] files = resourceFolder.listFiles(new ZipFilter());
		final List<DateTime> versions = new ArrayList<DateTime>();
		for(final File file : files) {
			
			try {
				
				versions.add(TIMESTAMP_FORMATTER.parseDateTime(file.getName().split("\\.")[0]));
				
			} catch (Exception e) {
				// ignore failed parses
			}
			
		}
		
		if(!versions.isEmpty()) {
			Collections.sort(versions);
			version = versions.get(versions.size() - 1).toString(TIMESTAMP_FORMATTER);
		}
		
		return version;
		
	}
	
	private File getLocalInstDef(final File resourceFolder) {
		return null;
	}
	
	private InputStream getLatestInstDefZipStream(final File instDef) throws ZipException, IOException {
		
		final ZipFile zFile = new ZipFile(instDef);
		final ZipEntry entry = zFile.entries().nextElement();
		final InputStream zinStream = zFile.getInputStream(entry);
		
		return zinStream;
	}
	
	private void populateLocalInstDef(final String resourceRoot) throws IOException {
		
		final URL instDefURL = new URL(URL);
		URLConnection conn = instDefURL.openConnection();
       
		InputStream in = conn.getInputStream();
        
        File outFile = new File(resourceRoot + "/instrumentDef.zip");
        FileOutputStream out = new FileOutputStream(outFile);
        
        byte[] b = new byte[1024];
        int count;
        while ((count = in.read(b)) >= 0) {
            out.write(b, 0, count);
        }
        out.flush(); out.close(); in.close();
        
	}
	
	private boolean hasDB(final File resourceFolder) {
		
		File dbFolder = null;
		for(final File file : resourceFolder.listFiles()) {
			if(file.isDirectory() && file.getName().equals(DB_DIR)) {
				dbFolder = file;
				break;
			}
		}
		
		if(dbFolder == null) {
			return false;
		}
		
		for(final String file : dbFolder.list()) {
			if(file.toLowerCase().endsWith(".jdb")) {
				return true;
			}
		}
		
		return false;
	}
	
	private File getDBFolder(final String root) {
		return null;
	}
	
	private class ZipFilter implements FileFilter {

		@Override
		public boolean accept(final File pathname) {
			return pathname.getName().toLowerCase().endsWith(".zip");
		}
		
	}
	
}
