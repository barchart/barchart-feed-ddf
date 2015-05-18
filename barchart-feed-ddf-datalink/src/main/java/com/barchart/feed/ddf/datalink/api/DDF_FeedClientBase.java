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
package com.barchart.feed.ddf.datalink.api;

import com.barchart.feed.api.connection.Connection;
import com.barchart.util.common.anno.UsedOnce;

/**
 * Base interface for DDF feed clients. Classes directly implementing this
 * interface should be connectionless listeners. Feeds requiring 2 way
 * communication and a managed connection should implement DDF_FeedClient.
 */
public interface DDF_FeedClientBase {

	/**
	 * Binds the feed client to a port or other data source and begins
	 * listening.
	 */
	void startup();
	
	void startUpProxy();

	/**
	 * Stops listening to the data source.
	 */
	void shutdown();

	/**
	 * Attach a feed state listener to the client.
	 */
	void bindStateListener(Connection.Monitor stateListener);
	
	/**
	 * Attaches a message listener to the client for data consumption.
	 */
	@UsedOnce
	void bindMessageListener(DDF_MessageListener msgListener);
	
}
