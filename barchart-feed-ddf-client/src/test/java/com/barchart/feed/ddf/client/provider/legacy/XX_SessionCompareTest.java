package com.barchart.feed.ddf.client.provider.legacy;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.model.data.Session;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;

public class XX_SessionCompareTest {
	
	final static String SYMBOL = "ESU3";
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final TestableFeed feed = new TestableFeed(username, password);
		
		final MarketObserver<Session> callback = new MarketObserver<Session>() {

			@Override
			public void onNext(final Session v) {
				
				System.out.println("AGENT: " +
				v.instrument().symbol() + " " +
				v.open().asDouble() + " " +
				v.high().asDouble() + " " +
				v.low().asDouble() + " " +
				v.volume().asDouble()
				
				);
				
			}

		};
		
		feed.startup();
		
		final Agent myAgent = feed.newAgent(Session.class, callback);
		
		final Instrument inst = DDF_InstrumentProvider.fromSymbol(SYMBOL);
		
		myAgent.include(inst);
		
		feed.addTaker(new SessionTaker(new Instrument[]{inst}));
		
		Thread.sleep(700000);
		
	}
	
	public static class SessionTaker implements 
			MarketTaker<com.barchart.feed.base.market.api.Market> {

			final Instrument[] instruments;
	
			public SessionTaker(final Instrument[] instruments) {
				this.instruments = instruments;
			}
	
			@Override
			public MarketField<com.barchart.feed.base.market.api.Market> bindField() {
				return MarketField.MARKET;
			}

			@Override
			public MarketEvent[] bindEvents() {
				return new MarketEvent[] {MarketEvent.NEW_BAR_CURRENT};
			}

			@Override
			public Instrument[] bindInstruments() {
				return instruments;
			}

			@Override
			public void onMarketEvent(MarketEvent event, Instrument instrument,
					com.barchart.feed.base.market.api.Market v) {
				
				System.out.println("TAKER: " + v.instrument().symbol() +  " " +
						v.session().open().asDouble() + " " +
						v.session().high().asDouble() + " " +
						v.session().low().asDouble() + " " +
						v.session().volume().asDouble()
						
						+ "\n");
				
			}
		
	};

}
