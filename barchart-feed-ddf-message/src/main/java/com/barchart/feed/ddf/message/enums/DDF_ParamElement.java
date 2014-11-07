/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.enums;

import static com.barchart.util.common.ascii.ASCII.QUEST;
import static com.barchart.util.common.ascii.ASCII._0_;
import static com.barchart.util.common.ascii.ASCII._1_;
import static com.barchart.util.common.ascii.ASCII._2_;
import static com.barchart.util.common.ascii.ASCII._3_;
import static com.barchart.util.common.ascii.ASCII._4_;
import static com.barchart.util.common.ascii.ASCII._5_;
import static com.barchart.util.common.ascii.ASCII._6_;
import static com.barchart.util.common.ascii.ASCII._7_;
import static com.barchart.util.common.ascii.ASCII._A_;
import static com.barchart.util.common.ascii.ASCII._B_;
import static com.barchart.util.common.ascii.ASCII._C_;
import static com.barchart.util.common.ascii.ASCII._D_;
import static com.barchart.util.common.ascii.ASCII._E_;
import static com.barchart.util.common.ascii.ASCII._F_;
import static com.barchart.util.common.ascii.ASCII._S_;
import static com.barchart.util.common.ascii.ASCII._X_;
import static com.barchart.util.common.ascii.ASCII._d_;
import static com.barchart.util.common.ascii.ASCII._V_;

import com.barchart.feed.base.enums.EnumByteOrdinal;
import com.barchart.feed.base.enums.EnumCodeByte;
import com.barchart.util.common.math.MathExtra;

// TODO: Auto-generated Javadoc
/**
 * qualifier for market parameter message.
 */
public enum DDF_ParamElement implements EnumCodeByte, EnumByteOrdinal {

	/** 0 = trade */
	TRADE(_0_), //

	/** 1 = ask */
	ASK(_1_), //

	/** 2 = bid */
	BID(_2_), //

	/** 3 = close; 31 is offer; 32 is bid */
	CLOSE(_3_), //

	/** 4 = 2nd of closing range; 41 is offer; 42 is bid */
	CLOSE_2(_4_), //

	/** 5 = high; 52 is high made by valid bid */
	HIGH(_5_), //

	/** 6 = low; 61 is low made by valid offer */
	LOW(_6_), //

	/** 7 = volume; 71 = yesterday's volume; 76 = today's cumulative volume */
	VOLUME(_7_), //

	/** A = open; A1 is offer; A2 is bid */
	OPEN(_A_), //

	/** B = 2nd of opening range; B1 is offer; B2 is bid */
	OPEN_2(_B_), //

	/** C = open interest; C1 = yesterday's open interest */
	INTEREST(_C_), //

	/** D = settlement */
	SETTLE_END(_D_), //

	/** d = settlement (during market trading) */
	SETTLE_NOW(_d_), //

	/** E = previous */
	PREVIOUS(_E_), //

	/** F = ETF informational message */
	ETF_INFO(_F_), //

	/** S = 52 Week High or Low Values */
	YEAR_BACK(_S_), //

	/** X = cancelled trade message */
	TRADE_CANCEL(_X_), //
	
	/** V = VWAP parameter message */
	VWAP(_V_),

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

	private DDF_ParamElement(final byte code) {
		this.ord = (byte) ordinal();
		this.code = code;
	}

	private final static DDF_ParamElement[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ param element[]
	 */
	@Deprecated
	public final static DDF_ParamElement[] valuesUnsafe() {
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