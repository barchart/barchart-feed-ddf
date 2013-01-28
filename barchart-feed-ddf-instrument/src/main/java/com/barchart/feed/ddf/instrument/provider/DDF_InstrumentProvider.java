/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.util.HelperXML.XML_STOP;
import static com.barchart.feed.ddf.util.HelperXML.xmlDocumentDecode;
import static com.barchart.feed.ddf.util.HelperXML.xmlFirstChild;
import static com.barchart.util.values.provider.ValueBuilder.newText;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.barchart.feed.ddf.instrument.api.DDF_DefinitionService;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.util.HelperXML;
import com.barchart.feed.inst.api.Instrument;
import com.barchart.util.anno.ThreadSafe;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.provider.ValueBuilder;

/**
 * The Class DDF_InstrumentProvider.
 */
@ThreadSafe
public final class DDF_InstrumentProvider {

	private static final Logger log = LoggerFactory
			.getLogger(DDF_InstrumentProvider.class);

	/** The Constant NULL_INSTRUMENT. */
	public static final DDF_Instrument NULL_INSTRUMENT = new InstrumentDDF();

	static final List<DDF_Instrument> NULL_LIST = Collections
			.unmodifiableList(new ArrayList<DDF_Instrument>(0));

	static final String SERVER_EXTRAS = "extras.ddfplus.com";

	static final String urlInstrumentLookup(final CharSequence lookup) {
		return "http://" + SERVER_EXTRAS + "/instruments/?lookup=" + lookup;
	}

	private DDF_InstrumentProvider() {
	}

	private static volatile DDF_DefinitionService instance;

	private static Boolean overrideURL = false;

	// TODO
	private static volatile WeakReference<DDF_DefinitionService> service;

	static {
		// bind(null);
	}

	private static DDF_DefinitionService instance() {

		// DDF_DefinitionService instance = service.get();

		if (instance == null) {

			synchronized (DDF_InstrumentProvider.class) {

				if (instance == null) {

					log.error("resolver is missing; using default : {}",
							ServiceMemoryDDF.class.getName());

					instance = new ServiceMemoryDDF();

				}

			}

			// bind(instance);

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
	public static DDF_Instrument find(final TextValue symbol) {
		return new InstrumentDDF(instance().lookup(symbol));
	}

	/**
	 * Find ddf.
	 * 
	 * @param symbol
	 *            the symbol
	 * @return the dD f_ instrument
	 */
	public static DDF_Instrument findDDF(final TextValue symbol) {
		return instance().lookupDDF(symbol);
	}

	/**
	 * NOTE: cache via instrument service;.
	 * 
	 * @param symbol
	 *            the symbol
	 * @return resolved instrument or {@link #NULL_INSTRUMENT}
	 */
	public static DDF_Instrument find(final String symbol) {
		return find(newText(symbol));
	}

	class RetrieveInstrument implements Future<DDF_Instrument> {

		private final TextValue symbol;

		private volatile DDF_Instrument result = null;

		RetrieveInstrument(final String symbol) {
			this.symbol = ValueBuilder.newText(symbol);
		}

		RetrieveInstrument(final TextValue symbol) {
			this.symbol = symbol;
		}

		@Override
		public boolean cancel(final boolean mayInterruptIfRunning) {
			throw new UnsupportedOperationException("Not Supported");
		}

		@Override
		public DDF_Instrument get() throws InterruptedException,
				ExecutionException {
			result = instance().lookupDDF(symbol);
			return result;
		}

		@Override
		public DDF_Instrument get(final long timeout, final TimeUnit unit)
				throws InterruptedException, ExecutionException,
				TimeoutException {
			throw new UnsupportedOperationException("Not Supported");
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return result != null;
		}

	}

	/**
	 * NOTE: cache via instrument service;.
	 * 
	 * @param symbolList
	 *            the symbol list
	 * @return list with instruments or empty list;
	 */
	public static List<DDF_Instrument> find(final List<String> symbolList) {
		return instance().lookupDDF(symbolList);
	}

	class RetrieveInstrumentList implements Future<List<DDF_Instrument>> {

		private final List<String> symbolList;

		private volatile List<DDF_Instrument> result;

		RetrieveInstrumentList(final List<String> symbolList) {
			this.symbolList = symbolList;
		}

		@Override
		public boolean cancel(final boolean mayInterruptIfRunning) {
			throw new UnsupportedOperationException("Not Supported");
		}

		@Override
		public List<DDF_Instrument> get() throws InterruptedException,
				ExecutionException {
			result = instance().lookupDDF(symbolList);
			return result;
		}

		@Override
		public List<DDF_Instrument>
				get(final long timeout, final TimeUnit unit)
						throws InterruptedException, ExecutionException,
						TimeoutException {
			throw new UnsupportedOperationException("Not Supported");
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return result != null;
		}

	}

	/**
	 * NOTE: does NOT cache NOR use instrument service.
	 * 
	 * @param symbolList
	 *            the symbol list
	 * @return the list
	 */
	public static List<DDF_Instrument> fetch(final List<String> symbolList) {

		if (CodecHelper.isEmpty(symbolList)) {
			return NULL_LIST;
		}

		try {
			return remoteLookup(symbolList);
		} catch (final Exception e) {
			log.error("", e);
			return NULL_LIST;
		}

	}

	/**
	 * modifiable instrument.
	 * 
	 * @return the dD f_ instrument do
	 */
//	public static DDF_InstrumentDo newInstrumentDDF() {
//
//		return new InstrumentDDF();
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

	static DDF_Instrument remoteLookup(CharSequence symbol) throws Exception {

		if (overrideURL) {
			symbol = symbol + "&bats=1";
		}

		final String symbolURI = urlInstrumentLookup(symbol);

		log.debug("SINGLE symbolURI");
		// log.debug("SINGLE symbolURI={}", symbolURI);

		final Element root = xmlDocumentDecode(symbolURI);

		final Element tag = xmlFirstChild(root, XmlTagExtras.TAG, XML_STOP);

		final DDF_Instrument instrument = new InstrumentDDF(InstrumentXML.decodeXML(tag));

		return instrument;

	}

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

	static List<DDF_Instrument> remoteLookup(final List<String> symbolList)
			throws Exception {

		final List<DDF_Instrument> list =
				new ArrayList<DDF_Instrument>(symbolList.size());

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
							final DDF_Instrument ddfInst = new InstrumentDDF(instrument);
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

}
