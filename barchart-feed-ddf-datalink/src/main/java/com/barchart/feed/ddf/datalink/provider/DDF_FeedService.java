/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.settings.enums.DDF_ServerType;

// TODO: Auto-generated Javadoc
/**
 * Factory class for building FeedClientDDF.
 *
 * @author g-litchfield
 */
public class DDF_FeedService {

	private static final Logger log = LoggerFactory
			.getLogger(DDF_FeedService.class);

	private DDF_FeedService() {
		//
	}

	/**
	 * Factory which defaults the DDF_ServerType to STREAM.
	 *
	 * @param runner the runner
	 * @return the dD f_ feed client
	 */
	public static DDF_FeedClient newInstance(final Executor runner) {

		log.debug("Built new DDF_FeedClient, default to DDF_ServerType.STREAM");

		return new FeedClientDDF(DDF_ServerType.STREAM, runner);

	}

	/**
	 * Factory which allows user specified DDF_ServerType.
	 *
	 * @param serverType the server type
	 * @param runner the runner
	 * @return the dD f_ feed client
	 */
	public static DDF_FeedClient newInstance(final DDF_ServerType serverType,
			final Executor runner) {

		log.debug("Built new DDF_FeedClient, DDF_ServerType.{}",
				serverType.name());

		return new FeedClientDDF(serverType, runner);

	}

}
