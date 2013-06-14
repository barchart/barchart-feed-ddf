/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.api.inst.InstrumentFuture;
import com.barchart.feed.api.inst.InstrumentFutureMap;
import com.barchart.feed.api.inst.InstrumentGUID;
import com.barchart.feed.api.inst.SymbologyContext;
import com.barchart.feed.ddf.instrument.api.DDF_DefinitionService;
import com.barchart.missive.core.ObjectMapFactory;
import com.barchart.util.anno.ThreadSafe;

/**
 * keeps in memory cache.
 */
@ThreadSafe
public class ServiceMemoryDDF implements DDF_DefinitionService {

	static final Logger log = LoggerFactory.getLogger(ServiceMemoryDDF.class);

	private final ConcurrentMap<InstrumentGUID, Instrument> guidMap = 
			new ConcurrentHashMap<InstrumentGUID, Instrument>();
	
	private final LocalCacheSymbologyContextDDF cache = 
			new LocalCacheSymbologyContextDDF();
	private final SymbologyContext<CharSequence> remote = 
			new RemoteSymbologyContextDDF(guidMap);
			
	private final ExecutorService executor;
	
	/**
	 * Instantiates a new service memory ddf.  Uses system default cached
	 * thread pool executor.
	 */
	public ServiceMemoryDDF() {
		executor = Executors.newCachedThreadPool();
	}
	
	/**
	 * Instantiates a new service memory ddf using specified executor service.
	 * @param executor
	 */
	public ServiceMemoryDDF(final ExecutorService executor) {
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
	public Map<CharSequence, Instrument> lookup(
			Collection<? extends CharSequence> symbols) {
		
		try {
			return retrieveMap(symbols);
		} catch (final Throwable t) {
			log.error("Lookup failed", t);
			return new HashMap<CharSequence, Instrument>();
		}
		
	}

	@Override
	public InstrumentFuture lookupAsync(CharSequence symbol) {
		
		InstrumentFuture future = new InstrumentFuture();
		
		executor.submit(new LookupCallable(symbol, future));
		
		return future;
		
	}

	@Override
	public InstrumentFutureMap<CharSequence> lookupAsync(
			Collection<? extends CharSequence> symbols) {
		
		InstrumentFutureMap<CharSequence> future = 
				new InstrumentFutureMap<CharSequence>();
		
		executor.submit(new LookupMapCallable(symbols, future));
		
		return future;
		
	}
	
	private final class LookupCallable implements Callable<Instrument> {

		private final CharSequence symbol;
		private final InstrumentFuture future;
		
		LookupCallable(final CharSequence symbol, final InstrumentFuture future) {
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
	
	private Instrument retrieve(final CharSequence symbol) {
		
		if(symbol == null || symbol.length() == 0) {
			return Instrument.NULL_INSTRUMENT;
		}
		
		InstrumentGUID guid = cache.lookup(symbol.toString().toUpperCase()); 
				
		if(guid.isNull()) {
			guid = remote.lookup(symbol.toString().toUpperCase());
		}
		
		if(guid.equals(InstrumentGUID.NULL_INSTRUMENT_GUID)) {
			return Instrument.NULL_INSTRUMENT;
		}
		
		cache.storeGUID(symbol, guid);
		
		final InstrumentDDF instrument = (InstrumentDDF) guidMap.get(guid);

		if (instrument == null) {
			return Instrument.NULL_INSTRUMENT;
		}

		return ObjectMapFactory.build(InstrumentDDF.class, instrument);
		
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
