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
 * Encapsulates all necessary data on the server to which a connection is made.
 * <p>
 * 
 */
@NotMutable
public interface DDF_Server {

	/**
	 * 
	 * @return
	 */
	DDF_ServerType getServerType();

	/**
	 * 
	 * @return
	 */
	String getPrimary();

	/**
	 * 
	 * @return
	 */
	String getSecondary();

	/**
	 * 
	 * @return
	 */
	String getRecovery();

	// round robin
	/**
	 * 
	 * @return
	 */
	String getPrimaryOrSecondary();
	
	String getWss();

	/**
	 * 
	 * @return
	 */
	boolean isValid();

}