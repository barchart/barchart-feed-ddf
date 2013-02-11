/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.settings.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.barchart.feed.ddf.settings.api.DDF_Settings;
import com.barchart.feed.ddf.util.HelperXML;

/**
 * Provides static factory methods for building DDF_Settings objects.
 * <p>
 * This will be the primary entry point for a user wishing to initiate a login
 * to a DDF data feed.
 * 
 */
public final class DDF_SettingsService {

	private static final Logger log = LoggerFactory
			.getLogger(DDF_SettingsService.class);

	/** The Constant NULL_SETTINGS. */
	public final static DDF_Settings NULL_SETTINGS = new SettingsDDF("");

	private DDF_SettingsService() {
	}

	/**
	 * Basic static factory method for creating a DDF_Settings object.
	 * <p>
	 * Automatically pulls user settings from internal Barchart client info uri.
	 * 
	 * @param username
	 *            User's Barchart given user name.
	 * @param password
	 *            User's Barchart given password.
	 * @return DDF_Settings object instantiated with user's settings.
	 */
	public static DDF_Settings newSettings(final String username,
			final String password) {

		final String settingsURI = ConstSettingsDDF.urlCentral(username,
				password);

		System.out.println(settingsURI);
		return newSettings(settingsURI, username, password);

	}

	/**
	 * Static factory method for creating a DDF_Settings object with a user
	 * denoted uri from which to pull settings data.
	 * 
	 * @param xmlURI
	 *            URI of user settings
	 * @param username
	 *            User's Barchart given user name.
	 * @param password
	 *            User's Barchart given password.
	 * @return DDF_Settings object instantiated with user's settings.
	 */
	static final DDF_Settings newSettings(final String xmlURI,
			final String username, final String password) {

		final Element element;
		try {
			element = HelperXML.xmlDocumentDecode(xmlURI);
		} catch (final Exception e) {
			log.debug("", e);
			final String comment = e.getClass().getSimpleName() + " : "
					+ e.getMessage();
			return new SettingsDDF(comment);
		}

		return new SettingsDDF(element, username, password);

	}

	/**
	 * New settings mock valid.
	 * 
	 * @param username
	 *            User's Barchart given user name.
	 * @param password
	 *            User's Barchart given password.
	 * @return DDF_Settings object instantiated with user's settings.
	 */
	public static DDF_Settings newSettingsMockValid(final String username,
			final String password) {
		final String fileName = "ddf-settings-response-valid.xml";
		final String xmlURI = ConstSettingsDDF.class.getClassLoader()
				.getResource(fileName).toExternalForm();
		return newSettings(xmlURI, username, password);
	}

	/**
	 * New settings mock not valid.
	 * 
	 * @param username
	 *            User's Barchart given user name.
	 * @param password
	 *            User's Barchart given password.
	 * @return DDF_Settings object instantiated with user's settings.
	 */
	public static DDF_Settings newSettingsMockNotValid(final String username,
			final String password) {
		final String fileName = "ddf-settings-response-not-valid.xml";
		final String xmlURI = ConstSettingsDDF.class.getClassLoader()
				.getResource(fileName).toExternalForm();
		return newSettings(xmlURI, username, password);
	}

}
