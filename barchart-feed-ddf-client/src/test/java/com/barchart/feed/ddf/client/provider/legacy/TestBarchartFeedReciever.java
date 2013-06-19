/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.client.provider.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.bar.api.MarketBar;
import com.barchart.feed.base.bar.enums.MarketBarField;
import com.barchart.feed.base.market.api.Market;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.base.state.enums.MarketStateEntry;

public class TestBarchartFeedReciever {

	private static final Logger log = LoggerFactory
			.getLogger(TestBarchartFeedReciever.class);
	
	
	public static void main(final String[] args) throws Exception {
		
		BarchartFeedReceiver client = new BarchartFeedReceiver();
		
		final Instrument[] instruments = { client.lookup("ESU13")};
		
		client.listenTCP(7000, true); 
		client.addTaker(TakerFactory.makeFactory(instruments));
		
		Thread.sleep(60 * 1000);
		client.shutdown();		
		System.exit(0);
		
	}
	
	private static class TakerFactory {

		static MarketTaker<Market> makeFactory(
				final Instrument[] instruments) {
			
			return new MarketTaker<Market>() {

				@Override
				public MarketField<Market> bindField() {
					return MarketField.MARKET;
				}

				@Override
				public MarketEvent[] bindEvents() {

					//return MarketEvent.in(MarketEvent.values());
					return MarketEvent.in(MarketEvent.values());

				}

				@Override
				public Instrument[] bindInstruments() {

					return instruments;

				}

				@Override
				public void onMarketEvent(final MarketEvent event,
						final Instrument instrument, final Market value) {

					final StringBuilder sb = new StringBuilder("Event: ")
							.append(event);

					final MarketBar barCurrent = value
							.get(MarketField.BAR_CURRENT);

					if (!barCurrent.isNull()) {
						sb.append("; price=")
								.append(barCurrent.get(MarketBarField.CLOSE)
										.mantissa())
								.append("; time=")
								.append(barCurrent.get(MarketBarField.BAR_TIME)
										.asDateTime())
								.append("; day=")
								.append(barCurrent.get(
										MarketBarField.TRADE_DATE).asDateTime())
								.append("; settled="
										+ value.get(MarketField.STATE)
												.contains(
														MarketStateEntry.IS_SETTLED));
					}

					log.debug(sb.toString());

				}

			};

		}
	}
	
}
