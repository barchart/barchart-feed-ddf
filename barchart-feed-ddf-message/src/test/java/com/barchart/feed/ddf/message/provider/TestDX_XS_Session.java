/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.util.ascii.ASCII.*;

import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestDX_XS_Session extends TestDDFBase {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEncodeEmpty() {

		final DX_XS_Session msg = new DX_XS_Session();

		final ByteBuffer buffer = ByteBuffer.allocate(96);

		msg.encodeXML(buffer);

		System.out.println("place=" + buffer.position());
		System.out.println("limit=" + buffer.limit());
		System.out.println("" + new String(buffer.array()));

		final String result = "<QUOTE basecode=\"?\" ddfexchange=\"?\" exchange=\"NONE\" name=\"NONE\" pointvalue=\"0.0\" symbol=\"NONE\"/>";

		final byte[] arraySource = buffer.array();
		final byte[] arrayTarget = result.getBytes(ASCII_CHARSET);
		// assertTrue(Arrays.equals(arraySource, arrayTarget));

	}

}
