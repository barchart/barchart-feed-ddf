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
import com.barchart.feed.ddf.datalink.api.Subscription;
import com.barchart.feed.ddf.datalink.enums.TP;
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
	 * Constructs a new feed client with the user's user name and password. The
	 * transport protocol defaults to TCP and a default executor are used.
	 * 
	 * @param username
	 * @param password
	 */
	public void login(final String username, final String password) {

		loginMain(username, password, TP.TCP, executor);

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
			final TP tp, final Executor executor) {

		loginMain(username, password, tp, executor);

	}

	/*
	 * Handles login. Non-blocking.
	 */
	private void loginMain(final String username, final String password,
			final TP tp, final Executor executor) {

		maker.clearAll();

		feed =
				DDF_FeedClientFactory.newConnectionClient(tp, username,
						password, executor);

		setClient(feed);

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
				public void onRegistrationChange(
						final MarketInstrument instrument,
						final Set<MarketEvent> events) {

					/*
					 * The market maker denotes 'unsubscribe' with an empty
					 * event set
					 */
					if (events.isEmpty()) {
						log.debug("Unsubscribing to "
								+ instrument.get(InstrumentField.ID));
						feed.unsubscribe(new Subscription(instrument, events));
					} else {
						log.debug("Subscribing to "
								+ instrument.get(InstrumentField.ID)
								+ " Events: " + printEvents(events));
						feed.subscribe(new Subscription(instrument, events));
					}

				}

			};

	private String printEvents(final Set<MarketEvent> events) {
		final StringBuffer sb = new StringBuffer();

		for (final MarketEvent me : events) {
			sb.append(me.name() + ", ");
		}

		return sb.toString();
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
	// public void setFeedEventPolicy(final DDF_FeedEvent event,
	// final EventPolicy policy) {
	// if (feed == null) {
	// throw new UnsupportedOperationException(
	// "Cannot set feed event policy before a sucessful login");
	// }
	// feed.setPolicy(event, policy);
	// }

}
