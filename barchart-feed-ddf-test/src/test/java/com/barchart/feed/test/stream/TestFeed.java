package com.barchart.feed.test.stream;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.client.provider.BarchartMarketplace;

public class TestFeed {

	public static void main(final String... args) throws Exception {

		final String[] symbols = new String[] {
				"ESH4", "ESM4", "ESU4", "ESZ4", "ZCH4", "ZCK4", "ZCN4", "ZCU4", "XFH4", "XFK4", "XFN4", "XFU4",
				"RMF4", "RMH4", "RMK4", "RMN4", "KCH4", "KCK4", "KCN4", "KCU4", "DXH14", "DXM4", "DXU4",
				"DXZ4", "GOOG.BZ", "IBM.BZ", "AAPL.BZ", "FXE.BZ", "IAU.BZ", "INTC.BZ", "S.BZ", "SPY.BZ",
				"XOM.BZ", "ZNGA.BZ"
		};

		final Marketplace marketplace = BarchartMarketplace.builder().username("jongsma").password("pass").build();

		marketplace.subscribe(Market.class, new MarketObserver<Market>() {

			private long lastReport = 0;

			@Override
			public void onNext(final Market m) {
				final long time = System.currentTimeMillis();
				// Report every 5 seconds
				if (time - lastReport > 5 * 1000) {
					MarketSnapshot.printReport(marketplace, symbols);
					lastReport = time;
				}
			}

		}, symbols);

		marketplace.startup();

		while (true) {
			Thread.sleep(10000);
			MarketSnapshot.printReport(marketplace, symbols);
		}

	}

}
