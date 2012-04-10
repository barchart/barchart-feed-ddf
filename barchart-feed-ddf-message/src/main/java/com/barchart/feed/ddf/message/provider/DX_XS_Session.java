/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.feed.ddf.message.provider.XmlTagSession.PRICE_HIGH;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.PRICE_LAST;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.PRICE_LAST_PREV;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.PRICE_LOW;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.PRICE_OPEN;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.PRICE_SETTLE;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.SESSION;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.SESSION_INDICATOR;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.SIZE_INTEREST;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.SIZE_LAST;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.SIZE_VOLUME;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.TAG;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.TIME_LAST;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.TIME_UPDATE;
import static com.barchart.feed.ddf.message.provider.XmlTagSession.TRADE_DAY;
import static com.barchart.feed.ddf.util.HelperDDF.newSizeDDF;
import static com.barchart.feed.ddf.util.HelperXML.XML_PASS;
import static com.barchart.feed.ddf.util.HelperXML.XML_STOP;
import static com.barchart.feed.ddf.util.HelperXML.xmlByteDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlByteEncode;
import static com.barchart.feed.ddf.util.HelperXML.xmlCheckTagName;
import static com.barchart.feed.ddf.util.HelperXML.xmlDecimalDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlDecimalEncode;
import static com.barchart.feed.ddf.util.HelperXML.xmlLongDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlLongEncode;
import static com.barchart.feed.ddf.util.HelperXML.xmlStringDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlStringEncode;
import static com.barchart.feed.ddf.util.HelperXML.xmlTimeDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlTimeEncode;

import org.w3c.dom.Element;

import com.barchart.feed.ddf.message.api.DDF_MarketSession;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.enums.DDF_Indicator;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.message.enums.DDF_Session;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.feed.ddf.util.HelperXML;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.util.ascii.ASCII;
import com.barchart.util.values.api.SizeValue;
import com.barchart.util.values.api.TimeValue;
import com.barchart.util.values.provider.ValueBuilder;

// TODO: Auto-generated Javadoc
class DX_XS_Session extends DF_21_Snap implements DDF_MarketSession {

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.DF_21_Snap#accept(com.barchart.feed.ddf.message.api.DDF_MessageVisitor, java.lang.Object)
	 */
	@Override
	public <Result, Param> Result accept(
			final DDF_MessageVisitor<Result, Param> visitor, final Param param) {
		return visitor.visit(this, param);
	}

	DX_XS_Session() {
		super(DDF_MessageType.SESSION_SNAP_XML);
	}

	DX_XS_Session(final DDF_MessageType messageType) {
		super(messageType);
	}

	// //////////////////////////////////////

	/** current vs previous vs individual */
	private byte ordIndicator = DDF_Indicator.UNKNOWN.ord;

	/** last trade size */
	protected long sizeLast = HelperDDF.DDF_EMPTY;

	/** last trade time */
	protected long timeLast = HelperDDF.DDF_EMPTY;

	// //////////////////////////////////////

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketSession#getSizeLast()
	 */
	@Override
	public final SizeValue getSizeLast() {
		return newSizeDDF(sizeLast);
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketSession#getTimeLast()
	 */
	@Override
	public final TimeValue getTimeLast() {
		return ValueBuilder.newTime(timeLast);
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketSession#getIndicator()
	 */
	@Override
	public final DDF_Indicator getIndicator() {
		return DDF_Indicator.fromOrd(ordIndicator);
	}

	/**
	 * Sets the indicator.
	 *
	 * @param indicator the new indicator
	 */
	public final void setIndicator(final DDF_Indicator indicator) {
		ordIndicator = indicator.ord;
	}

	//

	@Override
	protected final String xmlTagName() {
		return TAG;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Base#decodeXML(org.w3c.dom.Element)
	 */
	@Override
	public final void decodeXML(final Element tag) {

		xmlCheckTagName(tag, TAG);

		final DDF_Exchange exch = getExchange();
		final DDF_Fraction frac = getFraction();

		//

		final String indCode = xmlStringDecode(tag, SESSION_INDICATOR, XML_STOP);
		setIndicator(DDF_Indicator.fromCode(indCode));

		final byte dayCode = xmlByteDecode(tag, TRADE_DAY, XML_PASS);
		setTradeDay(DDF_TradeDay.fromCode(dayCode));

		//

		// last trade today
		final byte typeCode = xmlByteDecode(tag, SESSION, XML_PASS);
		setSession(DDF_Session.fromPair(exch.ord, typeCode));
		priceLast = xmlDecimalDecode(frac, tag, PRICE_LAST, XML_PASS);
		sizeLast = xmlLongDecode(tag, SIZE_LAST, XML_PASS);
		timeLast = xmlTimeDecode(exch.kind.time.zone, tag, TIME_LAST, XML_PASS);

		// settle yesterday
		priceLastPrevious = xmlDecimalDecode(frac, tag, PRICE_LAST_PREV,
				XML_PASS);

		//

		priceOpen = xmlDecimalDecode(frac, tag, PRICE_OPEN, XML_PASS);
		priceHigh = xmlDecimalDecode(frac, tag, PRICE_HIGH, XML_PASS);
		priceLow = xmlDecimalDecode(frac, tag, PRICE_LOW, XML_PASS);
		priceSettle = xmlDecimalDecode(frac, tag, PRICE_SETTLE, XML_PASS);

		sizeVolume = xmlLongDecode(tag, SIZE_VOLUME, XML_PASS);
		sizeInterest = xmlLongDecode(tag, SIZE_INTEREST, XML_PASS);

		//

		millisUTC = xmlTimeDecode(exch.kind.time.zone, tag, TIME_UPDATE,
				XML_PASS);

		// log.debug("### priceLast : {}", priceLast);

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Base#encodeXML(org.w3c.dom.Element)
	 */
	@Override
	public final void encodeXML(final Element tag) {

		xmlCheckTagName(tag, TAG);

		final DDF_Exchange exch = getExchange();
		final DDF_Fraction frac = getFraction();

		//

		xmlStringEncode(getIndicator().code, tag, SESSION_INDICATOR);

		xmlByteEncode(getTradeDay().code, tag, TRADE_DAY);

		//

		final DDF_Session session = getSession();

		//

		xmlByteEncode(session.code, tag, SESSION);
		xmlDecimalEncode(priceLast, frac, tag, PRICE_LAST);
		xmlLongEncode(sizeLast, tag, SIZE_LAST);
		xmlTimeEncode(timeLast, exch.kind.time.zone, tag, TIME_LAST);

		//

		xmlDecimalEncode(priceLastPrevious, frac, tag, PRICE_LAST_PREV);

		//

		xmlDecimalEncode(priceOpen, frac, tag, PRICE_OPEN);
		xmlDecimalEncode(priceHigh, frac, tag, PRICE_HIGH);
		xmlDecimalEncode(priceLow, frac, tag, PRICE_LOW);
		xmlDecimalEncode(priceSettle, frac, tag, PRICE_SETTLE);

		xmlLongEncode(sizeVolume, tag, SIZE_VOLUME);
		xmlLongEncode(sizeInterest, tag, SIZE_INTEREST);

		//

		xmlTimeEncode(millisUTC, exch.kind.time.zone, tag, TIME_UPDATE);

	}

	/* (non-Javadoc)
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

}
