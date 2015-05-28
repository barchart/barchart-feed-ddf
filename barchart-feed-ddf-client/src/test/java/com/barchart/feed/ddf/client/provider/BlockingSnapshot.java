package com.barchart.feed.ddf.client.provider;

import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.client.provider.BarchartMarketplace;

/**
 * This test shows that under normal circumstances blocking on the snapshots
 * observable does not cause a deadlock with the feed. 
 */
public class BlockingSnapshot {

	private static final InstrumentID instID = new InstrumentID(1261904); // GOOG

	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final Marketplace market = BarchartMarketplace.builder()
				.username(username)
				.password(password)
				.build();
		
		market.startup();
		
		final Market test = market.snapshot(instID)
				.toBlockingObservable()
				.first();
		
		System.out.println("Passed Block");
		
		System.out.println(test.toString());
		
		Thread.sleep(5 * 1000);
		
		market.shutdown();
		
	}
	
}
