package com.barchart.feed.ddf.instrument.provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.framework.data.InstrumentEntity;
import com.barchart.feed.ddf.instrument.api.DDF_DefinitionService;
import com.barchart.feed.inst.provider.InstrumentFactory;
import com.barchart.proto.buf.inst.InstrumentDefinition;
import com.barchart.util.values.provider.ValueBuilder;

public class ServiceDatabaseDDF implements DDF_DefinitionService {
	
	static final Logger log = LoggerFactory.getLogger(ServiceDatabaseDDF.class);
	
	private final ConcurrentMap<CharSequence, InstrumentEntity> cache = 
			new ConcurrentHashMap<CharSequence, InstrumentEntity>();
	
	private final LocalInstrumentDBMap db;
	private final ExecutorService executor;
	
	public ServiceDatabaseDDF(final LocalInstrumentDBMap map, 
			final ExecutorService executor) {
		
		this.db = map;
		this.executor = executor;
	}

	@Override
	public InstrumentEntity lookup(final CharSequence symbol) {
		
		return lookupBase(symbol);
		
	}

	@Override
	public Future<InstrumentEntity> lookupAsync(final CharSequence symbol) {
		return executor.submit(new SymbolLookup(symbol));
	}

	@Override
	public Map<CharSequence, InstrumentEntity> lookup(
			final Collection<? extends CharSequence> symbols) {
		
		final Map<CharSequence, InstrumentEntity> result = 
				new HashMap<CharSequence, InstrumentEntity>();
		
		// Currently just doing serial lookup
		for(final CharSequence symbol : symbols) {
			result.put(symbol, lookupBase(symbol));
		}
		
		return result;
	}

	@Override
	public Map<CharSequence, Future<InstrumentEntity>> lookupAsync(
			final Collection<? extends CharSequence> symbols) {
		
		final Map<CharSequence, Future<InstrumentEntity>> result =
				new HashMap<CharSequence, Future<InstrumentEntity>>();
		
		for(final CharSequence symbol : symbols) {
			result.put(symbol, executor.submit(new SymbolLookup(symbol)));
		}
				
		return result;
	}
	
	private InstrumentEntity lookupBase(CharSequence symbol) {
		
		symbol = ValueBuilder.newText(symbol.toString());
		
		if(symbol == null || symbol.length() == 0) {
			return InstrumentEntity.NULL_INSTRUMENT;
		}
		
		InstrumentEntity instrument = cache.get(symbol);
		
		if(instrument != null) {
			return instrument;
		}
		
		final InstrumentDefinition instDef = db.get(symbol.toString());
		
		if(cache.size() % 10000 == 0) {
			log.debug("Cache size = {}", cache.size());
		}
		
		if(instDef == null) {
			//log.debug("Symbol {} not in db", symbol);
			cache.put(symbol, InstrumentEntity.NULL_INSTRUMENT);
			return InstrumentEntity.NULL_INSTRUMENT;
		} else {
			instrument = InstrumentFactory.buildFromProtoBuf(instDef);
			cache.put(symbol, instrument);
			return instrument;
		}
	}
	
	private class SymbolLookup implements Callable<InstrumentEntity> {

		private final CharSequence symbol;
		
		public SymbolLookup(final CharSequence symbol) {
			this.symbol = symbol;
		}
		
		@Override
		public InstrumentEntity call() throws Exception {
			
			return lookupBase(symbol);
			
		}
		
	}

}
