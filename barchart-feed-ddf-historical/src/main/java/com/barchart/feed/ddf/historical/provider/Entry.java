/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import static com.barchart.feed.ddf.historical.provider.CodecHelper.splitCSV;

import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.ddf.historical.api.DDF_Entry;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;

// TODO: Auto-generated Javadoc
abstract class Entry implements DDF_Entry, Codec {

	//

	protected int index;

	protected long millisUTC;

	protected Instrument inst;

	protected byte ordTradeDay = DDF_TradeDay.UNKNOWN.ord;

	//

	Entry(final Instrument instrument) {
		this.inst = instrument;
	}

	//

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.historical.api.DDF_Entry#getMillisUTC()
	 */
	@Override
	public long getMillisUTC() {
		return millisUTC;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.historical.api.DDF_Entry#getInstrument()
	 */
	@Override
	public Instrument getInstrument() {
		return inst;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.historical.api.DDF_Entry#priceExponent()
	 */
	@Override
	public int priceExponent() {
		return (int)inst.displayFraction().decimalExponent();
	}

	//

	protected void decodeHead(final String[] inputArray) {
		throw new UnsupportedOperationException();
	}

	protected void decodeTail(final String[] inputArray) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.historical.provider.Codec#decode(java.lang.String)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.historical.provider.Codec#encode()
	 */
	@Override
	public String encode() {
		throw new UnsupportedOperationException();
	}

	//

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return encode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.historical.api.DDF_Entry#csvHeader()
	 */
	@Override
	public String csvHeader() {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.historical.api.DDF_Entry#csvEntry()
	 */
	@Override
	public String csvEntry() {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.historical.api.DDF_Entry#getIndex()
	 */
	@Override
	public int getIndex() {
		return index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.historical.api.DDF_Entry#getTradeDay()
	 */
	@Override
	public DDF_TradeDay getTradeDay() {
		return DDF_TradeDay.fromOrd(ordTradeDay);
	}

}
