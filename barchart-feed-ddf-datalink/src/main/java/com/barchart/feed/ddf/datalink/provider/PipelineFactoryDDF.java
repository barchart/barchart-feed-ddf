/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

/**
 */
class PipelineFactoryDDF implements ChannelPipelineFactory {

	private final ChannelHandler handler;

	PipelineFactoryDDF(final ChannelHandler handler) {
		this.handler = handler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
	 */
	@Override
	public ChannelPipeline getPipeline() throws Exception {

		// TODO consider StaticChannelPipeline
		final ChannelPipeline pipeline = Channels.pipeline();

		pipeline.addFirst("execution-handler", new ExecutionHandler(
				new OrderedMemoryAwareThreadPoolExecutor(16, 1048576, 1048576)));

		// ### Decoders ###

		pipeline.addLast("ddf frame decoder", new MsgDeframerDDF());

		// remove after debug
		// pipeline.addLast("ddf logger", new LoggerDDF());

		pipeline.addLast("ddf message decoder", new MsgDecoderDDF());

		// ### Encoders ###

		pipeline.addLast("ddf command encoder", new MsgEncoderDDF());

		// ### Handler ###

		pipeline.addLast("ddf data feed client", handler);

		return pipeline;

	}
}
