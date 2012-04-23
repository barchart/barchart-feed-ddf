/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.datalink.api.DDF_FeedHandler;
import com.barchart.feed.ddf.datalink.enums.DDF_FeedEvent;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_ControlResponse;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.settings.api.DDF_Server;
import com.barchart.feed.ddf.settings.api.DDF_Settings;
import com.barchart.feed.ddf.settings.enums.DDF_ServerType;
import com.barchart.feed.ddf.settings.provider.DDF_SettingsService;
import com.barchart.feed.ddf.util.FeedDDF;

class FeedClientDDF extends SimpleChannelHandler implements DDF_FeedClient {

	/** use slf4j for internal NETTY LoggingHandler facade */
	static {
		final InternalLoggerFactory defaultFactory = new Slf4JLoggerFactory();
		InternalLoggerFactory.setDefaultFactory(defaultFactory);
	}

	private static final Logger log = LoggerFactory
			.getLogger(FeedClientDDF.class);

	/** channel operation time out */
	private static final long TIMEOUT = 2 * 1000;
	private static final String TIMEOUT_OPTION = "connectTimeoutMillis";

	//

	private final DDF_ServerType _serverType;
	private final Executor runner;

	FeedClientDDF(final DDF_ServerType serverType, final Executor executor) {

		_serverType = serverType;
		this.runner = executor;

		final ChannelFactory channelFactory = new NioClientSocketChannelFactory(
				runner, runner);

		boot = new ClientBootstrap(channelFactory);

		final ChannelPipelineFactory pipelineFactory = new PipelineFactoryDDF(
				this);

		boot.setPipelineFactory(pipelineFactory);

		boot.setOption(TIMEOUT_OPTION, TIMEOUT);

	}

	private final BlockingQueue<DDF_FeedEvent> eventQueue = new LinkedBlockingQueue<DDF_FeedEvent>();

	private final BlockingQueue<DDF_BaseMessage> messageQueue = new LinkedBlockingQueue<DDF_BaseMessage>();

	private final BlockingQueue<CharSequence> commandQueue = new LinkedBlockingQueue<CharSequence>();

	private final RunnerDDF eventTask = new RunnerDDF() {
		@Override
		protected void runCore() {
			while (true) {
				try {
					final DDF_FeedEvent event = eventQueue.take();
					if (isActive()) {
						handler.handleEvent(event);
					}
				} catch (final InterruptedException e) {
					log.trace("terminated");
					return;
				} catch (final Throwable e) {
					log.error("event delivery failed", e);
				}
			}
		}
	};

	private final RunnerDDF messageTask = new RunnerDDF() {
		@Override
		protected void runCore() {
			while (true) {
				try {
					final DDF_BaseMessage message = messageQueue.take();
					if (isActive()) {
						handler.handleMessage(message);
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

	private final RunnerDDF commandTask = new RunnerDDF() {
		@Override
		protected void runCore() {
			while (true) {
				try {
					final CharSequence command = commandQueue.take();
					if (isConnected()) {
						send(command);
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

	private void postEvent(final DDF_FeedEvent event) {
		try {
			eventQueue.put(event);
		} catch (final InterruptedException e) {
			log.trace("terminated");
		}
	}

	private void postMessage(final DDF_BaseMessage message) {
		try {
			messageQueue.put(message);
		} catch (final InterruptedException e) {
			log.trace("terminated");
		}
	}

	//

	private final ClientBootstrap boot;

	private Channel channel;

	private void initialize() {

		runner.execute(eventTask);
		runner.execute(messageTask);
		runner.execute(commandTask);

	}

	private void terminate() {

		eventTask.interrupt();
		messageTask.interrupt();
		commandTask.interrupt();

		eventQueue.clear();
		messageQueue.clear();
		commandQueue.clear();

		if (channel != null) {
			channel.close();
			channel = null;
		}

	}

	private DDF_FeedEvent login(final String host, final int port,
			final String username, final String password) {

		final InetSocketAddress address = new InetSocketAddress(host, port);

		final ChannelFuture futureConnect = boot.connect(address);

		channel = futureConnect.getChannel();

		futureConnect.awaitUninterruptibly();

		if (!futureConnect.isDone()) {
			log.warn("channel connect timeout; {}:{} ", host, port);
			return DDF_FeedEvent.CHANNEL_CONNECT_TIMEOUT;
		}

		if (!futureConnect.isSuccess()) {
			log.warn("channel connect unsuccessful; {}:{} ", host, port);
			return DDF_FeedEvent.CHANNEL_CONNECT_FAILURE;
		}

		final boolean isLoginSent = true && //
				send(FeedDDF.tcpLogin(username, password)) && //
				send(FeedDDF.tcpVersion(FeedDDF.VERSION_3)) && //
				send(FeedDDF.tcpGo(FeedDDF.SYMBOL_TIMESTAMP)) && //
				true;

		if (!isLoginSent) {
			log.warn("login startup timeout; {}:{} ", host, port);
			return DDF_FeedEvent.LOGIN_FAILURE;
		}

		return DDF_FeedEvent.LOGIN_SUCCESS;

	}

	/**
	 * Can post to the FeedEventHandler the following events:
	 * <p>
	 * CHANNEL_CONNECT_TIMEOUT {@link DDF_FeedEvent.CHANNEL_CONNECT_TIMEOUT}
	 * <p>
	 * CHANNEL_CONNECT_FAILURE {@link DDF_FeedEvent.CHANNEL_CONNECT_FAILURE}
	 * <p>
	 * SETTINGS_RETRIEVAL_FAILURE
	 * {@link DDF_FeedEvent.SETTINGS_RETRIEVAL_FAILURE}
	 * <p>
	 * LOGIN_FAILURE {@link DDF_FeedEvent.LOGIN_FAILURE}
	 * <p>
	 * LOGIN_SUCCESS {@link DDF_FeedEvent.LOGIN_SUCCESS}
	 */
	@Override
	public synchronized void login(final String username, final String password) {

		terminate();

		initialize();

		final DDF_Settings settings = DDF_SettingsService.newSettings(username,
				password);

		if (!settings.isValidLogin()) {
			postEvent(DDF_FeedEvent.SETTINGS_RETRIEVAL_FAILURE);
		}

		final DDF_Server server = settings.getServer(_serverType);
		final String primary = server.getPrimary();
		final String secondary = server.getSecondary();
		final int port = 7500;

		final DDF_FeedEvent eventOne = login(primary, port, username, password);

		if (eventOne == DDF_FeedEvent.LOGIN_SUCCESS) {
			postEvent(DDF_FeedEvent.LOGIN_SUCCESS);
			return;
		}

		final DDF_FeedEvent eventTwo = login(secondary, port, username,
				password);

		if (eventTwo == DDF_FeedEvent.LOGIN_SUCCESS) {
			postEvent(DDF_FeedEvent.LOGIN_SUCCESS);
			return;
		}

		// For simplicity, we only return the error message from the primary
		// server in the event both logins fail.
		postEvent(eventOne);

	}

	@Override
	public synchronized void logout() {

		postEvent(DDF_FeedEvent.LOGOUT);

		send(FeedDDF.tcpLogout());

		terminate();

	}

	private boolean isConnected() {
		if (channel == null) {
			return false;
		}
		return channel.isConnected();
	}

	@Override
	public boolean send(final CharSequence command) {

		if (command == null) {
			log.error("command == null", new Exception());
			return false;
		}

		if (!isConnected()) {
			log.error("!isConnected()");
			return false;
		}

		final ChannelFuture futureWrite = channel.write(command);

		final boolean isWritten = futureWrite.awaitUninterruptibly(TIMEOUT);

		return isWritten;

	}

	@Override
	public boolean post(final CharSequence command) {

		if (command == null) {
			log.error("command == null", new Exception());
			return false;
		}

		if (!isConnected()) {
			return false;
		}

		try {
			commandQueue.put(command);
		} catch (final InterruptedException e) {
			log.trace("interrupted");
		}

		return true;

	}

	private DDF_FeedHandler handler;

	@Override
	public synchronized void bind(final DDF_FeedHandler handler) {
		this.handler = handler;
	}

	@Override
	public void channelConnected(final ChannelHandlerContext ctx,
			final ChannelStateEvent e) throws Exception {

		postEvent(DDF_FeedEvent.LINK_CONNECT);

		ctx.sendUpstream(e);

	}

	@Override
	public void channelDisconnected(final ChannelHandlerContext ctx,
			final ChannelStateEvent e) throws Exception {

		postEvent(DDF_FeedEvent.LINK_DISCONNECT);

		ctx.sendUpstream(e);

	}

	private void doMarket(final DDF_BaseMessage message) {

		postMessage(message);

	}

	private void doResponse(final DDF_BaseMessage message) {

		postMessage(message);

		final DDF_MessageType type = message.getMessageType();
		final DDF_ControlResponse control = (DDF_ControlResponse) message;
		final String comment = control.getComment().toString();

		switch (type) {
		case TCP_ACCEPT:
			if (comment.contains(FeedDDF.RESPONSE_LOGIN_SUCCESS)) {
				postEvent(DDF_FeedEvent.LOGIN_SUCCESS);
			}
			break;
		case TCP_REJECT:
			if (comment.contains(FeedDDF.RESPONSE_LOGIN_FAILURE)) {
				postEvent(DDF_FeedEvent.LOGIN_FAILURE);
			}
			break;
		case TCP_COMMAND:
			if (comment.contains(FeedDDF.RESPONSE_SESSION_LOCKOUT)) {
				postEvent(DDF_FeedEvent.SESSION_LOCKOUT);
			}
			break;
		default:
			break;
		}

	}

	private void doTimestamp(final DDF_BaseMessage message) {

		postMessage(message);

		postEvent(DDF_FeedEvent.HEART_BEAT);

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

		if (type.isControlResponse) {
			doResponse(message);
			return;
		}

		log.debug("unknown message : {}", message);

	}

	private boolean isActive() {

		if (handler == null) {
			log.error("handler == null");
			return false;
		}

		return true;

	}

}
