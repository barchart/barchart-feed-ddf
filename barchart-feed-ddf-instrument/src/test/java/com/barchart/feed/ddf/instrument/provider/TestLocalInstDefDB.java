package com.barchart.feed.ddf.instrument.provider;

import java.io.IOException;
import java.util.zip.ZipException;

public class TestLocalInstDefDB {

	public static void main(final String[] args) throws ZipException, IOException {
		
		final String resourcePath = "/home/gavin/TestLocalDB";
		
		final LocalInstDefDB db = new LocalInstDefDB(resourcePath);
		
	}
	
}
