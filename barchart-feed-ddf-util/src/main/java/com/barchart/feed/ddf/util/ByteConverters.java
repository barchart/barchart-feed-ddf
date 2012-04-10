/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ByteConverters.
 */
public final class ByteConverters {

	private static final Logger log = LoggerFactory
			.getLogger(ByteConverters.class);

	/*
	 * Convert an int to a byte source of size <i>len</i>, big-endian order
	 */
	/**
	 * Int to bytes.
	 *
	 * @param i the i
	 * @param len the len
	 * @return the byte[]
	 */
	public static byte[] intToBytes(int i, int len) {
		byte[] b = new byte[len];
		for (int j = 0; j < len; ++j)
			b[j] = (byte) ((i << (((4 - len) + j) * 8)) >> 24);
		return b;
	}

	/*
	 * Convert a byte source of size <i>len</i> to an int, big-endian order
	 */
	/**
	 * Bytes to int.
	 *
	 * @param b the b
	 * @param offset the offset
	 * @param len the len
	 * @return the int
	 */
	public static int bytesToInt(byte[] b, int offset, int len) {
		int value = 0;
		for (int j = 0; j < len; ++j)
			value += (int) (b[j + offset] & 0x000000FF) << ((3 - (j + (4 - len))) * 8);
		return value;
	}

	/*
	 * Convert a long to a byte source of size <i>len</i>, big-endian order
	 */
	/**
	 * Long to bytes.
	 *
	 * @param i the i
	 * @param len the len
	 * @return the byte[]
	 */
	public static byte[] longToBytes(long i, int len) {
		byte[] b = new byte[len];
		for (int j = 0; j < len; ++j)
			b[j] = (byte) ((i << (((8 - len) + j) * 8)) >> 56);
		return b;
	}

	/*
	 * Convert a byte source of size <i>len</i> to a long, big-endian order
	 */
	/**
	 * Bytes to long.
	 *
	 * @param b the b
	 * @param offset the offset
	 * @param len the len
	 * @return the long
	 */
	public static long bytesToLong(byte[] b, int offset, int len) {
		long value = 0;
		for (int j = 0; j < len; ++j)
			value += (long) (b[j + offset] & 0x000000FF) << ((7 - (j + (8 - len))) * 8);
		return value;
	}

	/*
	 * Convert a byte source to a UTF-8 string
	 */
	/**
	 * Bytes to unicode.
	 *
	 * @param b the b
	 * @param offset the offset
	 * @param len the len
	 * @return the string
	 */
	public static String bytesToUnicode(byte[] b, int offset, int len) {
		if (len == 0)
			return null;
		try {
			return new String(b, offset, len, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.warn("No unicode support!", e);
			return new String(b, offset, len);
		}
	}

	/*
	 * Convert a UTF-8 string to a byte source
	 */
	/**
	 * Unicode to bytes.
	 *
	 * @param s the s
	 * @return the byte[]
	 */
	public static byte[] unicodeToBytes(String s) {
		if (s == null)
			return new byte[] {};
		try {
			return s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.warn("No unicode support!", e);
			return s.getBytes();
		}
	}

	/**
	 * Concatenate an source of byte arrays into a single byte source.
	 * 
	 * @param ba
	 *            An source of byte arrays
	 * @return A single concatenated byte source
	 */
	public static byte[] concatBytes(byte[][] ba) {
		if (ba.length == 0)
			return new byte[] {};
		int totalLength = 0;
		for (byte[] b : ba)
			totalLength += b.length;
		byte[] result = Arrays.copyOf(ba[0], totalLength);
		int offset = ba[0].length;
		for (int i = 1; i < ba.length; ++i) {
			System.arraycopy(ba[i], 0, result, offset, ba[i].length);
			offset += ba[i].length;
		}
		return result;
	}

	/**
	 * Unsigned byte to int.
	 *
	 * @param value the value
	 * @return the int
	 */
	public final static int unsignedByteToInt(final byte value) {
		return value & 0xFF;
	}

	/**
	 * Char from bytes.
	 *
	 * @param hi the hi
	 * @param lo the lo
	 * @return the char
	 */
	public static final char charFromBytes(final byte hi, final byte lo) {
		return (char) (((hi & 0xFF) << 8) | (lo & 0xFF));
	}

	/**
	 * from int value = 0xFAEBDCCD;
	 * 
	 * into byte[] array = { 0xFA, 0xEB, 0xDC, 0xCD }; (at index: 0, 1, 2, 3).
	 *
	 * @param value the value
	 * @return the byte[]
	 */
	public static final byte[] byteArrayFromInt(final int value) {
		return new byte[] {
		//
				(byte) (value >>> 24), // 
				(byte) (value >>> 16), // 
				(byte) (value >>> 8), //
				(byte) value };
	}

}
