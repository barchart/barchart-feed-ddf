package com.barchart.feed.test.stream;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Session.Type;
import com.barchart.feed.api.model.data.SessionData;
import com.barchart.feed.client.provider.BarchartMarketplace;

public class Sandbox {

	public static void main(final String... args) throws Exception {

		// final Marketplace marketplace = BarchartMarketplace.builder().username("jongsma").password("pass").build();
		// final Marketplace marketplace = BarchartMarketplace.builder()
		// .feedType(FeedType.LISTENER_TCP)
		// .port(7000)
		// .build();
		final Marketplace marketplace = new BarchartMarketplace("abakus", "barchart");

		marketplace.startup();

		final Agent agent = marketplace.subscribe(Market.class, new MarketObserver<Market>() {

			@Override
			public synchronized void onNext(final Market m) {

				final SessionData current = m.sessionSet().session(Type.DEFAULT_CURRENT);

				if (current != null) {
					System.out.println("++ CURRENT");
					System.out.println("OPEN: " + (current.open().isNull() ? "0" : current.open().toString()));
					System.out.println("HIGH: " + (current.high().isNull() ? "0" : current.high().toString()));
					System.out.println("LOW: " + (current.low().isNull() ? "0" : current.low().toString()));
					System.out.println("CLOSE: " + (current.close().isNull() ? "0" : current.close().toString()));
				}

				final SessionData previous = m.sessionSet().session(Type.DEFAULT_PREVIOUS);

				if (previous != null) {
					System.out.println("++ PREVIOUS");
					System.out.println("OPEN: " + (previous.open().isNull() ? "0" : previous.open().toString()));
					System.out.println("HIGH: " + (previous.high().isNull() ? "0" : previous.high().toString()));
					System.out.println("LOW: " + (previous.low().isNull() ? "0" : previous.low().toString()));
					System.out.println("CLOSE: " + (previous.close().isNull() ? "0" : previous.close().toString()));
				}

			}

		}, "GOOG");

		agent.include("IBM");

		while (true) {
			Thread.sleep(10000);
		}

	}

}
