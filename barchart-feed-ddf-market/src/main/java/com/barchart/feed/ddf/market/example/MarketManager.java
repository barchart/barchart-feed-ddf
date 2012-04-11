/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.market.example;

import java.util.Set;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.api.instrument.values.MarketInstrument;
import com.barchart.feed.base.api.market.MarketMaker;
import com.barchart.feed.base.api.market.enums.MarketEvent;
import com.barchart.feed.base.api.market.provider.MarketRegListener;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.datalink.api.DDF_FeedHandler;
import com.barchart.feed.ddf.datalink.enums.DDF_FeedEvent;
import com.barchart.feed.ddf.datalink.enums.DDF_FeedInterest;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedService;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField;
import com.barchart.feed.ddf.market.api.DDF_MarketProvider;
import com.barchart.feed.ddf.market.provider.DDF_MarketService;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.util.FeedDDF;

// TODO: Auto-generated Javadoc
/**
 * The Class MarketManager.
 */
public class MarketManager implements DDF_FeedHandler, MarketRegListener {

	private static final Logger log = LoggerFactory
			.getLogger(MarketManager.class);

	private final DDF_FeedClient client;
	private final DDF_MarketProvider maker;

	/**
	 * Instantiates a new market manager.
	 */
	public MarketManager() {

		final Executor runner = new Executor() {
			@Override
			public void execute(final Runnable task) {
				new Thread(task).start();
			}
		};

		client = DDF_FeedService.newInstance(runner);
		maker = DDF_MarketService.newInstance();

		client.bind(this);
		maker.add(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.datalink.api.DDF_FeedHandler#handleEvent(com.barchart
	 * .feed.ddf.datalink.enums.DDF_FeedEvent)
	 */
	@Override
	public void handleEvent(final DDF_FeedEvent event) {

		log.debug("event : {}", event);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.datalink.api.DDF_FeedHandler#handleMessage(com.
	 * barchart.feed.ddf.message.api.DDF_BaseMessage)
	 */
	@Override
	public void handleMessage(final DDF_BaseMessage message) {

		log.debug("message : {}", message);

		if (message instanceof DDF_MarketBase) {

			final DDF_MarketBase marketMessage = (DDF_MarketBase) message;

			maker.make(marketMessage);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.base.api.market.provider.MarketRegListener#
	 * onRegistrationChange
	 * (com.barchart.feed.base.api.instrument.values.MarketInstrument,
	 * java.util.Set)
	 */
	@Override
	public void onRegistrationChange(final MarketInstrument instrument,
			final Set<MarketEvent> events) {

		final DDF_Instrument instrumentDDF = (DDF_Instrument) instrument;

		final CharSequence symbol = instrumentDDF
				.get(DDF_InstrumentField.DDF_SYMBOL_REALTIME);

		final CharSequence interest = DDF_FeedInterest.from(events);

		final CharSequence command = FeedDDF.tcpGo(symbol, interest);

		log.debug("command : {}", command);

		client.send(command);

	}

	/**
	 * Gets the maker.
	 * 
	 * @return the maker
	 */
	public MarketMaker getMaker() {
		return maker;
	}

	/**
	 * Gets the client.
	 * 
	 * @return the client
	 */
	public DDF_FeedClient getClient() {
		return client;
	}

}
