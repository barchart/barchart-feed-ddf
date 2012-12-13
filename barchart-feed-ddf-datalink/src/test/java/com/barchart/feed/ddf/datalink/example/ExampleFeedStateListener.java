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
package com.barchart.feed.ddf.datalink.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.client.api.FeedStateListener;
import com.barchart.feed.client.enums.FeedState;

/**
 * @author g-litchfield
 * 
 */
public class ExampleFeedStateListener implements FeedStateListener {

	private static final Logger log = LoggerFactory
			.getLogger(ExampleFeedStateListener.class);

	@Override
	public void stateUpdate(final FeedState state) {

		log.debug("Feed state update: " + state.toString());

	}

}
