/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.util.anno.NotMutable;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;

/** ddf feed market snapshot messages; "reduced session info" */
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

}