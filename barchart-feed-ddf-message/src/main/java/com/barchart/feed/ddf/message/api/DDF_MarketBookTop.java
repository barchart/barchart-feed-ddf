/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.book.api.MarketDoBookEntry;
import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.util.common.anno.NotMutable;

/** represents ddf feed top of market depth a.k.a bid/ask */
@NotMutable
public interface DDF_MarketBookTop extends DDF_MarketBase {

	MarketDoBookEntry entry(Instrument instrument, Book.Side side);

	//

	PriceValue getPriceBid();

	SizeValue getSizeBid();

	//

	PriceValue getPriceAsk();

	SizeValue getSizeAsk();

}