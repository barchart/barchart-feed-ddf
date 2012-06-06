/**
 * 
 */
package com.barchart.feed.ddf.client.provider;

import static com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory.TransportProtocol.TCP;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import com.barchart.feed.base.instrument.enums.InstrumentField;
import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.ddf.client.api.MarketService_DDF;
import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.datalink.api.Subscription;
import com.barchart.feed.ddf.datalink.enums.DDF_FeedInterest;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.feed.ddf.market.api.DDF_MarketProvider;
import com.barchart.feed.ddf.market.provider.DDF_MarketService;
import com.barchart.util.values.api.Value;

/**
 * 
 * 
 */
public class MarketServiceProvider implements MarketService_DDF {

	protected final DDF_MarketProvider maker = DDF_MarketService.newInstance();

	protected final DDF_FeedClient feed;

	private final Executor executor = new Executor() {

		private final AtomicLong counter = new AtomicLong(0);

		final String name = "# DDF Feed Client - " + counter.getAndIncrement();

		@Override
		public void execute(final Runnable task) {
			new Thread(task, name).start();
		}

	};

	private final ConcurrentHashMap<String, Subscription> subscriptions =
			new ConcurrentHashMap<String, Subscription>();

	private final ConcurrentHashMap<String, List<MarketTaker<?>>> instrumentMap =
			new ConcurrentHashMap<String, List<MarketTaker<?>>>();

	public MarketServiceProvider(final String username, final String password) {

		feed =
				DDF_FeedClientFactory.newInstance(TCP, username, password,
						executor);

	}

	@Override
	public boolean isRegistered(final MarketTaker<?> taker) {
		return maker.isRegistered(taker);
	}

	@Override
	public <V extends Value<V>> boolean add(final MarketTaker<V> taker) {

		final Set<MarketEvent> events = new HashSet<MarketEvent>();

		for (final MarketEvent event : taker.bindEvents()) {
			events.add(event);
		}

		final Set<DDF_FeedInterest> interest =
				DDF_FeedInterest.fromEvents(events);

		for (final MarketInstrument inst : taker.bindInstruments()) {

			final String name = inst.get(InstrumentField.ID).toString();

			// May need to do a look up here to convert to a DDF symbol

			/* Only create, add, and subscribe if not already subscribed */
			if (!subscriptions.containsKey(name)) {
				final Subscription sub = new Subscription(name, interest);
				subscriptions.put(name, sub);
				feed.subscribe(sub);
			}

			/*
			 * Create a new list if this is the first time instrument has been
			 * subscribed
			 */
			if (!instrumentMap.containsKey(name)) {
				instrumentMap.put(name, new ArrayList<MarketTaker<?>>());
			}

			/* Associate instrument with taker */
			instrumentMap.get(name).add(taker);

		}

		return maker.register(taker);
	}

	@Override
	public <V extends Value<V>> boolean update(final MarketTaker<V> taker) {
		if (maker.isRegistered(taker)) {
			maker.unregister(taker);

			// Need to ensure the feed is still updating the correct set of
			// interests

		}
		return maker.register(taker);
	}

	@Override
	public <V extends Value<V>> boolean remove(final MarketTaker<V> taker) {

		/* Create dummy interest */
		final Set<DDF_FeedInterest> interest = new HashSet<DDF_FeedInterest>();

		for (final MarketInstrument inst : taker.bindInstruments()) {

			final String name = inst.get(InstrumentField.ID).toString();

			instrumentMap.get(name).remove(taker);

			/*
			 * If no takers reqire data from the instruemnt remove from map and
			 * unsubscribe
			 */
			if (instrumentMap.get(name).size() == 0) {

				instrumentMap.remove(name);

				final Subscription sub = new Subscription(name, interest);

				feed.unsubscribe(sub);

			}

		}

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
