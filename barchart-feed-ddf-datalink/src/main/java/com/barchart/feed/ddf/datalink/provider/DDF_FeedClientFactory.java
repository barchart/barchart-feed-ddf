/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.settings.enums.DDF_ServerType;

/**
 * Factory class for building FeedClientDDF.
 * 
 * @author g-litchfield
 */
public class DDF_FeedClientFactory {

	private static final Logger log = LoggerFactory
			.getLogger(DDF_FeedClientFactory.class);

	private DDF_FeedClientFactory() {
		//
	}

	/**
	 * Default factory method for DDF_FeedClient.
	 * <p>
	 * Defaults server type to STREAM.
	 * <p>
	 * Defaults channel executor to "create new thread and run."
	 * 
	 * @return The DDF_FeedClient
	 */
	public static DDF_FeedClient newInstance() {

		return new FeedClientDDF(DDF_ServerType.STREAM, new Executor() {
			@Override
			public void execute(final Runnable task) {
				new Thread(task).start();
			}
		});
	}

	/**
	 * Factory which defaults the DDF_ServerType to STREAM.
	 * 
	 * @param runner
	 *            The executor used by the ClientSocketChannel as both the boss
	 *            and worker executor. See
	 *            org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory
	 * @return the DDF_FeedClient
	 */
	public static DDF_FeedClient newInstance(final Executor runner) {

		log.debug("Built new DDF_FeedClient, default to DDF_ServerType.STREAM");

		return new FeedClientDDF(DDF_ServerType.STREAM, runner);

	}

	/**
	 * Factory which allows user specified DDF_ServerType.
	 * 
	 * @param serverType
	 *            The server type
	 * @param runner
	 *            The executor used by the ClientSocketChannel as both the boss
	 *            and worker executor. See
	 *            org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory
	 * @return the DDF_FeedClient
	 */
	public static DDF_FeedClient newInstance(final DDF_ServerType serverType,
			final Executor runner) {

		log.debug("Built new DDF_FeedClient, DDF_ServerType.{}",
				serverType.name());

		return new FeedClientDDF(serverType, runner);

	}

}
