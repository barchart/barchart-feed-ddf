/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.ddf.instrument.provider.ext.NewInstrumentProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class InstrumentLookupExample.
 */
public class InstrumentLookupExample {

	private static final Logger log = LoggerFactory
			.getLogger(InstrumentLookupExample.class);

	static final void find(final String symbol) {

		final Instrument instrument = NewInstrumentProvider.fromSymbol(symbol);

		if (instrument.isNull()) {
			log.error("instrument lookup failed for : {}", symbol);
			return;
		}

		log.info("instrument : {} \n", instrument);

	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public final static void main(final String[] args) {

		find("_S_SP_CLM2_CLN2");

		// find("esu10");
		//
		// find("esu0");
		// find("zcn9");
		// find("rjm8");
		//
		// find("ibm");
		// find("goog");
		// find("orcl");
		//
		// find("$inx");
		// find("$dowi");
		// find("$nasx");
		//
		// find("rjz1");

		find("_S_FX_A6H2_A6Z1");

	}

}
