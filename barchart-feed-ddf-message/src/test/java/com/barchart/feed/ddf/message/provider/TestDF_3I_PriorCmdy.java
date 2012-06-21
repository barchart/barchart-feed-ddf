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
public class TestDF_3I_PriorCmdy extends TestDDFBase {

	// 3I
	final static byte[] msg3I = "3CLN2,CCJ10,2160838,2323862,SGJFTKDw9"
			.getBytes(ASCII_CHARSET);

	@Test
	public void testDecodeSpread() {
		final DF_3I_PriorCmdy msg = new DF_3I_PriorCmdy();

		final ByteBuffer buffer = ByteBuffer.wrap(msg3I);

		msg.decodeDDF(buffer);

		System.out.println(msg.toString());
	}

}
