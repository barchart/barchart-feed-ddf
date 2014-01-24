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
public class TestDF_3S_StockEOD extends TestDDFBase {

	// 3S
	final static byte[] msg3S_A =
			"3GOOG,SAM>>,01/01/2013,12112,12285,12094,12278,5967600JFTKDw9"
					.getBytes(ASCII_CHARSET);

	final static byte[] msg3S_B =
			"3VAP.AX,SBv>>,12/03/2012,54900,55490,54900,55360,1868"
					.getBytes(ASCII_CHARSET);

	@Test
	public void testDecode() {

		final DF_3S_StockEOD msg = new DF_3S_StockEOD();

		final ByteBuffer buffer = ByteBuffer.wrap(msg3S_A);

		msg.decodeDDF(buffer);

//		buffer = ByteBuffer.wrap(msg3S_B);
//
//		msg.decodeDDF(buffer);
//
//		System.out.println(msg.toString());

	}

}
