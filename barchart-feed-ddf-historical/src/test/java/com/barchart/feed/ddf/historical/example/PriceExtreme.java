/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.example;

import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.api.framework.data.InstrumentField;
import com.barchart.feed.api.framework.market.MarketDisplay;
import com.barchart.feed.base.provider.MarketDisplayBaseImpl;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.provider.ValueBuilder;

/**
 * helper class to demonstrate batch processing of historical data
 */
class PriceExtreme {

	Instrument inst;
	
	static final MarketDisplay display = new MarketDisplayBaseImpl();

	PriceExtreme(final Instrument instrument) {
		this.inst = instrument;
	}

	long mantissaMin = Long.MAX_VALUE;

	long mantissaMax = Long.MIN_VALUE;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		final int exponent = (int)inst.get(InstrumentField.DISPLAY_FRACTION).decimalExponent();

		final PriceValue priceMin = ValueBuilder
				.newPrice(mantissaMin, exponent);
		final PriceValue priceMax = ValueBuilder
				.newPrice(mantissaMax, exponent);

		final String stringMin = display.priceText(priceMin, 
				inst.get(InstrumentField.DISPLAY_FRACTION));
		final String stringMax = display.priceText(priceMax, 
				inst.get(InstrumentField.DISPLAY_FRACTION));

		return String
				.format("minimum : %s  maximum : %s", stringMin, stringMax);

	}

}