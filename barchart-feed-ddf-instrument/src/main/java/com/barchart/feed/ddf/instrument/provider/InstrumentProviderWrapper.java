package com.barchart.feed.ddf.instrument.provider;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.api.inst.InstrumentService;

public class InstrumentProviderWrapper implements InstrumentService<CharSequence> {

	@Override
	public Instrument lookup(CharSequence symbol) {
		return DDF_InstrumentProvider.find(symbol);
	}

	@Override
	public Future<Instrument> lookupAsync(CharSequence symbol) {
		return null;
	}

	@Override
	public Map<CharSequence, Instrument> lookup(
			Collection<? extends CharSequence> symbols) {
		return DDF_InstrumentProvider.find(symbols);
	}

	@Override
	public Map<CharSequence, Future<Instrument>> lookupAsync(
			Collection<? extends CharSequence> symbols) {
		return null;
	}

}
