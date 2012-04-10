/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public enum DDF_OptionYear {

	// NOTE keep order; ordinal() is in use

	CALL_0('C'), //
	CALL_1('D'), //
	CALL_2('E'), //
	CALL_3('F'), //
	CALL_4('G'), //
	CALL_5('H'), //
	CALL_6('I'), //
	CALL_7('J'), //
	CALL_8('K'), //
	CALL_9('L'), //
	// 
	PUT_0('P'), //
	PUT_1('Q'), //
	PUT_2('R'), //
	PUT_3('S'), //
	PUT_4('T'), //
	PUT_5('U'), //
	PUT_6('V'), //
	PUT_7('W'), //
	PUT_8('X'), //
	PUT_9('Y'), //

	UNKNOWN('?'), //

	;

	private static final Logger log = LoggerFactory
			.getLogger(DDF_OptionYear.class);

	public final char code;

	private DDF_OptionYear(char code) {
		this.code = code;
	}

	public static final DDF_OptionYear fromCode(final char code) {
		switch (code) {
		case 'C':
			return CALL_0;
		case 'D':
			return CALL_1;
		case 'E':
			return CALL_2;
		case 'F':
			return CALL_3;
		case 'G':
			return CALL_4;
		case 'H':
			return CALL_5;
		case 'I':
			return CALL_6;
		case 'J':
			return CALL_7;
		case 'K':
			return CALL_8;
		case 'L':
			return CALL_9;
			//
		case 'P':
			return PUT_0;
		case 'Q':
			return PUT_1;
		case 'R':
			return PUT_2;
		case 'S':
			return PUT_3;
		case 'T':
			return PUT_4;
		case 'U':
			return PUT_5;
		case 'V':
			return PUT_6;
		case 'W':
			return PUT_7;
		case 'X':
			return PUT_8;
		case 'Y':
			return PUT_9;
			//
		default:
			return UNKNOWN;
		}
	}

	public static final DDF_OptionYear fromIndiYear(final DDF_Option indicator,
			final DDF_ExpireYear year) {
		if (indicator == null || year == null) {
			return UNKNOWN;
		}
		if (DDF_Option.UNKNOWN == indicator || DDF_ExpireYear.UNKNOWN == year) {
			return UNKNOWN;
		}
		final DDF_OptionYear base;
		switch (indicator) {
		case CALL:
			base = CALL_0;
			break;
		case PUT:
			base = PUT_0;
			break;
		default:
			return UNKNOWN;
		}
		final int index = base.ordinal() + year.offset();
		assert 0 <= index && index < values().length : "index=" + index;
		return values()[index];
	}

	public static final DDF_OptionYear getForYear(final DDF_Option indicator,
			final int thisYear, final int testYear) {
		if (thisYear < 2000 || testYear < 2000) {
			throw new IllegalArgumentException("year must be after 2000");
		}
		final int diffYear = (testYear - thisYear);
		if (diffYear < 0) {
			log.error("test year must be same as this year or in the future");
			return UNKNOWN;
		}
		if (diffYear > 9) {
			log.error("(testYear - thisYear) difference must be under 9");
			return UNKNOWN;
		}
		final int index;
		switch (indicator) {
		case CALL:
			index = CALL_0.ordinal() + diffYear;
			break;
		case PUT:
			index = PUT_0.ordinal() + diffYear;
			break;
		default:
			index = UNKNOWN.ordinal();
			break;
		}
		return values()[index];
	}

	public final static boolean isValid(final char code) {
		final DDF_OptionYear indicator = fromCode(code);
		return indicator != UNKNOWN;
	}

}
