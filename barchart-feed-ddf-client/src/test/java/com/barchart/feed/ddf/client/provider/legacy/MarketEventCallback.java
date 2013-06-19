/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.client.provider.legacy;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.util.values.api.Value;

interface MarketEventCallback<V extends Value<V>> {

	public void onMarketEvent(MarketEvent event,
			Instrument instrument, V value); 
	
}
