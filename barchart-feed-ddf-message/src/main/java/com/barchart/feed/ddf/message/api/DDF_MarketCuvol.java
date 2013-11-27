/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.feed.base.cuvol.api.MarketDoCuvolEntry;
import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.util.anno.NotMutable;

/**
 * represents ddf feed market cumulative volume snapshot.
 */
@NotMutable
public interface DDF_MarketCuvol extends DDF_MarketBase {

	/**
	 * a.k.a tick increment
	 */
	PriceValue getPriceStep();

	/**
	 * last trade price today
	 */
	PriceValue getPriceLast();

	/**
	 * last trade size today
	 */
	SizeValue getSizeLast();

	/**
	 * cumulative volume at last trade price level
	 */
	SizeValue getSizeLastCuvol();

	/**
	 * cumulative volume at all price levels for a trading session
	 */
	MarketDoCuvolEntry[] entries();

}