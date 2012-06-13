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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.instrument.enums.InstrumentField;
import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.MarketRegListener;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.client.api.FeedStateListener;
import com.barchart.feed.client.api.TimestampListener;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClientBase;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.datalink.api.EventPolicy;
import com.barchart.feed.ddf.datalink.api.Subscription;
import com.barchart.feed.ddf.datalink.enums.DDF_FeedEvent;
import com.barchart.feed.ddf.datalink.enums.TP;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.feed.ddf.market.api.DDF_MarketProvider;
import com.barchart.feed.ddf.market.provider.DDF_MarketService;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_ControlTimestamp;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.util.values.api.Value;

/**
 * The entry point for Barchart data feed services.
 */
public class BarchartFeedClient {

	private static final Logger log = LoggerFactory
			.getLogger(BarchartFeedClient.class);

	private volatile DDF_FeedClient feed = null;

	private volatile DDF_FeedClientBase listener = null;

	private final DDF_MarketProvider maker = DDF_MarketService.newInstance();

	private final CopyOnWriteArrayList<TimestampListener> timeStampListeners =
			new CopyOnWriteArrayList<TimestampListener>();

	private final Executor defaultExecutor = new Executor() {

		private final AtomicLong counter = new AtomicLong(0);

		final String name = "# DDF Client - " + counter.getAndIncrement();

		@Override
		public void execute(final Runnable task) {
			new Thread(task, name).start();
		}

	};

	private FeedStateListener stateListener;

	public BarchartFeedClient() {
		maker.add(instrumentSubscriptionListener);
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

		loginMain(username, password, TP.TCP, defaultExecutor);

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
	public void
			login(final String username, final String password, final TP tp) {

		loginMain(username, password, tp, defaultExecutor);

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
			final TP tp, final Executor executor) {

		loginMain(username, password, tp, executor);

	}

	/*
	 * Handles login. Non-blocking.
	 */
	private void loginMain(final String username, final String password,
			final TP tp, final Executor executor) {

		/* Enforce single connection */
		if (listener != null) {
			listener.shutdown();
			listener = null;
		}

		maker.clearAll();

		if (feed != null) {
			feed.shutdown();
		}

		feed =
				DDF_FeedClientFactory.newConnectionClient(tp, username,
						password, executor);

		feed.bindMessageListener(msgListener);

		if (stateListener != null) {
			feed.bindStateListener(stateListener);
		}

		feed.startup();

	}

	/**
	 * Shuts down the data feed and clears all registered market takers.
	 */
	public void shutdown() {
		maker.clearAll();

		if (feed != null) {
			feed.shutdown();
			feed = null;
		}

		if (listener != null) {
			listener.shutdown();
			listener = null;
		}
	}

	/**
	 * Starts a stateless connection to the specified port. If the user is
	 * already logged in, this call will end the previous connection and reset
	 * all registered market takers.
	 * 
	 * @param socketAddress
	 */
	// public void startListener(final int socketAddress) {
	//
	// if (feed != null) {
	// feed.shutdown();
	// }
	//
	// feed = null;
	//
	// listener =
	// DDF_FeedClientFactory.newStatelessListenerClient(socketAddress,
	// defaultExecutor);
	//
	// listener.bindMessageListener(msgListener);
	// }

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
				public void onRegistrationChange(
						final MarketInstrument instrument,
						final Set<MarketEvent> events) {

					/*
					 * The market maker denotes 'unsubscribe' with an empty
					 * event set
					 */
					if (events.isEmpty()) {
						log.debug("Unsubsctibing to "
								+ instrument.get(InstrumentField.ID));
						feed.unsubscribe(new Subscription(instrument, events));
					} else {
						log.debug("Subsctibing to "
								+ instrument.get(InstrumentField.ID));
						feed.subscribe(new Subscription(instrument, events));
					}

				}

			};

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
	 * Users wishing to modify the feed client's response to feed connectivity
	 * or other events can bind an instance of EventPolicy to a specific
	 * DDF_FeedEvent.
	 * <p>
	 * Note: There are two default behaviors set initially for convenience. On
	 * any disconnect or failed login, the feed client will wait two seconds and
	 * then attempt to reconnect. On successful login, all instruments which had
	 * been subscribed to by registered market takers will be resubscribed.
	 * 
	 * @param event
	 *            The event to specify a policy for.
	 * @param policy
	 *            A user defined action to be performed on a specific feed
	 *            event.
	 */
	public void setFeedEventPolicy(final DDF_FeedEvent event,
			final EventPolicy policy) {
		if (feed == null) {
			throw new UnsupportedOperationException(
					"Cannot set feed event policy before a sucessful login");
		}
		feed.setPolicy(event, policy);
	}

	//

	/**
	 * Adds a market taker to the client. This performs instrument registration
	 * with the market maker as well as subscribing to the required data from
	 * the feed.
	 * 
	 * @param taker
	 *            The market taker to be added.
	 * @return True if the taker was successfully added.
	 */
	public boolean isTakerRegistered(final MarketTaker<?> taker) {
		return maker.isRegistered(taker);
	}

	/**
	 * Adds a market taker to the client. This performs instrument registration
	 * with the market maker as well as subscribing to the required data from
	 * the feed.
	 * 
	 * @param taker
	 *            The market taker to be added.
	 * @return True if the taker was successfully added.
	 */
	public <V extends Value<V>> boolean addTaker(final MarketTaker<V> taker) {
		return maker.register(taker);
	}

	/**
	 * Removes a market taker from the client. If no other takers require its
	 * instruments, they are unsubscribed from the feed.
	 * 
	 * @param taker
	 *            THe market taker to be removed.
	 * @return True if the taker was successfully removed.
	 */
	public <V extends Value<V>> boolean removeTaker(final MarketTaker<V> taker) {
		return maker.unregister(taker);
	}

	/**
	 * Retrieves the instrument object denoted by symbol. The local instrument
	 * cache will be checked first. If the instrument is not stored locally, a
	 * remote call to the instrument service is made.
	 * 
	 * @return NULL_INSTRUMENT if the symbol is not resolved.
	 */
	public MarketInstrument lookup(final String symbol) {
		return DDF_InstrumentProvider.find(symbol);
	}

	/**
	 * Retrieves a list of instrument objects denoted by symbols provided. The
	 * local instrument cache will be checked first. If any instruments are not
	 * stored locally, a remote call to the instrument service is made.
	 * 
	 * @return An empty list if no symbols can be resolved.
	 */
	public List<MarketInstrument> lookup(final List<String> symbolList) {
		final List<DDF_Instrument> list =
				DDF_InstrumentProvider.find(symbolList);

		return new ArrayList<MarketInstrument>(list);
	}

	/**
	 * Makes a query to the market maker for a snapshot of a market field for a
	 * specific instrument. The returned values are frozen and disconnected from
	 * live market.
	 * 
	 * @return NULL_VALUE for all fields if market is not present.
	 */
	public <S extends MarketInstrument, V extends Value<V>> V take(
			final S instrument, final MarketField<V> field) {
		return maker.take(instrument, field);
	}

}
