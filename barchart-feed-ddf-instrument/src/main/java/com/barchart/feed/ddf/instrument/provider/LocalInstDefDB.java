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
import java.util.zip.ZipException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.barchart.proto.buf.inst.InstrumentDefinition;

public class LocalInstDefDB {
	
	private static final Logger log = LoggerFactory.getLogger(LocalInstDefDB.class);
	
	//private static final String INST_DEF = "instrumentDef.zip";
	private static final String URL = "https://s3.amazonaws.com/instrument-def/active/instrumentDef.zip";
	private static final String S3_URL = "https://s3.amazonaws.com/instrument-def/";
	
	private static final String DB_DIR = "persist";
	
	private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	private final LocalInstrumentDBMap map;
	
	public LocalInstDefDB(final String resourceRoot) throws ZipException, IOException {
		
		if(resourceRoot == null || resourceRoot.length() == 0) {
			throw new IllegalArgumentException("Resource root is empty");
		}
		
		final File resourceFolder = new File(resourceRoot);
		if(!resourceFolder.isDirectory()) {
			throw new IllegalArgumentException("Resource root must be a directory");
		}
		
		final File dbFolder = getDBFolder(resourceFolder);
		
		DB_STATUS dbStatus = DB_STATUS.getStatus(dbFolder);
		INST_DEF_STATUS instDefStatus = INST_DEF_STATUS.getStatus(resourceFolder);
		
		switch(instDefStatus) {
		
		case NONE:
		default:
			log.error("No local instrument def file and unable to reach remote");
			throw new IllegalStateException("No local instrument def file and unable to reach remote");
		
		case NO_LOCAL_HAS_REMOTE:
			log.warn("No local insttrument def file, pulling from remote");
		case NEED_UPDATE:
			log.debug("Populating local instrument def file from remote");
			populateLocalInstDef(resourceRoot);
		case HAS_LOCAL_NO_REMOTE:
		case GOOD:
			
			if(dbStatus == DB_STATUS.GOOD) {
				log.debug("");
				map = new LocalInstrumentDBMap(dbFolder);
			} else {
				map = new LocalInstrumentDBMap(dbFolder, getLocalInstDef(resourceFolder));
			}
			
			break;
		
		}
		
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(final String key) {
		return map.containsKey(key);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public InstrumentDefinition getValue(final String key) {
		return map.getValue(key);
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void put(final String key, final InstrumentDefinition value) {
		map.put(key, value);
	}
	
	/**
	 * 
	 * @return
	 */
	public int size() {
		return map.size();
	}
	
	static String getLatestS3InstDefVersion() {
		
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
	static String getLocalInstDefVersion(final File resourceFolder) {
		
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
	
	File getLocalInstDef(final File resourceFolder) {
		
		File version = null;
		final File[] files = resourceFolder.listFiles(new ZipFilter());
		final List<File> versions = new ArrayList<File>();
		for(final File file : files) {
			
			try {
				
				TIMESTAMP_FORMATTER.parseDateTime(file.getName().split("\\.")[0]);
				versions.add(file);
				
			} catch (Exception e) {
				// ignore failed parses
			}
			
		}
		
		if(!versions.isEmpty()) {
			Collections.sort(versions);
			version = versions.get(versions.size() - 1);
		}
		
		return version;
		
	}
	
	static void populateLocalInstDef(final String resourceRoot) throws IOException {
		
		final URL instDefURL = new URL(URL);
		URLConnection conn = instDefURL.openConnection();
       
		InputStream in = conn.getInputStream();
        
		final String latestVersionName = getLatestS3InstDefVersion();
        File outFile = new File(resourceRoot + "/" + latestVersionName + ".zip");
        FileOutputStream out = new FileOutputStream(outFile);
        
        byte[] b = new byte[1024];
        int count;
        while ((count = in.read(b)) >= 0) {
            out.write(b, 0, count);
        }
        out.flush(); out.close(); in.close();
        
	}
	
	static boolean hasDB(final File dbFolder) {
		
		for(final String file : dbFolder.list()) {
			if(file.toLowerCase().endsWith(".jdb")) {
				return true;
			}
		}
		
		return false;
	}
	
	private File getDBFolder(final File resourceFolder) {
		
		final File db = new File(resourceFolder, DB_DIR);
		
		if(!db.exists()) {
			db.mkdir();
		}
		
		return db;
	}
	
	private static class ZipFilter implements FileFilter {

		@Override
		public boolean accept(final File pathname) {
			return pathname.getName().toLowerCase().endsWith(".zip");
		}
		
	}
	
	private enum DB_STATUS {
		
		NONE, GOOD;
		
		public static DB_STATUS getStatus(final File dbFolder) {
			
			if(hasDB(dbFolder)) {
				return GOOD;
			}
			
			return NONE;
		}
		
	}
	
	private enum INST_DEF_STATUS {
		
		NONE, 
		NO_LOCAL_HAS_REMOTE, 
		HAS_LOCAL_NO_REMOTE, 
		NEED_UPDATE, GOOD;
		
		public static INST_DEF_STATUS getStatus(final File resourceFolder) {
			
			final String localInstDefVersion = getLocalInstDefVersion(resourceFolder);
			final String latestRemoteInstDefVersion = getLatestS3InstDefVersion();
			
			if(localInstDefVersion == null && latestRemoteInstDefVersion == null) {
				return NONE;
			}
			
			if(localInstDefVersion == null) {
				return NO_LOCAL_HAS_REMOTE;
			}
			
			if(latestRemoteInstDefVersion == null) {
				return HAS_LOCAL_NO_REMOTE;
			}
			
			if(localInstDefVersion.equals(latestRemoteInstDefVersion)) {
				return GOOD;
			} else {
				return NEED_UPDATE;
			}
			
		}
		
	}
	
}
