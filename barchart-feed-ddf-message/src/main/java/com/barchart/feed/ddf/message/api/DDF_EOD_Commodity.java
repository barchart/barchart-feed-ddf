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
