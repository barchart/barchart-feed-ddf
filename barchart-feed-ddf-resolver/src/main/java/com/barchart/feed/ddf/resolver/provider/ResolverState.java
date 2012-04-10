/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.resolver.provider;

class ResolverState {

	private boolean isOpen;

	protected boolean isOpen() {
		return isOpen;
	}

	protected boolean isClosed() {
		return !isOpen;
	}

	protected void open() {
		isOpen = true;
	}

	protected void close() {
		isOpen = false;
	}

}
