/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.feed.ddf.message.provider.CodecHelper.DDF_NO_SESSIONS;
import static com.barchart.feed.ddf.message.provider.CodecHelper.xmlDecSymbol;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.EXCHANGE_DDF;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.EXCHANGE_EXTRA;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.FRACTION_DDF;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.PRICE_ASK;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.PRICE_BID;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.PRICE_POINT_VALUE;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.PRICE_TICK_INCREMENT;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.QUOTE_MODE;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.QUOTE_STATE;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.SIZE_ASK;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.SIZE_BID;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.SYMBOL;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.SYMBOL_NAME;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.TAG;
import static com.barchart.feed.ddf.message.provider.XmlTagQuote.TIME_UPDATE;
import static com.barchart.feed.ddf.util.HelperDDF.newPriceDDF;
import static com.barchart.feed.ddf.util.HelperXML.XML_PASS;
import static com.barchart.feed.ddf.util.HelperXML.XML_STOP;
import static com.barchart.feed.ddf.util.HelperXML.xmlAsciiDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlAsciiEncode;
import static com.barchart.feed.ddf.util.HelperXML.xmlByteDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlByteEncode;
import static com.barchart.feed.ddf.util.HelperXML.xmlCheckTagName;
import static com.barchart.feed.ddf.util.HelperXML.xmlDecimalDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlDecimalEncode;
import static com.barchart.feed.ddf.util.HelperXML.xmlLongDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlLongEncode;
import static com.barchart.feed.ddf.util.HelperXML.xmlNewElement;
import static com.barchart.feed.ddf.util.HelperXML.xmlPriceDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlPriceEncode;
import static com.barchart.feed.ddf.util.HelperXML.xmlTimeDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlTimeEncode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.barchart.feed.api.model.meta.Exchange;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.provider.ValueConverter;
import com.barchart.feed.ddf.instrument.provider.ext.InstBase;
import com.barchart.feed.ddf.instrument.provider.ext.NewInstrumentProvider;
import com.barchart.feed.ddf.message.api.DDF_MarketQuote;
import com.barchart.feed.ddf.message.api.DDF_MarketSession;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.enums.DDF_Condition;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.message.enums.DDF_QuoteMode;
import com.barchart.feed.ddf.message.enums.DDF_QuoteState;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.feed.ddf.util.HelperXML;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.util.ascii.ASCII;
import com.barchart.util.value.api.Fraction;
import com.barchart.util.value.api.Price;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.provider.ValueBuilder;
import com.barchart.util.values.provider.ValueConst;

// TODO: Auto-generated Javadoc
/**
 * 15:08:28.935 [# ddf-messages] DEBUG c.d.f.f.example.LoggingHandler - message
 * 
 * <QUOTE ask="2965" asksize="1" basecode="A" bid="2965" bidsize="1"
 * ddfexchange="Q" exchange="NASDAQ" lastupdate="20110930010822" mode="R"
 * name="Oracle Corp." pointvalue="1.0" symbol="ORCL" tickincrement="1">
 * 
 * <SESSION day="S" high="3061" id="combined" last="2965" low="2908" open="3000"
 * previous="2945" session="R" timestamp="20110929160822" tradesize="100"
 * tradetime="20110929160055" volume="29247490"/>
 * 
 * <SESSION day="?" id="previous" last="2945" session="?"/>
 * 
 * </QUOTE>
 * 
 */

class DX_XQ_Quote extends DF_28_BookTop implements DDF_MarketQuote {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.message.provider.DF_28_BookTop#accept(com.barchart
	 * .feed.ddf.message.api.DDF_MessageVisitor, java.lang.Object)
	 */
	@Override
	public <Result, Param> Result accept(
			final DDF_MessageVisitor<Result, Param> visitor, final Param param) {
		return visitor.visit(this, param);
	}

	DX_XQ_Quote() {
		super(DDF_MessageType.QUOTE_SNAP_XML);
	}

	DX_XQ_Quote(final DDF_MessageType messageType) {
		super(messageType);
	}

	// //////////////////////////////////////

	private byte ordState = DDF_QuoteState.UNKNOWN.ord;
	private byte ordMode = DDF_QuoteMode.UNKNOWN.ord;
	private final byte ordCondition = DDF_Condition.UNKNOWN.ord;

	/** contract price point value (not related to fraction) */
	protected PriceValue pricePoint = ValueConst.NULL_PRICE;

	protected long priceStep = HelperDDF.DDF_EMPTY;

	/** optional */
	protected byte[] symbolName = null; // DDF_Symbology.DDF_NO_NAME;

	/** optional */
	protected byte[] exchangeExtra = null; // DDF_Symbology.DDF_NO_NAME;

	protected DX_XS_Session[] sessions = DDF_NO_SESSIONS;

	// //////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketQuote#getState()
	 */
	@Override
	public final DDF_QuoteState getState() {
		return DDF_QuoteState.fromOrd(ordState);
	}

	/**
	 * Sets the state.
	 * 
	 * @param state
	 *            the new state
	 */
	public final void setState(final DDF_QuoteState state) {
		ordState = state.ord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketQuote#getCondition()
	 */
	@Override
	public final DDF_Condition getCondition() {
		return DDF_Condition.fromOrd(ordCondition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketQuote#getMode()
	 */
	@Override
	public final DDF_QuoteMode getMode() {
		return DDF_QuoteMode.fromOrd(ordMode);
	}

	/**
	 * Sets the mode.
	 * 
	 * @param mode
	 *            the new mode
	 */
	public final void setMode(final DDF_QuoteMode mode) {
		ordMode = mode.ord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketQuote#getPointValue()
	 */
	@Override
	public final PriceValue getPointValue() {
		if (pricePoint == null) {
			return ValueConst.NULL_PRICE;
		}
		return pricePoint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketQuote#getSymbolName()
	 */
	@Override
	public final TextValue getSymbolName() {
		return ValueBuilder.newText(symbolName);
	}

	/**
	 * Gets the name bytes.
	 * 
	 * @return the name bytes
	 */
	public final byte[] getNameBytes() {
		return symbolName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketQuote#getPriceStep()
	 */
	@Override
	public final PriceValue getPriceStep() {
		return newPriceDDF(priceStep, getFraction());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketQuote#sessions()
	 */
	@Override
	public final DDF_MarketSession[] sessions() {
		if (sessions == null) {
			return new DDF_MarketSession[0];
		}
		return sessions;
	}

	//

	@Override
	protected final String xmlTagName() {
		return TAG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.message.provider.Base#decodeXML(org.w3c.dom.Element
	 * )
	 */
	@Override
	public final void decodeXML(final Element tag) {

		xmlCheckTagName(tag, TAG);

		symbolArray = xmlDecSymbol(tag, SYMBOL, XML_STOP);

		updateSpread();

		symbolName = xmlAsciiDecode(tag, SYMBOL_NAME, XML_PASS);

		exchangeExtra = xmlAsciiDecode(tag, EXCHANGE_EXTRA, XML_PASS);

		final byte baseCode = xmlByteDecode(tag, FRACTION_DDF, XML_STOP);
		final DDF_Fraction frac = DDF_Fraction.fromBaseCode(baseCode);
		setFraction(frac);

		// FIXME, MDEX ddfexchange code missing
		final byte exchCode = xmlByteDecode(tag, EXCHANGE_DDF, XML_PASS);
		final DDF_Exchange exch = DDF_Exchange.fromCode(exchCode);
		setExchange(exch);

		final byte modeCode = xmlByteDecode(tag, QUOTE_MODE, XML_PASS);
		final DDF_QuoteMode mode = DDF_QuoteMode.fromCode(modeCode);
		setMode(mode);

		final byte flagCode = xmlByteDecode(tag, QUOTE_STATE, XML_PASS);
		final DDF_QuoteState flag = DDF_QuoteState.fromCode(flagCode);
		setState(flag);
		if (flag == DDF_QuoteState.UNKNOWN) {
			log.debug("Parsed UNKNOWN QuoteState : {}", flagCode);
		}

		priceStep = xmlDecimalDecode(frac, tag, PRICE_TICK_INCREMENT, XML_STOP);

		priceBid = xmlDecimalDecode(frac, tag, PRICE_BID, XML_PASS);
		sizeBid = xmlLongDecode(tag, SIZE_BID, XML_PASS);

		priceAsk = xmlDecimalDecode(frac, tag, PRICE_ASK, XML_PASS);
		sizeAsk = xmlLongDecode(tag, SIZE_ASK, XML_PASS);

		pricePoint = xmlPriceDecode(tag, PRICE_POINT_VALUE, XML_PASS);

		//

		final NodeList nodeList = tag.getElementsByTagName(XmlTagSession.TAG);

		if (nodeList != null) {
			final int size = nodeList.getLength();
			sessions = new DX_XS_Session[size];
			for (int k = 0; k < size; k++) {
				final Element sessionTag = (Element) nodeList.item(k);
				final DX_XS_Session session = new DX_XS_Session();
				// note: session inherits these from quote
				session.symbolArray = symbolArray;
				session.setExchange(exch);
				session.setFraction(frac);
				//
				session.decodeXML(sessionTag);
				sessions[k] = session;
			}
		}

		//

		final long millisUTC =
				xmlTimeDecode(exch.kind.time.zone, tag, TIME_UPDATE, XML_PASS);
		setDecodeDefaults(millisUTC);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.message.provider.Base#encodeXML(org.w3c.dom.Element
	 * )
	 */
	@Override
	public final void encodeXML(final Element tag) {

		xmlCheckTagName(tag, TAG);

		xmlAsciiEncode(getSymbolFull(), tag, SYMBOL);

		xmlAsciiEncode(symbolName, tag, SYMBOL_NAME);

		xmlAsciiEncode(exchangeExtra, tag, EXCHANGE_EXTRA);

		final DDF_Fraction frac = getFraction();
		xmlByteEncode(frac.baseCode, tag, FRACTION_DDF);

		final DDF_Exchange exch = getExchange();
		xmlByteEncode(exch.code, tag, EXCHANGE_DDF);

		final DDF_QuoteMode mode = getMode();
		if (mode.isKnown()) {
			xmlByteEncode(mode.code, tag, QUOTE_MODE);
		}

		final DDF_QuoteState flag = getState();
		if (flag.isKnown()) {
			xmlByteEncode(flag.code, tag, QUOTE_STATE);
		}

		xmlDecimalEncode(priceStep, frac, tag, PRICE_TICK_INCREMENT);

		xmlDecimalEncode(priceBid, frac, tag, PRICE_BID);
		xmlLongEncode(sizeBid, tag, SIZE_BID);

		xmlDecimalEncode(priceAsk, frac, tag, PRICE_ASK);
		xmlLongEncode(sizeAsk, tag, SIZE_ASK);

		xmlPriceEncode(pricePoint, tag, PRICE_POINT_VALUE);

		//

		xmlTimeEncode(millisUTC, exch.kind.time.zone, tag, TIME_UPDATE);

		//

		for (final DX_XS_Session session : sessions) {
			final Element sessionTag = xmlNewElement(tag, XmlTagSession.TAG);
			session.encodeXML(sessionTag);
			tag.appendChild(sessionTag);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.message.provider.Base#toString()
	 */
	@Override
	public String toString() {
		final Element tag = HelperXML.xmlNewDocument(xmlTagName());
		encodeXML(tag);
		final byte[] array = HelperXML.xmlDocumentEncode(tag, true);
		return new String(array, ASCII.ASCII_CHARSET);
	}

	@Override
	protected void appedFields(final StringBuilder text) {

		super.appedFields(text);

		// TODO
		text.append("TODO : ");

	}
	
	@Override
	public Instrument getInstrument() {
		return stub;
	}
	
	/*  
	 * Lazy eval instrument stub 
	 */
	private final Instrument stub = new InstBase() {

		@Override
		public String marketGUID() {
			return NewInstrumentProvider.formatSymbol(getId().toString());
		}

		@Override
		public SecurityType securityType() {
			return getExchange().kind.asSecType();
		}

		@Override
		public String symbol() {
			return NewInstrumentProvider.formatSymbol(getId().toString());
		}

		@Override
		public Exchange exchange() {
			return getExchange().asExchange();
		}

		@Override
		public String exchangeCode() {
			return new String(new byte[] {getExchange().code});
		}

		@Override
		public Price tickSize() {
			return ValueConverter.price(getPriceStep());
		}

		@Override
		public Price pointValue() {
			return ValueConverter.price(getPointValue());
		}

		@Override
		public Fraction displayFraction() {
			return ValueConverter.fraction(getFraction().fraction);
		}

	};

}
