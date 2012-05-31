/**
 * 
 */
package com.barchart.feed.ddf.datalink.api;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jboss.netty.channel.ChannelFuture;

/**
 * Wrapper for a Netty ChannelFuture ******
 * 
 * @author g-litchfield
 * 
 */
public class CommandFuture implements Future<Boolean> {

	private final ChannelFuture future;

	public CommandFuture(final ChannelFuture channelFuture) {
		future = channelFuture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#cancel(boolean)
	 */
	@Override
	public boolean cancel(final boolean arg0) {
		if (future == null) {
			return true;
		}
		return future.cancel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#get()
	 */
	@Override
	public Boolean get() throws InterruptedException, ExecutionException {
		if (future == null) {
			return true;
		}
		future.await();
		return future.isSuccess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Boolean get(final long timeout, final TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (future == null) {
			return true;
		}
		future.awaitUninterruptibly(timeout, unit);
		return future.isSuccess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		if (future == null) {
			return true;
		}
		return future.isCancelled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#isDone()
	 */
	@Override
	public boolean isDone() {
		if (future == null) {
			return true;
		}
		return future.isDone();
	}

}
