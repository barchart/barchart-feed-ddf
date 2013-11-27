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

import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.bar.api.MarketDoBar;
import com.barchart.feed.base.bar.enums.MarketBarField;
import com.barchart.feed.base.bar.enums.MarketBarType;
import com.barchart.feed.base.book.api.MarketBook;
import com.barchart.feed.base.book.api.MarketDoBook;
import com.barchart.feed.base.book.api.MarketDoBookEntry;
import com.barchart.feed.base.book.enums.UniBookResult;
import com.barchart.feed.base.cuvol.api.MarketDoCuvol;
import com.barchart.feed.base.cuvol.api.MarketDoCuvolEntry;
import com.barchart.feed.base.provider.VarMarket;
import com.barchart.feed.base.state.api.MarketState;
import com.barchart.feed.base.state.enums.MarketStateEntry;
import com.barchart.feed.base.trade.api.MarketDoTrade;
import com.barchart.feed.base.trade.enums.MarketTradeField;
import com.barchart.feed.base.trade.enums.MarketTradeSequencing;
import com.barchart.feed.base.trade.enums.MarketTradeSession;
import com.barchart.feed.base.trade.enums.MarketTradeType;
import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.base.values.api.TimeValue;
import com.barchart.feed.base.values.provider.ValueBuilder;
import com.barchart.util.anno.Mutable;
import com.barchart.util.value.api.Price;

/**
 * Logic #1
 * 
 * keep here value relations and event management logic only
 **/
@Mutable
class VarMarketDDF extends VarMarket {

	public VarMarketDDF(Instrument instrument) {
		super(instrument);
	}

	private static final Logger log = LoggerFactory
			.getLogger(VarMarketDDF.class);

	private final void updateMarket(final TimeValue time) {

		set(MARKET_TIME, time);

		eventAdd(MARKET_UPDATED);

	}

	@Override
	public void setInstrument(final Instrument newSymbol) {
		instrument = newSymbol;
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
			final CharSequence id = instrument.id().toString();
			final CharSequence comment = instrument.description();
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

		makeCuvol(entry.priceValue(), entry.sizeValue(), time);

		updateMarket(time);

	}

	@Override
	public void setCuvolSnapshot(final MarketDoCuvolEntry[] entries,
			final TimeValue time) {

		assert entries != null && time != null;

		final MarketDoCuvol cuvol = loadCuvol();

		cuvol.clear();

		for (final MarketDoCuvolEntry entry : entries) {
			cuvol.add(entry.priceValue(), entry.sizeValue(), time);
		}

		eventAdd(NEW_CUVOL_SNAPSHOT);

		updateMarket(time);

	}

	@SuppressWarnings("deprecation")
	private final void applyTradeToBar(final MarketTradeSession session,
			final MarketTradeSequencing sequencing, final PriceValue price,
			final SizeValue size, final TimeValue time, final TimeValue date) {

		final MarketBarType barType = session == EXTENDED ? CURRENT_EXT
				: CURRENT;
		final MarketDoBar bar = loadBar(barType.field);

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

	private final void makeCuvol(final PriceValue price, final SizeValue size,
			final TimeValue time) {

		final MarketDoCuvol cuvol = loadCuvol();

		if(!cuvol.isNull()) {
		
			cuvol.add(price, size, time);
	
			eventAdd(NEW_CUVOL_UPDATE);
		
		}

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

		// ### trade

		final MarketDoTrade trade = loadTrade();

		trade.set(TYPE, type);
		trade.set(SESSION, session);
		trade.set(SEQUENCING, sequencing);
		trade.set(PRICE, price);
		trade.set(SIZE, size);
		trade.set(TRADE_TIME, time);
		trade.set(MarketTradeField.TRADE_DATE, date);

		eventAdd(NEW_TRADE);

		// ### bar

		applyTradeToBar(session, sequencing, price, size, time, date);

		// ### cuvol

		makeCuvol(price, size, time);

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

			Book.Type type = null;
			switch(instrument.liquidityType()) {
			default :
				type = Book.Type.NONE;
				break;
			case NONE:
				type = Book.Type.NONE;
				break;
			case DEFAULT:
				type = Book.Type.DEFAULT;
				break;
			case IMPLIED:
				type = Book.Type.IMPLIED;
				break;
			case COMBINED:
				type = Book.Type.COMBINED;
				break;
			}
			final SizeValue size = LIMIT;
			// TODO ValueConverter
			final Price tempStep = instrument.tickSize();
			final PriceValue step = ValueBuilder.newPrice(tempStep.mantissa(), 
					tempStep.exponent());

			final VarBookDDF varBook = new VarBookDDF(instrument, type, size, step);
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

	@Override
	public void setChange(Component c) {
		changeSet.add(c);
	}

	@Override
	public void clearChanges() {
		changeSet.clear();
	}

}
