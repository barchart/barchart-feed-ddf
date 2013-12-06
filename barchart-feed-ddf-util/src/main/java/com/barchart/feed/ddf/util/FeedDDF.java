/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.util;

import com.barchart.util.common.ascii.ASCII;

/**
 * DDF data feed protocol constants
 */
public class FeedDDF {

	public static final String VERSION_2 = "2";

	public static final String VERSION_3 = "3";
	
	public static final String VERSION_4 = "4";

	// ### tcp elements ###

	/** server response: request accepted */
	public static final char TCP_ACCEPT = ASCII.PLUS;

	/** server response: request rejected */
	public static final char TCP_REJECT = ASCII.MINUS;

	/** server response: incoming command */
	public static final char TCP_COMMAND = ASCII._C_;

	/** server response: incoming welcome */
	public static final char TCP_WELCOME = ASCII._W_;

	// ### common elements ###

	/** end of stream */
	public static final int EOS = -1;

	/** status = empty */
	public static final char NUL = ASCII.NUL;

	/** ddf message begins */
	public static final char DDF_START = ASCII.SOH;

	/** ddf message continues */
	public static final char DDF_MIDDLE = ASCII.STX;

	/** ddf message ends */
	public static final char DDF_FINISH = ASCII.ETX;

	/** message separator */
	public static final char DDF_TERMINATE = ASCII.LF;

	/** "century" code : time stamp start marker */
	public static final char DDF_CENTURY = ASCII.DC4;

	/** "record" code of time stamp message */
	public static final char DDF_TIMESTAMP = ASCII.POUND;

	/** ddf command term separator */
	public static final char SEP = ASCII.SPACE;

	/** xml formatted market snapshot */
	public static final char XML_SNAPSHOT = ASCII.PERCENT;

	/** xml pseudo record code for xml messages */
	public static final char XML_RECORD = ASCII._X_;

	/** xml pseudo sub record code for book */
	public static final char XML_SUB_BOOK = ASCII._B_;

	/** xml pseudo sub record code for cumulative volume */
	public static final char XML_SUB_CUVOL = ASCII._C_;

	/** xml pseudo sub record code for market quote */
	public static final char XML_SUB_QUOTE = ASCII._Q_;

	/** xml pseudo sub record code for market session */
	public static final char XML_SUB_SESSION = ASCII._S_;

	//

	/** name of invalid symbol sent when we need start receiving time stamps */
	public static final String SYMBOL_TIMESTAMP = "TIMESTAMP";

	//

	private FeedDDF() {
		// singleton
	}

	public static final CharSequence tcpLogin(final CharSequence username,
			final CharSequence password) {
		return "LOGIN" + SEP + username + ":" + password + DDF_TERMINATE;
	}

	public static final CharSequence tcpVersion(final CharSequence version) {
		return "VERSION" + SEP + version + DDF_TERMINATE;
	}

	/** start with default interest : quote updates only */
	public static final CharSequence tcpGo(final CharSequence symbol) {
		return "GO" + SEP + symbol + DDF_TERMINATE;
	}

	/** empty interest will stop feed for this symbol */
	public static final CharSequence tcpGo(final CharSequence symbol,
			final CharSequence interest) {
		return "GO" + SEP + symbol + "=" + interest + DDF_TERMINATE;
	}

	public static final CharSequence tcpStreamListen(
			final CharSequence symbolString) {
		return "STR LIS" + SEP + symbolString + DDF_TERMINATE;
	}

	public static final CharSequence tcpStreamSnapshot(
			final CharSequence symbolString) {
		return "STR STA" + SEP + symbolString + DDF_TERMINATE;
	}

	public static final CharSequence tcpStreamRaw(
			final CharSequence symbolString) {
		return "STR RAW" + SEP + symbolString + DDF_TERMINATE;
	}

	public static final CharSequence tcpStop() {
		return "STOP" + DDF_TERMINATE;
	}

	public static final CharSequence tcpLogout() {
		return "LOGOFF" + DDF_TERMINATE;
	}

	public static final CharSequence tcpLockout() {
		return "LOCKOUT" + DDF_TERMINATE;
	}

	/* ############################## */

	public static final String RESPONSE_LOGIN_SUCCESS = "Successful login";
	public static final String RESPONSE_LOGIN_FAILURE = "Login Failed";
	public static final String RESPONSE_SESSION_LOCKOUT = "IP Lockout";
	public static final String RESPONSE_UNKNOWN_COMMAND = "Unknown Command";
	public static final String RESPONSE_VERSION_SET_3 = "Version set to 3";
	public static final String RESPONSE_VERSION_SET_4 = "Version set to 4";

}
