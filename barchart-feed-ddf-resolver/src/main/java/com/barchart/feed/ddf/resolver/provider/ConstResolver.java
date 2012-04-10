/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.resolver.provider;

import java.util.Arrays;
import java.util.List;

import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ConstResolver {

	private static Logger log = LoggerFactory.getLogger(ConstResolver.class);

	static final Version VERSION = Version.LUCENE_34;

	/**
	 * used by @see #SYMBOL_LOOKUP
	 */
	static final String XML_RESULT = "Result";

	/**
	 * used by @see #SYMBOL_LOOKUP
	 */
	static final String XML_SYMBOL = "symbol";

	/** used for ddf symbol guid discovery */
	static final String SYMBOL_LOOKUP = "http://professional.barchart.com/support/symbollookup.php";

	/** search "starts with" */
	static String getSymbolLookupURI(final String term) {

		String resource = SYMBOL_LOOKUP + "?" + "name=" + term + "&" + "opt1=1";

		log.trace("resource : {}", resource);

		return resource;

	}

	private static final String[] SYMBOL_PREFIX = new String[] {
			//
			"#", "@", "-", "$", "0", "1", "2", "3", "4", "5", "6", "7", "8",
			"9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
			"m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y",
			"z" };

	/** list of known ddf symbol prefixes */
	static List<String> getSymbolPrefixList() {

		return Arrays.asList(SYMBOL_PREFIX);

		// return Arrays.asList(new String[] { "e" });

	}

	static final int LOG_STEP = 2 * 1000;

}
