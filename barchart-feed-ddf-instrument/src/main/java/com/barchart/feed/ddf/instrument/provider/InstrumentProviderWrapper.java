package com.barchart.feed.ddf.instrument.provider;

import java.util.Collection;
import java.util.Map;


import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.ddf.instrument.provider.ext.NewInstrumentProvider;
import com.barchart.feed.inst.InstrumentFuture;
import com.barchart.feed.inst.InstrumentFutureMap;
import com.barchart.feed.inst.InstrumentService;

public class InstrumentProviderWrapper implements InstrumentService<String> {

	@Override
	public Instrument lookup(String symbol) {
		return NewInstrumentProvider.fromSymbol(symbol.toString());
	}

	@Override
	public InstrumentFuture lookupAsync(String symbol) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Instrument> lookup(Collection<String> symbols) {
		return NewInstrumentProvider.fromSymbols(symbols);
	}

	@Override
	public InstrumentFutureMap<String> lookupAsync(
			Collection<String> symbols) {
		throw new UnsupportedOperationException();
	}

}
