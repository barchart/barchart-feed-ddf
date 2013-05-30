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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.base.bar.api.MarketBar;
import com.barchart.feed.base.bar.enums.MarketBarField;
import com.barchart.feed.base.market.api.Market;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.base.state.enums.MarketStateEntry;
import com.barchart.feed.client.api.FeedStateListener;
import com.barchart.feed.client.enums.FeedState;
import com.barchart.feed.client.provider.BarchartFeedClient;

/**
 * 
 * Stress test to try and break the login/logout lifecycle
 */
public class TestBarchartFeedClient {

	private static final Logger log = LoggerFactory
			.getLogger(TestBarchartFeedClient.class);

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {

		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		final BarchartFeedClient client = new BarchartFeedClient();

		final Instrument[] instruments = { client.lookup("GOOG")};
		final FeedStateListener feedListener = new FeedStateListener() {

			@Override
			public void stateUpdate(final FeedState state) {

				if (state == FeedState.LOGGED_IN) {
					client.addTaker(TakerFactory.makeFactory(instruments));
				}

			}

		};

		client.login(username, password);
		
		client.bindFeedStateListener(feedListener);

		try {
			while (true) {
				Thread.sleep(1000);
			}
		} catch (final Exception e) {
			// Interrupted
		}

		client.shutdown();

	}

	private static class TakerFactory {

		static MarketTaker<Market> makeFactory(
				final Instrument[] instruments) {
			
			return new MarketTaker<Market>() {

				@Override
				public MarketField<Market> bindField() {
					return MarketField.MARKET;
				}

				@Override
				public MarketEvent[] bindEvents() {

					//return MarketEvent.in(MarketEvent.values());
					return new MarketEvent[] { MarketEvent.MARKET_UPDATED };

				}

				@Override
				public Instrument[] bindInstruments() {

					return instruments;

				}

				@Override
				public void onMarketEvent(final MarketEvent event,
						final Instrument instrument, final Market value) {

					final StringBuilder sb = new StringBuilder("Event: ")
							.append(event);

					final MarketBar barCurrent = value
							.get(MarketField.BAR_CURRENT);

					if (!barCurrent.isNull()) {
						sb.append("; price=")
								.append(barCurrent.get(MarketBarField.CLOSE)
										.mantissa())
								.append("; time=")
								.append(barCurrent.get(MarketBarField.BAR_TIME)
										.asDateTime())
								.append("; day=")
								.append(barCurrent.get(
										MarketBarField.TRADE_DATE).asDateTime())
								.append("; settled="
										+ value.get(MarketField.STATE)
												.contains(
														MarketStateEntry.IS_SETTLED));
					}

					log.debug(sb.toString());

				}

			};

		}
	}

}
