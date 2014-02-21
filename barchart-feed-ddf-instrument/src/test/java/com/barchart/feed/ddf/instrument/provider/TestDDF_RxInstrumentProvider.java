package com.barchart.feed.ddf.instrument.provider;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observer;

import com.barchart.feed.api.consumer.MetadataService.Result;
import com.barchart.feed.api.model.meta.Instrument;

public class TestDDF_RxInstrumentProvider {

	private static final Logger log = LoggerFactory.getLogger(
			TestDDF_RxInstrumentProvider.class);
	
	public static void main(final String[] args) throws Exception {
		
		DDF_RxInstrumentProvider.fromString("$DOWI").subscribe(obs());
		DDF_RxInstrumentProvider.fromString("DJY0").subscribe(obs());
		
		Thread.sleep(1 * 1000);
		
		// "F.US.CLES2X12"
		
//		DDF_RxInstrumentProvider.fromCQGString("F.US.CLES2X14")
//				.subscribe(strObs());  
		
		// 
		
		Thread.sleep(1 * 1000);
		System.exit(0);
		
	}
	
	
	static Observer<Result<Instrument>> obs() {
		return new Observer<Result<Instrument>>() {

			@Override
			public void onCompleted() {
				log.debug("On Completed Called");
			}

			@Override
			public void onError(Throwable e) {
				log.debug("On Error Called /n{}", e);
			}

			@Override
			public void onNext(Result<Instrument> args) {
				log.debug("On Next Called");
				
				final Map<String, List<Instrument>> insts = args.results();
				
				for(final Entry<String, List<Instrument>> e : insts.entrySet()) {
					
					log.debug("Query : {}", e.getKey());
					
					final List<Instrument> is = e.getValue();
					
					for(final Instrument i : is) {
						log.debug("Result : \n{}", i);
						log.debug("Inst Type : {}", i.securityType());
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
