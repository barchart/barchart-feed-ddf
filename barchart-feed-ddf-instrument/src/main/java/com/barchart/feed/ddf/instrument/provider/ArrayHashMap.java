package com.barchart.feed.ddf.instrument.provider;

import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.inst.InstrumentField;

public class ArrayHashMap {
	
	private static final int POW = 23;
	private static final int MAX_COLLISIONS = 10;
	private static final int SIZE = (int) Math.pow(2, POW);
	
	private final Instrument[][] vals = new Instrument[SIZE][];
	
	public void put(final String symbol, final Instrument inst) {
		
		int index = symbol.hashCode() & (SIZE-1);
		
		if(vals[index] == null) {
			vals[index] = new Instrument[MAX_COLLISIONS];
			vals[index][0] = inst;
			return;
		} else {
			for(int i = 0; i < MAX_COLLISIONS; i++) {
				if(vals[index][i] == null) {
					vals[index][i] = inst;
					return;
				}
			}
		}
		
		throw new IndexOutOfBoundsException("Max collisions exceeded for " + symbol);
		
	}
	
	public Instrument get(final String symbol) {
		
		int index = symbol.hashCode() & (SIZE-1);
		
		if(vals[index] == null) {
			return Instrument.NULL_INSTRUMENT;
		}
		
		for(int i = 0; i < MAX_COLLISIONS; i++) {
			if(symbol.equals(vals[index][i].get(InstrumentField.SYMBOL))) {
				return vals[index][i];
			}
		}
		
		System.out.println("Symbol not found " + symbol);
		
		return Instrument.NULL_INSTRUMENT;
		
	}

}
