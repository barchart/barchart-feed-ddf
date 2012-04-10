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

import static com.barchart.feed.ddf.settings.provider.XmlTagSettingsDDF.*;

import org.w3c.dom.Element;

import com.barchart.feed.ddf.settings.api.DDF_Server;
import com.barchart.feed.ddf.settings.enums.DDF_ServerType;

// TODO: Auto-generated Javadoc
class ServerDDF implements DDF_Server {

	private final DDF_ServerType type;

	private final String primary;

	private final String secondary;

	private final String recovery;

	ServerDDF(final Element nodeServer) {

		this.type = DDF_ServerType.fromCode(nodeServer
				.getAttribute(SERVERS_SERVER_TYPE));
		this.primary = nodeServer.getAttribute(SERVERS_SERVER_PRIMARY);
		this.secondary = nodeServer.getAttribute(SERVERS_SERVER_SECONDARY);
		this.recovery = nodeServer.getAttribute(SERVERS_SERVER_RECOVERY);

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.settings.api.DDF_Server#getServerType()
	 */
	public DDF_ServerType getServerType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.settings.api.DDF_Server#getPrimary()
	 */
	public String getPrimary() {
		return primary;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.settings.api.DDF_Server#getSecondary()
	 */
	public String getSecondary() {
		return secondary;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.settings.api.DDF_Server#getRecovery()
	 */
	public String getRecovery() {
		return recovery;
	}

	private boolean isPrimary = true;

	/**
	 * round robin; non null.
	 *
	 * @return the primary or secondary
	 */
	public synchronized String getPrimaryOrSecondary() {

		final boolean isValidPrimary = isValid(primary);
		final boolean isValidSecondary = isValid(secondary);

		if (!isValidPrimary && !isValidSecondary) {
			return "";
		}

		if (isValidPrimary && !isValidSecondary) {
			return primary;
		}

		if (!isValidPrimary && isValidSecondary) {
			return secondary;
		}

		// both are valid

		final String choice;
		if (isPrimary) {
			choice = primary;
		} else {
			choice = secondary;
		}
		isPrimary = !isPrimary;
		return choice;

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.settings.api.DDF_Server#isValid()
	 */
	@Override
	public final boolean isValid() {
		return isValid(primary) && isValid(secondary);
	}

	private final boolean isValid(final String server) {
		if (server == null || server.length() == 0) {
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
				"\n type      " + type + //
				"\n primary   " + primary + //
				"\n secondary " + secondary + //
				"\n recovery  " + recovery + //
				"\n";
	}

}
