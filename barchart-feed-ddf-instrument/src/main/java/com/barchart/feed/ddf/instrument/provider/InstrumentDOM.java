/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.BASE_CODE_DDF;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.EXCHANGE_COMMENT;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.EXCHANGE_DDF;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.LOOKUP;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.PRICE_POINT_VALUE;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.PRICE_TICK_INCREMENT;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.STATUS;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_CODE_CFI;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_COMMENT;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_EXPIRE;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_HIST;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_REAL;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_UNI;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.TIME_ZONE_DDF;
import static com.barchart.feed.ddf.util.HelperXML.XML_PASS;
import static com.barchart.feed.ddf.util.HelperXML.XML_STOP;
import static com.barchart.feed.ddf.util.HelperXML.xmlByteDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlDecimalDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlPriceDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlStringDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlTimeDecode;
import static com.barchart.util.values.provider.ValueBuilder.newPrice;
import static com.barchart.util.values.provider.ValueBuilder.newText;

import org.w3c.dom.Element;

import com.barchart.feed.base.instrument.enums.CodeCFI;
import com.barchart.feed.base.instrument.enums.InstrumentField;
import com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_TimeZone;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.TimeValue;

class InstrumentDOM extends InstrumentDDF implements CodecDOM {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.instrument.provider.CodecDOM#decodeXML(org.w3c.
	 * dom.Element)
	 */
	@Override
	public void decodeXML(final Element tag) throws Exception {

		// lookup status

		final String statusCode = xmlStringDecode(tag, STATUS, XML_STOP);

		final StatusXML status = StatusXML.fromCode(statusCode);

		if (!status.isFound()) {

			final String lookup = xmlStringDecode(tag, LOOKUP, XML_STOP);

			throw new SymbolNotFoundException(lookup);

		}

		// decode DOM

		final String symbolUni = xmlStringDecode(tag, SYMBOL_UNI, XML_STOP);

		final String symbolHist = xmlStringDecode(tag, SYMBOL_HIST, XML_STOP);

		final String symbolReal = xmlStringDecode(tag, SYMBOL_REAL, XML_STOP);

		final byte exchCode = xmlByteDecode(tag, EXCHANGE_DDF, XML_PASS); // XXX

		final byte baseCode = xmlByteDecode(tag, BASE_CODE_DDF, XML_STOP);

		final String codeCFI = xmlStringDecode(tag, SYMBOL_CODE_CFI, XML_PASS);

		final String zoneCode = xmlStringDecode(tag, TIME_ZONE_DDF, XML_STOP);

		final String symolComment = xmlStringDecode(tag, SYMBOL_COMMENT,
				XML_PASS);

		final String exchangeComment = xmlStringDecode(tag, EXCHANGE_COMMENT,
				XML_PASS);

		final TimeValue expire = xmlTimeDecode(tag, SYMBOL_EXPIRE, XML_PASS);

		//

		final DDF_TimeZone zone = DDF_TimeZone.fromCode(zoneCode);

		final DDF_Exchange exchange = DDF_Exchange.fromCode(exchCode);

		final DDF_Fraction frac = DDF_Fraction.fromBaseCode(baseCode);

		final long priceStepMantissa = xmlDecimalDecode(frac, tag,
				PRICE_TICK_INCREMENT, XML_STOP);

		final PriceValue pricePoint = xmlPriceDecode(tag, PRICE_POINT_VALUE,
				XML_PASS);

		final PriceValue priceStep = newPrice(priceStepMantissa,
				frac.decimalExponent);

		/* GENERIC */

		set(InstrumentField.ID, newText(symbolUni));
		set(InstrumentField.SYMBOL, newText(symbolUni));
		set(InstrumentField.DESCRIPTION, newText(symolComment));
		set(InstrumentField.TYPE, CodeCFI.fromCode(codeCFI));
		set(InstrumentField.EXCHANGE_ID, newText(exchange.name()));
		set(InstrumentField.FRACTION, frac.fraction);
		set(InstrumentField.PRICE_STEP, priceStep);
		set(InstrumentField.PRICE_POINT, pricePoint);

		// TODO
		// final VarCalendar calendar = new VarCalendar();
		// set(InstrumentField.CALENDAR, calendar);
		set(InstrumentField.TIME_ZONE, newText(zone.code));
		set(InstrumentField.DATE_FINISH, expire);

		/* PROPRIETARY */

		set(DDF_InstrumentField.DDF_ZONE, zone);
		set(DDF_InstrumentField.DDF_EXCHANGE, exchange);
		set(DDF_InstrumentField.DDF_EXCH_DESC, newText(exchangeComment));
		set(DDF_InstrumentField.DDF_SYMBOL_UNIVERSAL, newText(symbolUni));
		set(DDF_InstrumentField.DDF_SYMBOL_HISTORICAL, newText(symbolHist));
		set(DDF_InstrumentField.DDF_SYMBOL_REALTIME, newText(symbolReal));

	}

}
