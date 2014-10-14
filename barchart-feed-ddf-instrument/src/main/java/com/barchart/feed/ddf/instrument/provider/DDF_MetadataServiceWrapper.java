package com.barchart.feed.ddf.instrument.provider;

import java.util.Map;

import rx.Observable;

import com.barchart.feed.api.consumer.MetadataService;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.InstrumentID;

public class DDF_MetadataServiceWrapper implements MetadataService {

	@Override
	public Observable<Result<Instrument>> instrument(final String... symbols) {
		return DDF_RxInstrumentProvider.fromString(symbols);
	}

	@Override
	public Observable<Result<Instrument>> instrument(final SearchContext ctx,
			final String... symbols) {
		return DDF_RxInstrumentProvider.fromString(ctx, symbols);
	}

	@Override
	public Observable<Map<InstrumentID, Instrument>> instrument(final InstrumentID... ids) {
		return DDF_RxInstrumentProvider.fromID(ids);
	}

}
