package com.barchart.feed.ddf.instrument.provider.ext;

import java.util.concurrent.Callable;

import org.openfeed.proto.inst.InstrumentDefinition;

public class TestNewInstrumentProvider {
	
	public static void main(final String[] args) throws Exception {
		
		final Callable<InstrumentDefinition> remote1 = 
				NewInstrumentProvider.remoteCallable("ESU3");
		
		final InstrumentDefinition def1 = remote1.call();
		
		System.out.println(def1.toString());
		
	}

}
