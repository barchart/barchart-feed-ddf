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
 * Encapsulates all necessary data on the server to which a connection is made.
 * 
 * @author g-litchfield
 * 
 */
@NotMutable
public interface DDF_Server {

	DDF_ServerType getServerType();

	String getPrimary();

	String getSecondary();

	String getRecovery();

	// round robin
	String getPrimaryOrSecondary();

	boolean isValid();

}