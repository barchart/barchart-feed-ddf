/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import static com.barchart.feed.ddf.historical.provider.CodecHelper.*;

import com.barchart.feed.base.api.instrument.enums.InstrumentField;
import com.barchart.feed.ddf.historical.api.DDF_Entry;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;

abstract class Entry implements DDF_Entry, Codec {

	//

	protected int index;

	protected long millisUTC;

	protected DDF_Instrument instrument;

	protected byte ordTradeDay = DDF_TradeDay.UNKNOWN.ord;

	//

	Entry(final DDF_Instrument instrument) {
		this.instrument = instrument;
	}

	//

	@Override
	public long getMillisUTC() {
		return millisUTC;
	}

	@Override
	public DDF_Instrument getInstrument() {
		return instrument;
	}

	@Override
	public int priceExponent() {
		return instrument.get(InstrumentField.FRACTION).decimalExponent;
	}

	//

	protected void decodeHead(final String[] inputArray) {
		throw new UnsupportedOperationException();
	}

	protected void decodeTail(final String[] inputArray) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void decode(final String inputLine) {

		if (inputLine == null) {
			return;
		}

		final String[] inputArray = splitCSV(inputLine);

		decodeHead(inputArray);

		decodeTail(inputArray);

	}

	//

	@Override
	public String encode() {
		throw new UnsupportedOperationException();
	}

	//

	@Override
	public String toString() {
		return encode();
	}

	@Override
	public String csvHeader() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String csvEntry() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public DDF_TradeDay getTradeDay() {
		return DDF_TradeDay.fromOrd(ordTradeDay);
	}

}
