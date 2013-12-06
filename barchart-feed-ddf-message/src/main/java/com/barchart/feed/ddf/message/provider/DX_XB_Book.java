/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.feed.ddf.message.provider.CodecHelper.*;
import static com.barchart.feed.ddf.message.provider.XmlTagBook.*;
import static com.barchart.feed.ddf.util.HelperXML.*;
import static com.barchart.util.common.ascii.ASCII.*;

import org.w3c.dom.Element;

import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.feed.ddf.util.HelperXML;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.util.common.ascii.ASCII;

// TODO: Auto-generated Javadoc
class DX_XB_Book extends DF_3B_Book {

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.DF_3B_Book#accept(com.barchart.feed.ddf.message.api.DDF_MessageVisitor, java.lang.Object)
	 */
	@Override
	public <Result, Param> Result accept(
			final DDF_MessageVisitor<Result, Param> visitor, final Param param) {
		return visitor.visit(this, param);
	}

	DX_XB_Book() {
		super(DDF_MessageType.BOOK_SNAP_XML);
	}

	DX_XB_Book(final DDF_MessageType messageType) {
		super(messageType);
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

		symbolArray = xmlDecSymbol(tag, SYMBOL, XML_STOP);

		updateSpread();

		final byte baseCode = xmlByteDecode(tag, FRACTION_DDF, XML_STOP);
		final DDF_Fraction frac = DDF_Fraction.fromBaseCode(baseCode);
		setFraction(frac);

		//

		countBid = xmlIntegerDecode(tag, BID_COUNT, XML_STOP);

		if (countBid > 0) {

			sizeBidArray = xmlLongArrayDecode(tag, BID_SIZE_ARRAY, COMMA,
					XML_STOP);

			priceBidArray = xmlDecimalArrayDecode(tag, BID_PRICE_ARRAY, COMMA,
					frac, XML_STOP);

		}

		//

		countAsk = xmlIntegerDecode(tag, ASK_COUNT, XML_STOP);

		if (countAsk > 0) {

			sizeAskArray = xmlLongArrayDecode(tag, ASK_SIZE_ARRAY, COMMA,
					XML_STOP);

			priceAskArray = xmlDecimalArrayDecode(tag, ASK_PRICE_ARRAY, COMMA,
					frac, XML_STOP);

		}

		//

		setDecodeDefaults();

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Base#encodeXML(org.w3c.dom.Element)
	 */
	@Override
	public final void encodeXML(final Element tag) {

		xmlCheckTagName(tag, TAG);

		xmlAsciiEncode(getSymbolFull(), tag, SYMBOL);

		final DDF_Fraction frac = getFraction();
		xmlByteEncode(frac.baseCode, tag, FRACTION_DDF);

		xmlIntegerEncode(countBid, tag, BID_COUNT);

		xmlIntegerEncode(countAsk, tag, ASK_COUNT);

		final StringBuilder textSizeBid = new StringBuilder(128);
		final StringBuilder textSizeAsk = new StringBuilder(128);
		final StringBuilder textPriceBid = new StringBuilder(128);
		final StringBuilder textPriceAsk = new StringBuilder(128);

		int bids = countBid;

		for (int index = 0; index < countBid; index++) {

			final long sizeBid = sizeBidArray[index];

			if (sizeBid != 0) {
				textSizeBid.append(Long.toString(sizeBid));
				textPriceBid.append(HelperDDF.decimalEncode(
						priceBidArray[index], frac));
				bids--;
				if (bids > 0) {
					textSizeBid.append(STRING_COMMA);
					textPriceBid.append(STRING_COMMA);
				}
			}
		}

		int asks = countAsk;

		for (int index = 0; index < countAsk; index++) {

			final long sizeAsk = sizeAskArray[index];

			if (sizeAsk != 0) {
				textSizeAsk.append(Long.toString(sizeAsk));
				textPriceAsk.append(HelperDDF.decimalEncode(
						priceAskArray[index], frac));
				asks--;
				if (asks > 0) {
					textSizeAsk.append(STRING_COMMA);
					textPriceAsk.append(STRING_COMMA);
				}
			}

		}

		assert bids == 0 && asks == 0;

		xmlTextEncode(textSizeBid, tag, BID_SIZE_ARRAY);
		xmlTextEncode(textSizeAsk, tag, ASK_SIZE_ARRAY);
		xmlTextEncode(textPriceBid, tag, BID_PRICE_ARRAY);
		xmlTextEncode(textPriceAsk, tag, ASK_PRICE_ARRAY);

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
