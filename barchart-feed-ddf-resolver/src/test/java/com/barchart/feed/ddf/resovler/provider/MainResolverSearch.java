/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.resovler.provider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.api.framework.data.InstrumentField;
import com.barchart.feed.ddf.resolver.api.DDF_Resolver;
import com.barchart.feed.ddf.resolver.provider.DDF_ResolverProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class MainResolverSearch.
 */
public class MainResolverSearch {

	private static Logger log = LoggerFactory
			.getLogger(MainResolverSearch.class);

	static DDF_Resolver resolver;

	static void search() throws Exception {

		log.debug("start");

		// List<DDF_Instrument> result =
		// resolver.searchSimple("mini nasd 100 08");

		// List<DDF_Instrument> result = resolver.searchSimple("es*1 *450c");
		final List<Instrument> result = resolver.searchSimple("msft");

		log.debug("finish");

		log.debug("result.size : {}", result.size());

		for (final Instrument inst : result) {

			log.debug("instrument {} {}", inst.get(InstrumentField.SYMBOL),
					inst.get(InstrumentField.DESCRIPTION));

		}

	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(final String... args) throws Exception {

		final String folder = MainConst.FOLDER;
		final int limit = MainConst.LIMIT;

		resolver = DDF_ResolverProvider.newInstance(null, folder, limit);

		while (true) {

			search();

			Thread.sleep(3 * 1000);

		}

	}

}
