package com.barchart.feed.client.provider;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.connection.Connection.Monitor;
import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.connection.TimestampListener;
import com.barchart.feed.api.consumer.ConsumerAgent;
import com.barchart.feed.api.consumer.MarketService;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.MarketData;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClientBase;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.datalink.enums.DDF_Transport;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;
import com.barchart.feed.ddf.instrument.provider.DDF_RxInstrumentProvider;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_ControlTimestamp;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.util.value.FactoryImpl;
import com.barchart.util.value.api.Factory;

public class BarchartMarketProvider implements MarketService {

	private static final Logger log = LoggerFactory.getLogger(
			BarchartMarketProvider.class);
			
	/* Value api factory */
	private static final Factory values = new FactoryImpl();
	
	private volatile DDF_FeedClientBase connection;
	
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
		
		// 
		
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
							.getStampUTC().asMillisUTC(), ""));
				}
			}

			if (message instanceof DDF_MarketBase) {
				final DDF_MarketBase marketMessage = (DDF_MarketBase) message;
				
				// *************************
				// TODO
				//maker.make(marketMessage);
			}

		}

	};
	
	
	@Override
	public <V extends MarketData<V>> ConsumerAgent register(
			MarketObserver<V> callback, Class<V> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Observable<Market> snapshot(InstrumentID instrument) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
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
	public Observable<Result<Instrument>> instrument(String... symbols) {
		return DDF_RxInstrumentProvider.fromString(SearchContext.NULL, symbols);
	}

	@Override
	public Observable<Result<Instrument>> instrument(SearchContext ctx,
			String... symbols) {
		return DDF_RxInstrumentProvider.fromString(ctx, symbols);
	}

}
