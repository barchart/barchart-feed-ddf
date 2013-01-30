package com.barchart.feed.ddf.instrument.provider;

import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField;
import com.barchart.feed.inst.api.InstrumentField;

public class TestDDFExtrasRemoteLookup {

	public static void main(final String[] args) {
		
		DDF_Instrument inst = DDF_InstrumentProvider.findDDF("ESH3");
		
		System.out.println(inst.toString());
		System.out.println();
		System.out.println("ID : " + inst.get(InstrumentField.ID));
		System.out.println("SYMBOL : " + inst.get(InstrumentField.SYMBOL));
		System.out.println("SYMBOL_UNIVERSAL : " + inst.get(DDF_InstrumentField.DDF_SYMBOL_UNIVERSAL));
		System.out.println("SYMBOL_REALTIME : " + inst.get(DDF_InstrumentField.DDF_SYMBOL_REALTIME)); 
		System.out.println("SYMBOL_HISTORICAL : " + inst.get(DDF_InstrumentField.DDF_SYMBOL_HISTORICAL));
		
	}
	
}
