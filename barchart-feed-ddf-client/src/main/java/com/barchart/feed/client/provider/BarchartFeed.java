package com.barchart.feed.client.provider;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.handler.codec.replay.UnreplayableOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.Feed;
import com.barchart.feed.api.MarketCallback;
import com.barchart.feed.api.connection.ConnectionStateListener;
import com.barchart.feed.api.data.Cuvol;
import com.barchart.feed.api.data.Exchange;
import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.api.data.Market;
import com.barchart.feed.api.data.MarketData;
import com.barchart.feed.api.data.OrderBook;
import com.barchart.feed.api.data.TopOfBook;
import com.barchart.feed.api.data.Trade;
import com.barchart.feed.api.enums.MarketEventType;
import com.barchart.feed.api.inst.InstrumentFuture;
import com.barchart.feed.api.inst.InstrumentFutureMap;
import com.barchart.feed.client.api.TimestampListener;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClientBase;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.datalink.enums.DDF_Transport;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.feed.ddf.market.provider.DDF_Marketplace;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_ControlTimestamp;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;

public class BarchartFeed implements Feed {
	
	private static final Logger log = LoggerFactory
			.getLogger(BarchartFeed.class);
	
	protected volatile DDF_FeedClientBase feed = null;
	
	protected volatile DDF_Marketplace maker;
	
	private Executor executor = null;
	
	private ConnectionStateListener stateListener;
	
	private final CopyOnWriteArrayList<TimestampListener> timeStampListeners =
			new CopyOnWriteArrayList<TimestampListener>();
	
	public BarchartFeed() {
		this(new Executor() {

			private final AtomicLong counter = new AtomicLong(0);

			final String name = "# DDF Client - " + counter.getAndIncrement();

			@Override
			public void execute(final Runnable task) {
				log.debug("executing new runnable = " + task.toString());
				new Thread(task, name).start();
			}

		});
	}
	
	public BarchartFeed(final Executor ex) {
		executor = ex;
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
	public void login(final String username, final String password) {
		
		loginMain(username, password, DDF_Transport.TCP, executor);
		
	}
	
	/**
	 * Starts the data feed asynchronously. Notification of login success is
	 * reported by FeedStateListeners which are bound to this object.
	 * <p>
	 * Constructs a new feed client with the user's user name, password, desired
	 * transport protocol, and framework executor.
	 * 
	 * @param username
	 * @param password
	 * @param tp
	 * @param executor
	 */
	public void login(final String username, final String password,
			final DDF_Transport tp, final Executor executor) {

		loginMain(username, password, tp, executor);

	}
	
	/*
	 * Handles login. Non-blocking.
	 */
	private void loginMain(final String username, final String password,
			final DDF_Transport tp, final Executor executor) {

		if(maker != null) {
			maker.clearAll();
		}

		// Returns the FeedClientDDF
		feed = DDF_FeedClientFactory.newConnectionClient(tp, username,
				password, executor);

		feed.bindMessageListener(msgListener);
		
		if (stateListener != null) {
			feed.bindStateListener(stateListener);
		}
		
		feed.startup();
		
		maker = DDF_Marketplace.newInstance(feed);

	}
	
	@Override
	public void startup() {
		
	}
	
	@Override
	public void shutdown() {

		if(maker != null) {
			maker.clearAll();
		}

		if (feed != null) {
			feed.shutdown();
			feed = null;
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
					listener.handleTimestamp(((DDF_ControlTimestamp) message)
							.getStampUTC());
				}
			}

			if (message instanceof DDF_MarketBase) {
				final DDF_MarketBase marketMessage = (DDF_MarketBase) message;
				maker.make(marketMessage);
			}

		}

	};
	
	/**
	 * Applications which need to react to the connectivity state of the feed
	 * instantiate a FeedStateListener and bind it to the client.
	 * 
	 * @param listener
	 *            The listener to be bound.
	 */
	@Override
	public void bindConnectionStateListener(final ConnectionStateListener listener) {

		stateListener = listener;

		if (feed != null) {
			feed.bindStateListener(listener);
		}

	}
	
	/**
	 * Applications which require time-stamp or heart-beat messages from the
	 * data server instantiate a DDF_TimestampListener and bind it to the
	 * client.
	 * 
	 * @param listener
	 */
	public void bindTimestampListener(final TimestampListener listener) {
		timeStampListeners.add(listener);
	}
	
	/* ***** ***** ***** InstrumentService ***** ***** ***** */
	
	@Override
	public Instrument lookup(final CharSequence symbol) {
		return DDF_InstrumentProvider.find(symbol);
	}
	
	@Override
	public InstrumentFuture lookupAsync(final CharSequence symbol) {
		// TODO
		throw new UnreplayableOperationException();
	}
	
	@Override
	public Map<CharSequence, Instrument> lookup(
			final Collection<? extends CharSequence> symbolList) {
		return DDF_InstrumentProvider.find(symbolList);
	}

	@Override
	public InstrumentFutureMap<CharSequence> lookupAsync(
			final Collection<? extends CharSequence> symbols) {
		
		// TODO
		throw new UnreplayableOperationException();
	}

	/* ***** ***** ***** AgentBuilder ***** ***** ***** */
	
	@Override
	public <V extends MarketData<V>> Agent newAgent(final Class<V> dataType, 
			final MarketCallback<V> callback,	final MarketEventType... types) {
		
		return maker.newAgent(dataType, callback, types);
		
	}
	
	/* ***** ***** ***** Helper subscribe methods ***** ***** ***** */
	
	@Override
	public <V extends MarketData<V>> Agent subscribe(final Class<V> clazz,
			final MarketCallback<V> callback, final MarketEventType[] types,
			final String... symbols) {
		
		final Agent agent = newAgent(clazz, callback, types);
		
		agent.include(symbols);
		
		return agent;
	}

	@Override
	public <V extends MarketData<V>> Agent subscribe(final Class<V> clazz,
			final MarketCallback<V> callback, final MarketEventType[] types,
			final Instrument... instruments) {
		
		final Agent agent = newAgent(clazz, callback, types);
		
		agent.include(instruments);
		
		return agent;
	}

	@Override
	public <V extends MarketData<V>> Agent subscribe(final Class<V> clazz,
			final MarketCallback<V> callback, final MarketEventType[] types,
			final Exchange... exchanges) {

		final Agent agent = newAgent(clazz, callback, types);
		
		agent.include(exchanges);
		
		return agent;
	}

	@Override
	public Agent subscribeMarket(final MarketCallback<Market> callback,
			final String... symbols) {
		
		final Agent agent = newAgent(Market.class, callback, MarketEventType.ALL);
		
		agent.include(symbols);
		
		return agent;
	}

	@Override
	public Agent subscribeTrade(final MarketCallback<Trade> lastTrade,
			final String... symbols) {
		
		final Agent agent = newAgent(Trade.class, lastTrade, MarketEventType.TRADE);
		
		agent.include(symbols);
		
		return agent;
	}

	@Override
	public Agent subscribeBook(final MarketCallback<OrderBook> book,
			final String... symbols) {
		
		final Agent agent = newAgent(OrderBook.class, book, 
				MarketEventType.BOOK_SNAPSHOT, MarketEventType.BOOK_UPDATE);
		
		agent.include(symbols);
		
		return agent;
	}

	@Override
	public Agent subscribeTopOfBook(final MarketCallback<TopOfBook> top,
			final String... symbols) {
		
		final Agent agent = newAgent(TopOfBook.class, top, 
				MarketEventType.BOOK_SNAPSHOT, MarketEventType.BOOK_UPDATE);
		
		return agent;
	}

	@Override
	public Agent subscribeCuvol(final MarketCallback<Cuvol> cuvol,
			final String... symbols) {
		
		final Agent agent = newAgent(Cuvol.class, cuvol, 
				MarketEventType.CUVOL_SNAPSHOT, MarketEventType.CUVOL_UPDATE);
		
		return agent;
	}

}
