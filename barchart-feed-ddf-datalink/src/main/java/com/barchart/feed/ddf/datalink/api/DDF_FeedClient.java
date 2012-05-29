/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.api;

import com.barchart.feed.ddf.datalink.enums.DDF_FeedEvent;
import com.barchart.util.anno.UsedOnce;

/**
 * Client responsible for lowest level connectivity to the data server.
 * <p>
 * Implementation should handle all communications from server asynchronously.
 * Permits blocking and nonblocking commands.
 * <p>
 * User is required to bind a FeedHandler to the client, specifying feed event
 * behavior and data processing behavior.
 */
public interface DDF_FeedClient extends DDF_FeedClientBase {

	/**
	 * Attach single feed state listener to the client.
	 */
	@UsedOnce
	void bindStateListener(DDF_FeedStateListener stateListener);

	/**
	 * 
	 * @param event
	 * @param policy
	 */
	void setPolicy(DDF_FeedEvent event, EventPolicy policy);

	/**
	 * Send a DDF TCP command to data server; blocking call.
	 * 
	 * @return True if successful.
	 */
	boolean send(CharSequence command);

	/**
	 * Post a DDF TCP command to data server; non blocking call.
	 * 
	 * @return True if successful.
	 */
	boolean post(CharSequence command);

}
