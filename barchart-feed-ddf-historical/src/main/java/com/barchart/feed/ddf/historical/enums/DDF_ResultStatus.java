/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.enums;

public enum DDF_ResultStatus {

	ERROR, //

	SUCCESS, //

	INTERRUPTED, //

	;

	private DDF_ResultStatus() {

	}

	private static final DDF_ResultStatus[] ENUM_VALUES = values();

	public static final DDF_ResultStatus[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	public boolean isSuccess() {
		return this == SUCCESS;
	}

}
