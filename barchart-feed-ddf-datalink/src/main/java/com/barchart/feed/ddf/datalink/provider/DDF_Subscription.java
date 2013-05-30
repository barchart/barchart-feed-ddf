/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/**
 * 
 */
package com.barchart.feed.ddf.datalink.provider;

import java.util.Set;

import com.barchart.feed.api.consumer.connection.Subscription;
import com.barchart.feed.api.consumer.connection.SubscriptionType;
import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.inst.InstrumentField;

/**
 * Represents a subscription to a single instrument for JERQ.
 */
public class DDF_Subscription implements Subscription<Instrument> {

	private final Instrument inst;
	private final String symbol;
	private final Set<SubscriptionType> interests;

	/**
	 * @param instrument
	 *            The instrument to subscribe or unsubscribe to
	 * @param interests
	 *            A set of DDF_FeedInterests specifying what data for JERQ to
	 *            send a client. An empty set is interpreted as a request to
	 *            unsubscribe.
	 */
	public DDF_Subscription(final String instrument,
			final Set<SubscriptionType> interests) {
		this.symbol = instrument;
		inst = null;
		this.interests = interests;
	}

	/**
	 * A feed-base friendly constructor for convenience.
	 * 
	 * @param instrument
	 * @param events
	 */
	public DDF_Subscription(final Instrument instrument,
			final Set<MarketEvent> events) {
		inst = instrument;
		this.symbol =
				instrument.get(InstrumentField.SYMBOL)
						.toString();
		this.interests = DDF_FeedInterest.fromEvents(events);
	}

	@Override
	public Set<SubscriptionType> types() {
		return interests;
	}
	
	@Override
	public Instrument interest() {
		return inst;
	}
	
	@Override
	public String interestName() {
		return symbol;
	}

	@Override
	public void addTypes(final Set<SubscriptionType> insts) {
		interests.addAll(insts);
	}
	
	@Override
	public void removeTypes(final Set<SubscriptionType> insts) {
		interests.removeAll(insts);
	}
	
	/**
	 * Helper method returning the JERQ command to unsubscribe this
	 * subscription. Note: the "STOP " header is omitted for chaining requests.
	 * 
	 * @return The JERQ command to unsubscribe this subscription.
	 */
	@Override
	public String unsubscribe() {
		return symbol + "=" + DDF_FeedInterest.from(interests);
	}

	/**
	 * Helper method returning the JERQ command to subscribe this subscription.
	 * Note: the "GO " header is omitted for chaining requests.
	 * 
	 * @return The JERQ command to subscribe this subscription.
	 */
	@Override
	public String subscribe() {
		return symbol + "=" + DDF_FeedInterest.from(interests);
	}

	/**
	 * Returns the JERQ command to request this subscription.
	 */
	@Override
	public String toString() {
		return symbol + " " + DDF_FeedInterest.from(interests);
	}

}
