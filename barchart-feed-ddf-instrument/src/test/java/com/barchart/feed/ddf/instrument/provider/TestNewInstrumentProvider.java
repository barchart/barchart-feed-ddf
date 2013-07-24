package com.barchart.feed.ddf.instrument.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.openfeed.proto.inst.InstrumentDefinition;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;

public class TestNewInstrumentProvider {
	
	public static void main(final String[] args) throws Exception {
		
		final Collection<String> symbolList = new ArrayList<String>();

		symbolList.add("IBM");
		symbolList.add("ORCL");
		symbolList.add("MSFT");
		symbolList.add("ESU3");
		symbolList.add("RJU3");
		symbolList.add("YGU3");
		symbolList.add("_S_FX_A6H2_A6Z1");
		symbolList.add("_S_BF_ZSQ2_ZSU2_ZSX2");

		final Map<String, Instrument> list = DDF_InstrumentProvider
				.fromSymbols(symbolList);

		Thread.sleep(1000);
		
		for (final Entry<String, Instrument> e : list.entrySet()) {

			if(e.getValue() == null) {
				System.out.println(" = null");
				continue;
			} else if(e.getValue().isNull()) {
				System.out.println("isNull");
				continue;
			} else {
				final Instrument inst = e.getValue();
				System.out.println(inst.toString());
			}
			
		}
		
		Thread.sleep(100000);
		
	}

//	@Test
//	public void testSymbolFormat() {
//		
//		/* Futures */
//		assertTrue(DDF_InstrumentProvider.formatSymbol("MGU3").equals("MGU2013"));
//		
//		/* Spreads */
//		
//		
//		/* Options */
//		assertTrue(DDF_InstrumentProvider.formatSymbol("A6Z885C").equals("A6Z2013|885C"));
//		assertTrue(DDF_InstrumentProvider.formatSymbol("A6U850P").equals("A6U2013|850P"));
//		assertTrue(DDF_InstrumentProvider.formatSymbol("EWH2550Q").equals("EWH2014|2550P"));
//		assertTrue(DDF_InstrumentProvider.formatSymbol("EWH3950D").equals("EWH2014|3950C"));
//	}
	
}
