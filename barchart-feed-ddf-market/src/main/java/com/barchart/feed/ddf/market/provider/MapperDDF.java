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
import static com.barchart.feed.base.book.enums.MarketBookSide.ASK;
import static com.barchart.feed.base.book.enums.MarketBookSide.BID;
import static com.barchart.feed.ddf.message.provider.DDF_MessageService.isClear;
import static com.barchart.feed.ddf.message.provider.DDF_MessageService.isEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.bar.api.MarketDoBar;
import com.barchart.feed.base.bar.enums.MarketBarField;
import com.barchart.feed.base.bar.enums.MarketBarType;
import com.barchart.feed.base.book.api.MarketBook;
import com.barchart.feed.base.book.api.MarketBookTop;
import com.barchart.feed.base.book.api.MarketDoBookEntry;
import com.barchart.feed.base.book.enums.MarketBookAction;
import com.barchart.feed.base.book.enums.MarketBookSide;
import com.barchart.feed.base.cuvol.api.MarketDoCuvolEntry;
import com.barchart.feed.base.market.api.MarketDo;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.base.provider.DefBookEntry;
import com.barchart.feed.base.state.enums.MarketStateEntry;
import com.barchart.feed.base.trade.enums.MarketTradeSession;
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
import com.barchart.feed.ddf.message.enums.DDF_Condition;
import com.barchart.feed.ddf.message.enums.DDF_Indicator;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.message.enums.DDF_ParamType;
import com.barchart.feed.ddf.message.enums.DDF_QuoteMode;
import com.barchart.feed.ddf.message.enums.DDF_QuoteState;
import com.barchart.feed.ddf.message.enums.DDF_Session;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;
import com.barchart.feed.inst.api.InstrumentField;
import com.barchart.feed.inst.enums.MarketBookType;
import com.barchart.util.anno.ThreadSafe;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.api.TimeValue;
import com.barchart.util.values.provider.ValueConst;

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

		final MarketDoBookEntry[] entries = message.entries();

		final TimeValue time = message.getTime();

		market.setBookSnapshot(entries, time);

		return null;
	}

	@Override
	public Void visit(final DDF_MarketBookTop message, final MarketDo market) {

		final TimeValue time = message.getTime();

		applyTop(message.entry(MarketBookSide.BID), time, market);

		applyTop(message.entry(MarketBookSide.ASK), time, market);

		return null;
	}

	@Override
	public Void visit(final DDF_MarketCondition message, final MarketDo market) {
		log.error("TODO : \n{}", message);
		return null;
	}

	@Override
	public Void visit(final DDF_MarketCuvol message, final MarketDo market) {

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
			log.error("wrong kind; treat as size : {}", kind);
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
			log.debug("Mapper saw TRADE_ASK_PRICE - Price=" + price.toString() + 
					" " + message.getSession() + " " + message.getTradeDay().toString() + " "
					+ message.getInstrument().get(InstrumentField.SYMBOL).toString());
			
			market.setTrade(ddfSession.type, ddfSession.session,
					ddfSession.sequencing, price, size, time, date);
			
			return null;
			
		case TRADE_BID_PRICE:  // NEW
			log.debug("Mapper saw TRADE_BID_PRICE - Price=" + price.toString() + 
					" " + message.getSession() + " " + message.getTradeDay().toString() + " "
					+ message.getInstrument().get(InstrumentField.SYMBOL).toString());
			
			market.setTrade(ddfSession.type, ddfSession.session,
					ddfSession.sequencing, price, size, time, date);
			
			return null;
		
		case TRADE_LAST_PRICE:
			market.setTrade(ddfSession.type, ddfSession.session,
					ddfSession.sequencing, price, size, time, date);
			return null;
			
		case ASK_LAST: // NEW
			log.debug("Mapper saw ASK_LAST - Price=" + price.toString() + 
					" " + message.getSession() + " " + message.getTradeDay().toString() + " "
					+ message.getInstrument().get(InstrumentField.SYMBOL).toString());
			
		case ASK_LAST_PRICE:
			final DefBookEntry topAskPrice = new DefBookEntry(MODIFY, ASK,
					MarketBookType.DEFAULT, ENTRY_TOP, price, top.side(ASK).size());
			applyTop(topAskPrice, time, market);
			return null;

		case ASK_LAST_SIZE:
			final DefBookEntry topAskSize = new DefBookEntry(MODIFY, ASK,
					MarketBookType.DEFAULT, ENTRY_TOP, top.side(ASK).price(), size);
			applyTop(topAskSize, time, market);
			return null;

		case BID_LAST: // NEW
			log.debug("Mapper saw BID_LAST - Price=" + price.toString() + 
					" " + message.getSession() + " " + message.getTradeDay().toString() + " "
					+ message.getInstrument().get(InstrumentField.SYMBOL).toString());
			
		case BID_LAST_PRICE:
			final DefBookEntry topBidPrice = new DefBookEntry(MODIFY, BID,
					MarketBookType.DEFAULT, ENTRY_TOP, price, top.side(BID).size());
			applyTop(topBidPrice, time, market);
			return null;

		case BID_LAST_SIZE:
			final DefBookEntry topBidSize = new DefBookEntry(MODIFY, BID,
					MarketBookType.DEFAULT, ENTRY_TOP, top.side(BID).price(), size);
			applyTop(topBidSize, time, market);
			return null;

		case CLOSE_LAST: // NEW
		case CLOSE_2_LAST: // NEW
		case CLOSE_ASK_PRICE:
		case CLOSE_BID_PRICE:
		case CLOSE_2_ASK_PRICE:
		case CLOSE_2_BID_PRICE:
			barCurrent.set(MarketBarField.CLOSE, price);
			barCurrent.set(MarketBarField.BAR_TIME, time);
			market.setBar(CURRENT, barCurrent);
			return null;

		case HIGH_LAST_PRICE:
		case HIGH_BID_PRICE:
		case YEAR_HIGH_PRICE:
			barCurrent.set(MarketBarField.HIGH, price);
			barCurrent.set(MarketBarField.BAR_TIME, time);
			market.setBar(CURRENT, barCurrent);
			return null;

		case LOW_LAST_PRICE:
		case LOW_ASK_PRICE:
		case YEAR_LOW_PRICE:
			barCurrent.set(MarketBarField.LOW, price);
			barCurrent.set(MarketBarField.BAR_TIME, time);
			market.setBar(CURRENT, barCurrent);
			return null;

		case VOLUME_LAST_SIZE:
			// TODO
			break;

		case VOLUME_PAST_SIZE:
			barPrevious.set(MarketBarField.VOLUME, size);
			barPrevious.set(MarketBarField.BAR_TIME, time);
			market.setBar(PREVIOUS, barPrevious);
			return null;

		case VOLUME_THIS_SIZE:
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
			//
			barCurrent.set(MarketBarField.OPEN, price);
			barCurrent.set(MarketBarField.HIGH, price);
			barCurrent.set(MarketBarField.LOW, price);
			barCurrent.set(MarketBarField.CLOSE, price);
			barCurrent.set(MarketBarField.INTEREST, size);
			barCurrent.set(MarketBarField.VOLUME, size);
			barCurrent.set(MarketBarField.BAR_TIME, time);
			market.setBar(CURRENT, barCurrent);
			//
			market.setState(MarketStateEntry.IS_SETTLED, false);
			//
			return null;

		case INTEREST_LAST_SIZE:
			barCurrent.set(MarketBarField.INTEREST, size);
			barCurrent.set(MarketBarField.BAR_TIME, time);
			market.setBar(CURRENT, barCurrent);
			return null;

		case INTEREST_PAST_SIZE:
			barPrevious.set(MarketBarField.INTEREST, size);
			barPrevious.set(MarketBarField.BAR_TIME, time);
			market.setBar(PREVIOUS, barPrevious);

			return null;

		case PREVIOUS_LAST_PRICE:
			barPrevious.set(MarketBarField.CLOSE, price);
			barPrevious.set(MarketBarField.BAR_TIME, time);
			market.setBar(PREVIOUS, barPrevious);
			return null;

			/** only "final" sets the flag */
		case SETTLE_FINAL_PRICE:
			//log.debug("Set State IS_SETTLED true inside VISIT MARKET PARAMETER, SETTLE_FINAL_PRICE");
			market.setState(MarketStateEntry.IS_SETTLED, true);
			/** falls through */

			/** "early" does NOT set the flag */
		case SETTLE_EARLY_PRICE:
			//log.debug("Set prelim settle value but not IS_SETTLED flag inside VISIT MARKET PARAMETER, SETTLE_EARLY_PRICE");
			barCurrent.set(MarketBarField.SETTLE, price);
			barCurrent.set(MarketBarField.BAR_TIME, time);
			market.setBar(CURRENT, barCurrent);
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

		// ### process quote

		// TODO part of instrument; should update definition?
		final TextValue symbolName = message.getSymbolName();
		final PriceValue priceStep = message.getPriceStep();
		final PriceValue pointValue = message.getPointValue();

		// TODO add more complete flag support?
		final DDF_Condition condition = message.getCondition();
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
				break;
			case REALTIME:
				market.setState(MarketStateEntry.IS_PUBLISH_REALTIME, true);
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
				case PREVIOUS:
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
			final DDF_MarketBookTop bookTop = message;
			visit(bookTop, market);
		}

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

		// ### process session

		// TODO are these trade fields?
		final SizeValue sizeLast = message.getSizeLast();
		final TimeValue timeLast = message.getTimeLast();

		// ### process snapshot

		final DDF_MarketSnapshot snapshot = message;
		final DDF_Indicator indicator = message.getIndicator();

		visit(snapshot, market, indicator);

		return null;
	}

	@Override
	public Void visit(final DDF_EOD_Commodity message, final MarketDo market) {

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

		final MarketDoBar newBar = market.loadBar(MarketField.BAR_PREVIOUS);

		newBar.set(MarketBarField.VOLUME, message.getSizeVolume());
		newBar.set(MarketBarField.INTEREST, message.getSizeOpenInterest());

		market.setBar(MarketBarType.PREVIOUS, newBar);

		return null;
	}

	/**
	 * via feed message 21.
	 * 
	 * @param message
	 *            the message
	 * @param market
	 *            the market
	 * @return the void
	 */
	@Override
	public Void visit(final DDF_MarketSnapshot message, final MarketDo market) {

		final TimeValue time = message.getTime();

		/** Update SETTLE State */
		{

			final PriceValue priceSettle = message.getPriceSettle();

			if (isClear(priceSettle)) {
				// ",-, " : means remove old value
				//log.debug("Set State IS_SETTLED false inside visit MarketSnapshot because priceSettle is clear");
				market.setState(MarketStateEntry.IS_SETTLED, false);
			} else if (isEmpty(priceSettle)) {
				// ",," : means leave alone
				// no change of current value
			} else {
				// ",12345," : means replace with new value
				//log.debug("Set State IS_SETTLED true inside visit MarketSnapshot because priceSettle is not empty");
				market.setState(MarketStateEntry.IS_SETTLED, true);
			}

		}

		/** Update top of book */
		{

			final PriceValue priceBid = message.getPriceBid();
			final PriceValue priceAsk = message.getPriceAsk();

			/** XXX note: {@link MarketBook#ENTRY_TOP} */

			final MarketDoBookEntry entryBid = new DefBookEntry(MODIFY, BID,
					MarketBookType.DEFAULT, ENTRY_TOP, priceBid, ValueConst.NULL_SIZE);
			final MarketDoBookEntry entryAsk = new DefBookEntry(MODIFY, ASK,
					MarketBookType.DEFAULT, ENTRY_TOP, priceAsk, ValueConst.NULL_SIZE);

			applyTop(entryBid, time, market);
			applyTop(entryAsk, time, market);

		}

		/** Update CURRENT bar */
		{

			final MarketBarType type = CURRENT;

			final MarketDoBar bar = market.loadBar(type.field);

			final DDF_TradeDay tradeDay = message.getTradeDay();
			bar.set(MarketBarField.TRADE_DATE, tradeDay.tradeDate());

			final PriceValue priceOpen = message.getPriceOpen();
			final PriceValue priceHigh = message.getPriceHigh();
			final PriceValue priceLow = message.getPriceLow();
			final PriceValue priceClose = message.getPriceLast(); // XXX
			final PriceValue priceSettle = message.getPriceSettle();
			final SizeValue sizeVolume = message.getSizeVolume();

			applyBar(bar, MarketBarField.OPEN, priceOpen);
			applyBar(bar, MarketBarField.HIGH, priceHigh);
			applyBar(bar, MarketBarField.LOW, priceLow);
			applyBar(bar, MarketBarField.CLOSE, priceClose);
			applyBar(bar, MarketBarField.SETTLE, priceSettle);
			applyBar(bar, MarketBarField.VOLUME, sizeVolume);

			bar.set(MarketBarField.BAR_TIME, time);

			market.setBar(type, bar);

		}

		/** Update PREVIOUS bar */
		{

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

		}

		return null;
	}

	/**
	 * via xml quote >> xml session
	 */
	protected Void visit(final DDF_MarketSnapshot message,
			final MarketDo market, final DDF_Indicator indicator) {

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
		final PriceValue priceSettle = message.getPriceSettle();
		final SizeValue sizeVolume = message.getSizeVolume();
		final SizeValue sizeInterest = message.getSizeInterest();

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

		return null;
	}

	@Override
	public Void visit(final DDF_MarketTrade message, final MarketDo market) {

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
		final PriceValue price = entry.price();

		/* ",," a.k.a comma-comma; ddf value not provided */
		if (isEmpty(price)) {
			return;
		}

		/* ",-," a.k.a comma-dash-comma; ddf command : remove */
		if (isClear(price)) {
			entry = new DefBookEntry(MarketBookAction.REMOVE, entry.side(),
					MarketBookType.DEFAULT, MarketBook.ENTRY_TOP,
					ValueConst.NULL_PRICE, ValueConst.NULL_SIZE);
		}

		market.setBookUpdate(entry, time);

	}

}
