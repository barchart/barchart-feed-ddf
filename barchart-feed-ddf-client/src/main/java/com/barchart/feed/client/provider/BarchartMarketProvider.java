package com.barchart.feed.client.provider;

import rx.Observable;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.connection.Connection.Monitor;
import com.barchart.feed.api.connection.TimestampListener;
import com.barchart.feed.api.consumer.ConsumerAgent;
import com.barchart.feed.api.consumer.MarketService;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.MarketData;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.ddf.instrument.provider.DDF_RxInstrumentProvider;

public class BarchartMarketProvider implements MarketService {

	@Override
	public <V extends MarketData<V>> ConsumerAgent register(
			MarketObserver<V> callback, Class<V> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Observable<Market> snapshot(InstrumentID instrument) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bindConnectionStateListener(Monitor listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bindTimestampListener(TimestampListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Observable<Result<Instrument>> instrument(String... symbols) {
		return DDF_RxInstrumentProvider.fromString(SearchContext.NULL, symbols);
	}

	@Override
	public Observable<Result<Instrument>> instrument(SearchContext ctx,
			String... symbols) {
		return DDF_RxInstrumentProvider.fromString(ctx, symbols);
	}

}
