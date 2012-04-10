/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.api;

import com.barchart.feed.ddf.datalink.enums.DDF_FeedEvent;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;

/**
 * The Interface DDF_FeedHandler.
 */
public interface DDF_FeedHandler {

	/** called from pool thread */
	void handleEvent(DDF_FeedEvent event);

	/** called from pool thread */
	void handleMessage(DDF_BaseMessage message);

}
