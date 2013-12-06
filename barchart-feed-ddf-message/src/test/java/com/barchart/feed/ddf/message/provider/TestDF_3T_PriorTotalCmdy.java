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
public class TestDF_3T_PriorTotalCmdy extends TestDDFBase {

	// 3T
	final static byte[] msg3T = "3CL,CCJ10,2160838,2323862,SGJFTKDw9"
			.getBytes(ASCII_CHARSET);

	@Test
	public void testDecode() {
		final DF_3T_PriorTotalCmdy msg = new DF_3T_PriorTotalCmdy();

		final ByteBuffer buffer = ByteBuffer.wrap(msg3T);

		msg.decodeDDF(buffer);

		System.out.println(msg.toString());
	}

}
