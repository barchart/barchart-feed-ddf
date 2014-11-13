/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.util.common.anno.NotMutable;

/**
 * ddf feed market snapshot messages; "reduced session info".
 */
@NotMutable
public interface DDF_MarketSnapshot extends DDF_MarketSnapBase {

	PriceValue getPriceBid();

	PriceValue getPriceAsk();

	//

	/**
	 * last trade price today
	 */
	PriceValue getPriceLast();

	/**
	 * last trade price yesterday
	 */
	PriceValue getPriceLastPrevious();

	//

	PriceValue getPriceSettle();

	//

	PriceValue getPriceOpen2();

	PriceValue getPriceClose2();

	//

	SizeValue getSizeVolumePrevious();

	/**
	 * Volume weighted average price
	 */
	PriceValue getVWAP();
	
}