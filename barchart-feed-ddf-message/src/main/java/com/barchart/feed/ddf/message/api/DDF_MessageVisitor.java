/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

/**
 * http://en.wikipedia.org/wiki/Visitor_pattern
 *
 * @param <Result>
 *            the generic type
 * @param <Param>
 *            the generic type
 */
public interface DDF_MessageVisitor<Result, Param> {

	// control

	Result visit(DDF_ControlResponse message, Param param);

	Result visit(DDF_ControlTimestamp message, Param param);

	// market

	/** sent for {@link DDF_FeedInterest#CUVOL_SNAPSHOT} */
	Result visit(DDF_MarketCuvol message, Param param);

	/** sent for {@link DDF_FeedInterest#QUOTE_UPDATE} */
	Result visit(DDF_MarketCondition message, Param param);

	/** sent for {@link DDF_FeedInterest#BOOK_SNAPSHOT} */
	Result visit(DDF_MarketBook message, Param param);

	/** sent for {@link DDF_FeedInterest#BOOK_UPDATE} */
	Result visit(DDF_MarketBookTop message, Param param);

	/** sent for {@link DDF_FeedInterest#QUOTE_UPDATE} */
	Result visit(DDF_MarketParameter message, Param param);

	/** sent for {@link DDF_FeedInterest#QUOTE_UPDATE} */
	Result visit(DDF_MarketTrade message, Param param);

	/** sent for {@link DDF_FeedInterest#QUOTE_SNAPSHOT}; ddf v 2 */
	Result visit(DDF_MarketSnapshot message, Param param);

	/** sent for {@link DDF_FeedInterest#QUOTE_SNAPSHOT}; ddf v 3 */
	Result visit(DDF_MarketQuote message, Param param);

	/** processed as part of {@link DDF_MarketQuote} */
	Result visit(DDF_MarketSession message, Param param);

	/** EOD messages */

	Result visit(DDF_EOD_Commodity message, Param param);

	Result visit(DDF_EOD_CommoditySpread message, Param param);

	Result visit(DDF_EOD_EquityForex message, Param param);

	Result visit(DDF_Prior_IndividCmdy message, Param param);

	Result visit(DDF_Prior_TotCmdy message, Param param);

}
