package com.barchart.feed.test.stream;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.client.provider.BarchartMarketplace;

public class Sandbox {

	public static void main(final String... args) throws Exception {

		final Marketplace marketplace = BarchartMarketplace.builder().username("jongsma").password("pass").build();

		marketplace.startup();

		marketplace.subscribe(Market.class, new MarketObserver<Market>() {

			@Override
			public synchronized void onNext(final Market m) {
				System.out.println(m.instrument().symbol() + ": " + m.lastPrice().price());
				//System.out.println(m.instrument().symbol() + ": " + (m.lastPrice().isNull() ? "NULL" : m.lastPrice().price()));
			}

		}, "CTZ14");

		while (true) {
			Thread.sleep(10000);
		}

	}

}
