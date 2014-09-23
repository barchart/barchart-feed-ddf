package com.barchart.feed.ddf.instrument.provider;

import static com.barchart.feed.ddf.instrument.provider.XmlTagExtras.LOOKUP;
import static com.barchart.feed.ddf.util.HelperXML.XML_STOP;
import static com.barchart.feed.ddf.util.HelperXML.xmlStringDecode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.VendorID;
import com.barchart.util.value.ValueFactoryImpl;
import com.barchart.util.value.api.Size;
import com.barchart.util.value.api.ValueFactory;

public class TestInstrumentXML {

	private static final ValueFactory vals = ValueFactoryImpl.instance;

	private static final String IBM =
			"<instruments status=\"200\" count=\"1\">	<instrument lookup=\"IBM\" status=\"200\" guid=\"IBM\" id=\"1298146\" symbol_realtime=\"IBM\" symbol_ddf=\"IBM\" symbol_historical=\"IBM\" "
					+ "symbol_description=\"International Business Machines Corp.\" symbol_cfi=\"EXXXXX\" exchange=\"XNYS\" exchange_channel=\"NYSE\" exchange_description=\"New York Stock Exchange\" exchange_ddf=\"N\" time_zone_ddf=\"America/New_York\" "
					+ "tick_increment=\"1\" unit_code=\"2\" base_code=\"A\" point_value=\"1\"/> </instruments>";
	
	private static final String ESZ4 = 
		"<instruments status=\"200\" count=\"1\"> "
			+ "<instrument lookup=\"ESZ14\" status=\"200\" guid=\"ESZ2014\" id=\"165730509\" symbol_realtime=\"ESZ2014\" symbol_ddf=\"ESZ4\" symbol_historical=\"ESZ14\" symbol_expire=\"2014-12-19T23:59:59-06:00\" symbol_ddf_expire_month=\"Z\" symbol_ddf_expire_year=\"4\" symbol_cfi=\"FXXXXX\" exchange=\"XCME\" exchange_channel=\"GBLX\" exchange_description=\"CMEGroup CME (Globex Mini)\" exchange_ddf=\"M\" time_zone_ddf=\"America/Chicago\" tick_increment=\"25\" base_code=\"A\" unit_code=\"2\" point_value=\"50\" currency=\"\">"
				+ "<ticker provider=\"BARCHART\" id=\"165730509\" symbol=\"ESZ2014\"/>"
				+ "<ticker provider=\"CQG\" id=\"\" symbol=\"F.US.EPZ14\"/>"
				+ "<ticker provider=\"OEC\" id=\"\" symbol=\"ESZ4\"/>"
			+ "</instrument>"
			+ "<instrument lookup=\"ESM15\" status=\"200\" guid=\"ESM2015\" id=\"165730512\" symbol_realtime=\"ESM2015\" symbol_ddf=\"ESM5\" symbol_historical=\"ESM15\" symbol_expire=\"2014-12-19T23:59:59-06:00\" symbol_ddf_expire_month=\"Z\" symbol_ddf_expire_year=\"4\" symbol_cfi=\"FXXXXX\" exchange=\"XCME\" exchange_channel=\"GBLX\" exchange_description=\"CMEGroup CME (Globex Mini)\" exchange_ddf=\"M\" time_zone_ddf=\"America/Chicago\" tick_increment=\"25\" base_code=\"A\" unit_code=\"2\" point_value=\"50\" currency=\"\">"
				+ "<ticker provider=\"BARCHART\" id=\"165730512\" symbol=\"ESM2015\"/>"
				+ "<ticker provider=\"CQG\" id=\"\" symbol=\"F.US.EPM15\"/>"
				+ "<ticker provider=\"OEC\" id=\"\" symbol=\"ESM5\"/>"
			+ "</instrument>"
		+ "</instruments>";

	@Test
	public void testXML() throws Exception {

		final SAXParserFactory factory = SAXParserFactory.newInstance();
		final SAXParser parser = factory.newSAXParser();
		final List<InstrumentState> result = new ArrayList<InstrumentState>();
		final DefaultHandler handler = handler(result);

		parser.parse(new ByteArrayInputStream(IBM.getBytes()), handler);

		final Instrument IBMInst = result.get(0);

		assertTrue(IBMInst.marketGUID().equals("IBM"));
		assertTrue(IBMInst.securityType() == Instrument.SecurityType.EQUITY);
		assertTrue(IBMInst.liquidityType() == Instrument.BookLiquidityType.NONE);
		assertTrue(IBMInst.bookStructure() == Instrument.BookStructureType.NONE);
		assertTrue(IBMInst.maxBookDepth() == Size.NULL);
		assertTrue(IBMInst.vendor().id().equals("BARCHART"));
		assertTrue(IBMInst.symbol().equals("IBM"));
		assertTrue(IBMInst.description().equals("International Business Machines Corp."));
		assertTrue(IBMInst.CFICode().equals("EXXXXX"));
		assertTrue(IBMInst.exchangeCode().equals("N"));
		assertTrue(IBMInst.tickSize().equals(vals.newPrice(1, -2)));
		assertTrue(IBMInst.pointValue().equals(vals.newPrice(1, 0)));
		assertTrue(IBMInst.displayFraction().equals(vals.newFraction(10, -2)));
		assertTrue(IBMInst.timeZoneName().equals("America/New_York"));

	}
	
	@Test
	public void testExpandedXML() throws Exception {
		
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		final SAXParser parser = factory.newSAXParser();
		final Map<String, List<InstrumentState>> result = new HashMap<String, List<InstrumentState>>();
		final DefaultHandler handler = DDF_RxInstrumentProvider.symbolHandler(result);
		
		parser.parse(new ByteArrayInputStream(ESZ4.getBytes()), handler);
		
		Map<VendorID, String> vendors = result.get("ESZ14").get(0).vendorSymbols();
		assertEquals("ESZ2014", vendors.get(VendorID.BARCHART));
		assertEquals("F.US.EPZ14", vendors.get(new VendorID("CQG")));
		assertEquals("ESZ4", vendors.get(new VendorID("OEC")));
		
		vendors = result.get("ESM15").get(0).vendorSymbols();
		assertEquals("ESM2015", vendors.get(VendorID.BARCHART));
		assertEquals("F.US.EPM15", vendors.get(new VendorID("CQG")));
		assertEquals("ESM5", vendors.get(new VendorID("OEC")));
		
	}

	protected static DefaultHandler handler(final List<InstrumentState> result) {
		return new DefaultHandler() {

			@Override
			public void startElement(
					final String uri,
					final String localName, 
					final String qName,
					final Attributes ats) throws SAXException {

				if (qName != null && qName.equals("instrument")) {

					try {
						result.add(new DDF_Instrument(ats));
					} catch (final SymbolNotFoundException se) {
						throw new RuntimeException(se); // would be nice to add to map
					} catch (final Exception e) {
						throw new RuntimeException(e);
					}

				}

			}

		};
	}
	
}