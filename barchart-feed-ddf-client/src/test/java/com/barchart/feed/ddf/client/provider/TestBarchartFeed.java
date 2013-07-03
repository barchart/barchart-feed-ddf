package com.barchart.feed.ddf.client.provider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.connection.Connection.State;
import com.barchart.feed.api.model.data.Cuvol;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Trade;
import com.barchart.feed.client.provider.BarchartMarketplace;
import com.barchart.feed.inst.provider.Exchanges;

public class TestBarchartFeed {
	
	protected static final Logger log = LoggerFactory.getLogger(TestBarchartFeed.class);
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		final Marketplace feed = BarchartMarketplace.builder().
				username(username).
				password(password).
				useLocalInstDatabase().
				build();
		
		final MarketObserver<Trade> callback = new MarketObserver<Trade>() {

			@Override
			public void onNext(final Trade v) {
				
				//log.debug(v.instrument().symbol() + " " + v.types().name());
				
//				log.debug(
//				v.instrument().symbol() + " " +
//
//				v.book().top().ask().price().asDouble() + " " +
//				v.book().top().bid().price().asDouble());
				
				//printCuvol(v.cuvol().entryList()));
				
			}
			
		};
		
		feed.bindConnectionStateListener(new Connection.Monitor() {

			@Override
			public void handle(State state, Connection connection) {
				System.out.println("Connection: " + state.name());
			}
			
		});
		
		feed.startup();
		
		final Agent myAgent = feed.newAgent(Trade.class, callback);
		
		myAgent.include(Exchanges.fromName("CME"));
		//myAgent.include("GOOG");
		
		Thread.sleep(1000000);
		
		feed.shutdown();
		
//		Thread.sleep(5000);
//		
//		feed.startup();
//		
//		myAgent.include("GOOG");
//		
//		Thread.sleep(10000);
//		
//		feed.shutdown();
		
	}
	
	public static String printCuvol(final List<Cuvol.Entry> entries) {
		
		final StringBuilder sb = new StringBuilder();
		
		for(final Cuvol.Entry e : entries) {
			sb.append(e.toString() + "\n");
		}
		
		return sb.toString();
	}
	
}
