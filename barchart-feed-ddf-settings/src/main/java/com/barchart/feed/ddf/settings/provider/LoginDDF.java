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

import static com.barchart.feed.ddf.settings.provider.ConstSettingsDDF.DEFAULT_LOGIN_SERVICE_ID;
import static com.barchart.feed.ddf.settings.provider.ConstSettingsDDF.DEFAULT_LOGIN_SERVICE_MAXSYMBOLS;
import static com.barchart.feed.ddf.settings.provider.ConstSettingsDDF.LOGIN_EXCHANGES_IDS_SEPARATOR;
import static com.barchart.feed.ddf.settings.provider.ConstSettingsDDF.LOGIN_OK;
import static com.barchart.feed.ddf.settings.provider.XmlTagSettingsDDF.LOGIN_CREDENTIALS;
import static com.barchart.feed.ddf.settings.provider.XmlTagSettingsDDF.LOGIN_EXCHANGES;
import static com.barchart.feed.ddf.settings.provider.XmlTagSettingsDDF.LOGIN_EXCHANGES_IDS;
import static com.barchart.feed.ddf.settings.provider.XmlTagSettingsDDF.LOGIN_SERVICE;
import static com.barchart.feed.ddf.settings.provider.XmlTagSettingsDDF.LOGIN_SERVICE_ID;
import static com.barchart.feed.ddf.settings.provider.XmlTagSettingsDDF.LOGIN_SERVICE_MAXSYMBOLS;
import static com.barchart.feed.ddf.settings.provider.XmlTagSettingsDDF.LOGIN_STATUS;
import static com.barchart.feed.ddf.settings.provider.XmlTagSettingsDDF.LOGIN_USERNAME;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Element;

import com.barchart.feed.ddf.settings.api.DDF_Login;
import com.barchart.feed.ddf.util.HelperXML;

// TODO: Auto-generated Javadoc
class LoginDDF implements DDF_Login {

	// ////////////////////////////////

	private final String username;
	private final String status;
	private final String credentials;
	private final String serviceId;

	private final int maxSymbols;

	private final Set<String> exchangeSet = new HashSet<String>();

	// ////////////////////////////////

	LoginDDF(final Element nodeLogin) {

		this.username = nodeLogin.getAttribute(LOGIN_USERNAME);
		this.status = nodeLogin.getAttribute(LOGIN_STATUS);
		this.credentials = nodeLogin.getAttribute(LOGIN_CREDENTIALS);

		final Element nodeService = HelperXML.xmlFirstChild(nodeLogin,
				LOGIN_SERVICE, true);
		if (nodeService == null) {
			this.serviceId = DEFAULT_LOGIN_SERVICE_ID;
			this.maxSymbols = DEFAULT_LOGIN_SERVICE_MAXSYMBOLS;
		} else {
			this.serviceId = nodeService.getAttribute(LOGIN_SERVICE_ID);
			this.maxSymbols = Integer.parseInt(nodeService
					.getAttribute(LOGIN_SERVICE_MAXSYMBOLS));
		}

		final Element nodeExchanges = HelperXML.xmlFirstChild(nodeLogin,
				LOGIN_EXCHANGES, false);
		if (nodeService == null) {
			// silent ignore
		} else {
			final String stringExchanges = nodeExchanges
					.getAttribute(LOGIN_EXCHANGES_IDS);
			final String[] arrayExchanges = stringExchanges
					.split(LOGIN_EXCHANGES_IDS_SEPARATOR);
			for (final String exchange : arrayExchanges) {
				exchangeSet.add(exchange);
			}
		}

	}

	/**
	 * Constructor which returns a LoginDDF object with no user data.
	 */
	public LoginDDF() {
		username = "";
		status = "";
		credentials = "";
		serviceId = "";
		maxSymbols = 0;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.settings.api.DDF_Login#getUsername()
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.settings.api.DDF_Login#getStatus()
	 */
	@Override
	public String getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.settings.api.DDF_Login#getCredentials()
	 */
	@Override
	public String getCredentials() {
		return credentials;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.settings.api.DDF_Login#getServiceId()
	 */
	@Override
	public String getServiceId() {
		return serviceId;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.settings.api.DDF_Login#getMaxSymbols()
	 */
	@Override
	public int getMaxSymbols() {
		return maxSymbols;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.settings.api.DDF_Login#getExchangeSet()
	 */
	@Override
	public Set<String> getExchangeSet() {
		return Collections.unmodifiableSet(exchangeSet);
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.settings.api.DDF_Login#isValid()
	 */
	@Override
	public boolean isValid() {

		if (!LOGIN_OK.equalsIgnoreCase(status)) {
			return false;
		}

		if (!LOGIN_OK.equalsIgnoreCase(credentials)) {
			return false;
		}

		if (!(maxSymbols > 0)) {
			return false;
		}

		if (exchangeSet.isEmpty()) {
			return false;
		}

		return true;

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "" + //
				"\n username    " + username + //
				"\n credentials " + credentials + //
				"\n status      " + status + //
				"\n maxSymbols  " + maxSymbols + //
				"\n exchangeSet " + exchangeSet + //
				"\n";
	}
}
