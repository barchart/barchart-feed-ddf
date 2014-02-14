/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.barchart.feed.api.model.meta.Instrument;

/**
 * The Class TestDDF_InstrumentProvider.
 */
public class TestInstrumentProvider {

	/**
	 * Test1.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void test1() throws Exception {

		final Collection<String> symbolList = new ArrayList<String>();

		symbolList.add("IBM");
		symbolList.add("ORCL");
		symbolList.add("MSFT");
		symbolList.add("ESH3");
		symbolList.add("RJH3");
		symbolList.add("YGH3");
		symbolList.add("_S_FX_A6H2_A6Z1");
		symbolList.add("_S_BF_ZSQ2_ZSU2_ZSX2");

		final Map<String, Instrument> list = DDF_InstrumentProvider
				.fromSymbols(symbolList);

		assertEquals(8, list.size());

		for (final Entry<? extends CharSequence, Instrument> e : list.entrySet()) {

			if(e.getValue() == null) {
				System.out.println(" = null");
				continue;
			} else if(e.getValue().isNull()) {
				System.out.println("isNull");
				continue;
			} else {
				System.out.println(e.getValue().toString());
			}
			
			final Instrument inst = e.getValue();
//			final TimeInterval lifetime = inst.lifetime();
//			final TimeValue expires = e.getValue().lifetime().stop();
//			System.out.println("EXP Month Year DDF = "
//					+ (expires.asDateTime().getMonthOfYear())
//					+ " "
//					+ expires.asDateTime().getYear());
//
			System.out.println(e.getValue());

		}

	}
	
	@Test
	public void testHashmap() throws Exception {
		
		final Set<Instrument> insts = new HashSet<Instrument>();
		
		final Instrument i = DDF_InstrumentProvider.fromSymbol("ESU13");
		
		insts.add(i);
		
		assertTrue(insts.contains(i));
		
		final Instrument i2 = DDF_InstrumentProvider.fromSymbol("ESU13");
		
		assertTrue(insts.contains(i2));
		
	}

}
