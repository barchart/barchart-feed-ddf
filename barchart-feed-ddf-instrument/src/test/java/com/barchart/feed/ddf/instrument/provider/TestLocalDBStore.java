package com.barchart.feed.ddf.instrument.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.barchart.feed.api.data.InstrumentEntity;
import com.barchart.feed.inst.provider.LocalInstDefDB;

public class TestLocalDBStore {

	private static final String DBLocation = "src/test/resources/bdb";
	
	public static void main(final String[] args) {
		
		List<String> symbols = new ArrayList<String>();
		symbols.add("ESH3");
		symbols.add("GOOG");
		symbols.add("IBM");
		symbols.add("CLM3");
		symbols.add("GEM3");
		
		Map<CharSequence, InstrumentEntity> insts = DDF_InstrumentProvider.find(symbols);  
		
		LocalInstDefDB db = new LocalInstDefDB(DBLocation);
		
		for(final Entry<CharSequence, InstrumentEntity> e : insts.entrySet()) {
			db.store(e.getValue().getGUID(), e.getValue());
		}
		
		for(final Entry<CharSequence, InstrumentEntity> e : insts.entrySet()) {
			System.out.println(db.lookup(e.getValue().getGUID()).toString());
		}
		
	}
	
}
