package com.barchart.feed.ddf.instrument.provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.inst.InstrumentFuture;
import com.barchart.feed.api.inst.InstrumentFutureMap;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.ddf.instrument.api.DDF_DefinitionService;
import com.barchart.feed.inst.provider.InstrumentFactory;
import com.barchart.proto.buf.inst.InstrumentDefinition;
import com.barchart.util.values.provider.ValueBuilder;

public class ServiceDatabaseDDF implements DDF_DefinitionService {
	
	static final Logger log = LoggerFactory.getLogger(ServiceDatabaseDDF.class);
	
	private final ConcurrentMap<CharSequence, Instrument> cache = 
			new ConcurrentHashMap<CharSequence, Instrument>();
	
	private final LocalInstrumentDBMap db;
	private final ExecutorService executor;
	
	public ServiceDatabaseDDF(final LocalInstrumentDBMap map, 
			final ExecutorService executor) {
		
		this.db = map;
		this.executor = executor;
	}

	@Override
	public Instrument lookup(final CharSequence symbol) {
		
		try {
			return retrieve(symbol);
		} catch (final Throwable t) {
			log.error("Lookup failed", t);
			return Instrument.NULL_INSTRUMENT;
		}
		
	}

	@Override
	public InstrumentFuture lookupAsync(final CharSequence symbol) {
		
		InstrumentFuture future = new InstrumentFuture();
		
		executor.submit(new LookupCallable(symbol, future));
		
		return future;
		
	}

	@Override
	public Map<CharSequence, Instrument> lookup(
			final Collection<? extends CharSequence> symbols) {
		
		try {
			return retrieveMap(symbols);
		} catch (final Throwable t) {
			log.error("Lookup failed", t);
			return new HashMap<CharSequence, Instrument>();
		}
		
	}

	@Override
	public InstrumentFutureMap<CharSequence> lookupAsync(
			final Collection<? extends CharSequence> symbols) {
		
		InstrumentFutureMap<CharSequence> future = 
				new InstrumentFutureMap<CharSequence>();
		
		executor.submit(new LookupMapCallable(symbols, future));
		
		return future;
		
	}
	
	private Instrument retrieve(CharSequence symbol) {
		
		symbol = ValueBuilder.newText(symbol.toString());
		
		if(symbol == null || symbol.length() == 0) {
			return Instrument.NULL_INSTRUMENT;
		}
		
		Instrument instrument = cache.get(symbol);
		
		if(instrument != null) {
			return instrument;
		}
		
		final InstrumentDefinition instDef = db.get(symbol.toString());
		
		if(cache.size() % 10000 == 0) {
			log.debug("Cache size = {}", cache.size());
		}
		
		if(instDef == null) {
			//log.debug("Symbol {} not in db", symbol);
			cache.put(symbol, Instrument.NULL_INSTRUMENT);
			return Instrument.NULL_INSTRUMENT;
		} else {
			instrument = InstrumentFactory.buildFromProtoBuf(instDef);
			cache.put(symbol, instrument);
			return instrument;
		}
	}
	
	private class LookupCallable implements Callable<Instrument> {

		private final CharSequence symbol;
		private final InstrumentFuture future;
		
		public LookupCallable(final CharSequence symbol,
				final InstrumentFuture future) {
			this.symbol = symbol;
			this.future = future;
		}
		
		@Override
		public Instrument call() throws Exception {
			
			Instrument inst;
			
			try {
			
				inst = retrieve(symbol);
				future.succeed(inst);
				
			} catch (final Throwable t) {
				future.fail(t);
				return Instrument.NULL_INSTRUMENT;
			}
			
			return inst;
			
		}
		
	}
	
	private final class LookupMapCallable implements 
			Callable<Map<CharSequence, Instrument>> {

		private final Collection<? extends CharSequence> symbols;
		private final InstrumentFutureMap<CharSequence> future;

		LookupMapCallable(final Collection<? extends CharSequence> symbols,
				final InstrumentFutureMap<CharSequence> future) {
			this.symbols = symbols;
			this.future = future;
		}

		@Override
		public Map<CharSequence, Instrument> call() throws Exception {
			
			Map<CharSequence, Instrument> map;
			
			try {
				
				map = retrieveMap(symbols);
				future.succeed(map);
				
			} catch (final Throwable t) {
				future.fail(t);
				return new HashMap<CharSequence, Instrument>();
			}
			
			return map;
		}

	}
	
	private Map<CharSequence, Instrument> retrieveMap(
			final Collection<? extends CharSequence> symbols) {
		
		if (symbols == null || symbols.size() == 0) {
			log.warn("Lookup called with empty collection");
			return new HashMap<CharSequence, Instrument>(0); 
		}
		
		final Map<CharSequence, Instrument> result = 
				new HashMap<CharSequence, Instrument>();

		for (final CharSequence symbol : symbols) {
			try {
				result.put(symbol.toString(), retrieve(symbol));
			} catch (final Exception e) {
				result.put(symbol.toString(), Instrument.NULL_INSTRUMENT);
			}
		}
		
		return result;
		
	}

}
