package com.barchart.feed.ddf.client.provider;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.connection.Connection.State;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.client.provider.BarchartMarketplace;

public class ClientExampleTCP {

	public static void main(final String[] args) throws Exception {

		final String username = System.getProperty("barchart.username", "dlucek");
		final String password = System.getProperty("barchart.password", "barchart");

		// Create Client
		final Marketplace feed = new BarchartMarketplace(username, password);

		// Add connection listener
		feed.bindConnectionStateListener(new Connection.Monitor() {

			@Override
			public void handle(State state, Connection connection) {
				System.out.println("Connection: " + connection + " state: " + state);
			}

		});

		// Start Client Event Loop
		feed.startup();

		// Create Market event handler
		final MarketObserver<Market> eventHandler = new MarketObserver<Market>() {

			@Override
			public void onNext(Market market) {

				// System.out.println(
				// "\n############################################################################################################################");
				System.out.println(market);
				// throw new RuntimeException("Exception thrown");
				// System.out.println(
				// "############################################################################################################################\n");

			}

		};

		// Subscribe to all events for GOOG
		Agent agent = feed.subscribe(Market.class, eventHandler, "GOOG");

		Thread.sleep(1000000);

		agent.terminate();

		feed.shutdown();

	}

}
