/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.client.provider.test;

import com.barchart.feed.base.bar.api.MarketBar;
import com.barchart.feed.base.book.api.MarketBookEntry;
import com.barchart.feed.base.book.api.MarketBookTop;
import com.barchart.feed.base.book.enums.MarketBookSide;
import com.barchart.feed.base.instrument.enums.InstrumentField;
import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.Market;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.client.provider.BarchartFeedReceiver;
import com.barchart.util.values.util.ValueUtil;

public class TestReceiver {
	
	/**
	 * 
	 * Arguments = { TCP / UDP , port, instrument1, instrument2, ...}
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String... args) throws Exception {
		
		final int INST_START = 2;
		
		if(args == null) {
			throw new RuntimeException("No arguments passed to main");
		}

		final BarchartFeedReceiver client = new BarchartFeedReceiver();
		
		final MarketInstrument[] instruments = new MarketInstrument[args.length - INST_START];
		
		for(int i = INST_START; i < args.length; i++) {
			instruments[i-INST_START] = client.lookup(args[i]);
		}
		
		if(args[0].equals("TCP")) {
			client.listenTCP(Integer.parseInt(args[1]), false);
		} else if(args[0].equals("UDP")) {
			client.listenUDP(Integer.parseInt(args[1]), false);
		} else {
			throw new RuntimeException("Bad protocol, expecting UDP or TCP");
		}
		
		client.addAllMarketsTaker(TakerFactory.makeTaker(instruments));
		
		System.in.read();
		client.shutdown();
		System.exit(0);
		
	}
	
	private static class TakerFactory {

		static MarketTaker<Market> makeTaker(
				final MarketInstrument[] instruments) {
			
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
				public MarketInstrument[] bindInstruments() {

					return instruments;

				}

				@Override
				public void onMarketEvent(final MarketEvent event,
						final MarketInstrument instrument, final Market value) {

					final StringBuilder sb = new StringBuilder(value.get(MarketField.INSTRUMENT)
								.get(InstrumentField.ID))
							.append(" ")
							.append(event)
							.append(" EventTime=")
							.append(value.get(MarketField.MARKET_TIME).asDateTime().toString());

					final MarketBookTop top = value.get(MarketField.BOOK_TOP);
					
					MarketBookEntry entry = top.side(MarketBookSide.ASK);
					
					if(!entry.isNull()) {
						sb.append(" ASK TOP")
						.append(" price=").append(ValueUtil.asDouble(entry.price()))
						.append(" qty=").append(entry.size().asLong());
					}
					
					entry = top.side(MarketBookSide.BID);
					
					if(!entry.isNull()) {
						sb.append(" BID TOP")
						.append(" price=").append(ValueUtil.asDouble(entry.price()))
						.append(" qty=").append(entry.size().asLong());
					}

					final MarketBar barCurrent = value
							.get(MarketField.BAR_CURRENT);
					
					System.out.println(sb.toString());

				}

			};

		}
	}

}
