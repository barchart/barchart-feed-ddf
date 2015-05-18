/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.base.sub.SubCommand;
import com.barchart.feed.ddf.datalink.api.FeedEvent;
import com.barchart.feed.ddf.datalink.api.FeedClient;
import com.barchart.feed.ddf.datalink.provider.pipeline.PipelineFactoryDDF;
import com.barchart.feed.ddf.datalink.provider.util.DummyFuture;
import com.barchart.feed.ddf.datalink.provider.util.RunnerDDF;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;

public class TCPListenerClientDDF extends SimpleChannelHandler implements FeedClient {

	/** use slf4j for internal NETTY LoggingHandler facade */
	static {
		final InternalLoggerFactory defaultFactory = new Slf4JLoggerFactory();
		InternalLoggerFactory.setDefaultFactory(defaultFactory);
	}

	private static final Logger log = LoggerFactory.getLogger(TCPListenerClientDDF.class);

	private final ServerBootstrap boot;

	private volatile DDF_MessageListener msgListener = null;

	private final Executor runner;

	private final int socketAddress;
	private final boolean filterBySub;

	private final Map<String, SubCommand> subscriptions =
			new ConcurrentHashMap<String, SubCommand>();

	TCPListenerClientDDF(final int socketAddress, final boolean filterBySub,
			final Executor executor) {

		this.socketAddress = socketAddress;
		this.filterBySub = filterBySub;
		runner = executor;

		final ChannelFactory channelFactory =
				new NioServerSocketChannelFactory(executor, executor);

		boot = new ServerBootstrap(channelFactory);

		final ChannelPipelineFactory pipelineFactory = new PipelineFactoryDDF(this);

		boot.setPipelineFactory(pipelineFactory);

	}

	private final BlockingQueue<DDF_BaseMessage> messageQueue =
			new LinkedBlockingQueue<DDF_BaseMessage>();

	private final RunnerDDF messageTask = new RunnerDDF() {

		@Override
		protected void runCore() {
			while (true) {

				try {
					final DDF_BaseMessage message = messageQueue.take();

					if (msgListener != null) {
						if (!filterBySub || filter(message)) {
							msgListener.handleMessage(message);
						}

					}
					
				} catch (final InterruptedException e) {
					log.trace("terminated");
					return;
				} catch (final Throwable e) {
					log.error("message delivery failed", e);
				}
			}
		}

	};

	private boolean filter(final DDF_BaseMessage message) {

		/* Filter by market message */
		if (!message.getMessageType().isMarketMessage) {
			return false;
		}

		final DDF_MarketBase marketMsg = (DDF_MarketBase) message;

		/* Filter by instrument */
		if (subscriptions.containsKey(marketMsg.getInstrument()
				.symbol())) {
			return true;
		}

		return false;
	}

	@Override
	public void startup() {

		log.debug("TCP Listener Client startup called");
		runner.execute(messageTask);

		boot.bind(new InetSocketAddress(socketAddress));

	}

	@Override
	public void shutdown() {

		messageTask.interrupt();

		messageQueue.clear();

	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx,
			final ExceptionEvent e) throws Exception {
		log.warn("SimpleChannelHandler caught exception {}", e.getCause().getLocalizedMessage());
		e.getCause().printStackTrace();
	}

	private void postMessage(final DDF_BaseMessage message) {
		try {
			messageQueue.put(message);
		} catch (final InterruptedException e) {
			log.trace("terminated");
		}
	}

	private void doMarket(final DDF_BaseMessage message) {
		postMessage(message);
	}

	private void doTimestamp(final DDF_BaseMessage message) {
		postMessage(message);
	}

	@Override
	public void messageReceived(final ChannelHandlerContext context,
			final MessageEvent eventIn) throws Exception {

		final Object messageIn = eventIn.getMessage();

		if (!(messageIn instanceof DDF_BaseMessage)) {
			context.sendUpstream(eventIn);
			return;
		}

		final DDF_BaseMessage message = (DDF_BaseMessage) messageIn;
		final DDF_MessageType type = message.getMessageType();

		if (type.isMarketMessage) {
			doMarket(message);
			return;
		}

		if (type.isControlTimestamp) {
			doTimestamp(message);
			return;
		}

		log.debug("unknown message : {}", message);

	}

	@Override
	public void bindMessageListener(final DDF_MessageListener msgListener) {
		this.msgListener = msgListener;
	}

	@Override
	public void bindStateListener(final Connection.Monitor stateListener) {
		// TODO Implement connection notifications for TCP listeners
	}

	@Override
	public void startUpProxy() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPolicy(FeedEvent event, EventPolicy policy) {
		/* Does nothing */
	}

	@Override
	public Future<Boolean> write(final String message) {
		return new DummyFuture();
	}
	
}
