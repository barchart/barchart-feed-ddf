package com.barchart.feed.ddf.instrument.provider;

import java.io.File;

import com.barchart.proto.buf.inst.InstrumentDefinition;

public class TestLocalInstrumentDBMap {

	private static final String SYMBOL1 = "symbol1";
	private static final long ID1 = 1000;
	
	public static void main(final String[] args) {
		
		final File dbFile = new File("src/test/resources/bdb");
		
		final LocalInstrumentDBMap dbMap = new LocalInstrumentDBMap(dbFile);
		
		dbMap.put(SYMBOL1, buildDef(SYMBOL1, ID1));
		
		InstrumentDefinition def = dbMap.getValue(SYMBOL1);
		
		System.out.println(def.toString());
		
	}
	
	private static InstrumentDefinition buildDef(final String symbol, final long id) {
		
		InstrumentDefinition.Builder builder = InstrumentDefinition.newBuilder();
		
		builder.setSymbol(symbol);
		builder.setMarketId(id);
		
		return builder.build();
	}
	
}
