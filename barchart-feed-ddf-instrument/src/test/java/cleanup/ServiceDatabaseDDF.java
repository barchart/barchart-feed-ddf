package cleanup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import legacy.InstrumentFactory;

import org.openfeed.proto.inst.InstrumentDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.ddf.instrument.api.DDF_DefinitionService;
import com.barchart.feed.ddf.instrument.provider.InstrumentDatabaseMap;
import com.barchart.feed.inst.InstrumentFuture;
import com.barchart.feed.inst.InstrumentFutureMap;
import com.barchart.util.values.provider.ValueBuilder;

class ServiceDatabaseDDF implements DDF_DefinitionService {
	
	static final Logger log = LoggerFactory.getLogger(ServiceDatabaseDDF.class);
	
	private static final List<Instrument> EMPTY_LIST = Collections.emptyList();
	
	private final ConcurrentMap<CharSequence, List<Instrument>> cache = 
			new ConcurrentHashMap<CharSequence, List<Instrument>>();
	
	private final ConcurrentMap<CharSequence, Boolean> failedCache = 
			new ConcurrentHashMap<CharSequence, Boolean>();
	
	private final InstrumentDatabaseMap db;
	private final ExecutorService executor;
	private final ServiceMemoryDDF remoteInstService;
	
	public ServiceDatabaseDDF(final InstrumentDatabaseMap map, 
			final ExecutorService executor) {
		
		this.db = map;
		this.executor = executor;
		remoteInstService = new ServiceMemoryDDF(executor);
		
	}

	@Override
	public List<Instrument> lookup(final CharSequence symbol) {
		
		try {
			return retrieve(symbol);
		} catch (final Throwable t) {
			log.error("Lookup failed", t);
			return Collections.emptyList();
		}
		
	}

	@Override
	public InstrumentFuture lookupAsync(final CharSequence symbol) {
		
		InstrumentFuture future = new InstrumentFuture();
		
		executor.submit(new LookupCallable(symbol, future));
		
		return future;
		
	}

	@Override
	public Map<CharSequence, List<Instrument>> lookup(
			final Collection<? extends CharSequence> symbols) {
		
		try {
			return retrieveMap(symbols);
		} catch (final Throwable t) {
			log.error("Lookup failed", t);
			return Collections.emptyMap();
		}
		
	}

	@Override
	public InstrumentFutureMap<CharSequence> lookupAsync(
			final Collection<? extends CharSequence> symbols) {
		
		InstrumentFutureMap<CharSequence> future = 
				new InstrumentFutureMap<CharSequence>();
		
		executor.submit(new LookupMapCallable(symbols, future));
		
		return future;
		
	}
	
	private List<Instrument> retrieve(CharSequence symbol) {
		
		symbol = ValueBuilder.newText(symbol.toString());
		
		if(symbol == null || symbol.length() == 0) {
			return Collections.emptyList();
		}
		
		if(failedCache.containsKey(symbol)) {
			return EMPTY_LIST;
		}
		
		/*
		 * FIXME HACK
		 * Currently need to filter options because of symbology
		 */
		if(symbol.charAt(symbol.length() - 1) == 'P' ||
			symbol.charAt(symbol.length() - 1) == 'C' ||
			symbol.charAt(symbol.length() - 1) == 'Q' ||
			symbol.charAt(symbol.length() - 1) == 'D') {
			return EMPTY_LIST;
		}
		
		List<Instrument> instrument = cache.get(symbol);
		
		if(instrument != null) {
			return instrument;
		}
		
		final InstrumentDefinition instDef = db.get(symbol.toString());
		
		if(cache.size() % 10000 == 0) {
			log.debug("Cache size = {}", cache.size());
		}
		
		if(instDef != null) {
			
			instrument = Collections.singletonList(
					InstrumentFactory.buildFromProtoBuf(instDef));
			cache.put(symbol, instrument);
			
			return instrument;
			
		} 
		
		//log.debug("Symbol {} not found in DB, using remote", symbol);
		instrument = remoteInstService.lookup(symbol);
		
		if(instrument != null && instrument.size() > 0) {
			cache.put(symbol, instrument);
			return instrument;
		} else {
			failedCache.put(symbol, false);
			return EMPTY_LIST;
		}
		
	}
	
	private class LookupCallable implements Callable<List<Instrument>> {

		private final CharSequence symbol;
		private final InstrumentFuture future;
		
		public LookupCallable(final CharSequence symbol,
				final InstrumentFuture future) {
			this.symbol = symbol;
			this.future = future;
		}
		
		@Override
		public List<Instrument> call() throws Exception {
			
			List<Instrument> inst;
			
			try {
			
				inst = retrieve(symbol);
				future.succeed(inst);
				
			} catch (final Throwable t) {
				future.fail(t);
				return Collections.emptyList();
			}
			
			return inst;
			
		}
		
	}
	
	private final class LookupMapCallable implements 
			Callable<Map<CharSequence, List<Instrument>>> {

		private final Collection<? extends CharSequence> symbols;
		private final InstrumentFutureMap<CharSequence> future;

		LookupMapCallable(final Collection<? extends CharSequence> symbols,
				final InstrumentFutureMap<CharSequence> future) {
			this.symbols = symbols;
			this.future = future;
		}

		@Override
		public Map<CharSequence, List<Instrument>> call() throws Exception {
			
			Map<CharSequence, List<Instrument>> map;
			
			try {
				
				map = retrieveMap(symbols);
				future.succeed(map);
				
			} catch (final Throwable t) {
				future.fail(t);
				return Collections.emptyMap();
			}
			
			return map;
		}

	}
	
	private Map<CharSequence, List<Instrument>> retrieveMap(
			final Collection<? extends CharSequence> symbols) {
		
		if (symbols == null || symbols.size() == 0) {
			log.warn("Lookup called with empty collection");
			return Collections.emptyMap(); 
		}
		
		final Map<CharSequence, List<Instrument>> result = 
				new HashMap<CharSequence, List<Instrument>>();

		for (final CharSequence symbol : symbols) {
			try {
				result.put(symbol.toString(), retrieve(symbol));
			} catch (final Exception e) {
				result.put(symbol.toString(), new ArrayList<Instrument>(0));
			}
		}
		
		return result;
		
	}
	
}
