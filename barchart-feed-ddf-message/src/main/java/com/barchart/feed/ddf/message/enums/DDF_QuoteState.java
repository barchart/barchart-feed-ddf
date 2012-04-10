/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.enums;

import static com.barchart.util.ascii.ASCII.*;

import com.barchart.util.enums.EnumByteOrdinal;
import com.barchart.util.enums.EnumCodeByte;
import com.barchart.util.math.MathExtra;

// TODO: Auto-generated Javadoc
/**
 * ddf quote state transitions.
 */
public enum DDF_QuoteState implements EnumCodeByte, EnumByteOrdinal {

	/** received market close message */
	GOT_CLOSE(_c_), //

	/** received market settle message */
	GOT_SETTLE(_s_), //

	/** received preliminary market settle message */
	GOT_PRELIM_SETTLE(_P_), //

	/** detected "pre-open" state based on date change after close/settle */
	PRE_MARKET(_p_), //

	/** detected "market open" after pre-market due to any new message */
	UNKNOWN(QUEST), //

	/* (non-Javadoc)
  * @see com.barchart.util.enums.EnumByteOrdinal#ord()
  */
 ;

	@Override
	public final byte ord() {
		return ord;
	}

	/* (non-Javadoc)
	 * @see com.barchart.util.enums.EnumCodeByte#code()
	 */
	@Override
	public final byte code() {
		return code;
	}

	/** The ord. */
	public final byte ord;

	/** The code. */
	public final byte code;

	DDF_QuoteState(final byte code) {
		this.ord = (byte) ordinal();
		this.code = code;
	}

	private final static DDF_QuoteState[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ quote state[]
	 */
	@Deprecated
	public final static DDF_QuoteState[] valuesUnsafe() {
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
	 * @return the dD f_ quote state
	 */
	public final static DDF_QuoteState fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	/**
	 * From code.
	 *
	 * @param code the code
	 * @return the dD f_ quote state
	 */
	public final static DDF_QuoteState fromCode(final byte code) {
		for (final DDF_QuoteState known : ENUM_VALUES) {
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

}
