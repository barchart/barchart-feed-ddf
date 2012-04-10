/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import static com.barchart.feed.ddf.historical.provider.CodecHelper.*;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.*;

import org.joda.time.DateTime;

import com.barchart.feed.ddf.historical.api.DDF_EntryTick;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField;
import com.barchart.util.ascii.ASCII;

class EntryTicksDetail extends EntryTicks implements DDF_EntryTick {

	public EntryTicksDetail(final DDF_Instrument instrument) {
		super(instrument);
	}

	// ///////////////////////////

	protected long priceTradeMantissa;

	protected long sizeTrade;

	// ///////////////////////////

	@Override
	public long priceTradeMantissa() {
		return priceTradeMantissa;
	}

	@Override
	public long sizeTrade() {
		return sizeTrade;
	}

	/**
	 * YYYY­MM­DD HH:MM:SS.FFF,TRADING_DAY,SESSION_CODE,
	 * 
	 * PRICE,SIZE
	 */
	@Override
	public void decodeTail(final String[] inputArray) {

		priceTradeMantissa = decodeMantissa(inputArray[3], priceExponent());

		sizeTrade = decodeLong(inputArray[4]);

	}

	@Override
	public String encode() {

		final StringBuilder text = new StringBuilder(128);

		text.append(encodeTicksTime(millisUTC, instrument));
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

	@Override
	public String csvHeader() {
		return HEADER;
	}

	@Override
	public String csvEntry() {

		final StringBuilder text = new StringBuilder(128);

		text.append(index);
		text.append(ASCII.STRING_COMMA);

		text.append(instrument.get(DDF_SYMBOL_UNIVERSAL));
		text.append(ASCII.STRING_COMMA);

		text.append(millisUTC);
		text.append(ASCII.STRING_COMMA);

		text.append(new DateTime(millisUTC, instrument
				.get(DDF_InstrumentField.DDF_ZONE).zone));
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
