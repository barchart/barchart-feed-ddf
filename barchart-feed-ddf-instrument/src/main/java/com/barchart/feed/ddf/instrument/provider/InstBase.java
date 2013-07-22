package com.barchart.feed.ddf.instrument.provider;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.market.api.MarketDisplay;
import com.barchart.feed.base.provider.MarketDisplayBaseImpl;
import com.barchart.feed.inst.provider.InstrumentBase;
import com.barchart.util.values.api.TimeValue;
import com.barchart.util.values.provider.ValueBuilder;

/**
 * Base implementation, implement symbol and marketGUID
 */
public abstract class InstBase extends InstrumentBase implements Instrument {
	
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
