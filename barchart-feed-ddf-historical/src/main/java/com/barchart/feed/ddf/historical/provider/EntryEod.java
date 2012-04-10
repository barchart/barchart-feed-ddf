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

import com.barchart.feed.ddf.historical.api.DDF_EntryBarEod;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;
import com.barchart.util.ascii.ASCII;

// TODO: Auto-generated Javadoc
class EntryEod extends EntryBar implements DDF_EntryBarEod {

	/**
	 * Instantiates a new entry eod.
	 *
	 * @param instrument the instrument
	 */
	public EntryEod(final DDF_Instrument instrument) {
		super(instrument);
	}

	// ///////////////////////////

	protected long sizeInterest;

	// ///////////////////////////

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.api.DDF_EntryBarEod#sizeInterest()
	 */
	@Override
	public long sizeInterest() {
		return sizeInterest;
	}

	//

	/**
	 * SYMBOL,YYYY­MM­DD,OPEN,HIGH,LOW,CLOSE,VOLUME[,OPENINTEREST].
	 *
	 * @param inputLine the input line
	 */
	@Override
	public void decode(final String inputLine) {

		if (inputLine == null) {
			return;
		}

		final String[] inputArray = splitCSV(inputLine);

		instrument = decodeInstrument(inputArray[0]);

		millisUTC = decodeEodTime(inputArray[1], instrument);

		ordTradeDay = DDF_TradeDay.fromMillisUTC(millisUTC).ord;

		priceOpenMantissa = decodeMantissa(inputArray[2], priceExponent());
		priceHighMantissa = decodeMantissa(inputArray[3], priceExponent());
		priceLowMantissa = decodeMantissa(inputArray[4], priceExponent());
		priceCloseMantissa = decodeMantissa(inputArray[5], priceExponent());

		sizeVolume = decodeLong(inputArray[6]);

		if (inputArray.length >= 8) {
			sizeInterest = decodeLong(inputArray[7]);
		}

	}

	static final String HEADER = "INDEX,SYMBOL,MILLIS_UTC,DATE_TIME_ISO,"
			+ "PRICE_OPEN,PRICE_HIGH,PRICE_LOW,PRICE_CLOSE,"
			+ "SIZE_VOLUME,SIZE_INTEREST";

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.provider.EntryBar#encode()
	 */
	@Override
	public String encode() {

		final StringBuilder text = new StringBuilder(128);

		text.append(encodeInstrument(instrument, millisUTC));
		text.append(ASCII.STRING_COMMA);

		text.append(encodeEodTime(millisUTC, instrument));
		text.append(ASCII.STRING_COMMA);

		text.append(encodeMantissa(priceOpenMantissa, priceExponent()));
		text.append(ASCII.STRING_COMMA);
		text.append(encodeMantissa(priceHighMantissa, priceExponent()));
		text.append(ASCII.STRING_COMMA);
		text.append(encodeMantissa(priceLowMantissa, priceExponent()));
		text.append(ASCII.STRING_COMMA);
		text.append(encodeMantissa(priceCloseMantissa, priceExponent()));
		text.append(ASCII.STRING_COMMA);

		text.append(encodeLong(sizeVolume));

		if (sizeInterest > 0) {
			text.append(ASCII.STRING_COMMA);
			text.append(encodeLong(sizeInterest));
		}

		return text.toString();

	}

	//

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.provider.EntryBar#csvHeader()
	 */
	@Override
	public String csvHeader() {
		return HEADER;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.provider.EntryBar#csvEntry()
	 */
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

		text.append(encodeMantissa(priceOpenMantissa, priceExponent()));
		text.append(ASCII.STRING_COMMA);

		text.append(encodeMantissa(priceHighMantissa, priceExponent()));
		text.append(ASCII.STRING_COMMA);

		text.append(encodeMantissa(priceLowMantissa, priceExponent()));
		text.append(ASCII.STRING_COMMA);

		text.append(encodeMantissa(priceCloseMantissa, priceExponent()));
		text.append(ASCII.STRING_COMMA);

		text.append(encodeLong(sizeVolume));
		text.append(ASCII.STRING_COMMA);

		text.append(encodeLong(sizeInterest));

		return text.toString();

	}

}
