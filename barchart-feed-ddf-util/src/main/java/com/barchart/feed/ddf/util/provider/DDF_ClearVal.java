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
 * Corresponds to DDF_CLEAR "comma-dast-comma" values.
 * 
 * @author g-litchfield
 * 
 */
public final class DDF_ClearVal {

	/** The Constant DECIMAL_CLEAR. */
	public static final DecimalValue DECIMAL_CLEAR = newDecimalMutable(0, 0);

	/** The Constant PRICE_CLEAR. */
	public static final PriceValue PRICE_CLEAR = newPriceMutable(0, 0);

	/** The Constant SIZE_CLEAR. */
	public static final SizeValue SIZE_CLEAR = newSizeMutable(0);

	/** The Constant TIME_CLEAR. */
	public static final TimeValue TIME_CLEAR = newTimeMutable(0);

}
