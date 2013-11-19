package com.barchart.feed.ddf.instrument.provider;

import rx.Observable;

import com.barchart.feed.api.consumer.MetadataService;
import com.barchart.feed.api.model.meta.Instrument;

public class DDF_MetadataServiceWrapper implements MetadataService {

	@Override
	public Observable<Result<Instrument>> instrument(String... symbols) {
		return DDF_RxInstrumentProvider.fromString(symbols);
	}

	@Override
	public Observable<Result<Instrument>> instrument(SearchContext ctx,
			String... symbols) {
		return DDF_RxInstrumentProvider.fromString(ctx, symbols);
	}

}
