/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.example;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.datalink.api.DDF_FeedHandler;
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

		final Executor runner = Executors.newCachedThreadPool();

		final DDF_FeedClient client = DDF_FeedClientFactory.newInstance(runner);

		final DDF_FeedHandler handler = new LoggingHandler();

		client.bind(handler);

		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		final boolean isLogin = client.login(username, password);

		if (!isLogin) {
			log.error("invalid login");
			return;
		}

		final CharSequence request = "" + //
				"ORCL=bBsScCvVqQ," + //
				"GOOG=bBsScCvVqQ," + //
				"IBM=bBsScCvVqQ," + //
				"ESH2=bBsScCvVqQ," + //
				"XFK2=bBsScCvVqQ," + //
				"KCH2=bBsScCvVqQ," + //
				"";

		final boolean isSent = client.send(FeedDDF.tcpGo(request));
		if (!isSent) {
			log.error("invalid session");
			return;
		}

		Thread.sleep(10 * 1000);

		client.logout();

	}

}
