package com.barchart.feed.test.replay;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.model.data.Market;


public class TestXFSettle {

	public static void main(final String[] args) {

		BarchartMarketplaceReplay market = new BarchartMarketplaceReplay();
		
		Agent agent = market.newAgent(Market.class, obs);
		
		agent.include("XFH4");
		
		final FeedReplayer replayer =
				new FeedReplayer(
						FeedReplayer.class.getResource("/XF_20140113.txt"));

		replayer.run(market.maker());

	}
	
	static MarketObserver<Market> obs = new MarketObserver<Market>() {

		@Override
		public void onNext(final Market v) {
			//System.out.println(v.updated() + " ");		
		}
		
	};

}
