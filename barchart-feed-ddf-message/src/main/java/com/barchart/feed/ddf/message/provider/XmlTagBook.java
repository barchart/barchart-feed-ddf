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
 * %<BOOK symbol="ESM0" basecode="A" askcount="10" bidcount="10" askprices
 * ="110000,110025,110050,110075,110100,110125,110150,110175,110200,110225"
 * asksizes="247,794,646,868,811,1049,830,1244,2287,694" bidprices=
 * "109975,109950,109925,109900,109875,109850,109825,109800,109775,109750"
 * bidsizes="162,582,692,698,953,680,819,931,958,1166"/>
 * 
 * */
final class XmlTagBook {

	private XmlTagBook() {
	}

	final static String TAG = "BOOK";

	// note: there is no exchange in this tag

	final static String SYMBOL = "symbol";
	final static String FRACTION_DDF = "basecode";

	final static String ASK_COUNT = "askcount";
	final static String BID_COUNT = "bidcount";

	final static String ASK_PRICE_ARRAY = "askprices";
	final static String ASK_SIZE_ARRAY = "asksizes";

	final static String BID_PRICE_ARRAY = "bidprices";
	final static String BID_SIZE_ARRAY = "bidsizes";

}
