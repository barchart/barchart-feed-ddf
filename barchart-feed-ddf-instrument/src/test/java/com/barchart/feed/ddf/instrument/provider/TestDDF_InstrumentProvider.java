/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.util.values.api.TimeInterval;
import com.barchart.util.values.api.TimeValue;

/**
 * The Class TestDDF_InstrumentProvider.
 */
public class TestDDF_InstrumentProvider {

	/**
	 * Test1.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void test1() throws Exception {

		final Collection<CharSequence> symbolList = new ArrayList<CharSequence>();

		symbolList.add("IBM");
		symbolList.add("ORCL");
		symbolList.add("MSFT");
		symbolList.add("ESH3");
		symbolList.add("RJH3");
		symbolList.add("YGH3");
		symbolList.add("_S_FX_A6H2_A6Z1");
		symbolList.add("_S_BF_ZSQ2_ZSU2_ZSX2");

		final Map<? extends CharSequence, Instrument> list = DDF_InstrumentProvider
				.find(symbolList);

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
//			System.out.println(e.getValue());

		}

	}

}
