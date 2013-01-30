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

import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField;

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
		symbolList.add("ESZ2");
		symbolList.add("RJZ2");
		symbolList.add("YGZ2");
		symbolList.add("_S_FX_A6H2_A6Z1");
		symbolList.add("_S_BF_ZSQ2_ZSU2_ZSX2");

		final Map<? extends CharSequence, DDF_Instrument> list = DDF_InstrumentProvider
				.findDDF(symbolList);

		assertEquals(8, list.size());

		for (final Entry<? extends CharSequence, DDF_Instrument> e : list.entrySet()) {

			System.out.println("EXP Month Year DDF = "
					+ e.getValue().get(DDF_InstrumentField.DDF_EXPIRE_MONTH)
							.toString()
					+ " "
					+ e.getValue().get(DDF_InstrumentField.DDF_EXPIRE_YEAR)
							.toString());

			System.out.println(e.getValue());

		}

	}

}
