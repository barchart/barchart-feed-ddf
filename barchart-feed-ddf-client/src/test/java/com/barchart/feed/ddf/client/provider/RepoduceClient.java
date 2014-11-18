package com.barchart.feed.ddf.client.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.model.data.Session;
import com.barchart.feed.api.model.data.Trade;
import com.barchart.feed.api.model.meta.Exchange;
import com.barchart.feed.client.provider.BarchartMarketplace;
import com.barchart.feed.inst.Exchanges;

public class RepoduceClient {

	private static final Logger log = LoggerFactory.getLogger(
			TestBarchartMarketProvider.class);
	
	private static final Exchange[] exchanges = new Exchange[] {
	        Exchanges.fromName("NYSE"), //
	        Exchanges.fromName("NASDAQ"), //
	        Exchanges.fromName("AMEX"), //
	        Exchanges.fromName("INDEX") };
	 
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final Marketplace market = BarchartMarketplace.builder()
				.username(username)
				.password(password)
				.build();
		
		market.startup();
		
		market.subscribe(Trade.class, tradeObs(), exchanges);
		// market.subscribe(Session.class, sessionObs(), exchanges);
		
		Thread.sleep(1000 * 60 * 60);
		
	}
	
	private static MarketObserver<Trade> tradeObs() {
		
		return new MarketObserver<Trade> () {

			private final AtomicInteger counter = new AtomicInteger(0);
			
			@Override
			public void onNext(final Trade v) {
				
				if(counter.incrementAndGet() % 500 == 0) {
					log.debug("Saw TRADE callbacks = " + counter.get());
				}
				
			}
			
		};
		
	}
	
	private static MarketObserver<Session> sessionObs() {
		
		return new MarketObserver<Session> () {

			private final Map<String, String> ids = new ConcurrentHashMap<String, String>();
			private final AtomicInteger counter = new AtomicInteger(0);
			
			@Override
			public void onNext(final Session v) {
				
				if(!ids.containsKey(v.instrument().id().id())) {
					ids.put(v.instrument().id().id(), v.instrument().id().id());
				}
				
				if(counter.incrementAndGet() >= 5000) {
					log.debug("Saw " + ids.size() + " unique instruments from SESSIONS");
					counter.set(0);
				}
				
			}
			
		};
		
	}
	
}