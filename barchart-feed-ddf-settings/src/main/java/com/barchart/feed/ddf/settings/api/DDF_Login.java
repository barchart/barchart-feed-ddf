/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.settings.api;

import java.util.Set;

import com.barchart.util.anno.NotMutable;

/**
 * Encapsulates all necessary data for a user login.
 * 
 * @author g-litchfield
 * 
 */
@NotMutable
public interface DDF_Login {

	/**
	 * 
	 * @return The Barchart given user name used to populate login data.
	 */
	String getUsername();

	/**
	 * Call to determine the status of a login attempt.
	 * 
	 * @return String "ok" if user name is valid.
	 */
	String getStatus();

	/**
	 * Call to determine the status of a login attempt.
	 * 
	 * @return String "ok" if both user name and password are valid.
	 */
	String getCredentials();

	/**
	 * 
	 * @return
	 */
	String getServiceId();

	/**
	 * 
	 * @return The maximum number of symbols the user is allowed to listen to on
	 *         this connection.
	 */
	int getMaxSymbols();

	/**
	 * 
	 * @return The set of exchanges available to the user from this connection.
	 */
	Set<String> getExchangeSet();

	/**
	 * 
	 * @return
	 */
	boolean isValid();

}