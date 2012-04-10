/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/*
 * 
 */
package com.barchart.feed.ddf.settings.enums;

import com.barchart.util.enums.EnumCodeString;
import com.barchart.util.math.MathExtra;

// TODO: Auto-generated Javadoc
/**
 * Enum denoting the data source/server type for a connection.
 * 
 * @author g-litchfield
 * 
 */
public enum DDF_ServerType implements EnumCodeString {

	/** market data service */
	STREAM("stream"), //

	/** legacy historical system */
	HISTORICAL("historical"), //

	/** current historical system */
	HISTORICAL_V2("historicalv2"), //

	/** instrument lookup service */
	EXTRAS("extras"), //

	/** market news service */
	NEWS("news"), //

	//

	UNKNOWN(""), //

	;

	private final String code;

	private DDF_ServerType(final String code) {
		this.code = code;
	}

	private final static DDF_ServerType[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ server type[]
	 */
	@Deprecated
	public final static DDF_ServerType[] valuesUnsafe() {
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
	 * @return the dD f_ server type
	 */
	public final static DDF_ServerType fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	/**
	 * From code.
	 *
	 * @param code the code
	 * @return the dD f_ server type
	 */
	public static final DDF_ServerType fromCode(final String code) {
		for (final DDF_ServerType known : ENUM_VALUES) {
			if (known.code.equalsIgnoreCase(code)) {
				return known;
			}
		}
		return UNKNOWN;
	}

	/* (non-Javadoc)
	 * @see com.barchart.util.enums.EnumCodeString#code()
	 */
	@Override
	public final String code() {
		return code;
	}

}
