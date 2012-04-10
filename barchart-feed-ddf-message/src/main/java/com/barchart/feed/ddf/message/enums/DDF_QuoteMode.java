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

/** optional xml quote field */
public enum DDF_QuoteMode implements EnumCodeByte, EnumByteOrdinal {

	END_OF_DAY(_D_), //

	DELAYED(_I_), //

	REALTIME(_R_), //

	SNAPSHOT(_S_), //

	//

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

	DDF_QuoteMode(final byte code) {
		this.ord = (byte) ordinal();
		this.code = code;
	}

	private final static DDF_QuoteMode[] ENUM_VALUES = values();

	@Deprecated
	public final static DDF_QuoteMode[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	public final static DDF_QuoteMode fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	public final static DDF_QuoteMode fromCode(final byte code) {
		for (final DDF_QuoteMode known : ENUM_VALUES) {
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
