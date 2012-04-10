/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.enums;

import static org.junit.Assert.*;

import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class TestDDF_QueryType.
 */
public class TestDDF_QueryType {

	/**
	 * Test from code.
	 */
	@Test
	public void testFromCode() {

		for (final DDF_QueryType<?> type : DDF_QueryType.values()) {

			System.out.println("type : " + type);

		}

		// DDF_QueryType<?> type = DDF_QueryType.fromCode("ticks");

		assertEquals(DDF_QueryType.TICKS, DDF_QueryType.fromCode("ticks"));

		assertEquals(DDF_QueryType.MINUTES, DDF_QueryType.fromCode("mins"));

		assertTrue(true);

	}

}
