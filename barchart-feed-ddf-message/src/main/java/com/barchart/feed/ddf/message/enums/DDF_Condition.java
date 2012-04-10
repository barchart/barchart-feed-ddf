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

/** market condition */
public enum DDF_Condition implements EnumCodeByte, EnumByteOrdinal {

	NORMAL(NUL), //

	TRADING_HALT(_A_), //

	TRADING_RESUMTPION(_B_), //

	QUOTATION_RESUMPTION(_C_), //

	END_FAST_MARKET(_E_), //

	FAST_MARKET(_F_), //

	LATE_MARKET(_L_), //

	END_LATE_MARKET(_M_), //

	POST_SESSION(_P_), //

	END_POST_SESSION(_Q_), //

	OPENING_DELAY(_1_), //

	NO_OPEN_RESUME(_4_), //

	//

	UNKNOWN(QUEST), //

	;

	public final byte ord;

	public final byte code;

	@Override
	public final byte ord() {
		return ord;
	}

	@Override
	public final byte code() {
		return code;
	}

	DDF_Condition(final byte code) {
		this.ord = (byte) ordinal();
		this.code = code;
	}

	private final static DDF_Condition[] ENUM_VALUES = values();

	@Deprecated
	public final static DDF_Condition[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	public final static DDF_Condition fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	public final static DDF_Condition fromCode(final byte code) {
		for (final DDF_Condition known : ENUM_VALUES) {
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
