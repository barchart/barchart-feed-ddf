/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.feed.ddf.message.enums.DDF_Condition;
import com.barchart.feed.ddf.message.enums.DDF_QuoteMode;
import com.barchart.feed.ddf.message.enums.DDF_QuoteState;
import com.barchart.util.anno.NotMutable;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.TextValue;

/**
 * represents xml "market quote"; a collection of sessions.
 */
@NotMutable
public interface DDF_MarketQuote extends DDF_MarketBookTop {

	// instrument fields

	/** ddf feed symbol name; such "Microsoft Corporation" */
	TextValue getSymbolName();

	/** a.k.a tick increment */
	PriceValue getPriceStep();

	/** future contract / stock share */
	PriceValue getPointValue();

	// intrinsic market state

	/** market active / halted; etc */
	DDF_Condition getCondition();

	/** market date roll over detection */
	DDF_QuoteState getState();

	/** realtime vs delayed; etc */
	DDF_QuoteMode getMode();

	// market component sessions

	/**
	 * @return session array; can be empty; supported session types: CURRENT
	 *         (default or combo), PREVIOUS (default or combo)
	 **/
	DDF_MarketSession[] sessions();

}
