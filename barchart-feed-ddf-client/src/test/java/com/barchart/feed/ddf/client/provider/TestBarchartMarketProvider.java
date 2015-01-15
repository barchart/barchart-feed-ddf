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

import com.barchart.feed.api.Agent;
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
import com.barchart.feed.inst.Exchanges;

public class TestBarchartMarketProvider {

	private static final Logger log = LoggerFactory.getLogger(
			TestBarchartMarketProvider.class);
	
	private static final String[] insts = {
		"CLH500C" // RMU15 ZCU16
	};
	
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
		
// 		final Agent ag = market.subscribe(Market.class, marketObs(), Exchanges.fromName("AMEX"));
		
		final ConsumerAgent agent1 = market.register(marketObs(), Market.class);
		final ConsumerAgent agent2 = market.register(marketObs(), Market.class);
//		final ConsumerAgent agent2 = market.register(bookObs(), Book.class);
//		final ConsumerAgent agent3 = market.register(sessObs(), Session.class);
//		final ConsumerAgent agent4 = market.register(tradeObs(), Trade.class);
		
//		agent1.include(Exchanges.fromName("BATS"));
//		agent1.include(insts).subscribe(instObs());
//		agent2.include(insts).subscribe(instObs());
//		agent3.include(insts).subscribe(instObs());
//		agent4.include(insts).subscribe(instObs());
		
		agent1.include("CL^O").subscribe();
		
		Thread.sleep(30 * 1000);

		log.debug("Shutting down");
		market.shutdown();
		
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

			private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss z");
			
			//GSAT,PLUG,ADP,ICPT,AVNR
			
			@Override
			public void onNext(final Market m) {
				
				System.out.println(m.toString());
				
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
