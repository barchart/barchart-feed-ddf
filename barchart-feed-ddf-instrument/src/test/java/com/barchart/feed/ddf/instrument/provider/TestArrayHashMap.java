package com.barchart.feed.ddf.instrument.provider;

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.barchart.feed.inst.provider.InstrumentFactory;
import com.barchart.proto.buf.inst.InstrumentDefinition;

public class TestArrayHashMap {
	
	public static void main(final String[] args) throws Exception {
		
		final ArrayHashMap map = new ArrayHashMap();
		
		final File instDefZip = new File("/home/gavin/instrumentDef.zip");
		
		final ZipFile zFile = new ZipFile(instDefZip);
		final ZipEntry entry = zFile.entries().nextElement();
		final InputStream inStream = zFile.getInputStream(entry);
		
		InstrumentDefinition def = null;
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
			
			if(!def.getSymbol().equals("|")) {
				map.put(def.getSymbol(), InstrumentFactory.buildFromProtoBuf(def));
			}
			
		}
		
		System.out.println("Finished loading instruments");
		
		while(true) {
			
		}
		
	}

}
