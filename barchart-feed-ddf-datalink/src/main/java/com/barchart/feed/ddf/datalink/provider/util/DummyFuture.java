/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/**
 * 
 */
package com.barchart.feed.ddf.datalink.provider.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Future that is immediately completed. Used when an asynchronous request is
 * sent to the feed client but the client is not logged in.
 */
public class DummyFuture implements Future<Boolean> {

	@Override
	public boolean cancel(final boolean mayInterruptIfRunning) {
		return true;
	}

	@Override
	public Boolean get() throws InterruptedException, ExecutionException {
		return true;
	}

	@Override
	public Boolean get(final long timeout, final TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return true;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}

}
