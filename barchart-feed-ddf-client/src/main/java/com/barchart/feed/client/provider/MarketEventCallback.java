package com.barchart.feed.client.provider;

import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.Market;
import com.barchart.feed.base.market.enums.MarketEvent;

public interface MarketEventCallback {

	public void onMarketEvent(MarketEvent event,
			MarketInstrument instrument, Market value); 
	
}
