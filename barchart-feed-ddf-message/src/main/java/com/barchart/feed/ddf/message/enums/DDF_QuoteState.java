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

/** ddf quote state transitions */
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

	;

	@Override
	public final byte ord() {
		return ord;
	}

	@Override
	public final byte code() {
		return code;
	}

	public final byte ord;

	public final byte code;

	DDF_QuoteState(final byte code) {
		this.ord = (byte) ordinal();
		this.code = code;
	}

	private final static DDF_QuoteState[] ENUM_VALUES = values();

	@Deprecated
	public final static DDF_QuoteState[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	public final static DDF_QuoteState fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	public final static DDF_QuoteState fromCode(final byte code) {
		for (final DDF_QuoteState known : ENUM_VALUES) {
			if (known.code == code) {
				return known;
			}
		}
		return UNKNOWN;
	}

	public final boolean isKnown() {
		return this != UNKNOWN;
	}

}
