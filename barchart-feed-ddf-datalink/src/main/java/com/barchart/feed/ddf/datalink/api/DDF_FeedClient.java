/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.api;

import com.barchart.util.anno.UsedOnce;

/**
 * Client responsible for lowest level connectivity to the data server.
 * 
 * Permits blocking and nonblocking commands.
 * 
 * Implementation should handle all communications from server asynchronously.
 * 
 * User is required to bind a FeedHandler to the client, specifying feed event
 * behavior and data processing behavior.
 */
public interface DDF_FeedClient {

	/**
	 * Initiate login; blocking call.
	 * 
	 * @return false initial client setup failed
	 */
	boolean login(String username, String password);

	/**
	 * Initiate logout; non blocking call.
	 */
	void logout();

	/**
	 * Send a DDF TCP command to data server; blocking call.
	 */
	boolean send(CharSequence command);

	/**
	 * Post a DDF TCP command to data server; non blocking call.
	 */
	boolean post(CharSequence command);

	/**
	 * Attach single feed handler to the client.
	 */
	@UsedOnce
	void bind(DDF_FeedHandler handler);

}
