/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.enums;

import com.barchart.util.enums.EnumByteOrdinal;
import com.barchart.util.enums.EnumCodeString;
import com.barchart.util.math.MathExtra;

/**
 * ddf quote session indicator : "current combo" vs "previous combo" vs
 * "individual session"; possible format:
 * 
 * "combined", is combo for today;
 * 
 * "previous", is combo for yesterday;
 * 
 * "session_D_S", individual (non-combo) day/session permutations; with
 * D=DDF_TradeDay, S=DDF_Session;
 * 
 * allows to classify quote snapshot sessions: THIS vs PAST market day
 * 
 */
public enum DDF_Indicator implements EnumCodeString, EnumByteOrdinal {

	/** default or combo for this trading day */
	CURRENT("combined"),

	/** default or combo for past trading day */
	PREVIOUS("previous"),

	/** non-combo, specific for a trading day + session pair */
	UNKNOWN(""), //

	;

	public final byte ord;

	public final String code;

	@Override
	public final byte ord() {
		return ord;
	}

	@Override
	public final String code() {
		return code;
	}

	private DDF_Indicator(final String code) {
		this.ord = (byte) ordinal();
		this.code = code;
	}

	private final static DDF_Indicator[] ENUM_VALUES = values();

	@Deprecated
	public final static DDF_Indicator[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	public final static DDF_Indicator fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	public final static DDF_Indicator fromCode(final String code) {
		for (final DDF_Indicator known : ENUM_VALUES) {
			if (known.code.equalsIgnoreCase(code)) {
				return known;
			}
		}
		return UNKNOWN;
	}

	public final boolean isKnown() {
		return this != UNKNOWN;
	}

}
