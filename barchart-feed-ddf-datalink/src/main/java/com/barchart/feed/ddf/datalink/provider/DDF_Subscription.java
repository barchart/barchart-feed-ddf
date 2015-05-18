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

import static com.barchart.feed.base.sub.SubscriptionType.BOOK_SNAPSHOT;
import static com.barchart.feed.base.sub.SubscriptionType.BOOK_UPDATE;
import static com.barchart.feed.base.sub.SubscriptionType.CUVOL_SNAPSHOT;
import static com.barchart.feed.base.sub.SubscriptionType.QUOTE_SNAPSHOT;
import static com.barchart.feed.base.sub.SubscriptionType.QUOTE_UPDATE;

import java.util.EnumSet;
import java.util.Set;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.Metadata;
import com.barchart.feed.api.model.meta.id.MetadataID;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.sub.SubCommand;
import com.barchart.feed.base.sub.SubscriptionType;

/**
 * Represents a subscription to a single instrument for JERQ.
 */
public class DDF_Subscription implements SubCommand {

	private final MetadataID<?> id;
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
			final MetadataID<?> id, 
			final Metadata.MetaType type, 
			final Set<SubscriptionType> interests) {
		
		this.id = id;
		this.types = interests;
		this.metaType = type;
	}
	
	public DDF_Subscription(final SubCommand sub, final Metadata.MetaType type) {
		
		id = sub.interestID();
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
		
		id = instrument.id();
		this.types = fromEvents(events);
		this.metaType = type;
	}

	@Override
	public MetadataID<?> interestID() {
		return id;
	}

	@Override
	public String typeString() {
		
		final StringBuilder sb = new StringBuilder();
		
		for(final SubscriptionType t : types) {
			sb.append(t.code());
		}
		
		return sb.toString();
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
	public void addTypes(final Set<SubscriptionType> insts) {
		types.addAll(insts);
	}
	
	@Override
	public void removeTypes(final Set<SubscriptionType> insts) {
		types.removeAll(insts);
	}
	
	@Override
	public boolean isNull() {
		return false;
	}
	
	private static final String NONE = "";

	public final String from(final Set<MarketEvent> eventSet) {
		
		if (eventSet == null || eventSet.isEmpty()) {
			return NONE;
		}
		
		final Set<SubscriptionType> result = fromEvents(eventSet);
		
		if (result.isEmpty()) {
			return NONE;
		}
		
		final StringBuilder text = new StringBuilder();
		
		for(final SubscriptionType type : result) {
			text.append(type.code());
		}
		
		return text.toString();
		
	}
	
	private Set<SubscriptionType> fromEvents(final Set<MarketEvent> eventSet) {
		
		final Set<SubscriptionType> result = 
				EnumSet.noneOf(SubscriptionType.class);
		
		if(eventSet == null || eventSet.isEmpty()) {
			return result;
		}
		
		for(final MarketEvent event : eventSet) {
			switch(event) {
			
			case MARKET_UPDATED:
				result.add(QUOTE_SNAPSHOT);
				result.add(QUOTE_UPDATE);
				result.add(BOOK_UPDATE);
				result.add(BOOK_SNAPSHOT);
				result.add(CUVOL_SNAPSHOT);
				break;
			case NEW_TRADE:
				result.add(QUOTE_UPDATE);
				break;
				
			case NEW_BAR_CURRENT:
			case NEW_BAR_PREVIOUS:
			case NEW_OPEN:
			case NEW_HIGH:
			case NEW_LOW:
			case NEW_CLOSE:
			case NEW_SETTLE:
			case NEW_VOLUME:
			case NEW_INTEREST:
				result.add(QUOTE_SNAPSHOT);
				result.add(QUOTE_UPDATE);
				break;
				
			case NEW_BOOK_ERROR:
				// debug use only
				break;

			case NEW_BOOK_SNAPSHOT:
				result.add(BOOK_SNAPSHOT);
				break;

			case NEW_BOOK_UPDATE:
			case NEW_BOOK_TOP:
				result.add(BOOK_UPDATE);
				result.add(BOOK_SNAPSHOT);
				break;

			//NEW_CUVOL_UPDATE not supported by ddf
				
			case NEW_CUVOL_SNAPSHOT:
				result.add(CUVOL_SNAPSHOT);
				break;

			default:
				result.add(QUOTE_UPDATE);
				break;
			}
		}
		
		return result;
	}
	
}
