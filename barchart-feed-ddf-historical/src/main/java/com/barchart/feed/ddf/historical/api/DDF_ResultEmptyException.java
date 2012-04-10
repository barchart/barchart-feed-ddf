/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.api;

@SuppressWarnings("serial")
public final class DDF_ResultEmptyException extends RuntimeException {

	public DDF_ResultEmptyException(final String message) {
		super(message);
	}

}
