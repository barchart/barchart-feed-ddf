/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.api.fields.InstrumentField.BOOK_DEPTH;
import static com.barchart.feed.api.fields.InstrumentField.CURRENCY_CODE;
import static com.barchart.feed.api.fields.InstrumentField.DESCRIPTION;
import static com.barchart.feed.api.fields.InstrumentField.EXCHANGE_CODE;
import static com.barchart.feed.api.fields.InstrumentField.LIFETIME;
import static com.barchart.feed.api.fields.InstrumentField.POINT_VALUE;
import static com.barchart.feed.api.fields.InstrumentField.PRICE_STEP;
import static com.barchart.feed.api.fields.InstrumentField.SYMBOL;
import static com.barchart.feed.api.fields.InstrumentField.TIME_ZONE_OFFSET;
import static com.barchart.feed.api.fields.InstrumentField.VENDOR;

import java.util.HashMap;
import java.util.Map;

import com.barchart.feed.api.fields.InstrumentField;
import com.barchart.feed.api.inst.Instrument;
import com.barchart.feed.api.inst.InstrumentGUID;
import com.barchart.feed.api.market.MarketDisplay;
import com.barchart.feed.base.provider.MarketDisplayBaseImpl;
import com.barchart.feed.inst.provider.InstrumentBase;
import com.barchart.feed.inst.provider.InstrumentFactory;
import com.barchart.missive.core.MissiveException;
import com.barchart.missive.core.Tag;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.api.TimeValue;

class InstrumentDDF extends InstrumentBase implements Instrument {

	private final Instrument inst;
	
	private static final MarketDisplay display = new MarketDisplayBaseImpl();
	
	InstrumentDDF(final Instrument inst) {
		this.inst = inst;
	}
	
	// Null version
	InstrumentDDF() {
		inst = Instrument.NULL_INSTRUMENT;
	}
	
	// Service Basic DDF
	@SuppressWarnings("rawtypes")
	InstrumentDDF(final TextValue symbol) {
		
		final Map<Tag, Object> map = new HashMap<Tag, Object>();
		
		map.put(com.barchart.feed.api.fields.InstrumentField.SYMBOL, symbol);
		inst = InstrumentFactory.build(map);
	}

	//

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
		return this == Instrument.NULL_INSTRUMENT || 
				inst == Instrument.NULL_INSTRUMENT;
	}

	@Override
	public final String toString() {
		return "" + //
				"\n id          : " + get(SYMBOL) + //
				"\n description : " + get(DESCRIPTION) + //
				"\n book depth   : " + get(BOOK_DEPTH) + //
				"\n currency    : " + get(CURRENCY_CODE) + //
				"\n exchange    : " + get(EXCHANGE_CODE) + //
				"\n time zone offset  : " + get(TIME_ZONE_OFFSET) + //
				"\n priceStep   : " + get(PRICE_STEP) + //
				"\n pointValue  : " + get(POINT_VALUE) + //
				"\n fullText    : " + fullText() + //
				"";
	}

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

		final TimeValue expire = inst.get(LIFETIME).stop();
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
