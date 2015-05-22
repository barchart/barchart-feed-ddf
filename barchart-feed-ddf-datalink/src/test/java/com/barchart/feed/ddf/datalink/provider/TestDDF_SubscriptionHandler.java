package com.barchart.feed.ddf.datalink.provider;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.consumer.ConsumerAgent;
import com.barchart.feed.api.consumer.MetadataService;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.base.sub.SubscriptionHandler;
import com.barchart.feed.ddf.datalink.provider.TestableMarketService.TestableFeedClient;
import com.barchart.feed.ddf.instrument.provider.DDF_MetadataServiceWrapper;

public class TestDDF_SubscriptionHandler {

	private TestableMarketService market;
	private TestableFeedClient feed;
	
	@Before
	public void setUp() {
		
		feed = new TestableFeedClient();
		
		final MetadataService metaService = new DDF_MetadataServiceWrapper();
		final SubscriptionHandler subHandler = new DDF_SubscriptionHandler(feed, market);
		
		market = new TestableMarketService(metaService, subHandler);
		
		feed.setOnline();
		
	}
	
	@Test
	public void stepUpAndDown() throws Exception {
		
		final ConsumerAgent agent = market.register(new DummyObserver(), Market.class);
		
		agent.include("CLM15").subscribe();
		
		Thread.sleep(500);
		
//		assertEquals(feed.getLastWrite(), "GO CLM5=bBcsS");
//		assertEquals(market.numberOfSubscriptions(), 1);
//		
	}
	
	public class DummyObserver implements MarketObserver<Market> {
		
		@Override
		public void onNext(Market v) {
			
		}
		
	}
	
}
