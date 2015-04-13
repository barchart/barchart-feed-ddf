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
import com.barchart.feed.api.model.meta.Metadata;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.sub.SubCommand;
import com.barchart.feed.base.sub.SubscriptionType;

/**
 * Represents a subscription to a single instrument for JERQ.
 */
public class DDF_Subscription implements SubCommand {

	private final String interest;
	private final Set<SubscriptionType> types;
	private final Metadata.MetaType metaType;

	/**
	 * @param instrument
	 *            The instrument to subscribe or unsubscribe to
	 * @param interests
	 *            A set of DDF_FeedInterests specifying what data for JERQ to
	 *            send a client. An empty set is interpreted as a request to
	 *            unsubscribe.
	 */
	public DDF_Subscription(
			final String instrument, 
			final Metadata.MetaType type, 
			final Set<SubscriptionType> interests) {
		
		this.interest = instrument;
		this.types = interests;
		this.metaType = type;
	}
	
	public DDF_Subscription(final SubCommand sub, final Metadata.MetaType type) {
		
		interest = sub.interest();
		types = sub.types();
		this.metaType = type;
	}

	/**
	 * A feed-base friendly constructor for convenience.
	 * 
	 * @param instrument
	 * @param events
	 */
	public DDF_Subscription(
			final Instrument instrument,
			final Set<MarketEvent> events, 
			final Metadata.MetaType type) {
		
		this.interest = instrument.symbol();
		this.types = DDF_FeedInterest.fromEvents(events);
		this.metaType = type;
	}

	@Override
	public Metadata.MetaType metaType() {
		return metaType;
	}
	
	@Override
	public Set<SubscriptionType> types() {
		return types;
	}
	
	@Override
	public String interest() {
		return interest;
	}
	
	@Override
	public String encode() {
		return interest + "=" + DDF_FeedInterest.from(types);
	}

	@Override
	public void addTypes(final Set<SubscriptionType> insts) {
		types.addAll(insts);
	}
	
	@Override
	public void removeTypes(final Set<SubscriptionType> insts) {
		types.removeAll(insts);
	}
	
	/**
	 * Returns the JERQ command to request this subscription.
	 */
	@Override
	public String toString() {
		return interest + " " + DDF_FeedInterest.from(types);
	}

	@Override
	public boolean isNull() {
		return false;
	}

}
