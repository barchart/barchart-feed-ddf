package com.barchart.feed.ddf.instrument.provider;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observer;

import com.barchart.feed.api.consumer.MetadataService.Result;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.api.model.meta.id.VendorID;

public class TestDDF_RxInstrumentProvider {

	private static final Logger log = LoggerFactory.getLogger(
			TestDDF_RxInstrumentProvider.class);
	
	// _S_EQ_ESZ4_ESH5
	
	public static void main(final String[] args) throws Exception {
		
//		DDF_RxInstrumentProvider.fromID(new InstrumentID("136439690")
//				, new InstrumentID("165730509")
//				, new InstrumentID("166176649")	
//				//, "ESV2014|2045C"
//				).subscribe(idObs());
//		
//		Thread.sleep(3 * 1000);
		
		log.debug("****************************************************************");
		
		DDF_RxInstrumentProvider.fromString("EADSF.BZ").subscribe(obs());
		//DDF_RxInstrumentProvider.fromID(new InstrumentID(68926976)).subscribe(idObs());
		
		Thread.sleep(3 * 1000);
		
		// "F.US.CLES2X12"
		
//		DDF_RxInstrumentProvider.fromCQGString("F.US.CLES2X14").subscribe(strObs());  
		
		//Thread.sleep(1 * 1000);
		//DDF_RxInstrumentProvider.fromString("ESM2014|1950C").subscribe(obs());
		
//		DDF_RxInstrumentProvider.fromID(new InstrumentID("1000002")).subscribe(idObs());
//		
//		Thread.sleep(3 * 1000);
		
		//System.exit(0);
		
	}
	
	static Observer<Map<InstrumentID, Instrument>> idObs() {
		return new Observer<Map<InstrumentID, Instrument>>() {

			@Override
			public void onCompleted() {
				log.debug("On Completed Called");
			}

			@Override
			public void onError(Throwable e) {
				log.error("On Error Called", e);
			}

			@Override
			public void onNext(Map<InstrumentID, Instrument> t) {
				log.debug("On Next Called");
				
				for(final Entry<InstrumentID, Instrument> e : t.entrySet()) {
					final Instrument i = e.getValue();
					log.debug("ID = {} Instrument = \n{}", e.getKey(), e.getValue());
					for(final Entry<VendorID, String> en : i.vendorSymbols().entrySet()) {
						log.debug("{} - {}", en.getKey(), en.getValue());
					}
				}
				
				System.out.println();
				
			}
			
		};
	}
	
	static Observer<Result<Instrument>> obs() {
		return new Observer<Result<Instrument>>() {

			@Override
			public void onCompleted() {
				log.debug("On Completed Called");
			}

			@Override
			public void onError(Throwable e) {
				log.error("On Error Called", e);
			}

			@Override
			public void onNext(Result<Instrument> args) {
				log.debug("On Next Called");
				
				final Map<String, List<Instrument>> insts = args.results();
				
				for(final Entry<String, List<Instrument>> e : insts.entrySet()) {
					
					log.debug("Query : {}", e.getKey());
					
					final List<Instrument> is = e.getValue();
					
					if(is.isEmpty()) {
						log.error("EMPTY LIST");
					}
					
					for(final Instrument i : is) {
//						log.debug("Result : \n{}", i);
//						log.debug("Inst Type : {}", i.securityType());
//						log.debug("InstID : {}", i.id().toString());
//						if(i.strikePrice().isNull()) {
//							log.debug("Strike Price is NULL");
//						} else {
//							log.debug("Strike price : {}", i.strikePrice());
//						}
//						log.debug("Underlier ID = {}", i.underlier());
//						log.debug("Spread Type is = {}", i.spreadType());
//						log.debug("Number of legs = {}", i.spreadLegs().size());
//						log.debug("Exchange Code = {}", i.exchangeCode());
//						log.debug("Point Value = {}", i.pointValue());
//						log.debug("Tick Size = {}", i.tickSize());
						
						for(final Entry<VendorID, String> v : i.vendorSymbols().entrySet()) {
							log.debug("VENDOR ID = {} SYMBOL = {}", v.getKey(), v.getValue());
						}
						log.debug("STRIKE PRICE = {}", i.strikePrice());
						log.debug("Security Type = {}", i.securityType());
						log.debug("CFI Code = {}", i.CFICode());
						log.debug("Price Step = {}", i.tickSize());
						log.debug("Expiration = {}", i.expiration());
						log.debug("Description = {}", i.description());
						
					}
					
				}
				
			}
			
		};
	}
	
	static Observer<String> strObs() {
		return new Observer<String>() {

			@Override
			public void onCompleted() {
				
			}

			@Override
			public void onError(final Throwable e) {
				
			}

			@Override
			public void onNext(final String args) {
				System.out.println(args);
			}
			
		};
		
	}
	
	
}
