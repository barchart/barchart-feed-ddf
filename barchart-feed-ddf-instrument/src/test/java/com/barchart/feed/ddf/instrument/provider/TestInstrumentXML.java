package com.barchart.feed.ddf.instrument.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
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
	
	private static final String OPTIONS = 
			"<instruments status=\"200\" count=\"2\">"
				+ "<instrument lookup=\"ESV2014|1300P\" status=\"200\" guid=\"ESV2014|1300P\" id=\"131144461\" symbol_realtime=\"ESV2014|1300P\" symbol_ddf=\"ESV1300P\" symbol_historical=\"ESV1300P\" symbol_expire=\"2014-10-17T00:00:00-05:00\" symbol_ddf_expire_month=\"V\" symbol_ddf_expire_year=\"4\" symbol_cfi=\"OPXFXX\" exchange=\"XCME\" exchange_channel=\"GBLX\" exchange_description=\"CMEGroup CME (Globex Mini)\" exchange_ddf=\"M\" time_zone_ddf=\"America/Chicago\" tick_increment=\"1\" base_code=\"A\" unit_code=\"2\" point_value=\"50\" currency=\"USD\" underlier=\"ESZ14\" underlier_id=\"165730509\">"
					+ "<ticker provider=\"BARCHART\" id=\"131144461\" symbol=\"ESV2014|1300P\"/>"
					+ "<ticker provider=\"CQG\" id=\"\" symbol=\"P.US.EPV1413000\"/>"
				+ "</instrument>"
				+ "<instrument lookup=\"ESV2014|2045C\" status=\"200\" guid=\"ESV2014|2045C\" id=\"274515764\" symbol_realtime=\"ESV2014|2045C\" symbol_ddf=\"ESV2045C\" symbol_historical=\"ESV2045C\" symbol_expire=\"2014-10-17T00:00:00-05:00\" symbol_ddf_expire_month=\"V\" symbol_ddf_expire_year=\"4\" symbol_cfi=\"OCXFXX\" exchange=\"XCME\" exchange_channel=\"GBLX\" exchange_description=\"CMEGroup CME (Globex Mini)\" exchange_ddf=\"M\" time_zone_ddf=\"America/Chicago\" tick_increment=\"1\" base_code=\"A\" unit_code=\"2\" point_value=\"50\" currency=\"USD\" underlier=\"ESZ14\" underlier_id=\"165730509\">"
					+ "<ticker provider=\"BARCHART\" id=\"274515764\" symbol=\"ESV2014|2045C\"/>"
					+ "<ticker provider=\"CQG\" id=\"\" symbol=\"C.US.EPV1420450\"/>"
				+ "</instrument>"
			+ "</instruments>";

	@SuppressWarnings("deprecation")
	@Test
	public void testXML() throws Exception {

		final SAXParserFactory factory = SAXParserFactory.newInstance();
		final SAXParser parser = factory.newSAXParser();
		final Map<String, List<InstrumentState>> result = new HashMap<String, List<InstrumentState>>();
		final DefaultHandler handler = DDF_RxInstrumentProvider.symbolHandler(result);

		parser.parse(new ByteArrayInputStream(IBM.getBytes()), handler);

		final Instrument IBMInst = result.get("IBM").get(0);

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

	@Test
	public void testOptions() throws Exception {
		
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		final SAXParser parser = factory.newSAXParser();
		final Map<String, List<InstrumentState>> result = new HashMap<String, List<InstrumentState>>();
		final DefaultHandler handler = DDF_RxInstrumentProvider.symbolHandler(result);
		
		parser.parse(new ByteArrayInputStream(OPTIONS.getBytes()), handler);

		Map<VendorID, String> vendors = result.get("ESV2014|1300P").get(0).vendorSymbols();
		
//		for(final Entry<VendorID, String> e : vendors.entrySet()) {
//			System.out.println(e.getKey() + " " + e.getValue());
//		}
		
		assertEquals("ESV2014|1300P", vendors.get(VendorID.BARCHART));
		assertEquals("ESV1300P", vendors.get(VendorID.BARCHART_SHORT));
		assertEquals("P.US.EPV1413000", vendors.get(VendorID.CQG));
		assertEquals("ESV1300P", vendors.get(VendorID.BARCHART_HISTORICAL));
		
		
		vendors = result.get("ESV2014|2045C").get(0).vendorSymbols();
		
//		for(final Entry<VendorID, String> e : vendors.entrySet()) {
//			System.out.println(e.getKey() + " " + e.getValue());
//		}
		
		assertEquals("ESV2014|2045C", vendors.get(VendorID.BARCHART));
		assertEquals("ESV2045C", vendors.get(VendorID.BARCHART_SHORT));
		assertEquals("C.US.EPV1420450", vendors.get(VendorID.CQG));
		assertEquals("ESV2045C", vendors.get(VendorID.BARCHART_HISTORICAL));
		
		
	}
	
}