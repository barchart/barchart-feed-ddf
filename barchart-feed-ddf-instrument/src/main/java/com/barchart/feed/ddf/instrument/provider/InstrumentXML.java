package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.BASE_CODE_DDF;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.EXCHANGE_COMMENT;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.EXCHANGE_DDF;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.GUID;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.LOOKUP;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.PRICE_POINT_VALUE;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.PRICE_TICK_INCREMENT;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.STATUS;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_CODE_CFI;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_COMMENT;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_DDF_EXPIRE_MONTH;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_DDF_EXPIRE_YEAR;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_DDF_REAL;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_EXPIRE;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_HIST;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_REALTIME;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.TIME_ZONE_DDF;
import static com.barchart.feed.ddf.util.HelperXML.XML_PASS;
import static com.barchart.feed.ddf.util.HelperXML.XML_STOP;
import static com.barchart.feed.ddf.util.HelperXML.xmlByteDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlDecimalDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlStringDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlTimeDecode;
import static com.barchart.util.values.provider.ValueBuilder.newPrice;
import static com.barchart.util.values.provider.ValueBuilder.newText;
import static com.barchart.feed.inst.api.InstrumentField.*;


import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.enums.InstrumentFieldDDF;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_TimeZone;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.feed.inst.api.Instrument;
import com.barchart.feed.inst.enums.CodeCFI;
import com.barchart.feed.inst.provider.InstrumentFactory;
import com.barchart.missive.core.Tag;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.TimeValue;
import com.barchart.util.values.provider.ValueBuilder;

public final class InstrumentXML {
	
	private InstrumentXML() {
		
	}
	
	@SuppressWarnings("rawtypes")
	public static DDF_Instrument decodeXML(final Element tag) throws Exception {
		
		// lookup status

		final String statusCode = xmlStringDecode(tag, STATUS, XML_STOP);

		final StatusXML status = StatusXML.fromCode(statusCode);

		if (!status.isFound()) {

			final String lookup = xmlStringDecode(tag, LOOKUP, XML_STOP);

			throw new SymbolNotFoundException(lookup);

		}

		// decode DOM
		
		final String guid = xmlStringDecode(tag, GUID, XML_STOP);
		final String symbolHist = xmlStringDecode(tag, SYMBOL_HIST, XML_STOP);
		final String symbolReal = xmlStringDecode(tag, SYMBOL_REALTIME, XML_STOP);
		final String symbolDDFReal = xmlStringDecode(tag, SYMBOL_DDF_REAL, XML_STOP);
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

		// month code for exp of futures contract
		final String ddf_expire_month = xmlStringDecode(tag,
				SYMBOL_DDF_EXPIRE_MONTH, XML_PASS);
		final String ddf_expire_year = xmlStringDecode(tag,
				SYMBOL_DDF_EXPIRE_YEAR, XML_PASS);
		final DDF_TimeZone zone = DDF_TimeZone.fromCode(zoneCode);
		final DDF_Exchange exchange = DDF_Exchange.fromCode(exchCode);
		final DDF_Fraction frac = DDF_Fraction.fromBaseCode(baseCode);
		final long priceStepMantissa = xmlDecimalDecode(frac, tag,
				PRICE_TICK_INCREMENT, XML_STOP);
		final String pricePointString = xmlStringDecode(tag, PRICE_POINT_VALUE,
				XML_PASS);

		PriceValue pricePoint = ValueBuilder.newPrice(0);
		if (pricePointString != null) {
			try {
				pricePoint = ValueBuilder.newPrice(Double
						.valueOf(pricePointString));
			} catch (Exception e) {
			}

		}

		final PriceValue priceStep = newPrice(priceStepMantissa,
				frac.decimalExponent);
		
		final Map<Tag, Object> map = new HashMap<Tag, Object>();
		
		/* GENERIC */
		
		map.put(ID, newText(guid));
		map.put(SYMBOL, newText(symbolReal));
		map.put(DESCRIPTION, newText(symolComment));
		map.put(TYPE, CodeCFI.fromCode(codeCFI));
		map.put(EXCHANGE_ID, newText(exchange.name()));
		map.put(FRACTION, frac.fraction);
		map.put(PRICE_STEP, priceStep);
		map.put(PRICE_POINT, pricePoint);
		map.put(TIME_ZONE, newText(zone.code));
		map.put(DATE_FINISH, expire);
		
		/* PROPRIETARY */

		map.put(InstrumentFieldDDF.DDF_EXPIRE_MONTH, newText(ddf_expire_month));
		map.put(InstrumentFieldDDF.DDF_EXPIRE_YEAR, newText(ddf_expire_year));
		map.put(InstrumentFieldDDF.DDF_ZONE, zone);
		map.put(InstrumentFieldDDF.DDF_EXCHANGE, exchange);
		map.put(InstrumentFieldDDF.DDF_EXCH_DESC, newText(exchangeComment));
		map.put(InstrumentFieldDDF.DDF_SYMBOL_UNIVERSAL, newText(symbolReal));
		map.put(InstrumentFieldDDF.DDF_SYMBOL_HISTORICAL, newText(symbolHist));
		map.put(InstrumentFieldDDF.DDF_SYMBOL_REALTIME, newText(symbolDDFReal));
		
		return new InstrumentDDF(InstrumentFactory.build(map));
		
	}
	
	@SuppressWarnings("rawtypes")
	public static DDF_Instrument decodeSAX(final Attributes ats) throws Exception {
		
		// lookup status
		final String statusCode = xmlStringDecode(ats, STATUS, XML_STOP);
		final StatusXML status = StatusXML.fromCode(statusCode);
		if (!status.isFound()) {
			final String lookup = xmlStringDecode(ats, LOOKUP, XML_STOP);
			throw new SymbolNotFoundException(lookup);
		}

		// decode SAX
		final String guid = xmlStringDecode(ats, GUID, XML_STOP);
		final String symbolHist = xmlStringDecode(ats, SYMBOL_HIST, XML_STOP);
		final String symbolReal = xmlStringDecode(ats, SYMBOL_REALTIME, XML_STOP);
		final String symbolDDFReal = xmlStringDecode(ats, SYMBOL_DDF_REAL, XML_STOP);
		final byte exchCode = xmlByteDecode(ats, EXCHANGE_DDF, XML_PASS); // XXX
		final byte baseCode = xmlByteDecode(ats, BASE_CODE_DDF, XML_STOP);
		final String codeCFI = xmlStringDecode(ats, SYMBOL_CODE_CFI, XML_PASS);
		final String zoneCode = xmlStringDecode(ats, TIME_ZONE_DDF, XML_STOP);
		final String symolComment = xmlStringDecode(ats, SYMBOL_COMMENT,
				XML_PASS);
		final String exchangeComment = xmlStringDecode(ats, EXCHANGE_COMMENT,
				XML_PASS);
		final TimeValue expire = xmlTimeDecode(ats, SYMBOL_EXPIRE, XML_PASS);

		//

		final String ddf_expire_month = xmlStringDecode(ats,
				SYMBOL_DDF_EXPIRE_MONTH, XML_PASS);
		final String ddf_expire_year = xmlStringDecode(ats,
				SYMBOL_DDF_EXPIRE_YEAR, XML_PASS);
		final DDF_TimeZone zone = DDF_TimeZone.fromCode(zoneCode);
		final DDF_Exchange exchange = DDF_Exchange.fromCode(exchCode);
		final DDF_Fraction frac = DDF_Fraction.fromBaseCode(baseCode);
		final long priceStepMantissa = xmlDecimalDecode(frac, ats,
				PRICE_TICK_INCREMENT, XML_STOP);
		final String pricePointString = xmlStringDecode(ats, PRICE_POINT_VALUE,
				XML_PASS);

		PriceValue pricePoint = ValueBuilder.newPrice(0);
		if (pricePointString != null) {
			try {
				pricePoint = ValueBuilder.newPrice(Double
						.valueOf(pricePointString));
			} catch (Exception e) {
			}
		}

		final PriceValue priceStep = newPrice(priceStepMantissa,
				frac.decimalExponent);
		
		final Map<Tag, Object> map = new HashMap<Tag, Object>();
		
		/* GENERIC */
		
		map.put(ID, newText(guid));
		map.put(SYMBOL, newText(symbolReal));
		map.put(DESCRIPTION, newText(symolComment));
		map.put(TYPE, CodeCFI.fromCode(codeCFI));
		map.put(EXCHANGE_ID, newText(exchange.name()));
		map.put(FRACTION, frac.fraction);
		map.put(PRICE_STEP, priceStep);
		map.put(PRICE_POINT, pricePoint);
		map.put(TIME_ZONE, newText(zone.code));
		map.put(DATE_FINISH, expire);
		
		/* PROPRIETARY */

		map.put(InstrumentFieldDDF.DDF_EXPIRE_MONTH, newText(ddf_expire_month));
		map.put(InstrumentFieldDDF.DDF_EXPIRE_YEAR, newText(ddf_expire_year));
		map.put(InstrumentFieldDDF.DDF_ZONE, zone);
		map.put(InstrumentFieldDDF.DDF_EXCHANGE, exchange);
		map.put(InstrumentFieldDDF.DDF_EXCH_DESC, newText(exchangeComment));
		map.put(InstrumentFieldDDF.DDF_SYMBOL_UNIVERSAL, newText(symbolReal));
		map.put(InstrumentFieldDDF.DDF_SYMBOL_HISTORICAL, newText(symbolHist));
		map.put(InstrumentFieldDDF.DDF_SYMBOL_REALTIME, newText(symbolDDFReal));
		
		return new InstrumentDDF(InstrumentFactory.build(map));
		
	}

}
