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

import static com.barchart.feed.ddf.settings.provider.XmlTagSettingsDDF.LOGIN;
import static com.barchart.feed.ddf.settings.provider.XmlTagSettingsDDF.SERVERS;
import static com.barchart.feed.ddf.settings.provider.XmlTagSettingsDDF.SERVERS_SERVER;

import java.util.EnumMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.barchart.feed.ddf.settings.api.DDF_Server;
import com.barchart.feed.ddf.settings.api.DDF_Settings;
import com.barchart.feed.ddf.settings.enums.DDF_ServerType;
import com.barchart.feed.ddf.util.HelperXML;

class SettingsDDF implements DDF_Settings {

	private static final Logger log = LoggerFactory
			.getLogger(SettingsDDF.class);

	private final LoginDDF login;

	private final Map<DDF_ServerType, ServerDDF> serverMap = //
	new EnumMap<DDF_ServerType, ServerDDF>(DDF_ServerType.class);

	private final String username;
	private final String password;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.settings.api.DDF_Settings#getAuthUser()
	 */
	@Override
	public final String getAuthUser() {
		return username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.settings.api.DDF_Settings#getAuthPass()
	 */
	@Override
	public String getAuthPass() {
		return password;
	}

	SettingsDDF(final String comment) {

		this.comment = comment;

		this.username = "";
		this.password = "";

		this.login = new LoginDDF();

	}

	SettingsDDF(final Element root, final String username, final String password) {

		this.username = username;
		this.password = password;

		final Element nodeLogin = HelperXML.xmlFirstChild(root, LOGIN, true);

		this.login = new LoginDDF(nodeLogin);

		//

		final Element nodeServerList = HelperXML.xmlFirstChild(root, SERVERS,
				true);

		final NodeList nodeList = nodeServerList.getChildNodes();

		final int sizeServerList = nodeList.getLength();

		for (int index = 0; index < sizeServerList; index++) {

			final Node nodeIndex = nodeList.item(index);

			if (nodeIndex.getNodeType() == Node.ELEMENT_NODE) {

				final Element nodeServer = (Element) nodeIndex;

				final String name = nodeServer.getNodeName();

				if (name.equalsIgnoreCase(SERVERS_SERVER)) {
					final ServerDDF server = new ServerDDF(nodeServer);
					serverMap.put(server.getServerType(), server);
				} else {
					log.warn("unexpected node name={}", name);
				}

			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.settings.api.DDF_Settings#getLogin()
	 */
	@Override
	public LoginDDF getLogin() {
		return login;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.settings.api.DDF_Settings#getServer(com.barchart
	 * .feed.ddf.settings.enums.DDF_ServerType)
	 */
	@Override
	public DDF_Server getServer(final DDF_ServerType type) {
		return serverMap.get(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "" + //
				"\n Login:" + //
				"\n " + login + //
				"\n Servers:" + //
				"\n " + serverMap + //
				"";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.settings.api.DDF_Settings#isValid(com.barchart.
	 * feed.ddf.settings.enums.DDF_ServerType)
	 */
	@Override
	public boolean isValid(final DDF_ServerType type) {
		if (!login.isValid()) {
			return false;
		}
		if (type == null) {
			return false;
		}
		final DDF_Server server = serverMap.get(type);
		if (server == null || !server.isValid()) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.settings.api.DDF_Settings#isValidLogin()
	 */
	@Override
	public boolean isValidLogin() {
		return login.isValid();
	}

	private String comment = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.settings.api.DDF_Settings#getCommentDDF()
	 */
	@Override
	public String getCommentDDF() {
		return comment;
	}

}
