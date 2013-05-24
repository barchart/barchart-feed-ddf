/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.framework.data.InstrumentEntity;
import com.barchart.feed.base.market.api.Market;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class LoggingHandler.
 */
public class LoggingHandler implements DDF_MessageListener {

	private static final Logger log = LoggerFactory
			.getLogger(LoggingHandler.class);

	private final File file = new File(
			"C:\\Users\\g-litchfield.BCINC\\Desktop\\DDFConnectionTest.txt");

	BufferedWriter writer = null;

	private static ArrayList<InstrumentEntity> symbolList = new ArrayList<InstrumentEntity>();

	final MarketTaker<Market> taker;

	private static InstrumentEntity[] getSymbols() {
		final InstrumentEntity[] symbols = new InstrumentEntity[symbolList
				.size()];

		for (int i = 0; i < symbolList.size(); i++) {
			symbols[i] = symbolList.get(i);
		}

		return symbols;

	}

	public LoggingHandler() {
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (final IOException e) {
			e.printStackTrace();
		}

		final InstrumentEntity instrument2 = DDF_InstrumentProvider.find("XFU2");
		final InstrumentEntity instrument = DDF_InstrumentProvider.find("RMN2");

		symbolList.add(instrument);
		symbolList.add(instrument2);

		taker = new MarketTaker<Market>() {

			@Override
			public MarketField<Market> bindField() {
				return MarketField.MARKET;
			}

			@Override
			public MarketEvent[] bindEvents() {
				return MarketEvent.in(MarketEvent.values());
			}

			@Override
			public InstrumentEntity[] bindInstruments() {
				return getSymbols();
			}

			@Override
			public void onMarketEvent(final MarketEvent event,
					final InstrumentEntity instrument, final Market value) {

				try {
					// writer.write(event.toString());
					log.debug(event.toString());
					log.debug(value.toString());
				} catch (final Exception e) {
					e.printStackTrace();
				}

			}

		};

	}

	@Override
	public void handleMessage(final DDF_BaseMessage message) {

		// Timestamp
		log.debug("message : {}", message.toString());

		try {
			writer.write(message.toString() + "\n");
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

}
