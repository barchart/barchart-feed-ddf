/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.api.FeedClient;
import com.barchart.feed.ddf.datalink.api.FeedClient.DDF_Transport;

/**
 * Factory class for building FeedClientDDF.
 */
public class DDF_FeedClientFactory {

	private static final Logger log = LoggerFactory
			.getLogger(DDF_FeedClientFactory.class);

	private DDF_FeedClientFactory() {
		//
	}

	/**
	 * Returns a stateful two way connection to a data source.
	 * 
	 * @param executor
	 *            The executor used by the ClientSocketChannel as both the boss
	 *            and worker executor. See
	 *            org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory
	 * @return the DDF_FeedClient
	 */
	public static FeedClient newConnectionClient(final DDF_Transport protocol,
			final String username, final String password,
			final Executor executor) {

		log.debug("Built new DDF_FeedClient,using to DDF_ServerType.STREAM");

		return new FeedClientDDF(username, password, executor);

	}
	
	public static FeedClient newConnectionClient(final DDF_Transport protocol,
			final String username, final String password,
			final Executor executor, final boolean isMobile) {

		log.debug("Built new DDF_FeedClient,using to DDF_ServerType.STREAM");

		return new FeedClientDDF(username, password, executor, null, isMobile);

	}
	
	
	/**
	 * Returns a stateful two way connection to a data source.
	 * 
	 * @param executor
	 *            The executor used by the ClientSocketChannel as both the boss
	 *            and worker executor. See
	 *            org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory
	 * @return the DDF_FeedClient
	 */
	public static FeedClient newConnectionClient(final DDF_Transport protocol,
			final String username, final String password,
			final Executor executor, final DDF_SocksProxy proxySettings) {

		log.debug("Built new DDF_FeedClient with Socks5 connection,using to DDF_ServerType.STREAM");

		return new FeedClientDDF(username, password, executor, proxySettings);

	}

	/**
	 * Returns a stateless UDP listener client with a user specified executor
	 * 
	 * @param ports
	 *            The port to listen to
	 * @param executor
	 *            The executor used by the NioDatagramChannel
	 * @return
	 */
	public static FeedClient newUDPListenerClient(final List<Integer> ports, 
			final boolean filterBySub, final Executor executor) {

		return new UDPListenerClientDDF(ports, filterBySub, executor);

	}
	
	/**
	 * Returns a stateless TCP listener client with a user specified executor
	 * 
	 * @param ports
	 *            The port to listen to
	 * @param executor
	 *            The executor used by the NioDatagramChannel
	 * @return
	 */
	public static FeedClient newStatelessTCPListenerClient(final List<Integer> ports, 
			final boolean filterBySub, final Executor executor) {
		
		return new TCPListenerClientDDF(ports, filterBySub, executor);
		
	}

}
