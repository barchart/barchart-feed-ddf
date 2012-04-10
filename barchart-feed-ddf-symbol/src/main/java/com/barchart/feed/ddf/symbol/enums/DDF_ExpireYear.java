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

// TODO: Auto-generated Javadoc
/**
 * The Enum DDF_ExpireYear.
 */
public enum DDF_ExpireYear {

	/*
	 * YEAR 1 Byte, Alphanumeric; Contains the last digit of the expiration
	 * year.
	 */

	Y0('0'), //
	Y1('1'), //
	Y2('2'), //
	Y3('3'), //
	Y4('4'), //
	Y5('5'), //
	Y6('6'), //
	Y7('7'), //
	Y8('8'), //
	Y9('9'), //

	UNKNOWN('?'), //

	;

	private static final Logger log = LoggerFactory
			.getLogger(DDF_ExpireYear.class);

	/** The code. */
	public final char code;

	private DDF_ExpireYear(final char code) {
		this.code = code;
	}

	/**
	 * From code.
	 *
	 * @param code the code
	 * @return the dD f_ expire year
	 */
	public static final DDF_ExpireYear fromCode(final char code) {
		switch (code) {
		case '0':
			return Y0;
		case '1':
			return Y1;
		case '2':
			return Y2;
		case '3':
			return Y3;
		case '4':
			return Y4;
		case '5':
			return Y5;
		case '6':
			return Y6;
		case '7':
			return Y7;
		case '8':
			return Y8;
		case '9':
			return Y9;
		default:
			return UNKNOWN;
		}
	}

	/**
	 * four digit year value for this enum and millisUTC; for example:
	 * 
	 * Y9.getYear("millisUTC-some-time-in-2009") -> 2009
	 * 
	 * Y9.getYear("millisUTC-some-time-in-2013") -> 2019
	 *
	 * @param millisUTC the millis utc
	 * @return the year this or next
	 */
	public final int getYearThisOrNext(final long millisUTC) {

		// say, 2
		final int expiYearDigit = code - '0';

		// say, 2013
		final int testYear = new DateTime(millisUTC, DateTimeZone.UTC)
				.getYear();

		// then 3
		final int testYearDigit = testYear % 10;

		// then 2010
		final int testYearDecade = testYear - testYearDigit;

		if (expiYearDigit >= testYearDigit) {
			// same decade : 2012
			return testYearDecade + expiYearDigit;
		} else {
			// next decade : 2022
			return testYearDecade + 10 + expiYearDigit;
		}

	}

	/**
	 * Gets the year this or past.
	 *
	 * @param millisUTC the millis utc
	 * @return the year this or past
	 */
	public final int getYearThisOrPast(final long millisUTC) {

		// say 2
		final int expiYearDigit = code - '0';

		// say, 2013
		final int testYear = new DateTime(millisUTC, DateTimeZone.UTC)
				.getYear();

		// then 3
		final int testYearDigit = testYear % 10;

		// then 2010
		final int testYearDecade = testYear - testYearDigit;

		if (expiYearDigit > testYearDigit) {
			// past decade : 2002
			return testYearDecade - 10 + expiYearDigit;
		} else {
			// this decade : 2012
			return testYearDecade + expiYearDigit;
		}

	}

	/**
	 * From date time.
	 *
	 * @param dateTime the date time
	 * @return the dD f_ expire year
	 */
	public static final DDF_ExpireYear fromDateTime(final DateTime dateTime) {
		final int year = dateTime.getYear();
		final int yearOfDecade = year % 10;
		// NOTE: assumes ascii values
		final char code = (char) (yearOfDecade + '0');
		return fromCode(code);
	}

	// NOTE: TIME_ZONE_CHICAGO
	/**
	 * From millis utc.
	 *
	 * @param millisUTC the millis utc
	 * @return the dD f_ expire year
	 */
	public static final DDF_ExpireYear fromMillisUTC(final long millisUTC) {
		final DateTime dateTime = new DateTime(millisUTC, DateTimeZone.UTC);
		return fromDateTime(dateTime);
	}

	/**
	 * Checks if is valid.
	 *
	 * @param code the code
	 * @return true, if is valid
	 */
	public static final boolean isValid(final char code) {
		final DDF_ExpireYear year = fromCode(code);
		return year != UNKNOWN;
	}

	/**
	 * Offset.
	 *
	 * @return the int
	 */
	public int offset() {
		return code - '0';
	}

	/**
	 * From option year.
	 *
	 * @param indicator the indicator
	 * @return the dD f_ expire year
	 */
	public static final DDF_ExpireYear fromOptionYear(
			final DDF_OptionYear indicator) {
		switch (indicator) {
		case CALL_0:
			return Y0;
		case CALL_1:
			return Y1;
		case CALL_2:
			return Y2;
		case CALL_3:
			return Y3;
		case CALL_4:
			return Y4;
		case CALL_5:
			return Y5;
		case CALL_6:
			return Y6;
		case CALL_7:
			return Y7;
		case CALL_8:
			return Y8;
		case CALL_9:
			return Y9;
		case PUT_0:
			return Y0;
		case PUT_1:
			return Y1;
		case PUT_2:
			return Y2;
		case PUT_3:
			return Y3;
		case PUT_4:
			return Y4;
		case PUT_5:
			return Y5;
		case PUT_6:
			return Y6;
		case PUT_7:
			return Y7;
		case PUT_8:
			return Y8;
		case PUT_9:
			return Y9;
		default:
			return UNKNOWN;
		}
	}

}
