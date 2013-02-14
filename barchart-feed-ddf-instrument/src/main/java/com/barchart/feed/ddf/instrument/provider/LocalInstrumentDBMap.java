package com.barchart.feed.ddf.instrument.provider;

import java.io.File;

import com.barchart.proto.buf.inst.InstrumentDefinition;
import com.google.protobuf.InvalidProtocolBufferException;
import com.sleepycat.bind.EntityBinding;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class LocalInstrumentDBMap {

	final StoredMap<String, InstrumentDefinition> map;
	
	public LocalInstrumentDBMap(final Database dbase) {
		map = new StoredMap<String, InstrumentDefinition>(dbase, new SymbolBinding(), 
				new InstDefBinding(), true);
	}
	
	public LocalInstrumentDBMap(final File dbFile) {
		
		final EnvironmentConfig envConfig = new EnvironmentConfig()
			.setAllowCreate(true)
			.setTransactional(true);
		envConfig.setTxnNoSync(true); // TODO
		final Environment env = new Environment(dbFile, envConfig);
		
		final DatabaseConfig dbConfig = new DatabaseConfig()
			.setAllowCreate(true)
			.setTransactional(true)
			.setSortedDuplicates(false);
		final Database db = env.openDatabase(null, "InstrumentDef", dbConfig);
		
		map = new StoredMap<String, InstrumentDefinition>(db, new SymbolBinding(), 
				new InstDefBinding(), true);
	}
	
	public boolean containsKey(final String key) {
		return map.containsKey(key);
	}
	
	public InstrumentDefinition getValue(final String key) {
		return map.get(key);
	}
	
	public void put(final String key, final InstrumentDefinition value) {
		map.put(key, value);
	}
	
	public int size() {
		return map.size();
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
