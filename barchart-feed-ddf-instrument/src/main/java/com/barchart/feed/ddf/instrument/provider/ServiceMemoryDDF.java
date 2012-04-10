/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.*;
import static com.barchart.feed.ddf.symbol.provider.DDF_Symbology.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.util.anno.ThreadSafe;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.provider.ValueBuilder;

// TODO: Auto-generated Javadoc
/**
 * keeps in memory cache.
 */
@ThreadSafe
public class ServiceMemoryDDF extends ServiceBasicDDF {

	static final Logger log = LoggerFactory.getLogger(ServiceMemoryDDF.class);

	private final ConcurrentMap<TextValue, DDF_Instrument> instrumentMap = //
	new ConcurrentHashMap<TextValue, DDF_Instrument>();

	private final ConcurrentMap<TextValue, DDF_Instrument> ddfInstrumentMap = //
	new ConcurrentHashMap<TextValue, DDF_Instrument>();
	
	/**
	 * Instantiates a new service memory ddf.
	 */
	public ServiceMemoryDDF() {
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.instrument.provider.ServiceBasicDDF#clear()
	 */
	@Override
	public final void clear() {
		instrumentMap.clear();
		ddfInstrumentMap.clear();
	}

	/** make an upper case id */
	private DDF_Instrument load(final TextValue symbol) {

		final TextValue lookup = lookupFromSymbol(symbol);

		return instrumentMap.get(lookup);

	}

	/** this will make 3 entries for futures and 1 entry for equities */
	private void store(final DDF_Instrument instrument) {

		/**
		 * making assumption that first lookup of ESM0 will set symbol GUID per
		 * DDF convention; that is ESM2020 in year 2011;
		 * "if symbol expired, move forward"
		 * 
		 * this logic can overwrite previously defined symbol; say we are in
		 * year 2011; say ESM0 was already defined for ESM2020 resolution; now
		 * request comes for ESM2010; now ESM0 will resolve to ESM2010, and not
		 * ESM2020
		 * 
		 * @author g-litchfield Removed mapping by DDF_SYMBOL_REALTIME because
		 *         this is not always unique and was causing caching problems,
		 *         specifically in KCK2 vs KCK02
		 * 
		 */

		final TextValue symbolDDF = instrument.get(DDF_SYMBOL_REALTIME)
				.toUpperCase();
		final TextValue symbolHIST = instrument.get(DDF_SYMBOL_HISTORICAL)
				.toUpperCase();
		final TextValue symbolGUID = instrument.get(DDF_SYMBOL_UNIVERSAL)
				.toUpperCase();

		ddfInstrumentMap.put(symbolDDF, instrument);
		
		// hack for bats 
	
		if(symbolDDF.toString().contains(".BZ")){
			final TextValue lookup = ValueBuilder
					.newText(symbolDDF.toString().replace(".BZ", ""));
			
			instrumentMap.put(lookup, instrument);
		}
		
		instrumentMap.put(symbolHIST, instrument);
		instrumentMap.put(symbolGUID, instrument);

		log.debug("defined instrument={}", symbolGUID);

	}
	
	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.instrument.provider.ServiceBasicDDF#lookupDDF(com.barchart.util.values.api.TextValue)
	 */
	@Override
	public DDF_Instrument lookupDDF(TextValue symbol) {

		if (CodecHelper.isEmpty(symbol)) {
			return DDF_InstrumentProvider.NULL_INSTRUMENT;
		}

		final TextValue lookup = symbol.toUpperCase();
		
		DDF_Instrument instrument = ddfInstrumentMap.get(lookup);

		if (instrument == null) {

			try {

				instrument = DDF_InstrumentProvider.remoteLookup(symbol);

				store(instrument);

			} catch (final Exception e) {
				log.warn("lookupDDF : instrument lookup failed; symbol : {}", symbol);
				return DDF_InstrumentProvider.NULL_INSTRUMENT;
			}

		}

		return instrument;


	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.instrument.provider.ServiceBasicDDF#lookup(com.barchart.util.values.api.TextValue)
	 */
	@Override
	public final DDF_Instrument lookup(final TextValue symbol) {

		if (CodecHelper.isEmpty(symbol)) {
			return DDF_InstrumentProvider.NULL_INSTRUMENT;
		}

		DDF_Instrument instrument = load(symbol);

		if (instrument == null) {

			try {

				instrument = DDF_InstrumentProvider.remoteLookup(symbol);

				store(instrument);

			} catch (final Exception e) {
				log.warn("instrument lookup failed; symbol : {}", symbol);
				return DDF_InstrumentProvider.NULL_INSTRUMENT;
			}

		}

		return instrument;

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.instrument.provider.ServiceBasicDDF#lookup(java.util.List)
	 */
	@Override
	public List<DDF_Instrument> lookup(final List<String> symbolList) {

		if (CodecHelper.isEmpty(symbolList)) {
			return DDF_InstrumentProvider.NULL_LIST;
		}

		final int size = symbolList.size();

		final List<String> fetchList = new ArrayList<String>(size);

		final List<DDF_Instrument> oldList = new ArrayList<DDF_Instrument>(size);

		for (final String symbol : symbolList) {

			final TextValue id = ValueBuilder.newText(symbol);

			final DDF_Instrument instrument = load(id);

			if (instrument == null) {
				fetchList.add(symbol);
			} else {
				oldList.add(instrument);
			}

		}

		final List<DDF_Instrument> newList = DDF_InstrumentProvider
				.fetch(fetchList);

		for (final DDF_Instrument instrument : newList) {
			store(instrument);
		}

		oldList.addAll(newList);

		return oldList;

	}

}
