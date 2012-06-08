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
import com.barchart.feed.ddf.datalink.api.DDF_FeedClientBase;
import com.barchart.feed.ddf.datalink.enums.TP;

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
	 * Factory which defaults the DDF_ServerType to STREAM.
	 * 
	 * @param executor
	 *            The executor used by the ClientSocketChannel as both the boss
	 *            and worker executor. See
	 *            org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory
	 * @return the DDF_FeedClient
	 */
	public static DDF_FeedClient newInstance(final TP protocol,
			final String username, final String password,
			final Executor executor) {

		log.debug("Built new DDF_FeedClient,using to DDF_ServerType.STREAM");

		return new FeedClientDDF(username, password, executor);

	}

	/**
	 * Returns a stateless UDP listener client with a user specified executor
	 * 
	 * @param port
	 *            The port to lisen to
	 * @param executor
	 *            The executor used by the NioDatagramChannel
	 * @return
	 */
	public static DDF_FeedClientBase newStatelessListenerClient(final int port,
			final Executor executor) {

		return new ListenerClientDDF(port, executor);

	}

}
