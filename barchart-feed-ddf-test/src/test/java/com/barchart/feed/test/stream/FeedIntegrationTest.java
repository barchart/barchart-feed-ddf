package com.barchart.feed.test.stream;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.base.provider.Symbology;
import com.barchart.feed.base.provider.Symbology.ExpireMonth;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.util.ClockDDF;
import com.barchart.feed.test.replay.BarchartMarketplaceReplay;
import com.barchart.feed.test.replay.FeedReplay;
import com.barchart.feed.test.replay.FeedReplay.MessageListener;

public class FeedIntegrationTest {
	
	private static final Logger log = LoggerFactory.getLogger(
			FeedIntegrationTest.class);

	@Test
	public void HistoricReplayMarketStateTest() throws InterruptedException, IOException {
		
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
		
		final String[] products = new String[] {
				"AAPL",
				"GOOG",   
				"IBM", 
				"ES", 
				"KC", 
				"RM", 
				"XF", 
				"ZC"
		};
		
		for(final String prod : products) {
		
			/* Read results from file */
			final InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(prod + "-20140110_Result.txt");
			final InputStreamReader inReader = new InputStreamReader(inStream);
			final BufferedReader reader = new BufferedReader(inReader);
			
			/* Create marketplace */
			final BarchartMarketplaceReplay marketplace = new BarchartMarketplaceReplay();

			marketplace.subscribe(Market.class, new MarketObserver<Market>() {
				@Override
				public synchronized void onNext(final Market m) {
					
				}
			}, symbols);
			
			final FeedReplay replay = FeedReplay.builder()
					.source(IntegrationTestResultWriter.class.getResource("/" + prod + "-20140110-week.ddf.gz"))
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
										
										final String testLine = reader.readLine();
										final String curLine = MarketSnapshot.printReportLine(
												time, marketplace, symbols);
										
										if(!testLine.equals(curLine)) {
											log.error("Line mismatch for product {}", prod);
											log.error("Expected Line  {}", testLine);
											log.error("Current line   {}", curLine);
											assertTrue(false);
										}
										
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
			
			log.debug("Product {} test passed", prod);
			assertTrue(true);
		
			inStream.close();
			
			ClockDDF.reset();
			
		}
		
	}
	
}
