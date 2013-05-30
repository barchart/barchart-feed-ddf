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
import java.util.Set;
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

import com.barchart.feed.client.api.FeedStateListener;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClientBase;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.datalink.api.DummyFuture;
import com.barchart.feed.ddf.datalink.api.EventPolicy;
import com.barchart.feed.ddf.datalink.api.FailedFuture;
import com.barchart.feed.ddf.datalink.enums.DDF_FeedEvent;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.inst.InstrumentField;

public class TCPListenerClientDDF extends SimpleChannelHandler implements
		DDF_FeedClientBase {

	/** use slf4j for internal NETTY LoggingHandler facade */
	static {
		final InternalLoggerFactory defaultFactory = new Slf4JLoggerFactory();
		InternalLoggerFactory.setDefaultFactory(defaultFactory);
	}

	private static final Logger log = LoggerFactory
			.getLogger(TCPListenerClientDDF.class);

	private final ServerBootstrap boot;

	private volatile DDF_MessageListener msgListener = null;

	private final Executor runner;

	private final int socketAddress;
	private final boolean filterBySub;

	private final Map<String, DDF_Subscription> subscriptions =
			new ConcurrentHashMap<String, DDF_Subscription>();

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
				.get(InstrumentField.SYMBOL).toString())) {
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
	public void bindStateListener(final FeedStateListener stateListener) {
		// TODO Implement connection notifications for TCP listeners
	}

	@Override
	public void startUpProxy() {
		// TODO Auto-generated method stub

	}

	@Override
	public Future<Boolean> subscribe(final Set<DDF_Subscription> subs) {

		if (subs == null) {
			log.error("Null subscribes request recieved");
			return new FailedFuture();
		}

		for (final DDF_Subscription sub : subs) {

			if (sub != null) {

				final String inst = sub.getInstrument();

				/* If we're subscribed already, add new interests, otherwise add */
				if (subscriptions.containsKey(inst)) {
					subscriptions.get(inst).addInterests(sub.getInterests());
				} else {
					subscriptions.put(inst, sub);
				}

			}
		}

		return new DummyFuture();
	}

	@Override
	public Future<Boolean> subscribe(final DDF_Subscription sub) {

		if (sub == null) {
			log.error("Null subscribe request recieved");
			return new FailedFuture();
		}

		log.debug("Subscription registered {}", sub.toString());

		final String inst = sub.getInstrument();
		if (subscriptions.containsKey(inst)) {
			subscriptions.get(inst).addInterests(sub.getInterests());
		} else {
			subscriptions.put(inst, sub);
		}

		return new DummyFuture();
	}

	// Unsubscribe is somewhat ambiguous, there is shared behavior between
	// the registration and unregistration of instruments in the
	// market maker and the feed.
	@Override
	public Future<Boolean> unsubscribe(final Set<DDF_Subscription> subs) {

		if (subs == null) {
			log.error("Null subscribes request recieved");
			return new FailedFuture();
		}

		for (final DDF_Subscription sub : subs) {

			if (sub != null) {
				subscriptions.remove(sub.getInstrument());
			}
		}

		return new DummyFuture();
	}

	@Override
	public Future<Boolean> unsubscribe(final DDF_Subscription sub) {

		if (sub == null) {
			log.error("Null subscribe request recieved");
			return new FailedFuture();
		}

		subscriptions.remove(sub.getInstrument());

		return new DummyFuture();
	}

	@Override
	public void setPolicy(final DDF_FeedEvent event, final EventPolicy policy) {
		// Does nothing for now, some functionality will be added
		// for tcp listeners
	}

}
