package com.barchart.feed.ddf.instrument.provider.ext;

import java.util.Collections;
import java.util.List;

import com.barchart.feed.api.model.meta.Exchange;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.util.Identifier;
import com.barchart.feed.base.market.api.MarketDisplay;
import com.barchart.feed.base.provider.MarketDisplayBaseImpl;
import com.barchart.feed.inst.provider2.IdentifierImpl;
import com.barchart.util.value.api.Fraction;
import com.barchart.util.value.api.Price;
import com.barchart.util.value.api.Schedule;
import com.barchart.util.value.api.Size;
import com.barchart.util.value.api.TimeInterval;
import com.barchart.util.values.api.TimeValue;
import com.barchart.util.values.provider.ValueBuilder;

/**
 * Base implementation, implement symbol and marketGUID
 */
public abstract class InstBase implements Instrument {
	
	@Override
	public SecurityType securityType() {
		return SecurityType.NULL_TYPE;
	}
	
	@Override
	public BookLiquidityType liquidityType() {
		return BookLiquidityType.NONE;
	}

	@Override
	public BookStructureType bookStructure() {
		return BookStructureType.NONE;
	}

	@Override
	public Size maxBookDepth() {
		return Size.NULL;
	}
	
	@Override
	public String instrumentDataVendor() {
		return "Unknown Data Vendor";
	}

	@Override
	public String description() {
		return symbol();
	}
	
	@Override
	public String CFICode() {
		return "Unknown CFI Code";
	}

	@Override
	public Exchange exchange() {
		return Exchange.NULL;
	}
	
	@Override
	public Price tickSize() {
		return Price.NULL;
	}
	
	@Override
	public Price pointValue() {
		return Price.NULL;
	}
	
	@Override
	public Fraction displayFraction() {
		return Fraction.NULL;
	}
	
	@Override
	public TimeInterval lifetime() {
		return TimeInterval.NULL;
	}

	@Override
	public Schedule marketHours() {
		return Schedule.NULL;
	}
	
	@Override
	public String timeZoneName() {
		return "Null Time Zone";
	}
	
	// TODO remove these???
	@Override
	public long timeZoneOffset() {
		return 0;
	}
	
	@Override
	public List<Identifier> componentLegs() {
		return Collections.emptyList();
	}
	
	@Override
	public Identifier id() {
		return new IdentifierImpl(marketGUID());
	}
	
	@Override
	public int compareTo(final Instrument o) {
		return id().compareTo(o.id());
	}
	
	@Override
	public boolean equals(final Object o) {
		
		if(!(o instanceof Instrument)) {
			return false;
		}
		
		return compareTo((Instrument)o) == 0;
		
	}
	
	@Override
	public int hashCode() {
		return id().hashCode();
	}
	
	@Override
	public boolean isNull() {
		return this == Instrument.NULL;
	}
	
	@Override
	public MetaType type() {
		return MetaType.INSTRUMENT;
	}
	
	@Override
	public String toString() {
		return id().toString();
	}
	
	/* Methods from old InstrumentDDF */
	private static final MarketDisplay display = new MarketDisplayBaseImpl();
	final static String SPACE = " ";
	
	private void addSpreadComponents(final StringBuilder text) {

		String id = symbol().toString();

		/** ddf prefix in spread symbology */
		if (id.startsWith("_S_")) {

			id = id.replaceFirst("_S_", "");

			id = id.replaceAll("_", " ");

			text.append(id);

		}

	}
	
	public String fullText() {

		final StringBuilder text = new StringBuilder(256);

		text.append(instrumentDataVendor());
		text.append(SPACE);

		text.append(symbol());
		text.append(SPACE);

		text.append(description());
		text.append(SPACE);

		text.append(exchangeCode());
		text.append(SPACE);

		addSpreadComponents(text);

		final TimeValue expire = ValueBuilder.newTime(lifetime().stop().millisecond());
		if (!expire.isNull()) {

			text.append(display.timeMonthFull(expire));
			text.append(SPACE);

			text.append(display.timeYearFull(expire));
			text.append(SPACE);

			text.append(display.timeYearShort(expire));
			text.append(SPACE);

		}

		return text.toString();

	}
	
}
