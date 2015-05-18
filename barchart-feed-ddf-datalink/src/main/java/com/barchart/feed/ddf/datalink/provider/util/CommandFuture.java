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

import org.jboss.netty.channel.ChannelFuture;

/**
 * Wrapper for a Netty ChannelFuture.
 * 
 */
public class CommandFuture implements Future<Boolean> {

	private final ChannelFuture future;

	public CommandFuture(final ChannelFuture channelFuture) {

		if (channelFuture == null) {
			throw new NullPointerException("ChannelFuture was null");
		}
		future = channelFuture;
	}

	@Override
	public boolean cancel(final boolean arg0) {
		return future.cancel();
	}

	@Override
	public Boolean get() throws InterruptedException, ExecutionException {
		future.await();
		return future.isSuccess();
	}

	@Override
	public Boolean get(final long timeout, final TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		future.awaitUninterruptibly(timeout, unit);
		return future.isSuccess();
	}

	@Override
	public boolean isCancelled() {
		return future.isCancelled();
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}

}
