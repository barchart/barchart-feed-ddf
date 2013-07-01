/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.util.Identifier;
import com.barchart.feed.ddf.instrument.api.DDF_DefinitionService;
import com.barchart.feed.inst.InstrumentFuture;
import com.barchart.feed.inst.InstrumentFutureMap;
import com.barchart.feed.inst.SymbologyContext;
import com.barchart.missive.core.ObjectMapFactory;
import com.barchart.util.anno.ThreadSafe;

/**
 * keeps in memory cache.
 */
@ThreadSafe
public class ServiceMemoryDDF implements DDF_DefinitionService {

	static final Logger log = LoggerFactory.getLogger(ServiceMemoryDDF.class);

	private final ConcurrentMap<Identifier, Instrument> guidMap = 
			new ConcurrentHashMap<Identifier, Instrument>();
	
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
	public List<Instrument> lookup(final CharSequence symbol) {
		
		try {
			return retrieve(symbol);
		} catch (final Throwable t) {
			log.error("Lookup failed", t);
			return Collections.emptyList();
		}
		
	}

	@Override
	public Map<CharSequence, List<Instrument>> lookup(
			Collection<? extends CharSequence> symbols) {
		
		try {
			return retrieveMap(symbols);
		} catch (final Throwable t) {
			log.error("Lookup failed", t);
			return Collections.emptyMap();
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
	
	private final class LookupCallable implements Callable<List<Instrument>> {

		private final CharSequence symbol;
		private final InstrumentFuture future;
		
		LookupCallable(final CharSequence symbol, final InstrumentFuture future) {
			this.symbol = symbol;
			this.future = future;
		}
		
		@Override
		public List<Instrument> call() throws Exception {
			
			List<Instrument> inst;
			
			try {
			
				inst = retrieve(symbol);
				future.succeed(inst);
				
			} catch (final Throwable t) {
				future.fail(t);
				return Collections.emptyList();
			}
			
			return inst;
			
		}
		
	}
	
	private List<Instrument> retrieve(final CharSequence symbol) {
		
		if(symbol == null || symbol.length() == 0) {
			return Collections.emptyList();
		}
		
		Identifier guid = cache.lookup(symbol.toString().toUpperCase()); 
				
		if(guid.isNull()) {
			guid = remote.lookup(symbol.toString().toUpperCase());
		}
		
		if(guid.isNull()) {
			return Collections.emptyList();
		}
		
		cache.storeGUID(symbol, guid);
		
		final InstrumentDDF instrument = (InstrumentDDF) guidMap.get(guid);

		if (instrument == null) {
			return Collections.emptyList();
		}

		Instrument inst = ObjectMapFactory.build(InstrumentDDF.class, instrument);
		
		return Collections.singletonList(inst);
		
	}
	
	private final class LookupMapCallable implements 
			Callable<Map<CharSequence, List<Instrument>>> {
		
		private final Collection<? extends CharSequence> symbols;
		private final InstrumentFutureMap<CharSequence> future;
		
		LookupMapCallable(final Collection<? extends CharSequence> symbols,
				final InstrumentFutureMap<CharSequence> future) {
			this.symbols = symbols;
			this.future = future;
		}

		@Override
		public Map<CharSequence, List<Instrument>> call() throws Exception {
			
			Map<CharSequence, List<Instrument>> map;
			
			try {
				
				map = retrieveMap(symbols);
				future.succeed(map);
				
			} catch (final Throwable t) {
				future.fail(t);
				return Collections.emptyMap();
			}
			
			return map;
		}
		
	}
	
	private Map<CharSequence, List<Instrument>> retrieveMap(
			final Collection<? extends CharSequence> symbols) {
		
		if (symbols == null || symbols.size() == 0) {
			log.warn("Lookup called with empty collection");
			return Collections.emptyMap(); 
		}
		
		final Map<CharSequence, List<Instrument>> result = 
				new HashMap<CharSequence, List<Instrument>>();

		for (final CharSequence symbol : symbols) {
			try {
				result.put(symbol.toString(), retrieve(symbol));
			} catch (final Exception e) {
				result.put(symbol.toString(), new ArrayList<Instrument>(0));
			}
		}
		
		return result;
		
	}
	
}
