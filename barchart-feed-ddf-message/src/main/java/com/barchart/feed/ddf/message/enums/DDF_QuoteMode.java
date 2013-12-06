/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.enums;

import static com.barchart.util.common.ascii.ASCII.QUEST;
import static com.barchart.util.common.ascii.ASCII._D_;
import static com.barchart.util.common.ascii.ASCII._I_;
import static com.barchart.util.common.ascii.ASCII._R_;
import static com.barchart.util.common.ascii.ASCII._S_;

import com.barchart.feed.base.enums.EnumByteOrdinal;
import com.barchart.feed.base.enums.EnumCodeByte;
import com.barchart.util.common.math.MathExtra;

// TODO: Auto-generated Javadoc
/**
 * optional xml quote field.
 */
public enum DDF_QuoteMode implements EnumCodeByte, EnumByteOrdinal {

	END_OF_DAY(_D_), //

	DELAYED(_I_), //

	REALTIME(_R_), //

	SNAPSHOT(_S_), //

	//

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

	DDF_QuoteMode(final byte code) {
		this.ord = (byte) ordinal();
		this.code = code;
	}

	private final static DDF_QuoteMode[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ quote mode[]
	 */
	@Deprecated
	public final static DDF_QuoteMode[] valuesUnsafe() {
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
	 * @return the dD f_ quote mode
	 */
	public final static DDF_QuoteMode fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	/**
	 * From code.
	 *
	 * @param code the code
	 * @return the dD f_ quote mode
	 */
	public final static DDF_QuoteMode fromCode(final byte code) {
		for (final DDF_QuoteMode known : ENUM_VALUES) {
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
