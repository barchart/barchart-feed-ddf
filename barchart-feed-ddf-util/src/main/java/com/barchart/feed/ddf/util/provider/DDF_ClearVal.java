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
 * Corresponds to DDF_CLEAR "comma-dast-comma" values.
 * 
 * @author g-litchfield
 * 
 */
public final class DDF_ClearVal {

	/** The Constant DECIMAL_CLEAR. */
	public static final DecimalValue DECIMAL_CLEAR = ValueBuilder.newDecimalMutable(0, 0);

	/** The Constant PRICE_CLEAR. */
	public static final PriceValue PRICE_CLEAR = ValueBuilder.newPriceMutable(0, 0);

	/** The Constant SIZE_CLEAR. */
	public static final SizeValue SIZE_CLEAR = ValueBuilder.newSizeMutable(0);

	/** The Constant TIME_CLEAR. */
	public static final TimeValue TIME_CLEAR = ValueBuilder.newTimeMutable(0);

}
