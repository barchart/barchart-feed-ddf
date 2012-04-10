/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.api.DDF_FeedHandler;
import com.barchart.feed.ddf.datalink.enums.DDF_FeedEvent;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class LoggingHandler.
 */
public class LoggingHandler implements DDF_FeedHandler {

	private static final Logger log = LoggerFactory
			.getLogger(LoggingHandler.class);

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.datalink.api.DDF_FeedHandler#handleEvent(com.barchart.feed.ddf.datalink.enums.DDF_FeedEvent)
	 */
	@Override
	public void handleEvent(final DDF_FeedEvent event) {

		log.debug("event : {}", event);

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.datalink.api.DDF_FeedHandler#handleMessage(com.barchart.feed.ddf.message.api.DDF_BaseMessage)
	 */
	@Override
	public void handleMessage(final DDF_BaseMessage message) {

		log.debug("message : {}", message);

	}

}
