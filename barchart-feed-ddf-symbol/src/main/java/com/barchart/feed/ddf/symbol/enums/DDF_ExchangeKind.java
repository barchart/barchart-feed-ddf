/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.enums;

import static com.barchart.feed.ddf.symbol.enums.DDF_TimeZone.*;
import static com.barchart.util.ascii.ASCII.*;

import com.barchart.util.math.MathExtra;
import com.barchart.util.values.api.Value;

/**
 * ddf market channels "kind" qualifiers
 */
public enum DDF_ExchangeKind implements Value<DDF_ExchangeKind> {

	FUTURE(_0_, CHICAGO), //

	STOCK(_1_, NEW_YORK), //

	INDEX(_2_, NEW_YORK), //

	FOREX(_3_, NEW_YORK), //

	//

	UNKNOWN(QUEST, DDF_TimeZone.LOCAL), //

	;

	// /////////////////////////

	/** byte sized enum ordinal */
	public final byte ord;

	/** ddf encoding of this enum */
	public final byte code;

	public final DDF_TimeZone time;

	// /////////////////////////

	private DDF_ExchangeKind(final byte code, final DDF_TimeZone zone) {
		this.ord = (byte) ordinal();
		this.code = code;
		this.time = zone;
	}

	private final static DDF_ExchangeKind[] ENUM_VALUES = values();

	@Deprecated
	public final static DDF_ExchangeKind[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	public final static DDF_ExchangeKind fromCode(final byte code) {
		for (final DDF_ExchangeKind known : ENUM_VALUES) {
			if (known.code == code) {
				return known;
			}
		}
		return UNKNOWN;
	}

	public final static DDF_ExchangeKind fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	public final boolean isKnown() {
		return this != UNKNOWN;
	}

	@Override
	public DDF_ExchangeKind freeze() {
		return this;
	}

	@Override
	public boolean isFrozen() {
		return true;
	}

	@Override
	public boolean isNull() {
		return this == UNKNOWN;
	}

}
