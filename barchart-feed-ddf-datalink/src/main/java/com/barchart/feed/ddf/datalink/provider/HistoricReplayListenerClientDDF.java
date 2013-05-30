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

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.FixedReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.consumer.connection.Subscription;
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

public class HistoricReplayListenerClientDDF extends SimpleChannelHandler implements
		DDF_FeedClientBase {

	/** use slf4j for internal NETTY LoggingHandler facade */
	static {
		final InternalLoggerFactory defaultFactory = new Slf4JLoggerFactory();
		InternalLoggerFactory.setDefaultFactory(defaultFactory);
	}

	private static final Logger log = LoggerFactory
			.getLogger(HistoricReplayListenerClientDDF.class);

	private final ConnectionlessBootstrap boot;

	private DatagramChannel channel;

	private volatile DDF_MessageListener msgListener = null;

	private final Executor runner;

	private final int socketAddress;

	private final Map<String, Subscription> subscriptions = 
			new ConcurrentHashMap<String, Subscription>();
	
	HistoricReplayListenerClientDDF(final int socketAddress, final Executor executor) {

		this.socketAddress = socketAddress;
		runner = executor;

		final DatagramChannelFactory channelFactory = new NioDatagramChannelFactory(
				runner);

		boot = new ConnectionlessBootstrap(channelFactory);

		final ChannelPipelineFactory pipelineFactory = new PipelineFactoryDDF(
				this);

		boot.setPipelineFactory(pipelineFactory);

		boot.setOption("broadcast", "false");
		boot.setOption("receiveBufferSizePredictorFactory", //
				new FixedReceiveBufferSizePredictorFactory(2 * 1024));

	}

	private final BlockingQueue<DDF_BaseMessage> messageQueue = new LinkedBlockingQueue<DDF_BaseMessage>();

	private final RunnerDDF messageTask = new RunnerDDF() {

		@Override
		protected void runCore() {
			while (true) {
				try {
					final DDF_BaseMessage message = messageQueue.take();

					if (msgListener != null && filter(message)) {
						msgListener.handleMessage(message);
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
		if(!message.getMessageType().isMarketMessage) {
			return false;
		}
		
		final DDF_MarketBase marketMsg = (DDF_MarketBase) message;
		
		/* Filter by instrument */
		if(subscriptions.containsKey(marketMsg.getInstrument().get(
				InstrumentField.SYMBOL).toString())) {
			
			return true;
		}
		
		return false;
	}

	@Override
	public void startup() {

		runner.execute(messageTask);

		boot.bind(new InetSocketAddress(socketAddress));

	}

	@Override
	public void shutdown() {

		//In FeedClientDDF, subscriptions are cleared on logout, do we want to do this here?
		
		messageTask.interrupt();

		messageQueue.clear();

		if (channel != null) {
			channel.close();
			channel = null;
		}

	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx,
			final ExceptionEvent e) throws Exception {
		log.warn("SimpleChannelHandler caught exception");

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
	public Future<Boolean> subscribe(Set<Subscription<?>> subs) {
		
		if (subs == null) {
			log.error("Null subscribes request recieved");
			return new FailedFuture();
		}
		
		for (final Subscription sub : subs) {

			if (sub != null) {
				
				final String inst = sub.interestName();
				
				/* If we're subscribed already, add new interests, otherwise add */
				if(subscriptions.containsKey(inst)) {
					subscriptions.get(inst).addTypes(sub.types());
				} else {
					subscriptions.put(inst, sub);
				}
				
			}
		}
		
		return new DummyFuture();
	}

	@Override
	public Future<Boolean> subscribe(Subscription sub) {
		
		if (sub == null) {
			log.error("Null subscribe request recieved");
			return new FailedFuture();
		}
		
		final String inst = sub.interestName();
		if(subscriptions.containsKey(inst)) {
			subscriptions.get(inst).addTypes(sub.types());
		} else {
			subscriptions.put(inst, sub);
		}
		
		return new DummyFuture();
	}
	
	// Unsubscribe is somewhat ambiguous, there is shared behavior between
	// the registration and unregistration of instruments in the 
	// market maker and the feed.  
	@Override
	public Future<Boolean> unsubscribe(Set<Subscription<?>> subs) {
		
		if (subs == null) {
			log.error("Null subscribes request recieved");
			return new FailedFuture();
		}
		
		for (final Subscription sub : subs) {

			if (sub != null) {
				subscriptions.remove(sub.interestName());
			}
		}
		
		return new DummyFuture();
	}
	
	@Override
	public Future<Boolean> unsubscribe(Subscription sub) {
		
		if (sub == null) {
			log.error("Null subscribe request recieved");
			return new FailedFuture();
		}
		
		subscriptions.remove(sub.interestName());
		
		return new DummyFuture();
	}
	
	@Override
	public void setPolicy(DDF_FeedEvent event, EventPolicy policy) {
		// Does nothing for now, some functionality will be added
		// for tcp listeners
	}
	
}
