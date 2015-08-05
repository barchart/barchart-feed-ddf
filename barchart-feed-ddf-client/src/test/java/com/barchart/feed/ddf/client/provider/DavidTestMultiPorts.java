package com.barchart.feed.ddf.client.provider;

import java.util.HashSet;
import java.util.Set;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.client.provider.BarchartMarketplace;
import com.barchart.feed.client.provider.BarchartMarketplace.FeedType;
import com.barchart.feed.inst.Exchanges;

public class DavidTestMultiPorts {

	public static void main(final String[] args) throws Exception {
		
		final Marketplace feed = BarchartMarketplace.builder()
				.feedType(FeedType.LISTENER_TCP)
				.port(10110) // AMEX B/A
				.port(10010) // AMEX
				.port(10130) // NASDAQ B/A 
				.port(10030) // NASDAQ
				.port(10120) // NYSE B/A
				.port(10020) // NYSE
				.build();
		
		feed.startup();
		
		final Set<String> exchanges = new HashSet<String>();
		exchanges.add("A"); // AMEX
		exchanges.add("a"); // AMEX B/A
		exchanges.add("Q"); // NASDAQ 
		exchanges.add("q"); // NASDAQ B/A
		exchanges.add("N"); // NYSE
		exchanges.add("n"); // NYSE B/A
		
		final MarketObserver<Market> callback = new MarketObserver<Market>() {

			@Override
			public void onNext(final Market v) {
				
				@SuppressWarnings("deprecation")
				final String code = v.instrument().exchange().id().id();
				
				if(exchanges.remove(code)) {
					System.out.println("Saw code " + code);
				}
				
			}
			
		};
		
		final Agent myAgent = feed.newAgent(Market.class, callback);
		myAgent.include(Exchanges.fromCode("A").id());
		myAgent.include(Exchanges.fromCode("a").id());
		myAgent.include(Exchanges.fromCode("Q").id());
		myAgent.include(Exchanges.fromCode("q").id());
		myAgent.include(Exchanges.fromCode("N").id());
		myAgent.include(Exchanges.fromCode("n").id());
		
		Thread.sleep(60 * 1000);
		
	}
	
	
}
