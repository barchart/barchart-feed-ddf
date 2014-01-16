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
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Market.Component;
import com.barchart.feed.api.model.data.Market.LastPrice;
import com.barchart.feed.api.model.data.Market.LastPrice.Source;
import com.barchart.feed.api.model.data.Session.Type;
import com.barchart.feed.api.model.data.SessionData;
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
import com.barchart.feed.base.provider.ValueConverter;
import com.barchart.feed.base.state.enums.MarketStateEntry;
import com.barchart.feed.base.trade.enums.MarketTradeSession;
import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.base.values.api.TimeValue;
import com.barchart.feed.base.values.provider.ValueConst;
import com.barchart.feed.ddf.message.api.DDF_ControlResponse;
import com.barchart.feed.ddf.message.api.DDF_ControlTimestamp;
import com.barchart.feed.ddf.message.api.DDF_EOD_Commodity;
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
import com.barchart.feed.ddf.message.enums.DDF_Indicator;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.message.enums.DDF_ParamType;
import com.barchart.feed.ddf.message.enums.DDF_QuoteMode;
import com.barchart.feed.ddf.message.enums.DDF_QuoteState;
import com.barchart.feed.ddf.message.enums.DDF_Session;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;
import com.barchart.util.common.anno.ThreadSafe;
import com.barchart.util.value.api.Price;

// TODO: Auto-generated Javadoc
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
		// not used
		return null;
	}

	@Override
	public Void visit(final DDF_MarketBook message, final MarketDo market) {

		/* Update changed comonents */
		market.clearChanges();
		market.setChange(Component.BOOK_COMBINED);
		
		final MarketDoBookEntry[] entries = message.entries();

		final TimeValue time = message.getTime();

		market.setBookSnapshot(entries, time);
		
		return null;
	}

	@Override
	public Void visit(final DDF_MarketBookTop message, final MarketDo market) {

		/* Update changed comonents */
		market.clearChanges();
		market.setChange(Component.BOOK_COMBINED);
		
		final TimeValue time = message.getTime();

		applyTop(message.entry(market.instrument(), Book.Side.BID), time, market);

		applyTop(message.entry(market.instrument(), Book.Side.ASK), time, market);

		return null;
	}

	@Override
	public Void visit(final DDF_MarketCondition message, final MarketDo market) {
		log.error("TODO : \n{}", message);
		return null;
	}

	@Override
	public Void visit(final DDF_MarketCuvol message, final MarketDo market) {

		/* Update changed comonents */
		market.clearChanges();
		market.setChange(Component.CUVOL);
		
		final MarketDoCuvolEntry[] entries = message.entries();

		final TimeValue time = message.getTime();

		market.setCuvolSnapshot(entries, time);
		
		return null;
	}

	@Override
	public Void visit(final DDF_MarketParameter message, final MarketDo market) {

		final DDF_ParamType param = message.getParamType();

		final TimeValue time = message.getTime();
		
		final TimeValue date = message.getTradeDay().tradeDate();

		final DDF_ParamType.Kind kind = param.kind;

		final PriceValue price;
		final SizeValue size;

		switch (kind) {
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

		final DDF_Session ddfSession = message.getSession();
		
		//

		final MarketBookTop top = market.get(MarketField.BOOK_TOP);
		final MarketDoBar barCurrent = market.loadBar(CURRENT.field);
		final MarketDoBar barPrevious = market.loadBar(PREVIOUS.field);

		//
		switch (param) {
		
		case TRADE_ASK_PRICE:  // NEW
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.TRADE);
			
			market.setTrade(ddfSession.type, ddfSession.session,
					ddfSession.sequencing, price, size, time, date);

			return null;
			
		case TRADE_BID_PRICE:  // NEW
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.TRADE);
			
			market.setTrade(ddfSession.type, ddfSession.session,
					ddfSession.sequencing, price, size, time, date);
			
			return null;
		
		case TRADE_LAST_PRICE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.TRADE);
			
			market.setTrade(ddfSession.type, ddfSession.session,
					ddfSession.sequencing, price, size, time, date);
			
			return null;
			
		case ASK_LAST: // NEW
			
		case ASK_LAST_PRICE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.BOOK_COMBINED);
			
			final DefBookEntry topAskPrice = new DefBookEntry(
					MODIFY, Book.Side.ASK,
					Book.Type.DEFAULT, ENTRY_TOP, price, top.side(Book.Side.ASK).sizeValue());
			applyTop(topAskPrice, time, market);
			
			return null;

		case ASK_LAST_SIZE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.BOOK_COMBINED);
			
			final DefBookEntry topAskSize = new DefBookEntry(
					MODIFY, Book.Side.ASK,
					Book.Type.DEFAULT, ENTRY_TOP, top.side(Book.Side.ASK).priceValue(), size);
			applyTop(topAskSize, time, market);
			
			return null;

		case BID_LAST: // NEW
			
		case BID_LAST_PRICE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.BOOK_COMBINED);
			
			final DefBookEntry topBidPrice = new DefBookEntry(
					MODIFY, Book.Side.BID,
					Book.Type.DEFAULT, ENTRY_TOP, price, top.side(Book.Side.BID).sizeValue());
			applyTop(topBidPrice, time, market);
			
			return null;

		case BID_LAST_SIZE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.BOOK_COMBINED);
			
			final DefBookEntry topBidSize = new DefBookEntry(
					MODIFY, Book.Side.BID,
					Book.Type.DEFAULT, ENTRY_TOP, top.side(Book.Side.BID).priceValue(), size);
			applyTop(topBidSize, time, market);
			
			return null;

		case CLOSE_LAST: // NEW
		case CLOSE_2_LAST: // NEW
		case CLOSE_ASK_PRICE:
		case CLOSE_BID_PRICE:
		case CLOSE_2_ASK_PRICE:
		case CLOSE_2_BID_PRICE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.DEFAULT_CURRENT);
			
			barCurrent.set(MarketBarField.CLOSE, price);
			barCurrent.set(MarketBarField.BAR_TIME, time);
			market.setBar(CURRENT, barCurrent);
			
			return null;

		case HIGH_LAST_PRICE:
		case HIGH_BID_PRICE:
		case YEAR_HIGH_PRICE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.DEFAULT_CURRENT);
			
			barCurrent.set(MarketBarField.HIGH, price);
			barCurrent.set(MarketBarField.BAR_TIME, time);
			market.setBar(CURRENT, barCurrent);
			
			return null;

		case LOW_LAST_PRICE:
		case LOW_ASK_PRICE:
		case YEAR_LOW_PRICE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.DEFAULT_CURRENT);
			
			barCurrent.set(MarketBarField.LOW, price);
			barCurrent.set(MarketBarField.BAR_TIME, time);
			market.setBar(CURRENT, barCurrent);
			
			return null;

		case VOLUME_LAST_SIZE:
			// TODO
			break;

		case VOLUME_PAST_SIZE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.DEFAULT_PREVIOUS);
			
			barPrevious.set(MarketBarField.VOLUME, size);
			barPrevious.set(MarketBarField.BAR_TIME, time);
			market.setBar(PREVIOUS, barPrevious);
			
			return null;

		case VOLUME_THIS_SIZE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.DEFAULT_CURRENT);
			
			barCurrent.set(MarketBarField.VOLUME, size);
			barCurrent.set(MarketBarField.BAR_TIME, time);
			market.setBar(CURRENT, barCurrent);
			
			return null;

		case OPEN_LAST_PRICE:
		case OPEN_ASK_PRICE:
		case OPEN_BID_PRICE:
		case OPEN_2_LAST_PRICE:
		case OPEN_2_ASK_PRICE:
		case OPEN_2_BID_PRICE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.DEFAULT_CURRENT);
			
			//
			barCurrent.set(MarketBarField.OPEN, price);
			barCurrent.set(MarketBarField.HIGH, price);
			barCurrent.set(MarketBarField.LOW, price);
			barCurrent.set(MarketBarField.CLOSE, price);
			barCurrent.set(MarketBarField.INTEREST, size);
			barCurrent.set(MarketBarField.VOLUME, size);
			barCurrent.set(MarketBarField.BAR_TIME, time);
			//barCurrent.set(MarketBarField.SETTLE, ValueConst.NULL_PRICE);  // Test
			market.setBar(CURRENT, barCurrent);
			//
			market.setState(MarketStateEntry.IS_SETTLED, false);
			//
			
			return null;

		case INTEREST_LAST_SIZE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.DEFAULT_CURRENT);
			
			barCurrent.set(MarketBarField.INTEREST, size);
			barCurrent.set(MarketBarField.BAR_TIME, time);
			market.setBar(CURRENT, barCurrent);
			
			return null;

		case INTEREST_PAST_SIZE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.DEFAULT_PREVIOUS);
			
			barPrevious.set(MarketBarField.INTEREST, size);
			barPrevious.set(MarketBarField.BAR_TIME, time);
			market.setBar(PREVIOUS, barPrevious);

			return null;

		case PREVIOUS_LAST_PRICE:
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.DEFAULT_PREVIOUS);
			
			barPrevious.set(MarketBarField.CLOSE, price);
			barPrevious.set(MarketBarField.BAR_TIME, time);
			market.setBar(PREVIOUS, barPrevious);
			
			return null;

			/** only "final" sets the flag */
		case SETTLE_FINAL_PRICE:
			//log.debug("Set State IS_SETTLED true inside VISIT MARKET PARAMETER, SETTLE_FINAL_PRICE");
			/** falls through */

			/** "early" does NOT set the flag */
		case SETTLE_EARLY_PRICE:
			
			final DDF_TradeDay tradeDay = message.getTradeDay();
			final DDF_TradeDay curDay = DDF_TradeDay.fromMillisUTC(
					barCurrent.get(MarketBarField.TRADE_DATE).asMillisUTC());
			
			if(curDay.equals(tradeDay) && param == DDF_ParamType.SETTLE_EARLY_PRICE) {
				market.setState(MarketStateEntry.IS_SETTLED, true);
			}
			
			//log.debug("PARAM Message = {} Current = {}", tradeDay, curDay);
			
			/* Update changed comonents */
			market.clearChanges();
			market.setChange(Component.DEFAULT_CURRENT);
			
			//log.debug("Set prelim settle value but not IS_SETTLED flag inside VISIT MARKET PARAMETER, SETTLE_EARLY_PRICE");
			if(curDay == tradeDay) {
				barCurrent.set(MarketBarField.SETTLE, price);
				barCurrent.set(MarketBarField.BAR_TIME, time);
			} else {
				final DDF_TradeDay prevDay = DDF_TradeDay.fromMillisUTC(
						barPrevious.get(MarketBarField.TRADE_DATE).asMillisUTC());
				if(prevDay == tradeDay) {
					barPrevious.set(MarketBarField.SETTLE, price);
					barPrevious.set(MarketBarField.BAR_TIME, time);
				}
			}
			
			/* Update last price to settle price */
			market.setLastPrice(new LastPriceImpl(LastPrice.Source.SETTLE, ValueConverter.price(price)));
			
			return null;

		default:
			break;
		}

		log.debug("@@@ TODO : {} \n{}", param, message.getTime().toString() + " " + message);

		return null;

	}

	/**
	 * via feed XQ snapshot or web URL lookup.
	 * 
	 * @param message
	 *            the message
	 * @param market
	 *            the market
	 * @return the void
	 */
	@Override
	public Void visit(final DDF_MarketQuote message, final MarketDo market) {

		market.clearChanges();
		// ### process quote

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

		/** Handle settle state */
		{
			switch (state) {
			case GOT_CLOSE:
				break;
			case GOT_SETTLE:
				log.debug("Set State IS_SETTLED true inside visit MarketQuote by GOT_SETTLE state");
				market.setState(MarketStateEntry.IS_SETTLED, true);
				break;
			case PRE_MARKET:
				log.debug("Set State IS_SETTLED false inside visit MarketQuote by PRE_MARKET state");
				market.setState(MarketStateEntry.IS_SETTLED, false);
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
					
					/* Update changed comonents */
					market.setChange(Component.DEFAULT_CURRENT);
					
					visit(session, market);
					
					break;
				case PREVIOUS:
					
					/* Update changed comonents */
					market.setChange(Component.DEFAULT_PREVIOUS);
					
					visit(session, market);
					
					break;
				default:
					log.error("@@@ wrong indicator : {}", indicator);
					break;
				}
			}
		}

		/** Process top of book */
		{
			
			/* Update changed comonents */
			market.setChange(Component.BOOK_COMBINED);
			
			final DDF_MarketBookTop bookTop = message;
			visit(bookTop, market);
			
		}
		
		// 
		updateLastPrice(market, message.toString());

		return null;
	}

	/**
	 * via xml quote.
	 * 
	 * @param message
	 *            the message
	 * @param market
	 *            the market
	 * @return the void
	 */
	@Override
	public Void visit(final DDF_MarketSession message, final MarketDo market) {

		// ### process snapshot

		final DDF_MarketSnapshot snapshot = message;
		final DDF_Indicator indicator = message.getIndicator();

		visit(snapshot, market, indicator);

		return null;
	}

	@Override
	public Void visit(final DDF_EOD_Commodity message, final MarketDo market) {

		/* Update changed comonents */
		market.clearChanges();
		market.setChange(Component.DEFAULT_PREVIOUS);
		
		final MarketDoBar newBar = market.loadBar(MarketField.BAR_PREVIOUS);

		newBar.set(MarketBarField.OPEN, message.getPriceOpen());
		newBar.set(MarketBarField.HIGH, message.getPriceHigh());
		newBar.set(MarketBarField.LOW, message.getPriceLow());
		/* Note Last =/= Close in some cases */
		newBar.set(MarketBarField.CLOSE, message.getPriceLast());

		market.setBar(MarketBarType.PREVIOUS, newBar);

		return null;
	}

	@Override
	public Void visit(final DDF_EOD_EquityForex message, final MarketDo market) {
		
		/* Update changed comonents */
		market.clearChanges();
		market.setChange(Component.DEFAULT_PREVIOUS);
		
		final MarketDoBar newBar = market.loadBar(MarketField.BAR_PREVIOUS);

		newBar.set(MarketBarField.OPEN, message.getPriceOpen());
		newBar.set(MarketBarField.HIGH, message.getPriceHigh());
		newBar.set(MarketBarField.LOW, message.getPriceLow());
		/* Note Last =/= Close in some cases */
		newBar.set(MarketBarField.CLOSE, message.getPriceLast());

		newBar.set(MarketBarField.VOLUME, message.getSizeVolume());

		market.setBar(MarketBarType.PREVIOUS, newBar);
		
		return null;
	}

	@Override
	public Void visit(final DDF_Prior_IndividCmdy message, final MarketDo market) {

		/* Update changed comonents */
		market.clearChanges();
		market.setChange(Component.DEFAULT_PREVIOUS);
		
		final MarketDoBar newBar = market.loadBar(MarketField.BAR_PREVIOUS);

		newBar.set(MarketBarField.VOLUME, message.getSizeVolume());
		newBar.set(MarketBarField.INTEREST, message.getSizeOpenInterest());

		market.setBar(MarketBarType.PREVIOUS, newBar);
		
		return null;
	}

	/**
	 * via feed message 21, 22, 23, 24.
	 * 
	 * @param message
	 *            the message
	 * @param market
	 *            the market
	 * @return the void
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Void visit(final DDF_MarketSnapshot message, final MarketDo market) {

		market.clearChanges();
		
		final TimeValue time = message.getTime();
		
		/** Update PREVIOUS bar */
		if(message.getMessageType() == DDF_MessageType.SNAP_BACK_PLUS_PREV) {
			
			/* Update changed comonents */
			market.setChange(Component.DEFAULT_PREVIOUS);
			
			final MarketBarType type = PREVIOUS;

			final MarketDoBar bar = market.loadBar(type.field);
			final PriceValue priceOpen = message.getPriceOpen();
			final PriceValue priceHigh = message.getPriceHigh();
			final PriceValue priceLow = message.getPriceLow();
			final PriceValue priceClose = message.getPriceLastPrevious();
			final PriceValue priceSettle = message.getPriceSettle();
			final SizeValue sizeVolume = message.getSizeVolumePrevious();
			final SizeValue sizeInterest = message.getSizeInterest(); // XXX

			applyBar(bar, MarketBarField.OPEN, priceOpen);
			applyBar(bar, MarketBarField.HIGH, priceHigh);
			applyBar(bar, MarketBarField.LOW, priceLow);
			applyBar(bar, MarketBarField.CLOSE, priceClose);
			applyBar(bar, MarketBarField.SETTLE, priceSettle);
			applyBar(bar, MarketBarField.VOLUME, sizeVolume);
			applyBar(bar, MarketBarField.INTEREST, sizeInterest);

			bar.set(MarketBarField.BAR_TIME, time);

			market.setBar(type, bar);
			return null;
			
		}
		
		/* Update changed comonents */
		market.setChange(Component.DEFAULT_CURRENT);
		final MarketBarType type = CURRENT;
		final MarketDoBar bar = market.loadBar(type.field);


		/** Update SETTLE State */
		{
			final DDF_TradeDay tradeDay = message.getTradeDay();
			
			final DDF_TradeDay curDay = DDF_TradeDay.fromMillisUTC(
					bar.get(MarketBarField.TRADE_DATE).asMillisUTC());

			final PriceValue priceSettle = message.getPriceSettle();

			if (isClear(priceSettle)) {
				// ",-, " : means remove old value
				//log.debug("Set State IS_SETTLED false inside visit MarketSnapshot because priceSettle is clear");
				market.setState(MarketStateEntry.IS_SETTLED, false);
				applyBar(bar, MarketBarField.SETTLE, ValueConst.NULL_PRICE);
			} else if (isEmpty(priceSettle)) {
				// ",," : means leave alone
				// no change of current value
			} else {
				// ",12345," : means replace with new value
				//log.debug("Set State IS_SETTLED true inside visit MarketSnapshot because priceSettle is not empty");
				market.setState(MarketStateEntry.IS_SETTLED, true);
				applyBar(bar, MarketBarField.SETTLE, message.getPriceSettle().freeze());
				log.debug("SNAPSHOT Message = {} Current = {}", tradeDay, curDay);
			}

		}

		/** Update top of book */
		{

			/* Update changed comonents */
			market.setChange(Component.BOOK_COMBINED);
			
			final PriceValue priceBid = message.getPriceBid();
			final PriceValue priceAsk = message.getPriceAsk();

			/** XXX note: {@link MarketBook#ENTRY_TOP} */

			final MarketDoBookEntry entryBid = new DefBookEntry(
					MODIFY, Book.Side.BID,
					Book.Type.DEFAULT, ENTRY_TOP, priceBid, ValueConst.NULL_SIZE);
			final MarketDoBookEntry entryAsk = new DefBookEntry(
					MODIFY, Book.Side.ASK,
					Book.Type.DEFAULT, ENTRY_TOP, priceAsk, ValueConst.NULL_SIZE);

			applyTop(entryBid, time, market);
			applyTop(entryAsk, time, market);
			
		}

		/** Update CURRENT bar */
		{

			final DDF_TradeDay tradeDay = message.getTradeDay();
			
			final DDF_TradeDay curDay = DDF_TradeDay.fromMillisUTC(
					bar.get(MarketBarField.TRADE_DATE).asMillisUTC());
			
			/* Check for new Day Code */
			if(curDay.ord() < tradeDay.ord() || 
					(tradeDay.ord() < curDay.ord() && tradeDay == DDF_TradeDay.D01)) {
				
				//log.debug("Current bar date not equal to trade date, resetting flag {} {} {}\n",
				//		market.instrument().symbol(), message.toStringFields(), message.toString());
//				log.debug("Bar Day Code = {} Message Day Code = {}", curDay, tradeDay);
				market.setState(MarketStateEntry.IS_SETTLED, false);
				
				/* Set trade date */
				bar.set(MarketBarField.TRADE_DATE, tradeDay.tradeDate());
				
				/* Roll current session to previous */
				final MarketDoBar prev = market.loadBar(MarketBarType.PREVIOUS.field);
				applyBar(prev, MarketBarField.OPEN, bar.get(MarketBarField.OPEN).freeze());
				applyBar(prev, MarketBarField.HIGH, bar.get(MarketBarField.HIGH).freeze());
				applyBar(prev, MarketBarField.LOW, bar.get(MarketBarField.LOW).freeze());
				applyBar(prev, MarketBarField.CLOSE, bar.get(MarketBarField.CLOSE).freeze());
				applyBar(prev, MarketBarField.SETTLE, bar.get(MarketBarField.SETTLE).freeze());
				applyBar(prev, MarketBarField.VOLUME, bar.get(MarketBarField.VOLUME).freeze());
				market.setBar(MarketBarType.PREVIOUS, prev);
				
				/* Clear current bar */
				applyBar(bar, MarketBarField.OPEN, ValueConst.NULL_PRICE);
				applyBar(bar, MarketBarField.HIGH, ValueConst.NULL_PRICE);
				applyBar(bar, MarketBarField.LOW, ValueConst.NULL_PRICE);
				applyBar(bar, MarketBarField.CLOSE, ValueConst.NULL_PRICE);
				applyBar(bar, MarketBarField.SETTLE, ValueConst.NULL_PRICE);
				applyBar(bar, MarketBarField.VOLUME, ValueConst.NULL_SIZE);
				
			}
			
			final PriceValue priceOpen = message.getPriceOpen();
			final PriceValue priceHigh = message.getPriceHigh();
			final PriceValue priceLow = message.getPriceLow();
			final PriceValue priceClose = message.getPriceLast(); // XXX
			final SizeValue sizeVolume = message.getSizeVolume();
			
			if(!market.get(MarketField.STATE).contains(MarketStateEntry.IS_SETTLED)) {
				applyBar(bar, MarketBarField.SETTLE, ValueConst.NULL_PRICE);
			}

			applyBar(bar, MarketBarField.OPEN, priceOpen);
			applyBar(bar, MarketBarField.HIGH, priceHigh);
			applyBar(bar, MarketBarField.LOW, priceLow);
			applyBar(bar, MarketBarField.CLOSE, priceClose);
			applyBar(bar, MarketBarField.VOLUME, sizeVolume);

			bar.set(MarketBarField.BAR_TIME, time);

			market.setBar(type, bar);
			
		}

		// 
		updateLastPrice(market, message.toString());
		
		return null;
	}

	/**
	 * via xml quote >> xml session
	 */
	protected Void visit(final DDF_MarketSnapshot message,
			final MarketDo market, final DDF_Indicator indicator) {

		/* Update changed comonents */
		market.clearChanges();
		market.setChange(Component.DEFAULT_CURRENT);
		market.setChange(Component.DEFAULT_PREVIOUS);
		
		final DDF_Session session = message.getSession();

		final MarketBarType type = barTypeFrom(indicator, session);

		final MarketDoBar bar = market.loadBar(type.field);

		//
		final DDF_TradeDay tradeDay = message.getTradeDay();
		bar.set(MarketBarField.TRADE_DATE, tradeDay.tradeDate());
		//

		// extract

		final PriceValue priceOpen = message.getPriceOpen();
		final PriceValue priceHigh = message.getPriceHigh();
		final PriceValue priceLow = message.getPriceLow();
		final PriceValue priceClose = message.getPriceLast(); // XXX note: LAST
		final SizeValue sizeVolume = message.getSizeVolume();
		final SizeValue sizeInterest = message.getSizeInterest();

		PriceValue priceSettle = ValueConst.NULL_PRICE;
		if(market.get(MarketField.STATE).contains(MarketStateEntry.IS_SETTLED)) {
			priceSettle = message.getPriceSettle();
		}
		
		// apply

		applyBar(bar, MarketBarField.OPEN, priceOpen);
		applyBar(bar, MarketBarField.HIGH, priceHigh);
		applyBar(bar, MarketBarField.LOW, priceLow);
		applyBar(bar, MarketBarField.CLOSE, priceClose);
		applyBar(bar, MarketBarField.SETTLE, priceSettle);
		applyBar(bar, MarketBarField.VOLUME, sizeVolume);
		applyBar(bar, MarketBarField.INTEREST, sizeInterest);

		//

		bar.set(MarketBarField.BAR_TIME, message.getTime());

		market.setBar(type, bar);
		
		/*
		 * If a previous update, set in bar current.
		 */
		if(type == MarketBarType.PREVIOUS) {
			
			market.loadBar(MarketBarType.CURRENT.field).set(
					MarketBarField.SETTLE_PREVIOUS, priceSettle);
			
		}
		
		// 
		updateLastPrice(market, message.toString());
		
		return null;
	}

	@Override
	public Void visit(final DDF_MarketTrade message, final MarketDo market) {

		/* Update changed comonents */
		market.clearChanges();
		market.setChange(Component.TRADE);
		
		final DDF_MessageType tradeType = message.getMessageType();

		switch (tradeType) {
		case TRADE: {

			// message "27" : normal trade

			PriceValue price = message.getPrice();
			SizeValue size = message.getSize();

			// TODO review contract on how to clean partial values
			if (isClear(price) || isEmpty(price)) {
				price = ValueConst.NULL_PRICE;
				size = ValueConst.NULL_SIZE;
			}

			// TODO review contract on how to clean partial values
			if (isClear(size) || isEmpty(size)) {
				size = ValueConst.NULL_SIZE;
			}

			final DDF_Session ddfSession = message.getSession();
			final TimeValue time = message.getTime();
			final TimeValue date = message.getTradeDay().tradeDate();

			market.setTrade(ddfSession.type, ddfSession.session,
					ddfSession.sequencing, price, size, time, date);

			market.setLastPrice(new LastPriceImpl(Source.LAST_TRADE, 
					ValueConverter.price(price)));
		}
			break;

		case TRADE_VOL: {

			// message "2Z" : "do not know what to do", ignore for now

		}
			break;

		default:
			log.error("unsupported trade message type");
			break;

		}
		
		// 
		
		return null;
	}

	// ##################################

	protected void applyBar(final MarketDoBar bar,
			final MarketBarField<PriceValue> field, final PriceValue value) {

		if (isEmpty(value)) {
			// no change in market field value
			return;
		}

		if (isClear(value)) {
			// NULL_PRICE should be rendered as "price value not available"
			bar.set(field, ValueConst.NULL_PRICE);
			return;
		}

		bar.set(field, value);

	}

	protected void applyBar(final MarketDoBar bar,
			final MarketBarField<SizeValue> field, final SizeValue value) {

		if (isEmpty(value)) {
			// no change in market field value
			return;
		}

		if (isClear(value)) {
			// NULL_SIZE should be rendered as "size value not available"
			bar.set(field, ValueConst.NULL_SIZE);
			return;
		}

		bar.set(field, value);

	}

	/** currently does not differentiate between sources */
	protected MarketBarType barTypeFromCurrent(final DDF_Session session) {

		final MarketTradeSession tradeSession = session.session;

		switch (tradeSession) {

		case EXTENDED:
			return MarketBarType.CURRENT_EXT;
		default:
			return MarketBarType.CURRENT;
		}

	}

	/** currently only differentiate THIS day vs PAST day market */
	protected MarketBarType barTypeFrom(final DDF_Indicator indicator,
			final DDF_Session session) {

		switch (indicator) {
		default:
			log.error("wrong indicator : {}", indicator);
		case CURRENT:
			return barTypeFromCurrent(session);
		case PREVIOUS:
			return MarketBarType.PREVIOUS;
		}

	}

	protected void applyTop(/* local */MarketDoBookEntry entry,
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
	
	private void updateLastPrice(final MarketDo market, final String message) {
		
		/* SETTLE */
		Price price = market.session().settle();
		if(!price.isNull()) {
			//log.debug("Upated Last Price From Settle=" + price.toString());
			market.setLastPrice(new LastPriceImpl(Source.SETTLE, price));
			return;
		}
		
		/* TRADE because we don't actually have close messages */
		price = market.trade().price();
		if(!price.isNull()) {
			//log.debug("Updated Last Price From Trade=" + price.toString());
			market.setLastPrice(new LastPriceImpl(Source.LAST_TRADE, price));
			return;
		}
		
		/* CLOSE after TRADE because of snapshots give last price */ 
		price = market.session().close();
		if(!price.isNull()) {
			//log.trace("Updated Last Price From Close=" + price.toString());
			market.setLastPrice(new LastPriceImpl(Source.LAST_TRADE, price));
			return;
		}

		/* PREV CLOSE */
		SessionData session = market.sessionSet().session(Type.DEFAULT_PREVIOUS);
		if(session != null) {
			price = session.settle();
			if(!price.isNull()) {
				//log.debug("Updated Last Price From Previous=" + price.toString());
				market.setLastPrice(new LastPriceImpl(Source.PREV_SETTLE, price));
				return;
			}
		}
		
		if(price.isNull()) {
			market.setLastPrice(LastPrice.NULL);
		}
		
	}
	
	private class LastPriceImpl implements Market.LastPrice {

		private final Source source; 
		private final Price price;
		
		public LastPriceImpl(final Source source, final Price price) {
			this.source = source;
			this.price = price;
		}
		
		@Override
		public Source source() {
			return source;
		}

		@Override
		public Price price() {
			return price;
		}
		
	}

}
