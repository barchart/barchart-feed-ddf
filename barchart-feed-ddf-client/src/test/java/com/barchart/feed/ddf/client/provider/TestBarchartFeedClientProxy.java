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
import com.barchart.feed.ddf.datalink.api.DDF_SocksProxy;

/**
 * 
 * Stress test to try and break the login/logout lifecycle
 */
public class TestBarchartFeedClientProxy {

	private static final Logger log = LoggerFactory
			.getLogger(TestBarchartFeedClientProxy.class);

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {

		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		final BarchartFeedClient client = new BarchartFeedClient();

		final String symbol = "RMU12";

		final Instrument instrument = client.lookup(symbol);

		System.out.println(instrument.toString());

		final FeedStateListener feedListener = new FeedStateListener() {

			@Override
			public void stateUpdate(final FeedState state) {

				if (state == FeedState.LOGGED_IN) {
					client.addTaker(TakerFactory.makeFactory(instrument));
				}
				
				log.error("STATE = " + state);

			}

		};

		
		final DDF_SocksProxy proxySettings = new DDF_SocksProxy("10.222.4.184", 1080);
		
		proxySettings.setProxyUsername("");
		proxySettings.setProxyPassword("");
		
		//client.login(username, password);

		client.login(username, password, proxySettings);
		
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

		static MarketTaker<Market> makeFactory(final Instrument instrument) {
			return new MarketTaker<Market>() {

				@Override
				public MarketField<Market> bindField() {

					return MarketField.MARKET;

				}

				@Override
				public MarketEvent[] bindEvents() {

					return MarketEvent.in(MarketEvent.values());
					// return new MarketEvent[] { MarketEvent.NEW_TRADE };

				}

				@Override
				public Instrument[] bindInstruments() {

					return new Instrument[] { instrument };

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
