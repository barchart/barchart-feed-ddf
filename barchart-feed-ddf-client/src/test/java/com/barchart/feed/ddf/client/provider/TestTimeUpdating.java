package com.barchart.feed.ddf.client.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Market.Component;
import com.barchart.feed.client.provider.BarchartMarketplace;

public class TestTimeUpdating {

	private static final Logger log = LoggerFactory.getLogger(
			TestTimeUpdating.class);
	
	private static final String[] insts = {
		"GOOG" //"CLH500C" ,  , "ZCM15"
	};
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
		
		final Marketplace market = BarchartMarketplace.builder()
				.username(username)
				.password(password)
				.build();
		
		market.startup();
		
		Thread.sleep(500);
		
		final Agent agent = market.subscribeMarket(marketObs(), insts);
		agent.activate();
		
		Thread.sleep(10 * 60 * 1000);
		market.shutdown();
		
	}
	
	private static MarketObserver<Market> marketObs() {
		
		return new MarketObserver<Market>() {
			
			@Override
			public void onNext(final Market m) {
				
				if(m.trade().time().isNull()) {
					return;
				}
				
				if(m.change().contains(Component.TRADE)) {
					log.debug("{} *TRADE UPDATED = {} TRADE = {}", 
							m.instrument().symbol(), 
							m.updated().asDate(), 
							m.trade().time().asDate());
				} else {
					log.debug("{} MARKET UPDATED = {} TRADE = {}", 
							m.instrument().symbol(), 
							m.updated().asDate(), 
							m.trade().time().asDate());
				}
				
			}
			
		};
		
	}
	
}
