package com.barchart.feed.ddf.datalink.provider;

import static com.barchart.feed.api.connection.SubscriptionType.*;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.barchart.feed.api.connection.SubscriptionType;
import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.ddf.util.FeedDDF;

public final class DDF_FeedInterest {

	private static final SubscriptionType[] VALS = SubscriptionType.values();
	
	private static final Map<SubscriptionType, Character> CODES = 
			new EnumMap<SubscriptionType, Character>(SubscriptionType.class);
	
	static {
		CODES.put(UNKNOWN, '?');
		CODES.put(BOOK_SNAPSHOT, 'b');
		CODES.put(BOOK_UPDATE, 'B');
		CODES.put(CUVOL_SNAPSHOT, 'c');
		CODES.put(QUOTE_SNAPSHOT, 's');
		CODES.put(QUOTE_UPDATE, 'S');
	}
	
	public static final Set<SubscriptionType> setValues() {
		final Set<SubscriptionType> vals = new HashSet<SubscriptionType>();
		Collections.addAll(vals, VALS);
		vals.remove(SubscriptionType.UNKNOWN);
		return vals;
	}
	
	public static final int size() {
		return VALS.length;
	}
	
	private static final String NONE = "";
	
	public static final String from(final Set<MarketEvent> eventSet) {
		
		if (eventSet == null || eventSet.isEmpty()) {
			return NONE;
		}
		
		final Set<SubscriptionType> result = fromEvents(eventSet);
		
		if (result.isEmpty()) {
			return NONE;
		}
		
		final StringBuilder text = new StringBuilder(size());
		
		for(final SubscriptionType type : result) {
			text.append(CODES.get(type));
		}
		
		return text.toString();
		
	}
	
	public static final Set<SubscriptionType> fromEvents(
			final Set<MarketEvent> eventSet) {
		
		final Set<SubscriptionType> result = 
				EnumSet.noneOf(SubscriptionType.class);
		
		if(eventSet == null || eventSet.isEmpty()) {
			return result;
		}
		
		for(final MarketEvent event : eventSet) {
			switch(event) {
			
			case MARKET_UPDATED:
				result.addAll(DDF_FeedInterest.setValues());
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
	
	public static String from(final Collection<SubscriptionType> interests) {
		
		final StringBuilder sb = new StringBuilder();
		
		interests.remove(SubscriptionType.UNKNOWN);
		
		for(final SubscriptionType type : interests) {
			sb.append(CODES.get(type));
		}
		
		return sb.toString();
	}
	
	public static CharSequence command(final Instrument instrumentDDF,
			final Set<MarketEvent> eventSet) {
		
		final CharSequence symbol =
				instrumentDDF.symbol();
		
		final CharSequence interest = from(eventSet);

		final CharSequence command = FeedDDF.tcpGo(symbol, interest);
		
		return command;
		
	}
	
	
}
