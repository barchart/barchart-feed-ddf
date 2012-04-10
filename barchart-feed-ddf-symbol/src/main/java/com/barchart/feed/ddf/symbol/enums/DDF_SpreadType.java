/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.enums;

import com.barchart.feed.ddf.util.ByteConverters;
import com.barchart.util.math.MathExtra;
import com.barchart.util.values.api.Value;
import com.barchart.util.values.provider.ValueBuilder;

/**
 * ddf spread type
 */
public enum DDF_SpreadType implements Value<DDF_SpreadType> {

	DEFAULT("SP"), //

	//

	UNKNOWN("??"), //

	;

	public final byte ord;

	public final char code;

	private DDF_SpreadType(final String name) {
		this.ord = (byte) ordinal();
		assert name.length() == 2;
		assert ValueBuilder.isPureAscii(name);
		this.code = ByteConverters.charFromBytes((byte) name.charAt(0),
				(byte) name.charAt(1));
	}

	private final static DDF_SpreadType[] ENUM_VALUES = values();

	@Deprecated
	public final static DDF_SpreadType[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	public final static DDF_SpreadType fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	public final static DDF_SpreadType fromCode(final char code) {
		for (final DDF_SpreadType known : ENUM_VALUES) {
			if (known.code == code) {
				return known;
			}
		}
		return UNKNOWN;
	}

	public final boolean isKnown() {
		return this != UNKNOWN;
	}

	@Override
	public DDF_SpreadType freeze() {
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
