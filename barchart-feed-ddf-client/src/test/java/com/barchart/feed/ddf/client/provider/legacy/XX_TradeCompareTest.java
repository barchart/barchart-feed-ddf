package com.barchart.feed.ddf.client.provider.legacy;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.Feed;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.connection.ConnectionFuture;
import com.barchart.feed.api.model.data.Trade;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;

public class XX_TradeCompareTest {
	
	final static String SYMBOL = "ESU3";
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final TestableFeed feed = new TestableFeed(username, password);
		
		final MarketObserver<Trade> callback = new MarketObserver<Trade>() {

			@Override
			public void onNext(final Trade v) {
				
				System.out.println("AGENT: " +
				v.instrument().symbol() + " " +
				v.price().asDouble() + " " +
				v.size().asDouble()
				);
				
			}
			
		};
		
		final ConnectionFuture<Feed> start = feed.startup();
		
		start.get();
		
		final Agent myAgent = feed.newAgent(Trade.class, callback);
		
		final Instrument inst = DDF_InstrumentProvider.find(SYMBOL).get(0);
		
		myAgent.include(inst);
		
		feed.addTaker(new TradeTaker(new Instrument[]{inst}));
		
		Thread.sleep(700000);
		
	}
	
	public static class TradeTaker implements 
			MarketTaker<com.barchart.feed.base.market.api.Market> {

			final Instrument[] instruments;
	
			public TradeTaker(final Instrument[] instruments) {
				this.instruments = instruments;
			}
	
			@Override
			public MarketField<com.barchart.feed.base.market.api.Market> bindField() {
				return MarketField.MARKET;
			}

			@Override
			public MarketEvent[] bindEvents() {
				return new MarketEvent[] {MarketEvent.NEW_TRADE};
			}

			@Override
			public Instrument[] bindInstruments() {
				return instruments;
			}

			@Override
			public void onMarketEvent(MarketEvent event, Instrument instrument,
					com.barchart.feed.base.market.api.Market v) {
				
				final Trade trade = v.lastTrade();
				
				System.out.println("TAKER: " + v.instrument().symbol() +  " " +
						trade.price().asDouble() + " " +
						trade.size().asDouble() + "\n");
				
			}
		
	};

}
