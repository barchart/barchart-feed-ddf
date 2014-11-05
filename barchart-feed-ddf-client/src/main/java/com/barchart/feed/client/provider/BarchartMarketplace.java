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

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.connection.Subscription;
import com.barchart.feed.api.connection.TimestampListener;
import com.barchart.feed.api.consumer.ConsumerAgent;
import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.data.Cuvol;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.MarketData;
import com.barchart.feed.api.model.data.Trade;
import com.barchart.feed.api.model.meta.Exchange;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.ExchangeID;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClientBase;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.datalink.enums.DDF_Transport;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;
import com.barchart.feed.ddf.market.provider.DDF_Marketplace;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_ControlTimestamp;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.util.value.ValueFactoryImpl;
import com.barchart.util.value.api.ValueFactory;

public class BarchartMarketplace implements Marketplace {
	
	private static final Logger log = LoggerFactory
			.getLogger(BarchartMarketplace.class);
	
	public enum FeedType {
		NULL, CONNECTION, CONNECTION_PROXY, LISTENER_TCP, LISTENER_UDP
	}

	/* Value api factory */
	private static final ValueFactory factory = ValueFactoryImpl.instance;

	protected volatile DDF_FeedClientBase connection;
	protected volatile DDF_Marketplace maker;
	private final ExecutorService executor;

	@SuppressWarnings("unused")
	private volatile Connection.Monitor stateListener;

	private final CopyOnWriteArrayList<TimestampListener> timeStampListeners =
			new CopyOnWriteArrayList<TimestampListener>();

	public BarchartMarketplace(final String username, final String password) {
		this(username, password, getDefault(), FeedType.CONNECTION, 0);
	}

	protected BarchartMarketplace(
			final String username, 
			final String password,
			final ExecutorService ex,
			final FeedType type,
			final int port) {

		executor = ex;

		switch(type) {
		default:
			connection = DDF_FeedClientFactory.newConnectionClient(
				DDF_Transport.TCP,
				username, 
				password, 
				executor);
			break;
		case LISTENER_TCP:
			connection = DDF_FeedClientFactory.newStatelessTCPListenerClient(port, false, executor);
			break;
		case LISTENER_UDP:
			connection = DDF_FeedClientFactory.newUDPListenerClient(port, false, executor);
			break;
		}
		
		connection.bindMessageListener(msgListener);

		maker = DDF_Marketplace.newInstance(connection);

	}

	protected BarchartMarketplace(final DDF_Marketplace marketplace) {
		this(marketplace, getDefault());
	}

	protected BarchartMarketplace(final DDF_Marketplace marketplace,
			final ExecutorService ex) {

		executor = ex;
		maker = marketplace;

	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder for different BarchartFeed configurations
	 */
	public static class Builder {

		private String username = "NULL USERNAME";
		private String password = "NULL PASSWORD";
		private FeedType feedType = FeedType.NULL;
		private int port = 0;

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
		
		public Builder feedType(final FeedType type) {
			feedType = type;
			return this;
		}
		
		public Builder port(final int port) {
			this.port = port;
			return this;
		}

		public Marketplace build() {
			return new BarchartMarketplace(username, password, executor, feedType, port);
		}

	}

	private static ExecutorService getDefault() {
		return Executors.newCachedThreadPool(

		new ThreadFactory() {

			final AtomicLong counter = new AtomicLong(0);

			@Override
			public Thread newThread(final Runnable r) {

				final Thread t = new Thread(r, "Feed thread " + counter.getAndIncrement());

				t.setDaemon(true);

				return t;
			}

		});
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
	public synchronized void startup() {

		if (isStartingup.get()) {
			throw new IllegalStateException(
					"Startup called while already starting up");
		}

		if (isShuttingdown.get()) {
			throw new IllegalStateException(
					"Startup called while shutting down");
		}

		isStartingup.set(true);

		executor.execute(new StartupRunnable());

		/** Currently just letting it run */

	}

	private final class StartupRunnable implements Runnable {

		/*
		 * Ensures instrument database is installed/updated before user is able
		 * to send subscriptions to JERQ
		 */
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
	public synchronized void shutdown() {

		if (isStartingup.get()) {
			throw new IllegalStateException("Shutdown called while starting up");
		}

		if (isShuttingdown.get()) {
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

				if (maker != null) {
					maker.clearAll();
				}

				// dbUpdater.cancel(true);

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

	/*
	 * This is the default message listener. Users wishing to handle raw
	 * messages will need to implement their own feed client.
	 */
	protected final DDF_MessageListener msgListener = new DDF_MessageListener() {

		@Override
		public void handleMessage(final DDF_BaseMessage message) {

			if (message instanceof DDF_ControlTimestamp) {
				for (final TimestampListener listener : timeStampListeners) {
					listener.listen(factory.newTime(
							((DDF_ControlTimestamp) message)
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

		if (listener != null) {
			timeStampListeners.add(listener);
		}

	}

	/* ***** ***** SnapshotProvider ***** ***** */

	@Override
	public Market snapshot(final Instrument instrument) {
		return maker.snapshot(instrument);
	}

	@Override
	public Market snapshot(final String symbol) {
		return maker.snapshot(symbol);
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

		agent.include(symbols).subscribe();

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

		agent.include(symbols).subscribe();

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

		agent.include(symbols).subscribe();

		return agent;
	}

	@Override
	public Agent subscribeCuvol(final MarketObserver<Cuvol> cuvol,
			final String... symbols) {

		final Agent agent = newAgent(Cuvol.class, cuvol);
		
		agent.include(symbols).subscribe();

		return agent;
	}

	@Override
	public <V extends MarketData<V>> ConsumerAgent register(
			final MarketObserver<V> callback, final Class<V> clazz) {
		return maker.register(callback, clazz);
	}

	@Override
	public Observable<Market> snapshot(final InstrumentID instID) {
		return maker.snapshot(instID);
	}

	@Override
	public Observable<Map<InstrumentID, Instrument>> instrument(
			final InstrumentID... ids) {
		return maker.instrument(ids);
	}

	@Override
	public Observable<Result<Instrument>> instrument(final String... symbols) {
		return maker.instrument(symbols);
	}

	@Override
	public Observable<Result<Instrument>> instrument(final SearchContext ctx,
			final String... symbols) {
		return maker.instrument(ctx, symbols);
	}

	@Override
	public Map<InstrumentID, Subscription<Instrument>> instruments() {
		return maker.instruments();
	}

	@Override
	public Map<ExchangeID, Subscription<Exchange>> exchanges() {
		return maker.exchanges();
	}

}
