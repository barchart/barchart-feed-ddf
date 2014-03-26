package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.ALT_SYMBOL;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.BASE_CODE_DDF;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.EXCHANGE_DDF;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.ID;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.LOOKUP;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.PRICE_POINT_VALUE;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.PRICE_TICK_INCREMENT;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.STATUS;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_CODE_CFI;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_COMMENT;
import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.SYMBOL_DDF_EXPIRE_MONTH;
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

import org.openfeed.InstrumentDefinition;
import org.openfeed.InstrumentDefinition.BookLiquidity;
import org.openfeed.InstrumentDefinition.BookStructure;
import org.openfeed.InstrumentDefinition.Decimal;
import org.openfeed.InstrumentDefinition.InstrumentType;
import org.openfeed.InstrumentDefinition.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import com.barchart.feed.api.model.meta.id.VendorID;
import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.provider.ValueBuilder;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_TimeZone;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.feed.inst.provider.Exchanges;
import com.barchart.util.value.ValueFactoryImpl;
import com.barchart.util.value.api.Time;
import com.barchart.util.value.api.ValueFactory;

public final class InstrumentXML {

	@SuppressWarnings("unused")
	private static final ValueFactory factory = new ValueFactoryImpl();

	private static final Logger log = LoggerFactory.getLogger(InstrumentXML.class);

	private InstrumentXML() {

	}

	public static InstrumentDefinition decodeSAX(final Attributes ats)
			throws Exception {

		final InstrumentDefinition.Builder builder = InstrumentDefinition.newBuilder();

		try {

			// lookup status
			final String statusCode = xmlStringDecode(ats, STATUS, XML_STOP);
			final StatusXML status = StatusXML.fromCode(statusCode);
			if (!status.isFound()) {
				final String lookup = xmlStringDecode(ats, LOOKUP, XML_STOP);
				throw new SymbolNotFoundException(lookup);
			}

			/* market identifier; must be globally unique; */
			try {
				builder.setMarketId(Long.parseLong(xmlStringDecode(ats, ID, XML_STOP)));
			} catch (final Exception e) {
				/* Ensure no id collision by making negative */
				builder.setMarketId(Math.abs(xmlStringDecode(ats, ID, XML_STOP).hashCode()) * -1);
			}

			/* type of security, Forex, Equity, etc. */
			builder.setInstrumentType(InstrumentType.NO_INSTRUMENT);

			/* liquidy type, default / implied / combined */
			builder.setBookLiquidity(BookLiquidity.NO_BOOK_LIQUIDITY);

			/* structure of book */
			builder.setBookStructure(BookStructure.NO_BOOK_STRUCTURE);

			/* book depth - NOT AVAIL */

			/* vendor */
			builder.setVendorId(VendorID.BARCHART.toString());

			/* market symbol; can be non unique; */
			builder.setSymbol(xmlStringDecode(ats, SYMBOL_REALTIME, XML_STOP));

			/* Barchart symbol, only used for options */
			final String bar = xmlStringDecode(ats, SYMBOL_DDF_REAL, XML_PASS);

			if (bar != null) {
				final Symbol.Builder b = Symbol.newBuilder();
				b.setVendor(VendorID.BARCHART.toString());
				b.setSymbol(bar);
				final Symbol symbol = b.build();
				builder.addSymbols(symbol);
			}

			/* Barchart historical symbol, used for historical data queries */
			final String hist = xmlStringDecode(ats, SYMBOL_HIST, XML_PASS);
			
			if (hist != null) {
				final Symbol.Builder b = Symbol.newBuilder();
				b.setVendor(VendorID.BARCHART_HISTORICAL.toString());
				b.setSymbol(hist);
				final Symbol symbol = b.build();
				builder.addSymbols(symbol);
			}

			// NOTE: Hard coded for CQG
			final String cqg = xmlStringDecode(ats, ALT_SYMBOL, XML_PASS);

			if (cqg != null) {
				final Symbol.Builder b = Symbol.newBuilder();
				b.setVendor(DDF_RxInstrumentProvider.CQG_VENDOR_ID.toString());
				b.setSymbol(cqg);
				final Symbol symbol = b.build();
				builder.addSymbols(symbol);
			}

			/* market free style description; can be used in full text search */
			builder.setDescription(String.valueOf(xmlStringDecode(ats, SYMBOL_COMMENT, XML_PASS)));

			/* stock vs future vs etc. */
			builder.setCfiCode(xmlStringDecode(ats, SYMBOL_CODE_CFI, XML_PASS));

			/* price currency */
			builder.setCurrencyCode("USD");

			/* market originating exchange identifier */
			final DDF_Exchange exchange =
					DDF_Exchange.fromCode(xmlByteDecode(ats, EXCHANGE_DDF, XML_PASS));
			String eCode = new String(new byte[] {exchange.code});
			if (eCode == null || eCode.isEmpty()) {
				eCode = Exchanges.NULL_CODE;
			}
			builder.setExchangeCode(eCode);

			final DDF_Fraction frac = DDF_Fraction.fromBaseCode(xmlByteDecode(ats, BASE_CODE_DDF, XML_STOP));

			/* price step / increment size / tick size */
			try {
				final long priceStepMantissa =
						xmlDecimalDecode(frac, ats, PRICE_TICK_INCREMENT, XML_STOP);
				builder.setMinimumPriceIncrement(buildDecimal(
						priceStepMantissa, frac.decimalExponent));
			} catch (final Exception e) {
				builder.setMinimumPriceIncrement(buildDecimal(0, 0));
			}

			/* value of a future contract / stock share */
			final String pricePointString =
					xmlStringDecode(ats, PRICE_POINT_VALUE, XML_PASS);
			if (pricePointString == null || pricePointString.isEmpty()) {
				builder.setContractPointValue(buildDecimal(0, 0));
			} else {
				final PriceValue pricePoint =
						ValueBuilder.newPrice(Double.valueOf(pricePointString));
				builder.setContractPointValue(buildDecimal(
						pricePoint.mantissa(), pricePoint.exponent()));
			}

			/* display fraction base : decimal(10) vs binary(2), etc. */
			builder.setDisplayBase((int) frac.fraction.base());
			builder.setDisplayExponent(frac.fraction.exponent());

			/* Expire Month */
			final String expMonth = xmlStringDecode(ats, SYMBOL_DDF_EXPIRE_MONTH, XML_PASS);
			builder.setContractMonth(decodeMonth(expMonth));
			
			/* Expire */
			final Time expire = xmlTimeDecode(ats, SYMBOL_EXPIRE, XML_PASS);
			builder.setContractExpire(expire.millisecond());

			//
			final DDF_TimeZone zone =
					DDF_TimeZone.fromCode(xmlStringDecode(ats, TIME_ZONE_DDF, XML_STOP));

			// /* timezone represented as offset in minutes from utc */
			// builder.setTimeZoneOffset(zone.getUTCOffset());

			/* time zone name as text */
			builder.setTimeZoneName(zone.code());

			return builder.build();

		} catch (final Exception e) {
			log.error("Exception parsing instrument \n{}", e);
			final String lookup = xmlStringDecode(ats, LOOKUP, XML_STOP);
			throw new SymbolNotFoundException(lookup);
		}

	}
	
	public static org.openfeed.InstrumentDefinition.Month decodeMonth(final String m) {
		
		if(m.equals("F")) {
			return org.openfeed.InstrumentDefinition.Month.JANUARY;
		} else if(m.equals("G")) {
			return org.openfeed.InstrumentDefinition.Month.FEBRUARY;
		} else if(m.equals("H")) {
			return org.openfeed.InstrumentDefinition.Month.MARCH;
		} else if(m.equals("J")) {
			return org.openfeed.InstrumentDefinition.Month.APRIL;
		} else if(m.equals("K")) {
			return org.openfeed.InstrumentDefinition.Month.MAY;
		} else if(m.equals("M")) {
			return org.openfeed.InstrumentDefinition.Month.JUNE;
		} else if(m.equals("N")) {
			return org.openfeed.InstrumentDefinition.Month.JULY;
		} else if(m.equals("Q")) {
			return org.openfeed.InstrumentDefinition.Month.AUGUST;
		} else if(m.equals("U")) {
			return org.openfeed.InstrumentDefinition.Month.SEPTEMBER;
		} else if(m.equals("V")) {
			return org.openfeed.InstrumentDefinition.Month.OCTOBER;
		} else if(m.equals("X")) {
			return org.openfeed.InstrumentDefinition.Month.NOVEMBER;
		} else if(m.equals("Z")) {
			return org.openfeed.InstrumentDefinition.Month.DECEMBER;
		}
		
		return org.openfeed.InstrumentDefinition.Month.NULL_MONTH;
	}
	
	public static Decimal buildDecimal(final long mantissa, final int exponent) {

		final Decimal.Builder builder = Decimal.newBuilder();
		builder.setMantissa(mantissa);
		builder.setExponent(exponent);
		return builder.build();

	}

	// OLD
	// final String symbolHist = xmlStringDecode(ats, SYMBOL_HIST, XML_STOP);
	// final String symbolDDFReal = xmlStringDecode(ats, SYMBOL_DDF_REAL,
	// XML_STOP);
	// final String exchangeComment = xmlStringDecode(ats, EXCHANGE_COMMENT,
	// XML_PASS);
	// final String ddf_expire_month = xmlStringDecode(ats,
	// SYMBOL_DDF_EXPIRE_MONTH, XML_PASS);
	// final String ddf_expire_year = xmlStringDecode(ats,
	// SYMBOL_DDF_EXPIRE_YEAR, XML_PASS);

	/*
	 * <instruments status="200" count="1"> <instrument lookup="IBM"
	 * status="200" guid="IBM" id="1298146" symbol_realtime="IBM"
	 * symbol_ddf="IBM" symbol_historical="IBM"
	 * symbol_description="International Business Machines Corp."
	 * symbol_cfi="EXXXXX" exchange="XNYS" exchange_channel="NYSE"
	 * exchange_description="New York Stock Exchange" exchange_ddf="N"
	 * time_zone_ddf="America/New_York" tick_increment="1" unit_code="2"
	 * base_code="A" point_value="1"/> </instruments>
	 */

	/*
	 * <instruments status="200" count="1"> <instrument lookup="ESM3"
	 * status="200" guid="ESM2013" id="94112573" symbol_realtime="ESM2013"
	 * symbol_ddf="ESM3" symbol_historical="ESM13"
	 * symbol_description="E-Mini S&P 500"
	 * symbol_expire="2013-06-21T23:59:59-05:00" symbol_ddf_expire_month="M"
	 * symbol_ddf_expire_year="3" symbol_cfi="FXXXXX" exchange="XCME"
	 * exchange_channel="GBLX" exchange_description="CMEGroup CME (Globex Mini)"
	 * exchange_ddf="M" time_zone_ddf="America/Chicago" tick_increment="25"
	 * base_code="A" unit_code="2" point_value="50"/> </instruments>
	 */

	/*
	 * <instruments status="200" count="1"> <instrument lookup="_S_FX_A6H2_A6Z1"
	 * status="200" guid="_S_FX_A6H2_A6Z1" id="1819728"
	 * symbol_realtime="_S_FX_A6H2_A6Z1" symbol_ddf="_S_FX_A6H2_A6Z1"
	 * symbol_historical="_S_FX_A6H2_A6Z1"
	 * symbol_description="Australian Dollar Futures Foreign Exchange Spread"
	 * symbol_cfi="FMXXXX" exchange="XIMM" exchange_channel="IMM"
	 * exchange_description="CMEGroup CME" exchange_ddf="M"
	 * time_zone_ddf="America/New_York" tick_increment="1" unit_code="5"
	 * base_code="D" point_value="1"/> </instruments>
	 */

}
