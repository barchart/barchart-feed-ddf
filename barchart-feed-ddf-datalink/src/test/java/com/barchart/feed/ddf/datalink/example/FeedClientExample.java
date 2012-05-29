/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.datalink.api.DDF_FeedStateListener;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;
import com.barchart.feed.ddf.util.FeedDDF;

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

		final DDF_FeedClient client =
				DDF_FeedClientFactory.newInstance(username, password);

		final DDF_MessageListener handler = new LoggingHandler();

		client.bindMessageListener(handler);

		final DDF_FeedStateListener stateListener =
				new ExampleFeedStateListener();

		client.bindStateListener(stateListener);

		client.startup();

		// TODO Add feed handler
		// if (isLogin == DDF_FeedEvent.LOGIN_SUCCESS) {
		// log.error("invalid login");
		// return;
		// }

		Thread.sleep(5000);

		final CharSequence request = "" + //
				"GOOG=bBsScCvVqQ," + //
				"CLM2=bBsScCvVqQ," + //
				"NGM2=bBsScCvVqQ," + //
				"ESM2=bBsScCvVqQ," + //
				"XFM2=bBsScCvVqQ," + //
				"KCM2=bBsScCvVqQ," + //
				"";

		final boolean isSent = client.send(FeedDDF.tcpGo(request));
		if (!isSent) {
			log.error("invalid session");
			return;
		}

		Thread.sleep(1000 * 1000);

		client.shutdown();

	}

}
