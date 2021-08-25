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

final class ConstSettingsDDF {

	private ConstSettingsDDF() {
	}

	static final String DEFAULT_LOGIN_SERVICE_ID = "UNKNOWN";

	static final int DEFAULT_LOGIN_SERVICE_MAXSYMBOLS = 0;

	static final String LOGIN_OK = "OK";

	//

	static final String LOGIN_EXCHANGES_IDS_SEPARATOR = ",";

	//

	static final String DDF_CENTRAL = "https://webapp-proxy.aws.barchart.com/v1/ddfplus";

	static final String urlCentral(final String username, final String password) {
		return DDF_CENTRAL + "/" + "getUserSettings.php" + "?" + //
				"username=" + username + "&" + "password=" + password;
	}

}
