/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import static com.barchart.feed.ddf.historical.provider.CodecHelper.decodeInt;
import static com.barchart.feed.ddf.historical.provider.CodecHelper.decodeLong;
import static com.barchart.feed.ddf.historical.provider.CodecHelper.decodeMantissa;
import static com.barchart.feed.ddf.historical.provider.CodecHelper.decodeMinsTime;
import static com.barchart.feed.ddf.historical.provider.CodecHelper.splitCSV;

import com.barchart.feed.ddf.historical.api.DDF_EntryBarMinNearby;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;

class EntryMinsNearby extends EntryBar implements DDF_EntryBarMinNearby {

	/**
	 * Instantiates a new entry mins nearby.
	 *
	 * @param instrument the instrument
	 */
	public EntryMinsNearby(final DDF_Instrument instrument) {
		super(instrument);
	}


	@Override
	public void decode(final String inputLine) {

		if (inputLine == null) {
			return;
		}

		final String[] inputArray = splitCSV(inputLine);

		millisUTC = decodeMinsTime(inputArray[1], instrument);

		ordTradeDay = DDF_TradeDay.fromDay(decodeInt(inputArray[2])).ord;

		priceOpenMantissa = decodeMantissa(inputArray[3], priceExponent());
		priceHighMantissa = decodeMantissa(inputArray[4], priceExponent());
		priceLowMantissa = decodeMantissa(inputArray[5], priceExponent());
		priceCloseMantissa = decodeMantissa(inputArray[6], priceExponent());

		sizeVolume = decodeLong(inputArray[7]);

	}
}
