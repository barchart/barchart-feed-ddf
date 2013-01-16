package com.barchart.feed.ddf.instrument.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestLocalDefService {
	
	public static void main(final String[] args) throws IOException {
		
		final File defFile = new File("/home/gavin/Desktop/instrument_dump.txt");
		final InputStream input = new FileInputStream(defFile);
		
		LocalDefinitionServiceDDF defService = new LocalDefinitionServiceDDF(input);
		DDF_InstrumentProvider.bind(defService);
		
		// Do Something
		
		System.out.println();
		
	}

}
