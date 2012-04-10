/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.feed.ddf.message.provider.CodecHelper.*;
import static com.barchart.util.ascii.ASCII.*;

import java.nio.ByteBuffer;

import org.joda.time.DateTimeZone;

import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.message.enums.DDF_Session;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;
import com.barchart.feed.ddf.symbol.api.DDF_Symbol;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_SpreadType;
import com.barchart.feed.ddf.symbol.provider.DDF_SymbolService;
import com.barchart.feed.ddf.symbol.provider.DDF_Symbology;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.util.ascii.ASCII;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.provider.ValueBuilder;

abstract class BaseMarket extends Base implements DDF_MarketBase {

	BaseMarket() {
		//
	}

	BaseMarket(final DDF_MessageType messageType) {
		setMessageType(messageType);
	}

	// //////////////////////////////////////

	private byte ordExchange = DDF_Exchange.UNKNOWN.ord;
	private byte ordFraction = DDF_Fraction.UNKNOWN.ord;
	private byte ordTradeDay = DDF_TradeDay.UNKNOWN.ord;
	private byte ordSession = DDF_Session.UNKNOWN.ord;
	private byte ordSpread = DDF_SpreadType.UNKNOWN.ord;

	protected byte delay = DDF_NO_DELAY;

	protected byte[][] symbolArray;

	// //////////////////////////////////////

	// NOTE: invokes resolver
	@Override
	public final DDF_Instrument getInstrument() {
		return DDF_InstrumentProvider.findDDF(getId());
	}

	// NOTE: invokes parser
	@Override
	public final DDF_Symbol getSymbol() {
		return DDF_SymbolService.find(getId());
	}

	//

	@Override
	public final int getDelay() {
		return delay;
	}

	@Override
	public final DDF_Fraction getFraction() {
		return DDF_Fraction.fromOrd(ordFraction);
	}

	protected final void setFraction(final DDF_Fraction frac) {
		ordFraction = frac.ord;
	}

	@Override
	public final DDF_Exchange getExchange() {
		return DDF_Exchange.fromOrd(ordExchange);
	}

	protected final void setExchange(final DDF_Exchange exchange) {
		ordExchange = exchange.ord;
	}

	@Override
	public final DDF_Session getSession() {
		return DDF_Session.fromOrd(ordSession);
	}

	protected final void setSession(final DDF_Session session) {
		ordSession = session.ord;
	}

	@Override
	public final DDF_TradeDay getTradeDay() {
		return DDF_TradeDay.fromOrd(ordTradeDay);
	}

	protected final void setTradeDay(final DDF_TradeDay day) {
		ordTradeDay = day.ord;
	}

	@Override
	public final DDF_SpreadType getSpreadType() {
		return DDF_SpreadType.fromOrd(ordSpread);
	}

	protected final void setSpread(final DDF_SpreadType spread) {
		ordSpread = spread.ord;
	}

	//

	protected final byte[] getSymbolFull() {
		return DDF_Symbology.byteArrayFromSymbolArray(symbolArray);
	}

	private final byte[] getSymbolMain() {
		if (symbolArray == null || symbolArray.length == 0) {
			return DDF_Symbology.DDF_NO_NAME;
		}
		return symbolArray[0];
	}

	/*
	 * non spread: <symbol>
	 * 
	 * or
	 * 
	 * for spread: <symbol1>_<symbol2>_..._<symbolN>
	 */
	@Override
	public TextValue getId() {
		return ValueBuilder.newText(getSymbolFull());
	}

	// ######################

	/*
	 * <soh><rec><symbol>,<subrec><stx><base><exch>(<delay>)(,)(<spread>)||
	 * 
	 * ... body ...
	 * 
	 * ||<day><session><etx>||<time stamp>
	 */
	@Override
	public final void encodeDDF(final ByteBuffer buffer) {
		encodeHead(buffer);
		encodeBody(buffer);
		encodeTail(buffer);
	}

	/*
	 * <soh><rec><symbol>,<subrec><stx><base><exch>(<delay>)(,)(<spread>)||
	 */
	protected final void encodeHead(final ByteBuffer buffer) {
		final DDF_MessageType type = getMessageType();
		//
		buffer.put(SOH); // <soh>
		buffer.put(type.record); // <record>
		buffer.put(getSymbolMain()); // <symbol>
		buffer.put(COMMA); // ','
		buffer.put(type.subRecord); // <sub-record>
		buffer.put(STX); // <stx>
		buffer.put(getFraction().baseCode); // <base code>
		buffer.put(getExchange().code); // <exchange ID>
		encodeDelay(buffer); // optional (<delay>)(,)
		encodeSpread(buffer); // optional (<spread>)
	}

	/*
	 * ... body ...
	 */
	protected void encodeBody(final ByteBuffer buffer) {
		throw new UnsupportedOperationException();
	}

	/*
	 * ||<day><session><etx>||<time stamp>
	 */
	protected void encodeTail(final ByteBuffer buffer) {
		final DateTimeZone zone = getExchange().kind.time.zone;
		//
		buffer.put(getTradeDay().code); // <day>
		buffer.put(getSession().code); // <session>
		buffer.put(ETX); // <etx>
		encodeFeedTimeStamp(millisUTC, zone, buffer);// <time stamp>
	}

	// ######################

	@Override
	public final void decodeDDF(final ByteBuffer buffer) {
		decodeHead(buffer);
		decodeBody(buffer);
		decodeTail(buffer);
	}

	/*
	 * <soh><rec><symbol>,<subrec><stx><base><exch>(<delay>)(,)(<spread>)||
	 */
	protected final void decodeHead(final ByteBuffer buffer) {
		check(buffer.get(), SOH); // <soh>
		final byte record = buffer.get(); // <rec>
		final byte[] symbolMain = read(buffer, COMMA); // <symbol>,
		final byte subRecord = buffer.get(); // <subrec>
		check(buffer.get(), STX); // <stx>
		final byte baseCode = buffer.get(); // <base code>
		final byte exchCode = buffer.get(); // <exch code>
		decodeDelay(buffer); // optional (<delay>)(,)
		final byte[][] symbolLegs = decodeSpread(buffer); // optional (<spread>)
		//
		setSymbol(symbolMain, symbolLegs);
		setMessageType(DDF_MessageType.fromPair(record, subRecord));
		ordFraction = DDF_Fraction.fromBaseCode(baseCode).ord;
		ordExchange = DDF_Exchange.fromCode(exchCode).ord;
	}

	private final void setSymbol(final byte[] symbolMain,
			final byte[][] symbolLegs) {
		if (symbolMain == null) {
			symbolArray = null;
			return;
		}
		if (symbolLegs == null) {
			symbolArray = new byte[1][];
			symbolArray[0] = symbolMain;
			return;
		}
		final int legCount = symbolLegs.length;
		symbolArray = new byte[1 + legCount][];
		symbolArray[0] = symbolMain;
		System.arraycopy(symbolLegs, 0, symbolArray, 1, legCount);
	}

	/*
	 * ... body ...
	 */
	protected void decodeBody(final ByteBuffer buffer) {
		throw new UnsupportedOperationException();
	}

	/*
	 * ||<day><session><etx>||<time stamp>
	 */
	protected void decodeTail(final ByteBuffer buffer) {
		final DateTimeZone zone = getExchange().kind.time.zone;
		//
		final byte dayCode = buffer.get(); // <day>
		final byte sessCode = buffer.get(); // <session>
		check(buffer.get(), ETX); // <etx>
		millisUTC = decodeFeedTimeStamp(zone, buffer); // <time stamp>
		//
		ordTradeDay = DDF_TradeDay.fromCode(dayCode).ord;
		ordSession = DDF_Session.fromPair(ordExchange, sessCode).ord;
	}

	// ######################

	/* <spread type> <num of legs> <symbol2>, <symbol3>, <symbol4>, */

	private final void encodeSpread(final ByteBuffer buffer) {
		final DDF_SpreadType spread = getSpreadType();
		if (!spread.isKnown() || symbolArray == null || symbolArray.length == 1) {
			return;
		}
		final int legCount = symbolArray.length - 1;
		buffer.putChar(spread.code); // <spread type>
		encodeUnsigned_1(legCount, buffer); // <num of legs>
		for (int k = 1; k <= legCount; k++) {
			buffer.put(symbolArray[k]); // <symbol N>
			buffer.put(ASCII.COMMA); // ,
		}
	}

	private final byte[][] decodeSpread(final ByteBuffer buffer) {
		buffer.mark();
		final char spreadCode = buffer.getChar(); // <spread type>
		final DDF_SpreadType spread = DDF_SpreadType.fromCode(spreadCode);
		setSpread(spread);
		if (spread.isKnown()) {
			final int legCount = decodeUnsigned_1(buffer); // <num of legs>
			final byte[][] symbolLegs = new byte[legCount][];
			for (int k = 0; k < legCount; k++) {
				symbolLegs[k] = read(buffer, COMMA); // <symbol N>,
			}
			return symbolLegs;
		} else {
			buffer.reset();
			return null;
		}
	}

	protected final void updateSpread() {
		if (symbolArray == null) {
			return;
		}
		if (symbolArray.length == 1) {
			setSpread(DDF_SpreadType.UNKNOWN);
		} else {
			setSpread(DDF_SpreadType.DEFAULT);
		}
	}

	// ######################

	protected void encodeDelay(final ByteBuffer buffer) {
		encodeUnsigned_2(delay, buffer);
	}

	protected void decodeDelay(final ByteBuffer buffer) {
		delay = decodeUnsigned_2(buffer);
	}

	// ######################

	protected final void setDecodeDefaults() {
		setDecodeDefaults(System.currentTimeMillis());
	}

	protected final void setDecodeDefaults(final long millisUTC) {
		this.millisUTC = millisUTC;
		setTradeDay(DDF_TradeDay.fromMillisUTC(millisUTC));
		setSession(DDF_Session.FUTURE_COMBO);
	}

	// ######################

	@Override
	protected void appedFields(final StringBuilder text) {

		super.appedFields(text);

		text.append("symbol   : ");
		text.append(getId());
		text.append("\n");

		text.append("exchange : ");
		text.append(getExchange());
		text.append("\n");

		text.append("fraction : ");
		text.append(getFraction());
		text.append("\n");

		text.append("tradeDay : ");
		text.append(getTradeDay());
		text.append("\n");

		text.append("session  : ");
		text.append(getSession());
		text.append("\n");

		text.append("spread   : ");
		text.append(getSpreadType());
		text.append("\n");

		text.append("delay    : ");
		text.append(getDelay());
		text.append("\n");

	}

}
