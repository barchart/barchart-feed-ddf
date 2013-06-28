package com.barchart.feed.ddf.client.provider.legacy;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.connection.ConnectionFuture;
import com.barchart.feed.api.model.data.Book.Top;
import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;

public class XX_BookCompareTest {

final static String SYMBOL = "CLN3";
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final TestableFeed feed = new TestableFeed(username, password);
		
		final MarketObserver<Book> callback = new MarketObserver<Book>() {

			@Override
			public void onNext(final Book v) {
				
				final Top top = v.top();
				
				if(top == Top.NULL) {
					System.out.println("TOP OF BOOK NULL");
				} else {
				
					System.out.println("AGENT: " +
						v.instrument().symbol() + " " +
						top.ask().price().asDouble() + " " +
						top.ask().size().asDouble() + " " +
						top.bid().size().asDouble() + " " +
						top.bid().price().asDouble() + " " +
						v.lastBookUpdate().price().asDouble()
					);
					
				}
				
			}
			
		};
		
		final ConnectionFuture<Marketplace> start = feed.startup();
		
		start.get();
		
		final Agent myAgent = feed.newAgent(Book.class, callback);
		
		final Instrument inst = DDF_InstrumentProvider.find(SYMBOL).get(0);
		
		myAgent.include(inst);
		
		feed.addTaker(new BookTaker(new Instrument[]{inst}));
		
		Thread.sleep(700000);
		
	}
	
	public static class BookTaker implements 
			MarketTaker<com.barchart.feed.base.market.api.Market> {

		final Instrument[] instruments;

		public BookTaker(final Instrument[] instruments) {
			this.instruments = instruments;
		}

		@Override
		public MarketField<com.barchart.feed.base.market.api.Market> bindField() {
			return MarketField.MARKET;
		}

		@Override
		public MarketEvent[] bindEvents() {
			return new MarketEvent[] {MarketEvent.NEW_BOOK_SNAPSHOT, MarketEvent.NEW_BOOK_TOP,
					MarketEvent.NEW_BOOK_UPDATE};
		}

		@Override
		public Instrument[] bindInstruments() {
			return instruments;
		}

		@Override
		public void onMarketEvent(MarketEvent event, Instrument instrument,
				com.barchart.feed.base.market.api.Market v) {
			
			final Top top = v.book().top();
			
			System.out.println("TAKER: " + v.instrument().symbol() +  " " +
					top.ask().price().asDouble() + " " +
					top.ask().size().asDouble() + " " +
					top.bid().size().asDouble() + " " +
					top.bid().price().asDouble() + " " +
					v.book().lastBookUpdate().price().asDouble() + "\n");
			
		}
		
	};
	
}
