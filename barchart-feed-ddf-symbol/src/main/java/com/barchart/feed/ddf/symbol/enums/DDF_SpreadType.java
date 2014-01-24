/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.enums;

import com.barchart.feed.base.values.api.Value;
import com.barchart.feed.base.values.provider.ValueBuilder;
import com.barchart.feed.ddf.util.ByteConverters;
import com.barchart.util.common.math.MathExtra;

/**
 * ddf spread type.
 */
public enum DDF_SpreadType implements Value<DDF_SpreadType> {

	DEFAULT("SP", "Standard"),

	GENERIC("GN", "Generic"),

	CRACK("CR", "Crack Spread"),

	XMAS_TREE("XT", "Xmas Tree"),

	STRADDLE_STRIP("SS", "Straddle Strips"),

	RISK_REVERSAL("RR", "Risk Reversal"),

	STRIP_SR("SR", "Strip"),

	IRON_CONDOR("IC", "Iron Condor"),

	HORIZ_STRADDLE("HS", "Horizontal Straddle"),

	DOUBLE("DB ", "Double"),

	CONDOR_CO("CO", "Condor"),

	CONDITIONAL_CURVE("CC", "Conditional Curve"),

	BUTTERFLY_BO("BO", "Butterfly"),

	BOX("BX", "Box"),

	EQ_PUT_VERTICAL("VP", "Equity Put Vertical"),

	EQ_CALL_VERTICAL("VC", "Equity Call Vertical"),

	VERTICAL("VT", "Vertical"),

	STRANGLE("SG", "Strangle"),

	STRADDLE("ST", "Straddle"),

	HORIZ_CALENDAR("HO", "Horizontal Calendar"),

	DIAG_CALENDAR("DO", "Diagonal Calendar"),

	BUNDLE_SPREAD("BS", "Bundle Spread"),

	BUNDLE("FB", "Bundle"),

	PACK_SPREAD("PS", "Pack Spread"),

	DOUBLE_BUTTERFLY("DF", "Double Butterfly"),

	PACK_BUTTERFLY("PB", "Pack Butterfly"),

	MONTH_PACK("MP", "Month Pack"),

	PACK("PK", "Pack"),

	CRACK1("C1", "Crack"),

	INTERCOMMODITY("IS", "Inter-Commodity"),

	STRIP_FS("FS", "Strip"),

	CONDOR_CF("CF", "Condor"),

	BUTTERFLY_BF("BF", "Butterfly"),

	EQUITIES("EQ", "Equities"),

	REDUCED_TICK("RT", "Reduced Tick"),

	FOREX("FX", "Foreign Exchange"),

	THREE_WAY("3W", "3-Way"),

	RATIO_2_3("23", "Ratio 2x3"),

	RATIO_1_3("13", "Ratio 1x3"),

	RATIO_1_2("12", "Ratio 1x2"),

	UNKNOWN("??", "Unknown");

	public final byte ord;

	/** The code. */
	public final char code;
	public final String name;

	private DDF_SpreadType(final String code, final String name) {
		this.ord = (byte) ordinal();
		assert code.length() == 2;
		assert ValueBuilder.isPureAscii(code);
		this.code = ByteConverters.charFromBytes((byte) code.charAt(0),
				(byte) code.charAt(1));
		this.name = name;
	}

	private final static DDF_SpreadType[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ spread type[]
	 */
	@Deprecated
	public final static DDF_SpreadType[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	/**
	 * From ord.
	 *
	 * @param ord the ord
	 * @return the dD f_ spread type
	 */
	public final static DDF_SpreadType fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	/**
	 * From code.
	 *
	 * @param code the code
	 * @return the dD f_ spread type
	 */
	public final static DDF_SpreadType fromCode(final char code) {
		for (final DDF_SpreadType known : ENUM_VALUES) {
			if (known.code == code) {
				return known;
			}
		}
		return UNKNOWN;
	}

	/**
	 * Checks if is known.
	 *
	 * @return true, if is known
	 */
	public final boolean isKnown() {
		return this != UNKNOWN;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.barchart.util.values.api.Value#freeze()
	 */
	@Override
	public DDF_SpreadType freeze() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.barchart.util.values.api.Value#isFrozen()
	 */
	@Override
	public boolean isFrozen() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.barchart.util.values.api.Value#isNull()
	 */
	@Override
	public boolean isNull() {
		return this == UNKNOWN;
	}

}
