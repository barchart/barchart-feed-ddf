package com.barchart.feed.client.provider;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.connection.Connection.Monitor;
import com.barchart.feed.api.connection.Subscription;
import com.barchart.feed.api.connection.TimestampListener;
import com.barchart.feed.api.consumer.ConsumerAgent;
import com.barchart.feed.api.consumer.MarketService;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.MarketData;
import com.barchart.feed.api.model.meta.Exchange;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.ExchangeID;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.base.sub.SubscriptionHandler;
import com.barchart.feed.ddf.datalink.api.FeedClient;
import com.barchart.feed.ddf.datalink.api.FeedClient.DDF_MessageListener;
import com.barchart.feed.ddf.datalink.api.FeedClient.DDF_Transport;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;
import com.barchart.feed.ddf.datalink.provider.DDF_SubscriptionHandler;
import com.barchart.feed.ddf.instrument.provider.DDF_MetadataServiceWrapper;
import com.barchart.feed.ddf.instrument.provider.DDF_RxInstrumentProvider;
import com.barchart.feed.ddf.market.provider.DDF_ConsumerMarketProvider;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_ControlTimestamp;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.util.value.ValueFactoryImpl;
import com.barchart.util.value.api.ValueFactory;

public class BarchartMarketProvider implements MarketService {

	private static final Logger log = LoggerFactory.getLogger(
			BarchartMarketProvider.class);
			
	/* Value api factory */
	private static final ValueFactory values = ValueFactoryImpl.instance;
	
	private volatile FeedClient connection;
	private final DDF_ConsumerMarketProvider maker;
	private final ExecutorService executor;
	private final SubscriptionHandler subHandler;
	
	@SuppressWarnings("unused")
	private volatile Connection.Monitor stateListener;
	
	private final CopyOnWriteArrayList<TimestampListener> timeStampListeners =
			new CopyOnWriteArrayList<TimestampListener>();
	
	/* ***** ***** ***** Begin Constructors ***** ***** ***** */
	
	public BarchartMarketProvider(final String username, final String password) {
		this(username, password, getDefault());
	}
	
	public BarchartMarketProvider(final String username, final String password, 
			final ExecutorService exe) {
		
		connection = DDF_FeedClientFactory.newConnectionClient(
				DDF_Transport.TCP, username, password, exe);
		
		connection.bindMessageListener(msgListener);
		
		subHandler = new DDF_SubscriptionHandler(connection, new DDF_MetadataServiceWrapper());
		
		maker = DDF_ConsumerMarketProvider.newInstance(subHandler);
		
		executor = exe;
		
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
	 * This is the default message listener. Users wishing to handle raw
	 * messages will need to implement their own feed client.
	 */
	private final DDF_MessageListener msgListener = new DDF_MessageListener() {

		@Override
		public void handleMessage(final DDF_BaseMessage message) {

			if (message instanceof DDF_ControlTimestamp) {
				for (final TimestampListener listener : timeStampListeners) {
					listener.listen(values.newTime(((DDF_ControlTimestamp) message)
							.getStampUTC().asMillisUTC()));
				}
			}

			if (message instanceof DDF_MarketBase) {
				final DDF_MarketBase marketMessage = (DDF_MarketBase) message;
				maker.make(marketMessage);
			}
		}
	};
	
	/* ***** ***** ***** Begin Lifecycle ***** ***** ***** */
	
	private final AtomicBoolean isStartingup = new AtomicBoolean(false);
	private final AtomicBoolean isShuttingdown = new AtomicBoolean(false);
	
	@Override
	public void startup() {
		
		if(isStartingup.get()) {
			throw new IllegalStateException("Startup called while already starting up");
		}
		
		if(isShuttingdown.get()) {
			throw new IllegalStateException("Startup called while shutting down");
		}
		
		isStartingup.set(true);
		executor.execute(new StartupRunnable());
		
		/** Currently just letting it run */
	}

	private final class StartupRunnable implements Runnable {
		
		@Override
		public void run() {
			
			try {
			
				log.debug("Startup Runnable starting");
				connection.startup();
			
			} catch (final Throwable t) {
				log.error("Exception starting up marketplace {}", t);
				isStartingup.set(false);
				return;
			}
			
			isStartingup.set(false);
		}
		
	}
	
	@Override
	public void shutdown() {
		
		if(isStartingup.get()) {
			throw new IllegalStateException(
					"Shutdown called while starting up");
		}
		
		if(isShuttingdown.get()) {
			throw new IllegalStateException(
					"Shutdown called while already shutting down");
		}
		
		isShuttingdown.set(true);
		executor.execute(new ShutdownRunnable());
		
		/** Currently just letting it run */
	}
	
	private final class ShutdownRunnable implements Runnable {

		@Override
		public void run() {
			
			try {
				if(maker != null) {
					maker.clearAll();
				}
				
				connection.shutdown();
				log.debug("Barchart Feed shutdown");
				
			} catch (final Throwable t) {
				
				log.error("Error {}", t);
				isShuttingdown.set(false);
				return;
			}
			
			isShuttingdown.set(false);
			log.debug("Barchart Feed shutdown succeeded");
		}
	}
	
	/* ***** ***** ***** Begin MarketProvider Methods ***** ***** ***** */
	
	@Override
	public <V extends MarketData<V>> ConsumerAgent register(final MarketObserver<V> callback, 
			final Class<V> clazz) {
		return maker.register(callback, clazz);
	}

	@Override
	public Observable<Market> snapshot(InstrumentID instID) {
		return maker.snapshot(instID);
	}

	@Override
	public void bindConnectionStateListener(Monitor listener) {
		
		stateListener = listener;

		if (connection != null) {
			connection.bindStateListener(listener);
		} else {
			throw new RuntimeException("Connection state listener already bound");
		}
		
	}

	@Override
	public void bindTimestampListener(TimestampListener listener) {
		
		if(listener != null) {
			timeStampListeners.add(listener);
		}
		
	}
	
	@Override
	public Observable<Map<InstrumentID, Instrument>> instrument(final InstrumentID... ids) {
		return DDF_RxInstrumentProvider.fromID(ids);
	}

	@Override
	public Observable<Result<Instrument>> instrument(final String... symbols) {
		return DDF_RxInstrumentProvider.fromString(SearchContext.NULL, symbols);
	}

	@Override
	public Observable<Result<Instrument>> instrument(final SearchContext ctx, final String... symbols) {
		return DDF_RxInstrumentProvider.fromString(ctx, symbols);
	}

	/* ***** ***** SubscriptionService ***** ***** */
	
	@Override
	public Map<InstrumentID, Subscription<Instrument>> instruments() {
		return maker.instruments();
	}

	@Override
	public Map<ExchangeID, Subscription<Exchange>> exchanges() {
		return maker.exchanges();
	}
	
	@Override
	public int numberOfSubscriptions() {
		return subHandler.subscriptions().size();
	}

}
