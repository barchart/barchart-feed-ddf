package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.*;
import static com.barchart.feed.ddf.util.HelperXML.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.xml.sax.Attributes;

import com.barchart.feed.api.model.meta.Exchange;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.api.model.meta.id.VendorID;
import com.barchart.feed.api.model.meta.instrument.Event;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.feed.inst.Exchanges;
import com.barchart.feed.meta.instrument.DefaultCalendar;
import com.barchart.feed.meta.instrument.DefaultEvent;
import com.barchart.feed.meta.instrument.DefaultInstrument;
import com.barchart.util.value.ValueFactoryImpl;
import com.barchart.util.value.api.Fraction;
import com.barchart.util.value.api.Price;
import com.barchart.util.value.api.ValueFactory;

public class DDF_Instrument extends DefaultInstrument implements InstrumentState {

	protected static final ValueFactory VALUES = ValueFactoryImpl.getInstance();

	protected LoadState loadState = LoadState.NULL;

	protected Fraction displayFraction = Fraction.NULL;

	public DDF_Instrument(final InstrumentID id_) {

		super(id_);

		loadState = LoadState.EMPTY;

	}

	public DDF_Instrument(final String symbol_) {

		super(new InstrumentID(symbol_));

		symbol = symbol_;

		loadState = LoadState.EMPTY;

	}

	public DDF_Instrument(final InstrumentID id_, final Instrument stub_, final LoadState state_) {

		super(id_);

		copy(stub_);

		loadState = state_;

	}

	public DDF_Instrument(final Attributes attr) throws Exception {

		super(xmlId(attr));

		/* vendor */
		vendor = VendorID.BARCHART;

		/* market symbol; can be non unique; */
		symbol = xmlStringDecode(attr, SYMBOL_REALTIME, XML_STOP);

		/* Barchart symbol, only used for options */
		final String bar = xmlStringDecode(attr, SYMBOL_DDF_REAL, XML_PASS);
		if (bar != null) {
			vendorSymbols.put(VendorID.BARCHART, bar);
		}

		/* Barchart historical symbol, used for historical data queries */
		final String hist = xmlStringDecode(attr, SYMBOL_HIST, XML_PASS);
		if (hist != null) {
			vendorSymbols.put(VendorID.BARCHART_HISTORICAL, hist);
		}

		// NOTE: Hard coded for CQG
		final String cqg = xmlStringDecode(attr, ALT_SYMBOL, XML_PASS);
		if (cqg != null) {
			vendorSymbols.put(DDF_RxInstrumentProvider.CQG_VENDOR_ID, cqg);
		}

		/* market free style description; can be used in full text search */
		description = String.valueOf(xmlStringDecode(attr, SYMBOL_COMMENT, XML_PASS));

		/* stock vs future vs etc. */
		CFICode = xmlStringDecode(attr, SYMBOL_CODE_CFI, XML_PASS);

		securityType = SecurityType.fromCFI(CFICode);

		/* price currency */
		currencyCode = "USD";

		/* market originating exchange identifier */
		final DDF_Exchange exchange = DDF_Exchange.fromCode(xmlByteDecode(attr, EXCHANGE_DDF, XML_PASS));

		String eCode = new String(new byte[] {
				exchange.code
		});

		if (eCode == null || eCode.isEmpty()) {
			eCode = Exchanges.NULL_CODE;
		}

		exchangeCode = eCode;

		final DDF_Fraction frac = DDF_Fraction.fromBaseCode(xmlByteDecode(attr, BASE_CODE_DDF, XML_STOP));

		/* price step / increment size / tick size */
		try {
			final long priceStepMantissa = xmlDecimalDecode(frac, attr, PRICE_TICK_INCREMENT, XML_STOP);
			tickSize = VALUES.newPrice(priceStepMantissa, frac.decimalExponent);
		} catch (final Exception e) {
			tickSize = VALUES.newPrice(0, 0);
		}

		/* value of a future contract / stock share */
		final String pricePointString = xmlStringDecode(attr, PRICE_POINT_VALUE, XML_PASS);
		if (pricePointString == null || pricePointString.isEmpty()) {
			pointValue = VALUES.newPrice(0, 0);
		} else {
			pointValue = VALUES.newPrice(Double.valueOf(pricePointString));
		}

		displayFraction = VALUES.newFraction((int) frac.fraction.base(), frac.fraction.exponent());

		try {

			String zoneName = xmlStringDecode(attr, TIME_ZONE_DDF, XML_STOP);

			if (zoneName.equals("NEW_YORK")) {
				zoneName = "America/New_York";
			} else if (zoneName.equals("CHICAGO")) {
				zoneName = "America/Chicago";
			}

			timeZone = DateTimeZone.forID(zoneName);

		} catch (final Exception e) {
			timeZone = DateTimeZone.forID("America/Chicago");
		}

		/* Expiration */
		try {

			final String expireStr = xmlStringDecode(attr, SYMBOL_EXPIRE, XML_PASS);

			if (expireStr != null && !expireStr.isEmpty()) {

				final DefaultCalendar cal = new DefaultCalendar();
				calendar = cal;

				final DateTime expire = new DateTime(expireStr);
				cal.add(new DefaultEvent(Event.Type.LAST_TRADE_DATE, expire));

				/* Delivery */
				final int delMonth = month(xmlStringDecode(attr, SYMBOL_DDF_EXPIRE_MONTH, XML_PASS));

				if (delMonth > 0) {

					DateTime delivery = new DateTime(expire);

					if (delMonth < expire.getMonthOfYear()) {
						// Year rollover, advance a year before setting month
						delivery = delivery.plusYears(1);
					}

					delivery = delivery.withMonthOfYear(delMonth);
					cal.add(new DefaultEvent(Event.Type.LAST_DELIVERY_DATE, delivery));

				}

			}

		} catch (final Exception e) {
		}

		loadState = LoadState.FULL;

	}

	private static InstrumentID xmlId(final Attributes attr) throws SymbolNotFoundException {

		// lookup status
		final String statusCode = xmlStringDecode(attr, STATUS, XML_STOP);
		final StatusXML status = StatusXML.fromCode(statusCode);

		if (!status.isFound()) {
			final String lookup = xmlStringDecode(attr, LOOKUP, XML_STOP);
			throw new SymbolNotFoundException(lookup);
		}

		/* market identifier; must be globally unique; */
		try {
			final Long id = Long.parseLong(xmlStringDecode(attr, ID, XML_STOP));
			return new InstrumentID(String.valueOf(id));
		} catch (final Exception e) {
			/* Ensure no id collision by making negative */
			final Long id = Long.valueOf(Math.abs(xmlStringDecode(attr, ID, XML_STOP).hashCode()) * -1);
			return new InstrumentID(String.valueOf(id));
		}

	}

	private static int month(final String m) {

		if (m.equals("F")) {
			return 1;
		} else if (m.equals("G")) {
			return 2;
		} else if (m.equals("H")) {
			return 3;
		} else if (m.equals("J")) {
			return 4;
		} else if (m.equals("K")) {
			return 5;
		} else if (m.equals("M")) {
			return 6;
		} else if (m.equals("N")) {
			return 7;
		} else if (m.equals("Q")) {
			return 8;
		} else if (m.equals("U")) {
			return 9;
		} else if (m.equals("V")) {
			return 10;
		} else if (m.equals("X")) {
			return 11;
		} else if (m.equals("Z")) {
			return 12;
		}

		return 0;

	}

	@Override
	public LoadState loadState() {
		return loadState;
	}

	@Override
	public void process(final Instrument inst) {

		copy(inst);

		loadState = LoadState.FULL;

	}

	protected void copy(final Instrument inst) {

		// Update parent fields
		securityType = inst.securityType();
		liquidityType = inst.liquidityType();
		bookStructure = inst.bookStructure();
		maxBookDepth = inst.maxBookDepth();
		vendor = inst.vendor();
		description = inst.description();
		CFICode = inst.CFICode();
		tickSize = inst.tickSize();
		pointValue = inst.pointValue();
		calendar = inst.calendar();
		schedule = inst.schedule();
		symbol = inst.symbol();
		currencyCode = inst.currencyCode();
		exchangeCode = inst.exchangeCode();
		instrumentGroup = inst.instrumentGroup();
		state = inst.state();
		channel = inst.channel();
		created = inst.created();
		updated = inst.updated();
		timeZone = inst.timeZone();
		priceFormat = inst.priceFormat();
		optionStrikePriceFormat = inst.optionStrikePriceFormat();
		transactionPriceConversionFactor = inst.transactionPriceConversionFactor();
		underlier = inst.underlier();
		strikePrice = inst.strikePrice();
		optionType = inst.optionType();
		optionStyle = inst.optionStyle();
		spreadType = inst.spreadType();

		vendorSymbols.clear();
		vendorSymbols.putAll(inst.vendorSymbols());

		components.clear();
		components.addAll(inst.components());

		spreadLegs.clear();
		spreadLegs.addAll(inst.spreadLegs());

	}

	@Override
	public void reset() {
		loadState = LoadState.NULL;
	}

	/*
	 * Derived field values specific to extras lookup
	 */

	@Override
	public String marketGUID() {
		return symbol();
	}

	@Override
	public Exchange exchange() {
		return Exchanges.fromCode(exchangeCode());
	}

	@Override
	public Price transactionPriceConversionFactor() {

		// Temporary hack for specific contracts until we move to OpenFeed
		// definitions

		if (CFICode().startsWith("F") && (symbol().startsWith("SI") || symbol().startsWith("HG"))) {
			return VALUES.newPrice(1, -2);
		}

		if (CFICode().startsWith("F") && symbol().startsWith("J6")) {
			return VALUES.newPrice(1, 2);
		}

		return Price.ONE;
	}

	@Override
	public Fraction displayFraction() {
		return displayFraction;
	}

}
