package com.barchart.feed.ddf.instrument.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.proto.buf.inst.InstrumentDefinition;

public class LocalInstDefDB {
	
	private static final Logger log = LoggerFactory.getLogger(LocalInstDefDB.class);
	
	public static final String INST_DEF = "instrumentDef.zip";
	public static final String URL = "https://s3.amazonaws.com/instrument-def/active/instrumentDef.zip";
	
	public final String resourcePath;
	
	public LocalInstDefDB(final String resourcePath) {
		
		this.resourcePath = resourcePath;
		
		// if inst def zip file does not exist on resource path, pull from S3 bucket,
		// 		then build db
		
		// create database object
		
		// Check S3 bucket for update to db, if needing update, start runner to update in background
		// 
		
		final File dataFolder = new File(resourcePath);
		
		dataFolder.exists();
		dataFolder.isDirectory();
		
		final String[] files = dataFolder.list();
		
		boolean hasInstDef = false;
		for(String file : files) {
			if(file.equals(INST_DEF)) {
				hasInstDef = true;
			}
		}
		
		if(!hasInstDef) { //not working
			log.debug("No instrument def file located, pulling from remote");
			
			// Runner
			try {
				
				final URL instDefURL = new URL(URL);
				URLConnection conn = instDefURL.openConnection();
		       
				InputStream in = conn.getInputStream();
		        
		        File outFile = new File(resourcePath + "/instrumentDef.zip");
		        FileOutputStream out = new FileOutputStream(outFile);
		        
		        byte[] b = new byte[1024];
		        int count;
		        while ((count = in.read(b)) >= 0) {
		            out.write(b, 0, count);
		        }
		        out.flush(); out.close(); in.close();                   
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			log.debug("Local inst def file located");
		}
		
		final File dbFile = new File(resourcePath + "/" + "bdb");
		final LocalInstrumentDBMap dbMap = new LocalInstrumentDBMap(dbFile);
		
		File filez = null;
		
		final File[] fs = dataFolder.listFiles();
		for(File f : fs) {
			if(f.getName().equals(INST_DEF)) {
				filez = f;
			}
		}
		
		if(filez == null) {
			throw new RuntimeException("Failed to get instrument definition file");
		}
		
		try {
			
			final ZipFile zFile = new ZipFile(filez);
			final ZipEntry entry = zFile.entries().nextElement();
			final InputStream zinStream = zFile.getInputStream(entry);
			
			long counter = 0;
			while(true) {
				InstrumentDefinition def;
				
				try {
					def = InstrumentDefinition.
							parseDelimitedFrom(zinStream);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				
				if(def!= null) {
					//Instrument inst = InstrumentProtoBuilder.buildInstrument(def);
					if(def.hasSymbol()) {
						dbMap.put(def.getSymbol(), def);
					}
					
					if(counter % 10000 == 0) {
						System.out.println("Build count " + counter);
					}
					
					counter++;
					
				} else {
					break;
				}
			}
			
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(dbMap.size());
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
