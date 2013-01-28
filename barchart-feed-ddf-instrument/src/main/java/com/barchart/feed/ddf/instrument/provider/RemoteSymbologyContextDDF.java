package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.util.HelperXML.XML_STOP;
import static com.barchart.feed.ddf.util.HelperXML.xmlFirstChild;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;


import com.barchart.feed.ddf.util.HelperXML;
import com.barchart.feed.inst.api.Instrument;
import com.barchart.feed.inst.api.InstrumentField;
import com.barchart.feed.inst.api.InstrumentGUID;
import com.barchart.feed.inst.api.SymbologyContext;

public class RemoteSymbologyContextDDF implements SymbologyContext {

	private static final Logger log = LoggerFactory
			.getLogger(RemoteSymbologyContextDDF.class);
	
	private static Boolean overrideURL = false;
	
	static final String SERVER_EXTRAS = "extras.ddfplus.com";

	static final String urlInstrumentLookup(final CharSequence lookup) {
		return "http://" + SERVER_EXTRAS + "/instruments/?lookup=" + lookup;
	}
	
	final Map<InstrumentGUID, Instrument> instMap;
	
	public RemoteSymbologyContextDDF() {
		instMap = null;
	}
	
	public RemoteSymbologyContextDDF(final Map<InstrumentGUID, Instrument> instMap) {
		this.instMap = instMap;
	}
	
	@Override
	public InstrumentGUID lookup(CharSequence symbol) {
		
		if (overrideURL) {
			symbol = symbol + "&bats=1";
		}
		
		try {
			return remoteLookup(symbol);
		} catch (final Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<InstrumentGUID> search(final CharSequence symbol) {
		
		return null;
	}

	@Override
	public List<InstrumentGUID> search(final CharSequence symbol, int limit,
			int offset) {
		
		return null;
	}
	
	private InstrumentGUID remoteLookup(final CharSequence symbol) throws Exception {
		
		final String symbolURI = urlInstrumentLookup(symbol);
		
		final Element root = HelperXML.xmlDocumentDecode(symbolURI);
		final Element tag = xmlFirstChild(root, XmlTagExtras.TAG, XML_STOP);
		final Instrument instDOM = InstrumentXML.decodeXML(tag);
		
		InstrumentGUID guid = new InstrumentGUIDDDF(
				Long.parseLong(instDOM.get(InstrumentField.ID).toString()));
		
		if(instMap != null) {
			// add inst to map
		}
		
		return guid;
	}

}
