/**
 * 
 */
package com.barchart.feed.ddf.datalink.api;

import com.barchart.util.anno.UsedOnce;

/**
 * @author g-litchfield
 * 
 */
public interface DDF_FeedClientBase {

	/**
	 * Initiate login; non blocking call.
	 * <p>
	 * Success or failure description passed as DDF_FeedEvent and should be
	 * handled by a DDF_FeedHandler.
	 */
	void startup();

	/**
	 * Initiate logout; non blocking call.
	 */
	void shutdown();

	/**
	 * Attach single message listener to the client.
	 */
	@UsedOnce
	void bindMessageListener(DDF_MessageListener msgListener);

}
