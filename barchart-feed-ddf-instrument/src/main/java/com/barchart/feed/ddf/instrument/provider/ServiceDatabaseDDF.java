package com.barchart.feed.ddf.instrument.provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

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
	public Instrument lookup(final CharSequence symbol) {
		
		return lookupBase(symbol);
		
	}

	@Override
	public Future<Instrument> lookupAsync(final CharSequence symbol) {
		return new FutureTask<Instrument>(new SymbolLookup(symbol));
	}

	@Override
	public Map<CharSequence, Instrument> lookup(
			final Collection<? extends CharSequence> symbols) {
		
		final Map<CharSequence, Instrument> result = 
				new HashMap<CharSequence, Instrument>();
		
		// Currently just doing serial lookup
		for(final CharSequence symbol : symbols) {
			result.put(symbol, lookupBase(symbol));
		}
		
		return result;
	}

	@Override
	public Map<CharSequence, Future<Instrument>> lookupAsync(
			final Collection<? extends CharSequence> symbols) {
		
		final Map<CharSequence, Future<Instrument>> result =
				new HashMap<CharSequence, Future<Instrument>>();
		
		for(final CharSequence symbol : symbols) {
			result.put(symbol, new FutureTask<Instrument>(new SymbolLookup(symbol)));
		}
				
		return result;
	}
	
	private Instrument lookupBase(final CharSequence symbol) {
		
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
	
	private class SymbolLookup implements Callable<Instrument> {

		private final CharSequence symbol;
		
		public SymbolLookup(final CharSequence symbol) {
			this.symbol = symbol;
		}
		
		@Override
		public Instrument call() throws Exception {
			
			return lookupBase(symbol);
			
		}
		
	}

}
