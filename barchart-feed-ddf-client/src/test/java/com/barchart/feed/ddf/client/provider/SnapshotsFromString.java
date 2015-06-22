package com.barchart.feed.ddf.client.provider;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.connection.Connection.State;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.client.provider.BarchartMarketplace;

public class SnapshotsFromString {
	
	private static final Logger log = LoggerFactory.getLogger(
			SnapshotsFromString.class);
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final Marketplace market = BarchartMarketplace.builder()
				.username(username)
				.password(password)
				.build();
		
		final CountDownLatch lock = new CountDownLatch(1);
		
		market.bindConnectionStateListener(new Connection.Monitor() {

			@Override
			public void handle(State state, Connection connection) {
				
				if(state != State.CONNECTED) {
					return;
				}
				
				lock.countDown();;
				
			}
			
		});
		
		market.startup();
		
		//
		Market m = market.snapshot("GOOG");
		
		if(m.isNull()) {
			log.debug("***** Market was null");
		} else {
			log.debug("***** Market - \n{}", m);
		}
		
		lock.await();
		
		//
		m = market.snapshot("GOOG");
		
		if(m.isNull()) {
			log.debug("***** Market was null");
		} else {
			log.debug("***** Market - \n{}", m);
		}
		
		Thread.sleep(4000);
		
		//
		m = market.snapshot("GOOG");
		
		if(m.isNull()) {
			log.debug("***** Market was null");
		} else {
			log.debug("***** Market - \n{}", m);
		}
		
		market.shutdown();
		
		
	}

}
