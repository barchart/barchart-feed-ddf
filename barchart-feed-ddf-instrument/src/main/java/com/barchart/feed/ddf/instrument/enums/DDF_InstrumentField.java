/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.enums;

import static com.barchart.util.values.provider.ValueConst.*;

import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_SpreadType;
import com.barchart.feed.ddf.symbol.enums.DDF_TimeZone;
import com.barchart.util.anno.NotMutable;
import com.barchart.util.collections.BitSetEnum;
import com.barchart.util.enums.DictEnum;
import com.barchart.util.enums.ParaEnumBase;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.api.Value;

// TODO: Auto-generated Javadoc
/**
 * The Class DDF_InstrumentField.
 *
 * @param <V> the value type
 */
@NotMutable
public final class DDF_InstrumentField<V extends Value<V>> extends
		ParaEnumBase<V, DDF_InstrumentField<V>> implements
		BitSetEnum<DDF_InstrumentField<?>> {

	// ##################################

	/** ddf symbol used in market data feed; such as ESZ1. */
	public static final DDF_InstrumentField<TextValue> DDF_SYMBOL_REALTIME = NEW(NULL_TEXT);

	/** ddf symbol used in historical web service; such as ESZ11. */
	public static final DDF_InstrumentField<TextValue> DDF_SYMBOL_HISTORICAL = NEW(NULL_TEXT);

	/** ddf symbol guaranteed to be globally unique; such as ESZ2011. */
	public static final DDF_InstrumentField<TextValue> DDF_SYMBOL_UNIVERSAL = NEW(NULL_TEXT);

	/** ddf exchange source, such as AMEX. */
	public static final DDF_InstrumentField<DDF_Exchange> DDF_EXCHANGE = NEW(DDF_Exchange.UNKNOWN);

	/** ddf exchange description, such as NYSE Liffe. */
	public static final DDF_InstrumentField<TextValue> DDF_EXCH_DESC = NEW(NULL_TEXT);

	/** ddf spread type, such as SP. */
	public static final DDF_InstrumentField<DDF_SpreadType> DDF_SPREAD = NEW(DDF_SpreadType.UNKNOWN);

	/** ddf special time zone info. */
	public static final DDF_InstrumentField<DDF_TimeZone> DDF_ZONE = NEW(DDF_TimeZone.LOCAL);

	// ##################################

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public static int size() {
		return values().length;
	}

	/**
	 * Values.
	 *
	 * @return the dD f_ instrument field[]
	 */
	public static DDF_InstrumentField<?>[] values() {
		return DictEnum.valuesForType(DDF_InstrumentField.class);
	}

	private final long mask;

	/* (non-Javadoc)
	 * @see com.barchart.util.collections.BitSetEnum#mask()
	 */
	@Override
	public long mask() {
		return mask;
	}

	private DDF_InstrumentField() {
		super();
		mask = 0;
	}

	private DDF_InstrumentField(final V value) {
		super("", value);
		mask = ONE << ordinal();
	}

	private static final <X extends Value<X>> DDF_InstrumentField<X> NEW(X value) {
		return new DDF_InstrumentField<X>(value);
	}

}
