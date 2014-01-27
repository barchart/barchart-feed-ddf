package com.barchart.feed.test.stream;

import java.util.Map;
import java.util.TreeMap;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.test.replay.BarchartMarketplaceReplay;
import com.barchart.feed.test.replay.FeedReplay;
import com.barchart.feed.test.replay.FeedReplay.MessageListener;

public class FeedSnapshot implements Runnable {

	public static void main(final String... args) throws Exception {
		new FeedSnapshot().run();
	}

	@Override
	public void run() {

		final String[] symbols = new String[] {
				"ESH4", "ESM4", "ESU4", "ESZ4", "ZCH4", "ZCK4", "ZCN4", "ZCU4", "XFH4", "XFK4", "XFN4", "XFU4", "RMF4",
				"RMH4", "RMK4", "RMN4", "KCH4", "KCK4", "KCN4", "KCU4", "DXH14", "DXM4", "DXU4", "DXZ4"
		};

		final Map<String, Market> markets = new TreeMap<String, Market>();

		final BarchartMarketplaceReplay marketplace = new BarchartMarketplaceReplay();

		marketplace.subscribe(Market.class, new MarketObserver<Market>() {
			@Override
			public synchronized void onNext(final Market m) {
				markets.put(m.instrument().symbol(), m);
			}
		}, symbols);

		FeedReplay.builder()
				.source(getClass().getResource("/DX-20140110-week.ddf.gz"))
				.start("2014-01-13 00:00:00")
				.end("2014-01-14 08:30:00")
				.listener(new MessageListener() {

					private long lastReport = 0;

					@Override
					public void messageProcessed(final DDF_BaseMessage parsed, final byte[] raw) {
						if (parsed instanceof DDF_MarketBase) {
							final long time = ((DDF_MarketBase) parsed).getTime().asMillisUTC();
							// Report every 30 minutes
							if (time - lastReport > 30 * 60 * 1000) {
								MarketSnapshot.printReport(marketplace, symbols);
								lastReport = time;
							}
						}
					}

				})
				.build(marketplace.maker())
				.run();

		MarketSnapshot.printReport(marketplace, symbols);

	}

}
