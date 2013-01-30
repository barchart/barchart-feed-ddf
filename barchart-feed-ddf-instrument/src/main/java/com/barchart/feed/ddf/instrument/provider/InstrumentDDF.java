/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.instrument.enums.InstrumentField.*;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.*;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_EXCH_DESC;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_SPREAD;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_SYMBOL_HISTORICAL;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_SYMBOL_REALTIME;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_SYMBOL_UNIVERSAL;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_ZONE;

import java.util.HashMap;
import java.util.Map;

import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField;
import com.barchart.feed.ddf.instrument.enums.InstrumentField;
import com.barchart.feed.ddf.instrument.enums.InstrumentFieldDDF;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.feed.inst.api.Instrument;
import com.barchart.feed.inst.api.InstrumentConst;
import com.barchart.feed.inst.api.InstrumentGUID;
import com.barchart.feed.inst.enums.MarketDisplay;
import com.barchart.feed.inst.provider.InstrumentBase;
import com.barchart.feed.inst.provider.InstrumentFactory;
import com.barchart.missive.core.MissiveException;
import com.barchart.missive.core.Tag;
import com.barchart.util.enums.ParaEnumBase;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.api.TimeValue;
import com.barchart.util.values.api.Value;

class InstrumentDDF extends InstrumentBase implements DDF_Instrument {

	private final Instrument inst;
	
	@SuppressWarnings("rawtypes")
	private static final Map<ParaEnumBase, Tag> paraEnumMap = 
			new HashMap<ParaEnumBase, Tag>();

	static {
		paraEnumMap.put(ID, com.barchart.feed.inst.api.InstrumentField.ID);
		paraEnumMap.put(GROUP_ID, com.barchart.feed.inst.api.InstrumentField.GROUP_ID);
		paraEnumMap.put(EXCHANGE_ID, com.barchart.feed.inst.api.InstrumentField.EXCHANGE_ID);
		paraEnumMap.put(SYMBOL, com.barchart.feed.inst.api.InstrumentField.SYMBOL);
		paraEnumMap.put(DESCRIPTION, com.barchart.feed.inst.api.InstrumentField.DESCRIPTION);
		paraEnumMap.put(BOOK_SIZE, com.barchart.feed.inst.api.InstrumentField.BOOK_SIZE);
		paraEnumMap.put(BOOK_TYPE, com.barchart.feed.inst.api.InstrumentField.BOOK_TYPE);
		paraEnumMap.put(PRICE_STEP, com.barchart.feed.inst.api.InstrumentField.PRICE_STEP);
		paraEnumMap.put(PRICE_POINT, com.barchart.feed.inst.api.InstrumentField.PRICE_POINT);
		paraEnumMap.put(FRACTION, com.barchart.feed.inst.api.InstrumentField.FRACTION);
		paraEnumMap.put(CURRENCY, com.barchart.feed.inst.api.InstrumentField.CURRENCY);
		paraEnumMap.put(TYPE, com.barchart.feed.inst.api.InstrumentField.TYPE);
		paraEnumMap.put(TIME_ZONE, com.barchart.feed.inst.api.InstrumentField.TIME_ZONE);
		paraEnumMap.put(TIME_OPEN, com.barchart.feed.inst.api.InstrumentField.TIME_OPEN);
		paraEnumMap.put(TIME_CLOSE, com.barchart.feed.inst.api.InstrumentField.TIME_CLOSE);
		paraEnumMap.put(DATE_START, com.barchart.feed.inst.api.InstrumentField.DATE_START);
		paraEnumMap.put(DATE_FINISH, com.barchart.feed.inst.api.InstrumentField.DATE_FINISH);
		
		paraEnumMap.put(DDF_SYMBOL_REALTIME, InstrumentFieldDDF.DDF_SYMBOL_REALTIME);
		paraEnumMap.put(DDF_SYMBOL_HISTORICAL, InstrumentFieldDDF.DDF_SYMBOL_HISTORICAL);
		paraEnumMap.put(DDF_SYMBOL_UNIVERSAL, InstrumentFieldDDF.DDF_SYMBOL_UNIVERSAL);
		paraEnumMap.put(DDF_EXCHANGE, InstrumentFieldDDF.DDF_EXCHANGE);
		paraEnumMap.put(DDF_EXCH_DESC, InstrumentFieldDDF.DDF_EXCH_DESC);
		paraEnumMap.put(DDF_SPREAD, InstrumentFieldDDF.DDF_SPREAD);
		paraEnumMap.put(DDF_ZONE, InstrumentFieldDDF.DDF_ZONE);
		paraEnumMap.put(DDF_EXPIRE_MONTH, InstrumentFieldDDF.DDF_EXPIRE_MONTH);
		paraEnumMap.put(DDF_EXPIRE_YEAR, InstrumentFieldDDF.DDF_EXPIRE_YEAR);
	}
	
	InstrumentDDF(final Instrument inst) {
		this.inst = inst;
	}
	
	// Null version
	InstrumentDDF() {
		inst = InstrumentConst.NULL_INSTRUMENT;
	}
	
	// Service Basic DDF
	InstrumentDDF(final TextValue symbol) {
		
		final Map<Tag, Object> map = new HashMap<Tag, Object>();
		
		map.put(com.barchart.feed.inst.api.InstrumentField.SYMBOL, symbol);
		inst = InstrumentFactory.build(map);
	}

	//

	@SuppressWarnings("unchecked")
	@Override
	public <V extends Value<V>> V get(final DDF_InstrumentField<V> field) {
		return (V) inst.get(paraEnumMap.get(field));
	}

	@Override
	public boolean isFrozen() {
		return true;
	}

	@Override
	public final InstrumentDDF freeze() {
		return this;
	}

	@Override
	public final boolean isNull() {
		return this == DDF_InstrumentProvider.NULL_INSTRUMENT || 
				inst == DDF_InstrumentProvider.NULL_INSTRUMENT;
	}

	//
	private DDF_Fraction getFractionDDF() {
		return DDF_Fraction.fromFraction(get(FRACTION));
	}

	private final String getFractionDescription() {
		final DDF_Fraction frac = getFractionDDF();
		return frac + " (" + frac.fraction.description + ")";
	}

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

	@SuppressWarnings("unchecked")
	@Override
	public <V extends Value<V>> V get(final InstrumentField<V> field) {
		return (V) inst.get(paraEnumMap.get(field));
	}

	@Override
	public InstrumentGUID getGUID() {
		return inst.getGUID();
	}

	@Override
	public <V> V get(final Tag<V> tag) throws MissiveException {
		return inst.get(tag);
	}

	@Override
	public boolean contains(final Tag<?> tag) {
		return inst.contains(tag);
	}

	@Override
	public Tag<?>[] tags() {
		return inst.tags();
	}

	@Override
	public int size() {
		return inst.size();
	}

}
