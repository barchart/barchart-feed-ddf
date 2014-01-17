package com.barchart.feed.ddf.historical.provider;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestCodecHelper {

	@Test
	public void testDecodeMantissa() {
		
		assertTrue(CodecHelper.decodeMantissa("10.5", -1) == 105);
		assertTrue(CodecHelper.decodeMantissa("10.05", -2) == 1005);
		assertTrue(CodecHelper.decodeMantissa("-10.5", -1) == -105);
		assertTrue(CodecHelper.decodeMantissa("-10.05", -2) == -1005);
		
	}
	
}
