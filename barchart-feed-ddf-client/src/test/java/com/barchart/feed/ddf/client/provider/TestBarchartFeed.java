package com.barchart.feed.ddf.client.provider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.connection.Connection.State;
import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.data.Cuvol;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Session;
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
		
		feed.bindConnectionStateListener(new Connection.Monitor() {

			@Override
			public void handle(State state, Connection connection) {
				System.out.println("Connection: " + state.name());
			}
			
		});
		
		feed.startup();
		
//		final MarketObserver<Book> callback = new MarketObserver<Book>() {
//
//			@Override
//			public void onNext(final Book v) {
//				
//				log.debug(
//				v.instrument().symbol() + " " +
//				v.lastBookUpdate().level() + " " + 
//				v.lastBookUpdate().price().toString() + " " + 
//				v.lastBookUpdate().size().toString());
//			}
//			
//		};
//		
//		final Agent myAgent = feed.newAgent(Book.class, callback);
		
		final MarketObserver<Market> callback = new MarketObserver<Market>() {

			@Override
			public void onNext(final Market v) {
				
				log.debug(
				v.instrument().symbol() + " " +
				v.book().lastBookUpdate().level() + " " + 
				v.book().lastBookUpdate().price().toString() + " " + 
				v.book().lastBookUpdate().size().toString());
			}
			
		};
		
		final Agent myAgent = feed.newAgent(Market.class, callback);
		
		//myAgent.include(Exchanges.fromName("CME"));
		myAgent.include("^EURUSD");
		
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
