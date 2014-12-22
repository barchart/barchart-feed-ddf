package com.barchart.feed.test;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FormatDataFromJoel {
	
	public static void main(final String[] args) throws IOException {
		
		final InputStream input = ClassLoader.getSystemResourceAsStream("ZCH15/zch5-clean.txt");
		final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		final FileWriter writer = new FileWriter("ZCH15.txt");
		
		String line = reader.readLine();
		while(line != null) {
			
			writer.write(line
					.replace("<soh>", "")
					.replace("<stx>", "")
					.replace("<etx>", ""));
			
			writer.write("\n");
			
			line = reader.readLine();
		}
		
		writer.flush();
		writer.close();
		
	}
	
}
