package com.barchart.feed.test.instrument;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.api.model.meta.id.VendorID;
import com.barchart.feed.ddf.instrument.provider.DDF_RxInstrumentProvider;

public class TestDDF_RxInstrumentProvider {

	@Test
	public void testSymbolLookup() {

		final String s1 = "ESZ14";
		final String s2 = "NQZ14";
		
		
		final Map<String, List<Instrument>> results = DDF_RxInstrumentProvider.fromString(s1, s2)
				.toBlockingObservable().first().results();
		
		assertTrue(results.containsKey(s1));
		assertTrue(results.containsKey(s2));
		
		final Instrument i1 = results.get(s1).get(0);
		
		assertTrue("ESZ2014".equals(i1.vendorSymbols().get(VendorID.BARCHART)));
		assertTrue("ESZ4".equals(i1.vendorSymbols().get(VendorID.BARCHART_SHORT)));
		assertTrue("F.US.EPZ14".equals(i1.vendorSymbols().get(VendorID.CQG)));
		assertTrue("ESZ4".equals(i1.vendorSymbols().get(VendorID.OEC)));
	
		final Instrument i2 = results.get(s2).get(0);
		
		assertTrue("NQZ2014".equals(i2.vendorSymbols().get(VendorID.BARCHART)));
		assertTrue("NQZ4".equals(i2.vendorSymbols().get(VendorID.BARCHART_SHORT)));
		assertTrue("F.US.ENQZ14".equals(i2.vendorSymbols().get(VendorID.CQG)));
		assertTrue("NQZ4".equals(i2.vendorSymbols().get(VendorID.OEC)));
		
		
	}
	
	@Test
	public void testIDLookup() {
		
		final InstrumentID id1 = new InstrumentID("165730509");
		final InstrumentID id2 = new InstrumentID("166176649");
		
		final Map<InstrumentID, Instrument> results = DDF_RxInstrumentProvider.fromID(id1, id2)
				.toBlockingObservable().first();
		
		assertTrue(results.containsKey(id1));
		assertTrue(results.containsKey(id2));
		
		final Instrument i1 = results.get(id1);
		
//		for(final Entry<VendorID, String> e : i1.vendorSymbols().entrySet()) {
//			System.out.println(e.getKey() + " " + e.getValue());
//		}
		
		assertTrue("ESZ2014".equals(i1.vendorSymbols().get(VendorID.BARCHART)));
		assertTrue("ESZ4".equals(i1.vendorSymbols().get(VendorID.BARCHART_SHORT)));
		assertTrue("F.US.EPZ14".equals(i1.vendorSymbols().get(VendorID.CQG)));
		assertTrue("ESZ4".equals(i1.vendorSymbols().get(VendorID.OEC)));
		
		final Instrument i2 = results.get(id2);
		
//		for(final Entry<VendorID, String> e : i2.vendorSymbols().entrySet()) {
//			System.out.println(e.getKey() + " " + e.getValue());
//		}
		
		assertTrue("NQZ2014".equals(i2.vendorSymbols().get(VendorID.BARCHART)));
		assertTrue("NQZ4".equals(i2.vendorSymbols().get(VendorID.BARCHART_SHORT)));
		assertTrue("F.US.ENQZ14".equals(i2.vendorSymbols().get(VendorID.CQG)));
		assertTrue("NQZ4".equals(i2.vendorSymbols().get(VendorID.OEC)));
		
		
	}
	
}
