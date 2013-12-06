/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.enums;

import static com.barchart.util.common.ascii.ASCII.NUL;
import static com.barchart.util.common.ascii.ASCII.QUEST;
import static com.barchart.util.common.ascii.ASCII._1_;
import static com.barchart.util.common.ascii.ASCII._4_;
import static com.barchart.util.common.ascii.ASCII._A_;
import static com.barchart.util.common.ascii.ASCII._B_;
import static com.barchart.util.common.ascii.ASCII._C_;
import static com.barchart.util.common.ascii.ASCII._E_;
import static com.barchart.util.common.ascii.ASCII._F_;
import static com.barchart.util.common.ascii.ASCII._L_;
import static com.barchart.util.common.ascii.ASCII._M_;
import static com.barchart.util.common.ascii.ASCII._P_;
import static com.barchart.util.common.ascii.ASCII._Q_;

import com.barchart.feed.base.enums.EnumByteOrdinal;
import com.barchart.feed.base.enums.EnumCodeByte;
import com.barchart.util.common.math.MathExtra;

// TODO: Auto-generated Javadoc
/**
 * market condition.
 */
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

	/** The ord. */
 ;

	public final byte ord;

	/** The code. */
	public final byte code;

	/* (non-Javadoc)
	 * @see com.barchart.util.enums.EnumByteOrdinal#ord()
	 */
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

	DDF_Condition(final byte code) {
		this.ord = (byte) ordinal();
		this.code = code;
	}

	private final static DDF_Condition[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ condition[]
	 */
	@Deprecated
	public final static DDF_Condition[] valuesUnsafe() {
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
	 * @return the dD f_ condition
	 */
	public final static DDF_Condition fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	/**
	 * From code.
	 *
	 * @param code the code
	 * @return the dD f_ condition
	 */
	public final static DDF_Condition fromCode(final byte code) {
		for (final DDF_Condition known : ENUM_VALUES) {
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
