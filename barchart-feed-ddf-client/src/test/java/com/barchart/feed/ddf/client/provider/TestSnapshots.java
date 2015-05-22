package com.barchart.feed.ddf.client.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observer;

import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Session.Type;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.client.provider.BarchartMarketplace;

public class TestSnapshots {
	
	private static final Logger log = LoggerFactory.getLogger(
			TestSnapshots.class);
	
	private static final InstrumentID instID //= new InstrumentID("1261904"); // GOOG
	= new InstrumentID(230878362); // ESH15
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final Marketplace market = BarchartMarketplace.builder()
				.username(username)
				.password(password)
				.build();
		
		market.startup();
		
		market.snapshot(instID).subscribe(new Observer<Market>() {

			@Override
			public void onCompleted() {
				log.debug("On Completed");
			}

			@Override
			public void onError(final Throwable e) {
				log.error("Exception with snapshot", e);
			}

			@Override
			public void onNext(final Market market) {
				//log.debug("Market = \n{}", market);
				System.out.println(market.sessionSet().session(Type.DEFAULT_PREVIOUS).settle());
				log.debug(market.session().settle().toString());
				log.debug("Settle = {}",market.session().settle().asDouble());
			}
			
		});
		
		Thread.sleep(5 * 1000);
		
		market.snapshot(instID).subscribe(new Observer<Market>() {

			@Override
			public void onCompleted() {
				log.debug("On Completed");
			}

			@Override
			public void onError(final Throwable e) {
				log.error("Exception with snapshot", e);
			}

			@Override
			public void onNext(final Market market) {
				//log.debug("Market = \n{}", market);
				log.debug(market.session().settle().toString());
				log.debug("Settle = {}",market.session().settle().asDouble());
			}
			
		});
		
		Thread.sleep(5 * 1000);
		
		market.shutdown();
		
	}
	
}
