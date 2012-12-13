/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.client.provider;

import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.Market;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.util.values.api.Value;

public interface MarketEventCallback<V extends Value<V>> {

	public void onMarketEvent(MarketEvent event,
			MarketInstrument instrument, V value); 
	
}
