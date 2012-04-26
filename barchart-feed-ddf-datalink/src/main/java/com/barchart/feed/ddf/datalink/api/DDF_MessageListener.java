/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.api;

import com.barchart.feed.ddf.message.api.DDF_BaseMessage;

/**
 * Specifies the handling of feed events and data messages from the server.
 * <p>
 * All calls are handles asynchronously by the feed client which the feed
 * handler is bound to.
 * 
 */
public interface DDF_MessageListener {

	/**
	 * Asynchronous data receipt callback.
	 * 
	 * @param message
	 *            The data message produced by server.
	 */
	void handleMessage(DDF_BaseMessage message);

}
