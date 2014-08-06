package com.barchart.feed.test.stream;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.base.provider.Symbology;
import com.barchart.feed.base.provider.Symbology.ExpireMonth;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.test.replay.BarchartMarketplaceReplay;
import com.barchart.feed.test.replay.FeedReplay;
import com.barchart.feed.test.replay.FeedReplay.MessageListener;

public class IntegrationTestResultWriter {

	public static void main(final String[] args) throws IOException, InterruptedException {
		
		/* Set time so symbols are parsed correctly */
		Symbology.setMonthYear(ExpireMonth.JAN, 2014);
		
		final String[] symbols = new String[] {
				"AAPL", "GOOG", "IBM",
				"ESH4", "ESM4", "ESU4", "ESZ4", 
				"ZCH4", "ZCK4", "ZCN4", "ZCU4", 
				"XFH4", "XFK4", "XFN4", "XFU4", 
				"RMF4", "RMH4", "RMK4", "RMN4", 
				"KCH4", "KCK4", "KCN4", "KCU4", 
				"DXH4", "DXM4", "DXU4", "DXZ4"
		};
		
		final Map<String, Market> markets = new TreeMap<String, Market>();

		final BarchartMarketplaceReplay marketplace = new BarchartMarketplaceReplay();

		marketplace.subscribe(Market.class, new MarketObserver<Market>() {
			@Override
			public synchronized void onNext(final Market m) {
				markets.put(m.instrument().symbol(), m);
			}
		}, symbols);
		
		final File outFile = new File("/home/gavin/Desktop/ZC-20140110_Result.txt");
		final FileWriter fileWriter = new FileWriter(outFile);
		final BufferedWriter writer = new BufferedWriter(fileWriter);

		final FeedReplay replay = FeedReplay.builder()
				.source(IntegrationTestResultWriter.class.getResource("/ZC-20140110-week.ddf.gz"))
				.start("2014-01-13 00:00:00")
				.end("2014-01-14 08:30:00")
				.listener(new MessageListener() {

					private long lastReport = 0;

					@Override
					public void messageProcessed(final DDF_BaseMessage parsed, final byte[] raw) {
						
						if (parsed instanceof DDF_MarketBase) {
							try {
								final long time = ((DDF_MarketBase) parsed).getTime().asMillisUTC();
								
								// Report every 30 minutes
								if (time - lastReport > 30 * 60 * 1000) {
									
									lastReport = time;
									
									writer.write(MarketSnapshot.printReportLine(time, marketplace, symbols));
									writer.newLine();
									
								}
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					}

				})
				.build(marketplace.maker());
		
		replay.run();
		replay.await();
		
		writer.close();

	}
	
}
