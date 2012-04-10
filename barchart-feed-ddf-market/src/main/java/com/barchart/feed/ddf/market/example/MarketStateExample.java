/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.market.example;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.api.instrument.enums.InstrumentField;
import com.barchart.feed.base.api.instrument.values.MarketInstrument;
import com.barchart.feed.base.api.market.MarketTaker;
import com.barchart.feed.base.api.market.enums.MarketEvent;
import com.barchart.feed.base.api.market.enums.MarketField;
import com.barchart.feed.base.api.market.values.Market;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class MarketStateExample.
 */
public class MarketStateExample {

	private static final Logger log = LoggerFactory.getLogger(MarketStateExample.class);

	static final DDF_Instrument find(final String symbol) {

		final DDF_Instrument instrument = DDF_InstrumentProvider.find(symbol);

		if (instrument.isNull()) {
			log.error("instrument lookup failed for : {}", symbol);
			return null;
		}

		log.info("instrument : {} \n", instrument);

		return instrument;

	}

	private static ArrayList<MarketInstrument> symbolList = new ArrayList<MarketInstrument>();

	private static MarketInstrument[] getSymbols() {
		final MarketInstrument[] symbols = new MarketInstrument[symbolList
				.size()];

		for (int i = 0; i < symbolList.size(); i++) {
			symbols[i] = symbolList.get(i);
		}

		return symbols;

	}

	static int i = 0;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(final String[] args) throws Exception {

		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		// final String symbol = "RJZ1";
		// final String symbol = "ESZ1";
		final String symbol = "KCK02";

		// set bats only

		DDF_InstrumentProvider.overrideLookupURL(false);

		//
		final DDF_Instrument instrument2 = DDF_InstrumentProvider.find("MSFT");
		final DDF_Instrument instrument = DDF_InstrumentProvider.find("GOOG");
		// final DDF_Instrument instrument3 =
		// DDF_InstrumentProvider.find("KCK2");

		symbolList.add(instrument);
		symbolList.add(instrument2);
		// symbolList.add(instrument3);

		if (instrument == null) {
			log.error("invalid instrument");
			return;
		}

		//

		final MarketTaker<Market> taker = new MarketTaker<Market>() {

			@Override
			public MarketField<Market> bindField() {
				return MarketField.MARKET;
			}

			@Override
			public MarketEvent[] bindEvents() {
				return MarketEvent.in(MarketEvent.values());
			}

			@Override
			public MarketInstrument[] bindInstruments() {
				return getSymbols();
			}

			@Override
			public void onMarketEvent(final MarketEvent event,
					final MarketInstrument instrument, final Market market) {

				System.out.println("Market "
						+ instrument.get(InstrumentField.ID));

				if (i == 5) {
					symbolList.clear();
					symbolList.add(DDF_InstrumentProvider.find("KCK2"));
					System.out.println("DFFFFFFFFFFFFFFFF");
				}

				i++;
				// log.debug("event : \n{}", event);

				// log.debug("market : \n{}", market);

				// og.debug("final settles : \n{}",
				// market.get(MarketField.STATE).contains(MarketStateEntry.IS_SETTLED));

				// log.debug("prior settle: \n{}",
				// market.get(MarketField.BAR_PREVIOUS).get(MarketBarField.SETTLE));

			}

		};

		//

		final MarketManager manager = new MarketManager();

		final boolean isLoginOK = manager.getClient().login(username, password);

		if (!isLoginOK) {
			log.error("invalid login");
			return;
		}

		Thread.sleep(1 * 1000);

		manager.getMaker().register(taker);

		Thread.sleep(500 * 1000);

		manager.getMaker().unregister(taker);

		Thread.sleep(1 * 1000);

		manager.getClient().logout();

	}

}
