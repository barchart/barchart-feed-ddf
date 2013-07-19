package cleanup;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.barchart.feed.api.util.Identifier;
import com.barchart.feed.inst.SymbologyContext;

class LocalCacheSymbologyContextDDF implements SymbologyContext<CharSequence> {

	private final ConcurrentMap<CharSequence, Identifier> symbolMap = 
			new ConcurrentHashMap<CharSequence, Identifier>();
	
	public void storeGUID(final CharSequence symbol, final Identifier guid) {
		symbolMap.put(symbol, guid);
	}
	
	@Override
	public Identifier lookup(final CharSequence symbol) {
		
		if(symbol == null || symbol.length() == 0) {
			return Identifier.NULL;
		}
		
		final Identifier guid = symbolMap.get(symbol);
		
		if(guid == null) {
			return Identifier.NULL;
		} else {
			return guid;
		}
		
	}

	@Override
	public Map<CharSequence, Identifier> lookup(
			Collection<? extends CharSequence> symbols) {
		
		final Map<CharSequence, Identifier> result = 
				new HashMap<CharSequence, Identifier>();
		
		for(final CharSequence symbol : symbols) {
			result.put(symbol, lookup(symbol));
		}
		
		return result;
	}

	@Override
	public List<Identifier> search(CharSequence symbol) {
		throw new UnsupportedOperationException("Search not supported");
	}

	@Override
	public List<Identifier> search(CharSequence symbol, int limit,
			int offset) {
		throw new UnsupportedOperationException("Search not supported");
	}

}
