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
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.data.Instrument;
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
			return new LookupCallable(symbol).call();
		} catch (final Exception e) {
			return Instrument.NULL_INSTRUMENT;
		}
		
	}

	@Override
	public Map<CharSequence, Instrument> lookup(Collection<? extends CharSequence> symbols) {
		
		if (symbols == null || symbols.size() == 0) {
			log.warn("Lookup called with empty collection");
			return new HashMap<CharSequence, Instrument>(0); 
		}

		final Map<CharSequence, Instrument> instMap = 
				new HashMap<CharSequence, Instrument>();

		for (final CharSequence symbol : symbols) {
			try {
				instMap.put(symbol.toString(), new LookupCallable(symbol).call());
			} catch (final Exception e) {
				instMap.put(symbol.toString(), Instrument.NULL_INSTRUMENT);
			}
		}

		return instMap;
	}

	@Override
	public Future<Instrument> lookupAsync(CharSequence symbol) {
		return executor.submit(new LookupCallable(symbol));
	}

	@Override
	public Map<CharSequence, Future<Instrument>> lookupAsync(
			Collection<? extends CharSequence> symbols) {
		
		if (symbols == null || symbols.size() == 0) {
			log.warn("Lookup called with empty collection");
			return new HashMap<CharSequence, Future<Instrument>>(0); 
		}
		
		final Map<CharSequence, Future<Instrument>> result = 
				new HashMap<CharSequence, Future<Instrument>>();
		
		for(final CharSequence symbol : symbols) {
			result.put(symbol, executor.submit(new LookupCallable(symbol)));
		}
		
		return result;
	}
	
	private final class LookupCallable implements Callable<Instrument> {

		private final CharSequence symbol;
		
		LookupCallable(final CharSequence symbol) {
			this.symbol = symbol;
		}
		
		@Override
		public Instrument call() throws Exception {
			
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
			
			Instrument instrument = guidMap.get(guid);

			if (instrument == null) {
				return Instrument.NULL_INSTRUMENT;
			}

			return ObjectMapFactory.build(InstrumentDDF.class, instrument);
			
		}
		
	}
	
}
