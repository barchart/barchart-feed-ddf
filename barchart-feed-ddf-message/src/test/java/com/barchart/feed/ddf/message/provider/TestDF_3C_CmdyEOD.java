/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/**
 *
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.util.common.ascii.ASCII.ASCII_CHARSET;

import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * @author g-litchfield
 *
 */
public class TestDF_3C_CmdyEOD extends TestDDFBase {

	// 3C
	final static byte[] msg3C =
			"3CLN2,CCJ>>,01/01/2013,105365,105802,105010,105758JFTKDw9"
					.getBytes(ASCII_CHARSET);

	@Test
	public void testDecode() {
		final DF_3C_CmdyEOD msg = new DF_3C_CmdyEOD();

		final ByteBuffer buffer = ByteBuffer.wrap(msg3C);

		msg.decodeDDF(buffer);

		System.out.println(msg.toString());

	}

}
