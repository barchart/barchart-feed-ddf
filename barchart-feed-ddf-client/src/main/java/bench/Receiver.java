/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package bench;

import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.api.consumer.enums.MarketSide;
import com.barchart.feed.api.framework.data.InstrumentEntity;
import com.barchart.feed.api.framework.data.InstrumentField;
import com.barchart.feed.base.book.api.MarketBookEntry;
import com.barchart.feed.base.book.api.MarketBookTop;
import com.barchart.feed.base.market.api.Market;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.client.provider.BarchartFeedReceiver;
import com.barchart.util.values.util.ValueUtil;

/**
 * 
 * you can invoke this utility from command line:
 * 
 * <pre>
 * java - jar barchart-feed-ddf-client-X.Y.Z.jar bench.Receiver TCP 7500 IBM
 * </pre>
 * 
 * arguments = { TCP / UDP , port, instrument1, instrument2, ...}
 * 
 */
public class Receiver {

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String... args) throws Exception {

		final int INST_START = 2;

		if (args == null) {
			throw new RuntimeException("No arguments passed to main");
		}

		final BarchartFeedReceiver client = new BarchartFeedReceiver();

		final InstrumentEntity[] instruments = new InstrumentEntity[args.length
				- INST_START];

		for (int i = INST_START; i < args.length; i++) {
			instruments[i - INST_START] = client.lookup(args[i]);
		}

		if (args[0].equals("TCP")) {
			client.listenTCP(Integer.parseInt(args[1]), false);
		} else if (args[0].equals("UDP")) {
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
				public InstrumentEntity[] bindInstruments() {

					return instruments;

				}

				@Override
				public void onMarketEvent(final MarketEvent event,
						final InstrumentEntity instrument, final Market value) {

					final StringBuilder sb = new StringBuilder(value.get(
							MarketField.INSTRUMENT).get(InstrumentField.MARKET_GUID).toString())
							.append(" ")
							.append(event)
							.append(" EventTime=")
							.append(value.get(MarketField.MARKET_TIME)
									.asDateTime().toString());

					final MarketBookTop top = value.get(MarketField.BOOK_TOP);

					MarketBookEntry entry = top.side(MarketSide.ASK);

					if (!entry.isNull()) {
						sb.append(" ASK TOP").append(" price=")
								.append(ValueUtil.asDouble(entry.priceValue()))
								.append(" qty=").append(entry.sizeValue().asLong());
					}

					entry = top.side(MarketSide.BID);

					if (!entry.isNull()) {
						sb.append(" BID TOP").append(" price=")
								.append(ValueUtil.asDouble(entry.priceValue()))
								.append(" qty=").append(entry.sizeValue().asLong());
					}

					System.out.println(sb.toString());

				}

			};

		}
	}

}
