/**
 * 
 */
package com.barchart.feed.ddf.datalink.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.api.DDF_FeedStateListener;
import com.barchart.feed.ddf.datalink.enums.DDF_FeedState;

/**
 * @author g-litchfield
 * 
 */
public class ExampleFeedStateListener implements DDF_FeedStateListener {

	private static final Logger log = LoggerFactory
			.getLogger(ExampleFeedStateListener.class);

	@Override
	public void stateUpdate(final DDF_FeedState state) {

		log.debug("Feed state update: " + state.toString());

	}

}
