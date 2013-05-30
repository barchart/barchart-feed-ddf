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
import static com.barchart.feed.base.provider.MarketConst.NULL_BOOK_ENTRY;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import com.barchart.feed.api.data.PriceLevel;
import com.barchart.feed.api.enums.BookLiquidityType;
import com.barchart.feed.api.enums.MarketSide;
import com.barchart.feed.base.book.api.MarketBook;
import com.barchart.feed.base.book.api.MarketBookEntry;
import com.barchart.feed.base.book.api.MarketDoBook;
import com.barchart.feed.base.book.api.MarketDoBookEntry;
import com.barchart.feed.base.book.enums.UniBookResult;
import com.barchart.feed.base.provider.DefBook;
import com.barchart.feed.base.provider.DefBookEntry;
import com.barchart.util.anno.Mutable;
import com.barchart.util.anno.ThreadSafe;
import com.barchart.util.value.api.Price;
import com.barchart.util.value.api.Size;
import com.barchart.util.value.api.Time;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;
import com.barchart.util.values.api.TimeValue;
import com.barchart.util.values.provider.ValueBuilder;
import com.barchart.util.values.provider.ValueFreezer;

@Mutable
@ThreadSafe(rule = "use in runSafe() only")
public final class VarBookDDF extends ValueFreezer<MarketBook> implements
		MarketDoBook {

	@SuppressWarnings("serial")
	private static class EntryMap extends TreeMap<PriceValue, MarketBookEntry> {
		public EntryMap(final Comparator<PriceValue> comp) {
			super(comp);
		}
	}

	private long millisUTC;

	private static final Comparator<PriceValue> CMP_ASC = new Comparator<PriceValue>() {
		@Override
		public int compare(final PriceValue o1, final PriceValue o2) {
			return o1.compareTo(o2);
		}
	};

	private static final Comparator<PriceValue> CMP_DES = new Comparator<PriceValue>() {
		@Override
		public int compare(final PriceValue o1, final PriceValue o2) {
			return o2.compareTo(o1);
		}
	};

	private final EntryMap bids = new EntryMap(CMP_ASC);
	private final EntryMap asks = new EntryMap(CMP_DES);

	private MarketBookEntry topBid;
	private MarketBookEntry topAsk;

	VarBookDDF(final BookLiquidityType type, final SizeValue size,
			final PriceValue step) {
		// XXX
	}

	// #####################################

	@Override
	public final UniBookResult setEntry(final MarketDoBookEntry entry) {

		final MarketSide side = entry.side();

		final int place = entry.place();

		//

		final EntryMap map = map(side);

		switch (side) {
		case BID:
			if (place == ENTRY_TOP) {
				topBid = entry;
			}
			break;
		case ASK:
			if (place == ENTRY_TOP) {
				topAsk = entry;
			}
			break;
		default:
			return UniBookResult.ERROR;
		}

		if (place == ENTRY_TOP) {
			return UniBookResult.TOP;
		} else {
			return UniBookResult.NORMAL;
		}

	}

	private EntryMap map(final MarketSide side) {
		switch (side) {
		default:
		case BID:
			return bids;
		case ASK:
			return asks;
		}
	}

	@Override
	public final MarketBookEntry[] entries(final MarketSide side) {

		final EntryMap map = map(side);

		final Collection<MarketBookEntry> values = map.values();

		final int size = values.size();

		final MarketBookEntry[] array = new MarketBookEntry[size];

		int index = 0;

		for (final MarketBookEntry entry : values) {
			array[index] = entry;
			index++;
		}

		return array;

	}

	@Override
	public final DefBook freeze() {
		return new DefBook(time(), entries(BID), entries(ASK), null, null);
	}

	@Override
	public TimeValue time() {
		return ValueBuilder.newTime(millisUTC);
	}

	@Override
	public final void setTime(final TimeValue time) {
		millisUTC = time.asMillisUTC();
	}

	@Override
	public final boolean isFrozen() {
		return false;
	}

	@Override
	public final MarketBookEntry last() {

		final DefBookEntry entry = null; // XXX

		return entry == null ? NULL_BOOK_ENTRY : entry;

	}

	@Override
	public final MarketBookEntry top(final MarketSide side) {

		final MarketBookEntry entry;
		switch (side) {
		case BID:
			entry = topBid;
			break;
		case ASK:
			entry = topAsk;
			break;
		default:
			entry = null;
			break;
		}

		// System.err.println("### TOP : " + side + " ### " + entry);

		return entry == null ? NULL_BOOK_ENTRY : entry;

	}

	/* #################################### */

	@Override
	public PriceValue priceGap() {
		throw new UnsupportedOperationException("UNUSED");
	}

	@Override
	public final SizeValue[] sizes(final MarketSide side) {
		throw new UnsupportedOperationException("UNUSED");
	}

	@Override
	public PriceValue priceTop(final MarketSide side) {
		throw new UnsupportedOperationException("UNUSED");
	}

	@Override
	public SizeValue sizeTop(final MarketSide side) {
		throw new UnsupportedOperationException("UNUSED");
	}

	/* #################################### */

	@Override
	public void clear() {

		bids.clear();
		asks.clear();

	}

	@Override
	public UniBookResult setSnapshot(final MarketDoBookEntry[] entries) {

		clear();

		for (final MarketDoBookEntry entry : entries) {

			final EntryMap map = map(entry.side());

			map.put(entry.priceValue(), entry);

		}

		return UniBookResult.NORMAL;

	}

	@Override
	public Price bestPrice(MarketSide side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double bestPriceDouble(MarketSide side) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Size bestSize(MarketSide side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long bestSizeLong(MarketSide side) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<PriceLevel> entryList(MarketSide side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Price lastPrice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double lastPriceDouble() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Time timeUpdated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Time lastUpdateTime() {
		// TODO Auto-generated method stub
		return null;
	}

}
