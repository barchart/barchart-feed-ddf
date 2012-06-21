/**
 * 
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.util.values.api.PriceValue;

/**
 * @author g-litchfield
 * 
 */
public interface DDF_EOD_Commodity extends DDF_MarketBase {

	PriceValue getPriceOpen();

	PriceValue getPriceHigh();

	PriceValue getPriceLow();

	PriceValue getPriceLast();

}
