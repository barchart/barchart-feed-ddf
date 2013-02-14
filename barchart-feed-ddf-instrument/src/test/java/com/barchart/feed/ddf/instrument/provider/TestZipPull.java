package com.barchart.feed.ddf.instrument.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class TestZipPull {

	public static final String URL = "https://s3.amazonaws.com/instrument-def/active/20130213.zip";
	
	public static final String LOCAL = "/home/gavin/TestLocalDB/TestURL";
	
	public static void main(final String[] args) throws IOException {
		
		final URL instDefURL = new URL(URL);
		URLConnection conn = instDefURL.openConnection();
       
		InputStream in = conn.getInputStream();
		
		File outFile = new File(LOCAL);
	    FileOutputStream out = new FileOutputStream(outFile);
	       
	    byte[] b = new byte[1024];
	    int count;
	    while ((count = in.read(b)) >= 0) {
	    	out.write(b, 0, count);
	    }
	    out.flush(); out.close(); in.close();    
		
	}
	
}
