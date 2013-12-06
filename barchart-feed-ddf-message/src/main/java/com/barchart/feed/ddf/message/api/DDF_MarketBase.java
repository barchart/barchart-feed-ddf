/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.feed.base.market.api.MarketMessage;
import com.barchart.feed.base.values.api.TextValue;
import com.barchart.feed.base.values.api.TimeValue;
import com.barchart.feed.ddf.message.enums.DDF_Session;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;
import com.barchart.feed.ddf.symbol.api.DDF_Symbol;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_SpreadType;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.util.common.anno.NotMutable;

/**
 * Base type for ddf feed market data messages.
 */
@NotMutable
public interface DDF_MarketBase extends DDF_BaseMessage, MarketMessage {

	/* GENERIC */

	/** market instrument resolved from {@link #getId()} */
	//InstrumentEntity instrument();

	/** time from message time stamp if present or from message arrival time; */
	TimeValue getTime();

	/* PROPRIETARY */

	/** ddf feed symbol parser; resolved from {@link #getId()} */
	DDF_Symbol getSymbol();

	/** raw ddf feed symbol, such as MSFT or RJZ1 */
	TextValue getId();

	/** ddf exchange classifier */
	DDF_Exchange getExchange();

	/** ddf "base code" represented as generic "price fraction" */
	DDF_Fraction getFraction();

	/** ddf spread type, if any */
	DDF_SpreadType getSpreadType();

	/** ddf trading date of message */
	DDF_TradeDay getTradeDay();

	/** ddf trading session classifier; */
	DDF_Session getSession();

	/** nominal message delay time indicator, minutes; when delay is present */
	int getDelay();

}
