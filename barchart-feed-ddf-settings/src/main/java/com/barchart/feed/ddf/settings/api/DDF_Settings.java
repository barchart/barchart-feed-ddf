/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.settings.api;

import com.barchart.feed.ddf.settings.enums.DDF_ServerType;
import com.barchart.util.common.anno.NotMutable;

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
	 * @return login authorization username
	 */
	String getAuthUser();

	/**
	 * @return login authorization password
	 */
	String getAuthPass();

	/**
	 * 
	 * @return
	 */
	DDF_Login getLogin();

	/**
	 * 
	 * @param type
	 * @return
	 */
	DDF_Server getServer(final DDF_ServerType type);

	/**
	 * 
	 * @param type
	 * @return
	 */
	boolean isValid(final DDF_ServerType type);

	/**
	 * 
	 * @return
	 */
	boolean isValidLogin();

	/**
	 * @return operation error message
	 */
	String getCommentDDF();

}