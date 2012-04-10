/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.util;

import com.barchart.util.ascii.ASCII;

// TODO: Auto-generated Javadoc
/**
 * DDF data feed protocol constants.
 */
public class FeedDDF {

	/** The Constant VERSION_2. */
	public static final String VERSION_2 = "2";

	/** The Constant VERSION_3. */
	public static final String VERSION_3 = "3";

	// ### tcp elements ###

	/** server response: request accepted. */
	public static final char TCP_ACCEPT = ASCII.PLUS;

	/** server response: request rejected. */
	public static final char TCP_REJECT = ASCII.MINUS;

	/** server response: incoming command. */
	public static final char TCP_COMMAND = ASCII._C_;

	/** server response: incoming welcome. */
	public static final char TCP_WELCOME = ASCII._W_;

	// ### common elements ###

	/** end of stream. */
	public static final int EOS = -1;

	/** status = empty. */
	public static final char NUL = ASCII.NUL;

	/** ddf message begins. */
	public static final char DDF_START = ASCII.SOH;

	/** ddf message continues. */
	public static final char DDF_MIDDLE = ASCII.STX;

	/** ddf message ends. */
	public static final char DDF_FINISH = ASCII.ETX;

	/** message separator. */
	public static final char DDF_TERMINATE = ASCII.LF;

	/** "century" code : time stamp start marker. */
	public static final char DDF_CENTURY = ASCII.DC4;

	/** "record" code of time stamp message. */
	public static final char DDF_TIMESTAMP = ASCII.POUND;

	/** ddf command term separator. */
	public static final char SEP = ASCII.SPACE;

	/** xml formatted market snapshot. */
	public static final char XML_SNAPSHOT = ASCII.PERCENT;

	/** xml pseudo record code for xml messages. */
	public static final char XML_RECORD = ASCII._X_;

	/** xml pseudo sub record code for book. */
	public static final char XML_SUB_BOOK = ASCII._B_;

	/** xml pseudo sub record code for cumulative volume. */
	public static final char XML_SUB_CUVOL = ASCII._C_;

	/** xml pseudo sub record code for market quote. */
	public static final char XML_SUB_QUOTE = ASCII._Q_;

	/** xml pseudo sub record code for market session. */
	public static final char XML_SUB_SESSION = ASCII._S_;

	//

	/** name of invalid symbol sent when we need start receiving time stamps. */
	public static final String SYMBOL_TIMESTAMP = "TIMESTAMP";

	//

	private FeedDDF() {
		// singleton
	}

	/**
	 * Tcp login.
	 *
	 * @param username the username
	 * @param password the password
	 * @return the char sequence
	 */
	public static final CharSequence tcpLogin(CharSequence username,
			CharSequence password) {
		return "LOGIN" + SEP + username + ":" + password + DDF_TERMINATE;
	}

	/**
	 * Tcp version.
	 *
	 * @param version the version
	 * @return the char sequence
	 */
	public static final CharSequence tcpVersion(CharSequence version) {
		return "VERSION" + SEP + version + DDF_TERMINATE;
	}

	/**
	 * start with default interest : quote updates only.
	 *
	 * @param symbol the symbol
	 * @return the char sequence
	 */
	public static final CharSequence tcpGo(CharSequence symbol) {
		return "GO" + SEP + symbol + DDF_TERMINATE;
	}

	/**
	 * empty interest will stop feed for this symbol.
	 *
	 * @param symbol the symbol
	 * @param interest the interest
	 * @return the char sequence
	 */
	public static final CharSequence tcpGo(CharSequence symbol,
			CharSequence interest) {
		return "GO" + SEP + symbol + "=" + interest + DDF_TERMINATE;
	}

	/**
	 * Tcp stream listen.
	 *
	 * @param symbolString the symbol string
	 * @return the char sequence
	 */
	public static final CharSequence tcpStreamListen(CharSequence symbolString) {
		return "STR LIS" + SEP + symbolString + DDF_TERMINATE;
	}

	/**
	 * Tcp stream snapshot.
	 *
	 * @param symbolString the symbol string
	 * @return the char sequence
	 */
	public static final CharSequence tcpStreamSnapshot(CharSequence symbolString) {
		return "STR STA" + SEP + symbolString + DDF_TERMINATE;
	}

	/**
	 * Tcp stream raw.
	 *
	 * @param symbolString the symbol string
	 * @return the char sequence
	 */
	public static final CharSequence tcpStreamRaw(CharSequence symbolString) {
		return "STR RAW" + SEP + symbolString + DDF_TERMINATE;
	}

	/**
	 * Tcp stop.
	 *
	 * @return the char sequence
	 */
	public static final CharSequence tcpStop() {
		return "STOP" + DDF_TERMINATE;
	}

	/**
	 * Tcp logout.
	 *
	 * @return the char sequence
	 */
	public static final CharSequence tcpLogout() {
		return "LOGOFF" + DDF_TERMINATE;
	}

	/**
	 * Tcp lockout.
	 *
	 * @return the char sequence
	 */
	public static final CharSequence tcpLockout() {
		return "LOCKOUT" + DDF_TERMINATE;
	}

	/* ############################## */

	/** The Constant RESPONSE_LOGIN_SUCCESS. */
	public static final String RESPONSE_LOGIN_SUCCESS = "Successful login";
	
	/** The Constant RESPONSE_LOGIN_FAILURE. */
	public static final String RESPONSE_LOGIN_FAILURE = "Login Failed";
	
	/** The Constant RESPONSE_SESSION_LOCKOUT. */
	public static final String RESPONSE_SESSION_LOCKOUT = "IP Lockout";
	
	/** The Constant RESPONSE_UNKNOWN_COMMAND. */
	public static final String RESPONSE_UNKNOWN_COMMAND = "Unknown Command";

}
