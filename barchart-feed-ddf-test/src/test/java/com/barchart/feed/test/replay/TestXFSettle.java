package com.barchart.feed.test.replay;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.model.data.Market;

public class TestXFSettle {

	@Test
	public void testXFSettle() throws Exception {

		final BarchartMarketplaceReplay market =
				new BarchartMarketplaceReplay();

		final Agent agent = market.newAgent(Market.class, obs);

		agent.include("XFH14");

		final FeedReplayer replayer =
				new FeedReplayer(
						FeedReplayer.class.getResource("/XF_20140113.txt"));

		replayer.run(market.maker());

	}

	static MarketObserver<Market> obs = new MarketObserver<Market>() {

		@Override
		public void onNext(final Market v) {
			fail("Check market state");
		}

	};

}
