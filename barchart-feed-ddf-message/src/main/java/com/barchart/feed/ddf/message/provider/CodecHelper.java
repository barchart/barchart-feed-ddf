/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.util.ascii.ASCII.*;

import java.nio.ByteBuffer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableDateTime;
import org.w3c.dom.Element;

import com.barchart.feed.ddf.message.api.DDF_MarketBook;
import com.barchart.feed.ddf.symbol.provider.DDF_Symbology;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.feed.ddf.util.HelperXML;
import com.barchart.util.ascii.ASCII;

/* TODO make public ? */

class CodecHelper {

	// TODO make configurable
	static final boolean IS_FEED_TIME_STAMP_PRESENT = true;

	// ///////////////////////////////////////////////

	/** maximum size of ddf book snapshot on bid or ask side */
	final static int DDF_BOOK_LIMIT = DDF_MarketBook.ENTRY_LIMIT;

	/** default ddf message delay */
	static final int DDF_NO_DELAY = 0;

	/** default ddf book prices */
	static final long[] DDF_NO_PRICES = new long[DDF_BOOK_LIMIT];

	/** default ddf book sizes */
	static final long[] DDF_NO_SIZES = new long[DDF_BOOK_LIMIT];

	/** */
	static final DX_XS_Session[] DDF_NO_SESSIONS = new DX_XS_Session[0];

	/** */
	static final int DDF_NO_COUNT = 0;

	/** ddf encoded time stamp first byte */
	final static byte DDF_CENTURY = ASCII.DC4;

	/** ddf time stamp encoding/decoding mask */
	final static int DDF_TIME_STAMP_MASK = 0x40;

	// ///////////////////////////////////////////////

	// feed time stamp - "magic 9 bytes" or "current default"

	static final long decodeFeedTimeStamp(final DateTimeZone zone,
			final ByteBuffer buffer) {
		if (buffer.position() == buffer.limit()) {
			// no suffix; return current
			return System.currentTimeMillis();
		}
		//
		buffer.mark();
		final byte timeStampStart = buffer.get();
		buffer.reset();
		if (timeStampStart == DDF_CENTURY) {
			// magic 9 bytes
			return CodecHelper.decodeMillisUTC(zone, buffer);
		} else {
			// unknown suffix; return current
			return System.currentTimeMillis();
		}
	}

	static final void encodeFeedTimeStamp(final long millisUTC,
			final DateTimeZone zone, final ByteBuffer buffer) {
		if (millisUTC == HelperDDF.DDF_EMPTY
				|| millisUTC == HelperDDF.DDF_CLEAR) {
			return;
		}
		if (IS_FEED_TIME_STAMP_PRESENT) {
			CodecHelper.encodeMillisUTC(millisUTC, zone, buffer);
		}
	}

	static final byte[] read(final ByteBuffer buffer, final byte marker) {
		final byte[] source = buffer.array();
		final int start = buffer.position();
		int index = start;
		while (index < source.length && source[index] != marker) {
			index++;
		}
		buffer.position(index + 1);
		final int size = index - start;
		final byte[] target = new byte[size];
		System.arraycopy(source, start, target, 0, size);
		return target;
	}

	static final byte find(final ByteBuffer buffer, final byte marker) {
		return find(buffer.array(), buffer.position(), marker);
	}

	static final byte find(final byte[] source, final int start,
			final byte marker) {
		int index = start;
		while (index < source.length && source[index] != marker) {
			index++;
		}
		if (index == start || index == source.length) {
			return NUL;
		} else {
			return source[index - 1];
		}
	}

	//

	final static void checkDigit(final int value) {
		if (value < 0 || value >= 10) {
			throw new IllegalArgumentException();
		}
	}

	final static void encodeUnsigned_1(final int value, final ByteBuffer buffer) {
		checkDigit(value);
		buffer.put((byte) (_0_ + value));
	}

	final static void encodeUnsigned_1_book(final int value,
			final ByteBuffer buffer) {
		if (0 <= value && value < 10) {
			buffer.put((byte) (_0_ + value));
			return;
		}
		if (10 <= value && value < (_Z_ - _A_)) {
			buffer.put((byte) (_A_ + value));
			return;
		}
		// FIXME silent error
		buffer.put(_0_);
	}

	final static byte decodeUnsigned_1(final ByteBuffer buffer) {
		final int value = buffer.get() - _0_;
		checkDigit(value);
		return (byte) value;
	}

	final static byte decodeUnsigned_1_book(final ByteBuffer buffer) {
		final byte alpha = buffer.get();
		if (isDigit(alpha)) {
			return (byte) (alpha - _0_);
		}
		if (isLetterUpper(alpha)) {
			return (byte) (10 + alpha - _A_);
		}
		// FIXME silent error
		return 0;
	}

	final static void encodeUnsigned_2(final int value, final ByteBuffer buffer) {
		if (value < 0 || value >= 100) {
			throw new IllegalArgumentException();
		}
		final int ones = value % 10;
		final int tens = value / 10;
		buffer.put((byte) (_0_ + tens)); // first byte
		buffer.put((byte) (_0_ + ones)); // second byte
	}

	final static byte decodeUnsigned_2(final ByteBuffer buffer) {
		final int tens = buffer.get() - _0_;
		checkDigit(tens);
		final int ones = buffer.get() - _0_;
		checkDigit(ones);
		return (byte) (tens * 10 + ones);
	}

	static final int bookBidIndexFrom(final byte code) {
		if (code < _K_ || _T_ < code) {
			throw new IllegalArgumentException();
		}
		return (code - _K_);
	}

	static final byte bookBidCodeFrom(final int index) {
		if (index < 0 || DDF_BOOK_LIMIT <= index) {
			throw new IllegalArgumentException();
		}
		return (byte) (_K_ + index);
	}

	static final int bookAskIndexFrom(final byte code) {
		if (code < _A_ || _J_ < code) {
			throw new IllegalArgumentException();
		}
		return (_J_ - code);
	}

	static final byte bookAskCodeFrom(final int index) {
		if (index < 0 || DDF_BOOK_LIMIT <= index) {
			throw new IllegalArgumentException();
		}
		return (byte) (_J_ - index);
	}

	static final byte[][] xmlDecSymbol(final Element tag,
			final String attribute, final boolean isThrow) {

		final String string = tag.getAttribute(attribute);
		if (string.length() > 0) {
			try {
				return DDF_Symbology.symbolArrayFromSymbolString(string);
			} catch (final Exception e) {
				// below
			}
		}
		if (isThrow) {
			throw new IllegalArgumentException("attribute not valid : "
					+ attribute);
		}
		return null;
	}

	// byte array

	static final boolean isXmlBook(final Element root) {
		return HelperXML.isXmlNameMatch(root, XmlTagBook.TAG);
	}

	static final boolean isXmlCuvol(final Element root) {
		return HelperXML.isXmlNameMatch(root, XmlTagCuvol.TAG);
	}

	static final boolean isXmlQuote(final Element root) {
		return HelperXML.isXmlNameMatch(root, XmlTagQuote.TAG);
	}

	final static byte encodeTimeStampByte(final int timeField) {
		return (byte) (timeField | DDF_TIME_STAMP_MASK);
	}

	final static int decodeTimeStampByte(final byte timeByte) {
		return timeByte & (~DDF_TIME_STAMP_MASK);
	}

	/** time zone information is discarded */
	static final void encodeTimeStamp(final ReadableDateTime dateTime,
			final ByteBuffer buffer) {

		// base fields
		buffer.put(DDF_CENTURY); // century
		buffer.put(encodeTimeStampByte(dateTime.getYearOfCentury())); // year
		buffer.put(encodeTimeStampByte(dateTime.getMonthOfYear())); // month
		buffer.put(encodeTimeStampByte(dateTime.getDayOfMonth())); // day
		buffer.put(encodeTimeStampByte(dateTime.getHourOfDay())); // hours
		buffer.put(encodeTimeStampByte(dateTime.getMinuteOfHour())); // minutes
		buffer.put(encodeTimeStampByte(dateTime.getSecondOfMinute())); // seconds

		// milliseconds
		final int millisOfSecond = dateTime.getMillisOfSecond();
		buffer.put((byte) (millisOfSecond & 0xFF)); // low byte
		buffer.put((byte) ((millisOfSecond >>> 8) & 0xFF)); // high byte

	}

	/** time zone information is provided */
	static final DateTime decodeTimeStamp(final DateTimeZone zone,
			final ByteBuffer buffer) {

		// base fields
		buffer.get(); // DDF_CENTURY
		final int yearOfEra = 2000 + decodeTimeStampByte(buffer.get());
		final int monthOfYear = decodeTimeStampByte(buffer.get());
		final int dayOfMonth = decodeTimeStampByte(buffer.get());
		final int hourOfDay = decodeTimeStampByte(buffer.get());
		final int minuteOfHour = decodeTimeStampByte(buffer.get());
		final int secondOfMinute = decodeTimeStampByte(buffer.get());

		// milliseconds
		final byte lo = buffer.get();
		final byte hi = buffer.get();
		final int millisOfSecond = ((hi & 0xFF) << 8) | (lo & 0xFF);

		// will throw RTE if any field is out of range
		return new DateTime(//
				yearOfEra, monthOfYear, dayOfMonth, //
				hourOfDay, minuteOfHour, secondOfMinute, //
				millisOfSecond, zone);

	}

	static final void encodeMillisUTC(final long millisUTC,
			final DateTimeZone zone, final ByteBuffer buffer) {
		final DateTime dateTime = new DateTime(millisUTC, zone);
		encodeTimeStamp(dateTime, buffer);
	}

	static final long decodeMillisUTC(final DateTimeZone zone,
			final ByteBuffer buffer) {
		final DateTime dateTime = decodeTimeStamp(zone, buffer);
		return dateTime.getMillis();
	}

	//

	static final void check(final byte left, final byte right) {
		if (left == right) {
			return;
		} else {
			throw new RuntimeException("no match;" + " left=" + left
					+ " right=" + right);
		}
	}

}
