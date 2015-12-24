package com.barchart.feed.ddf.examples;


import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.connection.Connection.State;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.client.provider.BarchartMarketplace;

/**
 * Simple example to subscribe for all Market events for GOOG.
 * 
 * Requires system properties:
 * 
 * -Dbarchart.username=<name> -Dbarchart.password=<password>
 * 
 */
public class ClientExampleTCP {

	public static void main(final String[] args) throws Exception {

		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		if (username == null || password == null) {
			throw new IllegalArgumentException("username and password are required");
		}

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

				System.out.println(market);

			}

		};

		// Subscribe to all events for GOOG
		Agent agent = feed.subscribe(Market.class, eventHandler, "GOOG");

		Thread.sleep(1000000);

		agent.terminate();

		feed.shutdown();

	}

}
