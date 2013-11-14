/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.base.sub.Subscription;
import com.barchart.feed.base.sub.Subscription.Type;
import com.barchart.feed.ddf.datalink.api.CommandFuture;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.datalink.api.DDF_SocksProxy;
import com.barchart.feed.ddf.datalink.api.DummyFuture;
import com.barchart.feed.ddf.datalink.api.EventPolicy;
import com.barchart.feed.ddf.datalink.enums.DDF_FeedEvent;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.settings.api.DDF_Server;
import com.barchart.feed.ddf.settings.api.DDF_Settings;
import com.barchart.feed.ddf.settings.enums.DDF_ServerType;
import com.barchart.feed.ddf.settings.provider.DDF_SettingsService;
import com.barchart.feed.ddf.util.FeedDDF;

class FeedClientDDF implements DDF_FeedClient {

	private static final String VERSION = FeedDDF.VERSION_4;
	private static final int PORT = 7500;

	private static final int DEFAULT_IO_THREADS = 
			Runtime.getRuntime().availableProcessors() * 2;
	
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
	private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

	private static final long HEARTBEAT_TIMEOUT = 30 * 1000;

	//

	private final Map<DDF_FeedEvent, EventPolicy> eventPolicy =
			new ConcurrentHashMap<DDF_FeedEvent, EventPolicy>();

	private final Map<String, DDF_Subscription> subscriptions = 
			new ConcurrentHashMap<String, DDF_Subscription>();

	//

	private final LoginHandler loginHandler = new LoginHandler();

	//

	private final BlockingQueue<DDF_FeedEvent> eventQueue = 
			new LinkedBlockingQueue<DDF_FeedEvent>();

	private final BlockingQueue<DDF_BaseMessage> messageQueue = 
			new LinkedBlockingQueue<DDF_BaseMessage>();

	private final AtomicLong lastHeartbeat = new AtomicLong(0);

	//

	private volatile DDF_MessageListener msgListener = null;

	private final CopyOnWriteArrayList<Connection.Monitor> feedListeners = 
			new CopyOnWriteArrayList<Connection.Monitor>();

	//

	private ClientBootstrap boot;

	private ChannelFactory channelFactory;
	private Channel channel;
	private HashedWheelTimer timer = null;
	
	//

	private String username;
	private String password;
	private DDF_ServerType serverType = DDF_ServerType.STREAM;
	private Executor executor;
	
	//private ExecutorService localExecutor = Executors.newFixedThreadPool(100);

	// SOCKS5

	private DDF_SocksProxy proxySettings = null;
	private final BlockingQueue<Boolean> socksConnectResult = 
			new LinkedBlockingQueue<Boolean>();

	//

	FeedClientDDF(final String username, final String password,
			final Executor executor) {

		startup(username, password, executor, null);

	}

	public FeedClientDDF(String username, String password, Executor executor,
			DDF_SocksProxy proxySettings) {

		startup(username, password, executor, proxySettings);

	}

	private void startup(final String username, final String password,
			final Executor exec, final DDF_SocksProxy proxy) {

		this.username = username;
		this.password = password;
		this.executor = exec;
		
		this.proxySettings = proxy;

		timer = new HashedWheelTimer();
		
		channelFactory = new NioClientSocketChannelFactory(
				executor, 1, new NioWorkerPool(executor, DEFAULT_IO_THREADS), timer);

		boot = new ClientBootstrap(channelFactory);

		if (proxySettings == null) {

			/*
			 * The vector for data leaving the netty channel and entering the
			 * business application logic.
			 */
			final SimpleChannelHandler ddfHandler = new ChannelHandlerDDF(
					eventQueue, messageQueue);

			final ChannelPipelineFactory pipelineFactory = new PipelineFactoryDDF(
					ddfHandler);

			boot.setPipelineFactory(pipelineFactory);

		} else {

			final ChannelPipelineFactory socksPipelineFactory = new PipelineFactorySocks(
					executor, this, proxy);

			boot.setPipelineFactory(socksPipelineFactory);
			boot.setOption("child.tcpNoDelay", true);
			boot.setOption("child.keepAlive", true);
			boot.setOption("child.reuseAddress", true);
			boot.setOption("readWriteFair", true);

		}

		boot.setOption(TIMEOUT_OPTION, TIMEOUT);

		/* Initialize event policy with event policies that do nothing. */
		for (final DDF_FeedEvent event : DDF_FeedEvent.values()) {
			eventPolicy.put(event, new EventPolicy() {

				@Override
				public void newEvent(DDF_FeedEvent event) {
					/* Do nothing */
				}
			});
		}

		/* Add DefaultReloginPolicy to selected events */
		eventPolicy.put(DDF_FeedEvent.LOGIN_FAILURE, reconnectionPolicy);

		eventPolicy.put(DDF_FeedEvent.LINK_DISCONNECT, reconnectionPolicy);

		eventPolicy.put(DDF_FeedEvent.SETTINGS_RETRIEVAL_FAILURE,
				reconnectionPolicy);

		eventPolicy.put(DDF_FeedEvent.CHANNEL_CONNECT_FAILURE,
				reconnectionPolicy);
		eventPolicy.put(DDF_FeedEvent.CHANNEL_CONNECT_TIMEOUT,
				reconnectionPolicy);
		eventPolicy.put(DDF_FeedEvent.LINK_CONNECT_PROXY_TIMEOUT,
				reconnectionPolicy);

		/* Add SubscribeAfterLogin to LOGIN_SUCCESS */
		eventPolicy.put(DDF_FeedEvent.LOGIN_SUCCESS, new SubscribeAfterLogin());

		/* Add HeartbeatPolicy to HEART_BEAT */
		eventPolicy.put(DDF_FeedEvent.HEART_BEAT, new HeartbeatPolicy());

	}

	private final DefaultReloginPolicy reconnectionPolicy = new DefaultReloginPolicy();

	private boolean loginProxy(String username, String password,
			DDF_Server feedServers) {

		terminate();

		initialize();

		// do socks connection

		log.info("connect to proxy - address {} port {}",
				proxySettings.getProxyAddress(), proxySettings.getProxyPort());

		final InetSocketAddress address = new InetSocketAddress(
				proxySettings.getProxyAddress(), proxySettings.getProxyPort());

		final ChannelFuture futureConnect = boot.connect(address);

		channel = futureConnect.getChannel();

		final boolean isGoodConnect = futureConnect
				.awaitUninterruptibly(TIMEOUT);

		if (!isGoodConnect) {
			log.error("proxy connect error {}", futureConnect.getCause());
			log.error("proxy; {}:{} ", proxySettings.getProxyAddress(),
					proxySettings.getProxyPort());

			postEvent(DDF_FeedEvent.LINK_CONNECT_PROXY_TIMEOUT);

			channel.close();
			return false;
		}

		log.info("server = {}", feedServers.getPrimary());

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
		DDF_FeedEvent writeEvent = blockingWrite(FeedDDF.tcpLogin(username,
				password));

		if (writeEvent == DDF_FeedEvent.COMMAND_WRITE_FAILURE) {
			log.error("error sending login command to jerq");
			return false;
		}

		/* Send VERSION # command to JERQ */
		writeEvent = blockingWrite(FeedDDF.tcpVersion(VERSION));

		if (writeEvent == DDF_FeedEvent.COMMAND_WRITE_FAILURE) {
			log.error("error sending VERSION 3 command to jerq");
			return false;
		}

		/* Send timestamp command to JERQ */
		writeEvent = blockingWrite(FeedDDF.tcpGo(FeedDDF.SYMBOL_TIMESTAMP));

		if (writeEvent == DDF_FeedEvent.COMMAND_WRITE_FAILURE) {
			log.error("error sending login GO TIMESTAMP to jerq");
			return false;
		}

		// all is good

		return true;

	}

	/*
	 * This policy ensures that all subscribed instruments are requested from
	 * JERQ upon login or relogin after a dicsonnect
	 */
	private class SubscribeAfterLogin implements EventPolicy {

		@Override
		public void newEvent(DDF_FeedEvent event) {
			if (subscriptions.size() > 0) {
				log.debug("Requesting current subscriptions");
				final Set<Subscription> subs = new HashSet<Subscription>();
				for(final Entry<String, DDF_Subscription> e : subscriptions.entrySet()) {
					subs.add(e.getValue());
				}
				subscribe(subs);
			} else {
				log.warn("subscriptions set is empty.");
			}
		}
	}

	/*
	 * This policy pauses a runner thread for a specified time interval and then
	 * attempts to log in.
	 */
	private class DefaultReloginPolicy implements EventPolicy {

		@Override
		public void newEvent(DDF_FeedEvent event) {
			executor.execute(new Thread(new Disconnector(event.name())));
		}
	}

	/*
	 * This policy updates the lastHeartbeat to the current local clock on every
	 * heart beat event received.
	 */
	private class HeartbeatPolicy implements EventPolicy {
		@Override
		public void newEvent(DDF_FeedEvent event) {
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

			log.warn("# started DDF-EventTask {}", threadNumber);

			startupLatch.countDown();

			while (!Thread.currentThread().isInterrupted()) {

				try {

					final DDF_FeedEvent event = eventQueue.take();

					if (DDF_FeedEvent.isConnectionError(event)) {

						log.info("Setting feed state to logged out");
						updateFeedStateListeners(Connection.State.DISCONNECTED);

					} else if (event == DDF_FeedEvent.LOGIN_SUCCESS) {

						log.info("Login success, feed state updated");
						updateFeedStateListeners(Connection.State.CONNECTED);

					} else if (event == DDF_FeedEvent.LOGOUT) {

						log.info("Setting feed state to logged out");
						updateFeedStateListeners(Connection.State.DISCONNECTED);

					}

					log.trace("Enacting policy for :{}", event.name());

					eventPolicy.get(event).newEvent(event);

				} catch (final InterruptedException e) {

					log.warn("# DDF-EventTask InterruptedException {}",
							threadNumber);

					log.info("Setting feed state to logged out");
					updateFeedStateListeners(Connection.State.DISCONNECTED);

					Thread.currentThread().interrupt();

				} catch (final Throwable e) {
					log.error("event delivery failed", e);
				}
			}

			log.error("# DDF-EventTask death {}", threadNumber);
		}
	};

	private final RunnerDDF messageTask = new RunnerDDF() {
		@Override
		protected void runCore() {

			final int threadNumber = messageTaskNumber.getAndIncrement();

			Thread.currentThread()
					.setName("# DDF MESSAGE TASK " + threadNumber);

			log.warn("# started DDF-MessageTask {}", threadNumber);

			startupLatch.countDown();

			while (!Thread.currentThread().isInterrupted()) {
				try {
					final DDF_BaseMessage message = messageQueue.take();
					if (msgListener != null) {
						
						//// #######################
						//log.debug(message.toStringFields());
						// #######################
						
						msgListener.handleMessage(message);
					}
				} catch (final InterruptedException e) {

					log.warn("# DDF-MessageTask InterruptedException {}",
							threadNumber);

					Thread.currentThread().interrupt();

				} catch (final Throwable e) {
					log.error("message delivery failed", e);
				}
			}

			log.error("# DDF-MessageTask death {}", threadNumber);
		}
	};

	@Override
	public void setPolicy(final DDF_FeedEvent event, final EventPolicy policy) {
		log.debug("Setting policy for :{}", event.name());
		eventPolicy.put(event, policy);
	}

	//

	void postEvent(final DDF_FeedEvent event) {
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

		log.warn("# initialize start");

		final StackTraceElement[] trace = Thread.currentThread()
				.getStackTrace();

		for (final StackTraceElement e : trace) {
			log.debug(e.getClassName() + ":" + e.getLineNumber());
		}

		try {
			executor.execute(heartbeatTask);
		} catch (Exception e) {
			log.error("error starting DDF_Heartbeat Thread: {} ", e);

			try {
				executor.execute(new Thread(new Disconnector(
						"DDF_Heartbeat Thread Start Exception")));
			} catch (Exception e1) {
			}

			return;
		}

		try {
			executor.execute(eventTask);
		} catch (Exception e) {
			log.error("error starting DDF_Event Thread: {} ", e);

			try {
				executor.execute(new Thread(new Disconnector(
						"DDF_Event Thread Start Exception")));
			} catch (Exception e1) {
			}

			return;
		}

		try {
			executor.execute(messageTask);
		} catch (Exception e) {
			log.error("error starting DDF_Message Thread: {} ", e);

			try {
				executor.execute(new Thread(new Disconnector(
						"DDF_Message Thread Start Exception")));
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

		log.warn("# initialize complete");

	}

	private void terminate() {

		log.warn("## terminate start");
		
		eventQueue.clear();
		messageQueue.clear();

		/* Interrupts login thread if login is active */

		loginHandler.disableLogins();
		loginHandler.interruptLogin();

		// kill all threads
		// blocking order with join()

		if (heartbeatTask != null) {
			heartbeatTask.interrupt();

			log.warn("# terminate: DDF-heartbeat killed");

		}

		if (messageTask != null) {
			messageTask.interrupt();

			log.warn("# terminate: DDF-MesssageTask killed");

		}

		if (eventTask != null) {
			eventTask.interrupt();

			log.warn("# terminate DDF-EventTask killed");

		}

		log.warn("# terminate: closing channel.");

		if (channel != null) {

			final ChannelFuture cf = channel.close();

			try {
				cf.await();
			} catch (final InterruptedException e) {
				log.warn("# terminate: channel.close() channel interrupted");
			} finally {
				log.warn("# terminate: channel.close() complete");
				channel = null;
			}

		}
		
		log.warn("## terminate complete");

	}

	/**
	 * blocks
	 * 
	 */
	private synchronized void hardRestart(String caller) {
		log.warn("#### hardRestart called by: " + caller);
		log.warn("#### hardRestart: starting");

		log.warn("#### hardRestart: interupt logins");

		/* Interrupts login thread if login is active */
		loginHandler.disableLogins();
		loginHandler.interruptLogin();

		log.warn("#### hardRestart: calling terminate");

		terminate();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		log.warn("#### hardRestart: complete");

		log.warn("#### hardRestart: starting login");

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

	private DDF_FeedEvent blockingWrite(final CharSequence message) {
		final ChannelFuture futureWrite = channel.write(message);

		futureWrite.awaitUninterruptibly(TIMEOUT, TIME_UNIT);

		if (futureWrite.isSuccess()) {
			return DDF_FeedEvent.COMMAND_WRITE_SUCCESS;
		} else {
			return DDF_FeedEvent.COMMAND_WRITE_FAILURE;
		}
	}

	@Override
	public synchronized void shutdown() {

		log.warn("public shutdown() has been called, shutting down now.");

		/* Clear subscriptions, Jerq will stop sending data when we disconnect */
		subscriptions.clear();

		postEvent(DDF_FeedEvent.LOGOUT);

		terminate();
		
//		timer.stop();

		if (boot != null) {
			boot.releaseExternalResources();
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
		public void operationComplete(final ChannelFuture future)
				throws Exception {
			if (!future.isSuccess()) {
				postEvent(DDF_FeedEvent.COMMAND_WRITE_FAILURE);
			}
		}

	}

	/* Asynchronous write to the channel, future returns true on success */
	private Future<Boolean> writeAsync(final String message) {
		log.debug("Attempting to send reqeust to JERQ : {}", message);
		final ChannelFuture future = channel.write(message + "\n");
		future.addListener(new CommandFailureListener());
		return new CommandFuture(future);
	}

	@Override
	public Future<Boolean> subscribe(final Set<Subscription> subs) {

		if (subs == null) {
			log.error("Null subscribes request recieved");
			return null;
		}

		final Set<Subscription> insts = new HashSet<Subscription>();
		final Set<Subscription> exch = new HashSet<Subscription>();
		
		for(final Subscription sub : subs) {
			switch(sub.type()) {
			case INSTRUMENT:
				insts.add(sub);
				break;
			case EXCHANGE:
				exch.add(sub);
				break;
			}
		}
		
		if(!insts.isEmpty()) {
			
			if(exch.isEmpty()) {
				return subInsts(insts);
			} else {
				subInsts(insts);
				return subExcs(exch);
			}
			
		}
		
		if(!exch.isEmpty()) {
			return subExcs(exch);
		} else {
			return new DummyFuture();
		}
		
	}
	
	private Future<Boolean> subInsts(final Set<Subscription> subs) {
		
		/*
		 * Creates a single JERQ command from the set, subscriptions are added
		 * indivually.
		 */
		final StringBuffer sb = new StringBuffer();
		sb.append("GO ");
		for (final Subscription sub : subs) {

			if (sub != null) {
				
				final String interest = sub.interest();
				
				/* If we're subscribed already, add new interests, otherwise add  */
				if(subscriptions.containsKey(interest)) {
					subscriptions.get(interest).addTypes(sub.types());
				} else {
					subscriptions.put(interest, new DDF_Subscription(sub, Type.INSTRUMENT));
				}
				
				sb.append(subscriptions.get(interest).encode() + ",");
			}
		}
		
		if (!isConnected()) {
			return new DummyFuture();
		}
		
		return writeAsync(sb.toString());
		
	}
	
	private Future<Boolean> subExcs(final Set<Subscription> subs) {
		
		final StringBuffer sb = new StringBuffer();
		
		sb.append("STR L ");
		for(final Subscription sub : subs) {
			
			if(sub != null) {
			
				final String interest = sub.interest();
				
				if(!subscriptions.containsKey(interest)) {
					
					subscriptions.put(interest, new DDF_Subscription(sub, Type.EXCHANGE));
					
				}
				
				sb.append(interest + ";");
				
			}
			
		}
		
		if (!isConnected()) {
			return new DummyFuture();
		}
		
		return writeAsync(sb.toString());
	}
	
	@Override
	public Future<Boolean> subscribe(final Subscription sub) {

		//TODO Should these just return DummyFutures? NULL seems bad
		if (sub == null) {
			log.error("Null subscribe request recieved");
			return null;
		}
		
		if(sub.type() == Type.EXCHANGE) {
			return subExc(sub);
		} else {
			return subInst(sub);
		}
		
	}

	private Future<Boolean> subInst(final Subscription sub) {
		
		/* If we're subscribed already, add new interests, otherwise add */
		final String inst = sub.interest();
		if(subscriptions.containsKey(inst)) {
			subscriptions.get(inst).addTypes(sub.types());
		} else {
			subscriptions.put(inst, new DDF_Subscription(sub, Type.INSTRUMENT));
		}
		
		if (!isConnected()) {
			return new DummyFuture();
		}
		
		/* Request subscription from JERQ and return the future */
		return writeAsync("GO " + sub.encode());
		
	}
	
	private Future<Boolean> subExc(final Subscription sub) {
		
		final String interest = sub.interest();
		
		if(!subscriptions.containsKey(interest)) {
			subscriptions.put(interest, new DDF_Subscription(sub, Type.EXCHANGE));
		} else {
			return new DummyFuture();
		}
		
		if (!isConnected()) {
			return new DummyFuture();
		}
		
		return writeAsync("STR L " + sub.interest());
	}
	
	@Override
	public Future<Boolean> unsubscribe(final Set<Subscription> subs) {

		if (subs == null) {
			log.error("Null subscribes request recieved");
			return null;
		}

		final Set<Subscription> insts = new HashSet<Subscription>();
		final Set<Subscription> exch = new HashSet<Subscription>();
		
		for(final Subscription sub : subs) {
			switch(sub.type()) {
			case INSTRUMENT:
				insts.add(sub);
				break;
			case EXCHANGE:
				exch.add(sub);
				break;
			}
		}
		
		if(!insts.isEmpty()) {
			
			if(exch.isEmpty()) {
				return unsubInsts(insts);
			} else {
				unsubInsts(insts);
				return unsubExchs(exch);
			}
			
		}
		
		if(!exch.isEmpty()) {
			return unsubExchs(exch);
		} else {
			return new DummyFuture();
		}
		
	}

	private Future<Boolean> unsubInsts(final Set<Subscription> subs) {
		
		/*
		 * Creates a single JERQ command from the set. Subscriptions are removed
		 * individually.
		 */
		final StringBuffer sb = new StringBuffer();
		sb.append("STOP ");
		for (final Subscription sub : subs) {

			if (sub != null) {
				subscriptions.remove(sub.interest());
				sb.append(sub.interest() + ",");
			}
		}
		
		if (!isConnected()) {
			return new DummyFuture();
		}
		
		return writeAsync(sb.toString());
	}
	
	private Future<Boolean> unsubExchs(final Set<Subscription> subs) {
		
		for(final Subscription sub : subs) {
			subscriptions.remove(sub.interest());
		}
		
		if (!isConnected()) {
			return new DummyFuture();
		}
		
		/* Have to unsub from everything and resub */
		writeAsync("STOP");
		
		final Set<Subscription> resubs = new HashSet<Subscription>();
		for(final Entry<String, DDF_Subscription> e : subscriptions.entrySet()) {
			resubs.add(e.getValue());
		}
		
		return subscribe(resubs);
	}
	
	@Override
	public Future<Boolean> unsubscribe(final Subscription sub) {

		if (sub == null) {
			log.error("Null subscribe request recieved");
			return null;
		}
		
		if(sub.type() == Type.EXCHANGE) {
			return unsubExc(sub);
		} else {
			return unsubInst(sub);
		}

	}
	
	private Future<Boolean> unsubInst(final Subscription sub) {
		
		subscriptions.remove(sub.interest());

		if (!isConnected()) {
			return new DummyFuture();
		}

		/* Request subscription from JERQ and return the future */
		return writeAsync("STOP " + sub.encode());
		
	}
	
	private Future<Boolean> unsubExc(final Subscription sub) {
		
		subscriptions.remove(sub.interest());
		
		if (!isConnected()) {
			return new DummyFuture();
		}
		
		/* Have to unsub from everything and resub */
		writeAsync("STOP");
		
		final Set<Subscription> subs = new HashSet<Subscription>();
		for(final Entry<String, DDF_Subscription> e : subscriptions.entrySet()) {
			subs.add(e.getValue());
		}
		
		return subscribe(subs);
		
	}

	@Override
	public synchronized void bindMessageListener(
			final DDF_MessageListener handler) {
		this.msgListener = handler;
	}

	@Override
	public synchronized void bindStateListener(
			final Connection.Monitor stateListener) {
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
				log.warn("# LoginHandler - login thread killed.");
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

			log.warn(
					"# LoginHandler - login called. login enabled = {} isLoginActive = {} ",
					enabled, isLoginActive() + ". reconnect attempt count = "
							+ threadNumber);

			if (proxySettings != null) {

				startUpProxy();

				return;
			}

			if (enabled && !isLoginActive()) {

				log.warn("Setting feed state to attempting login");

				updateFeedStateListeners(Connection.State.CONNECTING);

				loginThread = new Thread(
						new LoginRunnable(delay, threadNumber), "# DDF Login "
								+ threadNumber);

				// use .start() not executor..

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

			log.info("starting LoginRunnable "
					+ Thread.currentThread().getName());

			initialize();

			log.info("trying to connect to setting service...");

			/* Attempt to get current data server settings */
			DDF_Settings settings = null;
			try {
				settings = DDF_SettingsService.newSettings(username, password);
				if (!settings.isValidLogin()) {
					log.error("Posting SETTINGS_RETRIEVAL_FAILURE");
					postEvent(DDF_FeedEvent.SETTINGS_RETRIEVAL_FAILURE);
					isLoggingIn = false;
					return;
				}
			} catch (final Exception e) {
				log.error("Posting SETTINGS_RETRIEVAL_FAILURE");
				postEvent(DDF_FeedEvent.SETTINGS_RETRIEVAL_FAILURE);
				isLoggingIn = false;
				return;
			}

			log.info("received settings from settings service");

			final DDF_Server server = settings.getServer(serverType);
			final String primary = server.getPrimary();
			final String secondary = server.getSecondary();

			log.info("trying primary server login " + primary);

			/* Attempt to connect and login to primary server */
			final DDF_FeedEvent eventOne = login(primary, PORT);

			if (eventOne == DDF_FeedEvent.LOGIN_SENT) {
				log.info("Posting LOGIN_SENT for primary server");
				postEvent(DDF_FeedEvent.LOGIN_SENT);
				isLoggingIn = false;
				return;
			}

			log.warn("failed to connect to primary server " + primary);

			log.warn("trying secondary server login " + secondary);

			/* Attempt to connect and login to secondary server */
			final DDF_FeedEvent eventTwo = login(secondary, PORT);

			if (eventTwo == DDF_FeedEvent.LOGIN_SENT) {
				log.info("Posting LOGIN_SENT for secondary server");
				postEvent(DDF_FeedEvent.LOGIN_SENT);
				isLoggingIn = false;
				return;
			}

			/*
			 * For simplicity, we only return the error message from the primary
			 * server in the event both logins fail.
			 */

			log.error("Failed to connect to both servers , Posting {}",
					eventOne.name());

			isLoggingIn = false;

			postEvent(eventOne);

			return;
		}

		/* Handles the login for an individual server */
		private DDF_FeedEvent login(final String host, final int port) {
			final InetSocketAddress address = new InetSocketAddress(host, port);

			ChannelFuture futureConnect = null;

			/* Netty attempt to connect to server */
			futureConnect = boot.connect(address);

			channel = futureConnect.getChannel();

			if (!futureConnect.awaitUninterruptibly(TIMEOUT, TIME_UNIT)) {
				log.error("channel connect timeout; {}:{} ", host, port);
				return DDF_FeedEvent.CHANNEL_CONNECT_TIMEOUT;
			}

			/* Handle connection attempt errors */
			if (!futureConnect.isDone()) {
				log.error("channel connect timeout; {}:{} ", host, port);
				return DDF_FeedEvent.CHANNEL_CONNECT_TIMEOUT;
			}

			if (!futureConnect.isSuccess()) {
				log.error("channel connect unsuccessful; {}:{} ", host, port);
				return DDF_FeedEvent.CHANNEL_CONNECT_FAILURE;
			}

			/* Send login command to JERQ */
			DDF_FeedEvent writeEvent = blockingWrite(FeedDDF.tcpLogin(username,
					password));

			if (writeEvent == DDF_FeedEvent.COMMAND_WRITE_FAILURE) {
				return DDF_FeedEvent.COMMAND_WRITE_FAILURE;
			}

			/* Send VERSION 3 command to JERQ */
			writeEvent = blockingWrite(FeedDDF.tcpVersion(VERSION));

			if (writeEvent == DDF_FeedEvent.COMMAND_WRITE_FAILURE) {
				return DDF_FeedEvent.COMMAND_WRITE_FAILURE;
			}

			/* Send timestamp command to JERQ */
			writeEvent = blockingWrite(FeedDDF.tcpGo(FeedDDF.SYMBOL_TIMESTAMP));

			if (writeEvent == DDF_FeedEvent.COMMAND_WRITE_FAILURE) {
				return DDF_FeedEvent.COMMAND_WRITE_FAILURE;
			}

			return DDF_FeedEvent.LOGIN_SENT;
		}

	}

	private final RunnerDDF heartbeatTask = new RunnerDDF() {

		private long delta;

		@Override
		public void runCore() {

			final int threadNumber = heartbeatTaskNumber.getAndIncrement();

			Thread.currentThread().setName(
					"# DDF HEARTBEAT TASK " + threadNumber);

			log.warn("started # DDF-heartbeat task {} ", threadNumber);

			startupLatch.countDown();

			try {

				while (!Thread.currentThread().isInterrupted()) {

					checkTime();
					Thread.sleep(2000); // This must be less than
										// HEARTBEAT_TIMEOUT
				}

			} catch (final InterruptedException e) {

				log.warn("# DDF-heartbeat task InterruptedException {}",
						threadNumber);

				Thread.currentThread().interrupt();

			} catch (final Exception e) {

				log.warn("# DDF-heartbeat exception: {}", e);

			}

			log.error("# DDF-heartbeat task death {}", threadNumber);

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
					executor.execute(new Thread(new Disconnector(
							"HEARTBEAT TIMEOUT")));

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
				log.warn("## "
						+ caller
						+ " is trying to call hardRestart, but we are still logging in.");
				return;
			}

			hardRestart(caller);

		}
	}

	// change how this is done

	public void setProxiedChannel(ChannelHandlerContext ctx, MessageEvent e,
			boolean success) {

		if (success) {
			this.channel = e.getChannel();

			// post ddf link connect
			postEvent(DDF_FeedEvent.LINK_CONNECT);

			final SimpleChannelHandler ddfHandler = new ChannelHandlerDDF(
					eventQueue, messageQueue);

			channel.getPipeline().addLast("ddf frame decoder",
					new MsgDeframerDDF());

			channel.getPipeline().addLast("ddf message decoder",
					new MsgDecoderDDF());

			// ### Encoders ###

			channel.getPipeline().addLast("ddf command encoder",
					new MsgEncoderDDF());

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

		log.warn("startUpProxy() - connecting...");

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
				postEvent(DDF_FeedEvent.SETTINGS_RETRIEVAL_FAILURE);

				connecting = false;
				return;
			}
		} catch (final Exception e) {
			log.error("Posting SETTINGS_RETRIEVAL_FAILURE");
			postEvent(DDF_FeedEvent.SETTINGS_RETRIEVAL_FAILURE);

			connecting = false;
			return;
		}

		final DDF_Server server = ddf_settings.getServer(serverType);

		loginProxy(username, password, server);

		log.warn("startUpProxy() done connecting...");

		connecting = false;

	}

}
