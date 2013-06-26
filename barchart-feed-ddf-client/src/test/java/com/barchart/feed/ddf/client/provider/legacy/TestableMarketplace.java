package com.barchart.feed.ddf.client.provider.legacy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.market.api.MarketDo;
import com.barchart.feed.base.market.api.MarketFactory;
import com.barchart.feed.base.market.api.MarketRegListener;
import com.barchart.feed.base.market.api.MarketSafeRunner;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.base.provider.RegTaker;
import com.barchart.feed.base.sub.SubscriptionHandler;
import com.barchart.feed.ddf.instrument.provider.InstrumentProviderWrapper;
import com.barchart.feed.ddf.market.provider.DDF_Marketplace;
import com.barchart.feed.ddf.market.provider.VarMarketEntityDDF;
import com.barchart.feed.inst.InstrumentService;
import com.barchart.util.values.api.Value;

public class TestableMarketplace extends DDF_Marketplace {

	private static final Logger log = LoggerFactory.getLogger(TestableMarketplace.class);
	
	protected TestableMarketplace(MarketFactory factory,
			InstrumentService<CharSequence> instLookup,
			SubscriptionHandler handler) {
		super(factory, instLookup, handler);
	}
	
	final ConcurrentMap<MarketTaker<?>, RegTaker<?>> takerMap = 
			new ConcurrentHashMap<MarketTaker<?>, RegTaker<?>>();
	
	private final CopyOnWriteArrayList<MarketRegListener> listenerList = //
			new CopyOnWriteArrayList<MarketRegListener>();
	
	public static final TestableMarketplace newTestableInstance(
			final SubscriptionHandler handler) {
		
		return new TestableMarketplace(new MarketFactory() {

			@Override
			public MarketDo newMarket(final Instrument instrument) {
				return new VarMarketEntityDDF(instrument);
			}

		}, new InstrumentProviderWrapper(), handler);
		
	}
	
	protected final void notifyRegListeners(final MarketDo market) {

		final Instrument inst = market.instrument();

		final Set<MarketEvent> events = market.regEvents();

		for (final MarketRegListener listener : listenerList) {
			try {
				listener.onRegistrationChange(inst, events);
			} catch (final Exception e) {
				log.error("", e);
			}
		}

	}
	
	@Override
	public <V extends Value<V>> boolean register(MarketTaker<V> taker) {
		
		if (!RegTaker.isValid(taker)) {
			return false;
		}
		
		RegTaker<?> regTaker = takerMap.get(taker);

		final boolean wasAdded = (regTaker == null);

		while (regTaker == null) {
			regTaker = new RegTaker<V>(taker);
			takerMap.putIfAbsent(taker, regTaker);
			regTaker = takerMap.get(taker);
		}
		
		if (wasAdded) {
			for (final Instrument inst : regTaker.getInstruments()) {

				if (!isValid(inst)) {
					continue;
				}

				if (!isRegistered(inst)) {
					register(inst);
				}

				final MarketDo market = marketMap.get(inst);

				market.runSafe(safeRegister, regTaker);

				notifyRegListeners(market);

			}
			
		} else {
			log.warn("already registered : {}", taker);
		}

		return wasAdded;

	}
	
	private final MarketSafeRunner<Void, RegTaker<?>> safeRegister = //
			new MarketSafeRunner<Void, RegTaker<?>>() {
				@Override
				public Void runSafe(final MarketDo market, final RegTaker<?> regTaker) {
					market.regAdd(regTaker);
					return null;
				}
			};

	@Override
	public <V extends Value<V>> boolean unregister(MarketTaker<V> taker) {
		
		if (!RegTaker.isValid(taker)) {
			return false;
		}

		final RegTaker<?> regTaker = takerMap.remove(taker);

		final boolean wasRemoved = (regTaker != null);

		if (wasRemoved) {
			for (final Instrument inst : regTaker.getInstruments()) {

				if (!isValid(inst)) {
					continue;
				}

				final MarketDo market = marketMap.get(inst);

				if(market==null){
					log.error("Failed to get MarketDo for " + inst.GUID().toString());
					continue;
				}
				
				market.runSafe(safeUnregister, regTaker);

				if (!market.hasRegTakers()) {
					unregister(inst);
				}

				notifyRegListeners(market);

			}
		} else {
			log.warn("was not registered : {}", taker);
		}

		return wasRemoved;

	}

	@Override
	public <V extends Value<V>> boolean update(MarketTaker<V> taker) {
		
		if (!RegTaker.isValid(taker)) {
			// debug logged already
			return false;
		}

		final RegTaker<?> regTaker = takerMap.get(taker);

		if (regTaker == null) {
			log.warn("Taker not registered : {}", taker);
			return false;
		}

		//

		final Set<Instrument> updateSet = new HashSet<Instrument>();
		final Set<Instrument> registerSet = new HashSet<Instrument>();
		final Set<Instrument> unregisterSet = new HashSet<Instrument>();
		final Set<Instrument> changeNotifySet = new HashSet<Instrument>();

		{

			final Instrument[] pastArray = regTaker.getInstruments();
			final Instrument[] nextArray = taker.bindInstruments();

			final Set<Instrument> pastSet = new HashSet<Instrument>(
					Arrays.asList(pastArray));
			final Set<Instrument> nextSet = new HashSet<Instrument>(
					Arrays.asList(nextArray));

			/** past & next */
			updateSet.addAll(pastSet);
			updateSet.retainAll(nextSet);

			/** next - past */
			registerSet.addAll(nextSet);
			registerSet.removeAll(updateSet);

			/** past - next */
			unregisterSet.addAll(pastSet);
			unregisterSet.removeAll(updateSet);

			/** past + next */
			changeNotifySet.addAll(updateSet);
			changeNotifySet.addAll(registerSet);
			changeNotifySet.addAll(unregisterSet);

		}

		//


		/** unregister : based on past */
		for (final Instrument inst : unregisterSet) {

			final MarketDo market = marketMap.get(inst);

			market.runSafe(safeUnregister, regTaker);

		}

		/** update : based on merge of next and past */
		for (final Instrument inst : updateSet) {

			final MarketDo market = marketMap.get(inst);

			market.runSafe(safeUpdate, regTaker);

		}

		/** past = next */
		regTaker.bind();

		/** register : based on next */
		for (final Instrument inst : registerSet) {

			if (!isValid(inst)) {
				continue;
			}

			if (!isRegistered(inst)) {
				register(inst);
			}

			final MarketDo market = marketMap.get(inst);

			market.runSafe(safeRegister, regTaker);

		}

		/** remove / notify */
		for (final Instrument inst : changeNotifySet) {

			final MarketDo market = marketMap.get(inst);

			if (!market.hasRegTakers()) {
				unregister(inst);
			}

			notifyRegListeners(market);

		}

		return true;
	}

	private final MarketSafeRunner<Void, RegTaker<?>> safeUnregister = //
			new MarketSafeRunner<Void, RegTaker<?>>() {
				@Override
				public Void runSafe(final MarketDo market, final RegTaker<?> regTaker) {
					market.regRemove(regTaker);
					return null;
				}
			};
			
	private final MarketSafeRunner<Void, RegTaker<?>> safeUpdate = //
			new MarketSafeRunner<Void, RegTaker<?>>() {
				@Override
				public Void runSafe(final MarketDo market, final RegTaker<?> regTaker) {
					market.regUpdate(regTaker);
					return null;
				}
			};
	
	public static final boolean isValid(final MarketTaker<?> taker) {

		if (taker == null) {
			log.debug("invalid : taker == null");
			return false;
		}

		final MarketEvent[] events = taker.bindEvents();

		if (events == null || events.length == 0) {
			log.debug("invalid : taker.bindEvents()");
			return false;
		}

		final MarketField<?> field = taker.bindField();

		if (field == null) {
			log.debug("invalid : taker.bindField()");
			return false;
		}

		final Instrument[] insts = taker.bindInstruments();

		if (insts == null) { // Removed size check
			log.debug("invalid : bindInstruments()");
			return false;
		}

		return true;

	}
	
	@Override
	public void add(final MarketRegListener listener) {
		log.debug("RegListener added to Marketplace");
		listenerList.addIfAbsent(listener);
	}

	@Override
	public void remove(final MarketRegListener listener) {
		listenerList.remove(listener);
	}
	
}
