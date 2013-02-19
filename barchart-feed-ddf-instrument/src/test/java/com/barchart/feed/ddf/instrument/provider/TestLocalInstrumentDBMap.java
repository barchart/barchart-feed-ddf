package com.barchart.feed.ddf.instrument.provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.barchart.proto.buf.inst.InstrumentDefinition;

public class TestLocalInstrumentDBMap {

	private static final String SYMBOL1 = "symbol1";
	private static final long ID1 = 1000;
	
	private static final String RESOURCE_PATH = "src/test/resources";
	private static final String INST_DEF = "/instrumentDef.zip";
	
	public static void main(final String[] args) throws ZipException, IOException {
		
		final File dbFile = new File(RESOURCE_PATH + "/bdb");
		final File instDefFile = new File(RESOURCE_PATH + INST_DEF);
		final InputStream instDefStream = getInstDefZipStream(instDefFile);
		
		final LocalInstrumentDBMap dbMap = new LocalInstrumentDBMap(dbFile, instDefStream);
		
//		dbMap.put(SYMBOL1, buildDef(SYMBOL1, ID1));
//		
//		InstrumentDefinition def = dbMap.getValue(SYMBOL1);
//		
//		System.out.println(def.toString());
//		
	}
	
	private static InstrumentDefinition buildDef(final String symbol, final long id) {
		
		InstrumentDefinition.Builder builder = InstrumentDefinition.newBuilder();
		
		builder.setSymbol(symbol);
		builder.setMarketId(id);
		
		return builder.build();
	}

	private static InputStream getInstDefZipStream(final File instDef) throws ZipException, IOException {
		
		final ZipFile zFile = new ZipFile(instDef);
		final ZipEntry entry = zFile.entries().nextElement();
		final InputStream zinStream = zFile.getInputStream(entry);
		
		return zinStream;
	}
	
}
