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
 * The Enum DDF_QueryEodType.
 */
public enum DDF_QueryEodType implements EnumCodeString {

	/** default */
	DAILY("daily"), // 

	DAILY_NEAREST("dailynearest"), //

	DAILY_CONTINUE("dailycontinue"), //

	WEEKLY("weekly"), //

	WEEKLY_NEAREST("weeklynearest"), //

	WEEKLY_CONTINUE("weeklycontinue"), //

	MONTHLY("monthly"), //

	MONTHLY_NEAREST("monthlynearest"), //

	MONTHLY_CONTINUE("monthlycontinue"), //
	
	QUARTERLY("quarterly"), 
	
	QUARTERLY_NEAREST("quarterlynearest"),
		
	QUARTERLY_CONTINUE("quarterlycontinue"),
		
	YEARLY("yearly"),
		
	YEARLY_NEAREST("yearlynearest"),
	  
	YEARLY_CONTINUE("yearlycontinue")

	;

	/** The code. */
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

	private DDF_QueryEodType(final String code) {
		this.code = code;
	}

	private static final DDF_QueryEodType[] ENUM_VALUES = values();

	private static final DDF_QueryEodType[] ENUM_VALUES_NON_FUTURE = //
	new DDF_QueryEodType[] { DAILY, WEEKLY, MONTHLY };

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ query eod type[]
	 */
	@Deprecated
	public static final DDF_QueryEodType[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	/**
	 * Values unsafe non future.
	 *
	 * @return the dD f_ query eod type[]
	 */
	@Deprecated
	public static final DDF_QueryEodType[] valuesUnsafeNonFuture() {
		return ENUM_VALUES_NON_FUTURE;
	}

	/**
	 * From code.
	 *
	 * @param code the code
	 * @return the dD f_ query eod type
	 */
	public static final DDF_QueryEodType fromCode(final String code) {
		for (final DDF_QueryEodType known : ENUM_VALUES) {
			if (known.code.equalsIgnoreCase(code)) {
				return known;
			}
		}
		return DAILY;
	}

}
