package com.barchart.feed.ddf.client.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class HistoricDataParsing {

	public static void main(final String[] args) throws IOException {
		
		final File zip = new File("/home/gavin/logs/Feed 20121203.zip");
        FileInputStream fis = new FileInputStream("/home/gavin/logs/Feed 20121203.zip");

        // this is where you start, with an InputStream containing the bytes from the zip file
        ZipInputStream zis = new ZipInputStream(fis);
        
        
        ZipEntry entry;
            // while there are entries I process them
        while ((entry = zis.getNextEntry()) != null) {
            System.out.println("entry: " + entry.getName() + ", " + entry.getSize());
                    // consume all the data from this entry
            while (zis.available() > 0)
                zis.read();
                 
        }
		
	}
	
}
