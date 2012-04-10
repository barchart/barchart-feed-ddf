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
 * The Interface DDF_FeedClient.
 */
public interface DDF_FeedClient {

	/**
	 * initiate login; blocking call;
	 * 
	 * @return false initial client setup failed
	 */
	boolean login(String username, String password);

	/**
	 * initiate logout; non blocking call;
	 */
	void logout();

	/**
	 * send a DDF TCP command to JERQ; blocking call;
	 */
	boolean send(CharSequence command);

	/**
	 * post a DDF TCP command to JERQ; non blocking call
	 */
	boolean post(CharSequence command);

	/**
	 * attach single feed handler to the client
	 */
	@UsedOnce
	void bind(DDF_FeedHandler handler);

}
