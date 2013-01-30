/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.example;

import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.enums.InstrumentField;
import com.barchart.feed.inst.enums.MarketDisplay.Fraction;
import com.barchart.feed.inst.enums.MarketDisplay;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.provider.ValueBuilder;

/**
 * helper class to demonstrate batch processing of historical data
 */
class PriceExtreme {

	DDF_Instrument instrument;

	PriceExtreme(final DDF_Instrument instrument) {
		this.instrument = instrument;
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

		final Fraction frac = instrument.get(InstrumentField.FRACTION);

		final int exponent = frac.decimalExponent;

		final PriceValue priceMin = ValueBuilder
				.newPrice(mantissaMin, exponent);
		final PriceValue priceMax = ValueBuilder
				.newPrice(mantissaMax, exponent);

		final String stringMin = MarketDisplay.priceText(priceMin, frac);
		final String stringMax = MarketDisplay.priceText(priceMax, frac);

		return String
				.format("minimum : %s  maximum : %s", stringMin, stringMax);

	}

}