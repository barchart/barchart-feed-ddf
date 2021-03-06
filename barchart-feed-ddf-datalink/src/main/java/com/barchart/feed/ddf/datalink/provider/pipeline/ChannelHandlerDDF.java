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
package com.barchart.feed.ddf.datalink.provider.pipeline;

import java.util.concurrent.BlockingQueue;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.api.FeedEvent;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_ControlResponse;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.util.FeedDDF;

public class ChannelHandlerDDF extends SimpleChannelHandler {

	/** use slf4j for internal NETTY LoggingHandler facade */
	static {
		final InternalLoggerFactory defaultFactory = new Slf4JLoggerFactory();
		InternalLoggerFactory.setDefaultFactory(defaultFactory);
	}

	private static final Logger log = LoggerFactory
			.getLogger(ChannelHandlerDDF.class);

	private final BlockingQueue<FeedEvent> eventQueue;

	private final BlockingQueue<DDF_BaseMessage> messageQueue;

	public ChannelHandlerDDF(final BlockingQueue<FeedEvent> eventQueue,
			final BlockingQueue<DDF_BaseMessage> messageQueue) {

		this.eventQueue = eventQueue;
		this.messageQueue = messageQueue;

	}

	@Override
	public void channelConnected(final ChannelHandlerContext ctx,
			final ChannelStateEvent e) throws Exception {

		log.debug("Posting LINK_CONNECT");

		try {
			eventQueue.put(FeedEvent.LINK_CONNECT);
		} catch (final InterruptedException ex) {
			log.debug("terminated");
		}

		ctx.sendUpstream(e);

	}

	@Override
	public void channelDisconnected(final ChannelHandlerContext ctx,
			final ChannelStateEvent e) throws Exception {

		log.debug("channelDisconnected posting LINK_DISCONNECT");

		try {
			eventQueue.put(FeedEvent.LINK_DISCONNECT);
		} catch (final InterruptedException ex) {
			log.debug("terminated");
		}

		ctx.sendUpstream(e);

	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx,
			final ExceptionEvent e) throws Exception {

		log.warn("SimpleChannelHandler caught exception {}", e.getCause());
		//e.getCause().printStackTrace();

		/*try {
			eventQueue.put(DDF_FeedEvent.CHANNEL_CONNECT_FAILURE);
		} catch (final InterruptedException ex) {
			log.trace("terminated");
		}

		ctx.sendUpstream(e);*/

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

		try {

			if (type.isMarketMessage) {
				if (!type.isNonInstrumentMarketMessage) {
					messageQueue.put(message);
					return;
				} else {
					// Handle non instrument message
					// for now do nothing
					return;
				}
			}

			if (type.isControlTimestamp) {
				messageQueue.put(message);
				eventQueue.put(FeedEvent.HEART_BEAT);
				return;
			}

			if (type.isControlResponse) {
				messageQueue.put(message);
				doResponse(message);
				return;
			}

			log.debug("unknown message : {}", message);
		} catch (final InterruptedException e) {
			log.debug("terminated");
		}
	}

	private void doResponse(final DDF_BaseMessage message)
			throws InterruptedException {

		final DDF_MessageType type = message.getMessageType();
		final DDF_ControlResponse control = (DDF_ControlResponse) message;
		final String comment = control.getComment().toString();

		switch (type) {
		case TCP_ACCEPT:
			/* Note: This is the only place a login success is set */
			if (comment.contains(FeedDDF.RESPONSE_VERSION_SET_3) ||
					comment.contains(FeedDDF.RESPONSE_VERSION_SET_4)) {
				eventQueue.put(FeedEvent.LOGIN_SUCCESS);
			}
			break;
		case TCP_REJECT:
			if (comment.contains(FeedDDF.RESPONSE_LOGIN_FAILURE)) {
				eventQueue.put(FeedEvent.LOGIN_FAILURE);
			}
			break;
		case TCP_COMMAND:
			if (comment.contains(FeedDDF.RESPONSE_SESSION_LOCKOUT)) {
				eventQueue.put(FeedEvent.SESSION_LOCKOUT);
			}
			break;
		default:
			break;
		}

	}

}
