/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.client.provider;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.enums.MarketSide;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.market.api.Market;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.client.provider.BarchartFeedReceiver;
import com.barchart.util.values.util.ValueUtil;

public class TestAllMarketsReceiver {

	private static final Logger log = LoggerFactory
			.getLogger(TestAllMarketsReceiver.class);

	public static void main(final String[] args) {

		final ExecutorService executor = Executors.newCachedThreadPool();
		
		
		
		final BarchartFeedReceiver client = new BarchartFeedReceiver(executor);

		try {
			
			final Instrument[] instruments1 = {};
			final Instrument[] instruments2 = { client.lookup("ESH13") };
	
			Future<Boolean> task;
			
			try {
				task = client.listenTCP(7000, false);
				task.get();
			} catch (final ExecutionException ex) {
				ex.getCause().printStackTrace();
				throw ex;
			}
			
			if(!task.get()) {
				throw new Exception("Instrument db update failed");
			}
			
			log.debug("Adding market takers");
			
			final MarketTaker<Market> taker1 =
					TakerFactory.makeFactory1(instruments1);
			final MarketTaker<Market> taker2 =
					TakerFactory.makeFactory2(instruments2);
	
			client.addAllMarketsTaker(taker1);
			client.addTaker(taker2);
	
			// Thread.sleep(10 * 1000);
			// client.removeTaker(taker2);
			Thread.sleep(10 * 10 * 60 * 1000);
		
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			client.shutdown();
			executor.shutdownNow();
		}

	}

	private static class TakerFactory {

		static MarketTaker<Market> makeFactory1(
				final Instrument[] instruments) {

			return new MarketTaker<Market>() {

				@Override
				public MarketField<Market> bindField() {
					return MarketField.MARKET;
				}

				@Override
				public MarketEvent[] bindEvents() {
					return new MarketEvent[] { MarketEvent.MARKET_UPDATED };
				}

				@Override
				public Instrument[] bindInstruments() {
					return instruments;
				}

				@Override
				public void onMarketEvent(final MarketEvent event,
						final Instrument instrument, final Market value) {

					final StringBuilder sb =
							new StringBuilder("Taker 1 Event: ").append(event);

					sb.append(" " + instrument.marketGUID());

					sb.append(" BID "
							+ ValueUtil.asDouble(value
									.get(MarketField.BOOK_TOP)
									.side(MarketSide.BID).priceValue()));

					log.debug(sb.toString());

				}

			};

		}

		static MarketTaker<Market> makeFactory2(
				final Instrument[] instruments) {

			return new MarketTaker<Market>() {

				@Override
				public MarketField<Market> bindField() {
					return MarketField.MARKET;
				}

				@Override
				public MarketEvent[] bindEvents() {

					return new MarketEvent[] { MarketEvent.MARKET_UPDATED };

				}

				@Override
				public Instrument[] bindInstruments() {

					return instruments;

				}

				@Override
				public void onMarketEvent(final MarketEvent event,
						final Instrument instrument, final Market value) {

					 final StringBuilder sb = new
					 StringBuilder(" *** TAKER 2 EVENT: ")
					 .append(instrument.marketGUID())
					 .append("******************************************************************************/n")
					 .append("******************************************************************************/n")
					 .append("******************************************************************************/n");
					
					 log.debug(sb.toString());

				}

			};

		}

	}
}
