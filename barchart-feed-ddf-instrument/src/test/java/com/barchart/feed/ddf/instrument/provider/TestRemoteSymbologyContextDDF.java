package com.barchart.feed.ddf.instrument.provider;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.Test;

import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.api.inst.InstrumentGUID;
import com.barchart.feed.ddf.symbol.api.DDF_Symbol;
import com.barchart.feed.ddf.symbol.provider.DDF_SymbolService;

public class TestRemoteSymbologyContextDDF {

	public static final String INST_1 = "GOOG";
	public static final InstrumentGUID GUID_1 = new InstrumentGUID("1261904");
	
	public static final String INST_2 = "A6H2012";
	public static final InstrumentGUID GUID_2 = new InstrumentGUID("1004331");
	
	public static final String FAIL_1 = "XXXGGG";
	
	@Test
	public void testRemoteLookup() {
		
		final ConcurrentMap<InstrumentGUID, Instrument> guidMap = 
				new ConcurrentHashMap<InstrumentGUID, Instrument>();
		
		final RemoteSymbologyContextDDF ctx = new RemoteSymbologyContextDDF(guidMap);
		
		final InstrumentGUID guid1 = ctx.lookup(INST_1);
		
		assertTrue(guid1.equals(GUID_1));
		assertTrue(guidMap.containsKey(guid1));
		assertTrue(ctx.symbolMap.containsKey(INST_1));
		
		final InstrumentGUID guid2 = ctx.lookup(INST_2);
		
		assertTrue(guid2.equals(GUID_2));
		assertTrue(guidMap.containsKey(guid2));
		assertTrue(ctx.symbolMap.containsKey(INST_2));
		
		final InstrumentGUID guidFail = ctx.lookup(FAIL_1);
		
		assertTrue(guidFail.equals(InstrumentGUID.NULL_INSTRUMENT_GUID));
		assertTrue(ctx.failedMap.containsKey(FAIL_1));
		
	}
	
	@Test
	public void testBatchRemoteLookup() {
		
		final ConcurrentMap<InstrumentGUID, Instrument> guidMap = 
				new ConcurrentHashMap<InstrumentGUID, Instrument>();
		
		final RemoteSymbologyContextDDF ctx = new RemoteSymbologyContextDDF(guidMap);
		final List<CharSequence> symbols = new ArrayList<CharSequence>();
		
		symbols.add(INST_1);
		symbols.add(INST_2);
		symbols.add(FAIL_1);
		
		final Map<CharSequence, InstrumentGUID> symbolMap = ctx.lookup(symbols);
		
		assertTrue(symbolMap.containsKey(INST_1));
		assertTrue(symbolMap.containsKey(INST_2));
		assertTrue(symbolMap.containsKey(FAIL_1));
		
		assertTrue(symbolMap.get(FAIL_1).equals(InstrumentGUID.NULL_INSTRUMENT_GUID));
		
		DDF_Symbol symbol = DDF_SymbolService.decode("ESH3");
		System.out.println(symbol.toString());
		
	}
	
}
