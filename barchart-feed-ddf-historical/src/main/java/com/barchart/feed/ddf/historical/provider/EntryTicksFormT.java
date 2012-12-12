/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import com.barchart.feed.ddf.historical.api.DDF_EntryTickFormT;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;


public class EntryTicksFormT extends EntryTicksDetail implements DDF_EntryTickFormT {

	public EntryTicksFormT(DDF_Instrument instrument) {
		super(instrument);
	}
	
}
