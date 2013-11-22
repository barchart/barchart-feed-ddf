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

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.sub.Sub;
import com.barchart.feed.base.sub.SubscriptionType;
import com.barchart.feed.base.sub.Sub.Type;

/**
 * Represents a subscription to a single instrument for JERQ.
 */
public class DDF_Subscription implements Sub {

	private final String interest;
	private final Set<SubscriptionType> interests;
	private final Type type;

	/**
	 * @param instrument
	 *            The instrument to subscribe or unsubscribe to
	 * @param interests
	 *            A set of DDF_FeedInterests specifying what data for JERQ to
	 *            send a client. An empty set is interpreted as a request to
	 *            unsubscribe.
	 */
	public DDF_Subscription(final String instrument, Type type, 
			final Set<SubscriptionType> interests) {
		this.interest = instrument;
		this.interests = interests;
		this.type = type;
	}
	
	public DDF_Subscription(final Sub sub, Type type) {
		interest = sub.interest();
		interests = sub.types();
		this.type = type;
	}

	/**
	 * A feed-base friendly constructor for convenience.
	 * 
	 * @param instrument
	 * @param events
	 */
	public DDF_Subscription(final Instrument instrument,
			final Set<MarketEvent> events, Type type) {
		this.interest =
				instrument.symbol();
		this.interests = DDF_FeedInterest.fromEvents(events);
		this.type = type;
	}

	@Override
	public Type type() {
		return type;
	}
	
	@Override
	public Set<SubscriptionType> types() {
		return interests;
	}
	
	@Override
	public String interest() {
		return interest;
	}
	
	@Override
	public String encode() {
		return interest + "=" + DDF_FeedInterest.from(interests);
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
	 * Returns the JERQ command to request this subscription.
	 */
	@Override
	public String toString() {
		return interest + " " + DDF_FeedInterest.from(interests);
	}

	@Override
	public boolean isNull() {
		return false;
	}

}
