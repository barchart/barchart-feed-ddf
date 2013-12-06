/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.enums;

import static com.barchart.feed.ddf.symbol.enums.DDF_TimeZone.CHICAGO;
import static com.barchart.feed.ddf.symbol.enums.DDF_TimeZone.NEW_YORK;
import static com.barchart.util.common.ascii.ASCII.QUEST;
import static com.barchart.util.common.ascii.ASCII._0_;
import static com.barchart.util.common.ascii.ASCII._1_;
import static com.barchart.util.common.ascii.ASCII._2_;
import static com.barchart.util.common.ascii.ASCII._3_;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.values.api.Value;
import com.barchart.util.common.math.MathExtra;

// TODO: Auto-generated Javadoc
/**
 * ddf market channels "kind" qualifiers.
 */
public enum DDF_ExchangeKind implements Value<DDF_ExchangeKind> {

	FUTURE(_0_, CHICAGO), //

	STOCK(_1_, NEW_YORK), //

	INDEX(_2_, NEW_YORK), //

	FOREX(_3_, CHICAGO), //

	//

	UNKNOWN(QUEST, DDF_TimeZone.LOCAL), //

	/** The ord. */
 ;

	// /////////////////////////

	/** byte sized enum ordinal */
	public final byte ord;

	/** ddf encoding of this enum. */
	public final byte code;

	/** The time. */
	public final DDF_TimeZone time;

	// /////////////////////////

	private DDF_ExchangeKind(final byte code, final DDF_TimeZone zone) {
		this.ord = (byte) ordinal();
		this.code = code;
		this.time = zone;
	}

	private final static DDF_ExchangeKind[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ exchange kind[]
	 */
	@Deprecated
	public final static DDF_ExchangeKind[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	/**
	 * From code.
	 *
	 * @param code the code
	 * @return the dD f_ exchange kind
	 */
	public final static DDF_ExchangeKind fromCode(final byte code) {
		for (final DDF_ExchangeKind known : ENUM_VALUES) {
			if (known.code == code) {
				return known;
			}
		}
		return UNKNOWN;
	}

	/**
	 * From ord.
	 *
	 * @param ord the ord
	 * @return the dD f_ exchange kind
	 */
	public final static DDF_ExchangeKind fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	/**
	 * Checks if is known.
	 *
	 * @return true, if is known
	 */
	public final boolean isKnown() {
		return this != UNKNOWN;
	}

	/* (non-Javadoc)
	 * @see com.barchart.util.values.api.Value#freeze()
	 */
	@Override
	public DDF_ExchangeKind freeze() {
		return this;
	}

	/* (non-Javadoc)
	 * @see com.barchart.util.values.api.Value#isFrozen()
	 */
	@Override
	public boolean isFrozen() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.barchart.util.values.api.Value#isNull()
	 */
	@Override
	public boolean isNull() {
		return this == UNKNOWN;
	}
	
	public Instrument.SecurityType asSecType() {
		switch(this) {
		default:
			return Instrument.SecurityType.NULL_TYPE;
		case FUTURE:
			return Instrument.SecurityType.FUTURE;
		case STOCK:
			return Instrument.SecurityType.EQUITY;
		case INDEX:
			return Instrument.SecurityType.INDEX;
		case FOREX:
			return Instrument.SecurityType.FOREX;
		}
		
	}

}
