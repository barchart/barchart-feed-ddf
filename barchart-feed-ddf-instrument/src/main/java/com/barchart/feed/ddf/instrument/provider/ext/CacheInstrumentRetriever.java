package com.barchart.feed.ddf.instrument.provider.ext;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.barchart.feed.api.util.Observer;
import com.barchart.feed.inst.meta.Result;
import com.barchart.feed.inst.meta.Retriever;
import com.barchart.market.provider.api.model.meta.InstrumentState;

public class CacheInstrumentRetriever implements Retriever<InstrumentState> {

	private static final long DEFAULT_TIMEOUT = 5000;
	private static final TimeUnit MILLIS = TimeUnit.MILLISECONDS;
	
	private final ConcurrentMap<String, InstrumentState> symbolMap =
			new ConcurrentHashMap<String, InstrumentState>();
	
	private final ExecutorService executor;
	private Observer<Result<InstrumentState>> observer = null;
	
	public CacheInstrumentRetriever(final ExecutorService executor, 
			final Observer<Result<InstrumentState>> observer) {
		this.executor = executor;
		this.observer = observer;
	}
	
	@Override
	public void retrieve(final String id) {
		executor.submit(runFactory(id, DEFAULT_TIMEOUT));
	}

	@Override
	public void retrieve(final String id, final long timeout) {
		executor.submit(runFactory(id, timeout));
	}

	@Override
	public void retrieve(Collection<String> ids) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieve(Collection<String> ids, long timeout) {
		// TODO Auto-generated method stub
		
	}

	private Runnable runFactory(final String id, final long timeout) {
		
		return new Runnable() {

			@Override
			public void run() {
				
				try {
					final Future<InstrumentState> f = executor.submit(callFactory(id));
					final InstrumentState i = f.get(timeout, MILLIS);
					
					if(i == null || i.isNull()) {
						// TODO Go on to next retriever
						observer.onNext(new InstrumentResult(InstrumentState.NULL, id));
					}
					
					/* Good result */
					observer.onNext(new InstrumentResult(i, id));
					
				} catch (final Exception e) {
					/* Failure Result */
					observer.onNext(new InstrumentResult(id, e));
				}
				
				
			}
			
		};
		
	}
	
	// Problem with this is that it doesn't compose with other retrievers
	
	private Callable<InstrumentState> callFactory(final String id) {
		
		return new Callable<InstrumentState>() {
	
			@Override
			public InstrumentState call() throws Exception {
				
				if(symbolMap.containsKey(id)) {
					return symbolMap.get(id);
				} else {
					return InstrumentState.NULL;
				}
				
			}
		
		};
		
	}
	
}
