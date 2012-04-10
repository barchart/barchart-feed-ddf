/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

//import com.barchart.feed.ddf.settings.api.DDF_Settings;
//import com.barchart.feed.ddf.settings.enums.DDF_ServerType;

/**
 * 
 * http://qs01.ddfplus.com/stream/quote.jsx?symbols=esm0,ibm&username=USER&
 * password=PASS
 * 
 * 
 * <data> − <QUOTE symbol="ESM0" name="E-Mini S&P 500" exchange="GBLX"
 * basecode="A" pointvalue="50.0" tickincrement="25" ddfexchange="M"
 * lastupdate="20100617211721" bid="111575" bidsize="2" ask="111600"
 * asksize="109" mode="R"> <SESSION day="H" session="G"
 * timestamp="20100617161742" open="111600" high="111625" low="111500"
 * last="111600" previous="111625" tradesize="1" volume="11656"
 * tradetime="20100617161629" id="combined"/> <SESSION day="G" session="G"
 * timestamp="20100617160825" open="111400" high="112250" low="110550"
 * last="111625" previous="111400" tradesize="4" volume="469613"
 * tradetime="20100617151457" id="previous"/> </QUOTE> − <QUOTE symbol="IBM"
 * name="International Business Machines" exchange="NYSE" basecode="A"
 * pointvalue="1.0" tickincrement="1" ddfexchange="N" flag="s"
 * lastupdate="20100617211605" bid="13068" bidsize="1" ask="13096" asksize="5"
 * mode="R"> <SESSION day="G" session="T" timestamp="20100617171408"
 * open="13007" high="13103" low="12986" last="13098" previous="13035"
 * settlement="13098" tradesize="400" volume="5570943"
 * tradetime="20100617160826" id="combined"/> <SESSION day="F" session="T"
 * timestamp="20100616184007" open="12834" high="13068" low="12834" last="13035"
 * previous="12979" settlement="13035" tradesize="3630" volume="6405495"
 * tradetime="20100616172653" id="previous"/> <SESSION day="F" session="R"
 * timestamp="20100616183054" previous="12979" id="session_F_R"/> <SESSION
 * day="G" session="R" timestamp="20100617171408" previous="13035"
 * id="session_G_R"/> </QUOTE> </data>
 * 
 * */

/**
 * 
 * no user/pass
 * 
 * http://qs01.ddfplus.com/restapi/symbolmaster/symbol/?symbol=IBM,ESM0
 * 
 * <symbols count="2">
 * 
 * <symbol unitcode="2" exchange="NYSE" name="International Business Machines"
 * tickincrement="1" pointvalue="1.0" symbol="IBM" request="IBM"/>
 * 
 * <symbol unitcode="2" exchange="GBLX" name="E-Mini S&P 500" tickincrement="25"
 * pointvalue="50.0" symbol="ESM0" request="ESM0"/>
 * 
 * </symbols>
 * 
 * */

final class ConstInstrumentDDF {

	private ConstInstrumentDDF() {
	}

//	static final DDF_ServerType SERVER_TYPE = DDF_ServerType.STREAM;

	/**
	 * * http://qs01.ddfplus.com/stream/quote.jsx?symbols=esm0,ibm&username=USER
	 * & password=PASS
	 * */
	private static final String urlQuoteLookup(final CharSequence server,
			final CharSequence username, final CharSequence password,
			final CharSequence symbolGuid) {
		return "http://" + server + "/stream/quote.jsx" + "?" + "symbols="
				+ symbolGuid + "&" + "username=" + username + "&" + "password="
				+ password;
	}

//	private static final String urlQuoteLookup(final DDF_Settings settings,
//			final CharSequence symbolGuid) {
//		final String server = settings.getServer(SERVER_TYPE)
//				.getPrimaryOrSecondary();
//		final String username = settings.getAuthUser();
//		final String password = settings.getAuthPass();
//		return urlQuoteLookup(server, username, password, symbolGuid);
//	}

	/**
	 * http://extras.ddfplus.com/instruments/?lookup=esu0
	 * 
	 * <instruments status="200" count="1">
	 * 
	 * <instrument lookup="esu0" status="200" guid="ESU2010" symbol_ddf="ESU0"
	 * symbol_historical="ESU10" symbol_description="E-Mini S&P 500"
	 * symbol_expire="2010-09-01T00:00:00-05:00" symbol_ddf_expire_month="U"
	 * symbol_ddf_expire_year="0" symbol_type="future" exchange="XCME"
	 * exchange_channel="GBLX" exchange_description="CMEGroup CME (Globex Mini)"
	 * exchange_ddf="M" time_zone_ddf="America/Chicago" tick_increment="25"
	 * base_code="2" unit_code="A" point_value="50"/>
	 * 
	 * </instruments>
	 */
	static final String SERVER_EXTRAS = "extras.ddfplus.com";

	static final String urlInstrumentLookup(final CharSequence lookup) {
		return "http://" + SERVER_EXTRAS + "/instruments/?lookup=" + lookup;
	}

}
