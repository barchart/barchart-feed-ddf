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

import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;

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
