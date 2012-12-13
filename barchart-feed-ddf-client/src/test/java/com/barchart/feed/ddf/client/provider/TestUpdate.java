/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/**
 * 
 */
package com.barchart.feed.ddf.client.provider;

import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.Market;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.client.api.FeedStateListener;
import com.barchart.feed.client.enums.FeedState;
import com.barchart.feed.client.provider.BarchartFeedClient;

/**
 * @author g-litchfield
 * 
 */
public class TestUpdate {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {

		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		final BarchartFeedClient client = new BarchartFeedClient();

		final MarketInstrument[] initInsts = new MarketInstrument[3];

		initInsts[0] = client.lookup("GOOG");
		initInsts[1] = client.lookup("AAPL");
		initInsts[2] = client.lookup("FB");

		final MarketInstrument[] newInsts = new MarketInstrument[3];

		newInsts[0] = client.lookup("AAPL");
		newInsts[1] = client.lookup("FB");
		newInsts[2] = client.lookup("ORCL");

		final Switch taker = new Switch(initInsts, newInsts);

		final FeedStateListener feedListener = new FeedStateListener() {

			@Override
			public void stateUpdate(final FeedState state) {

				if (state == FeedState.LOGGED_IN) {
					client.addTaker(taker);
				}
			}
		};

		client.login(username, password);
		client.bindFeedStateListener(feedListener);

		Thread.sleep(5000);

		System.out
				.println("**********************************************************");
		System.out
				.println("**********************************************************");
		System.out
				.println("**********************************************************");
		System.out
				.println("**********************************************************");
		System.out
				.println("**********************************************************");

		taker.switchInsts();
		client.updateTaker(taker);

		Thread.sleep(5000);

		client.shutdown();

	}

	public static class Switch implements MarketTaker<Market> {

		MarketInstrument[] old;
		MarketInstrument[] newI;
		MarketInstrument[] curr;

		Switch(final MarketInstrument[] old, final MarketInstrument[] newI) {
			this.old = old;
			this.newI = newI;
			this.curr = old;
		}

		public void switchInsts() {
			curr = newI;
		}

		@Override
		public MarketEvent[] bindEvents() {
			return MarketEvent.in(MarketEvent.values());
		}

		@Override
		public MarketField<Market> bindField() {
			return MarketField.MARKET;
		}

		@Override
		public MarketInstrument[] bindInstruments() {
			return curr;
		}

		@Override
		public void onMarketEvent(final MarketEvent event,
				final MarketInstrument instrument, final Market value) {
			System.out.println(value.get(MarketField.BOOK_TOP).toString());

		}

	}
}
