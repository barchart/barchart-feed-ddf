/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.api;

import com.barchart.feed.base.api.instrument.values.MarketInstrument;
import com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField;
import com.barchart.util.anno.NotMutable;
import com.barchart.util.values.api.Value;

/**
 * The Interface DDF_Instrument.
 */
@NotMutable
public interface DDF_Instrument extends MarketInstrument {

	/**
	 * ddf specific fields
	 * 
	 * @param field
	 * @return
	 */
	<V extends Value<V>> V get(DDF_InstrumentField<V> field);

	/** space separated keywords used for full text search */
	String fullText();

}
