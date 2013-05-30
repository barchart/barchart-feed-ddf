/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.ddf.instrument.api.DDF_DefinitionService;
import com.barchart.feed.ddf.symbol.enums.DDF_ExpireMonth;
import com.barchart.feed.ddf.util.HelperXML;
import com.barchart.feed.inst.missive.BarchartFeedInstManifest;
import com.barchart.missive.api.Tag;
import com.barchart.missive.core.Manifest;
import com.barchart.missive.core.ObjectMap;
import com.barchart.missive.core.ObjectMapFactory;
import com.barchart.util.anno.ThreadSafe;
import com.barchart.util.values.provider.ValueBuilder;

/**
 * The Class DDF_InstrumentProvider.
 */
@ThreadSafe
public final class DDF_InstrumentProvider {

	private static final Logger log = LoggerFactory
			.getLogger(DDF_InstrumentProvider.class);

	static final List<Instrument> NULL_LIST = Collections
			.unmodifiableList(new ArrayList<Instrument>(0));
	
	static final Map<CharSequence, Instrument> NULL_MAP = Collections
			.unmodifiableMap(new HashMap<CharSequence, Instrument>(0));

	static final String SERVER_EXTRAS = "extras.ddfplus.com";

	static final String urlInstrumentLookup(final CharSequence lookup) {
		return "http://" + SERVER_EXTRAS + "/instruments/?lookup=" + lookup;
	}

	private static final int YEAR;
	private static final char MONTH;
	
	private static final char[] T_Z_O = new char[] {'2', '0', '1'};
	private static final char[] T_Z_T = new char[] {'2', '0', '2'};
	private static final char[] T_Z = new char[] {'2', '0'};
	private static final char[] O = new char[] {'1'};
	
	static {
		final DateTime now = new DateTime();
		YEAR = now.year().get();
		MONTH = DDF_ExpireMonth.fromDateTime(now).code;
		
		ObjectMapFactory.install(new BarchartFeedInstManifest());
		final Manifest<ObjectMap> ddfManifest = new Manifest<ObjectMap>();
		ddfManifest.put(InstrumentDDF.class, new Tag<?>[0]);
		ObjectMapFactory.install(ddfManifest);
	}
	
	private DDF_InstrumentProvider() {
	}

	private static volatile DDF_DefinitionService instance;

	private static Boolean overrideURL = false;

	// TODO
	private static volatile WeakReference<DDF_DefinitionService> service;

	static {
		bind(null);
	}

	private static DDF_DefinitionService instance() {

		DDF_DefinitionService instance = service.get();

		if (instance == null) {

			synchronized (DDF_InstrumentProvider.class) {

				if (instance == null) {

					log.error("resolver is missing; using default : {}",
							ServiceMemoryDDF.class.getName());

					instance = new ServiceMemoryDDF();

				}
			}
			bind(instance);
		}

		return instance;

	}

	/**
	 * bind weak reference to resolver instance.
	 * 
	 * @param instance
	 *            the instance
	 */
	public static void bind(final DDF_DefinitionService instance) {

		//log.debug("Binding new definition service: {}", instance.
		//	getClass().getName());
		
		service = new WeakReference<DDF_DefinitionService>(instance);
		DDF_InstrumentProvider.instance = instance;

	}

	/**
	 * cache via instrument service;.
	 * 
	 * @param symbol
	 *            the symbol
	 * @return resolved instrument or {@link #NULL_INSTRUMENT}
	 */
	public static Instrument find(final CharSequence symbol) {
		return instance().lookup(formatSymbol(
				ValueBuilder.newText(symbol.toString())));
	}

	public static Instrument findHistorical(final CharSequence symbol) {
		return instance().lookup(formatHistoricalSymbol(
				ValueBuilder.newText(symbol.toString())));
	}
	
	/**
	 * cache via instrument service;.
	 * 
	 * @param symbols
	 * @return a map of resolved instruments
	 */
	public static Map<CharSequence, Instrument> find(
			final Collection<? extends CharSequence> symbols) {
		
		final Map<CharSequence, Instrument> insts = new HashMap<CharSequence, Instrument>();
		final Map<CharSequence, Instrument> charInsts = instance().lookup(symbols);
		
		for(final Entry<CharSequence, Instrument> e : charInsts.entrySet()) {
			insts.put(e.getKey().toString(), e.getValue());
		}
		
		return insts;
	}
	
	/**
	 * NOTE: does NOT cache NOR use instrument service.
	 * 
	 * @param symbolList
	 *            the symbol list
	 * @return the list
	 */
	public static List<Instrument> fetch(final List<String> symbolList) {

		if(symbolList == null || symbolList.size() == 0) {
			return NULL_LIST;
		}
		
		try {
			return remoteLookup(symbolList);
		} catch (final Exception e) {
			log.error("", e);
			return NULL_LIST;
		}

	}
	
//	class RetrieveInstrument implements Future<Instrument> {
//
//		private final TextValue symbol;
//
//		private volatile Instrument result = null;
//
//		RetrieveInstrument(final CharSequence symbol) {
//			this.symbol = ValueBuilder.newText(symbol.toString());
//		}
//
//		RetrieveInstrument(final TextValue symbol) {
//			this.symbol = symbol;
//		}
//
//		@Override
//		public boolean cancel(final boolean mayInterruptIfRunning) {
//			throw new UnsupportedOperationException("Not Supported");
//		}
//
//		@Override
//		public Instrument get() throws InterruptedException,
//				ExecutionException {
//			result = instance().lookup(symbol);
//			return result;
//		}
//
//		@Override
//		public Instrument get(final long timeout, final TimeUnit unit)
//				throws InterruptedException, ExecutionException,
//				TimeoutException {
//			throw new UnsupportedOperationException("Not Supported");
//		}
//
//		@Override
//		public boolean isCancelled() {
//			return false;
//		}
//
//		@Override
//		public boolean isDone() {
//			return result != null;
//		}
//
//	}
//
//	class RetrieveInstrumentList implements Future<Map<CharSequence, Instrument>> {
//
//		private final List<CharSequence> symbolList;
//
//		private volatile Map<CharSequence, Instrument> result;
//
//		RetrieveInstrumentList(final List<CharSequence> symbolList) {
//			this.symbolList = symbolList;
//		}
//
//		@Override
//		public boolean cancel(final boolean mayInterruptIfRunning) {
//			throw new UnsupportedOperationException("Not Supported");
//		}
//
//		@Override
//		public Map<CharSequence, Instrument> get() throws InterruptedException,
//				ExecutionException {
//			result = instance().lookup(symbolList);
//			return result;
//		}
//
//		@Override
//		public Map<CharSequence, Instrument> get(final long timeout, final TimeUnit unit)
//						throws InterruptedException, ExecutionException,
//						TimeoutException {
//			throw new UnsupportedOperationException("Not Supported");
//		}
//
//		@Override
//		public boolean isCancelled() {
//			return false;
//		}
//
//		@Override
//		public boolean isDone() {
//			return result != null;
//		}
//
//	}

	

	/**
	 * Override lookup url.
	 * 
	 * @param b
	 *            the b
	 */
	public static void overrideLookupURL(final boolean b) {
		overrideURL = b;
	}

	// TODO: FIXME - allow custom look URL
	//

//	static DDF_Instrument remoteLookup(CharSequence symbol) throws Exception {
//
//		if (overrideURL) {
//			symbol = symbol + "&bats=1";
//		}
//
//		final String symbolURI = urlInstrumentLookup(symbol);
//
//		log.debug("SINGLE symbolURI");
//		// log.debug("SINGLE symbolURI={}", symbolURI);
//
//		final Element root = xmlDocumentDecode(symbolURI);
//
//		final Element tag = xmlFirstChild(root, XmlTagExtras.TAG, XML_STOP);
//
//		final DDF_Instrument instrument = new InstrumentDDF(InstrumentXML.decodeXML(tag));
//
//		return instrument;
//
//	}

	static String concatenate(final List<String> symbolList) {

		final StringBuilder text = new StringBuilder(1024);

		int count = 0;

		for (final String symbol : symbolList) {
			text.append(symbol);
			count++;
			if (count != symbolList.size()) {
				text.append(",");
			}
		}

		return text.toString();

	}

	private static List<Instrument> remoteLookup(final List<String> symbolList)
			throws Exception {

		final List<Instrument> list =
				new ArrayList<Instrument>(symbolList.size());

		final String symbolString = concatenate(symbolList);

		final String symbolURI = urlInstrumentLookup(symbolString);

		log.debug("BATCH symbolURI");
		// log.debug("BATCH symbolURI : {}", symbolURI);

		final URL url = new URL(symbolURI);

		final InputStream input = url.openStream();

		final BufferedInputStream stream = new BufferedInputStream(input);

		try {

			final SAXParserFactory factory = SAXParserFactory.newInstance();

			final SAXParser parser = factory.newSAXParser();

			final DefaultHandler handler = new DefaultHandler() {

				int count;

				@Override
				public void startElement(final String uri,
						final String localName, final String qName,
						final Attributes attributes) throws SAXException {

					if (XmlTagExtras.TAG.equals(qName)) {

						try {

							final Instrument instrument = InstrumentXML.decodeSAX(attributes);
							final Instrument ddfInst = ObjectMapFactory.build(InstrumentDDF.class, instrument);
							list.add(ddfInst);

						} catch (final SymbolNotFoundException e) {

							log.warn("symbol not found : {}", e.getMessage());

						} catch (final Exception e) {

							log.error("decode failure", e);
							HelperXML.log(attributes);

						}

						count++;

						if (count % 1000 == 0) {
							log.debug("fetch count : {}", count);
						}

					}

				}
			};

			parser.parse(stream, handler);

		} catch (final SAXParseException e) {

			log.warn("parse failed : {} ", symbolURI);

		} finally {

			input.close();

		}

		return list;

	}
	
	public static CharSequence formatSymbol(CharSequence symbol) {

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
			
			final StringBuilder sb = new StringBuilder(symbol);
			int last = Character.getNumericValue(symbol.charAt(symbol.length() - 1));
			if(YEAR % 2010 < last) {
				symbol = sb.insert(symbol.length() - 1, T_Z_O);
			} else if(YEAR % 2010 > last) {
				symbol = sb.insert(symbol.length() - 1, T_Z_T);
			} else {
				if(symbol.charAt(symbol.length() - 2) >= MONTH) {
					symbol = sb.insert(symbol.length() - 1, T_Z_O);
				} else {
					symbol = sb.insert(symbol.length() - 1, T_Z_T);
				}
			}
			
			return symbol;
		}
		
		/* e.g. ESH13 */
		if(!Character.isDigit(symbol.charAt(symbol.length() - 3))) {
			
			return new StringBuilder(symbol).insert(symbol.length()-2, T_Z);
			
		}
		
		return symbol;
	}
	
	public static CharSequence formatHistoricalSymbol(CharSequence symbol) {
		
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
			return new StringBuilder(symbol).insert(symbol.length() - 1, O);
		}
		
		return symbol;
	}

}
