package com.barchart.feed.ddf.instrument.provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.openfeed.proto.inst.InstrumentDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sleepycat.bind.EntityBinding;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Durability;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentLockedException;

public final class InstrumentDatabaseMap {
	
	private static final Logger log = LoggerFactory
			.getLogger(InstrumentDatabaseMap.class);

	private final StoredMap<String, InstrumentDefinition> map;
	
	private final Durability durability = new Durability(Durability.SyncPolicy.WRITE_NO_SYNC,
			Durability.SyncPolicy.WRITE_NO_SYNC, Durability.ReplicaAckPolicy.NONE);
	private final EnvironmentConfig envConfig = (EnvironmentConfig) new EnvironmentConfig()
		.setAllowCreate(true)
		.setTransactional(true)
		.setDurability(durability);
	private final DatabaseConfig dbConfig = new DatabaseConfig()
		.setAllowCreate(true)
		.setTransactional(true)
		.setSortedDuplicates(false);
	
	private final Database dbase;
	
	/**
	 * 
	 * @param dbase
	 */
	public InstrumentDatabaseMap(final Database dbase) {
		
		this.dbase = dbase;
		
		map = new StoredMap<String, InstrumentDefinition>(dbase, new SymbolBinding(), 
				new InstDefBinding(), true);
	}
	
	/**
	 * 
	 * @param dbFolder
	 * @throws EnvironmentLockedException if provided File is locked by another application
	 */
	public InstrumentDatabaseMap(final File dbFolder) throws EnvironmentLockedException {
		
		final Environment env = new Environment(dbFolder, envConfig);
		dbase = env.openDatabase(null, "InstrumentDef", dbConfig);
		
		map = new StoredMap<String, InstrumentDefinition>(dbase, new SymbolBinding(), 
				new InstDefBinding(), true);
		
	}
	
	/**
	 * 
	 * @param dbFolder
	 * @param inStream
	 */
	public InstrumentDatabaseMap(final File dbFolder, final InputStream inStream) {
		
		final Environment env = new Environment(dbFolder, envConfig);
		dbase = env.openDatabase(null, "InstrumentDef", dbConfig);
		map =  new StoredMap<String, InstrumentDefinition>(dbase, new SymbolBinding(), 
				new InstDefBinding(), true);
		
		populateDB(inStream);
		
	}
	
	/**
	 * 
	 * @param dbFolder
	 * @param instDefZip
	 * @throws ZipException
	 * @throws IOException
	 */
	public InstrumentDatabaseMap(final File dbFolder, final File instDefZip) 
			throws ZipException, IOException {
		
		final Environment env = new Environment(dbFolder, envConfig);
		dbase = env.openDatabase(null, "InstrumentDef", dbConfig);
		map = new StoredMap<String, InstrumentDefinition>(dbase, new SymbolBinding(), 
				new InstDefBinding(), true);
		
		InputStream inStream = null;
		
		try {
		
			final ZipFile zFile = new ZipFile(instDefZip);
			final ZipEntry entry = zFile.entries().nextElement();
			inStream = zFile.getInputStream(entry);
			
			populateDB(inStream);
		
		} finally {
			if(inStream != null) {
				inStream.close();
			}
		}
		
	}
	
	private void populateDB(final InputStream inStream) {
		
		long counter = 0;
		while(true) {
			InstrumentDefinition def;
			
			try {
				def = InstrumentDefinition.
						parseDelimitedFrom(inStream);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			
			if(def!= null) {
				
				if(def.hasSymbol()) {
					map.put(def.getSymbol(), def);
				}
				
				if(counter % 10000 == 0) {
					System.out.println("Build count " + counter);
				}
				
				counter++;
				
			} else {
				break;
			}
		}
		
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(final String key) {
		return map.containsKey(key);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public InstrumentDefinition get(final String key) {
		return map.get(key);
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void put(final String key, final InstrumentDefinition value) {
		map.put(key, value);
	}
	
	/**
	 * 
	 * @return
	 */
	public int size() {
		return map.size();
	}
	
	/**
	 * 
	 */
	public void clear() {
		map.clear();
	}
	
	/**
	 * 
	 */
	public void close() {
		log.info("DB Closed");
		dbase.close();
	}
	
	private class SymbolBinding implements EntryBinding<String> {

		@Override
		public String entryToObject(DatabaseEntry entry) {
			return new String(entry.getData());
		}

		@Override
		public void objectToEntry(String object, DatabaseEntry entry) {
			entry.setData(object.getBytes());
		}
		
	}
	
	private class InstDefBinding implements EntityBinding<InstrumentDefinition> {

		@Override
		public InstrumentDefinition entryToObject(DatabaseEntry key,
				DatabaseEntry data) {
			try {
				return InstrumentDefinition.parseFrom(data.getData());
			} catch (InvalidProtocolBufferException e) {
				return InstrumentDefinition.getDefaultInstance();
			}
		}

		@Override
		public void objectToKey(InstrumentDefinition object, DatabaseEntry key) {
			key.setData(object.getSymbol().getBytes());
		}

		@Override
		public void objectToData(InstrumentDefinition object, DatabaseEntry data) {
			data.setData(object.toByteArray());
		}
		
	}

}
