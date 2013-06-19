package com.barchart.feed.ddf.client.provider.legacy;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.Feed;
import com.barchart.feed.api.MarketCallback;
import com.barchart.feed.api.connection.ConnectionFuture;
import com.barchart.feed.api.model.CuvolEntry;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;

public class XX_CuvolCompareTest {
	
	protected static final Logger log = LoggerFactory.getLogger(
			XX_CuvolCompareTest.class);
	
	final static String SYMBOL = "GOOG";
	
	public static void main(final String[] args) throws InterruptedException, ExecutionException {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final TestableFeed feed = new TestableFeed(username, password);
		
		final MarketCallback<Market> callback = new MarketCallback<Market>() {

			@Override
			public void call(final Market v) {
				
				log.debug(
				v.instrument().symbol() + "\n" +
				printCuvol(v.cuvol().cuvolList()));
				
			}
			
		};
		
		final ConnectionFuture<Feed> start = feed.startup();
		
		start.get();
		
		final Agent myAgent = feed.newAgent(Market.class, callback);
		
		final Instrument inst = feed.lookup(SYMBOL);
		
		//myAgent.include(inst);
		
		feed.addTaker(new CuvolTaker(new Instrument[]{inst}));
		
		Thread.sleep(700000);
		
		
		
	}
	
	public static String printCuvol(final List<CuvolEntry> entries) {
		
		final StringBuilder sb = new StringBuilder();
		
		for(final CuvolEntry e : entries) {
			sb.append(e.volume().asDouble() + "\t");
		}
		
		return sb.toString();
		
	}
	
	public static class CuvolTaker implements 
			MarketTaker<com.barchart.feed.base.market.api.Market> {

				final Instrument[] instruments;
		
				public CuvolTaker(final Instrument[] instruments) {
					this.instruments = instruments;
				}
		
				@Override
				public MarketField<com.barchart.feed.base.market.api.Market> bindField() {
					return MarketField.MARKET;
				}

				@Override
				public MarketEvent[] bindEvents() {
					return MarketEvent.in(MarketEvent.values());
				}

				@Override
				public Instrument[] bindInstruments() {
					return instruments;
				}

				@Override
				public void onMarketEvent(MarketEvent event, Instrument instrument,
						com.barchart.feed.base.market.api.Market v) {
					
					log.debug(v.instrument().symbol() + "\n" +
							printCuvol(v.cuvol().cuvolList()));
					
				}
		
		
		
	};

}
