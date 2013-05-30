/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import static com.barchart.feed.ddf.historical.provider.CodecHelper.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.ddf.historical.api.DDF_EntryTick;
import com.barchart.feed.inst.InstrumentField;
import com.barchart.util.ascii.ASCII;

// TODO: Auto-generated Javadoc
class EntryTicksDetail extends EntryTicks implements DDF_EntryTick {

	/**
	 * Instantiates a new entry ticks detail.
	 *
	 * @param instrument the instrument
	 */
	public EntryTicksDetail(final Instrument instrument) {
		super(instrument);
	}

	// ///////////////////////////

	protected long priceTradeMantissa;

	protected long sizeTrade;

	// ///////////////////////////

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.api.DDF_EntryTick#priceTradeMantissa()
	 */
	@Override
	public long priceTradeMantissa() {
		return priceTradeMantissa;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.api.DDF_EntryTick#sizeTrade()
	 */
	@Override
	public long sizeTrade() {
		return sizeTrade;
	}

	/**
	 * YYYY­MM­DD HH:MM:SS.FFF,TRADING_DAY,SESSION_CODE,
	 * 
	 * PRICE,SIZE
	 *
	 * @param inputArray the input array
	 */
	@Override
	public void decodeTail(final String[] inputArray) {

		priceTradeMantissa = decodeMantissa(inputArray[3], priceExponent());

		sizeTrade = decodeLong(inputArray[4]);

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

		text.append(encodeMantissa(priceTradeMantissa, priceExponent()));
		text.append(ASCII.STRING_COMMA);

		text.append(encodeLong(sizeTrade));

		return text.toString();

	}

	//

	static final String HEADER = "INDEX,SYMBOL,MILLIS_UTC,DATE_TIME_ISO,TRADE_DAY,TRADE_SESSION,PRICE,SIZE";

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

		text.append(encodeMantissa(priceTradeMantissa, priceExponent()));
		text.append(ASCII.STRING_COMMA);

		text.append(encodeLong(sizeTrade));

		return text.toString();

	}

}
