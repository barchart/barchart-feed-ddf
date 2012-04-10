/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.feed.base.provider.market.provider.MarketDoBookEntry;
import com.barchart.util.anno.NotMutable;

/** represents ddf feed market depth snapshot */
@NotMutable
public interface DDF_MarketBook extends DDF_MarketBase {

	/**
	 * maximum number of on bid or ask side
	 */
	int ENTRY_LIMIT = 10;

	MarketDoBookEntry[] entries();

}