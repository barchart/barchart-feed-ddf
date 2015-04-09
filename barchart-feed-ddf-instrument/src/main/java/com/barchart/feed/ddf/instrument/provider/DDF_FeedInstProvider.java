package com.barchart.feed.ddf.instrument.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observer;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.base.provider.Symbology;
import com.barchart.feed.ddf.instrument.provider.InstrumentState.LoadState;

public final class DDF_FeedInstProvider {

	private static final long DEFAULT_TIMEOUT = 5000;
	private static final long REMOTE_LOOKUP_INTERVAL = 1000;

	private static final Logger log = LoggerFactory
			.getLogger(DDF_FeedInstProvider.class);

	private static final ConcurrentMap<String, List<InstrumentState>> symbolMap =
			DDF_RxInstrumentProvider.symbolMap;

	private static final ConcurrentMap<InstrumentID, InstrumentState> idMap =
			DDF_RxInstrumentProvider.idMap;

	private static final ArrayBlockingQueue<String> remoteSymbolQueue =
			new ArrayBlockingQueue<String>(1000 * 1000);
	
	private static final ArrayBlockingQueue<InstrumentID> remoteIDQueue =
			new ArrayBlockingQueue<InstrumentID>(1000 * 1000);

	private static final List<String> failedRemoteSymbolQueue =
			new CopyOnWriteArrayList<String>();
	
	private DDF_FeedInstProvider() {

	}

	/**
	 * Default executor service with dameon threads
	 */
	private volatile static ExecutorService executor = Executors.newCachedThreadPool(

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

	static {
		executor.submit(new RemoteRunner());
	}

	/**
	 * Bind framework executor.
	 *
	 * @param e
	 */
	public synchronized static void bindExecutorService(final ExecutorService e) {

		log.debug("Binding new executor service");

		executor.shutdownNow();
		executor = e;
		executor.submit(new RemoteRunner());
	}

	/**
	 * This takes an instrument stub from a feed message, and does several
	 * things.
	 *
	 * 1. Checks symbol against map. If the stub represents an instrument
	 * already in the system, then it returns the canonical reference to that
	 * instrument.
	 *
	 * 2. If the symbol is unknown, it will make a new InstrumentState from the
	 * info in the stub and begin an async lookup of info.
	 *
	 * @param inst
	 * @return
	 */
	public static Instrument fromMessage(final Instrument inst) {

		if (inst == null || inst.isNull()) {
			return Instrument.NULL;
		}
		
		/* NOTE id() in ddf is just the realtime symbol, not an actual GUID */
		final String symbol = Symbology.formatSymbol(inst.symbol());

		if (symbolMap.containsKey(symbol)) {
			return symbolMap.get(symbol).get(0);
		}

		/* New symbol, create stub */
		final InstrumentState instState = new DDF_Instrument(
				new InstrumentID(inst.symbol()), inst, LoadState.PARTIAL);

		final List<InstrumentState> list = new CopyOnWriteArrayList<InstrumentState>(); 
		list.add(instState);
		symbolMap.put(symbol, list);

		/* Asnyc lookup */
		try {
			remoteSymbolQueue.put(symbol);
		} catch (final Exception e) {
			log.error("Failed to add {} to remote symbol queue", e, symbol);
			failedRemoteSymbolQueue.add(symbol);
		}

		return instState;

	}

	private static void handleInstLookup(final InstrumentState state) {
		
		final InstrumentState iState = idMap.get(state.id());
		if (iState == null || iState.isNull()) {
			idMap.put(iState.id(), state);
			final List<InstrumentState> list = new ArrayList<InstrumentState>();
			list.add(state);
			symbolMap.put(state.symbol(), list);
		} else {
			iState.process(state);
		}
		
	}
	
	private static Observer<InstrumentResult> observer =
			new Observer<InstrumentResult>() {

		@Override
		public void onNext(final InstrumentResult result) {
			
			final String symbol = result.expression();

			/* If exception, add to failed */
			if (result.exception() != null) {
				log.debug("Failed Symbol Lookup for {}", symbol);
				failedRemoteSymbolQueue.add(symbol);
				return;
			}

			final Instrument inst = result.result();
			
			if (inst.isNull()) { // Ignore
				log.trace("Instrument result was empty for {}", symbol);
				return; 
			}

			/* This should never be true */
			if(!symbolMap.containsKey(symbol)) {
				final InstrumentState i = result.result();
				symbolMap.put(symbol, Arrays.asList(i));
				idMap.put(i.id(), i);
			}
			
			final InstrumentState iState = symbolMap.get(symbol).get(0);

			if (iState == null || iState.isNull()) {
				final InstrumentState i = result.result();
				symbolMap.put(symbol, Arrays.asList(i));
				idMap.put(i.id(), i);
			} else {
				iState.process(result.result());
			}

		}

		@Override
		public void onError(final Throwable error) {
			log.error("Exception in instrument observer", error);
		}

		@Override
		public void onCompleted() {
			/* Long lived observer, should not complete */
		}

	};

	private static class InstDefResult implements InstrumentResult {

		private final String symbol;
		private final InstrumentState inst;
		private final Throwable t;

		InstDefResult(final String symbol, final InstrumentState inst) {
			this.symbol = symbol;
			this.inst = inst;
			t = null;
		}

		InstDefResult(final String symbol, final Throwable t) {
			this.symbol = symbol;
			inst = InstrumentState.NULL;
			this.t = t;
		}

		@Override
		public InstrumentState result() {
			return inst;
		}

		@Override
		public String expression() {
			return symbol;
		}

		@Override
		public Throwable exception() {
			return t;
		}

	}

	static Callable<Map<String, List<InstrumentState>>> remoteSymbolBatch(final String symbols) {

		return new Callable<Map<String, List<InstrumentState>>>() {

			@Override
			public Map<String, List<InstrumentState>> call() throws Exception {
				return DDF_RxInstrumentProvider.remoteSymbolLookup(symbols);
			}

		};

	}

	static Callable<Map<InstrumentID, InstrumentState>> remoteIDBatch(final String ids) {

		return new Callable<Map<InstrumentID, InstrumentState>>() {

			@Override
			public Map<InstrumentID, InstrumentState> call() throws Exception {
				return DDF_RxInstrumentProvider.remoteIDLookup(ids);
			}
			
		};

	}
	
	static class RemoteRunner implements Runnable {

		private List<Future<Map<String, List<InstrumentState>>>> symbFutures =
				new ArrayList<Future<Map<String, List<InstrumentState>>>>();

		private final List<Callable<Map<String, List<InstrumentState>>>> symbCallables =
				new ArrayList<Callable<Map<String, List<InstrumentState>>>>();
		
		private List<Future<Map<InstrumentID, InstrumentState>>> idFutures =
				new ArrayList<Future<Map<InstrumentID, InstrumentState>>>();

		private final List<Callable<Map<InstrumentID, InstrumentState>>> idCallables =
				new ArrayList<Callable<Map<InstrumentID, InstrumentState>>>();

		@Override
		public void run() {

			try {

				while (!Thread.interrupted()) {

					Thread.sleep(REMOTE_LOOKUP_INTERVAL);

					/* ***** ***** Handle Queued Symbols ***** ***** */
					final List<String> symbols = new ArrayList<String>();
					remoteSymbolQueue.drainTo(symbols);
					
					for(final String q : DDF_RxInstrumentProvider.buildSymbolQueries(symbols)) {
						symbCallables.add(remoteSymbolBatch(q));
					}
						
					symbFutures = executor.invokeAll(symbCallables, DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);

					for (final Future<Map<String, List<InstrumentState>>> f : symbFutures) {
						
						final Map<String, List<InstrumentState>> map = f.get(10, TimeUnit.SECONDS);

						for (final Entry<String, List<InstrumentState>> e : map.entrySet()) {
							
							InstrumentState def = InstrumentState.NULL;
							if(!e.getValue().isEmpty()) {
								def = e.getValue().get(0);
							}

							if (def == null || def.isNull()) {
								observer.onNext(new InstDefResult(e.getKey(), 
										new Throwable("Could not find " + e.getKey())));
							} else {
								observer.onNext(new InstDefResult(e.getKey(), def));
							}

						}

					}

					symbFutures.clear();
					symbCallables.clear();
					
					/* ***** ***** Handle Queued IDs ***** ***** */
					final List<InstrumentID> ids = new ArrayList<InstrumentID>();
					remoteIDQueue.drainTo(ids);
					
					for(final String q : DDF_RxInstrumentProvider.buildIDQueries(ids)) {
						idCallables.add(remoteIDBatch(q));
					}
					
					idFutures = executor.invokeAll(idCallables, DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
					
					for(final Future<Map<InstrumentID, InstrumentState>> f : idFutures) {
						
						for(final Entry<InstrumentID, InstrumentState> e : f.get().entrySet()) {
							
							final InstrumentState def = e.getValue();
							
							if (def == null || def.isNull()) {
								// Do something
							} else {
								handleInstLookup(def);
							}
							
						}
						
					}
					
					idFutures.clear();
					idCallables.clear();

				}

			} catch (final Throwable t) {
				log.error("Exception in Remote Runner Thread", t);
			}

		}

	}

}
