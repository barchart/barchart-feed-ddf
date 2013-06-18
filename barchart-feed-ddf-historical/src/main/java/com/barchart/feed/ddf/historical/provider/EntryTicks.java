/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import static com.barchart.feed.ddf.historical.provider.CodecHelper.*;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.ddf.message.enums.DDF_Session;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;

// TODO: Auto-generated Javadoc
abstract class EntryTicks extends Entry {

	/**
	 * Instantiates a new entry ticks.
	 *
	 * @param instrument the instrument
	 */
	public EntryTicks(final Instrument instrument) {
		super(instrument);
	}

	// ///////////////////////////

	protected byte ordTradeDay = DDF_TradeDay.UNKNOWN.ord;
	protected byte ordSession = DDF_Session.UNKNOWN.ord;

	// ///////////////////////////

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.provider.Entry#getTradeDay()
	 */
	@Override
	public DDF_TradeDay getTradeDay() {
		return DDF_TradeDay.fromOrd(ordTradeDay);
	}

	/**
	 * Gets the session.
	 *
	 * @return the session
	 */
	public DDF_Session getSession() {
		return DDF_Session.fromOrd(ordSession);
	}

	/**
	 * YYYY­MM­DD HH:MM:SS.FFF,TRADING_DAY,SESSION_CODE,...
	 *
	 * @param inputArray the input array
	 */
	@Override
	public void decodeHead(final String[] inputArray) {

		millisUTC = decodeTicksTime(inputArray[0], inst);

		ordTradeDay = DDF_TradeDay.fromDay(decodeInt(inputArray[1])).ord;

		// FIXME
		ordSession = DDF_Session.fromPair(//
				DDF_Exchange.UNKNOWN.code, decodeByte(inputArray[2])).ord;

	}

	//

}
