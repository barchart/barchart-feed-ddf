package com.barchart.feed.ddf.instrument.provider.ext;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.openfeed.proto.inst.InstrumentDefinition;

import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;

public class TestNewInstrumentProvider {
	
//	public static void main(final String[] args) throws Exception {
//		
//		final Callable<InstrumentDefinition> remote1 = 
//				NewInstrumentProvider.remoteCallable("ESU3");
//		
//		final InstrumentDefinition def1 = remote1.call();
//		
//		System.out.println(def1.toString());
//		
//	}

	@Test
	public void testSymbolFormat() {
		
		/* Futures */
		assertTrue(DDF_InstrumentProvider.formatSymbol("MGU3").equals("MGU2013"));
		
		/* Spreads */
		
		
		/* Options */
		assertTrue(DDF_InstrumentProvider.formatSymbol("A6Z885C").equals("A6Z2013|885C"));
		assertTrue(DDF_InstrumentProvider.formatSymbol("A6U850P").equals("A6U2013|850P"));
		assertTrue(DDF_InstrumentProvider.formatSymbol("EWH2550Q").equals("EWH2014|2550P"));
		assertTrue(DDF_InstrumentProvider.formatSymbol("EWH3950D").equals("EWH2014|3950C"));
	}
	
}
