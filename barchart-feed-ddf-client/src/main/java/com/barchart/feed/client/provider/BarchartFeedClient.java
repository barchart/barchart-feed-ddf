/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/**
 * 
 * The core DDF class which encapsulates all the core functionality a new user
 * will need to get started.
 * <p>
 * Instances are created using the public constructor and require a
 * valid user name and password. Optional parameters include specifying the
 * transport protocol and providing an executor.
 * <p>
 * The price feed is started and stopped using the startup() and shutdown()
 * methods. Note that these are non-blocking calls. Applications requiring
 * actions upon successful login should instantiate and bind a
 * FeedStatusListener to the client.
 * <p>
 * 
 * 
 */
package com.barchart.feed.client.provider;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.instrument.enums.InstrumentField;
import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.MarketRegListener;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.datalink.api.DDF_SocksProxy;
import com.barchart.feed.ddf.datalink.api.Subscription;
import com.barchart.feed.ddf.datalink.enums.DDF_Transport;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;

/**
 * The entry point for Barchart data feed services.
 */
public class BarchartFeedClient extends BarchartFeedClientBase {

	private static final Logger log = LoggerFactory
			.getLogger(BarchartFeedClient.class);

	private volatile DDF_FeedClient feed = null;

	private Executor executor = null;

	public BarchartFeedClient() {

		this(new Executor() {

			private final AtomicLong counter = new AtomicLong(0);

			final String name = "# DDF Client - " + counter.getAndIncrement();

			@Override
			public void execute(final Runnable task) {
				log.debug("executing new runnable = " + task.toString());
				new Thread(task, name).start();
			}

		});

	}

	public BarchartFeedClient(final Executor ex) {
		maker.add(instrumentSubscriptionListener);
		executor = ex;
	}

	/**
	 * Starts the data feed asynchronously. Notification of login success is
	 * reported by FeedStateListeners which are bound to this object.
	 * <p>
	 * Constructs a new feed client with the user's user name and password and a
	 * Socks5 proxy. The transport protocol defaults to TCP and a default
	 * executor are used.
	 * 
	 * @param username
	 * @param password
	 */
	public void login(final String username, final String password,
			final DDF_SocksProxy proxySettings) {

		loginProxy(username, password, DDF_Transport.TCP, executor,
				proxySettings);

	}

	/**
	 * Starts the data feed asynchronously. Notification of login success is
	 * reported by FeedStateListeners which are bound to this object.
	 * <p>
	 * Constructs a new feed client with the user's user name and password. The
	 * transport protocol defaults to TCP and a default executor are used.
	 * 
	 * @param username
	 * @param password
	 */
	public void login(final String username, final String password) {

		loginMain(username, password, DDF_Transport.TCP, executor);

	}

	/**
	 * Starts the data feed asynchronously. Notification of login success is
	 * reported by FeedStateListeners which are bound to this object.
	 * <p>
	 * Constructs a new feed client with the user's user name, password, and
	 * desired transport protocol. A default executor is used.
	 * 
	 * @param username
	 * @param password
	 * @param tp
	 */
	public void login(final String username, final String password,
			final DDF_Transport tp) {

		loginMain(username, password, tp, executor);

	}

	/**
	 * Starts the data feed asynchronously. Notification of login success is
	 * reported by FeedStateListeners which are bound to this object.
	 * <p>
	 * Constructs a new feed client with the user's user name, password, desired
	 * transport protocol, and framework executor.
	 * 
	 * @param username
	 * @param password
	 * @param tp
	 * @param executor
	 */
	public void login(final String username, final String password,
			final DDF_Transport tp, final Executor executor) {

		loginMain(username, password, tp, executor);

	}

	public int marketCount() {
		if (maker == null) {
			return 0;
		}

		return maker.marketCount();
	}

	/*
	 * Handles login. Non-blocking.
	 */
	private void loginMain(final String username, final String password,
			final DDF_Transport tp, final Executor executor) {

		maker.clearAll();

		feed = DDF_FeedClientFactory.newConnectionClient(tp, username,
				password, executor);

		setClient(feed, false);

	}

	private void loginProxy(final String username, final String password,
			final DDF_Transport tp, final Executor executor,
			final DDF_SocksProxy proxySettings) {

		maker.clearAll();

		feed = DDF_FeedClientFactory.newConnectionClient(tp, username,
				password, executor, proxySettings);

		setClient(feed, true);
	}

	/*
	 * This is where the instruments are registered and unregistered as needed
	 * by the market maker. Subscribe events are sent when the instrument has
	 * not been bound by a previously registered market taker. Unsubscribe
	 * events are sent only when the instrument is not needed by any previously
	 * registered market takers.
	 */
	private final MarketRegListener instrumentSubscriptionListener = 
			new MarketRegListener() {

		@Override
		public void onRegistrationChange(final Map<MarketInstrument, Set<MarketEvent>> 
				instMap) {

			final Set<Subscription> subs = new HashSet<Subscription>();
			final Set<Subscription> unsubs = new HashSet<Subscription>();
			
			for(final Entry<MarketInstrument, Set<MarketEvent>> e: instMap.entrySet()) {
				
				/*
				 * The market maker denotes 'unsubscribe' with an empty event set
				 */
				if (e.getValue().isEmpty()) {
					log.debug("Unsubscribing to "
							+ e.getKey().get(InstrumentField.ID));
					unsubs.add(new Subscription(e.getKey(), e.getValue()));
				} else {
					log.debug("Subscribing to "
							+ e.getKey().get(InstrumentField.ID) + " Events: "
							+ printEvents(e.getValue()));
					subs.add(new Subscription(e.getKey(), e.getValue()));
				}
			
			}
			
			if(!unsubs.isEmpty()) {
				feed.unsubscribe(unsubs);
			}
			feed.subscribe(subs);
			
		}

	};

	private String printEvents(final Set<MarketEvent> events) {
		final StringBuffer sb = new StringBuffer();

		for (final MarketEvent me : events) {
			sb.append(me.name() + ", ");
		}

		return sb.toString();
	}

}
