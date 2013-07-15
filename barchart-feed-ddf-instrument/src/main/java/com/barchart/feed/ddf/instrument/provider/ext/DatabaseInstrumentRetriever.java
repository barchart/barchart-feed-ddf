package com.barchart.feed.ddf.instrument.provider.ext;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import com.barchart.feed.ddf.instrument.provider.LocalInstrumentDBMap;
import com.barchart.feed.inst.meta.Retriever;
import com.barchart.market.provider.api.model.meta.InstrumentState;

public class DatabaseInstrumentRetriever implements Retriever<InstrumentState> {
	
	private final ExecutorService executor;
	private final LocalInstrumentDBMap db;
	
	public DatabaseInstrumentRetriever(final ExecutorService executor, 
			final LocalInstrumentDBMap db) {
		this.executor = executor;
		this.db = db;
	}

	@Override
	public void retrieve(String id) {
		
	}

	@Override
	public void retrieve(String id, long timeout) {
		
	}

	@Override
	public void retrieve(Collection<String> ids) {
		
	}

	@Override
	public void retrieve(Collection<String> ids, long timeout) {
		
	}
	
	

}
