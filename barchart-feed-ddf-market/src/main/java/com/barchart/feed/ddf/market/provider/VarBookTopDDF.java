/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.market.provider;

import static com.barchart.feed.api.enums.MarketSide.ASK;
import static com.barchart.feed.api.enums.MarketSide.BID;

import com.barchart.feed.api.data.TopOfBook;
import com.barchart.feed.api.enums.MarketSide;
import com.barchart.feed.base.book.api.MarketBookEntry;
import com.barchart.feed.base.book.api.MarketBookTop;
import com.barchart.feed.base.provider.DefBookTop;
import com.barchart.feed.base.provider.ValueConverter;
import com.barchart.feed.base.provider.VarBook;
import com.barchart.util.anno.ProxyTo;
import com.barchart.util.value.api.Time;
import com.barchart.util.values.api.TimeValue;
import com.barchart.util.values.provider.ValueFreezer;

@ProxyTo({ VarBook.class })
final class VarBookTopDDF extends ValueFreezer<MarketBookTop> implements
		MarketBookTop {

	private final VarBookDDF book;

	VarBookTopDDF(final VarBookDDF book) {
		this.book = book;
	}

	@Override
	public final DefBookTop freeze() {
		return new DefBookTop(time(), side(BID), side(ASK));
	}

	@Override
	public final MarketBookEntry side(final MarketSide side) {
		return book.top(side);
	}

	@Override
	public final TimeValue time() {
		return book.time();
	}

	@Override
	public final boolean isFrozen() {
		return false;
	}

	@Override
	public Time lastUpdateTime() {
		return ValueConverter.time(book.time());
	}

	@Override
	public TopOfBook copy() {
		return this.freeze();
	}

}
