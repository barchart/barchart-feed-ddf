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
		
		final BarchartFeed feed = new BarchartFeed(username, password);
		
		final MarketCallback<Market> callback = new MarketCallback<Market>() {

			@Override
			public void call(final Market v) {
				
				log.debug(
				v.instrument().symbol() + "\n" +
//				v.orderBook().topOfBook().bid().size().asDouble() + " " +
//				v.orderBook().topOfBook().bid().price().asDouble() + " " +
//				v.orderBook().topOfBook().ask().price().asDouble() + " " +
//				v.orderBook().topOfBook().ask().size().asDouble());

				printCuvol(v.cuvol().cuvolList()));
				
			}
			
		};
		
		final ConnectionFuture<Feed> start = feed.startup();
		
		start.get();
		
		final Agent myAgent = feed.newAgent(Market.class, callback);
		
		//myAgent.include(ExchangeFactory.fromName("CME"));
		
		myAgent.include("ESU3");
		
		try {
			Thread.sleep(70000);
		} catch (final Exception e) {
			// Interrupted
		}
		
//		myAgent.include("MSFT");
//		
//		try {
//			Thread.sleep(10000);
//		} catch (final Exception e) {
//			// Interrupted
//		}
//		
//		myAgent.exclude("GOOG");
//		
//		try {
//			Thread.sleep(15000);
//		} catch (final Exception e) {
//			// Interrupted
//		}
		
//		feed.shutdown();
//		
//		try {
//			Thread.sleep(2000);
//		} catch (final Exception e) {
//			// Interrupted
//		}
//		
//		feed.startup();
//		
//		try {
//			Thread.sleep(7000);
//		} catch (final Exception e) {
//			// Interrupted
//		}
		
		feed.shutdown();
		
		try {
			Thread.sleep(2000);
		} catch (final Exception e) {
			// Interrupted
		}
		
	}
	
	public static String printCuvol(final List<CuvolEntry> entries) {
		
		final StringBuilder sb = new StringBuilder();
		
		for(final CuvolEntry e : entries) {
			sb.append(e.toString() + "\n");
		}
		
		return sb.toString();
		
		
	}
	
	
	
	
	
}
