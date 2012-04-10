/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.settings.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.settings.api.DDF_Settings;
import com.barchart.feed.ddf.settings.provider.DDF_SettingsService;

// TODO: Auto-generated Javadoc
/**
 * The Class SettingsExample.
 */

public class SettingsExample {

	private static final Logger log = LoggerFactory
			.getLogger(SettingsExample.class);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public final static void main(String[] args) {

		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		final DDF_Settings settings = DDF_SettingsService.newSettings(username,
				password);

		if (settings.isValidLogin()) {
			log.info("login success : {}", settings);
		} else {
			log.error("login failure : {}", settings);
		}

	}

}
