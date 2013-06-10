package com.barchart.feed.client.provider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketCallback;
import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.api.data.MarketData;
import com.barchart.feed.api.enums.MarketEventType;
import com.barchart.feed.client.api.FeedStateListener;
import com.barchart.feed.client.api.TimestampListener;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClientBase;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.datalink.enums.DDF_Transport;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.feed.ddf.market.provider.DDF_Marketplace;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_ControlTimestamp;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;

public class BarchartFeed {
	
	private static final Logger log = LoggerFactory
			.getLogger(BarchartFeed.class);
	
	protected volatile DDF_FeedClientBase feed = null;
	
	protected volatile DDF_Marketplace maker;
	
	private Executor executor = null;
	
	private FeedStateListener stateListener;
	
	private final CopyOnWriteArrayList<TimestampListener> timeStampListeners =
			new CopyOnWriteArrayList<TimestampListener>();
	
	public BarchartFeed() {
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
	
	public BarchartFeed(final Executor ex) {
		executor = ex;
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
	
	/*
	 * Handles login. Non-blocking.
	 */
	private void loginMain(final String username, final String password,
			final DDF_Transport tp, final Executor executor) {

		if(maker != null) {
			maker.clearAll();
		}

		// Returns the FeedClientDDF
		feed = DDF_FeedClientFactory.newConnectionClient(tp, username,
				password, executor);

		feed.bindMessageListener(msgListener);
		
		if (stateListener != null) {
			feed.bindStateListener(stateListener);
		}
		
		feed.startup();
		
		maker = DDF_Marketplace.newInstance(feed);

	}
	
	/**
	 * Shuts down the data feed and clears all registered market takers.
	 */
	public void shutdown() {

		if(maker != null) {
			maker.clearAll();
		}

		if (feed != null) {
			feed.shutdown();
			feed = null;
		}

	}
	
	/*
	 * This is the default message listener. Users wishing to handle raw
	 * messages will need to implement their own feed client.
	 */
	private final DDF_MessageListener msgListener = new DDF_MessageListener() {

		@Override
		public void handleMessage(final DDF_BaseMessage message) {

			if (message instanceof DDF_ControlTimestamp) {
				for (final TimestampListener listener : timeStampListeners) {
					listener.handleTimestamp(((DDF_ControlTimestamp) message)
							.getStampUTC());
				}
			}

			if (message instanceof DDF_MarketBase) {
				final DDF_MarketBase marketMessage = (DDF_MarketBase) message;
				maker.make(marketMessage);
			}

		}

	};
	
	public <V extends MarketData<V>> Agent newAgent(Class<V> dataType, 
			MarketCallback<V> callback,	MarketEventType... types) {
		
		// TODO review maker lifecycle
		
		return maker.newAgent(dataType, callback, types);
		
	}
	
	// TODO Helper methods
	
	// subscribe()
	
	/**
	 * Applications which need to react to the connectivity state of the feed
	 * instantiate a FeedStateListener and bind it to the client.
	 * 
	 * @param listener
	 *            The listener to be bound.
	 */
	public void bindFeedStateListener(final FeedStateListener listener) {

		stateListener = listener;

		if (feed != null) {
			feed.bindStateListener(listener);
		}

	}
	
	/**
	 * Applications which require time-stamp or heart-beat messages from the
	 * data server instantiate a DDF_TimestampListener and bind it to the
	 * client.
	 * 
	 * @param listener
	 */
	public void bindTimestampListener(final TimestampListener listener) {
		timeStampListeners.add(listener);
	}
	
	/**
	 * Retrieves the instrument object denoted by symbol. The local instrument
	 * cache will be checked first. If the instrument is not stored locally, a
	 * remote call to the instrument service is made.
	 * 
	 * @return NULL_INSTRUMENT if the symbol is not resolved.
	 */
	public Instrument lookup(final String symbol) {
		return DDF_InstrumentProvider.find(symbol);
	}

	/**
	 * Retrieves a list of instrument objects denoted by symbols provided. The
	 * local instrument cache will be checked first. If any instruments are not
	 * stored locally, a remote call to the instrument service is made.
	 * 
	 * @return An empty list if no symbols can be resolved.
	 */
	public Map<CharSequence, Instrument> lookup(final List<String> symbolList) {
		return DDF_InstrumentProvider.find(symbolList);
	}


}
