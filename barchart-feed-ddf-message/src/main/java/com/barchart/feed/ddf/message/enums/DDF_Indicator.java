/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.enums.EnumByteOrdinal;
import com.barchart.feed.base.enums.EnumCodeString;
import com.barchart.util.common.math.MathExtra;

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
 * allows to classify quote snapshot sessions: THIS vs PAST market day.
 */
public enum DDF_Indicator implements EnumCodeString, EnumByteOrdinal {

	/** default or combo for this trading day */
	CURRENT("combined"),

	/** default or combo for past trading day */
	PREVIOUS("previous"),

	/** non-combo, specific for a trading day + session pair */
	UNKNOWN(""), //

	/** The ord. */
	;

	private static final Logger log = LoggerFactory.getLogger(
			DDF_Indicator.class);
	
	public final byte ord;

	/** The code. */
	public final String code;

	/* (non-Javadoc)
	 * @see com.barchart.util.enums.EnumByteOrdinal#ord()
	 */
	@Override
	public final byte ord() {
		return ord;
	}

	/* (non-Javadoc)
	 * @see com.barchart.util.enums.EnumCodeString#code()
	 */
	@Override
	public final String code() {
		return code;
	}

	private DDF_Indicator(final String code) {
		this.ord = (byte) ordinal();
		this.code = code;
	}

	private final static DDF_Indicator[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ indicator[]
	 */
	@Deprecated
	public final static DDF_Indicator[] valuesUnsafe() {
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
	 * @return the dD f_ indicator
	 */
	public final static DDF_Indicator fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	/**
	 * From code.
	 *
	 * @param code the code
	 * @return the dD f_ indicator
	 */
	public final static DDF_Indicator fromCode(final String code) {
		for (final DDF_Indicator known : ENUM_VALUES) {
			if (known.code.equalsIgnoreCase(code)) {
				return known;
			}
		}
		log.debug("Unknown Indicator {}", code);
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

}
