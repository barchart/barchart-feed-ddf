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

import static com.barchart.util.values.provider.ValueBuilder.newDecimalMutable;
import static com.barchart.util.values.provider.ValueBuilder.newPriceMutable;
import static com.barchart.util.values.provider.ValueBuilder.newSizeMutable;
import static com.barchart.util.values.provider.ValueBuilder.newTimeMutable;

import com.barchart.util.values.api.DecimalValue;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;
import com.barchart.util.values.api.TimeValue;

/**
 * The DDF null instance of the respective DDF Value classes
 * 
 * @author g-litchfield
 * 
 */
public final class DDF_NulVal {

	/** The Constant DECIMAL_EMPTY. */
	public static final DecimalValue DECIMAL_EMPTY = newDecimalMutable(0, 0);

	/** The Constant PRICE_EMPTY. */
	public static final PriceValue PRICE_EMPTY = newPriceMutable(0, 0);

	/** The Constant SIZE_EMPTY. */
	public static final SizeValue SIZE_EMPTY = newSizeMutable(0);

	/** The Constant TIME_EMPTY. */
	public static final TimeValue TIME_EMPTY = newTimeMutable(0);

}
