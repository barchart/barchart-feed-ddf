/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import com.barchart.feed.api.inst.Instrument;
import com.barchart.feed.ddf.historical.api.DDF_EntryBarMin;

class EntryMins extends EntryBar implements DDF_EntryBarMin {

	/**
	 * Instantiates a new entry mins.
	 *
	 * @param instrument the instrument
	 */
	public EntryMins(final Instrument instrument) {
		super(instrument);
	}

}
