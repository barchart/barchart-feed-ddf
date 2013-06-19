package com.barchart.feed.ddf.client.provider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.Feed;
import com.barchart.feed.api.MarketCallback;
import com.barchart.feed.api.connection.ConnectionFuture;
import com.barchart.feed.api.model.CuvolEntry;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.client.provider.BarchartFeed;

public class TestBarchartFeed {
	
	protected static final Logger log = LoggerFactory.getLogger(TestBarchartFeed.class);
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final Feed feed = new BarchartFeed(username, password).useLocalInstDefDB();
		
		final MarketCallback<Market> callback = new MarketCallback<Market>() {

			@Override
			public void call(final Market v) {
				
				log.debug(
				v.instrument().symbol() + "\n" +

				printCuvol(v.cuvol().cuvolList()));
				
			}
			
		};
		
		final ConnectionFuture<Feed> start = feed.startup();
		
		start.get();
		
		final Agent myAgent = feed.newAgent(Market.class, callback);
		
		//myAgent.include(ExchangeFactory.fromName("CME"));
		
		myAgent.include("GOOG");
		
		Thread.sleep(700000);
		
		feed.shutdown();
		
		Thread.sleep(2000);
		
	}
	
	public static String printCuvol(final List<CuvolEntry> entries) {
		
		final StringBuilder sb = new StringBuilder();
		
		for(final CuvolEntry e : entries) {
			sb.append(e.toString() + "\n");
		}
		
		return sb.toString();
		
	}
	
	
	
	
	
}
