/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import com.barchart.util.enums.EnumCodeString;
import com.barchart.util.math.MathExtra;

// TODO: Auto-generated Javadoc
enum StatusXML implements EnumCodeString {

	FOUND("200"), //

	NOT_FOUND("404"), //

	UNKNOWN(""), //

	;

	final String code;

	/* (non-Javadoc)
	 * @see com.barchart.util.enums.EnumCodeString#code()
	 */
	@Override
	public final String code() {
		return code;
	}

	StatusXML(final String code) {
		this.code = code;
	}

	private final static StatusXML[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the status xm l[]
	 */
	@Deprecated
	public final static StatusXML[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	static final StatusXML fromCode(final String code) {
		for (final StatusXML known : ENUM_VALUES) {
			if (known.code.equalsIgnoreCase(code)) {
				return known;
			}
		}
		return UNKNOWN;
	}

	boolean isFound() {
		return this == FOUND;
	}

}
