/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.api;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;

/**
 * The Interface DDF_Entry.
 */
public interface DDF_Entry {

	DDF_TradeDay getTradeDay();

	Instrument getInstrument();

	/** index of this entry in the result */
	int getIndex();

	/** shared price exponent */
	int priceExponent();

	//

	/**
	 * http://en.wikipedia.org/wiki/ISO_8601
	 */
	long getMillisUTC();

	//

	String csvHeader();

	String csvEntry();

}
