/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import com.barchart.feed.ddf.historical.api.DDF_EntryBarMinNearby;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;

class EntryMinsNearby extends EntryBar implements DDF_EntryBarMinNearby {

	/**
	 * Instantiates a new entry mins nearby.
	 *
	 * @param instrument the instrument
	 */
	public EntryMinsNearby(final DDF_Instrument instrument) {
		super(instrument);
	}

}
