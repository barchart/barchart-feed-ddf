package com.barchart.feed.ddf.instrument.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.barchart.feed.api.data.Instrument;
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
		
		Map<CharSequence, Instrument> insts = DDF_InstrumentProvider.find(symbols);  
		
		LocalInstDefDB db = new LocalInstDefDB(DBLocation);
		
//		for(final Entry<CharSequence, Instrument> e : insts.entrySet()) {
//			db.store(e.getValue().GUID(), e.getValue());
//		}
//		
//		for(final Entry<CharSequence, Instrument> e : insts.entrySet()) {
//			System.out.println(db.lookup(e.getValue().GUID()).toString());
//		}
		
	}
	
}
