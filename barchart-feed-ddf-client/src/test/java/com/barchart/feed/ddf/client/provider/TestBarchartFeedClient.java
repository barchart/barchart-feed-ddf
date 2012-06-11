/**
 * 
 */
package com.barchart.feed.ddf.client.provider;

import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.Market;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.ddf.datalink.api.DDF_FeedStateListener;
import com.barchart.feed.ddf.datalink.enums.DDF_FeedState;

/**
 * 
 * 
 */
public class TestBarchartFeedClient {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {

		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		final BarchartFeedClient client =
				new BarchartFeedClient(username, password);

		final String symbol = "GOOG";

		final MarketInstrument instrument = client.lookup(symbol);

		System.out.println(instrument.toString());

		final DDF_FeedStateListener feedListener = new DDF_FeedStateListener() {

			@Override
			public void stateUpdate(final DDF_FeedState state) {

				if (state == DDF_FeedState.LOGGED_IN) {
					client.addTaker(TakerFactory.makeFactory(instrument));
				}

			}

		};

		client.bindFeedStateListener(feedListener);

		client.startup();

		Thread.sleep(20 * 1000);

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
