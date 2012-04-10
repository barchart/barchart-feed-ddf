/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.enums;

// TODO: Auto-generated Javadoc
/**
 * The Enum DDF_ResultStatus.
 */
public enum DDF_ResultStatus {

	ERROR, //

	SUCCESS, //

	INTERRUPTED, //

	;

	private DDF_ResultStatus() {

	}

	private static final DDF_ResultStatus[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ result status[]
	 */
	public static final DDF_ResultStatus[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	/**
	 * Checks if is success.
	 *
	 * @return true, if is success
	 */
	public boolean isSuccess() {
		return this == SUCCESS;
	}

}
