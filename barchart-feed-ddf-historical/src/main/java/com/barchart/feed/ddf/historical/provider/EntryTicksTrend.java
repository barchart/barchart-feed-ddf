/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import static com.barchart.feed.ddf.historical.provider.CodecHelper.decodeMantissa;
import static com.barchart.feed.ddf.historical.provider.CodecHelper.encodeMantissa;
import static com.barchart.feed.ddf.historical.provider.CodecHelper.encodeTicksTime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.api.framework.data.InstrumentField;
import com.barchart.feed.ddf.historical.api.DDF_EntryTrend;
import com.barchart.util.ascii.ASCII;

// TODO: Auto-generated Javadoc
class EntryTicksTrend extends EntryTicks implements DDF_EntryTrend {

	/**
	 * Instantiates a new entry ticks trend.
	 *
	 * @param instrument the instrument
	 */
	public EntryTicksTrend(final Instrument instrument) {
		super(instrument);
	}

	// ///////////////////////////

	protected long priceResistance;
	protected long priceSupport;

	// ///////////////////////////

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.api.DDF_EntryTrend#priceResistance()
	 */
	@Override
	public long priceResistance() {
		return priceResistance;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.api.DDF_EntryTrend#priceSupport()
	 */
	@Override
	public long priceSupport() {
		return priceSupport;
	}

	/**
	 * YYYY­MM­DD HH:MM:SS.FFF,TRADING_DAY,SESSION_CODE,
	 * 
	 * PRICE_SUPPORT,PRICE_RESISTANCE
	 *
	 * @param inputArray the input array
	 */
	@Override
	public void decodeTail(final String[] inputArray) {

		priceSupport = decodeMantissa(inputArray[3], priceExponent());

		priceResistance = decodeMantissa(inputArray[4], priceExponent());

	}

	//

	static final String HEADER = "INDEX,SYMBOL,MILLIS_UTC,DATE_TIME_ISO,TRADE_DAY,TRADE_SESSION,"
			+ "PRICE_SUPPORT,PRICE_RESISTANCE";

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.provider.Entry#csvHeader()
	 */
	@Override
	public String csvHeader() {
		return HEADER;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.provider.Entry#csvEntry()
	 */
	@Override
	public String csvEntry() {

		final StringBuilder text = new StringBuilder(128);

		text.append(index);
		text.append(ASCII.STRING_COMMA);

		text.append(inst.get(InstrumentField.SYMBOL));
		text.append(ASCII.STRING_COMMA);

		text.append(millisUTC);
		text.append(ASCII.STRING_COMMA);

		text.append(new DateTime(millisUTC, DateTimeZone.forOffsetMillis(
				(int)inst.get(InstrumentField.TIME_ZONE_OFFSET).asLong())));
		text.append(ASCII.STRING_COMMA);

		text.append(getTradeDay());
		text.append(ASCII.STRING_COMMA);

		text.append(getSession());
		text.append(ASCII.STRING_COMMA);

		text.append(encodeMantissa(priceSupport(), priceExponent()));
		text.append(ASCII.STRING_COMMA);

		text.append(encodeMantissa(priceResistance(), priceExponent()));

		return text.toString();

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.provider.Entry#encode()
	 */
	@Override
	public String encode() {

		final StringBuilder text = new StringBuilder(128);

		text.append(encodeTicksTime(millisUTC, inst));
		text.append(ASCII.STRING_COMMA);

		text.append((char) getTradeDay().code);
		text.append(ASCII.STRING_COMMA);

		text.append((char) getSession().code);
		text.append(ASCII.STRING_COMMA);

		text.append(encodeMantissa(priceSupport(), priceExponent()));
		text.append(ASCII.STRING_COMMA);

		text.append(encodeMantissa(priceResistance(), priceExponent()));

		return text.toString();

	}

}
