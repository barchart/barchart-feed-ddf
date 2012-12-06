/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.base.instrument.enums.InstrumentField.BOOK_SIZE;
import static com.barchart.feed.base.instrument.enums.InstrumentField.CURRENCY;
import static com.barchart.feed.base.instrument.enums.InstrumentField.DATE_FINISH;
import static com.barchart.feed.base.instrument.enums.InstrumentField.DESCRIPTION;
import static com.barchart.feed.base.instrument.enums.InstrumentField.FRACTION;
import static com.barchart.feed.base.instrument.enums.InstrumentField.PRICE_POINT;
import static com.barchart.feed.base.instrument.enums.InstrumentField.PRICE_STEP;
import static com.barchart.feed.base.instrument.enums.InstrumentField.TYPE;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_EXCHANGE;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_EXCH_DESC;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_SPREAD;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_SYMBOL_HISTORICAL;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_SYMBOL_REALTIME;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_SYMBOL_UNIVERSAL;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_ZONE;

import com.barchart.feed.base.instrument.enums.MarketDisplay;
import com.barchart.feed.base.provider.VarInstrument;
import com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.util.values.api.TimeValue;
import com.barchart.util.values.api.Value;

// TODO: Auto-generated Javadoc
// TODO freeze
class InstrumentDDF extends VarInstrument implements DDF_InstrumentDo {

	// Delete if nothing is broken
	// static final SizeValue BOOK_LIMIT = ValueBuilder
	// .newSize(DDF_MarketBook.ENTRY_LIMIT);

	private final static int ARRAY_SIZE = DDF_InstrumentField.size();

	protected final Value<?>[] valueArray;

	InstrumentDDF() {

		valueArray = new Value<?>[ARRAY_SIZE];

	}

	//

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.instrument.api.DDF_Instrument#get(com.barchart.
	 * feed.ddf.instrument.enums.DDF_InstrumentField)
	 */
	@Override
	public <V extends Value<V>> V get(final DDF_InstrumentField<V> field) {

		@SuppressWarnings("unchecked")
		final V value = (V) valueArray[field.ordinal()];

		if (value == null) {
			return field.value();
		} else {
			return value;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.instrument.provider.DDF_InstrumentDo#set(com.barchart
	 * .feed.ddf.instrument.enums.DDF_InstrumentField,
	 * com.barchart.util.values.api.Value)
	 */
	@Override
	public <V extends Value<V>> V set(final DDF_InstrumentField<V> field,
			final V value) {

		final int index = field.ordinal();

		@SuppressWarnings("unchecked")
		final V result = (V) valueArray[index];

		valueArray[index] = value;

		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.base.provider.instrument.provider.VarInstrument#isFrozen
	 * ()
	 */
	@Override
	public boolean isFrozen() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.base.provider.instrument.provider.VarInstrument#freeze
	 * ()
	 */
	@Override
	public final InstrumentDDF freeze() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.base.provider.instrument.provider.BaseInstrument#isNull
	 * ()
	 */
	@Override
	public final boolean isNull() {
		return this == DDF_InstrumentProvider.NULL_INSTRUMENT;
	}

	//
	private DDF_Fraction getFractionDDF() {
		return DDF_Fraction.fromFraction(get(FRACTION));
	}

	private final String getFractionDescription() {
		final DDF_Fraction frac = getFractionDDF();
		return frac + " (" + frac.fraction.description + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.base.provider.instrument.provider.BaseInstrument#toString
	 * ()
	 */
	@Override
	public final String toString() {
		return "" + //
				"\n id          : " + get(DDF_SYMBOL_UNIVERSAL) + //
				"\n description : " + get(DESCRIPTION) + //
				"\n book size   : " + get(BOOK_SIZE) + //
				"\n currency    : " + get(CURRENCY) + //
				"\n exchange    : " + get(DDF_EXCHANGE) + //
				"\n exch  kind  : " + get(DDF_EXCHANGE).kind + //
				"\n time zone   : " + get(DDF_ZONE).zone + //
				"\n spreadType  : " + get(DDF_SPREAD) + //
				"\n fraction    : " + getFractionDescription() + //
				"\n priceStep   : " + get(PRICE_STEP) + //
				"\n pointValue  : " + get(PRICE_POINT) + //
				"\n fullText    : " + fullText() + //
				"";
	}

	final static String SPACE = " ";

	private void addSpreadComponents(final StringBuilder text) {

		String id = get(DDF_SYMBOL_UNIVERSAL).toString();

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
	@Override
	public String fullText() {

		final StringBuilder text = new StringBuilder(256);

		text.append(get(DDF_SYMBOL_UNIVERSAL));
		text.append(SPACE);

		text.append(get(DDF_SYMBOL_HISTORICAL));
		text.append(SPACE);

		text.append(get(DDF_SYMBOL_REALTIME));
		text.append(SPACE);

		text.append(get(DESCRIPTION));
		text.append(SPACE);

		text.append(get(DDF_EXCHANGE));
		text.append(SPACE);

		// text.append(get(DDF_EXCHANGE).kind);
		// text.append(SPACE);

		text.append(get(DDF_EXCHANGE).description);
		text.append(SPACE);

		text.append(get(DDF_EXCH_DESC));
		text.append(SPACE);

		text.append(get(TYPE).getDescription());
		text.append(SPACE);

		addSpreadComponents(text);

		final TimeValue expire = get(DATE_FINISH);
		if (!expire.isNull()) {

			text.append(MarketDisplay.timeMonthFull(expire));
			text.append(SPACE);

			text.append(MarketDisplay.timeYearFull(expire));
			text.append(SPACE);

			text.append(MarketDisplay.timeYearShort(expire));
			text.append(SPACE);

		}

		return text.toString();

	}

}
