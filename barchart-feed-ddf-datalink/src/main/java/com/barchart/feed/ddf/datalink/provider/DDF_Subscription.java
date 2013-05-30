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

import java.util.Collection;
import java.util.Set;

import com.barchart.feed.api.consumer.connection.SubscriptionType;
import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.inst.InstrumentField;

/**
 * Represents a subscription to a single instrument for JERQ.
 */
public class DDF_Subscription {

	private final String instrument;
	private final Collection<SubscriptionType> interests;

	/**
	 * @param instrument
	 *            The instrument to subscribe or unsubscribe to
	 * @param interests
	 *            A set of DDF_FeedInterests specifying what data for JERQ to
	 *            send a client. An empty set is interpreted as a request to
	 *            unsubscribe.
	 */
	public DDF_Subscription(final String instrument,
			final Collection<SubscriptionType> interests) {
		this.instrument = instrument;
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
		this.instrument =
				instrument.get(InstrumentField.SYMBOL)
						.toString();
		this.interests = DDF_FeedInterest.fromEvents(events);
	}

	public Collection<SubscriptionType> getInterests() {
		return interests;
	}
	
	public String getInstrument() {
		return instrument;
	}

	public void addInterests(final Collection<SubscriptionType> insts) {
		interests.addAll(insts);
	}
	
	public void removeInterests(final Collection<SubscriptionType> insts) {
		interests.removeAll(insts);
	}
	
	/**
	 * Helper method returning the JERQ command to unsubscribe this
	 * subscription. Note: the "STOP " header is omitted for chaining requests.
	 * 
	 * @return The JERQ command to unsubscribe this subscription.
	 */
	public String unsubscribe() {
		return instrument + "=" + DDF_FeedInterest.from(interests);
	}

	/**
	 * Helper method returning the JERQ command to subscribe this subscription.
	 * Note: the "GO " header is omitted for chaining requests.
	 * 
	 * @return The JERQ command to subscribe this subscription.
	 */
	public String subscribe() {
		return instrument + "=" + DDF_FeedInterest.from(interests);
	}

	/**
	 * Returns the JERQ command to request this subscription.
	 */
	@Override
	public String toString() {
		return instrument + " " + DDF_FeedInterest.from(interests);
	}

}
