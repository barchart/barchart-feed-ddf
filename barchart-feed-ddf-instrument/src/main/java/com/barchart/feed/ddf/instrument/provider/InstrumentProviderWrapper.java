package com.barchart.feed.ddf.instrument.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.ddf.instrument.provider.ext.NewInstrumentProvider;
import com.barchart.feed.inst.InstrumentFuture;
import com.barchart.feed.inst.InstrumentFutureMap;
import com.barchart.feed.inst.InstrumentService;

public class InstrumentProviderWrapper implements InstrumentService<CharSequence> {

	@Override
	public List<Instrument> lookup(CharSequence symbol) {
		return Collections.singletonList(NewInstrumentProvider.fromSymbol(symbol.toString()));
	}

	@Override
	public InstrumentFuture lookupAsync(CharSequence symbol) {
		return DDF_InstrumentProvider.findAsync(symbol);
	}

	@Override
	public Map<CharSequence, List<Instrument>> lookup(
			Collection<? extends CharSequence> symbols) {
		return DDF_InstrumentProvider.find(symbols);
	}

	@Override
	public InstrumentFutureMap<CharSequence> lookupAsync(
			Collection<? extends CharSequence> symbols) {
		return DDF_InstrumentProvider.findAsync(symbols);
	}

}
