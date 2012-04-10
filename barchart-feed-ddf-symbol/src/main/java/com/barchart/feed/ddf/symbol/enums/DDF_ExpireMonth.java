/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.enums;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum DDF_ExpireMonth {

	/*
	 * EXPIRATION MONTH CODE 1 Byte, Alphabetic. It indicates the expiration
	 * month for the security future.
	 */

	JAN('F', 1), //
	JAN_Long('A', 1), //
	FEB('G', 2), //
	FEB_Long('B', 2), //
	MAR('H', 3), //
	MAR_Long('C', 3), //
	APR('J', 4), //
	APR_Long('D', 4), //
	MAY('K', 5), //
	MAY_Long('E', 5), //
	JUN('M', 6), //
	JUN_Long('I', 6), //
	JUL('N', 7), //
	JUL_Long('L', 7), //
	AUG('Q', 8), //
	AUG_Long('O', 8), //
	SEP('U', 9), //
	SEP_Long('P', 9), //
	OCT('V', 10), //
	OCT_Long('R', 10), //
	NOV('X', 11), //
	NOV_Long('S', 11), //
	DEC('Z', 12), //
	DEC_Long('T', 12), //

	CASH('Y', 0), //

	UNKNOWN('?', -1), //

	;

	private static final Logger log = LoggerFactory
			.getLogger(DDF_ExpireMonth.class);

	public final char code;

	public final int value;

	private DDF_ExpireMonth(char code, int value) {
		this.code = code;
		this.value = value;
	}

	public static final DDF_ExpireMonth fromCode(final char code) {
		switch (code) {
		case 'F':
			return JAN;
		case 'G':
			return FEB;
		case 'H':
			return MAR;
		case 'J':
			return APR;
		case 'K':
			return MAY;
		case 'M':
			return JUN;
		case 'N':
			return JUL;
		case 'Q':
			return AUG;
		case 'U':
			return SEP;
		case 'V':
			return OCT;
		case 'X':
			return NOV;
		case 'Z':
			return DEC;
			//
		case 'Y':
			return CASH;
			// TODO 'long' months
		default:
			log.error(String.format(
					"unknown expiration month code: %1$c (ascii %1$d)", code));
			return UNKNOWN;
		}
	}

	public static final DDF_ExpireMonth fromMillisUTC(final long millisUTC) {
		final DateTime dateTime = new DateTime(millisUTC, DateTimeZone.UTC);
		return fromDateTime(dateTime);
	}

	public static final DDF_ExpireMonth fromDateTime(final DateTime dateTime) {
		if (dateTime == null) {
			return UNKNOWN;
		}
		// DateTime 'default' calendar is the ISO8601
		// JAN==1 ... DEC==12
		final int month = dateTime.getMonthOfYear();
		switch (month) {
		case 1:
			return JAN;
		case 2:
			return FEB;
		case 3:
			return MAR;
		case 4:
			return APR;
		case 5:
			return MAY;
		case 6:
			return JUN;
		case 7:
			return JUL;
		case 8:
			return AUG;
		case 9:
			return SEP;
		case 10:
			return OCT;
		case 11:
			return NOV;
		case 12:
			return DEC;
		default:
			return UNKNOWN;
		}
	}

	public static final boolean isValid(final char code) {
		final DDF_ExpireMonth month = fromCode(code);
		return month != UNKNOWN;
	}

}
