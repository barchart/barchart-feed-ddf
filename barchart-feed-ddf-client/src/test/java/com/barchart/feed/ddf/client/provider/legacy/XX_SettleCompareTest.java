package com.barchart.feed.ddf.client.provider.legacy;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.model.data.SessionSet;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;

public class XX_SettleCompareTest {
	
	// KCU13
	final static String SYMBOL = "ZCZ3";
	
	public static void main(final String[] args) throws Exception {
	
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final TestableFeed feed = new TestableFeed(username, password);
		
		final MarketObserver<SessionSet> callback = new MarketObserver<SessionSet>() {
	
			@Override
			public void onNext(final SessionSet v) {
				
//				if(v.session(Session.Type.DEFAULT_CURRENT).isSettled()) {
//				
//					System.out.println("AGENT: " +
//					v.instrument().symbol() + " " +
//					"IS SETTLED: " + v.session(Session.Type.DEFAULT_CURRENT).isSettled() + " " +
//					" @ " + v.session(Session.Type.DEFAULT_CURRENT).settle() + " " +
//					" PREVIOUS = " +
//					v.session(Session.Type.DEFAULT_PREVIOUS).close().asDouble()
//					
//					);
//				}
			}

			@Override
			public void onError(Throwable error) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onCompleted() {
				// TODO Auto-generated method stub
				
			}
			
		};
	
		feed.startup();
		
		final Agent myAgent = feed.newAgent(SessionSet.class, callback);
		
		final Instrument inst = DDF_InstrumentProvider.fromSymbol(SYMBOL);
		
		myAgent.include(inst);
		
		feed.addTaker(new SessionTaker(new Instrument[]{inst}));
		
		Thread.sleep(7000000);
	
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
			
			if(v.session().isSettled()) {
			
			System.out.println("TAKER: " + v.instrument().symbol() +  " " +
					"IS SETTLED: " + v.session().isSettled() + " " +
					" @ " + v.session().settle() + " " +
					" PREVIOUS = " 
					//v.session().previous().close().asDouble()
					
					+ "\n");
			
			}
			
		}
	
	};

}
