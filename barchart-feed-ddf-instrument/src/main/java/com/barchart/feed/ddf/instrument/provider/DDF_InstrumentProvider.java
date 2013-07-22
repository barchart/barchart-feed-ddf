package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.util.HelperXML.XML_STOP;
import static com.barchart.feed.ddf.util.HelperXML.xmlFirstChild;

import java.util.Collection;
import java.util.HashMap;
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

import org.joda.time.DateTime;
import org.openfeed.proto.inst.InstrumentDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.ddf.symbol.enums.DDF_ExpireMonth;
import com.barchart.feed.ddf.util.HelperXML;
import com.barchart.feed.inst.participant.InstrumentState;

public final class DDF_InstrumentProvider {
	
	private static final long DEFAULT_TIMEOUT = 5000;
	private static final TimeUnit MILLIS = TimeUnit.MILLISECONDS;
	
	private static final Logger log = LoggerFactory
			.getLogger(DDF_InstrumentProvider.class);
	
	private static final ConcurrentMap<String, InstrumentState> symbolMap =
			new ConcurrentHashMap<String, InstrumentState>();
	
	private static volatile InstrumentDatabaseMap db = null;
	
	private DDF_InstrumentProvider() {
		
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
	public synchronized static void bindDatabaseMap(final InstrumentDatabaseMap map) {
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
		
		/* New symbol, create stub */
		final InstrumentState instState = InstrumentStateFactory.
				newInstrumentFromStub(inst);
		
		symbolMap.put(symbol, instState);
		log.debug("Put {} stub into map", symbol);
		
		/* Asnyc lookup */
		executor.submit(populateRunner(symbol));
		
		return instState;
		
	}
	
	public static Instrument fromSymbol(String symbol) {
		
		if(symbol == null || symbol.isEmpty()) {
			return Instrument.NULL;
		}
		
		symbol = formatSymbol(symbol);
		
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
	
	public static Map<String, Instrument> fromSymbols(
			final Collection<String> symbols) {
		
		final Map<String, Instrument> map = new HashMap<String, Instrument>();
		
		for(final String symbol : symbols) {
			map.put(symbol, fromSymbol(symbol));
		}
		
		return map;
		
	}

	public static Instrument fromHistorical(String symbol) {
		
		if(symbol == null || symbol.isEmpty()) {
			return Instrument.NULL;
		}
		
		symbol = formatHistoricalSymbol(symbol);
		
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
	//
	
	private static Runnable populateRunner(final String id) {
		
		return new Runnable() {

			@Override
			public void run() {
				
				try {
				
					// Should already have an instState in map, this just updates
					InstrumentState iState = symbolMap.get(id);
					
					if(iState == null) {
						log.error("Runner called for {}, expected stub missing", id);
						return;
					}
					
					InstrumentDefinition inst;
					
					// TODO make default db...
					if(db != null) {
						
						inst = db.get(id);
						
						if(inst != null) {
							
							/*
							 * This updates all references to the instrument
							 */
							iState.process(inst);
							
							log.debug("Instrument def found in db for {}", id);
							
							return;
						}
					
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
						
						log.debug("Instrument def found in remote for {}", id);
						
						return;
					}
					
					// Here inst was null, so some error needs to be handled
					
				} catch (final Throwable t) {
					log.error("Exception in runner {}", t);
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
					
					log.debug("Starting remote lookup for {}", symbol);
					
					final String symbolURI = urlInstrumentLookup(symbol);
					final Element root = HelperXML.xmlDocumentDecode(symbolURI);
					final Element tag = xmlFirstChild(root, "instrument", XML_STOP);
					final InstrumentDefinition instDOM = InstrumentXML.decodeXML(tag);
					
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
	
	private static final int YEAR;
	private static final char MONTH;
	
	static {
		final DateTime now = new DateTime();
		YEAR = now.year().get();
		MONTH = DDF_ExpireMonth.fromDateTime(now).code;
	}
	
	private static final char[] T_Z_O = new char[] {'2', '0', '1'};
	private static final char[] T_Z_T = new char[] {'2', '0', '2'};
	private static final char[] T_Z = new char[] {'2', '0'};
	private static final char[] O = new char[] {'1'};
	
	public static String formatSymbol(String symbol) {
		
		if(symbol == null) {
			return "";
		}
		
		final int len = symbol.length();
		
		if(len < 3) {
			return symbol;
		}
		
		/* Spread */
		if(symbol.charAt(0) == '_') {
			return symbol;
		}
		
		/* Option */
		if(symbol.matches(".+\\d(C|P|D|Q)$")) {
			
			
			int pIndex = len - 2;
			while(Character.isDigit(symbol.charAt(pIndex))) {
				
				if(pIndex <= 2) {
					log.error("Failed to format option {}", symbol);
					return symbol + " failed";
				}
				
				pIndex--;
			}

			final String price = symbol.substring(pIndex + 1, len-1);
			
			final char mon = symbol.charAt(pIndex);
			DDF_ExpireMonth sMon = DDF_ExpireMonth.fromCode(mon);
			DDF_ExpireMonth nowMon = DDF_ExpireMonth.fromCode(MONTH);
			final String y = (sMon.value >= nowMon.value) ? String.valueOf(YEAR) :
				String.valueOf(YEAR+1);
			
			final StringBuilder sb = new StringBuilder();
			
			sb.append(symbol.substring(0, pIndex + 1)); // prefix
			sb.append(y); // year
			sb.append("|"); // pipe
			sb.append(price);
			
			if(symbol.matches(".+(C|D)$")) {
				return sb.append("C").toString();
			} else {
				return sb.append("P").toString();
			}
			
		}
		
		/* e.g. GOOG */
		if(!Character.isDigit(symbol.charAt(len - 1))) {
			return symbol;
		}
		
		/* e.g. ESH3 */
		if(!Character.isDigit(symbol.charAt(len - 2))) {
			
			final StringBuilder sb = new StringBuilder(symbol);
			int last = Character.getNumericValue(symbol.charAt(len - 1));
			if(YEAR % 2010 < last) {
				return sb.insert(len - 1, T_Z_O).toString();
			} else if(YEAR % 2010 > last) {
				return sb.insert(len - 1, T_Z_T).toString();
			} else {
				if(symbol.charAt(len - 2) >= MONTH) {
					return sb.insert(len - 1, T_Z_O).toString();
				} else {
					return sb.insert(len - 1, T_Z_T).toString();
				}
			}
			
		}
		
		/* e.g. ESH13 */
		if(!Character.isDigit(symbol.charAt(len - 3))) {
			return new StringBuilder(symbol).insert(len-2, T_Z).toString();
		}
		
		return symbol;
		
	}

	public static String formatHistoricalSymbol(String symbol) {
		
		if(symbol == null) {
			return "";
		}
		
		if(symbol.length() < 3) {
			return symbol;
		}
		
		/* e.g. GOOG */
		if(!Character.isDigit(symbol.charAt(symbol.length() - 1))) {
			return symbol;
		}
		
		/* e.g. ESH3 */
		if(!Character.isDigit(symbol.charAt(symbol.length() - 2))) {
			return new StringBuilder(symbol).insert(symbol.length() - 1, O).toString();
		}
		
		return symbol;
	}
	
}
