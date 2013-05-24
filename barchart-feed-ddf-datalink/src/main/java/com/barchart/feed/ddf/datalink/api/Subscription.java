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
package com.barchart.feed.ddf.datalink.api;

import java.util.Collection;
import java.util.Set;

import com.barchart.feed.api.framework.data.InstrumentEntity;
import com.barchart.feed.api.framework.data.InstrumentField;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.ddf.datalink.enums.DDF_FeedInterest;

/**
 * Represents a subscription to a single instrument for JERQ.
 */
public class Subscription {

	private final String instrument;
	private final Collection<DDF_FeedInterest> interests;

	/**
	 * @param instrument
	 *            The instrument to subscribe or unsubscribe to
	 * @param interests
	 *            A set of DDF_FeedInterests specifying what data for JERQ to
	 *            send a client. An empty set is interpreted as a request to
	 *            unsubscribe.
	 */
	public Subscription(final String instrument,
			final Collection<DDF_FeedInterest> interests) {
		this.instrument = instrument;
		this.interests = interests;
	}

	/**
	 * A feed-base friendly constructor for convenience.
	 * 
	 * @param instrument
	 * @param events
	 */
	public Subscription(final InstrumentEntity instrument,
			final Set<MarketEvent> events) {
		this.instrument =
				instrument.get(InstrumentField.SYMBOL)
						.toString();
		this.interests = DDF_FeedInterest.fromEvents(events);
	}

	public Collection<DDF_FeedInterest> getInterests() {
		return interests;
	}
	
	public String getInstrument() {
		return instrument;
	}

	public void addInterests(final Collection<DDF_FeedInterest> insts) {
		interests.addAll(insts);
	}
	
	public void removeInterests(final Collection<DDF_FeedInterest> insts) {
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
