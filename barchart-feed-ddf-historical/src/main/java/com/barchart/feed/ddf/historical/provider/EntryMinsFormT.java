/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.ddf.historical.api.DDF_EntryBarMinFormT;

class EntryMinsFormT extends EntryBar implements DDF_EntryBarMinFormT {

	/**
	 * Instantiates a new entry mins form t.
	 *
	 * @param instrument the instrument
	 */
	public EntryMinsFormT(final Instrument instrument) {
		super(instrument);
	}

}
