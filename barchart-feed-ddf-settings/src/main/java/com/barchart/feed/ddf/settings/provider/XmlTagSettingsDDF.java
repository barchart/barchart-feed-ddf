/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/*
 * 
 */
package com.barchart.feed.ddf.settings.provider;

/**
 * The Class Const.
 */
final class XmlTagSettingsDDF {

	private XmlTagSettingsDDF() {
	}

	//

	/** The Constant LOGIN_TAG. */
	public static final String LOGIN_TAG = "usersettings";

	/** The Constant LOGIN. */
	public static final String LOGIN = "login";

	/** The Constant LOGIN_USERNAME. */
	public static final String LOGIN_USERNAME = "username";

	/** The Constant LOGIN_PASSWORD. */
	public static final String LOGIN_PASSWORD = "password";

	/** The Constant LOGIN_STATUS. */
	public static final String LOGIN_STATUS = "status";

	/** The Constant LOGIN_CREDENTIALS. */
	public static final String LOGIN_CREDENTIALS = "credentials";

	/** The Constant LOGIN_SERVICE. */
	public static final String LOGIN_SERVICE = "service";

	/** The Constant LOGIN_SERVICE_ID. */
	public static final String LOGIN_SERVICE_ID = "id";

	/** The Constant LOGIN_SERVICE_MAXSYMBOLS. */
	public static final String LOGIN_SERVICE_MAXSYMBOLS = "maxsymbols";

	/** The Constant LOGIN_EXCHANGES. */
	public static final String LOGIN_EXCHANGES = "exchanges";

	/** The Constant LOGIN_EXCHANGES_IDS. */
	public static final String LOGIN_EXCHANGES_IDS = "ids";

	/** The Constant SERVERS. */
	public static final String SERVERS = "servers";

	/** The Constant SERVERS_SERVER. */
	public static final String SERVERS_SERVER = "server";

	/** The Constant SERVERS_SERVER_TYPE. */
	public static final String SERVERS_SERVER_TYPE = "type";

	/** The Constant SERVERS_SERVER_PRIMARY. */
	public static final String SERVERS_SERVER_PRIMARY = "primary";

	/** The Constant SERVERS_SERVER_SECONDARY. */
	public static final String SERVERS_SERVER_SECONDARY = "secondary";

	/** The Constant SERVERS_SERVER_RECOVERY. */
	public static final String SERVERS_SERVER_RECOVERY = "recovery";
	
	public static final String SERVERS_SERVER_WSS = "wss";

}
