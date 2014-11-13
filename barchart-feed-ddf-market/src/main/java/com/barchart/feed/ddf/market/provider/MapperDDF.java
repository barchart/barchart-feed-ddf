/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.market.provider;

import static com.barchart.feed.base.bar.enums.MarketBarType.CURRENT;
import static com.barchart.feed.base.bar.enums.MarketBarType.PREVIOUS;
import static com.barchart.feed.base.book.api.MarketBook.ENTRY_TOP;
import static com.barchart.feed.base.book.enums.MarketBookAction.MODIFY;
import static com.barchart.feed.ddf.message.provider.DDF_MessageService.isClear;
import static com.barchart.feed.ddf.message.provider.DDF_MessageService.isEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.data.Market.Component;
import com.barchart.feed.base.bar.api.MarketDoBar;
import com.barchart.feed.base.bar.enums.MarketBarField;
import com.barchart.feed.base.bar.enums.MarketBarType;
import com.barchart.feed.base.book.api.MarketBook;
import com.barchart.feed.base.book.api.MarketBookTop;
import com.barchart.feed.base.book.api.MarketDoBookEntry;
import com.barchart.feed.base.book.enums.MarketBookAction;
import com.barchart.feed.base.cuvol.api.MarketDoCuvolEntry;
import com.barchart.feed.base.market.api.MarketDo;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.base.provider.DefBookEntry;
import com.barchart.feed.base.state.enums.MarketStateEntry;
import com.barchart.feed.base.trade.enums.MarketTradeSequencing;
import com.barchart.feed.base.values.api.BooleanValue;
import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.base.values.api.TimeValue;
import com.barchart.feed.base.values.provider.ValueConst;
import com.barchart.feed.ddf.message.api.DDF_ControlResponse;
import com.barchart.feed.ddf.message.api.DDF_ControlTimestamp;
import com.barchart.feed.ddf.message.api.DDF_EOD_Commodity;
import com.barchart.feed.ddf.message.api.DDF_EOD_CommoditySpread;
import com.barchart.feed.ddf.message.api.DDF_EOD_EquityForex;
import com.barchart.feed.ddf.message.api.DDF_MarketBook;
import com.barchart.feed.ddf.message.api.DDF_MarketBookTop;
import com.barchart.feed.ddf.message.api.DDF_MarketCondition;
import com.barchart.feed.ddf.message.api.DDF_MarketCuvol;
import com.barchart.feed.ddf.message.api.DDF_MarketParameter;
import com.barchart.feed.ddf.message.api.DDF_MarketQuote;
import com.barchart.feed.ddf.message.api.DDF_MarketSession;
import com.barchart.feed.ddf.message.api.DDF_MarketSnapshot;
import com.barchart.feed.ddf.message.api.DDF_MarketTrade;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.api.DDF_Prior_IndividCmdy;
import com.barchart.feed.ddf.message.api.DDF_Prior_TotCmdy;
import com.barchart.feed.ddf.message.enums.DDF_Indicator;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.message.enums.DDF_ParamType;
import com.barchart.feed.ddf.message.enums.DDF_QuoteMode;
import com.barchart.feed.ddf.message.enums.DDF_QuoteState;
import com.barchart.feed.ddf.message.enums.DDF_Session;
import com.barchart.feed.ddf.util.provider.DDF_ClearVal;
import com.barchart.feed.ddf.util.provider.DDF_NulVal;
import com.barchart.util.common.anno.ThreadSafe;

@ThreadSafe
class MapperDDF implements DDF_MessageVisitor<Void, MarketDo> {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public Void visit(final DDF_ControlResponse message, final MarketDo market) {
		// not used
		return null;
	}

	@Override
	public Void visit(final DDF_ControlTimestamp message, final MarketDo market) {
		// TODO: need to set this as the default for subsequent messages without
		// a timestamp
		return null;
	}

	@Override
	public Void visit(final DDF_MarketBook message, final MarketDo market) {

		market.clearChanges();

		final MarketDoBookEntry[] entries = message.entries();
		final TimeValue time = message.getTime();

		market.setBookSnapshot(entries, time);

		return null;
	}

	@Override
	public Void visit(final DDF_MarketBookTop message, final MarketDo market) {

		market.clearChanges();

		final TimeValue time = message.getTime();
		
		message.getTime();

		applyTop(message.entry(market.instrument(), Book.Side.BID), time, market);
		applyTop(message.entry(market.instrument(), Book.Side.ASK), time, market);

		return null;
	}

	@Override
	public Void visit(final DDF_MarketCondition message, final MarketDo market) {
		// TODO properly update market state
		log.error("TODO : \n{}", message);
		return null;
	}

	@Override
	public Void visit(final DDF_MarketCuvol message, final MarketDo market) {

		market.clearChanges();

		final MarketDoCuvolEntry[] entries = message.entries();

		final TimeValue time = message.getTime();

		market.setCuvolSnapshot(entries, time);

		return null;
	}

	@Override
	public Void visit(final DDF_MarketParameter message, final MarketDo market) {

		market.clearChanges();

		final DDF_ParamType param = message.getParamType();
		final TimeValue time = message.getTime();
		final TimeValue date = message.getTradeDay().tradeDate();
		final DDF_Session ddfSession = message.getSession();

		final PriceValue price;
		final SizeValue size;

		switch (param.kind) {

			default:
			case SIZE:
				price = ValueConst.NULL_PRICE;
				size = message.getAsSize();
				break;

			case PRICE:
				price = message.getAsPrice();
				size = ValueConst.NULL_SIZE;
				break;

		}

		// Roll session if needed
		final MarketBarType type = market.ensureBar(date);

		final MarketBookTop top = market.get(MarketField.BOOK_TOP);
		final MarketDoBar bar = market.loadBar(type.field);

		switch (param) {

			case TRADE_ASK_PRICE:

				market.setTrade(ddfSession.type, ddfSession.session,
						ddfSession.sequencing, price, size, time, date);

				return null;

			case TRADE_BID_PRICE:

				market.setTrade(ddfSession.type, ddfSession.session,
						ddfSession.sequencing, price, size, time, date);

				return null;

			case TRADE_LAST_PRICE:

				market.setTrade(ddfSession.type, ddfSession.session,
						ddfSession.sequencing, price, size, time, date);

				return null;

			case ASK_LAST:
			case ASK_LAST_PRICE:

				final DefBookEntry topAskPrice = new DefBookEntry(
						MODIFY, Book.Side.ASK,
						Book.Type.DEFAULT, ENTRY_TOP, price, top.side(Book.Side.ASK).sizeValue());
				applyTop(topAskPrice, time, market);

				return null;

			case ASK_LAST_SIZE:

				final DefBookEntry topAskSize = new DefBookEntry(
						MODIFY, Book.Side.ASK,
						Book.Type.DEFAULT, ENTRY_TOP, top.side(Book.Side.ASK).priceValue(), size);
				applyTop(topAskSize, time, market);

				return null;

			case BID_LAST:
			case BID_LAST_PRICE:

				final DefBookEntry topBidPrice = new DefBookEntry(
						MODIFY, Book.Side.BID,
						Book.Type.DEFAULT, ENTRY_TOP, price, top.side(Book.Side.BID).sizeValue());
				applyTop(topBidPrice, time, market);

				return null;

			case BID_LAST_SIZE:

				final DefBookEntry topBidSize = new DefBookEntry(
						MODIFY, Book.Side.BID,
						Book.Type.DEFAULT, ENTRY_TOP, top.side(Book.Side.BID).priceValue(), size);
				applyTop(topBidSize, time, market);

				return null;

			case CLOSE_LAST:
			case CLOSE_2_LAST:
			case CLOSE_ASK_PRICE:
			case CLOSE_BID_PRICE:
			case CLOSE_2_ASK_PRICE:
			case CLOSE_2_BID_PRICE:

				bar.set(MarketBarField.CLOSE, price);
				bar.set(MarketBarField.BAR_TIME, time);
				market.setBar(type, bar);

				return null;

			case HIGH_LAST_PRICE:
			case HIGH_BID_PRICE:
			case YEAR_HIGH_PRICE:

				bar.set(MarketBarField.HIGH, price);
				bar.set(MarketBarField.BAR_TIME, time);
				market.setBar(type, bar);

				return null;

			case LOW_LAST_PRICE:
			case LOW_ASK_PRICE:
			case YEAR_LOW_PRICE:

				bar.set(MarketBarField.LOW, price);
				bar.set(MarketBarField.BAR_TIME, time);
				market.setBar(type, bar);

				return null;

			case VOLUME_PAST_SIZE:

				if (type == CURRENT) {
					final MarketDoBar barPrevious = market.loadBar(PREVIOUS.field);
					barPrevious.set(MarketBarField.VOLUME, size);
					barPrevious.set(MarketBarField.BAR_TIME, time);
					market.setBar(PREVIOUS, barPrevious);
				}

				return null;

			case VOLUME_THIS_SIZE:

				bar.set(MarketBarField.VOLUME, size);
				bar.set(MarketBarField.BAR_TIME, time);
				market.setBar(type, bar);

				return null;

			case OPEN_LAST_PRICE:
			case OPEN_ASK_PRICE:
			case OPEN_BID_PRICE:
			case OPEN_2_LAST_PRICE:
			case OPEN_2_ASK_PRICE:
			case OPEN_2_BID_PRICE:

				bar.set(MarketBarField.OPEN, price);
				bar.set(MarketBarField.HIGH, price);
				bar.set(MarketBarField.LOW, price);
				bar.set(MarketBarField.CLOSE, price);
				bar.set(MarketBarField.INTEREST, size);
				bar.set(MarketBarField.VOLUME, size);
				bar.set(MarketBarField.BAR_TIME, time);
				bar.set(MarketBarField.SETTLE, ValueConst.NULL_PRICE);
				bar.set(MarketBarField.IS_SETTLED, ValueConst.FALSE_BOOLEAN);

				market.setBar(type, bar);

				return null;

			case INTEREST_LAST_SIZE:

				bar.set(MarketBarField.INTEREST, size);
				bar.set(MarketBarField.BAR_TIME, time);
				market.setBar(type, bar);

				return null;

			case INTEREST_PAST_SIZE:

				if (type == CURRENT) {
					final MarketDoBar barPrevious = market.loadBar(PREVIOUS.field);
					barPrevious.set(MarketBarField.INTEREST, size);
					barPrevious.set(MarketBarField.BAR_TIME, time);
					market.setBar(PREVIOUS, barPrevious);
				}

				return null;

			case PREVIOUS_LAST_PRICE:

				if (type == CURRENT) {
					final MarketDoBar barPrevious = market.loadBar(PREVIOUS.field);
					barPrevious.set(MarketBarField.CLOSE, price);
					barPrevious.set(MarketBarField.BAR_TIME, time);
					market.setBar(PREVIOUS, barPrevious);
				}

				return null;

			case SETTLE_FINAL_PRICE:

				bar.set(MarketBarField.IS_SETTLED, ValueConst.TRUE_BOOLEAN);

				/* Fall through */
			case SETTLE_EARLY_PRICE:

				/* Update changed comonents */
				market.clearChanges();
				market.setChange(Component.DEFAULT_CURRENT);

				bar.set(MarketBarField.SETTLE, price);
				bar.set(MarketBarField.BAR_TIME, time);

				market.setBar(type, bar);

				return null;
				
			case VWAP_LAST_PRICE:
				
				bar.set(MarketBarField.VWAP, price);
				
				market.setBar(type, bar);
				
				return null;

			default:
				log.debug("@@@ TODO : {} \n{}", param, message.getTime().toString() + " " + message);

		}

		return null;

	}

	/**
	 * via feed XQ snapshot or web URL lookup.
	 *
	 * @param message the message
	 * @param market the market
	 * @return the void
	 */
	@Override
	public Void visit(final DDF_MarketQuote message, final MarketDo market) {

		market.clearChanges();

		final DDF_QuoteState state = message.getState();
		final DDF_QuoteMode mode = message.getMode();

		/** Handle quote publish state */
		{
			switch (mode) {
				case END_OF_DAY:
				case DELAYED:
				case SNAPSHOT:
				case UNKNOWN:
					market.setState(MarketStateEntry.IS_PUBLISH_REALTIME, false);
					market.setState(MarketStateEntry.IS_PUBLISH_DELAYED, true);
					break;
				case REALTIME:
					market.setState(MarketStateEntry.IS_PUBLISH_REALTIME, true);
					market.setState(MarketStateEntry.IS_PUBLISH_DELAYED, false);
					break;
				default:
					break;
			}
		}

		/** Process Sessions */
		{
			final DDF_MarketSession[] sessionArray = message.sessions();

			for (final DDF_MarketSession session : sessionArray) {

				final DDF_Indicator indicator = session.getIndicator();

				log.debug("DDF_Indicator for session {} : {} ", session
						.getSession().name(), indicator.name());

				switch (indicator) {

					case CURRENT:
						visit(session, market, state == DDF_QuoteState.GOT_SETTLE);
						break;

					case PREVIOUS:
						visit(session, market, false);
						break;

					default:
						// log.error("@@@ unsupported indicator : {}", indicator);
						break;
				}

			}

		}

		/** Process top of book */
		{

			final DDF_MarketBookTop bookTop = message;
			visit(bookTop, market);

		}

		return null;

	}

	/**
	 * via xml quote.
	 *
	 * @param message the message
	 * @param market the market
	 * @return the void
	 */
	@Override
	public Void visit(final DDF_MarketSession message, final MarketDo market) {
		return visit(message, market, false);
	}

	public Void visit(final DDF_MarketSession message, final MarketDo market,
			final boolean forceSettle) {

		// ### process snapshot

		final DDF_MarketSnapshot snapshot = message;
		final DDF_Indicator indicator = message.getIndicator();

		visit(snapshot, market, indicator, forceSettle);

		return null;
	}

	@Override
	public Void visit(final DDF_EOD_Commodity message, final MarketDo market) {

		market.clearChanges();

		final MarketBarType type = market.ensureBar(message.getTradeDay().tradeDate());
		final MarketDoBar bar = market.loadBar(type.field);

		bar.set(MarketBarField.OPEN, message.getPriceOpen());
		bar.set(MarketBarField.HIGH, message.getPriceHigh());
		bar.set(MarketBarField.LOW, message.getPriceLow());
		/* Note Last =/= Close in some cases */
		bar.set(MarketBarField.CLOSE, message.getPriceLast());

		market.setBar(type, bar);

		return null;

	}

	@Override
	public Void visit(final DDF_EOD_EquityForex message, final MarketDo market) {

		market.clearChanges();

		final MarketBarType type = market.ensureBar(message.getTradeDay().tradeDate());
		final MarketDoBar bar = market.loadBar(type.field);

		bar.set(MarketBarField.OPEN, message.getPriceOpen());
		bar.set(MarketBarField.HIGH, message.getPriceHigh());
		bar.set(MarketBarField.LOW, message.getPriceLow());
		/* Note Last =/= Close in some cases */
		bar.set(MarketBarField.CLOSE, message.getPriceLast());

		bar.set(MarketBarField.VOLUME, message.getSizeVolume());

		market.setBar(type, bar);

		return null;

	}

	@Override
	public Void visit(final DDF_Prior_IndividCmdy message, final MarketDo market) {

		market.clearChanges();

		final MarketBarType type = market.ensureBar(message.getTradeDay().tradeDate());
		final MarketDoBar bar = market.loadBar(type.field);

		bar.set(MarketBarField.VOLUME, message.getSizeVolume());
		bar.set(MarketBarField.INTEREST, message.getSizeOpenInterest());

		market.setBar(type, bar);

		return null;

	}

	@Override
	public Void visit(final DDF_Prior_TotCmdy message, final MarketDo market) {

		// TODO No support for total volume/OI yet
		return null;

	}

	@Override
	public Void visit(final DDF_EOD_CommoditySpread message, final MarketDo market) {

		// TODO No support for EOD spreads yet
		return null;

	}

	/**
	 * via feed message 21, 22, 23, 24.
	 *
	 * @param message the message
	 * @param market the market
	 * @return the void
	 */
	@Override
	public Void visit(final DDF_MarketSnapshot message, final MarketDo market) {

		market.clearChanges();

		final TimeValue date = message.getTradeDay().tradeDate();
		final TimeValue time = message.getTime();

		/** Update top of book */
		{

			final PriceValue priceBid = message.getPriceBid();
			final PriceValue priceAsk = message.getPriceAsk();

			final MarketDoBookEntry entryBid = new DefBookEntry(
					MODIFY, Book.Side.BID,
					Book.Type.DEFAULT, ENTRY_TOP, priceBid, ValueConst.NULL_SIZE);
			final MarketDoBookEntry entryAsk = new DefBookEntry(
					MODIFY, Book.Side.ASK,
					Book.Type.DEFAULT, ENTRY_TOP, priceAsk, ValueConst.NULL_SIZE);

			applyTop(entryBid, time, market);
			applyTop(entryAsk, time, market);

		}

		final PriceValue settle = message.getPriceSettle();
		BooleanValue settled = null;

		// Check settled flag
		if (isClear(settle)) {
			settled = ValueConst.FALSE_BOOLEAN;
		} else if (!isEmpty(settle)) {
			settled = ValueConst.TRUE_BOOLEAN;
		}

		market.setSnapshot(date,
				message.getPriceOpen(),
				message.getPriceHigh(),
				message.getPriceLow(),
				message.getPriceLast(),
				settle,
				message.getPriceLastPrevious(),
				message.getSizeVolume(),
				message.getSizeInterest(),
				message.getVWAP(),
				settled,
				message.getTime());

		return null;

	}

	/**
	 * via xml quote >> xml session
	 */
	protected Void visit(
			final DDF_MarketSnapshot message,
			final MarketDo market, 
			final DDF_Indicator indicator) {
		
		return visit(message, market, indicator, false);
	}

	protected Void visit(
			final DDF_MarketSnapshot message,
			final MarketDo market, 
			final DDF_Indicator indicator,
			final boolean forceSettle) {

		market.clearChanges();

		final TimeValue date = message.getTradeDay().tradeDate();

		final PriceValue settle = message.getPriceSettle();
		BooleanValue settled = null;

		// Check settled flag
		if (settle == DDF_ClearVal.PRICE_CLEAR) {
			settled = ValueConst.FALSE_BOOLEAN;
		} else if (settle == DDF_NulVal.PRICE_EMPTY) {
		} else if (indicator == DDF_Indicator.PREVIOUS || forceSettle) {
			settled = ValueConst.TRUE_BOOLEAN;
		}
		
		market.setSnapshot(date,
				message.getPriceOpen(),
				message.getPriceHigh(),
				message.getPriceLow(),
				message.getPriceLast(),
				settle,
				message.getPriceLastPrevious(),
				message.getSizeVolume(),
				message.getSizeInterest(),
				message.getVWAP(),
				settled,
				message.getTime());

		/*
		 * If a previous update, set in bar current.
		 */
		/* Commented out to see if there are any cases where the current bar's previous value isn't correct 
		 * This most likely can be deleted if it doesn't cause problems elsewhere */
//		if (indicator == DDF_Indicator.PREVIOUS && !settle.isZero()) {
//			market.loadBar(MarketBarType.CURRENT.field).set(MarketBarField.CLOSE_PREVIOUS, settle);
//		}

		return null;
	}
	
	@Override
	public Void visit(final DDF_MarketTrade message, final MarketDo market) {
		
		market.clearChanges();

		final DDF_MessageType tradeType = message.getMessageType();

		final TimeValue time = message.getTime();
		final TimeValue date = message.getTradeDay().tradeDate();
		final DDF_Session ddfSession = message.getSession();

		PriceValue price = message.getPrice();
		SizeValue size = message.getSize();

		if (isClear(price) || isEmpty(price)) {
			price = ValueConst.NULL_PRICE;
			size = ValueConst.NULL_SIZE;
		}

		if (isClear(size) || isEmpty(size)) {
			size = ValueConst.NULL_SIZE;
		}

		switch (tradeType) {

			case TRADE:

				// message "27" : normal trade

				market.setTrade(ddfSession.type, ddfSession.session,
						ddfSession.sequencing, price, size, time, date);

				break;

			case TRADE_VOL:

				// message "2Z" : volume updates for stocks

				// Override sequencing since the feed translator seems to be
				// looking at sale conditions we don't have
				market.setTrade(ddfSession.type, ddfSession.session,
						MarketTradeSequencing.UNSEQUENCED_VOLUME, price, size, time, date);

				break;

			default:

				log.error("unsupported trade message type");
				break;

		}

		//

		return null;
	}

	// ##################################

	protected void applyTop(MarketDoBookEntry entry,
			final TimeValue time, final MarketDo market) {

		/* ddf signals by special price values */
		final PriceValue price = entry.priceValue();

		/* ",," a.k.a comma-comma; ddf value not provided */
		if (isEmpty(price)) {
			return;
		}

		/* ",-," a.k.a comma-dash-comma; ddf command : remove */
		if (isClear(price)) {
			entry = new DefBookEntry(
					MarketBookAction.REMOVE, entry.side(),
					Book.Type.DEFAULT, MarketBook.ENTRY_TOP,
					ValueConst.NULL_PRICE, ValueConst.NULL_SIZE);
		}

		market.setBookUpdate(entry, time);

	}

}
