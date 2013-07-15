package com.barchart.feed.ddf.instrument.provider.ext;

import static com.barchart.feed.ddf.util.HelperXML.XML_STOP;
import static com.barchart.feed.ddf.util.HelperXML.xmlFirstChild;

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
import org.w3c.dom.Element;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.ddf.instrument.provider.InstrumentXML;
import com.barchart.feed.ddf.instrument.provider.OpenFeedInstDBMap;
import com.barchart.feed.ddf.util.HelperXML;
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
		
		final InstrumentState instState = InstrumentStateFactory.
				newInstrumentFromStub(inst);
		
		symbolMap.put(symbol, instState);
		
		/* Asnyc lookup */
		executor.submit(populateRunner(symbol));
		
		return instState;
		
	}
	
	public static Instrument fromSymbol(final String symbol) {
		
		if(symbol == null || symbol.isEmpty()) {
			return Instrument.NULL;
		}
		
		if(symbolMap.containsKey(symbol)) {
			return symbolMap.get(symbol);
		}
		
		final InstrumentState instState = InstrumentStateFactory.
				newInstrument(symbol);
		
		symbolMap.put(symbol, instState);
		
		/* Asnyc lookup */
		executor.submit(populateRunner(symbol));
		
		return instState;
		
	}

	public static Instrument fromID(final String id) {
		// TODO
		return null;
	}
	
	public static Map<String, Instrument> fromID(final Collection<String> ids) {
		// TODO
		return null;
	}
	
	public static Map<String, Instrument> fromSymbol(
			final Collection<String> symbols) {
		// TODO
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
					
					InstrumentDefinition inst = db.get(id);
					
					if(inst != null) {
						
						/*
						 * This updates all references to the instrument
						 */
						iState.process(inst);
						
						return;
					}
					
					/* Remote lookup */
					final Future<InstrumentDefinition> futdef = executor.submit(remoteCallable(id));
					
					/* Runnable blocks here, waiting for remote */
					inst = futdef.get(DEFAULT_TIMEOUT, MILLIS);
					
					if(inst != null) {
						
						/*
						 * This updates all references to the instrument
						 */
						iState.process(inst);
						
						return;
					}
					
					// Here inst was null, so some error needs to be handled
					
				} catch (final Throwable t) {
					// TODO How to handle failure?
				}
				
			}
			
		};
		
	}
	
	private static final String SERVER_EXTRAS = "extras.ddfplus.com";

	private static final String urlInstrumentLookup(final CharSequence lookup) {
		return "http://" + SERVER_EXTRAS + "/instruments/?lookup=" + lookup;
	}
	
	static Callable<InstrumentDefinition> remoteCallable(final String symbol) {
		
		return new Callable<InstrumentDefinition>() {
	
			@Override
			public InstrumentDefinition call() throws Exception {
				
				try {
					
					final String symbolURI = urlInstrumentLookup(symbol);
					final Element root = HelperXML.xmlDocumentDecode(symbolURI);
					final Element tag = xmlFirstChild(root, "instrument", XML_STOP);
					final InstrumentDefinition instDOM = InstrumentXML.decodeXMLProto(tag);
					
					if(instDOM == null || instDOM == InstrumentDefinition.getDefaultInstance()) {
						log.warn("Empty instrument def returned from remote lookup: {}", symbol);
						return InstrumentDefinition.getDefaultInstance();
					}
					
					return instDOM;
					
				} catch (final Throwable t) {
					log.error("Remote instrument lookup callable exception: {}", t);
					return InstrumentDefinition.getDefaultInstance();
				}
				
			}
		
		};
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
