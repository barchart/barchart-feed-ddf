package com.barchart.feed.ddf.instrument.provider;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openfeed.proto.inst.InstrumentDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class InstrumentDBProvider {

	private static final Logger log = LoggerFactory.getLogger(InstrumentDBProvider.class);
	
	private static final String DB_DIR = "persist";
	
	private static final String S3_URL = "https://s3.amazonaws.com/instrument-def/";
	private static final String S3_PATH = "active/instrumentDef.zip";
	
	public static final DateTimeFormatter TIMESTAMP_FORMATTER = 
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH-mm-ss");
	
	private InstrumentDBProvider() {
		
	}
	
	public static InstrumentDatabaseMap getMap(final File resourceFolder) {
		
		validateResourceFolder(resourceFolder);
		
		return new InstrumentDatabaseMap(getDBFolder(resourceFolder));
		
	}
	
	public static Callable<Boolean> updateDBMap(final File resourceFolder,
			final InstrumentDatabaseMap map) {
		
		validateResourceFolder(resourceFolder);
		
		if(map == null) {
			throw new IllegalArgumentException("Map is null");
		}
		
		return new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				
				synchronized(map) {
				
					boolean didUpdate = new UpdateInstrumentDefinitions(
							resourceFolder).call();
					
					if(!didUpdate) {
						return true;
					}
					
					boolean populateBoolean = new PopulateDatabase(
							resourceFolder, map).call();
					
					return populateBoolean;
				
				}
			}
			
		};
		
	}
	
	public static Callable<Boolean> updateHistoricalDBMap(final File resourceFolder,
			final InstrumentDatabaseMap map, final DateTime date) {
		
		//TODO
		
		return null;
	}
	
	public static File getLocalInstDef(final File resourceFolder) {
		
		validateResourceFolder(resourceFolder);
		
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
	
	public static File getDBFolder(final File resourceFolder) {

		validateResourceFolder(resourceFolder);
		
		final File db = new File(resourceFolder, DB_DIR);
		
		if(!db.exists()) {
			db.mkdir();
		}
		
		return db;
		
	}
	
	private static void validateResourceFolder(final File resourceFolder) {
		
		if(resourceFolder == null) {
			throw new IllegalArgumentException("Resource folder is null");
		}
		
		if(!resourceFolder.exists()) {
			throw new IllegalArgumentException("Resource folder does not exist");
		}
		
		if(!resourceFolder.isDirectory()) {
			throw new IllegalArgumentException("Resource folder must be a directory");
		}
		
		if(!resourceFolder.canWrite()) {
			throw new IllegalStateException("Resource folder is locked by another aplication");
		}
		
	}
	
	private static String getLatestS3InstDefVersion() {
		
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
	
	/* ***** ***** Begin private classes ***** ***** */
	
	private static class UpdateInstrumentDefinitions implements Callable<Boolean> {

		final File resourceFolder;
		
		public UpdateInstrumentDefinitions(final File resourceFolder) {
			this.resourceFolder = resourceFolder;
		}
		
		@Override
		public Boolean call() throws Exception {
			
			final File instDef = getLocalInstDef(resourceFolder);
			
			final String remoteVersion = getLatestS3InstDefVersion();
			
			if(remoteVersion == null) {
				
				if(instDef == null) {
					throw new IllegalStateException("No local instrument definitions " +
							"and unable to reach remote resource");
				} else {
					log.debug("Unable to reach remote, using local instrument definition file");
					return false;
				}
				
			}
			
			if(instDef == null) {
				log.debug("No local instrument def file found, updating from remote");
			} else {
				log.debug("Local instrument def file version {}", instDef.getName().split("\\.")[0]);
				log.debug("Remote instrument def file version {}", remoteVersion);
			}
			
			/* If local version is different, create new from remote */
			if(instDef == null || !instDef.getName().split("\\.")[0].equals(remoteVersion)) {
				
				// Purge old def files
				
				log.debug("Begin update of instrument definitions");
				
				final URL instDefURL = new URL(S3_URL + S3_PATH);
				URLConnection conn = instDefURL.openConnection();
		       
				InputStream in = conn.getInputStream();
				
				File outFile = new File(resourceFolder.getPath() + "/" + 
						remoteVersion + ".zip");
				
				/*
				 * Client exception here
				 */
			    FileOutputStream out = new FileOutputStream(outFile);
			        
			    byte[] b = new byte[1024];
			    int count;
			    while ((count = in.read(b)) >= 0) {
			    	out.write(b, 0, count);
			    }
			    out.flush(); out.close(); in.close();
			    
			    log.debug("Finished updating instrument definitions");
				
			    return true;
			}
			
			log.debug("Local instrument def file was up to date, skipping DB build");
			return false;
		}
		
	};
	
	public static class PopulateDatabase implements Callable<Boolean> {

		private final File resourceFolder;
		private final InstrumentDatabaseMap map;
		
		public PopulateDatabase(final File resourceFolder,
			final InstrumentDatabaseMap map) {
			this.resourceFolder = resourceFolder;
			this.map = map;
		}
		
		@Override
		public Boolean call() throws Exception {
			
			log.debug("Begin populating DB");
			
			InputStream inStream = null;
			
			try {
				
				final File instDef = getLocalInstDef(resourceFolder);
				
				if(instDef == null) {
					throw new IllegalStateException(
							"Unable to find instrument definition in " + 
							"resource folder");
				}
				
				final ZipFile zFile= new ZipFile(instDef);
				final ZipEntry entry = zFile.entries().nextElement();
				inStream = zFile.getInputStream(entry);
				
				map.clear();
				
				long counter = 0;
				InstrumentDefinition def = null;
				while(true) {
					def = null;
					
					try {
						def = InstrumentDefinition.
								parseDelimitedFrom(inStream);
					} catch (final Exception e) {
						break;
					}
					
					if(def!= null) {
						
						if(def.hasSymbol()) {
							map.put(def.getSymbol(), def);
						}
						
						if(counter % 100000 == 0) {
							log.debug("Instrument DB build count " + counter);
						}
						
						counter++;
						
					} else {
						break;
					}
				}
				
				return true;
			
			} finally {
				
				log.debug("Finished populating DB");
				
				if(inStream != null) {
					inStream.close();
				}
			}
		}
		
	};
	
	private static class ZipFilter implements FileFilter {

		@Override
		public boolean accept(final File pathname) {
			return pathname.getName().toLowerCase().endsWith(".zip");
		}
		
	}
	
}
