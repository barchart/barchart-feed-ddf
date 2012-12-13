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

/**
 * 
 *
 */
public class DDF_SpreadParser {

	public static byte[] stripSpreadPreamble(final byte[] ba) {
		// _SCLN9,8_AJ10SP2CLV9,-232,-,,,GG_
		// 0123456789a123456789b123456789d123456789e

		int pos = getIndexOf(ba, ',', 0);

		final String symbol = parseStringValue(ba, 2, pos - 2);
		final char subrecord = (char) ba[pos + 1];

		final int spStart = pos + 7;
		final String spreadType = parseStringValue(ba, pos + 7, 2);
		final int numberOfLegs = Character.getNumericValue(ba[pos + 9]);
		final String[] legs = new String[numberOfLegs];
		legs[0] = symbol;

		pos += 10;
		for (int i = 1; i < numberOfLegs; i++) {
			final int pos2 = getIndexOf(ba, ',', pos);
			legs[i] = parseStringValue(ba, pos, pos2 - pos);
			pos = pos2 + 1;
		}

		int start2 = pos;
		switch (subrecord) {
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
			// N.B. Keep the comma
			start2--;
			break;
		}
		final int length = spStart + (ba.length - start2);
		final byte[] ba2 = new byte[length];

		System.arraycopy(ba, 0, ba2, 0, spStart);
		System.arraycopy(ba, start2, ba2, spStart, ba.length - start2);

		final StringBuilder sb = new StringBuilder("_S_" + spreadType);
		for (final String s : legs) {
			sb.append("_" + s);
		}

		final byte[] ba3 = new byte[ba2.length - symbol.length() + sb.length()];
		ba3[0] = 1;
		ba3[1] = 50;

		for (int i = 0; i < sb.length(); i++) {
			ba3[2 + i] = (byte) sb.charAt(i);
		}

		System.arraycopy(ba2, 2 + symbol.length(), ba3, 2 + sb.length(),
				ba3.length - (2 + sb.length()));

		return ba3;
	}

	private static int
			getIndexOf(final byte[] b, final char c, final int offset) {

		if (b.length < offset) {
			throw new IndexOutOfBoundsException(
					"Offset greater than array length");
		}

		for (int i = offset; i < b.length; i++) {
			if (b[i] == c) {
				return i;
			}
		}

		return -1;
	}

	private static String parseStringValue(final byte[] b, final int start,
			final int length) {

		if (b.length < start + length) {
			throw new IndexOutOfBoundsException(
					"Requested string overflows array");
		}

		final StringBuffer sb = new StringBuffer();

		for (int i = start; i < start + length; i++) {
			sb.append((char) b[i]);
		}

		return sb.toString();
	}

}
