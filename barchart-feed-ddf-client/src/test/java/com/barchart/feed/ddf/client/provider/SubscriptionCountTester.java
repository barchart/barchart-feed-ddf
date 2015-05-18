package com.barchart.feed.ddf.client.provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observer;
import rx.observables.BlockingObservable;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.connection.Connection.Monitor;
import com.barchart.feed.api.connection.Connection.State;
import com.barchart.feed.api.consumer.ConsumerAgent;
import com.barchart.feed.api.consumer.MetadataService.Result;
import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Session;
import com.barchart.feed.api.model.data.Trade;
import com.barchart.feed.api.model.data.parameter.Param;
import com.barchart.feed.api.model.data.parameter.ParamMap;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.client.provider.BarchartMarketplace;
import com.barchart.feed.ddf.instrument.provider.DDF_RxInstrumentProvider;

public class SubscriptionCountTester {

	private static final Logger log = LoggerFactory.getLogger(
			SubscriptionCountTester.class);
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final Marketplace market = BarchartMarketplace.builder()
				.username(username)
				.password(password)
				.build();
//				.feedType(FeedType.LISTENER_TCP)
//				.port(41234).build();
		
		market.startup();
		
		Thread.sleep(5 * 1000);
		
		final ConsumerAgent agent1 = market.register(marketObs(), Market.class);
		
		agent1.include("ESM15").subscribe();
		Thread.sleep(3 * 1000);
		
		System.out.println("NUMBER OF INSTS = " + market.numberOfSubscriptions());
		Thread.sleep(3 * 1000);
		
		agent1.include("CLM15").subscribe();
		Thread.sleep(3 * 1000);
		
		System.out.println("NUMBER OF INSTS = " + market.numberOfSubscriptions());
		Thread.sleep(3 * 1000);
		
		agent1.exclude("CLM15").subscribe();
		Thread.sleep(3 * 1000);
		
		System.out.println("NUMBER OF INSTS = " + market.numberOfSubscriptions());
		Thread.sleep(3 * 1000);
		
		agent1.exclude("ESM15").subscribe();
		Thread.sleep(3 * 1000);
		
		System.out.println("NUMBER OF INSTS = " + market.numberOfSubscriptions());
		Thread.sleep(3 * 1000);

		log.debug("Shutting down");
		market.shutdown();
		
		Thread.sleep(5 * 1000);
		
	}
	
	Observer<Result<Instrument>> obs() {
		
		return new Observer<Result<Instrument>>() {

			@Override
			public void onCompleted() {
				System.out.println("ON COMPLETED");
			}
	
			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}
	
			@Override
			public void onNext(Result<Instrument> t) {
				System.out.println("ON NEXT");
			}
			
		};
		
	}
	private static Monitor listener(final CountDownLatch lock) { 
		
		return new Monitor() {

			@Override
			public void handle(State state, Connection connection) {
				
				log.debug("New State = {}", state);
				
				if(state == State.CONNECTED) {
					lock.countDown();
				}
			}
		};
	}
	
	private static MarketObserver<Market> marketObs() {
		
		return new MarketObserver<Market>() {

			@Override
			public void onNext(final Market m) {
				
					
//				final Book.Top topOfBook = m.book().top();
//				
//				System.out.println("Bid = " + topOfBook.bid().price() + " ASK = " + topOfBook.ask().price());
					
				
			}
		};
	}
	
	private static MarketObserver<Book> bookObs() {

		return new MarketObserver<Book>() {

			@Override
			public void onNext(final Book b) {
				System.out.println(b.instrument().symbol() + " BOOK " + b.updated());
			}

		};
	}

	private static MarketObserver<Session> sessObs() {

		return new MarketObserver<Session>() {

			@Override
			public void onNext(final Session s) {
				
				final ParamMap params = s.parameters();
				
				if(!params.has(Param.SESSION_VWAP)) {
					log.debug("NO VWAP DATA");
					return;
				}
				
				log.debug("VWAP = {}", params.get(Param.SESSION_VWAP));
				
			}

		};
	}

	private static MarketObserver<Trade> tradeObs() {

		return new MarketObserver<Trade>() {

			private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss z");
			
			@Override
			public void onNext(final Trade t) {
				if(t.isNull()) {
					System.out.println("NULL TRADE");
					// new RuntimeException().printStackTrace();
				} else {
					System.out.println(t.instrument().symbol() + " TRADE " + t.price());
				}
			}

		};

	}

	private static Observer<Result<Instrument>> instObs() {
		
		return new Observer<Result<Instrument>>() {

			@Override
			public void onCompleted() {
				log.debug("Lookup and registration complete");
			}

			@Override
			public void onError(Throwable e) {
				log.error("Exception in lookup and registration \n{}", e);
			}

			@Override
			public void onNext(Result<Instrument> args) {
				log.debug("New Instrument Lookup and Registration");
				//
			}
			
		};
	}
	
	public static Instrument getInst(final String barSym) {
		
		final Map<String, List<Instrument>> map = BlockingObservable.from(DDF_RxInstrumentProvider
				.fromString(barSym)).single().results();
		
		Instrument result = BlockingObservable.from(DDF_RxInstrumentProvider
				.fromString(barSym)).single().results().get(barSym).get(0);
		
		return result;
		
	}
	
	
}
