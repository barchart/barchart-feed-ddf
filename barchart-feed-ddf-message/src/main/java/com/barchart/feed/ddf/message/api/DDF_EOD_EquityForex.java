/**
 * 
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;

/**
 * @author g-litchfield
 * 
 */
public interface DDF_EOD_EquityForex extends DDF_MarketBase {

	PriceValue getPriceOpen();

	PriceValue getPriceHigh();

	PriceValue getPriceLow();

	PriceValue getPriceLast();

	SizeValue getSizeVolume();

}
