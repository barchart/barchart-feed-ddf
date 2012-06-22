/**
 * 
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.util.ascii.ASCII.ASCII_CHARSET;

import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * @author g-litchfield
 * 
 */
public class TestDF_3S_StockEOD extends TestDDFBase {

	// 3S
	final static byte[] msg3S =
			"3GOOG,SAM10,12112,12285,12094,12278,5967600,SGJFTKDw9"
					.getBytes(ASCII_CHARSET);

	@Test
	public void testDecodeSpread() {

		final DF_3S_StockEOD msg = new DF_3S_StockEOD();

		final ByteBuffer buffer = ByteBuffer.wrap(msg3S);

		msg.decodeDDF(buffer);

		System.out.println(msg.toString());

	}

}
