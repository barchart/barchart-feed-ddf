/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.util;

import static com.barchart.util.ascii.ASCII.ASCII_CHARSET;
import static com.barchart.util.ascii.ASCII.COMMA;
import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.barchart.feed.ddf.util.enums.DDF_Fraction;

public class TestHelperDDF {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDecodeLongString1() {
		final long value = 125986235986125L;
		final String valueString = Long.toString(value) + ',';
		final byte[] array = valueString.getBytes(ASCII_CHARSET);
		final ByteBuffer buffer = ByteBuffer.wrap(array);
		final long result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(value, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testDecodeLongString1e() {
		final long value = 125986235986125L;
		final String valueString = Long.toString(value) + "abc";
		final byte[] array = valueString.getBytes(ASCII_CHARSET);
		final ByteBuffer buffer = ByteBuffer.wrap(array);
		final long result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(value, result);
	}

	@Test
	public void testDecodeLongString2() {
		final long value = -125986235986125L;
		final String valueString = Long.toString(value) + ',';
		final byte[] array = valueString.getBytes(ASCII_CHARSET);
		final ByteBuffer buffer = ByteBuffer.wrap(array);
		final long result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(value, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testDecodeLongString2e() {
		final long value = -125986235986125L;
		final String valueString = Long.toString(value) + "abc";
		final byte[] array = valueString.getBytes(ASCII_CHARSET);
		final ByteBuffer buffer = ByteBuffer.wrap(array);
		final long result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(value, result);
	}

	@Test
	public void testDecodeLongStringBlank() {
		final long value = HelperDDF.DDF_EMPTY;
		final String valueString = ",";
		final byte[] array = valueString.getBytes(ASCII_CHARSET);
		final ByteBuffer buffer = ByteBuffer.wrap(array);
		final long result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(value, result);
	}

	@Test
	public void testDecodeLongStringBlank2() {
		final long value = HelperDDF.DDF_EMPTY;
		final String valueString = ",,";
		final byte[] array = valueString.getBytes(ASCII_CHARSET);
		final ByteBuffer buffer = ByteBuffer.wrap(array);
		long result;
		result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(value, result);
		result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(value, result);
	}

	@Test
	public void testEncodeLongBlank() {
		final long value = HelperDDF.DDF_EMPTY;
		final ByteBuffer buffer = ByteBuffer.allocate(1);
		HelperDDF.longEncode(value, buffer, COMMA);
		final String result = new String(buffer.array());
		assertEquals(",", result);
	}

	@Test
	public void testEncodeLongClear() {
		final long value = HelperDDF.DDF_CLEAR;
		final ByteBuffer buffer = ByteBuffer.allocate(2);
		HelperDDF.longEncode(value, buffer, COMMA);
		final String result = new String(buffer.array());
		assertEquals("-,", result);
	}

	@Test
	public void testDecodeLongStringClear() {
		final long value = HelperDDF.DDF_CLEAR;
		final String valueString = "-,";
		final byte[] array = valueString.getBytes(ASCII_CHARSET);
		final ByteBuffer buffer = ByteBuffer.wrap(array);
		final long result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(value, result);
	}

	@Test
	public void testDecodeLongStringClear2() {
		final long clear = HelperDDF.DDF_CLEAR;
		final String valueString = "-,-,";
		final byte[] array = valueString.getBytes(ASCII_CHARSET);
		final ByteBuffer buffer = ByteBuffer.wrap(array);
		long result;
		result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(clear, result);
		result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(clear, result);
	}

	@Test
	public void testDecodeLongStringClearBlank() {
		final long clear = HelperDDF.DDF_CLEAR;
		final long blank = HelperDDF.DDF_EMPTY;
		final String valueString = ",-,,-,";
		final byte[] array = valueString.getBytes(ASCII_CHARSET);
		final ByteBuffer buffer = ByteBuffer.wrap(array);
		long result;
		result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(blank, result);
		result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(clear, result);
		result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(blank, result);
		result = HelperDDF.longDecode(buffer, COMMA);
		assertEquals(clear, result);
	}

	@Test
	public void testDecodeBinaryToDecimal() {

		long value;
		com.barchart.feed.ddf.util.enums.DDF_Fraction frac;
		long result;

		// 10-1/4
		value = 101;
		frac = DDF_Fraction.Q4;
		result = HelperDDF.fromBinaryToDecimal(value, frac);
		// System.out.println(" value=" + value + " exp2=" + frac.nativeExponent
		// + " result=" + result + " exp10=" + frac.decimalExponent);
		assertEquals(1025, result);

		// (-)10-1/4
		value = -101;
		frac = DDF_Fraction.Q4;
		result = HelperDDF.fromBinaryToDecimal(value, frac);
		// System.out.println(" value=" + value + " exp2=" + frac.nativeExponent
		// + " result=" + result + " exp10=" + frac.decimalExponent);
		assertEquals(-1025, result);

		// 10-11/32
		value = 1011;
		frac = DDF_Fraction.Q32;
		result = HelperDDF.fromBinaryToDecimal(value, frac);
		// System.out.println(" value=" + value + " exp2=" + frac.nativeExponent
		// + " result=" + result + " exp10=" + frac.decimalExponent);
		assertEquals(1034375, result);

		// (-)10-11/32
		value = -1011;
		frac = DDF_Fraction.Q32;
		result = HelperDDF.fromBinaryToDecimal(value, frac);
		// System.out.println(" value=" + value + " exp2=" + frac.nativeExponent
		// + " result=" + result + " exp10=" + frac.decimalExponent);
		assertEquals(-1034375, result);

		// 12342-111/256
		value = 12342111;
		frac = DDF_Fraction.Q256;
		result = HelperDDF.fromBinaryToDecimal(value, frac);
		// System.out.println(" value=" + value + " exp2=" + frac.nativeExponent
		// + " result=" + result + " exp10=" + frac.decimalExponent);
		assertEquals(1234243359375L, result);

	}

	@Test
	public void testEncodeDecimalToBinary() {

		long value;
		DDF_Fraction frac;
		long result;

		// 10-1/4
		value = 1025;
		frac = DDF_Fraction.Q4;
		result = HelperDDF.fromDecimalToBinary(value, frac);
		// System.out.println(" value=" + value + " exp2=" + frac.nativeExponent
		// + " result=" + result + " exp10=" + frac.decimalExponent);
		assertEquals(101, result);

		// (-)10-1/4
		value = -1025;
		frac = DDF_Fraction.Q4;
		result = HelperDDF.fromDecimalToBinary(value, frac);
		// System.out.println(" value=" + value + " exp2=" + frac.nativeExponent
		// + " result=" + result + " exp10=" + frac.decimalExponent);
		assertEquals(-101, result);

		// 10-11/32
		value = 1034375;
		frac = DDF_Fraction.Q32;
		result = HelperDDF.fromDecimalToBinary(value, frac);
		// System.out.println(" value=" + value + " exp2=" + frac.nativeExponent
		// + " result=" + result + " exp10=" + frac.decimalExponent);
		assertEquals(1011, result);

		// (-)10-11/32
		value = -1034375;
		frac = DDF_Fraction.Q32;
		result = HelperDDF.fromDecimalToBinary(value, frac);
		// System.out.println(" value=" + value + " exp2=" + frac.nativeExponent
		// + " result=" + result + " exp10=" + frac.decimalExponent);
		assertEquals(-1011, result);

		// 12342-111/256
		value = 1234243359375L;
		frac = DDF_Fraction.Q256;
		result = HelperDDF.fromDecimalToBinary(value, frac);
		// System.out.println(" value=" + value + " exp2=" + frac.nativeExponent
		// + " result=" + result + " exp10=" + frac.decimalExponent);
		assertEquals(12342111, result);

	}

}
