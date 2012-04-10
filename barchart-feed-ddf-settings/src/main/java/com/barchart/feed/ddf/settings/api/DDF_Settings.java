/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.settings.api;

import com.barchart.feed.ddf.settings.enums.DDF_ServerType;
import com.barchart.util.anno.NotMutable;

/**
 * 
 * Encapsulates all necessary data for a DDF login including login and server
 * settings.
 * <p>
 * User will create a DDF_Settings object using one of the static factory
 * methods in DDF_SettingsService.
 * 
 **/
@NotMutable
public interface DDF_Settings {

	/**
	 * login authorization username
	 * */
	String getAuthUser();

	/** login authorization password */
	String getAuthPass();

	DDF_Login getLogin();

	DDF_Server getServer(final DDF_ServerType type);

	boolean isValid(final DDF_ServerType type);

	boolean isValidLogin();

	/** operation error message */
	String getCommentDDF();

}