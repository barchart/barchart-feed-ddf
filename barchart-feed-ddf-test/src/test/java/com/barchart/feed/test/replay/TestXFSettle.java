package com.barchart.feed.test.replay;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Session.Type;
import com.barchart.util.value.api.Price;

public class TestXFSettle {
	
	protected static final Logger log = LoggerFactory.getLogger(
			TestXFSettle.class);

	@Test
	public void testXFSettle() throws Exception {

		final BarchartMarketplaceReplay market =
				new BarchartMarketplaceReplay();

		final Agent agent = market.newAgent(Market.class, obs);

		agent.include("XFK14");

		final FeedReplayer replayer =
				new FeedReplayer(
						FeedReplayer.class.getResource("/XF_20140113.txt"));

		replayer.run(market.maker(), "XFK4");
		
		Market v = market.snapshot("XFK14");
		
		log.debug("Previous = " + v.sessionSet().session(Type.DEFAULT_PREVIOUS).settle().toString() + 
				" Current = " + v.session().settle());

	}

	static MarketObserver<Market> obs = new MarketObserver<Market>() {

		private Price prev = Price.NULL;
		private Price cur = Price.NULL;
		
		@Override
		public void onNext(final Market v) {
			
//			if(!prev.equals(v.sessionSet().session(Type.DEFAULT_PREVIOUS).settle()) ||
//					!cur.equals(v.session().settle())) {
//			
//				log.debug("Previous = " + v.sessionSet().session(Type.DEFAULT_PREVIOUS).settle().toString() + 
//					" Current = " + v.session().settle() + " ");
//				
//				prev = v.sessionSet().session(Type.DEFAULT_PREVIOUS).settle();
//				cur = v.session().settle();
//			
//			}
			
//			if(cur == Price.NULL) {
//				System.out.println();
//			} else {
//				System.out.println();
//			}
//
//			log.debug("Previous = " + v.sessionSet().session(Type.DEFAULT_PREVIOUS).settle().toString() + 
//					" Current = " + v.session().settle());
//			prev = v.sessionSet().session(Type.DEFAULT_PREVIOUS).settle();
//			cur = v.session().settle();

		}

	};

}
