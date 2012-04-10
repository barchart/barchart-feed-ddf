/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.util;

import static com.barchart.util.ascii.ASCII.ASCII_CHARSET;
import static com.barchart.util.ascii.ASCII.DASH;
import static com.barchart.util.ascii.ASCII.NUL;
import static com.barchart.util.ascii.ASCII.STRING_DASH;
import static com.barchart.util.ascii.ASCII.STRING_EMPTY;
import static com.barchart.util.ascii.ASCII._0_;

import java.nio.ByteBuffer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.util.ascii.ASCII;
import com.barchart.util.math.MathExtra;
import com.barchart.util.values.api.DecimalValue;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;
import com.barchart.util.values.api.TimeValue;
import com.barchart.util.values.provider.ValueBuilder;

// TODO: Auto-generated Javadoc
/**
 * The Class HelperDDF.
 */
public final class HelperDDF {

	/** "ddf empty" value for price or size;  ddf feed: looks like ",," ; means no change from previous  xml feed: looks like missing/optional tag attribute. */
	public static final long DDF_EMPTY = Long.MIN_VALUE + 1;
	
	/** "ddf clear" value for price or size; really is a command in data path;  ddf feed: looks like ",-," ; means value no longer available  xml feed: no representation; treat same as "ddf blank". */
	public static final long DDF_CLEAR = Long.MIN_VALUE + 2;

	private HelperDDF() {
	}

	// from 10-1 (mantissa=101, fraction=1/4)
	// into 10.25 (mantissa=1025, fraction=1/100)
	/**
	 * From binary to decimal.
	 *
	 * @param value the value
	 * @param frac the frac
	 * @return the long
	 */
	public final static long fromBinaryToDecimal(final long value,
			final DDF_Fraction frac) {

		if (value == HelperDDF.DDF_EMPTY || value == HelperDDF.DDF_CLEAR) {
			return value;
		}

		if (frac.isBinary) {

			//
			long whole = value;
			// shift right binary
			whole /= frac.spacesDenomPlus;
			// shift left decimal
			whole = MathExtra.longMult(whole, frac.decimalDenominator);

			//
			long part = value;
			// partial shift left decimal
			part = MathExtra.longMult(part, frac.spacesDenomMinus);
			// remove whole part
			part -= whole;
			// remaining shift left decimal
			part = MathExtra.longMult(part, frac.spacesDenomPlus);
			// convert binary fraction to decimal fraction
			part /= frac.nativeDenominator;

			// merge whole and part
			return MathExtra.longAdd(whole, part);

		} else {

			return value;

		}

	}

	/**
	 * From decimal to binary.
	 *
	 * @param value the value
	 * @param frac the frac
	 * @return the long
	 */
	public final static long fromDecimalToBinary(final long value,
			final DDF_Fraction frac) {

		if (value == HelperDDF.DDF_EMPTY || value == HelperDDF.DDF_CLEAR) {
			return value;
		}

		if (frac.isBinary) {

			long whole = value;

			whole /= frac.decimalDenominator;

			whole = MathExtra.longMult(whole, frac.spacesDenomPlus);

			long part = value;

			part %= frac.decimalDenominator;
			part = MathExtra.longMult(part, frac.nativeDenominator);
			part /= frac.decimalDenominator;

			return MathExtra.longAdd(whole, part);

		} else {

			return value;

		}

	}

	/**
	 * Decimal encode.
	 *
	 * @param mantissa the mantissa
	 * @param frac the frac
	 * @param buffer the buffer
	 * @param marker the marker
	 */
	public final static void decimalEncode(/* local */long mantissa,
			final DDF_Fraction frac, final ByteBuffer buffer, final byte marker) {
		mantissa = fromDecimalToBinary(mantissa, frac);
		HelperDDF.longEncode(mantissa, buffer, marker);
	}

	/**
	 * Decimal decode.
	 *
	 * @param frac the frac
	 * @param buffer the buffer
	 * @param marker the marker
	 * @return the long
	 */
	public final static long decimalDecode(final DDF_Fraction frac,
			final ByteBuffer buffer, final byte marker) {
		long mantissa = HelperDDF.longDecode(buffer, marker);
		mantissa = fromBinaryToDecimal(mantissa, frac);
		return mantissa;
	}

	/**
	 * New decimal ddf.
	 *
	 * @param mantissa the mantissa
	 * @param frac the frac
	 * @return the decimal value
	 */
	public final static DecimalValue newDecimalDDF(final long mantissa,
			final DDF_Fraction frac) {
		if (mantissa == HelperDDF.DDF_EMPTY) {
			return ValueBuilder.newDecimalMutable(0, 0);
		}
		if (mantissa == HelperDDF.DDF_CLEAR) {
			return ValueBuilder.newDecimalMutable(0, 0);
		}
		return ValueBuilder.newDecimal(mantissa, frac.decimalExponent);
	}

	/**
	 * New price ddf.
	 *
	 * @param mantissa the mantissa
	 * @param frac the frac
	 * @return the price value
	 */
	public final static PriceValue newPriceDDF(final long mantissa,
			final DDF_Fraction frac) {
		if (mantissa == HelperDDF.DDF_EMPTY) {
			return ValueBuilder.newPriceMutable(0, 0);
		}
		if (mantissa == HelperDDF.DDF_CLEAR) {
			return ValueBuilder.newPriceMutable(0, 0);
		}
		return ValueBuilder.newPrice(mantissa, frac.decimalExponent);
	}

	/**
	 * New size ddf.
	 *
	 * @param sizeValue the size value
	 * @return the size value
	 */
	public final static SizeValue newSizeDDF(final long sizeValue) {
		if (sizeValue == HelperDDF.DDF_EMPTY) {
			return ValueBuilder.newSizeMutable(0);
		}
		if (sizeValue == HelperDDF.DDF_CLEAR) {
			return ValueBuilder.newSizeMutable(0);
		}
		return ValueBuilder.newSize(sizeValue);
	}

	/**
	 * New time ddf.
	 *
	 * @param millisUTC the millis utc
	 * @return the time value
	 */
	public final static TimeValue newTimeDDF(final long millisUTC) {
		if (millisUTC == HelperDDF.DDF_EMPTY) {
			return ValueBuilder.newTimeMutable(0);
		}
		if (millisUTC == HelperDDF.DDF_CLEAR) {
			return ValueBuilder.newTimeMutable(0);
		}
		return ValueBuilder.newTime(millisUTC);
	}

	/**
	 * Decimal decode.
	 *
	 * @param string the string
	 * @param frac the frac
	 * @return the long
	 */
	public static final long decimalDecode(final String string,
			final DDF_Fraction frac) {
		long value = HelperDDF.longDecode(string);
		value = fromBinaryToDecimal(value, frac);
		return value;
	}

	/**
	 * Decimal encode.
	 *
	 * @param value the value
	 * @param frac the frac
	 * @return the string
	 */
	public static final String decimalEncode(/* local */long value,
			final DDF_Fraction frac) {
		value = fromDecimalToBinary(value, frac);
		final String string = HelperDDF.longEncode(value);
		return string;
	}

	/**
	 * Long encode.
	 *
	 * @param value the value
	 * @param buffer the buffer
	 * @param marker the marker
	 */
	public final static void longEncode(final long value,
			final ByteBuffer buffer, final byte marker) {
		if (value == DDF_EMPTY) {
			// empty
			buffer.put(marker);
			return;
		}
		if (value == DDF_CLEAR) {
			buffer.put(DASH);
			buffer.put(marker);
			return;
		}
		// TODO optimize
		buffer.put(Long.toString(value).getBytes(ASCII_CHARSET));
		if (marker == NUL) {
			// write no marker
			return;
		}
		buffer.put(marker);
	}

	/**
	 * note: NUL marker does not consume; while non NUL does.
	 *
	 * @param buffer the buffer
	 * @param marker the marker
	 * @return the long
	 */
	public final static long longDecode(final ByteBuffer buffer,
			final byte marker) {

		buffer.mark();

		final boolean isDash = (buffer.get() == DASH);

		if (!isDash) {
			buffer.reset();
		}

		long value = 0;
		int count = 0;

		while (true) {

			if (buffer.position() == buffer.limit()) {
				break;
			}

			final byte alpha = buffer.get();

			if (ASCII.isDigit(alpha)) {
				value *= 10;
				value += alpha - _0_;
				count++;
				continue;
			}

			// stop on any; do not consume
			if (marker == NUL) {
				buffer.position(buffer.position() - 1);
				break;
			}

			// stop on match; consume
			if (marker == alpha) {
				break;
			} else {
				throw new IllegalStateException();
			}

		}

		if (isDash) {
			if (count > 0) {
				return -value;
			} else {
				return DDF_CLEAR;
			}
		} else {
			if (count > 0) {
				return value;
			} else {
				return DDF_EMPTY;
			}
		}

	}

	/**
	 * Checks if is empty ddf.
	 *
	 * @param string the string
	 * @return true, if is empty ddf
	 */
	public static final boolean isEmptyDDF(final String string) {
		return string.length() == 0;
	}

	/**
	 * Checks if is dash ddf.
	 *
	 * @param string the string
	 * @return true, if is dash ddf
	 */
	public static final boolean isDashDDF(final String string) {
		return string.length() == 1 && string.charAt(0) == DASH;
	}

	/**
	 * Price decode.
	 *
	 * @param string the string
	 * @return the price value
	 */
	public final static PriceValue priceDecode(final String string) {
		final String[] array = string.split(ASCII.REGEX_DOT);
		final String mantString = array[0].concat(array[1]);
		final long mantissa = Long.parseLong(mantString);
		final int exponent = -array[1].length();
		return ValueBuilder.newPrice(mantissa, exponent);
	}

	/**
	 * Price encode.
	 *
	 * @param price the price
	 * @return the string
	 */
	public final static String priceEncode(final PriceValue price) {
		long whole = price.mantissa();
		int e1 = price.exponent();
		while (e1 > 0) {
			whole *= 10;
			e1--;
		}
		while (e1 < 0) {
			whole /= 10;
			e1++;
		}
		long part = 0;
		int e2 = price.exponent();
		if (e2 >= 0) {
			part = 0;
		} else {
			int denom = 1;
			while (e2 < 0) {
				denom *= 10;
				e2++;
			}
			part = price.mantissa() % denom;
		}
		return String.format("%d.%d", whole, part);
	}

	/**
	 * Byte as string.
	 *
	 * @param marker the marker
	 * @return the string
	 */
	public static final String byteAsString(final byte marker) {
		return new String(new char[] { (char) marker });
	}

	/**
	 * filter special ddf values.
	 *
	 * @param string the string
	 * @return the long
	 */
	public static final long longDecode(final String string) {
		if (isEmptyDDF(string)) {
			return DDF_EMPTY;
		} else if (isDashDDF(string)) {
			return DDF_CLEAR;
		} else {
			return Long.parseLong(string);
		}
	}

	/**
	 * filter special ddf values.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static final String longEncode(final long value) {
		if (value == DDF_EMPTY) {
			return STRING_EMPTY;
		} else if (value == DDF_CLEAR) {
			return STRING_DASH;
		} else {
			return Long.toString(value);
		}
	}

	/**
	 * from ddf "20100616124807" into millisUTC.
	 *
	 * @param value the value
	 * @param zone the zone
	 * @return the long
	 */
	public static final long timeDecode(/* local */long value,
			final DateTimeZone zone) {
		final int second = (int) (value % 100);
		value /= 100;
		final int minute = (int) (value % 100);
		value /= 100;
		final int hour = (int) (value % 100);
		value /= 100;
		final int day = (int) (value % 100);
		value /= 100;
		final int month = (int) (value % 100);
		value /= 100;
		final int year = (int) value;
		final DateTime dateTime = new DateTime(year, month, day, hour, minute,
				second, 0, zone);
		return dateTime.getMillis();
	}

	/**
	 * from millisUTC into ddf "20100616124807".
	 *
	 * @param millisUTC the millis utc
	 * @param zone the zone
	 * @return the long
	 */
	public static final long timeEncode(final long millisUTC,
			final DateTimeZone zone) {
		final DateTime dateTime = new DateTime(millisUTC, zone);
		long value = 0;
		final int year = dateTime.getYearOfEra();
		value += year;
		value *= 100;
		final int month = dateTime.getMonthOfYear();
		value += month;
		value *= 100;
		final int day = dateTime.getDayOfMonth();
		value += day;
		value *= 100;
		final int hour = dateTime.getHourOfDay();
		value += hour;
		value *= 100;
		final int minute = dateTime.getMinuteOfHour();
		value += minute;
		value *= 100;
		final int second = dateTime.getSecondOfMinute();
		value += second;
		return value;
	}

}
