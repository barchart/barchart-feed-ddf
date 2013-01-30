package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.util.HelperXML.XML_STOP;
import static com.barchart.feed.ddf.util.HelperXML.xmlFirstChild;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.enums.InstrumentFieldDDF;
import com.barchart.feed.ddf.util.HelperXML;
import com.barchart.feed.inst.api.Instrument;
import com.barchart.feed.inst.api.InstrumentConst;
import com.barchart.feed.inst.api.InstrumentField;
import com.barchart.feed.inst.api.InstrumentGUID;
import com.barchart.feed.inst.api.SymbologyContext;

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
		
		try {
			return remoteLookup(symbol);
		} catch (final Exception e) {
			log.error(e.getMessage());
			return InstrumentConst.NULL_GUID;
		}
		
	}
	
	@Override
	public Map<CharSequence, InstrumentGUID> lookup(
			Collection<? extends CharSequence> symbols) {
		
		final Map<CharSequence, InstrumentGUID> guidMap = 
				new HashMap<CharSequence, InstrumentGUID>();
		
		for(final CharSequence symbol : symbols) {
			InstrumentGUID guid = symbolMap.get(symbol);
			if(guid != null) {
				guidMap.put(symbol, guid);
			} else {
				try {
					guid = remoteLookup(symbol);
					guidMap.put(symbol, guid);
				} catch (final Exception e) {
					log.error(e.getMessage());
					guidMap.put(symbol, InstrumentConst.NULL_GUID);
				}
			}
			
		}
		
		return guidMap;
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
	
	private InstrumentGUID remoteLookup(final CharSequence symbol) throws Exception {
		
		final String symbolURI = urlInstrumentLookup(symbol);
		
		final Element root = HelperXML.xmlDocumentDecode(symbolURI);
		final Element tag = xmlFirstChild(root, XmlTagExtras.TAG, XML_STOP);
		final DDF_Instrument instDOM = InstrumentXML.decodeXML(tag);
		
		InstrumentGUID guid = new InstrumentGUIDDDF(
				Long.parseLong(instDOM.get(InstrumentField.ID).toString()));
		
		/* Cache symbols */
		symbolMap.put(instDOM.get(InstrumentField.SYMBOL), guid);
		symbolMap.put(instDOM.get(InstrumentFieldDDF.DDF_SYMBOL_REALTIME).toUpperCase(), guid);
		symbolMap.put(instDOM.get(InstrumentFieldDDF.DDF_SYMBOL_HISTORICAL).toUpperCase(), guid);
		symbolMap.put(instDOM.get(InstrumentFieldDDF.DDF_SYMBOL_UNIVERSAL).toUpperCase(), guid);
		
		/* Populate instrument map */
		if(symbolMap != null) {
			guidMap.put(guid, instDOM);
		}
		
		return guid;
	}

	

}