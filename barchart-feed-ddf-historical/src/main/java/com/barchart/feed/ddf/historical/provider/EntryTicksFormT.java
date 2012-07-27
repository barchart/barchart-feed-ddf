package com.barchart.feed.ddf.historical.provider;

import com.barchart.feed.ddf.historical.api.DDF_EntryTickFormT;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;


public class EntryTicksFormT extends EntryTicksDetail implements DDF_EntryTickFormT {

	public EntryTicksFormT(DDF_Instrument instrument) {
		super(instrument);
	}
	
}
