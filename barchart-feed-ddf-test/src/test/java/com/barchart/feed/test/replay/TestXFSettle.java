package com.barchart.feed.test.replay;

public class TestXFSettle {

	protected static final Logger log = LoggerFactory
			.getLogger(TestXFSettle.class);

	@Test
	public void testXFSettle() throws Exception {

		final BarchartMarketplaceReplay market =
				new BarchartMarketplaceReplay();

		final Agent agent = market.newAgent(Market.class, obs);

		agent.include(Exchanges.fromName("BMF"));

		final FeedReplayer replayer =
				new FeedReplayer(
						FeedReplayer.class.getResource("/XF_20140113.txt"));

		replayer.run(market.maker(), "XFK4");
		
		Market v = market.snapshot("XFK14");
		
		log.debug("Previous = " + v.sessionSet().session(Type.DEFAULT_PREVIOUS).settle().toString() + 
				" Current = " + v.session().settle());

	}

	@Test
	public void testXFSettle_JJ() throws Exception {

		final BarchartMarketplaceReplay marketplace =
				new BarchartMarketplaceReplay();

		final SettleObserver so = new SettleObserver();
		final Agent agent = marketplace.newAgent(Market.class, so);

		agent.include(Exchanges.fromName("BMF"));

		final FeedReplayer replayer =
				new FeedReplayer(
						FeedReplayer.class.getResource("/XF_20140113.txt"));

		replayer.run(marketplace.maker(), null);

		System.out.println("Final settlement states:");
		for (final String sym : new String[] {
				"XFH14", "XFK14", "XFN14", "XFU14"
		}) {
			final Market snapshot = marketplace.snapshot(sym);
			System.out.println(sym
					+ ": settle="
					+ snapshot.sessionSet()
							.session(Session.Type.DEFAULT_CURRENT).settle()
							.asDouble()
					+ ", prevSettle="
					+ snapshot.sessionSet()
							.session(Session.Type.DEFAULT_PREVIOUS).settle()
							.asDouble());
		}

	}

	private static class SettleObserver implements MarketObserver<Market> {

		@Override
		public void onNext(final Market m) {
		}

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
