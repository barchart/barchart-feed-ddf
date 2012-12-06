package com.barchart.feed.client.provider;

import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.Market;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.util.values.api.Value;

public interface MarketEventCallback<V extends Value<V>> {

	public void onMarketEvent(MarketEvent event,
			MarketInstrument instrument, V value); 
	
}
