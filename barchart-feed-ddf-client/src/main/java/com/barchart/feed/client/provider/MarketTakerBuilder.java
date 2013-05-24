/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.client.provider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.util.values.api.Value;

public class MarketTakerBuilder<V extends Value<V>> {

	private Set<MarketEvent> events = new HashSet<MarketEvent>();
	private Set<Instrument> instruments = new HashSet<Instrument>();
	private List<MarketEventCallback<V>> eventCallbacks = new ArrayList<MarketEventCallback<V>>();
	private MarketField<V> field = null;
	
	public MarketTakerBuilder<V> addMarketField(final MarketField<V> field) {
		this.field = field;
		return this;
	}
	
	public MarketTakerBuilder<V> addEvent(final MarketEvent event) {
		events.add(event);
		return this;
	}
	
	public MarketTakerBuilder<V> addInstrument(final Instrument inst) {
		instruments.add(inst);
		return this;
	}
	
	public MarketTakerBuilder<V> addSymbol(final String symbol) {
		instruments.add(DDF_InstrumentProvider.find(symbol));
		return this;
	}
	
	public MarketTakerBuilder<V> addInstruments(final List<Instrument> insts) {
		instruments.addAll(insts);
		return this;
	}
	
	public MarketTakerBuilder<V> addSymbols(final List<String> symbols) {
		instruments.addAll(DDF_InstrumentProvider.find(symbols).values());
		return this;
	}
	
	public MarketTakerBuilder<V> addEventCallback(final MarketEventCallback<V> eventCallback) {
		eventCallbacks.add(eventCallback);
		return this;
	}
	
	public MarketTaker<V> build() {	
		
		final MarketTaker<V> taker =  new MarketTaker<V>() {
		
			final MarketEvent[] eventArray = events.toArray(new MarketEvent[0]);
			final Instrument[] instArray = instruments.toArray(new Instrument[0]);
 			final MarketEventCallback<V>[] callbackArray = eventCallbacks.toArray(new MarketEventCallback[0]);
			final MarketField<V> boundField = field;
 			
			@Override
			public MarketField<V> bindField() {
				return boundField;
			}
	
			@Override
			public MarketEvent[] bindEvents() {
				return eventArray;
			}
	
			@Override
			public Instrument[] bindInstruments() {
				return instArray;
			}
	
			@Override
			public void onMarketEvent(MarketEvent event,
					Instrument instrument, V value) {
				for(final MarketEventCallback<V> e : callbackArray) {
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
