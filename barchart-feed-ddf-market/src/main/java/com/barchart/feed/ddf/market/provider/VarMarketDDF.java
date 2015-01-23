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
import static com.barchart.feed.base.bar.enums.MarketBarField.HIGH;
import static com.barchart.feed.base.bar.enums.MarketBarField.LOW;
import static com.barchart.feed.base.bar.enums.MarketBarField.OPEN;
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
import static com.barchart.feed.base.market.enums.MarketField.TRADE;
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
import com.barchart.feed.base.state.enums.MarketStateEntry;
import com.barchart.feed.base.trade.api.MarketDoTrade;
import com.barchart.feed.base.trade.enums.MarketTradeField;
import com.barchart.feed.base.trade.enums.MarketTradeSequencing;
import com.barchart.feed.base.trade.enums.MarketTradeSession;
import com.barchart.feed.base.trade.enums.MarketTradeType;
import com.barchart.feed.base.values.api.BooleanValue;
import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.base.values.api.TimeValue;
import com.barchart.feed.base.values.provider.ValueConst;
import com.barchart.feed.ddf.message.provider.DDF_MessageService;
import com.barchart.util.common.anno.Mutable;

/**
 * Logic #1
 *
 * keep here value relations and event management logic only
 **/
@Mutable
class VarMarketDDF extends VarMarket {

	public VarMarketDDF(final Instrument instrument) {
		super(instrument);
	}

	private static final Logger log = LoggerFactory
			.getLogger(VarMarketDDF.class);

	private final void updateMarket(final TimeValue time) {

//		if(time.compareTo(get(MARKET_TIME)) > 0) {
//			set(MARKET_TIME, time);
//		}

		eventAdd(MARKET_UPDATED);

	}

	/*
	 * This is just being used in VarMarketEntityDDF (non-Javadoc)
	 *
	 * @see com.barchart.feed.base.market.api.MarketDo#fireCallbacks()
	 */
	@Override
	public void fireCallbacks() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setInstrument(final Instrument newSymbol) {
		instrument = newSymbol;
	}

	@Override
	public void setBookSnapshot(final MarketDoBookEntry[] entries, final TimeValue time) {

		assert entries != null;
		assert time != null;

		final MarketDoBook book = loadBook();

		book.setSnapshot(entries);

		setChange(Component.BOOK_COMBINED);
		eventAdd(NEW_BOOK_SNAPSHOT);

		book.setTime(time);
		set(MARKET_TIME, time);
		updateMarket(time);

	}

	@Override
	public void setBookUpdate(final MarketDoBookEntry entry, final TimeValue time) {

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
		set(MARKET_TIME, time);
		setChange(Component.BOOK_COMBINED);
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

		setChange(Component.CUVOL);
		eventAdd(NEW_CUVOL_SNAPSHOT);

		updateMarket(time);

	}

	@Override
	public void setTrade(
			final MarketTradeType type,
			final MarketTradeSession session,
			final MarketTradeSequencing sequencing, 
			final PriceValue price,
			final SizeValue size, 
			final TimeValue time, 
			final TimeValue date) {
		
		assert type != null;
		assert session != null;
		assert sequencing != null;
		assert price != null;
		assert size != null;
		assert time != null;
		assert date != null;
		
		// Update trade

		final MarketDoTrade trade = loadTrade();

		trade.set(TYPE, type);
		trade.set(SESSION, session);
		trade.set(SEQUENCING, sequencing);
		trade.set(PRICE, price);
		trade.set(SIZE, size);
		trade.set(TRADE_TIME, time);
		trade.set(MarketTradeField.TRADE_DATE, date);

		setChange(Component.TRADE);
		eventAdd(NEW_TRADE);

		set(TRADE, trade);

		applyTradeToBar(session, sequencing, price, size, time, date);

		makeCuvol(price, size, time);
		
		updateMarket(time);

	}

	@Override
	public void setSnapshot(
			final TimeValue tradeDate, 
			final PriceValue open, 
			final PriceValue high,
			final PriceValue low, 
			final PriceValue close, 
			final PriceValue settle, 
			final PriceValue previousSettle,
			final SizeValue volume, 
			final SizeValue interest,
			final PriceValue vwap,
			final BooleanValue isSettled, 
			final TimeValue barTime) {
		
		final MarketBarType type = ensureBar(tradeDate);

		if (type.isNull())
			return;

		final MarketDoBar bar = loadBar(type.field);

		applyBar(bar, MarketBarField.OPEN, open);
		applyBar(bar, MarketBarField.HIGH, high);
		applyBar(bar, MarketBarField.LOW, low);
		applyBar(bar, MarketBarField.CLOSE, close);
		applyBar(bar, MarketBarField.SETTLE, settle);
		applyBar(bar, MarketBarField.CLOSE_PREVIOUS, previousSettle);
		applyBar(bar, MarketBarField.VOLUME, volume);
		applyBar(bar, MarketBarField.INTEREST, interest);
		applyBar(bar, MarketBarField.VWAP, vwap);
		
		if (isSettled != null)
			bar.set(MarketBarField.IS_SETTLED, isSettled);

		if (barTime != null)
			bar.set(MarketBarField.BAR_TIME, barTime);

		setBar(type, bar);

	}

	@Override
	public void setBar(final MarketBarType type, final MarketDoBar bar) {

		assert type != null;
		assert bar != null;

		set(type.field, bar);

		switch (type) {
			case CURRENT:
				setChange(Component.DEFAULT_PREVIOUS);
				break;
			case PREVIOUS:
				setChange(Component.DEFAULT_CURRENT);
				break;
			case CURRENT_EXT:
				setChange(Component.EXTENDED_CURRENT);
				break;
			default:
				break;
		}

		eventAdd(type.event);

		/* Don't update time based on previous bar */
		if(type == MarketBarType.PREVIOUS) {
			eventAdd(MARKET_UPDATED);
		} else {
			updateMarket(bar.get(BAR_TIME));
		}

	}

	//

	/** XXX eliminate: temp hack for ddf */
	@Override
	protected MarketDoBook loadBook() {

		MarketBook book = get(BOOK);

		if (book.isFrozen()) {
			
			final VarBookDDF varBook = new VarBookDDF(instrument);
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

		final com.barchart.feed.base.state.api.MarketState state = loadState();

		if (isOn) {
			state.add(entry);
		} else {
			state.remove(entry);
		}

	}

	@Override
	public void setChange(final Component c) {
		changeSet.add(c);
	}

	@Override
	public void clearChanges() {
		changeSet.clear();
	}

	// ##################################################################################

	private void applyBar(final MarketDoBar bar,
			final MarketBarField<PriceValue> field, final PriceValue value) {

		if (DDF_MessageService.isEmpty(value)) {
			// no change in market field value
			return;
		}

		if (DDF_MessageService.isClear(value)) {
			// NULL_PRICE should be rendered as "price value not available"
			bar.set(field, ValueConst.NULL_PRICE);
			return;
		}

		bar.set(field, value);

	}

	private void applyBar(final MarketDoBar bar,
			final MarketBarField<SizeValue> field, final SizeValue value) {

		if (DDF_MessageService.isEmpty(value)) {
			// no change in market field value
			return;
		}

		if (DDF_MessageService.isClear(value)) {
			// NULL_SIZE should be rendered as "size value not available"
			bar.set(field, ValueConst.NULL_SIZE);
			return;
		}

		bar.set(field, value);

	}

	private final void applyTradeToBar(
			final MarketTradeSession session,
			final MarketTradeSequencing sequencing, 
			final PriceValue price,
			final SizeValue size, 
			final TimeValue time, 
			final TimeValue date) {

		MarketBarType barType = ensureBar(date);
		if (session == EXTENDED && barType == CURRENT)
			barType = CURRENT_EXT;

		final MarketDoBar bar = loadBar(barType.field);

		eventAdd(barType.event);

		final SizeValue volumeOld = bar.get(VOLUME);

		if (volumeOld.isNull()) {
			bar.set(VOLUME, size);
		} else {
			final SizeValue volumeNew = volumeOld.add(size);
			bar.set(VOLUME, volumeNew);
		}

		eventAdd(NEW_VOLUME);

		if (sequencing != MarketTradeSequencing.UNSEQUENCED_VOLUME) {

			// Only update last price for normal in-sequence trades unless null
			if (sequencing == NORMAL || bar.get(CLOSE).isNull()) {

				if (price.isNull()) {

					log.warn("null or zero price on trade message, not applying to bar");

				} else {

					// Set open if first trade
					if (bar.get(OPEN).isNull())
						bar.set(OPEN, price);

					bar.set(CLOSE, price);

					if (session == DEFAULT) {
						// events only for combo
						eventAdd(NEW_CLOSE);
					}

					// Update time
					bar.set(BAR_TIME, time);

				}

			}

			// Update high/low for sequential and non-sequential trades
			if (!price.isNull() && !price.isZero()) {

				final PriceValue high = bar.get(HIGH);
				if (high.isNull())
					bar.set(HIGH, price);
				else if (high.compareTo(price) < 0)
					bar.set(HIGH, price);

				final PriceValue low = bar.get(LOW);
				if (low.isNull())
					bar.set(LOW, price);
				else if (low.compareTo(price) > 0)
					bar.set(LOW, price);

			}

		}

	}

	private final void makeCuvol(final PriceValue price, final SizeValue size,
			final TimeValue time) {

		final MarketDoCuvol cuvol = loadCuvol();

		if (!cuvol.isNull()) {

			cuvol.add(price, size, time);

			setChange(Component.CUVOL);
			eventAdd(NEW_CUVOL_UPDATE);

		}

	}

}
