package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.util.HelperXML.XML_STOP;
import static com.barchart.feed.ddf.util.HelperXML.xmlFirstChild;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.util.InstrumentGUID;
import com.barchart.feed.ddf.util.HelperXML;
import com.barchart.feed.inst.InstrumentField;
import com.barchart.feed.inst.SymbologyContext;
import com.barchart.missive.core.ObjectMapFactory;

final class RemoteSymbologyContextDDF implements SymbologyContext<CharSequence> {

	private static final Logger log = LoggerFactory
			.getLogger(RemoteSymbologyContextDDF.class);
	
	private static Boolean overrideURL = false;
	
	static final String SERVER_EXTRAS = "extras.ddfplus.com";

	static final String urlInstrumentLookup(final CharSequence lookup) {
		return "http://" + SERVER_EXTRAS + "/instruments/?lookup=" + lookup;
	}
	
	final Map<CharSequence, InstrumentGUID> symbolMap = 
			new ConcurrentHashMap<CharSequence, InstrumentGUID>();
	final Map<CharSequence, CharSequence> failedMap = 
			new ConcurrentHashMap<CharSequence, CharSequence>();
	final Map<InstrumentGUID, Instrument> guidMap;
	
	public RemoteSymbologyContextDDF() {
		guidMap = null;
	}
	
	/* 
	 * Because DDF lookup combines symbol -> GUID and GUID -> Instrument, passing in
	 * a map for the InstrumentService to use saves a double lookup. 
	 */
	public RemoteSymbologyContextDDF(final Map<InstrumentGUID, Instrument> guidMap) {
		this.guidMap = guidMap;
		DDF_InstrumentProvider.overrideLookupURL(false);
	}
	
	@Override
	public InstrumentGUID lookup(CharSequence symbol) {
		
		if (overrideURL) {
			symbol = symbol + "&bats=1";
		}
		
		InstrumentGUID guid = symbolMap.get(symbol);
		if(guid != null) {
			return guid;
		}
		
		return remoteLookup(symbol);
		
	}
	
	@Override
	public Map<CharSequence, InstrumentGUID> lookup(
			final Collection<? extends CharSequence> symbols) {
		
		//TODO overrideURL??
		
		final Map<CharSequence, InstrumentGUID> gMap = 
				new HashMap<CharSequence, InstrumentGUID>();
		
		final Iterator<? extends CharSequence> iter = symbols.iterator();
		
		CharSequence symbol;
		while(iter.hasNext()) {
			symbol = iter.next();
			if(symbolMap.containsKey(symbol)) {
				gMap.put(symbol,  symbolMap.get(symbol));
				iter.remove();
			} else if(failedMap.containsKey(symbol)) {
				gMap.put(symbol, InstrumentGUID.NULL);
				iter.remove();
			}
		}
		
		/* Seed lookup values in result map with null */
		for(final CharSequence sym : symbols) {
			gMap.put(sym, InstrumentGUID.NULL);
		}
		
		try {
			remoteLookup(symbols, gMap);
		} catch (final Exception e) {
			log.error("Remote Lookup failed");
		}
		
		/* Cache all failed lookups */
		for(final Entry<CharSequence, InstrumentGUID> e : gMap.entrySet()) {
			
			if(e.getValue().equals(InstrumentGUID.NULL)) {
				failedMap.put(e.getKey(), "");
			}
			
		}
		
		return gMap;
	}

	@Override
	public List<InstrumentGUID> search(final CharSequence symbol) {
		
		final List<InstrumentGUID> list = new ArrayList<InstrumentGUID>();
		
		final InstrumentGUID guid = lookup(symbol);
		if(guid != null) {
			list.add(guid);
		}
		
		return list;
	}

	@Override
	public List<InstrumentGUID> search(final CharSequence symbol, int limit,
			int offset) {
		return search(symbol);
	}
	
	private InstrumentGUID remoteLookup(final CharSequence symbol) {
		
		try {
			
			final String symbolURI = urlInstrumentLookup(symbol);
			final Element root = HelperXML.xmlDocumentDecode(symbolURI);
			final Element tag = xmlFirstChild(root, XmlTagExtras.TAG, XML_STOP);
			final InstrumentDDF instDOM = InstrumentXML.decodeXML(tag);
			
			if(instDOM == null || instDOM.isNull()) {
				failedMap.put(symbol, "");
				return InstrumentGUID.NULL;
			}
			
			InstrumentGUID guid = new InstrumentGUID(
					instDOM.get(InstrumentField.MARKET_GUID));
			
			/* Cache symbols */
			log.debug("Caching {} for symbol {}", instDOM.get(InstrumentField.SYMBOL), symbol);
			symbolMap.put(instDOM.get(InstrumentField.SYMBOL).toString(), guid);
			
			/* Populate instrument map */
			if(symbolMap != null) {
				guidMap.put(guid, (Instrument)instDOM);
			}
			
			return guid;
			
		} catch (final SymbolNotFoundException se) {
			log.debug("HTTP status failed on {}", symbol);
			failedMap.put(symbol, "");
			return InstrumentGUID.NULL;
		} catch (final Exception e) {
			log.error("Symbol remote lookup failed for {}, {}", symbol, e.getMessage());
			failedMap.put(symbol, "");
			return InstrumentGUID.NULL;
		}
		
	}
	
	private void remoteLookup(final Collection<? extends CharSequence> symbols,
			final Map<CharSequence, InstrumentGUID> symMap) 
			throws Exception {
		
		final String symbolString = concatenate(symbols);
		final String symbolURI = urlInstrumentLookup(symbolString);
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

							final InstrumentDDF inst = InstrumentXML.decodeSAX(attributes);
							final InstrumentDDF ddfInst = ObjectMapFactory.build(InstrumentDDF.class, inst);
							symMap.put(inst.symbol(), ddfInst.GUID());
							guidMap.put(inst.GUID(), (Instrument)inst);
							
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
		
	}

	static String concatenate(final Collection<? extends CharSequence> symbolList) {

		final StringBuilder text = new StringBuilder(1024);
		int count = 0;

		for (final CharSequence symbol : symbolList) {
			text.append(symbol);
			count++;
			if (count != symbolList.size()) {
				text.append(",");
			}
		}

		return text.toString();
	}

}
