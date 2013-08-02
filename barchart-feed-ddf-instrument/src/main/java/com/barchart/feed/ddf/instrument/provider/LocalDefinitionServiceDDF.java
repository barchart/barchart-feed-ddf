package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_SYMBOL_HISTORICAL;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_SYMBOL_REALTIME;
import static com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField.DDF_SYMBOL_UNIVERSAL;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.barchart.feed.ddf.instrument.api.DDF_DefinitionService;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.provider.ValueBuilder;

public class LocalDefinitionServiceDDF implements DDF_DefinitionService {
	
	private static final Logger log = LoggerFactory.getLogger(LocalDefinitionServiceDDF.class);
	
	private final ConcurrentMap<TextValue, DDF_Instrument> instrumentMap = 
			new ConcurrentHashMap<TextValue, DDF_Instrument>();

	private final ConcurrentMap<TextValue, DDF_Instrument> ddfInstrumentMap = 
			new ConcurrentHashMap<TextValue, DDF_Instrument>();
			
	private final ConcurrentMap<TextValue, Void> failedLookups = 
			new ConcurrentHashMap<TextValue, Void>();
			
	public LocalDefinitionServiceDDF(final File defFile) throws IOException {
		this(new FileInputStream(defFile));
	}
			
	public LocalDefinitionServiceDDF(final InputStream input) throws IOException {
		
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

							final InstrumentSAX instrument =
									new InstrumentSAX();

							instrument.decodeSAX(attributes);

							store(instrument);

						} catch (final SymbolNotFoundException e) {

//							log.warn("symbol not found : {}", e.getMessage());

						} catch (final Exception e) {

//							log.error("decode failure", e);
//							HelperXML.log(attributes);

						}

						count++;

						if (count % 10000 == 0) {
							log.debug("decode count : {}", count);
						}

					}

				}
			};
			
			parser.parse(stream, handler);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} finally {
			input.close();
		}
		
		log.debug("Loaded instrument lookup maps");
		log.debug("InstMap size = {}", instrumentMap.size());
		
	}
	
	private void store(final DDF_Instrument instrument) {
		
		final TextValue symbolDDF =
				instrument.get(DDF_SYMBOL_REALTIME).toUpperCase();
		
		ddfInstrumentMap.put(symbolDDF, instrument);
		
		final TextValue symbolHIST =
				instrument.get(DDF_SYMBOL_HISTORICAL).toUpperCase();
		final TextValue symbolGUID =
				instrument.get(DDF_SYMBOL_UNIVERSAL).toUpperCase();

		// hack for bats
		if (symbolDDF.toString().contains(".BZ")) {
			final TextValue lookup =
					ValueBuilder.newText(symbolDDF.toString()
							.replace(".BZ", ""));

			instrumentMap.put(lookup, instrument);
		}

		instrumentMap.put(symbolHIST, instrument);
		instrumentMap.put(symbolGUID, instrument);

		//log.debug("defined instrument={}", symbolGUID);
		
	}
	
	@Override
	public void clear() {
		
	}

	@Override
	public DDF_Instrument lookup(final TextValue symbol) {
		
		if (CodecHelper.isEmpty(symbol)) {
			return DDF_InstrumentProvider.NULL_INSTRUMENT;
		}
		
		final DDF_Instrument instrument = instrumentMap.get(symbol.toUpperCase());
		
		if(instrument == null) {
//			log.warn("Unknown symbol {}", symbol);
			return DDF_InstrumentProvider.NULL_INSTRUMENT;
		}
		
		return instrument;
	}

	

	@Override
	public List<DDF_Instrument> lookup(final List<String> symbolList) {
		
		if (CodecHelper.isEmpty(symbolList)) {
			return DDF_InstrumentProvider.NULL_LIST;
		}
		
		//TODO
		
		return null;
	}

	@Override
	public DDF_Instrument lookupDDF(final TextValue symbol) {
		
		if (CodecHelper.isEmpty(symbol)) {
			return DDF_InstrumentProvider.NULL_INSTRUMENT;
		}
		
		final DDF_Instrument instrument = ddfInstrumentMap.get(symbol.toUpperCase());
		
		if(instrument == null) {
//			log.warn("Unknown symbol {}", symbol);
			return DDF_InstrumentProvider.NULL_INSTRUMENT;
		}
		
		return instrument;
	}

	@Override
	public Map<String, DDF_Instrument> lookupMap(List<String> symbolList) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
