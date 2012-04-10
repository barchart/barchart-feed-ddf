/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.api;

/**
 * http://en.wikipedia.org/wiki/Support_and_resistance
 * */
public interface DDF_EntryTrend extends DDF_Entry {

	/** http://en.wikipedia.org/wiki/Support_and_resistance#Resistance */
	long priceResistance();

	/** http://en.wikipedia.org/wiki/Support_and_resistance#Support */
	long priceSupport();

}
