/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import com.barchart.feed.ddf.historical.api.DDF_EntryBarMin;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;

class EntryMins extends EntryBar implements DDF_EntryBarMin {

	public EntryMins(final DDF_Instrument instrument) {
		super(instrument);
	}

}
