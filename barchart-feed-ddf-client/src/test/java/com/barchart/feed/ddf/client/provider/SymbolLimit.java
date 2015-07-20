package com.barchart.feed.ddf.client.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.connection.TimestampListener;
import com.barchart.feed.api.consumer.ConsumerAgent;
import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.meta.Exchange;
import com.barchart.feed.client.provider.BarchartMarketplace;
import com.barchart.feed.client.provider.BarchartMarketplace.FeedType;
import com.barchart.feed.inst.Exchanges;
import com.barchart.util.value.api.Time;

public class SymbolLimit {

	private static final Logger log = LoggerFactory.getLogger(
			SymbolLimit.class);
	
	private static final String[] exs = new String[] {
		//A", "X", "B", "M", "E", "L", "O", "t", "Q", "C", "J", "G", "h", "k", "1", "2", "4"
		"Q"
	};
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final Marketplace market = BarchartMarketplace.builder()
				.username(username)
				.password(password)
				.build();
		
		market.startup();
		
//		market.bindTimestampListener(new TimestampListener() {
//
//			@Override 	
//			public void listen(final Time timestamp) {
//				log.debug("Time Diff = {}", System.currentTimeMillis() - timestamp.millisecond());
//			}
//	
//		});
		
		Thread.sleep(5000);
		
		log.debug("START SUBS");
		
		//final ConsumerAgent agent = market.register(marketObs(), Market.class);
		final ConsumerAgent agent = market.register(bookObs(), Book.class);
		
		for(final String s : exs) {
			agent.include(Exchanges.fromCode(s).id());
		}
		
		agent.activate();
		
		log.debug("NUMBER OF SUBS = {}", market.numberOfSubscriptions());
		
		Thread.sleep(10 * 60 * 1000);
		market.shutdown();
		
	}
	
	private static MarketObserver<Market> marketObs() {
		
		return new MarketObserver<Market>() {
			
			@Override
			public void onNext(final Market m) {
				
				log.debug(m.session().timeOpened().toString());
				
				
			}
			
		};
		
	}
	
	private static MarketObserver<Book> bookObs() {
		
		return new MarketObserver<Book>() {

			@Override
			public void onNext(Book v) {
				
				final Book.Top t = v.top();
				
				//log.debug("Time diff {}", System.currentTimeMillis() - v.updated().millisecond());
				log.debug("Bid {} Ask {}", t.bid().price(), t.ask().price());
				
			}
			
		};
		
	}
	
	
}
