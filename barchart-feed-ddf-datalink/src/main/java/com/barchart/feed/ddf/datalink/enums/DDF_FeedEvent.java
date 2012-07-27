/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.enums;

import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;

/**
 * Data server events handled by a feed handler.
 */
public enum DDF_FeedEvent {

	/**
	 * Posted after {@link DDF_FeedClient#login}; when web service lookup
	 * failed.
	 */
	LOGIN_INVALID, //

	/**
	 * Posted after sucessful transmission of login request.
	 */
	LOGIN_SENT, //

	/**
	 * Posted after {@link DDF_FeedClient#login} success to remote feed server.
	 */
	LOGIN_SUCCESS, //

	/**
	 * Posted after {@link DDF_FeedClient#login} failure to remote feed server.
	 */
	LOGIN_FAILURE, //

	/**
	 * Posted after user initiated {@link DDF_FeedClient#logout}.
	 */
	LOGOUT, //

	/**
	 * Posted after {@link DDF_FeedClient#login}, success, if remote feed server
	 * issued a forced logout later, due to a duplicate login attempt.
	 */
	SESSION_LOCKOUT, //

	/**
	 * Transport connection initiated; normally posted before login.
	 */
	LINK_CONNECT, //

	/**
	 * Transport connection terminated; normally posted after logout.
	 */
	LINK_DISCONNECT, //

	/**
	 * Attempt to retrieve user settings failed.
	 */
	SETTINGS_RETRIEVAL_FAILURE,

	/**
	 * Posted after attempt to make channel connection timed out.
	 */
	CHANNEL_CONNECT_TIMEOUT,

	/**
	 * Posted after attempt to make channel connection timed out.
	 */
	CHANNEL_CONNECT_FAILURE,

	/**
	 * Posted if an attempt to write to JERQ is unsuccessful.
	 */
	COMMAND_WRITE_FAILURE,

	/**
	 * Posted if an attempt to write to JERQ is successful.
	 */
	COMMAND_WRITE_SUCCESS,

	/**
	 * Link heart beat; posted for each DDF time stamp message.
	 */
	HEART_BEAT, //

	LINK_CONNECT_PROXY,

	LINK_CONNECT_PROXY_TIMEOUT

	;

	public static boolean isConnectionError(final DDF_FeedEvent event) {

		return event == LOGIN_FAILURE || event == SESSION_LOCKOUT
				|| event == LINK_DISCONNECT
				|| event == LINK_CONNECT_PROXY_TIMEOUT
				|| event == SETTINGS_RETRIEVAL_FAILURE
				|| event == CHANNEL_CONNECT_TIMEOUT
				|| event == CHANNEL_CONNECT_FAILURE;

	}

}
