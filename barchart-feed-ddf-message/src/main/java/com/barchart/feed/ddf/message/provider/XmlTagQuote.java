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
 * http://qs01.ddfplus.com/stream/quote.jsx?symbols=esm0&username=XXX&password=XXX
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

/**
 * feed format:
 * 
 * %<QUOTE symbol="ESM0" name="E-Mini S&amp;P 500" exchange="GBLX" basecode="A"
 * pointvalue="50.0" tickincrement="25" ddfexchange="M"
 * lastupdate="20100615144110" bid="109975" bidsize="162" ask="110000"
 * asksize="248" mode="R">
 * 
 * <SESSION day="E" session="G" timestamp="20100615094112" open="109025"
 * high="110175" low="108850" last="109975" previous="109050" tradesize="2"
 * volume="362318" tradetime="20100615094111" id="combined"/>
 * 
 * <SESSION day="D" session="G" timestamp="20100614205606" open="109050"
 * high="110600" low="108875" last="109050" previous="108925" tradesize="1"
 * openinterest="1482925" volume="887313" tradetime="20100614151457"
 * id="previous"/></QUOTE>
 * 
 * */
final class XmlTagQuote {

	private XmlTagQuote() {
	}

	final static String TAG = "QUOTE";

	final static String SYMBOL = "symbol";
	final static String FRACTION_DDF = "basecode";

	final static String PRICE_TICK_INCREMENT = "tickincrement";

	final static String EXCHANGE_DDF = "ddfexchange";
	final static String EXCHANGE_EXTRA = "exchange";

	final static String SYMBOL_NAME = "name";

	/** book top descriptor */
	final static String PRICE_BID = "bid";
	final static String SIZE_BID = "bidsize";
	final static String PRICE_ASK = "ask";
	final static String SIZE_ASK = "asksize";

	final static String PRICE_POINT_VALUE = "pointvalue";

	/** time any market change */
	final static String TIME_UPDATE = "lastupdate";

	/** optional user mode/permission information */
	final static String QUOTE_MODE = "mode";

	/** quote state transitions flag */
	final static String QUOTE_STATE = "flag";

}
