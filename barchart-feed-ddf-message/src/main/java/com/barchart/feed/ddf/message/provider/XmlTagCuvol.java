/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

/**
 * feed format:
 * 
 * %<CV symbol="ESM0" basecode="A" tickincrement="25" last="109975" lastsize="2"
 * lastcvol="2" date="20100615120000" count="54" data="109275,1076:108900,437:109950,3813:109325,1429:108950,899:109925,6629:109875,5131:109225,1007:109800,5046:109125,1327:109075,1865:109750,5931:110050,2579:109025,1616:109975,2886:109200,1619:109550,875:109300,2010:109525,1691:109375,1504:109850,5318:109450,1163:109500,1899:109000,1906:110150,209:109625,2055:108850,38:109700,3787:110175,219:109350,1901:108975,476:109600,3659:109575,1799:110025,3751:110075,3451:110000,5387:109825,5224:109900,10427:109775,4809:109650,2887:109050,1817:109725,4396:109175,1807:109100,1835:108875,110:109400,1777:109425,1617:109150,1662:109475,938:110125,446:109250,1215:110100,2035:108925,751:109675,2493"
 * />
 * 
 * */
final class XmlTagCuvol {

	private XmlTagCuvol() {
	}

	final static String TAG = "CV";

	// note: there is no exchange in this tag

	final static String SYMBOL = "symbol";
	final static String FRACTION_DDF = "basecode";

	final static String PRICE_TICK_INCREMENT = "tickincrement";

	/** last trade descriptor */
	final static String PRICE_LAST = "last";
	final static String SIZE_LAST = "lastsize";
	final static String TIME_LAST = "date"; // XXX really?

	/** total current combo session volume at the last trade price level */
	final static String SIZE_LAST_CUVOL = "lastcvol";

	final static String ENTRY_COUNT = "count";
	final static String ENTRY_ARRAY = "data";

}
