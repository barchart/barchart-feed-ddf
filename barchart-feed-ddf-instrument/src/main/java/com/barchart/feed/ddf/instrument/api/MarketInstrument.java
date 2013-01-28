package com.barchart.feed.ddf.instrument.api;

import com.barchart.feed.ddf.instrument.enums.InstrumentField;
import com.barchart.feed.inst.api.Instrument;
import com.barchart.util.values.api.Value;

/**
 * 
 * Legacy interface, used only for backwards compatibility with DDF
 *
 */
public interface MarketInstrument extends Instrument {
	
	<V extends Value<V>> V get(InstrumentField<V> field);


	/** since used as map key */
	@Override
	boolean equals(Object thatInst);

	/** since used as map key */
	@Override
	int hashCode();

}
