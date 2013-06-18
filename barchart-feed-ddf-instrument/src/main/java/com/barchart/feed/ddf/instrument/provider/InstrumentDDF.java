/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.inst.InstrumentField.DESCRIPTION;
import static com.barchart.feed.inst.InstrumentField.EXCHANGE_CODE;
import static com.barchart.feed.inst.InstrumentField.LIFETIME;
import static com.barchart.feed.inst.InstrumentField.SYMBOL;
import static com.barchart.feed.inst.InstrumentField.VENDOR;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.market.api.MarketDisplay;
import com.barchart.feed.base.provider.MarketDisplayBaseImpl;
import com.barchart.feed.inst.InstrumentField;
import com.barchart.feed.inst.provider.InstrumentBase;
import com.barchart.util.values.api.TimeValue;
import com.barchart.util.values.provider.ValueBuilder;

class InstrumentDDF extends InstrumentBase implements Instrument {

	private static final MarketDisplay display = new MarketDisplayBaseImpl();
	
	//

//	@Override
//	public final String toString() {
//		return "" + //
//				"\n guid           : " + get(MARKET_GUID) + //
//				"\n symbol       :" + get(SYMBOL) + //
//				"\n description  : " + get(DESCRIPTION) + //
//				"\n book depth   : " + get(BOOK_DEPTH) + //
//				"\n currency     : " + get(CURRENCY_CODE) + //
//				"\n exchange     : " + get(EXCHANGE_CODE) + //
//				"\n time zone offset  : " + get(TIME_ZONE_OFFSET) + //
//				"\n priceStep    : " + get(PRICE_STEP) + //
//				"\n pointValue   : " + get(POINT_VALUE) + //
//				"\n fullText     : " + fullText() + //
//				"";
//	}
	
	final static String SPACE = " ";

	private void addSpreadComponents(final StringBuilder text) {

		String id = get(InstrumentField.SYMBOL).toString();

		/** ddf prefix in spread symbology */
		if (id.startsWith("_S_")) {

			id = id.replaceFirst("_S_", "");

			id = id.replaceAll("_", " ");

			text.append(id);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.instrument.api.DDF_Instrument#fullText()
	 */
	public String fullText() {

		final StringBuilder text = new StringBuilder(256);

		text.append(get(VENDOR));
		text.append(SPACE);

		text.append(get(SYMBOL));
		text.append(SPACE);

		text.append(get(DESCRIPTION));
		text.append(SPACE);

		text.append(get(EXCHANGE_CODE));
		text.append(SPACE);

		addSpreadComponents(text);

		final TimeValue expire = ValueBuilder.newTime(get(LIFETIME).stop().millisecond());
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

	@Override 
	public Instrument instrument() {
		return this;
	}
	
}
