package com.barchart.feed.ddf.instrument.provider;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.barchart.feed.api.inst.InstrumentGUID;
import com.barchart.feed.api.inst.SymbologyContext;

public class LocalCacheSymbologyContextDDF implements SymbologyContext<CharSequence> {

	private final ConcurrentMap<CharSequence, InstrumentGUID> symbolMap = 
			new ConcurrentHashMap<CharSequence, InstrumentGUID>();
	
	public void storeGUID(final CharSequence symbol, final InstrumentGUID guid) {
		symbolMap.put(symbol, guid);
	}
	
	@Override
	public InstrumentGUID lookup(final CharSequence symbol) {
		
		if(symbol == null || symbol.length() == 0) {
			return InstrumentGUID.NULL_INSTRUMENT_GUID;
		}
		
		final InstrumentGUID guid = symbolMap.get(symbol);
		
		if(guid == null) {
			return InstrumentGUID.NULL_INSTRUMENT_GUID;
		} else {
			return guid;
		}
		
	}

	@Override
	public Map<CharSequence, InstrumentGUID> lookup(
			Collection<? extends CharSequence> symbols) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<InstrumentGUID> search(CharSequence symbol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<InstrumentGUID> search(CharSequence symbol, int limit,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

}
