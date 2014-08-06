/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.api;

import com.barchart.feed.ddf.datalink.enums.DDF_FeedEvent;

/**
 * Client responsible for lowest level connectivity to the data server.
 * <p>
 * Implementation should handle all communications from server asynchronously.
 * Permits only nonblocking commands.
 * <p>
 * User is required to bind a StateListener to the client, creating a callback
 * for login state changes for the client.
 * <p>
 */
public interface DDF_FeedClient extends DDF_FeedClientBase {


	/**
	 * Sets the event policy for a specific feed event. Default policies are set
	 * by the constructor to handle disconnects and login success.
	 * 
	 * @param event
	 *            The feed event on which to enact the policy.
	 * @param policy
	 *            The even policy to register.
	 */
	void setPolicy(DDF_FeedEvent event, EventPolicy policy);
	
}
