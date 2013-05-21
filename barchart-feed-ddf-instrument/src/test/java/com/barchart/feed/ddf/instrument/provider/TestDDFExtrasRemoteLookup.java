package com.barchart.feed.ddf.instrument.provider;

import com.barchart.feed.api.data.InstrumentEntity;
import com.barchart.feed.api.fields.InstrumentField;

public class TestDDFExtrasRemoteLookup {

	public static void main(final String[] args) {
		
		InstrumentEntity inst = DDF_InstrumentProvider.find("ESH3");
		
		System.out.println(inst.toString());
		System.out.println();
		System.out.println("ID : " + inst.get(InstrumentField.MARKET_GUID));
		System.out.println("SYMBOL : " + inst.get(InstrumentField.SYMBOL));
		
	}
	
}
