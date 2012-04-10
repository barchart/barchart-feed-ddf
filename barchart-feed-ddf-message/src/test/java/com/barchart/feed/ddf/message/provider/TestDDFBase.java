/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.Arrays;

abstract class TestDDFBase {

	/**
	 * Test encode decode.
	 *
	 * @param msg the msg
	 * @param arraySource the array source
	 */
	public void testEncodeDecode(final Base msg, final byte[] arraySource) {

		final ByteBuffer source = ByteBuffer.wrap(arraySource);

		msg.decodeDDF(source);

		final ByteBuffer target = ByteBuffer.allocate(arraySource.length);

		msg.encodeDDF(target);

		final byte[] arrayTarget = target.array();

		System.out.println("source=" + new String(arraySource));
		System.out.println("target=" + new String(arrayTarget));

		assertTrue(Arrays.equals(arraySource, arrayTarget));

	}

}
