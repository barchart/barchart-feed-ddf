/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import com.barchart.feed.ddf.settings.api.DDF_Server;
import com.barchart.feed.ddf.settings.api.DDF_Settings;
import com.barchart.feed.ddf.settings.enums.DDF_ServerType;

final class ConstHistorical {

	private ConstHistorical() {
	}

	static final DDF_ServerType SERVER_TYPE = DDF_ServerType.HISTORICAL_V2;

	static final String historicalServer(final DDF_Settings settings) {

		final DDF_Server server = settings.getServer(SERVER_TYPE);

		return server.getPrimaryOrSecondary();

	}

	static final String STATUS_EMPTY = "result set is empty ";

	static final String STATUS_COUNT = "result set count is ";

}
