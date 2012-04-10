/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.symbol.provider.DDF_Symbology.*;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.api.instrument.enums.InstrumentField;
import com.barchart.feed.ddf.instrument.api.DDF_DefinitionService;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.util.anno.ThreadSafe;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.provider.ValueBuilder;

// TODO: Auto-generated Javadoc
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

	/* (non-Javadoc)
	 * @see com.barchart.feed.base.api.instrument.DefinitionService#clear()
	 */
	@Override
	public void clear() {
	}

	/**
	 * TODO add more fields via symbol parser.
	 *
	 * @param symbol the symbol
	 * @return the dD f_ instrument
	 */
	@Override
	public DDF_Instrument lookup(final TextValue symbol) {

		if (CodecHelper.isEmpty(symbol)) {
			return DDF_InstrumentProvider.NULL_INSTRUMENT;
		}

		/** make an upper case id */
		final TextValue lookup = lookupFromSymbol(symbol);

		final InstrumentDDF instrument = new InstrumentDDF();

		instrument.set(InstrumentField.ID, lookup);

		return instrument;

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.instrument.api.DDF_DefinitionService#lookup(java.util.List)
	 */
	@Override
	public List<DDF_Instrument> lookup(final List<String> symbolList) {

		if (CodecHelper.isEmpty(symbolList)) {
			return DDF_InstrumentProvider.NULL_LIST;
		}

		final List<DDF_Instrument> list = new ArrayList<DDF_Instrument>(
				symbolList.size());

		for (final String symbol : symbolList) {

			final DDF_Instrument instrument = lookup(ValueBuilder
					.newText(symbol));

			if (instrument.isNull()) {
				continue;
			}

			list.add(instrument);

		}

		return list;

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.instrument.api.DDF_DefinitionService#lookupDDF(com.barchart.util.values.api.TextValue)
	 */
	@Override
	public DDF_Instrument lookupDDF(TextValue symbol) {
		
		return null;

	}

}
