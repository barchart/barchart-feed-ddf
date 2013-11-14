/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/**
 * 
 */
package com.barchart.feed.ddf.datalink.provider;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.sub.Subscription;
import com.barchart.feed.base.sub.SubscriptionType;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.datalink.enums.DDF_Transport;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;

/**
 * @author g-litchfield
 * 
 */
public class TestLogins {

	private static final Logger log = LoggerFactory.getLogger(TestLogins.class);

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

		final DDF_MessageListener handler = new DDF_MessageListener() {

			@Override
			public void handleMessage(final DDF_BaseMessage message) {
				log.debug(message.toString());
			}
		};

		client.bindMessageListener(handler);

		// Initial login
		client.startup();

		final Set<SubscriptionType> interests = new HashSet<SubscriptionType>();
		interests.addAll(DDF_FeedInterest.setValues());
		final DDF_Subscription sub = new DDF_Subscription("GOOG", Subscription.Type.INSTRUMENT, interests);

		client.subscribe(sub);

		sleep(45000);

		log.debug("*****************************************  Unsubscribing");

		client.unsubscribe(sub);

		sleep(10000);

		log.debug("*****************************************  Resubscribing");

		client.subscribe(sub);

		sleep(45000);

		while(true){
			sleep(1000);
		}
		
		//client.shutdown();
	}

	private static void sleep(final int mills) {
		try {
			Thread.sleep(mills);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

}
