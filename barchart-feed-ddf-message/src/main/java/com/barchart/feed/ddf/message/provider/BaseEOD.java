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

import static com.barchart.feed.ddf.message.provider.CodecHelper.decodeFeedTimeStamp;
import static com.barchart.feed.ddf.message.provider.CodecHelper.encodeFeedTimeStamp;
import static com.barchart.util.common.ascii.ASCII.COMMA;
import static com.barchart.util.common.ascii.ASCII.ETX;
import static com.barchart.util.common.ascii.ASCII.MORE;
import static com.barchart.util.common.ascii.ASCII.SLASH;
import static com.barchart.util.common.ascii.ASCII._0_;

import java.nio.ByteBuffer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.message.enums.DDF_Session;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;
import com.barchart.feed.ddf.util.HelperDDF;

/**
 * @author g-litchfield
 *
 */
abstract class BaseEOD extends BaseMarket {

	BaseEOD() {
		super(DDF_MessageType.EOD_CMDY);
	}

	BaseEOD(final DDF_MessageType messageType) {
		super(messageType);
	}

	// //////////////////////////////////////

	protected long priceOpen = HelperDDF.DDF_EMPTY;
	protected long priceHigh = HelperDDF.DDF_EMPTY;
	protected long priceLow = HelperDDF.DDF_EMPTY;
	protected long priceLast = HelperDDF.DDF_EMPTY;

	// //////////////////////////////////////

	/*
	 * <soh>3<symbol>,C<stx><base><exchange ID><reserved><reserved>,
	 * <date>,<open>,<high>,<low>,<last><etx>
	 */

	protected static final void encodeInt(final int value, final ByteBuffer buffer, final int len) {

		assert len > 0;

		if (value > 10) {
			final int digit = value % 10;
			if (len > 1)
				encodeInt((value - digit) / 10, buffer, len - 1);
			buffer.put((byte) (digit + _0_));
		} else {
			buffer.put((byte) (value + _0_));
		}
	}

	protected static final int decodeInt(final ByteBuffer buffer, int len) {

		int value = 0;

		while (len > 0) {
			value *= 10;
			final int next = buffer.get() - _0_;
			if (next < 0 || next > 9)
				throw new IllegalArgumentException("Not an integer");
			value += next;
			len--;
		}

		return value;

	}

	protected final void decodeDay(final ByteBuffer buffer) {

		final int month = decodeInt(buffer, 2);
		check(buffer.get(), SLASH);
		final int day = decodeInt(buffer, 2);
		check(buffer.get(), SLASH);
		final int year = decodeInt(buffer, 4);
		check(buffer.get(), COMMA);

		final DateTime date = new DateTime(year, month, day, 0, 0, 0, DateTimeZone.UTC);

		setTradeDay(DDF_TradeDay.fromMillisUTC(date.getMillis()));

	}

	protected final void encodeDay(final ByteBuffer buffer) {

		final DateTime date = new DateTime(getTradeDay().tradeDate().asMillisUTC(), DateTimeZone.UTC);

		encodeInt(date.getMonthOfYear(), buffer, 2);
		buffer.put(SLASH);
		encodeInt(date.getDayOfMonth(), buffer, 2);
		buffer.put(SLASH);
		encodeInt(date.getYear(), buffer, 4);
		buffer.put(COMMA);

	}

	@Override
	protected final void encodeTail(final ByteBuffer buffer) {
		final DateTimeZone zone = getExchange().kind.time.zone;
		//
		buffer.put(ETX); // <etx>
		encodeFeedTimeStamp(millisUTC, zone, buffer);// <time stamp>
	}

	@Override
	protected final void decodeTail(final ByteBuffer buffer) {
		check(buffer.get(), ETX); // <etx>
		final DateTimeZone zone = getExchange().kind.time.zone;
		millisUTC = decodeFeedTimeStamp(zone, buffer); // <time stamp>
		setSession(DDF_Session.$_AT);
	}

	@Override
	protected final void encodeDelay(final ByteBuffer buffer) {
		buffer.put(MORE); // >
		buffer.put(MORE); // >
	}

	/**
	 * More forgiving delay parser since many EOD messages pass >>
	 */
	@Override
	protected final void decodeDelay(final ByteBuffer buffer) {
		delay = 0;
		check(buffer.get(), MORE); // >
		check(buffer.get(), MORE); // >
	}

}
