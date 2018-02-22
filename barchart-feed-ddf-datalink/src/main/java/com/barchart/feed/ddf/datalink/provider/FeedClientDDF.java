/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.model.meta.id.MetadataID;
import com.barchart.feed.base.sub.SubCommand;
import com.barchart.feed.ddf.datalink.api.FeedClient;
import com.barchart.feed.ddf.datalink.api.FeedEvent;
import com.barchart.feed.ddf.datalink.provider.pipeline.ChannelHandlerDDF;
import com.barchart.feed.ddf.datalink.provider.pipeline.MsgDecoderDDF;
import com.barchart.feed.ddf.datalink.provider.pipeline.MsgDeframerDDF;
import com.barchart.feed.ddf.datalink.provider.pipeline.MsgEncoderDDF;
import com.barchart.feed.ddf.datalink.provider.pipeline.PipelineFactoryDDF;
import com.barchart.feed.ddf.datalink.provider.pipeline.PipelineFactorySocks;
import com.barchart.feed.ddf.datalink.provider.pipeline.PipelineFactoryWebSocket;
import com.barchart.feed.ddf.datalink.provider.util.CommandFuture;
import com.barchart.feed.ddf.datalink.provider.util.RunnerDDF;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_ControlTimestamp;
import com.barchart.feed.ddf.settings.api.DDF_Server;
import com.barchart.feed.ddf.settings.api.DDF_Settings;
import com.barchart.feed.ddf.settings.enums.DDF_ServerType;
import com.barchart.feed.ddf.settings.provider.DDF_SettingsService;
import com.barchart.feed.ddf.util.ClockDDF;
import com.barchart.feed.ddf.util.FeedDDF;

public class FeedClientDDF implements FeedClient {

	/* XXX TEMPY for testing */
	public static boolean isWebSocket = false;
	public static String WEBSOCKET_EP = "ws://qsws-us-e-02.aws.barchart.com:80/jerq";

	private static final String VERSION = FeedDDF.VERSION_4;
	private static final int PORT = 7500;
	
	public static String CUSTOM_HOST = null;
	public static int CUSTOM_PORT = Integer.MAX_VALUE;

	private static final int DEFAULT_IO_THREADS = Runtime.getRuntime().availableProcessors() * 2;

	/** use slf4j for internal NETTY LoggingHandler facade */
	static {
		final InternalLoggerFactory defaultFactory = new Slf4JLoggerFactory();
		InternalLoggerFactory.setDefaultFactory(defaultFactory);
	}

	private static final Logger log = LoggerFactory.getLogger(FeedClientDDF.class);

	/** channel operation time out */
	private static final long TIMEOUT = 2 * 1000;
	private static final String TIMEOUT_OPTION = "connectTimeoutMillis";
	private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

	private static final long HEARTBEAT_TIMEOUT = 30 * 1000;

	//

	private final Map<FeedEvent, EventPolicy> eventPolicy = new ConcurrentHashMap<FeedEvent, EventPolicy>();

	private final Map<MetadataID<?>, SubCommand> subscriptions = new ConcurrentHashMap<MetadataID<?>, SubCommand>();

	//

	private final LoginHandler loginHandler = new LoginHandler();

	//

	private final BlockingQueue<FeedEvent> eventQueue = new LinkedBlockingQueue<FeedEvent>();

	private final BlockingQueue<DDF_BaseMessage> messageQueue = new LinkedBlockingQueue<DDF_BaseMessage>();

	private final AtomicLong lastHeartbeat = new AtomicLong(0);

	//

	private volatile DDF_MessageListener msgListener = null;

	private final CopyOnWriteArrayList<Connection.Monitor> feedListeners = new CopyOnWriteArrayList<Connection.Monitor>();

	//

	private ClientBootstrap boot;

	private ChannelFactory channelFactory;
	private Channel channel;
	private HashedWheelTimer timer = null;

	//

	private String username;
	private String password;
	private final DDF_ServerType serverType = DDF_ServerType.STREAM;
	private Executor executor;

	// SOCKS5

	private DDF_SocksProxy proxySettings = null;
	private final BlockingQueue<Boolean> socksConnectResult = new LinkedBlockingQueue<Boolean>();

	//

	FeedClientDDF(final String username, final String password, final Executor executor) {

		startup(username, password, executor, null, false);

	}

	public FeedClientDDF(String username, String password, Executor executor, DDF_SocksProxy proxySettings) {

		startup(username, password, executor, proxySettings, false);

	}

	public FeedClientDDF(String username, String password, Executor executor, DDF_SocksProxy proxySettings,
			boolean isMobile) {

		startup(username, password, executor, proxySettings, isMobile);

	}

	private void startup(final String username, final String password, final Executor exec, final DDF_SocksProxy proxy,
			final Boolean isMobile) {

		this.username = username;
		this.password = password;
		this.executor = exec;

		this.proxySettings = proxy;

		timer = new HashedWheelTimer();

		if (isMobile) {
			channelFactory = new NioClientSocketChannelFactory(executor, executor);
		} else { /* Android hates this constructor */
			channelFactory = new NioClientSocketChannelFactory(executor, 1, new NioWorkerPool(executor,
					DEFAULT_IO_THREADS), timer);
		}

		boot = new ClientBootstrap(channelFactory);

		initBoot();

		boot.setOption(TIMEOUT_OPTION, TIMEOUT);

		/* Initialize event policy with event policies that do nothing. */
		for (final FeedEvent event : FeedEvent.values()) {
			eventPolicy.put(event, new EventPolicy() {

				@Override
				public void newEvent(FeedEvent event) {
					/* Do nothing */
				}
			});
		}

		/* Add DefaultReloginPolicy to selected events */
		eventPolicy.put(FeedEvent.LOGIN_FAILURE, reconnectionPolicy);

		eventPolicy.put(FeedEvent.LINK_DISCONNECT, reconnectionPolicy);

		eventPolicy.put(FeedEvent.SETTINGS_RETRIEVAL_FAILURE, reconnectionPolicy);

		eventPolicy.put(FeedEvent.CHANNEL_CONNECT_FAILURE, reconnectionPolicy);
		eventPolicy.put(FeedEvent.CHANNEL_CONNECT_TIMEOUT, reconnectionPolicy);
		eventPolicy.put(FeedEvent.LINK_CONNECT_PROXY_TIMEOUT, reconnectionPolicy);

		/* Add HeartbeatPolicy to HEART_BEAT */
		eventPolicy.put(FeedEvent.HEART_BEAT, new HeartbeatPolicy());

	}

	public void setProxySettings(final DDF_SocksProxy proxy) {
		this.proxySettings = proxy;

		hardRestart("setProxySettings: " + proxy.getProxyAddress() + ":" + proxy.getProxyPort());
	}

	public void clearProxySettings() {
		this.proxySettings = null;

		hardRestart("clearProxySettings()");
	}

	private void initBoot() {

		final SimpleChannelHandler ddfHandler = new ChannelHandlerDDF(eventQueue, messageQueue);

		if (proxySettings != null) {

			final ChannelPipelineFactory socksPipelineFactory = new PipelineFactorySocks(executor, this, proxySettings);

			boot.setPipelineFactory(socksPipelineFactory);
			boot.setOption("child.tcpNoDelay", true);
			boot.setOption("child.keepAlive", true);
			boot.setOption("child.reuseAddress", true);
			boot.setOption("readWriteFair", true);

		} else if (isWebSocket) {

			try {
				final URI u = new URI(WEBSOCKET_EP);
				final ChannelPipelineFactory wsFactory = new PipelineFactoryWebSocket(executor, this, ddfHandler, u);

				boot.setPipelineFactory(wsFactory);
			} catch (Exception e) {
				log.error("failed to boot WebSocket Pipeline", e);
			}

		} else {

			final ChannelPipelineFactory pipelineFactory = new PipelineFactoryDDF(ddfHandler);

			boot.setPipelineFactory(pipelineFactory);

		}
	}

	private final DefaultReloginPolicy reconnectionPolicy = new DefaultReloginPolicy();

	private boolean loginProxy(String username, String password, DDF_Server feedServers) {

		terminate();

		initialize();

		// do socks connection

		log.debug("connect to proxy - address {} port {}", proxySettings.getProxyAddress(),
				proxySettings.getProxyPort());

		final InetSocketAddress address = new InetSocketAddress(proxySettings.getProxyAddress(),
				proxySettings.getProxyPort());

		final ChannelFuture futureConnect = boot.connect(address);

		channel = futureConnect.getChannel();

		final boolean isGoodConnect = futureConnect.awaitUninterruptibly(TIMEOUT);

		if (!isGoodConnect) {
			log.error("proxy connect error {}", futureConnect.getCause());
			log.error("proxy; {}:{} ", proxySettings.getProxyAddress(), proxySettings.getProxyPort());

			postEvent(FeedEvent.LINK_CONNECT_PROXY_TIMEOUT);

			channel.close();
			return false;
		}

		log.debug("server = {}", feedServers.getPrimary());

		// set the ddf servers

		proxySettings.setFeedServer(feedServers);

		// block until we get proxy_connect_command result

		Boolean proxyResult = false;

		try {
			proxyResult = socksConnectResult.take();
		} catch (InterruptedException e) {
		}

		if (proxyResult == false) {

			log.error("Socks connect error");
			// postEvent(DDF_FeedEvent.LINK_CONNECT_PROXY_TIMEOUT);

			return false;
		}

		/* Send login command to JERQ */
		FeedEvent writeEvent = blockingWrite(FeedDDF.tcpLogin(username, password));

		if (writeEvent == FeedEvent.COMMAND_WRITE_FAILURE) {
			log.error("error sending login command to jerq");
			return false;
		}

		/* Send VERSION # command to JERQ */
		writeEvent = blockingWrite(FeedDDF.tcpVersion(VERSION));

		if (writeEvent == FeedEvent.COMMAND_WRITE_FAILURE) {
			log.error("error sending VERSION 3 command to jerq");
			return false;
		}

		/* Send timestamp command to JERQ */
		writeEvent = blockingWrite(FeedDDF.tcpGo(FeedDDF.SYMBOL_TIMESTAMP));

		if (writeEvent == FeedEvent.COMMAND_WRITE_FAILURE) {
			log.error("error sending login GO TIMESTAMP to jerq");
			return false;
		}

		// all is good

		return true;

	}

	/*
	 * This policy pauses a runner thread for a specified time interval and then
	 * attempts to log in.
	 */
	private class DefaultReloginPolicy implements EventPolicy {

		@Override
		public void newEvent(FeedEvent event) {
			executor.execute(new Thread(new Disconnector(event.name())));
		}
	}

	/*
	 * This policy updates the lastHeartbeat to the current local clock on every
	 * heart beat event received.
	 */
	private class HeartbeatPolicy implements EventPolicy {
		@Override
		public void newEvent(FeedEvent event) {
			lastHeartbeat.set(System.currentTimeMillis());
		}
	}

	private volatile AtomicInteger eventTaskNumber = new AtomicInteger();
	private volatile AtomicInteger messageTaskNumber = new AtomicInteger();
	private volatile AtomicInteger heartbeatTaskNumber = new AtomicInteger();

	private final RunnerDDF eventTask = new RunnerDDF() {

		@Override
		protected void runCore() {

			final int threadNumber = eventTaskNumber.getAndIncrement();

			Thread.currentThread().setName("# DDF EVENT TASK " + threadNumber);

			log.debug("# started DDF-EventTask {}", threadNumber);

			startupLatch.countDown();

			while (!Thread.currentThread().isInterrupted()) {

				try {

					final FeedEvent event = eventQueue.take();

					if (FeedEvent.isConnectionError(event)) {

						log.debug("Setting feed state to logged out");
						updateFeedStateListeners(Connection.State.DISCONNECTED);

					} else if (event == FeedEvent.LOGIN_SUCCESS) {

						log.debug("Login success, feed state updated");
						updateFeedStateListeners(Connection.State.CONNECTED);

					} else if (event == FeedEvent.LOGOUT) {

						log.debug("Setting feed state to logged out");
						updateFeedStateListeners(Connection.State.DISCONNECTED);

					}

					eventPolicy.get(event).newEvent(event);

				} catch (final InterruptedException e) {

					log.debug("# DDF-EventTask InterruptedException {}", threadNumber);

					log.debug("Setting feed state to logged out");
					updateFeedStateListeners(Connection.State.DISCONNECTED);

					Thread.currentThread().interrupt();

				} catch (final Throwable e) {
					log.error("event delivery failed", e);
				}
			}

			log.debug("# DDF-EventTask death {}", threadNumber);
		}
	};

	private final RunnerDDF messageTask = new RunnerDDF() {

		@Override
		protected void runCore() {

			final int threadNumber = messageTaskNumber.getAndIncrement();

			Thread.currentThread().setName("# DDF MESSAGE TASK " + threadNumber);

			log.debug("# started DDF-MessageTask {}", threadNumber);

			startupLatch.countDown();

			while (!Thread.currentThread().isInterrupted()) {
				try {
					final DDF_BaseMessage message = messageQueue.take();
					if (msgListener != null) {

						/*
						 * We set the clock by the timestamp messages, however,
						 * we also set the clock by market messages, but this
						 * has to happen in the message decoding
						 */
						// XXX For now, we have removed the setting of time by
						// market
						// messages because delayed messages were making real
						// time
						// data look delayed
						if (message instanceof DDF_ControlTimestamp) {
							ClockDDF.clock.set(((DDF_ControlTimestamp) message).getStampUTC().asMillisUTC());
						}

						// #######################
						// log.debug(message.toString());
						// #######################

						msgListener.handleMessage(message);
					}
				} catch (final InterruptedException e) {

					log.warn("# DDF-MessageTask InterruptedException {}", threadNumber);

					Thread.currentThread().interrupt();

				} catch (final Throwable e) {
					log.error("message delivery failed", e);
				}
			}

			log.debug("# DDF-MessageTask death {}", threadNumber);
		}
	};

	@Override
	public void setPolicy(final FeedEvent event, final EventPolicy policy) {
		log.debug("Setting policy for :{}", event.name());
		eventPolicy.put(event, policy);
	}

	//

	public void postEvent(final FeedEvent event) {
		try {
			eventQueue.put(event);
		} catch (final InterruptedException e) {
			log.error("could not post event - interrupted");
		}
	}

	private volatile CountDownLatch startupLatch = new CountDownLatch(3);

	/*
	 * the calls to initialize() and terminate were causing the threading issue,
	 * as the runnables were never getting shutdown...
	 */
	private void initialize() {

		startupLatch = new CountDownLatch(3);

		log.debug("# initialize start");

		final StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		for (final StackTraceElement e : trace) {
			log.debug(e.getClassName() + ":" + e.getLineNumber());
		}

		try {
			executor.execute(heartbeatTask);
		} catch (Exception e) {
			log.error("error starting DDF_Heartbeat Thread: {} ", e);

			try {
				executor.execute(new Thread(new Disconnector("DDF_Heartbeat Thread Start Exception")));
			} catch (Exception e1) {
			}

			return;
		}

		try {
			executor.execute(eventTask);
		} catch (Exception e) {
			log.error("error starting DDF_Event Thread: {} ", e);

			try {
				executor.execute(new Thread(new Disconnector("DDF_Event Thread Start Exception")));
			} catch (Exception e1) {
			}

			return;
		}

		try {
			executor.execute(messageTask);
		} catch (Exception e) {
			log.error("error starting DDF_Message Thread: {} ", e);

			try {
				executor.execute(new Thread(new Disconnector("DDF_Message Thread Start Exception")));
			} catch (Exception e1) {
			}

			return;
		}

		try {
			startupLatch.await();
		} catch (InterruptedException e1) {
			log.warn("# initialize interrupted, returning");
			return;
		}

		log.debug("# initialize complete");

	}

	private void terminate() {

		log.debug("## terminate start");

		eventQueue.clear();
		messageQueue.clear();

		/* Interrupts login thread if login is active */

		loginHandler.disableLogins();
		loginHandler.interruptLogin();

		// kill all threads
		// blocking order with join()

		if (heartbeatTask != null) {
			heartbeatTask.interrupt();

			log.debug("# terminate: DDF-heartbeat killed");

		}

		if (messageTask != null) {
			messageTask.interrupt();

			log.debug("# terminate: DDF-MesssageTask killed");

		}

		if (eventTask != null) {
			eventTask.interrupt();

			log.debug("# terminate DDF-EventTask killed");

		}

		log.debug("# terminate: closing channel.");

		if (channel != null) {

			final ChannelFuture cf = channel.close();

			try {
				cf.await();
			} catch (final InterruptedException e) {
				log.warn("# terminate: channel.close() channel interrupted");
			} finally {
				log.debug("# terminate: channel.close() complete");
				channel = null;
			}

		}

		log.debug("## terminate complete");

	}

	/**
	 * blocks
	 * 
	 */
	private synchronized void hardRestart(String caller) {
		log.debug("#### hardRestart called by: " + caller);

		/* Interrupts login thread if login is active */
		loginHandler.disableLogins();
		loginHandler.interruptLogin();

		terminate();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		initBoot();

		log.debug("#### hardRestart: complete");

		log.debug("#### hardRestart: starting login");

		// login

		loginHandler.enableLogins();
		loginHandler.login(0);

	}

	/*
	 * Can post to the FeedEventHandler the following events: <p>
	 * CHANNEL_CONNECT_TIMEOUT {@link DDF_FeedEvent.CHANNEL_CONNECT_TIMEOUT} <p>
	 * CHANNEL_CONNECT_FAILURE {@link DDF_FeedEvent.CHANNEL_CONNECT_FAILURE} <p>
	 * SETTINGS_RETRIEVAL_FAILURE {@link
	 * DDF_FeedEvent.SETTINGS_RETRIEVAL_FAILURE} <p> LOGIN_FAILURE {@link
	 * DDF_FeedEvent.LOGIN_FAILURE} <p> LOGIN_SUCCESS {@link
	 * DDF_FeedEvent.LOGIN_SUCCESS}
	 */
	@Override
	public synchronized void startup() {

		log.debug("Public login called");

		loginHandler.enableLogins();
		loginHandler.login(0);

	}

	private FeedEvent blockingWrite(final CharSequence message) {

		if (isWebSocket) {
			log.debug("About to write: {}", message.toString());
			final ChannelFuture futureWrite = channel.write(new TextWebSocketFrame(message.toString()));

			futureWrite.awaitUninterruptibly(TIMEOUT, TIME_UNIT);

			if (futureWrite.isSuccess()) {
				return FeedEvent.COMMAND_WRITE_SUCCESS;
			} else {
				return FeedEvent.COMMAND_WRITE_FAILURE;
			}
		} else {
			final ChannelFuture futureWrite = channel.write(message);

			futureWrite.awaitUninterruptibly(TIMEOUT, TIME_UNIT);

			if (futureWrite.isSuccess()) {
				return FeedEvent.COMMAND_WRITE_SUCCESS;
			} else {
				return FeedEvent.COMMAND_WRITE_FAILURE;
			}
		}

	}

	@Override
	public synchronized void shutdown() {

		log.debug("public shutdown() has been called, shutting down now.");

		/*
		 * Clear subscriptions, Jerq will stop sending data when we disconnect
		 */
		subscriptions.clear();

		postEvent(FeedEvent.LOGOUT);

		terminate();

		killTimer();

	}

	@SuppressWarnings("deprecation")
	private void killTimer() {

		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

		for (final Thread t : threadSet) {
			if (t.getName().startsWith("Hashed wheel timer")) {
				while (t.isAlive()) {
					t.stop();
				}
			}
		}

	}

	private boolean isConnected() {
		if (channel == null) {
			return false;
		}
		return channel.isConnected();
	}

	/*
	 * Adds a COMMAND_WRITE_ERROR to the event queue if an attempt to write to
	 * the channel fails.
	 */
	private class CommandFailureListener implements ChannelFutureListener {

		@Override
		public void operationComplete(final ChannelFuture future) throws Exception {
			if (!future.isSuccess()) {
				postEvent(FeedEvent.COMMAND_WRITE_FAILURE);
			}
		}

	}

	/* Asynchronous write to the channel, future returns true on success */
	@Override
	public Future<Boolean> write(final String message) {
		// log.debug("Attempting to send reqeust to JERQ : {}", message);
		final ChannelFuture future = channel.write(message + "\n");
		future.addListener(new CommandFailureListener());
		return new CommandFuture(future);
	}

	@Override
	public void bindMessageListener(final DDF_MessageListener handler) {
		this.msgListener = handler;
	}

	@Override
	public void bindStateListener(final Connection.Monitor stateListener) {
		feedListeners.add(stateListener);
	}

	private volatile Thread loginThread = null;

	private volatile AtomicInteger loginThreadNumber = new AtomicInteger();
	private volatile boolean isLoggingIn = false;

	private class LoginHandler {

		private boolean enabled = true;

		void enableLogins() {
			enabled = true;
		}

		void disableLogins() {
			enabled = false;
		}

		boolean isLoginActive() {
			return loginThread != null && loginThread.isAlive();
		}

		void interruptLogin() {
			if (isLoginActive()) {
				loginThread.interrupt();
				log.debug("# LoginHandler - login thread killed.");
			}
		}

		synchronized void login(final int delay) {

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				log.error("# LoginHandler interrupted while sleeping");
				return;
			}

			final int threadNumber = loginThreadNumber.getAndIncrement();

			log.debug("# LoginHandler - login called. login enabled = {} isLoginActive = {} ", enabled, isLoginActive()
					+ ". reconnect attempt count = " + threadNumber);

			if (proxySettings != null) {

				startUpProxy();

				return;
			}

			if (enabled && !isLoginActive()) {

				log.debug("Setting feed state to attempting login");

				updateFeedStateListeners(Connection.State.CONNECTING);

				loginThread = new Thread(new LoginRunnable(delay, threadNumber), "# DDF Login " + threadNumber);

				loginThread.start();

			}
		}

	}

	private void updateFeedStateListeners(final Connection.State state) {
		for (final Connection.Monitor listener : feedListeners) {
			// FIXME Monitor not implemented yet, so this needs to be a Monitor
			listener.handle(state, null);
		}
	}

	/* Runnable which handles connection, login, and initializaion */
	class LoginRunnable implements Runnable {

		@SuppressWarnings("unused")
		private final int delay;
		@SuppressWarnings("unused")
		private final int threadNumber;

		public LoginRunnable(final int delay, final int threadNumber) {
			this.delay = delay;
			this.threadNumber = threadNumber;
		}

		@Override
		public void run() {

			isLoggingIn = true;

			log.debug("starting LoginRunnable " + Thread.currentThread().getName());

			initialize();

			log.debug("trying to connect to setting service...");

			/* Attempt to get current data server settings */
			DDF_Settings settings = null;
			try {
				settings = DDF_SettingsService.newSettings(username, password);
				if (!settings.isValidLogin()) {
					log.error("Posting SETTINGS_RETRIEVAL_FAILURE");
					postEvent(FeedEvent.SETTINGS_RETRIEVAL_FAILURE);
					isLoggingIn = false;
					return;
				}
			} catch (final Exception e) {
				log.error("Posting SETTINGS_RETRIEVAL_FAILURE");
				postEvent(FeedEvent.SETTINGS_RETRIEVAL_FAILURE);
				isLoggingIn = false;
				return;
			}

			log.debug("received settings from settings service\n{}", settings);

			final DDF_Server server = settings.getServer(serverType);
			final String primary = server.getPrimary();
			final String secondary = server.getSecondary();

			log.debug("trying primary server login " + primary);

			/* Attempt to connect and login to primary server */
			final FeedEvent eventOne = login(primary, PORT);

			if (eventOne == FeedEvent.LOGIN_SENT) {
				log.debug("Posting LOGIN_SENT for primary server");
				postEvent(FeedEvent.LOGIN_SENT);
				isLoggingIn = false;
				return;
			}

			log.warn("failed to connect to primary server {} trying secondary server login {}", primary, secondary);

			/* Attempt to connect and login to secondary server */
			final FeedEvent eventTwo = login(secondary, PORT);

			if (eventTwo == FeedEvent.LOGIN_SENT) {
				log.debug("Posting LOGIN_SENT for secondary server");
				postEvent(FeedEvent.LOGIN_SENT);
				isLoggingIn = false;
				return;
			}

			/*
			 * For simplicity, we only return the error message from the primary
			 * server in the event both logins fail.
			 */
			log.error("Failed to connect to both servers , Posting {}", eventOne.name());

			isLoggingIn = false;

			postEvent(eventOne);

			return;
		}

		/* Handles the login for an individual server */
		private FeedEvent login(String host, int port) {

			if (CUSTOM_HOST != null) {
				host = CUSTOM_HOST;
				log.warn("connecting with CUSTOM_HOST: {}", CUSTOM_PORT);
			}

			if (CUSTOM_PORT != Integer.MAX_VALUE) {
				port = CUSTOM_PORT;
				log.warn("connecting with CUSTOM_PORT: {}", CUSTOM_PORT);
			}

			if (isWebSocket) {
				try {
					URI u = new URI(WEBSOCKET_EP);
					host = u.getHost();
					port = u.getPort();
				} catch (URISyntaxException e) {
					log.error("failed to parse WebSocket endpoint = {}", WEBSOCKET_EP, e);
				}
			}

			final InetSocketAddress address = new InetSocketAddress(host, port);

			ChannelFuture futureConnect = null;

			/* Netty attempt to connect to server */
			futureConnect = boot.connect(address);

			channel = futureConnect.getChannel();

			if (!futureConnect.awaitUninterruptibly(TIMEOUT, TIME_UNIT)) {
				log.error("channel connect timeout; {}:{} ", host, port);
				return FeedEvent.CHANNEL_CONNECT_TIMEOUT;
			}

			/* Handle connection attempt errors */
			if (!futureConnect.isDone()) {
				log.error("channel connect timeout; {}:{} ", host, port);
				return FeedEvent.CHANNEL_CONNECT_TIMEOUT;
			}

			if (!futureConnect.isSuccess()) {
				log.error("channel connect unsuccessful; {}:{} ", host, port);
				return FeedEvent.CHANNEL_CONNECT_FAILURE;
			}

			/* wait for handshake to complete */
			if (isWebSocket) {
				try {
					final PipelineFactoryWebSocket w = (PipelineFactoryWebSocket) boot.getPipelineFactory();
					w.getHandshaker().handshake(channel).syncUninterruptibly();
				} catch (Exception e) {
					log.error("failed to wait for WS handshake", e);
				}
			}

			waitFor(250);

			/* Send login command to JERQ */
			FeedEvent writeEvent = blockingWrite(FeedDDF.tcpLogin(username, password));

			if (writeEvent == FeedEvent.COMMAND_WRITE_FAILURE) {
				return FeedEvent.COMMAND_WRITE_FAILURE;
			}

			waitFor(250);

			/* Send VERSION 3 command to JERQ */
			writeEvent = blockingWrite(FeedDDF.tcpVersion(VERSION));

			if (writeEvent == FeedEvent.COMMAND_WRITE_FAILURE) {
				return FeedEvent.COMMAND_WRITE_FAILURE;
			}

			waitFor(250);

			/* Send timestamp command to JERQ */
			writeEvent = blockingWrite(FeedDDF.tcpGo(FeedDDF.SYMBOL_TIMESTAMP));

			if (writeEvent == FeedEvent.COMMAND_WRITE_FAILURE) {
				return FeedEvent.COMMAND_WRITE_FAILURE;
			}

			return FeedEvent.LOGIN_SENT;
		}

	}

	private void waitFor(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			log.error("failed to wait, interrupted", e);
		}

	}

	private final RunnerDDF heartbeatTask = new RunnerDDF() {

		private long delta;

		@Override
		public void runCore() {

			final int threadNumber = heartbeatTaskNumber.getAndIncrement();

			Thread.currentThread().setName("# DDF HEARTBEAT TASK " + threadNumber);

			log.debug("started # DDF-heartbeat task {} ", threadNumber);

			startupLatch.countDown();

			try {

				while (!Thread.currentThread().isInterrupted()) {

					checkTime();
					Thread.sleep(2000); // This must be less than
										// HEARTBEAT_TIMEOUT
				}

			} catch (final InterruptedException e) {

				log.warn("# DDF-heartbeat task InterruptedException {}", threadNumber);

				Thread.currentThread().interrupt();

			} catch (final Exception e) {

				log.warn("# DDF-heartbeat exception: {}", e);

			}

			log.debug("# DDF-heartbeat task death {}", threadNumber);

		}

		private void checkTime() {

			/*
			 * If not currently logged in, keep the last heart beat updated so
			 * when we do query it, it will be fresh.
			 */

			if (loginHandler.isLoginActive() || !isConnected()) {
				lastHeartbeat.set(System.currentTimeMillis());
			} else {
				delta = System.currentTimeMillis() - lastHeartbeat.get();

				/*
				 * Close channel if time delta is greater than threshold and
				 * reset last heart beat.
				 */
				if (delta > HEARTBEAT_TIMEOUT) {
					log.error("Heartbeat check failed - calling hardRestart()");
					log.error("Heartbeat delta: " + delta);

					// any calls here will happen in this thread
					// ...so we will start new thread so this one can die
					executor.execute(new Thread(new Disconnector("HEARTBEAT TIMEOUT")));

					lastHeartbeat.set(System.currentTimeMillis());
				}

			}
		}

	};

	private class Disconnector implements Runnable {

		final String caller;

		public Disconnector(final String caller) {
			this.caller = caller;
		}

		@Override
		public void run() {
			if (isLoggingIn) {
				log.warn("## " + caller + " is trying to call hardRestart, but we are still logging in.");
				return;
			}

			hardRestart(caller);

		}
	}

	// change how this is done

	public void setProxiedChannel(ChannelHandlerContext ctx, MessageEvent e, boolean success) {

		if (success) {
			this.channel = e.getChannel();

			// post ddf link connect
			postEvent(FeedEvent.LINK_CONNECT);

			final SimpleChannelHandler ddfHandler = new ChannelHandlerDDF(eventQueue, messageQueue);

			channel.getPipeline().addLast("ddf frame decoder", new MsgDeframerDDF());

			channel.getPipeline().addLast("ddf message decoder", new MsgDecoderDDF());

			// ### Encoders ###

			channel.getPipeline().addLast("ddf command encoder", new MsgEncoderDDF());

			channel.getPipeline().addLast("ddf data feed client", ddfHandler);

			socksConnectResult.add(true);
		} else {
			socksConnectResult.add(false);
		}
	}

	boolean connecting = false;

	@Override
	public void startUpProxy() {

		if (connecting == true) {
			log.error("Still connecting");
			return;
		}

		connecting = true;

		log.debug("startUpProxy() - connecting...");

		if (proxySettings == null) {
			log.error("Poxysettings are null, starting direct connect");
			startup();
			return;
		}

		/* Attempt to get current data server settings */
		DDF_Settings ddf_settings = null;
		try {
			ddf_settings = DDF_SettingsService.newSettings(username, password);
			if (!ddf_settings.isValidLogin()) {
				log.error("Posting SETTINGS_RETRIEVAL_FAILURE");
				postEvent(FeedEvent.SETTINGS_RETRIEVAL_FAILURE);

				connecting = false;
				return;
			}
		} catch (final Exception e) {
			log.error("Posting SETTINGS_RETRIEVAL_FAILURE");
			postEvent(FeedEvent.SETTINGS_RETRIEVAL_FAILURE);

			connecting = false;
			return;
		}

		final DDF_Server server = ddf_settings.getServer(serverType);

		loginProxy(username, password, server);

		log.warn("startUpProxy() done connecting...");

		connecting = false;

	}

}
