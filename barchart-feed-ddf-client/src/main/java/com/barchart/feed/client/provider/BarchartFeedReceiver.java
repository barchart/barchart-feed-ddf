/**
 * 
 * The core DDF class which encapsulates all the core functionality a new user
 * will need to get started.
 * <p>
 * Instances are created using the public constructor. An optional parameter
 * can provide an executor.
 * <p>
 * The price feed is started and stopped using the startup() and shutdown()
 * methods. Note that these are non-blocking calls. Applications requiring
 * actions upon successful login should instantiate and bind a
 * FeedStatusListener to the client.  Note that UDP listeners (the default)
 * will not fire any feed state events, they will just begin receiving data.
 * <p>
 * 
 */
package com.barchart.feed.client.provider;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;

/**
 * The entry point for Barchart data feed services.
 */
public class BarchartFeedReceiver extends BarchartFeedClientBase {

	private static final Logger log = LoggerFactory
			.getLogger(BarchartFeedReceiver.class);

	private Executor executor = null;

	public BarchartFeedReceiver() {

		this(new Executor() {

			private final AtomicLong counter = new AtomicLong(0);

			final String name = "# DDF Receiver - " + counter.getAndIncrement();

			@Override
			public void execute(final Runnable task) {
				new Thread(task, name).start();
			}

		});

	}

	public BarchartFeedReceiver(final Executor ex) {
		executor = ex;
	}

	/**
	 * Starts a stateless connection to the specified port. If the user is
	 * already logged in, this call will end the previous connection and reset
	 * all registered market takers.
	 * 
	 * @param socketAddress
	 */
	public void listen(final int socketAddress) {

		startListener(socketAddress);

	}

	/**
	 * Starts a stateless connection to the specified port. If the user is
	 * already logged in, this call will end the previous connection and reset
	 * all registered market takers.
	 * 
	 * @param socketAddress
	 */
	private void startListener(final int socketAddress) {

		setClient(DDF_FeedClientFactory.newStatelessListenerClient(
				socketAddress, executor));

	}

}
