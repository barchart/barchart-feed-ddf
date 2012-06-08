/**
 * 
 */
package com.barchart.feed.ddf.client.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.instrument.enums.InstrumentField;
import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.MarketRegListener;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.ddf.client.api.DDF_Client;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.datalink.api.DDF_FeedStateListener;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.datalink.api.DDF_TimestampListener;
import com.barchart.feed.ddf.datalink.api.Subscription;
import com.barchart.feed.ddf.datalink.enums.TP;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.feed.ddf.market.api.DDF_MarketProvider;
import com.barchart.feed.ddf.market.provider.DDF_MarketService;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_ControlTimestamp;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.util.values.api.Value;

public class ClientDDF implements DDF_Client {

	private static final Logger log = LoggerFactory.getLogger(ClientDDF.class);

	private final DDF_FeedClient feed;

	private final DDF_MarketProvider maker = DDF_MarketService.newInstance();

	private final CopyOnWriteArrayList<DDF_TimestampListener> timeStampListeners =
			new CopyOnWriteArrayList<DDF_TimestampListener>();

	private final Executor defaultExecutor = new Executor() {

		private final AtomicLong counter = new AtomicLong(0);

		final String name = "# DDF Client - " + counter.getAndIncrement();

		@Override
		public void execute(final Runnable task) {
			new Thread(task, name).start();
		}

	};

	/* User obtains instances through DDF_ClientFactory static method */
	ClientDDF(final TP tp, final String username, final String password,
			final Executor executor) {

		if (executor == null) {

			feed =
					DDF_FeedClientFactory.newConnectionClient(tp, username, password,
							defaultExecutor);

		} else {

			feed =
					DDF_FeedClientFactory.newConnectionClient(tp, username, password,
							executor);
		}

		feed.bindMessageListener(msgListener);

		maker.add(instrumentSubscriptionListener);

	}

	@Override
	public void startup() {
		feed.startup();
	}

	@Override
	public void shutdown() {
		maker.clearAll();
		feed.shutdown();
	}

	/*
	 * This is where the instruments are registered and unregistered as needed
	 * by the market maker. Subscribe events are sent when the instrument has
	 * not been bound by a previously registered market taker. Unsubscribe
	 * events are sent only when the instrument is not needed by any previousl
	 * registerd market takers.
	 */
	private final MarketRegListener instrumentSubscriptionListener =
			new MarketRegListener() {

				@Override
				public void onRegistrationChange(
						final MarketInstrument instrument,
						final Set<MarketEvent> events) {

					/*
					 * The market maker denotes 'unsubscribe' with an empty
					 * event set
					 */
					if (events.isEmpty()) {
						log.debug("Unsubsctibing to "
								+ instrument.get(InstrumentField.ID));
						feed.unsubscribe(new Subscription(instrument, events));
					} else {
						log.debug("Subsctibing to "
								+ instrument.get(InstrumentField.ID));
						feed.subscribe(new Subscription(instrument, events));
					}

				}

			};

	/*
	 * This is the default message listener. Users wishing to handle raw
	 * messages will need to implement their own feed client.
	 */
	private final DDF_MessageListener msgListener = new DDF_MessageListener() {

		@Override
		public void handleMessage(final DDF_BaseMessage message) {

			if (message instanceof DDF_ControlTimestamp) {
				for (final DDF_TimestampListener listener : timeStampListeners) {
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

	@Override
	public void bindFeedStateListener(final DDF_FeedStateListener listener) {
		feed.bindStateListener(listener);
	}

	@Override
	public boolean isTakerRegistered(final MarketTaker<?> taker) {
		return maker.isRegistered(taker);
	}

	@Override
	public <V extends Value<V>> boolean addTaker(final MarketTaker<V> taker) {
		return maker.register(taker);
	}

	@Override
	public <V extends Value<V>> boolean removeTaker(final MarketTaker<V> taker) {
		return maker.unregister(taker);
	}

	@Override
	public MarketInstrument lookup(final String symbol) {
		return DDF_InstrumentProvider.find(symbol);
	}

	@Override
	public List<MarketInstrument> lookup(final List<String> symbolList) {
		final List<DDF_Instrument> list =
				DDF_InstrumentProvider.find(symbolList);

		return new ArrayList<MarketInstrument>(list);
	}

	@Override
	public <S extends MarketInstrument, V extends Value<V>> V take(
			final S instrument, final MarketField<V> field) {
		return maker.take(instrument, field);
	}

}
