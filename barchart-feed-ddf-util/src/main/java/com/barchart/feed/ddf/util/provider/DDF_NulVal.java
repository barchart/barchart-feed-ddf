/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/**
 * 
 */
package com.barchart.feed.ddf.util.provider;

import com.barchart.feed.base.values.api.DecimalValue;
import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.base.values.api.TimeValue;
import com.barchart.feed.base.values.provider.ValueBuilder;

/**
 * The DDF null instance of the respective DDF Value classes
 * 
 */
public final class DDF_NulVal {

	/** The Constant DECIMAL_EMPTY. */
	public static final DecimalValue DECIMAL_EMPTY = ValueBuilder.newDecimalMutable(0, 0);

	/** The Constant PRICE_EMPTY. */
	public static final PriceValue PRICE_EMPTY = ValueBuilder.newPriceMutable(0, 0);

	/** The Constant SIZE_EMPTY. */
	public static final SizeValue SIZE_EMPTY = ValueBuilder.newSizeMutable(0);

	/** The Constant TIME_EMPTY. */
	public static final TimeValue TIME_EMPTY = ValueBuilder.newTimeMutable(0);

}
