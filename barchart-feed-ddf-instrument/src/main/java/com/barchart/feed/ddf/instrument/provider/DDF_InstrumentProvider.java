/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.instrument.provider.ConstInstrumentDDF.*;
import static com.barchart.feed.ddf.util.HelperXML.*;
import static com.barchart.util.values.provider.ValueBuilder.*;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.barchart.util.anno.ThreadSafe;
import com.barchart.util.values.api.TextValue;

@ThreadSafe
public final class DDF_InstrumentProvider {

	private static final Logger log = LoggerFactory
			.getLogger(DDF_InstrumentProvider.class);

	public static final DDF_Instrument NULL_INSTRUMENT = new InstrumentDDF();

	static final List<DDF_Instrument> NULL_LIST = Collections
			.unmodifiableList(new ArrayList<DDF_Instrument>(0));

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

	/** bind weak reference to resolver instance */
	public static void bind(final DDF_DefinitionService instance) {

		service = new WeakReference<DDF_DefinitionService>(instance);

	}

	/**
	 * cache via instrument service;
	 * 
	 * @return resolved instrument or {@link #NULL_INSTRUMENT}
	 */
	public static DDF_Instrument find(final TextValue symbol) {
		return instance().lookup(symbol);
	}
	
	public static DDF_Instrument findDDF(final TextValue symbol){
		return instance().lookupDDF(symbol);
	}

	/**
	 * NOTE: cache via instrument service;
	 * 
	 * @return resolved instrument or {@link #NULL_INSTRUMENT}
	 */
	public static DDF_Instrument find(final String symbol) {
		return find(newText(symbol));
	}

	/**
	 * NOTE: cache via instrument service;
	 * 
	 * @return list with instruments or empty list;
	 */
	public static List<DDF_Instrument> find(final List<String> symbolList) {
		return instance().lookup(symbolList);
	}

	/** NOTE: does NOT cache NOR use instrument service */
	public static List<DDF_Instrument> fetch(final List<String> symbolList) {

		if (CodecHelper.isEmpty(symbolList)) {
			return NULL_LIST;
		}

		try {
			return remoteLookup(symbolList);
		} catch (Exception e) {
			log.error("", e);
			return NULL_LIST;
		}

	}

	/** modifiable instrument */
	public static DDF_InstrumentDo newInstrumentDDF() {

		return new InstrumentDDF();

	}

	public static void overrideLookupURL(boolean b){
		overrideURL = b;
	}
	
	// TODO: FIXME - allow custom look URL
	//
	
	static DDF_Instrument remoteLookup(CharSequence symbol)
			throws Exception {

		if(overrideURL)
			symbol = symbol + "&bats=1";
		
		final String symbolURI = urlInstrumentLookup(symbol);

		log.debug("SINGLE symbolURI");
		// log.debug("SINGLE symbolURI={}", symbolURI);

		final Element root = xmlDocumentDecode(symbolURI);

		final Element tag = xmlFirstChild(root, XmlTagExtras.TAG, XML_STOP);

		final InstrumentDOM instrument = new InstrumentDOM();

		instrument.decodeXML(tag);

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

		final List<DDF_Instrument> list = new ArrayList<DDF_Instrument>(
				symbolList.size());

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

							final InstrumentSAX instruement = new InstrumentSAX();

							instruement.decodeSAX(attributes);

							list.add(instruement);

						} catch (SymbolNotFoundException e) {

							log.warn("symbol not found : {}", e.getMessage());

						} catch (Exception e) {

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

		} catch (SAXParseException e) {

			log.warn("parse failed : {} ", symbolURI);

		} finally {

			input.close();

		}

		return list;

	}

}
