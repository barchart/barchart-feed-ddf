/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider.pipeline;


import java.util.concurrent.Executor;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

import com.barchart.feed.ddf.datalink.provider.DDF_SocksProxy;
import com.barchart.feed.ddf.datalink.provider.FeedClientDDF;
import com.barchart.feed.ddf.datalink.provider.SocksClientHandler;

public class PipelineFactorySocks implements ChannelPipelineFactory {

	@SuppressWarnings("unused")
	private Executor exec;
	private FeedClientDDF feedClient;
	private DDF_SocksProxy proxySettings;

	public PipelineFactorySocks(Executor pipelineExecutor, FeedClientDDF feedClient, DDF_SocksProxy proxy) {
		 
		 this.exec = pipelineExecutor;
		 this.feedClient = feedClient;
		 this.proxySettings = proxy;
		 
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {

		final ChannelPipeline pipeline = Channels.pipeline();

		pipeline.addLast("socks5handler", new SocksClientHandler(feedClient, proxySettings));
		
		// remove after debug
		//pipeline.addLast("ddf logger", new LoggerDDF());
		
		return pipeline;
	}

}
