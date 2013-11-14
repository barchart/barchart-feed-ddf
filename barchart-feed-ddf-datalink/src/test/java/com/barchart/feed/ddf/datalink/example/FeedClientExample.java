/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.example;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.base.sub.SubscriptionType;
import com.barchart.feed.base.sub.Subscription.Type;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.datalink.enums.DDF_Transport;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;
import com.barchart.feed.ddf.datalink.provider.DDF_Subscription;

// TODO: Auto-generated Javadoc
/**
 * The Class FeedClientExample.
 */
public class FeedClientExample {

	private static final Logger log = LoggerFactory
			.getLogger(FeedClientExample.class);

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(final String[] args) throws Exception {

		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		final Executor runner = new Executor() {

			private final AtomicLong counter = new AtomicLong(0);

			final String name = "# DDF Feed Client - "
					+ counter.getAndIncrement();

			@Override
			public void execute(final Runnable task) {
				new Thread(task, name).start();
			}

		};

		final DDF_FeedClient client =
				DDF_FeedClientFactory.newConnectionClient(DDF_Transport.TCP, username,
						password, runner);

		final DDF_MessageListener handler = new LoggingHandler();

		client.bindMessageListener(handler);

		final Connection.Monitor stateListener = new ExampleFeedStateListener();

		client.bindStateListener(stateListener);

		client.startup();

		// TODO Add feed handler
		// if (isLogin == DDF_FeedEvent.LOGIN_SUCCESS) {
		// log.error("invalid login");
		// return;
		// }

		Thread.sleep(5000);

		final CharSequence request = "" + //
				"$OEX=BbSs" +
				"";

		Set<SubscriptionType> insts = new HashSet<SubscriptionType>();
		insts.add(SubscriptionType.BOOK_SNAPSHOT);
		insts.add(SubscriptionType.BOOK_UPDATE);
		insts.add(SubscriptionType.QUOTE_SNAPSHOT);
		insts.add(SubscriptionType.QUOTE_UPDATE);
		client.subscribe(new DDF_Subscription("$OEX", Type.INSTRUMENT, insts));
//		 if (!isSent) {
//		 log.error("invalid session");
//		 return;
//		 }
		
		 Thread.sleep(1000 * 1000);
		
		 client.shutdown();

	}

}
