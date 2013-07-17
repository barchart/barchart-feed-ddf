package com.barchart.feed.ddf.client.provider;

import java.io.File;
import java.io.IOException;

import com.barchart.feed.ddf.instrument.provider.InstrumentDBProvider;
import com.barchart.feed.ddf.instrument.provider.InstrumentDatabaseMap;

public class LookupSymbolsInLocalDB {

	
	
	public static void main(final String[] args) {
		
	
		InstrumentDatabaseMap dbMap = InstrumentDBProvider.getMap(getTempFolder());
		
		System.out.println(dbMap.get("LEQ0|1100C").toString());
		System.out.println(dbMap.get("ESV1450P").toString());
		
	}
	
	
	/*
	 * Returns the default temp folder
	 */
	private static File getTempFolder() {
		
		try {
			
			return File.createTempFile("temp", null).getParentFile();
			
		} catch (IOException e) {
			throw new RuntimeException("Java, I dont give a shit right now");
		}
		
	}
	
}
