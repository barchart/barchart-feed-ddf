package com.barchart.feed.ddf.client.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketCallback;
import com.barchart.feed.api.data.Market;
import com.barchart.feed.api.data.MarketData;
import com.barchart.feed.api.enums.MarketEventType;
import com.barchart.feed.api.enums.MarketSide;
import com.barchart.feed.client.provider.BarchartFeed;

public class TestBarchartFeed {
	
	protected static final Logger log = LoggerFactory.getLogger(TestBarchartFeed.class);
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final BarchartFeed feed = new BarchartFeed();
		
		feed.login(username, password);
		
		final MarketCallback<Market> callback = new MarketCallback<Market>() {

			@Override
			public void call(final Market v, final MarketEventType type) {
				
				log.debug(
				v.topOfBook().side(MarketSide.BID).size().asDouble() + " " +
				v.topOfBook().side(MarketSide.BID).price().asDouble() + " " +
				v.topOfBook().side(MarketSide.ASK).price().asDouble() + " " +
				v.topOfBook().side(MarketSide.ASK).size().asDouble());
				
			}
			
		};
		
		final Agent myAgent = feed.newAgent(MarketData.Type.MARKET, callback, 
				MarketEventType.values());
		
		myAgent.include("ESU3");
		
		try {
			Thread.sleep(10000);
		} catch (final Exception e) {
			// Interrupted
		}
		
		feed.shutdown();
		
	}

}
