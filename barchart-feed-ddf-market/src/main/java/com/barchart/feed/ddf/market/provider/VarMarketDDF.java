/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.market.provider;

import static com.barchart.feed.base.bar.enums.MarketBarField.BAR_TIME;
import static com.barchart.feed.base.bar.enums.MarketBarField.CLOSE;
import static com.barchart.feed.base.bar.enums.MarketBarField.TRADE_DATE;
import static com.barchart.feed.base.bar.enums.MarketBarField.VOLUME;
import static com.barchart.feed.base.bar.enums.MarketBarType.CURRENT;
import static com.barchart.feed.base.bar.enums.MarketBarType.CURRENT_EXT;
import static com.barchart.feed.base.market.enums.MarketEvent.MARKET_UPDATED;
import static com.barchart.feed.base.market.enums.MarketEvent.NEW_BOOK_ERROR;
import static com.barchart.feed.base.market.enums.MarketEvent.NEW_BOOK_SNAPSHOT;
import static com.barchart.feed.base.market.enums.MarketEvent.NEW_BOOK_TOP;
import static com.barchart.feed.base.market.enums.MarketEvent.NEW_BOOK_UPDATE;
import static com.barchart.feed.base.market.enums.MarketEvent.NEW_CLOSE;
import static com.barchart.feed.base.market.enums.MarketEvent.NEW_CUVOL_SNAPSHOT;
import static com.barchart.feed.base.market.enums.MarketEvent.NEW_CUVOL_UPDATE;
import static com.barchart.feed.base.market.enums.MarketEvent.NEW_TRADE;
import static com.barchart.feed.base.market.enums.MarketEvent.NEW_VOLUME;
import static com.barchart.feed.base.market.enums.MarketField.BOOK;
import static com.barchart.feed.base.market.enums.MarketField.BOOK_TOP;
import static com.barchart.feed.base.market.enums.MarketField.INSTRUMENT;
import static com.barchart.feed.base.market.enums.MarketField.MARKET_TIME;
import static com.barchart.feed.base.trade.enums.MarketTradeField.PRICE;
import static com.barchart.feed.base.trade.enums.MarketTradeField.SEQUENCING;
import static com.barchart.feed.base.trade.enums.MarketTradeField.SESSION;
import static com.barchart.feed.base.trade.enums.MarketTradeField.SIZE;
import static com.barchart.feed.base.trade.enums.MarketTradeField.TRADE_TIME;
import static com.barchart.feed.base.trade.enums.MarketTradeField.TYPE;
import static com.barchart.feed.base.trade.enums.MarketTradeSequencing.NORMAL;
import static com.barchart.feed.base.trade.enums.MarketTradeSession.DEFAULT;
import static com.barchart.feed.base.trade.enums.MarketTradeSession.EXTENDED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.api.enums.BookLiquidityType;
import com.barchart.feed.base.bar.api.MarketDoBar;
import com.barchart.feed.base.bar.enums.MarketBarField;
import com.barchart.feed.base.bar.enums.MarketBarType;
import com.barchart.feed.base.book.api.MarketBook;
import com.barchart.feed.base.book.api.MarketDoBook;
import com.barchart.feed.base.book.api.MarketDoBookEntry;
import com.barchart.feed.base.book.enums.UniBookResult;
import com.barchart.feed.base.cuvol.api.MarketDoCuvol;
import com.barchart.feed.base.cuvol.api.MarketDoCuvolEntry;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.base.provider.VarMarket;
import com.barchart.feed.base.state.api.MarketState;
import com.barchart.feed.base.state.enums.MarketStateEntry;
import com.barchart.feed.base.trade.api.MarketDoTrade;
import com.barchart.feed.base.trade.enums.MarketTradeField;
import com.barchart.feed.base.trade.enums.MarketTradeSequencing;
import com.barchart.feed.base.trade.enums.MarketTradeSession;
import com.barchart.feed.base.trade.enums.MarketTradeType;
import com.barchart.feed.inst.InstrumentField;
import com.barchart.util.anno.Mutable;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.api.TimeValue;

/**
 * Logic #1
 * 
 * keep here value relations and event management logic only
 **/
@Mutable
class VarMarketDDF extends VarMarket {

	private static final Logger log = LoggerFactory
			.getLogger(VarMarketDDF.class);

	private final void updateMarket(final TimeValue time) {

		set(MARKET_TIME, time);

		eventAdd(MARKET_UPDATED);

	}

	@Override
	public void setInstrument(final Instrument newSymbol) {

		final Instrument oldInst = get(INSTRUMENT);

		if (Instrument.NULL_INSTRUMENT.equals(oldInst)) {
			set(INSTRUMENT, newSymbol);
		} else {
			throw new IllegalStateException("symbol can be set only once");
		}

	}

	@Override
	public void setBookSnapshot(final MarketDoBookEntry[] entries,
			final TimeValue time) {

		assert entries != null;
		assert time != null;

		final MarketDoBook book = loadBook();

		book.setSnapshot(entries);

		eventAdd(NEW_BOOK_SNAPSHOT);

		book.setTime(time);
		updateMarket(time);

	}

	// XXX original
	public void setBookSnapshotXXX(final MarketDoBookEntry[] entries,
			final TimeValue time) {

		assert entries != null;
		assert time != null;

		final MarketDoBook book = loadBook();

		book.clear();

		for (final MarketDoBookEntry entry : entries) {

			if (entry == null) {
				continue;
			}

			final UniBookResult result = book.setEntry(entry);

			switch (result) {
			case TOP:
			case NORMAL:
				break;
			default:
				eventAdd(NEW_BOOK_ERROR);
				log.error("result : {} entry : {}", result, entry);
				break;
			}

		}

		eventAdd(NEW_BOOK_SNAPSHOT);

		book.setTime(time);
		updateMarket(time);

	}

	@Override
	public void setBookUpdate(final MarketDoBookEntry entry,
			final TimeValue time) {

		assert entry != null && time != null;

		final MarketDoBook book = loadBook();

		final UniBookResult result = book.setEntry(entry);

		switch (result) {
		case TOP:
			eventAdd(NEW_BOOK_TOP);
			// continue
		case NORMAL:
			eventAdd(NEW_BOOK_UPDATE);
			break;
		default:
			eventAdd(NEW_BOOK_ERROR);
			final Instrument inst = get(MarketField.INSTRUMENT);
			final TextValue id = inst.get(InstrumentField.MARKET_GUID);
			final TextValue comment = inst.get(InstrumentField.DESCRIPTION);
			log.error("instrument : {} : {}", id, comment);
			log.error("result : {} ; entry : {} ;", result, entry);
			return;
		}

		book.setTime(time);
		updateMarket(time);

	}

	@Override
	public void setCuvolUpdate(final MarketDoCuvolEntry entry,
			final TimeValue time) {

		assert entry != null && time != null;

		makeCuvol(entry.priceValue(), entry.sizeValue());

		updateMarket(time);

	}

	@Override
	public void setCuvolSnapshot(final MarketDoCuvolEntry[] entries,
			final TimeValue time) {

		assert entries != null && time != null;

		final MarketDoCuvol cuvol = loadCuvol();

		cuvol.clear();

		for (final MarketDoCuvolEntry entry : entries) {
			cuvol.add(entry.priceValue(), entry.sizeValue());
		}

		eventAdd(NEW_CUVOL_SNAPSHOT);

		updateMarket(time);

	}

	private final void applyTradeToBar(final MarketTradeSession session,
			final MarketTradeSequencing sequencing, final PriceValue price,
			final SizeValue size, final TimeValue time, final TimeValue date) {

		final MarketBarType barType = session == EXTENDED ? CURRENT_EXT
				: CURRENT;
		final MarketDoBar bar = loadBar(barType.field);

		// XXX this is disabled to force compatibility with ddf2
		// if (bar.get(BAR_TIME).compareTo(time) > 0) {
		// log.error("ignoring past trade");
		// return;
		// }

		eventAdd(barType.event);

		// Reset current bar if session day changes
		final TimeValue prevDate = bar.get(TRADE_DATE);

		if (session == DEFAULT && !prevDate.isNull() && !date.equals(prevDate)) {

			log.debug("New day code: old=" + prevDate + "; new=" + date);

			bar.set(MarketBarField.OPEN, price);
			bar.set(MarketBarField.HIGH, price);
			bar.set(MarketBarField.LOW, price);
			bar.set(MarketBarField.INTEREST, size);
			bar.set(MarketBarField.VOLUME, size);

			setState(MarketStateEntry.IS_SETTLED, false);

		} else {

			// ### volume

			final SizeValue volumeOld = bar.get(VOLUME);
			final SizeValue volumeNew = volumeOld.add(size);
			bar.set(VOLUME, volumeNew);
			eventAdd(NEW_VOLUME);

			// ### high

			// XXX disable for dd2 compatibility
			// final PriceValue high = bar.get(HIGH);
			// if (price.compareTo(high) > 0 || high.isNull()) {
			// bar.set(HIGH, price);
			// if (type == CURRENT) {
			// // events only for combo
			// eventAdd(NEW_HIGH);
			// }
			// }

			// ### low

			// XXX disable for dd2 compatibility
			// final PriceValue low = bar.get(LOW);
			// if (price.compareTo(low) < 0 || low.isNull()) {
			// bar.set(LOW, price);
			// if (type == CURRENT) {
			// // events only for combo
			// eventAdd(NEW_LOW);
			// }
			// }

		}

		// ### last

		// Only update last for normal in-sequence trades
		if (sequencing == NORMAL) {
			if (price.isNull()) {
				log.warn("null or zero price on trade message, not applying to bar");
			} else {
				bar.set(CLOSE, price);
				if (session == DEFAULT) {
					// events only for combo
					eventAdd(NEW_CLOSE);
				}
				// ### time
				bar.set(BAR_TIME, time);
			}
		} else {
			// XXX: Update high / low, or just wait for refresh?
		}

		bar.set(MarketBarField.TRADE_DATE, date);

	}

	private final void makeCuvol(final PriceValue price, final SizeValue size) {

		final MarketDoCuvol cuvol = loadCuvol();

		cuvol.add(price, size);

		eventAdd(NEW_CUVOL_UPDATE);

	}

	@Override
	public void setTrade(final MarketTradeType type,
			final MarketTradeSession session,
			final MarketTradeSequencing sequencing, final PriceValue price,
			final SizeValue size, final TimeValue time, final TimeValue date) {

		assert type != null;
		assert session != null;
		assert sequencing != null;
		assert price != null;
		assert size != null;
		assert time != null;
		assert date != null;

//		log.debug("Trade: symbol="
//				+ get(MarketField.INSTRUMENT).get(InstrumentField.SYMBOL)
//				+ "; type=" + type + "; session=" + session + "; sequencing="
//				+ sequencing + "; price=" + price);
		// assert isValidPrice(price);

		// ### trade

		final MarketDoTrade trade = loadTrade();

		// XXX disabled to match ddf
		// if (trade.get(TRADE_TIME).compareTo(time) > 0) {
		// log.error("ignoring past trade");
		// return;
		// }

		trade.set(TYPE, type);
		trade.set(SESSION, session);
		trade.set(SEQUENCING, sequencing);
		trade.set(PRICE, price);
		trade.set(SIZE, size);
		trade.set(TRADE_TIME, time);
		trade.set(MarketTradeField.TRADE_DATE, date);

		eventAdd(NEW_TRADE);

		// ### bar

		// apply Form-T trades to CURRENT_EXT bar
		if (session == EXTENDED) {
			applyTradeToBar(session, sequencing, price, size, time, date);
		} else {
			applyTradeToBar(session, sequencing, price, size, time, date);
		}

		// ### cuvol

		makeCuvol(price, size);

		// ### time

		updateMarket(time);

	}

	@Override
	public void setBar(final MarketBarType type, final MarketDoBar bar) {

		assert type != null;
		assert bar != null;

		set(type.field, bar);

		eventAdd(type.event);

		updateMarket(bar.get(BAR_TIME));

	}

	//

	/** XXX eliminate: temp hack for ddf */
	protected MarketDoBook loadBook() {

		MarketBook book = get(BOOK);

		if (book.isFrozen()) {

			final Instrument inst = get(INSTRUMENT);

			final BookLiquidityType type = inst.get(InstrumentField.BOOK_LIQUIDITY);
			final SizeValue size = LIMIT;
			final PriceValue step = inst.get(InstrumentField.TICK_SIZE);

			final VarBookDDF varBook = new VarBookDDF(type, size, step);
			final VarBookTopDDF varBookTop = new VarBookTopDDF(varBook);

			set(BOOK, varBook);
			set(BOOK_TOP, varBookTop);

			book = varBook;

		}

		return (MarketDoBook) book;

	}

	@Override
	public void setState(final MarketStateEntry entry, final boolean isOn) {

		assert entry != null;

		final MarketState state = loadState();

		if (isOn) {
			state.add(entry);
		} else {
			state.remove(entry);
		}

	}

}
