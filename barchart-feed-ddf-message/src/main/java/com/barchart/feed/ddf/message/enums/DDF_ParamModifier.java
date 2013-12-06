/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.enums;

import static com.barchart.util.common.ascii.ASCII.EQUAL;
import static com.barchart.util.common.ascii.ASCII.LESS;
import static com.barchart.util.common.ascii.ASCII.MORE;
import static com.barchart.util.common.ascii.ASCII.QUEST;
import static com.barchart.util.common.ascii.ASCII._0_;
import static com.barchart.util.common.ascii.ASCII._1_;
import static com.barchart.util.common.ascii.ASCII._2_;
import static com.barchart.util.common.ascii.ASCII._6_;
import static com.barchart.util.common.ascii.ASCII._H_;
import static com.barchart.util.common.ascii.ASCII._L_;
import static com.barchart.util.common.ascii.ASCII._N_;
import static com.barchart.util.common.ascii.ASCII._S_;

import com.barchart.feed.base.enums.EnumByteOrdinal;
import com.barchart.feed.base.enums.EnumCodeByte;
import com.barchart.util.common.math.MathExtra;

// TODO: Auto-generated Javadoc
/**
 * qualifier for market parameter message.
 */
public enum DDF_ParamModifier implements EnumCodeByte, EnumByteOrdinal {

	/** 0 = last */
	LAST(_0_), //

	/** 1 = ask */
	ASK_PRICE(_1_), //

	/** 1 = past, previous, yesterday */
	PAST(_1_), //

	/** 2 = bid */
	BID_PRICE(_2_), //

	/** < = bid size */
	BID_SIZE(LESS), //

	/** = = ask size */
	ASK_SIZE(EQUAL), //

	/** > = trade size */
	TRADE_SIZE(MORE), //

	/** 6 = today */
	THIS(_6_), //

	/** S = EFT shares outstanding */
	EFT_SHARES(_S_), //

	/** N = EFT net asset value */
	EFT_VALUE(_N_), //

	/** H = high (52-week high) */
	YEAR_HIGH(_H_), //

	/** L = low (52-week low) */
	YEAR_LOW(_L_), //

	//

	/** not classified / missing / error */
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

	private DDF_ParamModifier(final byte code) {
		this.ord = (byte) ordinal();
		this.code = code;
	}

	private final static DDF_ParamModifier[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ param modifier[]
	 */
	@Deprecated
	public final static DDF_ParamModifier[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
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