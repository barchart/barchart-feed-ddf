/**
 * 
 */
package com.barchart.feed.ddf.datalink.api;

import com.barchart.feed.client.api.FeedStateListener;
import com.barchart.util.anno.UsedOnce;

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

	/**
	 * Stops listening to the data source.
	 */
	void shutdown();

	/**
	 * Attaches a message listener to the client for data consumption.
	 */
	@UsedOnce
	void bindMessageListener(DDF_MessageListener msgListener);

	/**
	 * Attach single feed state listener to the client.
	 */
	@UsedOnce
	void bindStateListener(FeedStateListener stateListener);

}
