package com.barchart.feed.ddf.instrument.provider;

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.barchart.proto.buf.inst.InstrumentDefinition;

public class TestDefHashes {
	
	
	
	public static void main(final String[] args) throws Exception {
		
		int testPower = 23;
		int testSize = (int) Math.pow(2, testPower);
		
		System.out.println();
		System.out.println(testSize);
		System.out.println();
		
		int[] results = new int[testSize];
		for(int i = 0; i < testSize; i++) {
			results[i] = 0;
		}
		
		final File instDefZip = new File("/home/gavin/instrumentDef.zip");
		
		final ZipFile zFile = new ZipFile(instDefZip);
		final ZipEntry entry = zFile.entries().nextElement();
		final InputStream inStream = zFile.getInputStream(entry);
		
		InstrumentDefinition def = null;
		int totalCount = 0;
		while(true) {
			
			try {
				def = InstrumentDefinition.parseDelimitedFrom(inStream);
			} catch (Exception e) {
				System.out.println("Breaking exception in parse");
				break;
			}
			
			if(def == null) {
				break;
			}
			
			String symbol = def.getSymbol();
			
			int index = smear(symbol.hashCode());
			
			if(!symbol.equals("|")) {
				results[index & (testSize-1)]++;
				totalCount++;
			}
			
		}
		
		int histSize = 50;
		int[] hist = new int[histSize];
		
		for(int i = 0; i < histSize; i++) {
			hist[i] = 0;
		}
		
		for(int i = 0; i < testSize; i++) {
			if(results[i] > 1) {
				hist[results[i] - 2]++;
			}
		}
		
		int collisions = 0;
		for(int i = 0; i < histSize; i++) {
			if(hist[i] > 0) {
				System.out.println("" + (i + 2) + " collisions happened " + hist[i] + " times");
				collisions += hist[i];
			}
		}
		
		System.out.println();
		
		System.out.println(collisions / (double) totalCount);
		
	}
	
	/*
	 * This method was written by Doug Lea with assistance from members of JCP
	 * JSR-166 Expert Group and released to the public domain, as explained at
	 * http://creativecommons.org/licenses/publicdomain
	 * 
	 * As of 2010/06/11, this method is identical to the (package private) hash
	 * method in OpenJDK 7's java.util.HashMap class.
	 */
	static int smear(int hashCode) {
		hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
		return hashCode ^ (hashCode >>> 7) ^ (hashCode >>> 4);
	}

}
