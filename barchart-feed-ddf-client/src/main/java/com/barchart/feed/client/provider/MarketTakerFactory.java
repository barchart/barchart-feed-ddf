package com.barchart.feed.client.provider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.Market;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;

public class MarketTakerFactory {

	private Set<MarketEvent> events = new HashSet<MarketEvent>();
	private Set<MarketInstrument> instruments = new HashSet<MarketInstrument>();
	private List<MarketEventCallback> eventCallbacks = new ArrayList<MarketEventCallback>();
	
	public MarketTakerFactory addEvent(final MarketEvent event) {
		events.add(event);
		return this;
	}
	
	public MarketTakerFactory addInstrument(final MarketInstrument inst) {
		instruments.add(inst);
		return this;
	}
	
	public MarketTakerFactory addSymbol(final String symbol) {
		instruments.add(DDF_InstrumentProvider.find(symbol));
		return this;
	}
	
	public MarketTakerFactory addInstruments(final List<MarketInstrument> insts) {
		instruments.addAll(insts);
		return this;
	}
	
	public MarketTakerFactory addSymbols(final List<String> symbols) {
		instruments.addAll(DDF_InstrumentProvider.find(symbols));
		return this;
	}
	
	public MarketTakerFactory addEventCallback(final MarketEventCallback eventCallback) {
		eventCallbacks.add(eventCallback);
		return this;
	}
	
	public MarketTaker<Market> build() {	
		
		final MarketTaker<Market> taker =  new MarketTaker<Market>() {
		
			final MarketEvent[] eventArray = events.toArray(new MarketEvent[0]);
			final MarketInstrument[] instArray = instruments.toArray(new MarketInstrument[0]);
 			final MarketEventCallback[] callbackArray = eventCallbacks.toArray(new MarketEventCallback[0]);
			
			@Override
			public MarketField<Market> bindField() {
				return MarketField.MARKET;
			}
	
			@Override
			public MarketEvent[] bindEvents() {
				return eventArray;
			}
	
			@Override
			public MarketInstrument[] bindInstruments() {
				return instArray;
			}
	
			@Override
			public void onMarketEvent(MarketEvent event,
					MarketInstrument instrument, Market value) {
				for(final MarketEventCallback e : callbackArray) {
					e.onMarketEvent(event, instrument, value);
				}
			}
			
		};
		
		events.clear();
		instruments.clear();
		eventCallbacks.clear();
		
		return taker;
		
	}
	
}
