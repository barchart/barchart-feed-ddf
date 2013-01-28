/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.symbol.provider.DDF_Symbology.lookupFromSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.instrument.api.DDF_DefinitionService;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.inst.api.Instrument;
import com.barchart.feed.inst.api.InstrumentField;
import com.barchart.feed.inst.api.InstrumentGUID;
import com.barchart.feed.inst.provider.InstrumentFactory;
import com.barchart.missive.core.Tag;
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

	public void clear() {
		
	}
	
	/**
	 * TODO add more fields via symbol parser.
	 * 
	 * @param symbol
	 *            the symbol
	 * @return the dD f_ instrument
	 */
	@Override
	public DDF_Instrument lookupDDF(final TextValue symbol) {

		if (CodecHelper.isEmpty(symbol)) {
			return DDF_InstrumentProvider.NULL_INSTRUMENT;
		}

		/** make an upper case id */
		final TextValue lookup = lookupFromSymbol(symbol);

		final Map<Tag, Object> map = new HashMap<Tag, Object>();
		map.put(InstrumentField.ID, lookup);
		final DDF_Instrument instrument = new InstrumentDDF(InstrumentFactory.build(map));

		return instrument;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.instrument.api.DDF_DefinitionService#lookup(java
	 * .util.List)
	 */
	@Override
	public List<DDF_Instrument> lookupDDF(final List<String> symbolList) {

		if (CodecHelper.isEmpty(symbolList)) {
			return DDF_InstrumentProvider.NULL_LIST;
		}

		final List<DDF_Instrument> list = new ArrayList<DDF_Instrument>(
				symbolList.size());

		for (final String symbol : symbolList) {

			final DDF_Instrument instrument = lookupDDF(ValueBuilder
					.newText(symbol));

			if (instrument.isNull()) {
				continue;
			}

			list.add(instrument);

		}

		return list;

	}

	@Override
	public Instrument lookup(CharSequence symbol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Instrument> lookupAsync(CharSequence symbol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Instrument> lookup(List<CharSequence> symbols) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<Instrument>> lookupAsync(List<CharSequence> symbols) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instrument lookup(InstrumentGUID guid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Instrument> lookupAsync(InstrumentGUID guid) {
		// TODO Auto-generated method stub
		return null;
	}


}
