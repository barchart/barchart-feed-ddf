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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.inst.Instrument;
import com.barchart.feed.api.inst.InstrumentGUID;
import com.barchart.feed.api.inst.SymbologyContext;
import com.barchart.feed.ddf.instrument.api.DDF_DefinitionService;
import com.barchart.missive.core.Missive;
import com.barchart.util.anno.ThreadSafe;

/**
 * keeps in memory cache.
 */
@ThreadSafe
public class ServiceMemoryDDF implements DDF_DefinitionService {

	static final Logger log = LoggerFactory.getLogger(ServiceMemoryDDF.class);

	private final ConcurrentMap<InstrumentGUID, Instrument> guidMap = 
			new ConcurrentHashMap<InstrumentGUID, Instrument>();
	
	private final LocalCacheSymbologyContextDDF cache = new LocalCacheSymbologyContextDDF();
	private final SymbologyContext<CharSequence> remote = new RemoteSymbologyContextDDF(guidMap);
			
	/**
	 * Instantiates a new service memory ddf.
	 */
	public ServiceMemoryDDF() {
	}

	@Override
	public Instrument lookup(final CharSequence symbol) {
		
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

		return Missive.build(InstrumentDDF.class, instrument);
		
	}

	@Override
	public Map<CharSequence, Instrument> lookup(Collection<? extends CharSequence> symbols) {
		
		if (symbols == null || symbols.size() == 0) {
			log.warn("Lookup called with empty collection");
			return new HashMap<CharSequence, Instrument>(0); 
		}

		final Map<CharSequence, InstrumentGUID> gMap = remote.lookup(symbols);
				
		final Map<CharSequence, Instrument> instMap = 
				new HashMap<CharSequence, Instrument>();

		for (final CharSequence symbol : symbols) {
			instMap.put(symbol.toString(), guidMap.get(gMap.get(symbol.toString().toUpperCase())));
		}

		return instMap;
	}

	@Override
	public Future<Instrument> lookupAsync(CharSequence symbol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<CharSequence, Future<Instrument>> lookupAsync(
			Collection<? extends CharSequence> symbols) {
		// TODO Auto-generated method stub
		return null;
	}
	
//	/** make an upper case id */
//	private DDF_Instrument load(final TextValue symbol) {
//
//		final TextValue lookup = lookupFromSymbol(symbol);
//
//		return instrumentMap.get(lookup);
//
//	}
//
//	/** this will make 3 entries for futures and 1 entry for equities */
//	private void store(final DDF_Instrument instrument) {
//
//		/**
//		 * making assumption that first lookup of ESM0 will set symbol GUID per
//		 * DDF convention; that is ESM2020 in year 2011;
//		 * "if symbol expired, move forward"
//		 * 
//		 * this logic can overwrite previously defined symbol; say we are in
//		 * year 2011; say ESM0 was already defined for ESM2020 resolution; now
//		 * request comes for ESM2010; now ESM0 will resolve to ESM2010, and not
//		 * ESM2020
//		 * 
//		 * @author g-litchfield Removed mapping by DDF_SYMBOL_REALTIME because
//		 *         this is not always unique and was causing caching problems,
//		 *         specifically in KCK2 vs KCK02
//		 * 
//		 */
//
//		final TextValue symbolDDF =
//				instrument.get(DDF_SYMBOL_REALTIME).toUpperCase();
//		final TextValue symbolHIST =
//				instrument.get(DDF_SYMBOL_HISTORICAL).toUpperCase();
//		final TextValue symbolGUID =
//				instrument.get(DDF_SYMBOL_UNIVERSAL).toUpperCase();
//
//		ddfInstrumentMap.put(symbolDDF, instrument);
//
//		// hack for bats
//
//		if (symbolDDF.toString().contains(".BZ")) {
//			final TextValue lookup =
//					ValueBuilder.newText(symbolDDF.toString()
//							.replace(".BZ", ""));
//
//			instrumentMap.put(lookup, instrument);
//		}
//
//		instrumentMap.put(symbolHIST, instrument);
//		instrumentMap.put(symbolGUID, instrument);
//
//		log.debug("defined instrument={}", symbolGUID);
//
//	}
	
}
