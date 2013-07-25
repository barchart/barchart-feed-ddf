package com.barchart.feed.ddf.instrument.provider;

import java.io.File;

public class TestInstDB {

	public static void main(final String[] args) throws Exception {
		
		final File instDefZip = new File("/home/gavin/logs/instrumentDef.zip");
		final File dbFolder = new File("/home/gavin/logs/");
		
		final InstrumentDatabaseMap dbMap = new InstrumentDatabaseMap(dbFolder, instDefZip);
		
		DDF_InstrumentProvider.bindDatabaseMap(dbMap);
		
		for(final String symbol : dbMap.keySet()) {
			System.out.println(symbol);
		}
		
	}
	
}
