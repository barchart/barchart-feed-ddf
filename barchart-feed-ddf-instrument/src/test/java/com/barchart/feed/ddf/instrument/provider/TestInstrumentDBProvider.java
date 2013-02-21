package com.barchart.feed.ddf.instrument.provider;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestInstrumentDBProvider {
	
	public static final String FOLDER_PATH = "src/test/resources/testDB";
	
	public static final String DUMMY_INST_DEF = "1900-01-01T01:00:00.zip";
	
	@Before
	public void startUp() {
		
		final File resourceFolder = new File(FOLDER_PATH);
		
		if(!resourceFolder.exists()) {
			resourceFolder.mkdir();
		}
		
		/* Clean resource folder for testing */
		final File[] files = resourceFolder.listFiles();
		
		for(final File file : files) {
			file.delete();
		}
		
	}
	
	@After
	public void shutDown() {
		
		final File resourceFolder = new File(FOLDER_PATH);
		
		final File dbFolder = InstrumentDBProvider.getDBFolder(resourceFolder);
		
		File[] files = dbFolder.listFiles();
		
		for(final File file : files) {
			file.delete();
		}
		
		files = resourceFolder.listFiles();
		
		for(final File file : files) {
			file.delete();
		}
		
	}
	
	@Test
	public void test() throws Exception {
		
		final File resourceFolder = new File(FOLDER_PATH);
		
		File localInstDef = InstrumentDBProvider.getLocalInstDef(resourceFolder);
		
		assertTrue(localInstDef == null);
		
		localInstDef = new File(FOLDER_PATH + "/" + DUMMY_INST_DEF);
		localInstDef.createNewFile();
		
		localInstDef = InstrumentDBProvider.getLocalInstDef(resourceFolder);
		
		assertTrue(localInstDef.getName().equals(DUMMY_INST_DEF));
		
		LocalInstrumentDBMap map = InstrumentDBProvider.getMap(resourceFolder);
		
		//InstrumentDBProvider.updateDBMap(resourceFolder, map).call();
		
		
	}

}
