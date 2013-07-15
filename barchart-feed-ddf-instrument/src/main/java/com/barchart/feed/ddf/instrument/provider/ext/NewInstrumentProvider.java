package com.barchart.feed.ddf.instrument.provider.ext;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.openfeed.proto.inst.InstrumentDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.ddf.instrument.provider.OpenFeedInstDBMap;
import com.barchart.market.provider.api.model.meta.InstrumentState;

public final class NewInstrumentProvider {
	
	private static final long DEFAULT_TIMEOUT = 5000;
	private static final TimeUnit MILLIS = TimeUnit.MILLISECONDS;
	
	private static final Logger log = LoggerFactory
			.getLogger(NewInstrumentProvider.class);
	
	private static final ConcurrentMap<String, InstrumentState> symbolMap =
			new ConcurrentHashMap<String, InstrumentState>();
	
	private static volatile OpenFeedInstDBMap db = null;
	
	private NewInstrumentProvider() {
		
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
	
	/**
	 * Bind framework executor.
	 * @param e
	 */
	public synchronized static void bindExecutorService(final ExecutorService e) {
		executor = e;
	}
	
	/**
	 * @param map Bind an already built db map
	 */
	public synchronized static void bindDatabaseMap(final OpenFeedInstDBMap map) {
		db = map;
	}
	
	/**
	 * This takes an instrument stub from a feed message, and does several things.
	 * 
	 * 1. Checks symbol against map.  If the stub represents an instrument already
	 * in the system, then it returns the canonical reference to that instrument.
	 * 
	 * 2. If the symbol is unknown, it will make a new InstrumentState from the info
	 * in the stub and begin an async lookup of info.
	 * 
	 * 
	 * @param inst
	 * @return
	 */
	public static Instrument fromMessage(final Instrument inst) {
		
		if(inst == null || inst.isNull()) {
			return Instrument.NULL;
		}
		
		/* NOTE id() in ddf is just the realtime symbol, not an actual GUID */
		final String symbol = formatSymbol(inst.id().toString());
		
		if(symbolMap.containsKey(symbol)) {
			return symbolMap.get(symbol);
		}
		
		final InstrumentState instState = InstrumentStateFactory.newInstrumentFromStub(inst);
		
		symbolMap.put(symbol, instState);
		
		// start async lookup / populate
		executor.submit(populateRunner(symbol));
		
		return instState;
	}
	
	public static Instrument fromSymbol(final String id) {
		
		if(id == null || id.isEmpty()) {
			return Instrument.NULL;
		}
		
		if(symbolMap.containsKey(id)) {
			return symbolMap.get(id);
		}
		
		// Here we need to run search, populate data
		
		return InstrumentStateFactory.newInstrument(id);
		
	}

	public static Instrument fromID(final String id) {
		return null;
	}
	
	public static Map<String, Instrument> fromID(final Collection<String> ids) {
		return null;
	}
	
	public static Map<String, Instrument> fromSymbol(
			final Collection<String> symbols) {
		return null;
	}
	
	//
	
	public static String formatSymbol(final String symbol) {
		
		StringBuffer sb = new StringBuffer();
		
		// TODO
		
		return sb.toString();
		
	}
	
	private static Runnable populateRunner(final String id) {
		
		return new Runnable() {

			@Override
			public void run() {
				
				try {
				
					// Should already have an instState in map, this just updates
					InstrumentState iState = symbolMap.get(id);
					
					if(iState == null) {
						// TODO ????
					}
					
					// TODO Need to convert over to open feed def
					InstrumentDefinition inst = db.get(id);
					
					if(inst != null) {
						
						// TODO null here should be the inst from the db
						/*
						 * This updates all references to the instrument
						 */
						iState.process(null);
						
						return;
					}
					
					/* Remote lookup */
					final Future<InstrumentDefinition> futdef = executor.submit(remoteCallable(id));
					final InstrumentDefinition def = futdef.get(DEFAULT_TIMEOUT, MILLIS);
					
					if(iState == null) {
						// TODO Problem
					}
					
					
					
					
				} catch (final Throwable t) {
					// TODO How to handle failure?
				}
				
			}
			
		};
		
	}
	
	private static Callable<org.openfeed.proto.inst.InstrumentDefinition> remoteCallable(final String id) {
		
		return new Callable<org.openfeed.proto.inst.InstrumentDefinition>() {
	
			@Override
			public org.openfeed.proto.inst.InstrumentDefinition call() throws Exception {
				
				return null;
				
			}
		
		};
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
