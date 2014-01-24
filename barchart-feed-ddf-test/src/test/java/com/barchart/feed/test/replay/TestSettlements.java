package com.barchart.feed.test.replay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Session;
import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.api.DDF_MarketParameter;
import com.barchart.feed.ddf.message.api.DDF_MarketSnapshot;
import com.barchart.feed.ddf.message.api.DDF_MarketTrade;
import com.barchart.feed.ddf.message.provider.DDF_MessageService;
import com.barchart.feed.ddf.util.FeedDDF;
import com.barchart.feed.test.replay.FeedReplay.MessageListener;
import com.barchart.util.value.api.Price;

// This shit needs redoing after feed changes
public class TestSettlements {

	private static final DateFormat DP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final DateFormat DF = new SimpleDateFormat(
			"yyyy-MM-dd (EEE)");

	protected static final Logger log = LoggerFactory
			.getLogger(TestSettlements.class);

	private final SettleObserver observer = new SettleObserver();

	private BarchartMarketplaceReplay marketplace;

	public void testXFH14() throws Exception {

		testSettlements("XFH4", "/XF-20140110-week.ddf", //
				"09:35:00", // Time when settlements should be reset
				"13:30:00", // Time when settlements should be in
				new Double[] { 145.95, null }, // Friday
				new Double[] {
						null, null
				}, // Saturday (no change)
				new Double[] { null, 145.95 }, // Sunday (no change)
				new Double[] { 145.50, 145.95 }, // Monday
				new Double[] { 143.75, 145.50 }, // Tuesday
				new Double[] { 141.85, 143.75 }, // Wednesday
				new Double[] { 142.50, 141.85 }); // Thursday

	}

	public void testXFK14() throws Exception {

		testSettlements("XFK4", "/XF-20140110-week.ddf", //
				"09:35:00", // Time when settlements should be reset
				"13:30:00", // Time when settlements should be in
				new Double[] { 148.70, null }, // Friday
				new Double[] { null, 145.95 }, // Saturday (no change)
				new Double[] { null, 145.95 }, // Sunday (no change)
				new Double[] { 145.50, 145.95 }, // Monday
				new Double[] { 143.75, 145.50 }, // Tuesday
				new Double[] { 141.85, 143.75 }, // Wednesday
				new Double[] { 142.50, 141.85 }); // Thursday

	}

	public void testXFN14() throws Exception {

		testSettlements("XFN4", "/XF-20140110-week.ddf", //
				"09:35:00", //
				"14:30:00", // Later settlement window, low traded

				// Friday
				new Double[] { 149.15, null },
				// Saturday - no roll
				new Double[] { 149.15, null, 149.15 },
				// Sunday - no roll
				new Double[] { 149.15, null, 149.15 },

				// Low-traded symbol, so we never reset on open and have
				// to wait until a new snapshot rolls us later. This is
				// not *correct* behavior per se, but as correct as we will
				// get with DDF

				// Monday
				new Double[] { 148.80, // day settle
						149.15, // yesterday settle
						149.15, // posted settle after open
						null // posted previous settle at open
				},
				// Tuesday
				new Double[] { 147.00, 148.80, 148.80, 149.15 },
				// Wednesday
				new Double[] { 145.75, 147.00, 147.00, 148.80 },
				// Thursday
				new Double[] { 146.55, 145.75, 145.75, 147.00 });

	}

	public void testXFU14() throws Exception {

		testSettlements("XFU4", "/XF-20140110-week.ddf", //
				"09:35:00", // Time when settlements should be reset
				"13:30:00", // Time when settlements should be in
				new Double[] { 149.50, null }, // Friday
				new Double[] { 149.10, null, 149.10 }, // Saturday (no change)
				new Double[] { 149.10, null, 149.10 }, // Sunday (no change)
				new Double[] { 147.85, 149.10 }, // Monday
				new Double[] { 148.00, 148.90 }, // Tuesday
				new Double[] { 146.05, 147.15 }, // Wednesday
				new Double[] { 147.00, 146.05 }); // Thursday

	}

	private void testSettlements(final String symbol, final String feedlog, final String opened,
			final String closed, final Double[]... expected) throws Exception {

		int day = 10;
		final Date start = DP.parse("2014-01-10 00:00:00");

		for (final Double[] settles : expected) {

			final Date open = DP.parse("2014-01-" + day + " " + opened);
			final Date close = DP.parse("2014-01-" + day + " " + closed);
			log.info(symbol + " open: " + DF.format(open));

			runSegment(feedlog, symbol, //
					start, open, //
					settles.length >= 3 ? settles[2] : null,
					settles.length >= 4 ? settles[3] : settles[1]);

			log.info(symbol + " settle: " + DF.format(close));
			runSegment(feedlog, symbol, //
					start, close, //
					settles[0], settles[1]);

			day++;

		}

	}

	private void runSegment(final String resource, final String symbol, final Date start,
			final Date end, final Double current, final Double previous) throws Exception {

		marketplace = new BarchartMarketplaceReplay();
		marketplace.subscribe(Market.class, observer, symbol);

		FeedReplay.builder()
				.source(FeedReplay.class.getResource(resource))
				.start(start)
				.end(end)
				.listener(new FilteredListener(symbol))
				.build(marketplace.maker())
				.run();

		verifyFinalState(marketplace.snapshot(symbol), current, previous);

	}

	private void verifyFinalState(final Market market,
			final Double expectCurrent, final Double expectPrevious) {

		final Price current = market.session().settle();
		final Price previous =
				market.sessionSet().session(Session.Type.DEFAULT_PREVIOUS)
						.settle();

		if (expectCurrent == null)
			assertTrue(String.valueOf(current.asDouble()), current.isNull());
		else
			assertEquals(expectCurrent, current.asDouble(), 0.0001);

		if (expectPrevious == null)
			assertTrue(String.valueOf(previous.asDouble()), previous.isNull());
		else
			assertEquals(expectPrevious, previous.asDouble(), 0.0001);

	}

	private static class SettleObserver implements MarketObserver<Market> {

		@Override
		public void onNext(final Market m) {
		}

	}

	private static class FilteredListener implements MessageListener {

		private int lastDay = 0;
		private String filterSymbol = null;

		public FilteredListener(final String sym) {
			filterSymbol = sym;
		}

		@Override
		public void messageProcessed(final DDF_BaseMessage parsed, final byte[] raw) {

			int middle = 0;
			String symbol = null;
			String type = null;

			for (int i = 0; i < raw.length; i++) {
				if (raw[i] == FeedDDF.DDF_MIDDLE) {
					middle = i;
					break;
				}
			}

			if (middle > 0) {

				final String header = new String(raw, 1, middle - 1);
				symbol = header.substring(1, header.indexOf(','));
				type = header.substring(0, 1)
						+ header.substring(header.indexOf(','));

			}

			if (filterSymbol != null && !filterSymbol.equals(symbol))
				return;

			String label = "[" + symbol + "] ";

			if (parsed instanceof DDF_MarketBase) {

				final DDF_MarketBase mb = (DDF_MarketBase) parsed;

				if (lastDay < mb.getTradeDay().day
						&& (mb instanceof DDF_MarketSnapshot || mb instanceof DDF_MarketTrade)) {
					log.warn("New day code " + mb.getTradeDay().day
							+ ", session rolling: "
							+ parsed.getClass().getSimpleName());
					lastDay = mb.getTradeDay().day;
				}

				label = "[" + symbol + " " + lastDay + " "
						+ mb.getTime().asDateTime() + "] ";

			}

			if (parsed instanceof DDF_MarketParameter) {

				final DDF_MarketParameter mp = (DDF_MarketParameter) parsed;

				switch (mp.getParamType()) {
				case SETTLE_EARLY_PRICE:
					log.info(label + "early settle: " + mp.getAsPrice());
					break;
				case SETTLE_FINAL_PRICE:
					log.info(label + "final settle: " + mp.getAsPrice());
					break;
				default:
					break;
				}

			} else if (parsed instanceof DDF_MarketSnapshot) {

				final DDF_MarketSnapshot ms = (DDF_MarketSnapshot) parsed;

				final PriceValue settle = ms.getPriceSettle();

				if (DDF_MessageService.isClear(settle)) {
					log.info(label + "clear settle");
				} else if (DDF_MessageService.isEmpty(settle)) {
				} else {
					log.info(label + "snapshot settle: " + settle);
				}

			}

		}

	}

}
