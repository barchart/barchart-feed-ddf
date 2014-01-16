package com.barchart.feed.test.replay;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Session;
import com.barchart.feed.inst.provider.Exchanges;
import com.barchart.util.value.api.Time;

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

		replayer.run(market.maker());

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

		replayer.run(marketplace.maker());

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

		private Time updated = Time.NULL;

		@Override
		public void onNext(final Market v) {

			if (updated.isNull()) {
				updated = v.session().updated();
				return;
			}

			if (updated.compareTo(v.session().updated()) > 0) {
				log.debug("Updated time less than previous time {}",
						updated.millisecond()
								- v.session().updated().millisecond());
			}

		}

	};

}
