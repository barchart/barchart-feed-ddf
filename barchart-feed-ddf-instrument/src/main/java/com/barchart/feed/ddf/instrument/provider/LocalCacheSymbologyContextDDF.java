package com.barchart.feed.ddf.instrument.provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.barchart.feed.api.util.InstrumentGUID;
import com.barchart.feed.inst.SymbologyContext;

public class LocalCacheSymbologyContextDDF implements SymbologyContext<CharSequence> {

	private final ConcurrentMap<CharSequence, InstrumentGUID> symbolMap = 
			new ConcurrentHashMap<CharSequence, InstrumentGUID>();
	
	public void storeGUID(final CharSequence symbol, final InstrumentGUID guid) {
		symbolMap.put(symbol, guid);
	}
	
	@Override
	public InstrumentGUID lookup(final CharSequence symbol) {
		
		if(symbol == null || symbol.length() == 0) {
			return InstrumentGUID.NULL;
		}
		
		final InstrumentGUID guid = symbolMap.get(symbol);
		
		if(guid == null) {
			return InstrumentGUID.NULL;
		} else {
			return guid;
		}
		
	}

	@Override
	public Map<CharSequence, InstrumentGUID> lookup(
			Collection<? extends CharSequence> symbols) {
		
		final Map<CharSequence, InstrumentGUID> result = 
				new HashMap<CharSequence, InstrumentGUID>();
		
		for(final CharSequence symbol : symbols) {
			result.put(symbol, lookup(symbol));
		}
		
		return result;
	}

	@Override
	public List<InstrumentGUID> search(CharSequence symbol) {
		throw new UnsupportedOperationException("Search not supported");
	}

	@Override
	public List<InstrumentGUID> search(CharSequence symbol, int limit,
			int offset) {
		throw new UnsupportedOperationException("Search not supported");
	}

}
