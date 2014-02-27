package com.barchart.feed.ddf.instrument.provider;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.openfeed.proto.inst.InstrumentDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import rx.Observer;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.base.provider.Symbology;
import com.barchart.feed.inst.InstrumentDefinitionResult;
import com.barchart.feed.inst.participant.InstrumentState;
import com.barchart.feed.inst.provider.InstrumentFactory;

public final class DDF_InstrumentProvider {
	
	private static final long DEFAULT_TIMEOUT = 5000;
	private static final TimeUnit MILLIS = TimeUnit.MILLISECONDS;
	private static final int MAX_URL_LEN = 7500;
	private static final long REMOTE_LOOKUP_INTERVAL = 1000;
	
	private static final Logger log = LoggerFactory
			.getLogger(DDF_InstrumentProvider.class);
	
	private static final ConcurrentMap<String, List<InstrumentState>> symbolMap =
			DDF_RxInstrumentProvider.symbolMap;
	
	private static final ConcurrentMap<InstrumentID, InstrumentState> idMap =
			DDF_RxInstrumentProvider.idMap;
	
	private static final ArrayBlockingQueue<String> remoteQueue = 
			new ArrayBlockingQueue<String>(1000 * 1000);
	
	private static final List<String> failedRemoteQueue =
			new CopyOnWriteArrayList<String>();
	
	static final String cqgInstLoopURL(final CharSequence lookup) {
		return "http://" + SERVER_EXTRAS + "/symbology/?symbol=" + lookup + "&provider=CQG";
	}
	
	private DDF_InstrumentProvider() {
		
	}
	
	/**
	 * Default executor service with dameon threads
	 */
	// Consider ExecutorCompletionService
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
	 * @param e
	 */
	public synchronized static void bindExecutorService(final ExecutorService e) {
		
		log.debug("Binding new executor service");
		
		executor.shutdownNow();
		executor = e;
		executor.submit(new RemoteRunner());
	}
	
	public static Instrument fromID(final InstrumentID id) {
		if(!idMap.containsKey(id)) {
			return Instrument.NULL;
		} else {
			return idMap.get(id);
		}
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
		final String symbol = Symbology.formatSymbol(inst.id().toString());
		
		if(symbolMap.containsKey(symbol)) {
			return symbolMap.get(symbol).get(0);
		}
		
		/* New symbol, create stub */
		final InstrumentState instState = InstrumentStateFactory.newInstrumentFromStub(inst);
		
		final List<InstrumentState> list = new ArrayList<InstrumentState>();
		list.add(instState);
		symbolMap.put(symbol, list);
		log.debug("Put {} stub into map", symbol);
		
		/* Asnyc lookup */
		try {
			remoteQueue.put(symbol);
		} catch (final Exception e) {
			failedRemoteQueue.add(symbol);
		}
		
		return instState;
		
	}
	
	public static Instrument fromSymbol(String symbol) {
		
		if(symbol == null || symbol.isEmpty()) {
			return Instrument.NULL;
		}
		
		symbol = Symbology.formatSymbol(symbol);
		
		if(symbolMap.containsKey(symbol)) {
			return symbolMap.get(symbol).get(0);
		}
		
		final InstrumentState instState = InstrumentStateFactory.newInstrument(symbol);
		
		final List<InstrumentState> list = new ArrayList<InstrumentState>();
		list.add(instState);
		symbolMap.put(symbol, list);
		
		/* Asnyc lookup */
		try {
			remoteQueue.put(symbol);
		} catch (final Exception e) {
			failedRemoteQueue.add(symbol);
		}
		
		return instState;
		
	} 
	
	public static Map<String, Instrument> fromSymbols(final Collection<String> symbols) {
		
		final Map<String, Instrument> map = new HashMap<String, Instrument>();
		
		for(final String symbol : symbols) {
			map.put(symbol, fromSymbol(symbol));
		}
		
		return map;
		
	}

	private static Observer<InstrumentDefinitionResult> observer = 
			new Observer<InstrumentDefinitionResult>() {

		@Override
		public void onNext(final InstrumentDefinitionResult result) {
			
			final String symbol = result.expression();
			
			/* If exception, add to failed */
			if(result.exception() != null) {
				failedRemoteQueue.add(symbol);
			}
			
			final InstrumentDefinition def = result.result();
			
			if(def == InstrumentDefinition.getDefaultInstance()) {
				log.trace("Instrument result was empty for {}", symbol);
				return;  // Ignore
			}
			
			final InstrumentState iState = symbolMap.get(symbol).get(0);
			
			if(iState == null || iState.isNull()) {
				final List<InstrumentState> list = new ArrayList<InstrumentState>();
				final InstrumentState i = InstrumentFactory.instrumentState(result.result());
				list.add(i);
				symbolMap.put(symbol, list);
				idMap.put(i.id(), i);
			} else {
				iState.process(result.result());
			}
			
		}

		@Override
		public void onError(Throwable error) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCompleted() {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	private static class InstDefResult implements InstrumentDefinitionResult {

		private final String symbol;
		private final InstrumentDefinition def;
		private final Throwable t;
		
		InstDefResult(final String symbol, final InstrumentDefinition def) {
			this.symbol = symbol;
			this.def = def;
			t = null;
		}
		
		InstDefResult(final String symbol, final Throwable t) {
			this.symbol = symbol;
			def = InstrumentDefinition.getDefaultInstance();
			this.t = t;
		}
		
		@Override
		public InstrumentDefinition result() {
			return def;
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
	
	private static final String SERVER_EXTRAS = "extras.ddfplus.com";

	private static final String urlInstrumentLookup(final CharSequence lookup) {
		return "http://" + SERVER_EXTRAS + "/instruments/?lookup=" + lookup;
	}
	
	static Callable<Map<String, InstrumentDefinition>> remoteBatch(final String symbols) {
		
		return new Callable<Map<String, InstrumentDefinition>>() {

			@Override
			public Map<String, InstrumentDefinition> call() throws Exception {
				
				try {
					
					final Map<String, InstrumentDefinition> defs = 
							new HashMap<String, InstrumentDefinition>();
					
					log.debug("remote batch on {}", urlInstrumentLookup(symbols));
					
					final URL url = new URL(urlInstrumentLookup(symbols));
					
					final HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					
					connection.setRequestProperty("Accept-Encoding", "gzip");
					
					connection.connect();
					
					InputStream input = connection.getInputStream();

					if (connection.getContentEncoding() != null && connection.getContentEncoding().equals("gzip")) {
						input = new GZIPInputStream(input);
					}
					
					final BufferedInputStream stream =
							new BufferedInputStream(input);

					final SAXParserFactory factory =
							SAXParserFactory.newInstance();
					final SAXParser parser = factory.newSAXParser();
					
					final DefaultHandler handler = new DefaultHandler() {
						
						InstrumentDefinition def;
						
						@Override
						public void startElement(final String uri,
								final String localName, final String qName,
								final Attributes ats) throws SAXException {
							
							if (qName != null && qName.equals("instrument")) {

								try {
									def = InstrumentXML.decodeSAX(ats);
									if (def != InstrumentDefinition.getDefaultInstance()) {
										defs.put(def.getSymbol(), def);
									}
								} catch (final SymbolNotFoundException se) {
									observer.onNext(new InstDefResult(se.getMessage(), se));
								} catch (final Exception e) {
									log.trace("Exception in parsing batch lookup {}", e);
								}
								
							}
							
						}
						
					};
					
					parser.parse(stream, handler);
					
					return defs;
					
				} catch (final Throwable t) {
					failedRemoteQueue.addAll(Arrays.asList(symbols.split(",")));
					return null;
				}
				
			}
			
		};
		
	}
	
	static class RemoteRunner implements Runnable {
		
		private List<Future<Map<String, InstrumentDefinition>>> futures = 
				new ArrayList<Future<Map<String, InstrumentDefinition>>>();
		
		private final List<Callable<Map<String, InstrumentDefinition>>> callables = 
				new ArrayList<Callable<Map<String, InstrumentDefinition>>>();
		
		@Override
		public void run() {
			
			try {
				
				while(!Thread.interrupted()) {
					
					Thread.sleep(REMOTE_LOOKUP_INTERVAL);
					
					while(!remoteQueue.isEmpty()) {
						callables.add(remoteBatch(buildQuerey()));
					}
					
					futures = executor.invokeAll(callables, DEFAULT_TIMEOUT, MILLIS);
					
					for(final Future<Map<String, InstrumentDefinition>> f : futures) {
						
						for(final Entry<String, InstrumentDefinition> e : f.get().entrySet()) {
							
							final InstrumentDefinition def = e.getValue();
							
							if(def == null || def == InstrumentDefinition.getDefaultInstance()) {
								observer.onNext(new InstDefResult(e.getKey(),
										new Throwable("Could not find " + e.getKey())));
							} else {
								observer.onNext(new InstDefResult(e.getKey(), def));
							}
							
						}
						
					}
					
					futures.clear();
					callables.clear();
					
				}
				
			} catch (final Throwable t) {
				log.error("Exception in Remote Runner Thread", t);
			}
			
		}
		
	}
	
	private static String buildQuerey() throws Exception {
		
		final StringBuilder sb = new StringBuilder();
		
		int len = 0;
		int symCount = 0;
		
		while(len < MAX_URL_LEN && symCount < 400 && !remoteQueue.isEmpty()) {
			
			final String s = remoteQueue.take();
			
			log.debug("Pulled {} from remote queue", s);
			
			symCount++;
			len += s.length() + 1;
			sb.append(s).append(",");
			
		}
		
		/* Remove trailing comma */
		sb.deleteCharAt(sb.length() - 1);
		
		log.debug("Sending {} to remote lookup", sb.toString());
		
		return sb.toString();
		
	}
	
}
