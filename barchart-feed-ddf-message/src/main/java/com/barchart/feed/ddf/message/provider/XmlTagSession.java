/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

/**
 * 
 * <QUOTE symbol="ESM0" name="E-Mini S&P 500" exchange="GBLX" basecode="A"
 * pointvalue="50.0" tickincrement="25" ddfexchange="M"
 * lastupdate="20100615134838" bid="109950" bidsize="275" ask="109975"
 * asksize="258" mode="R">
 * 
 * <SESSION day="E" session="G" timestamp="20100615084859" open="109025"
 * high="110050" low="108850" last="109950" previous="109050" tradesize="1"
 * volume="240382" tradetime="20100615084856" id="combined"/>
 * 
 * <SESSION day="D" session="G" timestamp="20100614205606" open="109050"
 * high="110600" low="108875" last="109050" previous="108925" tradesize="1"
 * openinterest="1482925" volume="887313" tradetime="20100614151457"
 * id="previous"/>
 * 
 * </QUOTE>
 * 
 * */
final class XmlTagSession {

	private XmlTagSession() {
	}

	final static String TAG = "SESSION";

	// note: there is no exchange in this tag; inherit from parent

	/** trade date absolute */
	final static String TRADE_DAY = "day";

	/**
	 * quote qualifier : current combo vs previous combo vs individual non-combo
	 */
	final static String SESSION_INDICATOR = "id";

	final static String TIME_UPDATE = "timestamp";

	final static String PRICE_OPEN = "open";
	final static String PRICE_HIGH = "high";
	final static String PRICE_LOW = "low";
	// final static String PRICE_CLOSE = "close"; XXX ???
	final static String PRICE_SETTLE = "settlement";

	/** last trade source: DDF_Session */
	final static String SESSION = "session";

	/** last trade today */
	final static String PRICE_LAST = "last"; // XXX same as close
	final static String SIZE_LAST = "tradesize";
	final static String TIME_LAST = "tradetime";

	/** last trade price yesterday */
	final static String PRICE_LAST_PREV = "previous";

	final static String SIZE_VOLUME = "volume";
	final static String SIZE_INTEREST = "openinterest";

}
