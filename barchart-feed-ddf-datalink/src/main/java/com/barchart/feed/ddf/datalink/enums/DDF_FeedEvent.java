/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.enums;

import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;

public enum DDF_FeedEvent {

	/**
	 * posted after {@link DDF_FeedClient#login}; when web service lookup failed
	 */
	LOGIN_INVALID, //

	/**
	 * posted after {@link DDF_FeedClient#login} success to remote feed server
	 * */
	LOGIN_SUCCESS, //

	/**
	 * posted after {@link DDF_FeedClient#login} failure to remote feed server
	 * */
	LOGIN_FAILURE, //

	/**
	 * posted after user initiated {@link DDF_FeedClient#logout}
	 * */
	LOGOUT, //

	/**
	 * posted after {@link DDF_FeedClient#login}, success, if remote feed server
	 * issued a forced logout later, due to a duplicate login attempt;
	 */
	SESSION_LOCKOUT, //

	/**
	 * transport connection initiated; normally posted before login;
	 * */
	LINK_CONNECT, //

	/**
	 * transport connection terminated; normally posted after logout;
	 * */
	LINK_DISCONNECT, //

	/**
	 * link heart beat; posted for each DDF time stamp message
	 */
	HEART_BEAT, //

	;

}
