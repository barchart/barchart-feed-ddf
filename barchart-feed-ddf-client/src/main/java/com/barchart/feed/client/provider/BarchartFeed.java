package com.barchart.feed.client.provider;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.connection.ConnectionFuture;
import com.barchart.feed.api.connection.TimestampListener;
import com.barchart.feed.api.model.data.Cuvol;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.data.MarketData;
import com.barchart.feed.api.model.data.Trade;
import com.barchart.feed.api.model.meta.Exchange;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClientBase;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.datalink.enums.DDF_Transport;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.feed.ddf.instrument.provider.InstrumentDBProvider;
import com.barchart.feed.ddf.instrument.provider.LocalInstrumentDBMap;
import com.barchart.feed.ddf.instrument.provider.ServiceDatabaseDDF;
import com.barchart.feed.ddf.market.provider.DDF_Marketplace;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_ControlTimestamp;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.util.value.api.Factory;
import com.barchart.util.value.api.FactoryLoader;

public class BarchartFeed implements Marketplace {
	
	private static final Logger log = LoggerFactory
			.getLogger(BarchartFeed.class);
	
	/* Value api factory */
	private static final Factory factory = FactoryLoader.load();
	
	/* Used if unable to retrieve system default temp directory */
	private static final String TEMP_DIR = "C:\\windows\\temp\\";
	private final File dbFolder;
	private static final long DB_UPDATE_TIMEOUT = 60; // seconds
	
	private final DDF_FeedClientBase connection;
	private final DDF_Marketplace maker;
	private final ExecutorService executor;
	
	private volatile LocalInstrumentDBMap dbMap = null;
	
	@SuppressWarnings("unused")
	private volatile Connection.Monitor stateListener;
	
	private final CopyOnWriteArrayList<TimestampListener> timeStampListeners =
			new CopyOnWriteArrayList<TimestampListener>();
	
	private final boolean useLocalInstDB;
	
	public BarchartFeed(final String username, final String password) {
		this(username, password, getDefault(), getTempFolder(), false);
	}
	
	BarchartFeed(final String username, final String password, 
			final ExecutorService ex, final File dbFolder, final boolean useDB) {
		
		executor  = ex;
		this.dbFolder = dbFolder;
		
		connection = DDF_FeedClientFactory.newConnectionClient(
				DDF_Transport.TCP, username, password, executor);
		
		connection.bindMessageListener(msgListener);
		
		maker = DDF_Marketplace.newInstance(connection);
		
		this.useLocalInstDB = useDB; 
		
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	/**
	 * Builder for different BarchartFeed configurations
	 */
	public static class Builder {
		
		private String username = "NULL";
		private String password = "NULL";
		private File dbFolder = getTempFolder();
		private boolean useLocalDB = false; 
		
		private ExecutorService executor = getDefault();
		
		public Builder() {
		}
		
		public Builder username(final String username) {
			this.username = username;
			return this;
		}
		
		public Builder password(final String password) {
			this.password = password;
			return this;
		}
		
		public Builder executor(final ExecutorService executor) {
			this.executor = executor;
			return this;
		}
		
		public Builder dbaseFolder(final File dbFolder) {
			this.dbFolder = dbFolder; 
			return this;
		}
		
		public Builder useLocalInstDatabase() {
			useLocalDB = true;
			return this;
		}
		
		public Marketplace build() {
			return new BarchartFeed(username, password, executor, dbFolder, 
					useLocalDB);
		}
		
	}
	
	private static ExecutorService getDefault() {
		return Executors.newCachedThreadPool( 
				
				new ThreadFactory() {

			final AtomicLong counter = new AtomicLong(0);
			
			@Override
			public Thread newThread(final Runnable r) {
				
				final Thread t = new Thread(r, "Feed thread " + 
						counter.getAndIncrement()); 
				
				t.setDaemon(true);
				
				return t;
			}
			
		});
	}
	
	/*
	 * Returns the default temp folder
	 */
	private static File getTempFolder() {
		
		try {
			
			return File.createTempFile("temp", null).getParentFile();
			
		} catch (IOException e) {
			log.warn("Unable to retrieve system temp folder, using default {}", 
					TEMP_DIR);
			return new File(TEMP_DIR);
		}
		
	}
	
	/* ***** ***** ***** ConnectionLifecycle ***** ***** ***** */
	
	/**
	 * Starts the data feed asynchronously. Notification of login success is
	 * reported by FeedStateListeners which are bound to this object.
	 * <p>
	 * Constructs a new feed client with the user's user name and password. The
	 * transport protocol defaults to TCP and a default executor are used.
	 * 
	 * @param username
	 * @param password
	 */
	
	private final AtomicBoolean isStartingup = new AtomicBoolean(false);
	private final AtomicBoolean isShuttingdown = new AtomicBoolean(false);
	
	@Override
	public synchronized ConnectionFuture<Marketplace> startup() {
		
		// Consider dummy future?
		if(isStartingup.get()) {
			throw new IllegalStateException("Startup called while already starting up");
		}
		
		if(isShuttingdown.get()) {
			throw new IllegalStateException("Startup called while shutting down");
		}
		
		isStartingup.set(true);
		
		final ConnectionFuture<Marketplace> future = new ConnectionFuture<Marketplace>();
		
		executor.execute(new StartupRunnable(future));
		
		return future;
		
	}
	
	private final class StartupRunnable implements Runnable {

		private final ConnectionFuture<Marketplace> future;
		
		StartupRunnable(final ConnectionFuture<Marketplace> future) {
			this.future = future;
		}

		/*
		 * Ensures instrument database is installed/updated before
		 * user is able to send subscriptions to JERQ
		 */
		@Override
		public void run() {
			
			try {
			
				if(useLocalInstDB) {
				
					dbMap = InstrumentDBProvider.getMap(dbFolder);
					
					final ServiceDatabaseDDF dbService = new ServiceDatabaseDDF(dbMap, executor);
					
					DDF_InstrumentProvider.bind(dbService);
					
					final Future<Boolean> dbUpdate = executor.submit(
							InstrumentDBProvider.updateDBMap(dbFolder, dbMap));
					
					dbUpdate.get(DB_UPDATE_TIMEOUT, TimeUnit.SECONDS);
					
				}
				
				connection.startup();
			
			} catch (final Throwable t) {
				
				isStartingup.set(false);
				
				future.fail(t);
				
				return;
			}
			
			isStartingup.set(false);
			
			future.succeed(BarchartFeed.this);
			
		}
		
	}
	
	@Override
	public synchronized ConnectionFuture<Marketplace> shutdown() {

		// Consider dummy future?
		if(isStartingup.get()) {
			throw new IllegalStateException("Shutdown called while shutting down");
		}
		
		if(isShuttingdown.get()) {
			throw new IllegalStateException("Shutdown called while already shutting down");
		}
		
		isShuttingdown.set(true);
		
		final ConnectionFuture<Marketplace> future = new ConnectionFuture<Marketplace>();
		
		executor.execute(new ShutdownRunnable(future));
		
		return future;

	}
	
	private final class ShutdownRunnable implements Runnable {

		private final ConnectionFuture<Marketplace> future;
		
		ShutdownRunnable(final ConnectionFuture<Marketplace> future) {
			this.future = future;
		}
		
		@Override
		public void run() {
			
			try {
				
				if(maker != null) {
					maker.clearAll();
				}
				
				if(dbMap != null) {
					dbMap.close();
				}
	
				connection.shutdown();
				
				log.debug("Barchart Feed shutdown");
				
			} catch (final Throwable t) {
				
				log.error(t.getMessage());
				
				isShuttingdown.set(false);
				
				future.fail(t);
				
				return;
			}
			
			
			isShuttingdown.set(false);
			
			future.succeed(BarchartFeed.this);
			
			log.debug("Barchart Feed shutdown succeeded");
			
		}
		
	}
	
	/*
	 * This is the default message listener. Users wishing to handle raw
	 * messages will need to implement their own feed client.
	 */
	private final DDF_MessageListener msgListener = new DDF_MessageListener() {

		@Override
		public void handleMessage(final DDF_BaseMessage message) {

			if (message instanceof DDF_ControlTimestamp) {
				for (final TimestampListener listener : timeStampListeners) {
					listener.listen(factory.newTime(((DDF_ControlTimestamp) message)
							.getStampUTC().asMillisUTC(), ""));
				}
			}

			if (message instanceof DDF_MarketBase) {
				final DDF_MarketBase marketMessage = (DDF_MarketBase) message;
				maker.make(marketMessage);
			}

		}

	};
	
	@Override
	public void bindConnectionStateListener(final Connection.Monitor listener) {

		stateListener = listener;

		if (connection != null) {
			connection.bindStateListener(listener);
		} else {
			throw new RuntimeException("Connection state listener already bound");
		}

	}
	
	@Override
	public void bindTimestampListener(final TimestampListener listener) {
		
		if(listener != null) {
			timeStampListeners.add(listener);
		}
		
	}
	
	/* ***** ***** ***** AgentBuilder ***** ***** ***** */
	
	@Override
	public <V extends MarketData<V>> Agent newAgent(final Class<V> dataType, 
			final MarketObserver<V> callback) {
		
		return maker.newAgent(dataType, callback);
		
	}
	
	/* ***** ***** ***** Helper subscribe methods ***** ***** ***** */
	
	@Override
	public <V extends MarketData<V>> Agent subscribe(final Class<V> clazz,
			final MarketObserver<V> callback, final String... symbols) {
		
		final Agent agent = newAgent(clazz, callback);
		
		agent.include(symbols);
		
		return agent;
	}

	@Override
	public <V extends MarketData<V>> Agent subscribe(final Class<V> clazz,
			final MarketObserver<V> callback, final Instrument... instruments) {
		
		final Agent agent = newAgent(clazz, callback);
		
		agent.include(instruments);
		
		return agent;
	}

	@Override
	public <V extends MarketData<V>> Agent subscribe(final Class<V> clazz,
			final MarketObserver<V> callback, final Exchange... exchanges) {

		final Agent agent = newAgent(clazz, callback);
		
		agent.include(exchanges);
		
		return agent;
	}

	@Override
	public Agent subscribeMarket(final MarketObserver<Market> callback,
			final String... symbols) {
		
		final Agent agent = newAgent(Market.class, callback);
		
		agent.include(symbols);
		
		return agent;
	}

	@Override
	public Agent subscribeTrade(final MarketObserver<Trade> lastTrade,
			final String... symbols) {
		
		final Agent agent = newAgent(Trade.class, lastTrade);
		
		agent.include(symbols);
		
		return agent;
	}

	@Override
	public Agent subscribeBook(final MarketObserver<Book> book,
			final String... symbols) {
		
		final Agent agent = newAgent(Book.class, book);
		
		agent.include(symbols);
		
		return agent;
	}

	@Override
	public Agent subscribeCuvol(final MarketObserver<Cuvol> cuvol,
			final String... symbols) {
		
		final Agent agent = newAgent(Cuvol.class, cuvol);
		
		return agent;
	}

}
