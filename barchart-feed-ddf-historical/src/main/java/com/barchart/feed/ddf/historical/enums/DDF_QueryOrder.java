/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.enums;

import com.barchart.feed.base.enums.EnumCodeString;

// TODO: Auto-generated Javadoc
/**
 * The Enum DDF_QueryOrder.
 */
public enum DDF_QueryOrder implements EnumCodeString {

	/** default */
	ASCENDING("asc"), //

	DESCENDING("desc"), //

	/** The code. */
 ;

	public final String code;

	/**
	 * used in page url and as xml code.
	 *
	 * @return the string
	 */
	@Override
	public final String code() {
		return code;
	}

	private DDF_QueryOrder(final String code) {
		this.code = code;
	}

	private static final DDF_QueryOrder[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ query order[]
	 */
	@Deprecated
	public static final DDF_QueryOrder[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	/**
	 * From code.
	 *
	 * @param code the code
	 * @return the dD f_ query order
	 */
	public static final DDF_QueryOrder fromCode(final String code) {
		for (final DDF_QueryOrder known : ENUM_VALUES) {
			if (known.code.equalsIgnoreCase(code)) {
				return known;
			}
		}
		return ASCENDING;
	}

}
