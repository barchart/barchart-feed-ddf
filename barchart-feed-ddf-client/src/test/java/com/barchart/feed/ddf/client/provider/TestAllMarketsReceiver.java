package com.barchart.feed.ddf.client.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.book.enums.MarketBookSide;
import com.barchart.feed.base.instrument.enums.InstrumentField;
import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.Market;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.client.provider.BarchartFeedReceiver;
import com.barchart.util.values.util.ValueUtil;

public class TestAllMarketsReceiver {
	
	private static final Logger log = LoggerFactory
			.getLogger(TestAllMarketsReceiver.class);
	
	public static void main(final String[] args) throws Exception {
		
		BarchartFeedReceiver client = new BarchartFeedReceiver();
		
		final MarketInstrument[] instruments1 = {};
		final MarketInstrument[] instruments2 = {client.lookup("ESZ2")};
		
		client.listenTCP(7000, false);
		
		final MarketTaker<Market> taker1 = TakerFactory.makeFactory1(instruments1);
		final MarketTaker<Market> taker2 = TakerFactory.makeFactory2(instruments2);
		
		client.addAllMarketsTaker(taker1);
		client.addTaker(taker2);
		
		//Thread.sleep(10 * 1000);
		//client.removeTaker(taker2);
		Thread.sleep(10 * 60 * 1000);
		client.shutdown();		
		System.exit(0);
		
	}

	
	private static class TakerFactory {

		static MarketTaker<Market> makeFactory1(final MarketInstrument[] instruments) {
			
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

					final StringBuilder sb = new StringBuilder("Taker 1 Event: ")
							.append(event);

					sb.append(" " + instrument.get(InstrumentField.ID));

					sb.append(" BID " + ValueUtil.asDouble(value.get(MarketField.BOOK_TOP).side(MarketBookSide.BID).price()));
					
					log.debug(sb.toString());

				}

			};

		}

		static MarketTaker<Market> makeFactory2(final MarketInstrument[] instruments) {
			
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

					final StringBuilder sb = new StringBuilder(" *** TAKER 2 EVENT: ")
							.append(instrument.get(InstrumentField.ID).toString())
							.append("******************************************************************************/n")
							.append("******************************************************************************/n")
							.append("******************************************************************************/n");

					log.debug(sb.toString());

				}

			};

		}
		
	}
}
