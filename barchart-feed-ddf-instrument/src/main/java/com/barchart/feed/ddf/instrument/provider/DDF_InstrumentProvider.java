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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import rx.Observer;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.base.provider.Symbology;
import com.barchart.feed.ddf.instrument.provider.InstrumentState.LoadState;

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

	private static final ArrayBlockingQueue<String> remoteSymbolQueue =
			new ArrayBlockingQueue<String>(1000 * 1000);
	
	private static final ArrayBlockingQueue<InstrumentID> remoteIDQueue =
			new ArrayBlockingQueue<InstrumentID>(1000 * 1000);

	private static final List<String> failedRemoteSymbolQueue =
			new CopyOnWriteArrayList<String>();
	
	private static final List<InstrumentID> failedRemoteIDQueue =
			new CopyOnWriteArrayList<InstrumentID>();

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
		final InstrumentState instState = new DDF_Instrument(new InstrumentID(inst.symbol()), inst, LoadState.PARTIAL);

		final List<InstrumentState> list = new ArrayList<InstrumentState>();
		list.add(instState);
		symbolMap.put(symbol, list);
		log.debug("Put {} stub into map", symbol);

		/* Asnyc lookup */
		try {
			remoteSymbolQueue.put(symbol);
		} catch (final Exception e) {
			failedRemoteSymbolQueue.add(symbol);
		}

		return instState;

	}

	public static Instrument fromID(final InstrumentID id) {
		
		if(id == null || id.isNull()) {
			return Instrument.NULL;
		}
		
		if (idMap.containsKey(id)) {
			return idMap.get(id);
		} 
		
		final InstrumentState instState = new DDF_Instrument(id);
		idMap.put(id, instState);
		
		/* Asnyc lookup */
		try {
			remoteIDQueue.put(id);
		} catch (final Exception e) {
			failedRemoteIDQueue.add(id);
		}
		
		return instState;
		
	}
	
	public static Instrument fromSymbol(String symbol) {

		if (symbol == null || symbol.isEmpty()) {
			return Instrument.NULL;
		}

		symbol = Symbology.formatSymbol(symbol);

		if (symbolMap.containsKey(symbol)) {
			return symbolMap.get(symbol).get(0);
		}

		final InstrumentState instState = new DDF_Instrument(symbol);

		final List<InstrumentState> list = new ArrayList<InstrumentState>();
		list.add(instState);
		symbolMap.put(symbol, list);

		/* Asnyc lookup */
		try {
			remoteSymbolQueue.put(symbol);
		} catch (final Exception e) {
			failedRemoteSymbolQueue.add(symbol);
		}

		return instState;

	}

	public static Map<String, Instrument> fromSymbols(final Collection<String> symbols) {

		final Map<String, Instrument> map = new HashMap<String, Instrument>();

		for (final String symbol : symbols) {
			map.put(symbol, fromSymbol(symbol));
		}

		return map;

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
						failedRemoteSymbolQueue.add(symbol);
					}

					final Instrument inst = result.result();

					if (inst.isNull()) {
						log.trace("Instrument result was empty for {}", symbol);
						return; // Ignore
					}

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
					// TODO Auto-generated method stub

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

	private static final String SERVER_EXTRAS = "extras.ddfplus.com";

	private static final String urlSymbolLookup(final CharSequence lookup) {
		return "http://" + SERVER_EXTRAS + "/instruments/?lookup=" + lookup;
	}
	
	private static final String urlIDLookup(final CharSequence lookup) {
		return "http://" + SERVER_EXTRAS + "/instruments/?id=" + lookup;
	}

	static Callable<Map<String, InstrumentState>> remoteSymbolBatch(final String symbols) {

		return new Callable<Map<String, InstrumentState>>() {

			@Override
			public Map<String, InstrumentState> call() throws Exception {

				try {

					final Map<String, InstrumentState> defs = new HashMap<String, InstrumentState>();

					log.debug("remote batch on {}", urlSymbolLookup(symbols));

					final URL url = new URL(urlSymbolLookup(symbols));

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

						@Override
						public void startElement(final String uri,
								final String localName, final String qName,
								final Attributes ats) throws SAXException {

							if (qName != null && qName.equals("instrument")) {

								try {
									final InstrumentState inst = new DDF_Instrument(ats);
									defs.put(inst.symbol(), inst);
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
					failedRemoteSymbolQueue.addAll(Arrays.asList(symbols.split(",")));
					return null;
				}

			}

		};

	}

	static Callable<Map<InstrumentID, InstrumentState>> remoteIDBatch(final String ids) {

		return new Callable<Map<InstrumentID, InstrumentState>>() {

			@Override
			public Map<InstrumentID, InstrumentState> call() throws Exception {

				try {

					final Map<InstrumentID, InstrumentState> defs = new HashMap<InstrumentID, InstrumentState>();

					log.debug("remote id batch on {}", urlIDLookup(ids));

					final URL url = new URL(urlIDLookup(ids));

					final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

					connection.setRequestProperty("Accept-Encoding", "gzip");
					connection.connect();

					InputStream input = connection.getInputStream();

					if (connection.getContentEncoding() != null && 
							connection.getContentEncoding().equals("gzip")) {
						
						input = new GZIPInputStream(input);
					}

					final BufferedInputStream stream = new BufferedInputStream(input);
					final SAXParserFactory factory = SAXParserFactory.newInstance();
					final SAXParser parser = factory.newSAXParser();

					final DefaultHandler handler = new DefaultHandler() {

						@Override
						public void startElement(final String uri,
								final String localName, final String qName,
								final Attributes ats) throws SAXException {

							if (qName != null && qName.equals("instrument")) {

								try {
									final InstrumentState inst = new DDF_Instrument(ats);
									defs.put(inst.id(), inst);
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
					// failedRemoteSymbolQueue.addAll(Arrays.asList(ids.split(",")));
					return null;
				}

			}

		};

	}
	
	static class RemoteRunner implements Runnable {

		private List<Future<Map<String, InstrumentState>>> symbFutures =
				new ArrayList<Future<Map<String, InstrumentState>>>();

		private final List<Callable<Map<String, InstrumentState>>> symbCallables =
				new ArrayList<Callable<Map<String, InstrumentState>>>();
		
		private List<Future<Map<InstrumentID, InstrumentState>>> idFutures =
				new ArrayList<Future<Map<InstrumentID, InstrumentState>>>();

		private final List<Callable<Map<InstrumentID, InstrumentState>>> idCallables =
				new ArrayList<Callable<Map<InstrumentID, InstrumentState>>>();

		@Override
		public void run() {

			try {

				while (!Thread.interrupted()) {

					Thread.sleep(REMOTE_LOOKUP_INTERVAL);

					while (!remoteSymbolQueue.isEmpty()) {
						symbCallables.add(remoteSymbolBatch(buildSymbolQuerey()));
					}

					symbFutures = executor.invokeAll(symbCallables, DEFAULT_TIMEOUT, MILLIS);

					for (final Future<Map<String, InstrumentState>> f : symbFutures) {

						for (final Entry<String, InstrumentState> e : f.get().entrySet()) {

							final InstrumentState def = e.getValue();

							if (def == null || def.isNull()) {
								observer.onNext(new InstDefResult(e.getKey(), new Throwable("Could not find "
										+ e.getKey())));
							} else {
								observer.onNext(new InstDefResult(e.getKey(), def));
							}

						}

					}

					symbFutures.clear();
					symbCallables.clear();
					
					while(!remoteIDQueue.isEmpty()) {
						idCallables.add(remoteIDBatch(buildIDQuerey()));
					}
					
					idFutures = executor.invokeAll(idCallables, DEFAULT_TIMEOUT, MILLIS);
					
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

	private static String buildSymbolQuerey() throws Exception {

		final StringBuilder sb = new StringBuilder();

		int len = 0;
		int symCount = 0;

		while (len < MAX_URL_LEN && symCount < 400 && !remoteSymbolQueue.isEmpty()) {

			final String s = remoteSymbolQueue.take();

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
	
	private static String buildIDQuerey() throws Exception {

		final StringBuilder sb = new StringBuilder();

		int len = 0;
		int symCount = 0;

		while (len < MAX_URL_LEN && symCount < 400 && !remoteIDQueue.isEmpty()) {

			final String s = remoteIDQueue.take().toString();

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
