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
package com.barchart.feed.ddf.datalink.provider;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

/**
 * @author g-litchfield
 * 
 */
public class TestDataProducer extends SimpleChannelHandler {

	Channel channel;

	final Executor runner = new Executor() {

		private final AtomicLong counter = new AtomicLong(0);

		final String name = "# DDF Feed Client - " + counter.getAndIncrement();

		@Override
		public void execute(final Runnable task) {
			new Thread(task, name).start();
		}

	};

	final ChannelFactory channelFactory = new NioDatagramChannelFactory(runner);

}
