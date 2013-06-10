/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.symbol.provider.DDF_Symbology.lookupFromSymbol;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.api.inst.InstrumentFuture;
import com.barchart.feed.api.inst.InstrumentFutureMap;
import com.barchart.feed.ddf.instrument.api.DDF_DefinitionService;
import com.barchart.feed.inst.InstrumentField;
import com.barchart.missive.api.Tag;
import com.barchart.missive.core.ObjectMapFactory;
import com.barchart.util.anno.ThreadSafe;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.provider.ValueBuilder;

/**
 * creates fake unresolved instruments.
 */
@ThreadSafe
public class ServiceBasicDDF implements DDF_DefinitionService {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * Instantiates a new service basic ddf.
	 */
	public ServiceBasicDDF() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Instrument lookup(final CharSequence symbol) {
		if(symbol == null || symbol.length() == 0) {
			return Instrument.NULL_INSTRUMENT;
		}
		
		/** make an upper case id */
		final TextValue lookup = lookupFromSymbol(ValueBuilder.newText(symbol.toString()));

		final Map<Tag, Object> map = new HashMap<Tag, Object>();
		map.put(InstrumentField.MARKET_GUID, lookup);
		final Instrument instrument = ObjectMapFactory.build(InstrumentDDF.class, map);

		return instrument;
	}

	@Override
	public InstrumentFuture lookupAsync(final CharSequence symbol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<CharSequence, Instrument> lookup(final Collection<? extends CharSequence> symbols) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InstrumentFutureMap<CharSequence> lookupAsync(
			Collection<? extends CharSequence> symbols) {
		// TODO Auto-generated method stub
		return null;
	}

}
