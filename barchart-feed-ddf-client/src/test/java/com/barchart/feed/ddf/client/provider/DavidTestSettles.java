package com.barchart.feed.ddf.client.provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.client.provider.BarchartMarketplace;
import com.barchart.feed.client.provider.BarchartMarketplace.FeedType;

public class DavidTestSettles {
	
	protected static final Logger log = LoggerFactory.getLogger(DavidTestSettles.class);
	
	private static final DateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);

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
		
		final Map<InstrumentID, SettleTracker> settles = 
				new HashMap<InstrumentID, SettleTracker>();
		
		settles.put(new InstrumentID(1261904), new SettleTracker());
		settles.put(new InstrumentID(1025921), new SettleTracker());
		settles.put(new InstrumentID(1539537), new SettleTracker());
		
		final MarketObserver<Market> callback = new MarketObserver<Market>() {
			
			@Override
			public void onNext(final Market v) {
				
				final SettleTracker tracker = settles.get(v.instrument().id());
				
				if(tracker == null) {
					log.debug("Unexpected Instrument ID " + v.instrument().id());
					return;
				}
				
				tracker.update(v);
				
			}
		
		};
		
		final Agent myAgent = feed.newAgent(Market.class, callback);

		myAgent.include(new InstrumentID(1261904));  // GOOG NASDAQ
		myAgent.include(new InstrumentID(1025921)); // Bank of America NYSE
		myAgent.include(new InstrumentID(1539537));  // New Gold Inc AMEX
		
		Thread.sleep(24 * 60 * 60 * 1000);
		
	}
	
	private static class SettleTracker {
		
		private boolean isSettled = true;
		
		public void update(final Market m) {
			
			// Check if flag has changed
			if(isSettled == m.session().isSettled().value()) {
				return;
			}
			
			final StringBuilder sb = new StringBuilder();
			
			sb.append("Flag changed for ")
				.append(m.instrument().symbol())
				.append(" ")
				.append(timestamp.format(m.updated().asDate()))
				.append(" from ")
				.append(isSettled)
				.append(" to ")
				.append(m.session().isSettled().value());
			
			log.debug(sb.toString());
			
			// Update last seen value
			isSettled = m.session().isSettled().value();
			
		}
		
	}
	
}
