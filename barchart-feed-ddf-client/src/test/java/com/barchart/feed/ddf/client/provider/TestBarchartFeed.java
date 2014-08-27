package com.barchart.feed.ddf.client.provider;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.observables.BlockingObservable;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.connection.Connection.State;
import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.data.Cuvol;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.client.provider.BarchartMarketplace;
import com.barchart.feed.ddf.instrument.provider.DDF_RxInstrumentProvider;

public class TestBarchartFeed {
	
	protected static final Logger log = LoggerFactory.getLogger(TestBarchartFeed.class);
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		final Marketplace feed = new BarchartMarketplace(username, password);
		
		feed.bindConnectionStateListener(new Connection.Monitor() {

			@Override
			public void handle(State state, Connection connection) {
				System.out.println("Connection: " + state.name());
			}
			
		});
		
		feed.startup();
		
		final MarketObserver<Book> callback = new MarketObserver<Book>() {

			@Override
			public void onNext(final Book v) {
				
				System.out.println(v.toString());
				System.out.println(v.top().toString());
				System.out.println("\n***********************************************\n");
				
			}
			
		};
		
//		final MarketObserver<Market> callback = new MarketObserver<Market>() {
//
//			@Override
//			public void onNext(final Market v) {
//				
//				log.debug(
//					v.instrument().symbol() + " " +
//							v.change()
//				);
//				
//			}
//
//		};
		
		final Agent myAgent = feed.newAgent(Book.class, callback);
		
		myAgent.include(getInst("AAPL"));
		//myAgent.include("ESU3");
		
		Thread.sleep(1000000);
		
		feed.shutdown();
		
	}
	
	public static Instrument getInst(final String barSym) {
		
		final Map<String, List<Instrument>> map = BlockingObservable.from(DDF_RxInstrumentProvider
				.fromString(barSym)).single().results();
		
		Instrument result = BlockingObservable.from(DDF_RxInstrumentProvider
				.fromString(barSym)).single().results().get(barSym).get(0);
		
		return result;
		
	}
	
	public static String printCuvol(final List<Cuvol.Entry> entries) {
		
		final StringBuilder sb = new StringBuilder();
		
		for(final Cuvol.Entry e : entries) {
			sb.append(e.toString() + "\n");
		}
		
		return sb.toString();
	}
	
	public static String printChange(final Set<Market.Component> changes) {
	
		return "";
		
	}
	
}
