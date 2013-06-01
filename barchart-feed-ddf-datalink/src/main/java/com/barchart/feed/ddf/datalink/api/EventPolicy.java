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

import com.barchart.feed.ddf.datalink.enums.DDF_FeedEvent;

/**
 * A callback action to be fired on a specific event. Registered with a feed
 * client along with an event type.
 * 
 */
public interface EventPolicy {

	public void newEvent(DDF_FeedEvent event);

}
