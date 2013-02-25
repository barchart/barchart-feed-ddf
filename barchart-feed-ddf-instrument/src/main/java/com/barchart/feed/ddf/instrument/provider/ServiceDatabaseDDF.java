package com.barchart.feed.ddf.instrument.provider;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.inst.Instrument;
import com.barchart.feed.ddf.instrument.api.DDF_DefinitionService;
import com.barchart.feed.inst.provider.InstrumentFactory;
import com.barchart.proto.buf.inst.InstrumentDefinition;

public class ServiceDatabaseDDF implements DDF_DefinitionService {
	
	static final Logger log = LoggerFactory.getLogger(ServiceDatabaseDDF.class);
	
	private final ConcurrentMap<CharSequence, Instrument> cache = 
			new ConcurrentHashMap<CharSequence, Instrument>();
	
	private final LocalInstrumentDBMap db;
	
	public ServiceDatabaseDDF(final LocalInstrumentDBMap map) {
		this.db = map;
	}

	@Override
	public Instrument lookup(CharSequence symbol) {
		
		if(symbol == null || symbol.length() == 0) {
			return Instrument.NULL_INSTRUMENT;
		}
		
		Instrument instrument = cache.get(symbol);
		
		if(instrument != null) {
			return instrument;
		}
		
		final InstrumentDefinition instDef = db.get(symbol.toString());
		
		if(instDef == null) {
			cache.put(symbol, Instrument.NULL_INSTRUMENT);
			return Instrument.NULL_INSTRUMENT;
		} else {
			instrument = InstrumentFactory.buildFromProtoBuf(instDef);
			cache.put(symbol, instrument);
			return instrument;
		}
		
	}

	@Override
	public Future<Instrument> lookupAsync(CharSequence symbol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<CharSequence, Instrument> lookup(
			Collection<? extends CharSequence> symbols) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<CharSequence, Future<Instrument>> lookupAsync(
			Collection<? extends CharSequence> symbols) {
		// TODO Auto-generated method stub
		return null;
	}

}
