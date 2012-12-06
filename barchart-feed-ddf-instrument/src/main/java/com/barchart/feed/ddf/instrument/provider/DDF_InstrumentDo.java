/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import com.barchart.feed.base.provider.MarketDoInstrument;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField;
import com.barchart.util.anno.Mutable;
import com.barchart.util.values.api.Value;

/**
 * The Interface DDF_InstrumentDo.
 */
@Mutable
public interface DDF_InstrumentDo extends DDF_Instrument, MarketDoInstrument {

	<V extends Value<V>> V set(DDF_InstrumentField<V> field, V value);

}
