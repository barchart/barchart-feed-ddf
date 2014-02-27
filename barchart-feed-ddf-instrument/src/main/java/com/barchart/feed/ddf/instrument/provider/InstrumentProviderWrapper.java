package com.barchart.feed.ddf.instrument.provider;

import java.util.Collection;
import java.util.Map;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.inst.InstrumentService;

public class InstrumentProviderWrapper implements InstrumentService<String> {

	@Override
	public Instrument lookup(String symbol) {
		return DDF_InstrumentProvider.fromSymbol(symbol.toString());
	}


	@Override
	public Map<String, Instrument> lookup(Collection<String> symbols) {
		return DDF_InstrumentProvider.fromSymbols(symbols);
	}


	@Override
	public Instrument lookup(InstrumentID id) {
		return DDF_InstrumentProvider.fromID(id);
	}
	
	

}
