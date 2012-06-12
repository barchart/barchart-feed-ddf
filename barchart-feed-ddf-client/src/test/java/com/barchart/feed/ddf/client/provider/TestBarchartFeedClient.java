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
 * 
 * Stress test to try and break the login/logout lifecycle
 */
public class TestBarchartFeedClient {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {

		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		final BarchartFeedClient client = new BarchartFeedClient();

		final String symbol = "GOOG";

		final MarketInstrument instrument = client.lookup(symbol);

		System.out.println(instrument.toString());

		final FeedStateListener feedListener = new FeedStateListener() {

			@Override
			public void stateUpdate(final FeedState state) {

				if (state == FeedState.LOGGED_IN) {
					client.addTaker(TakerFactory.makeFactory(instrument));
				}

			}

		};

		for (int i = 0; i < 100; i++) {

			if (Math.random() < 0.5) {

				client.login(username, password);

				client.bindFeedStateListener(feedListener);

			} else {
				client.shutdown();
			}

			Thread.sleep((long) (Math.random() * 3000));

		}

		client.shutdown();

	}

	private static class TakerFactory {

		static MarketTaker<Market>
				makeFactory(final MarketInstrument instrument) {
			return new MarketTaker<Market>() {

				@Override
				public MarketField<Market> bindField() {

					return MarketField.MARKET;

				}

				@Override
				public MarketEvent[] bindEvents() {

					return MarketEvent.in(MarketEvent.values());

				}

				@Override
				public MarketInstrument[] bindInstruments() {

					return new MarketInstrument[] { instrument };

				}

				@Override
				public void onMarketEvent(final MarketEvent event,
						final MarketInstrument instrument, final Market value) {

					System.out.println(value.get(MarketField.BOOK_TOP)
							.toString());

				}

			};

		}
	}

}
