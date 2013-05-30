/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.enums;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.ddf.util.FeedDDF;
import com.barchart.feed.inst.InstrumentField;

/**
 * Flags for ddf JERQ "go" command to activate specific information channels in
 * the feed.
 */
enum DDF_FeedInterest {

	UNKNOWN('?'), //

	/** A one-time snapshot of a symbol's full book */
	BOOK_SNAPSHOT('b'), //
	/** A stream of the symbol's top of book */
	BOOK_UPDATE('B'), // only book top

	//
	/** A one-time cumulative volume snapshot */
	CUVOL_SNAPSHOT('c'), //

	//
	/** A one-time snapshot quote for a symbol */
	QUOTE_SNAPSHOT('s'), //
	/** A stream of quote updates for a symbol */
	QUOTE_UPDATE('S'), //

	;

	private static final DDF_FeedInterest[] ENUM_VALS = values();

	public static final Set<DDF_FeedInterest> setValues() {
		final Set<DDF_FeedInterest> vals = new HashSet<DDF_FeedInterest>();
		for (final DDF_FeedInterest i : values()) {
			vals.add(i);
		}
		vals.remove(DDF_FeedInterest.UNKNOWN);
		return vals;
	}

	public static final int size() {
		return ENUM_VALS.length;
	}

	/** The code. */
	public final char code;

	private DDF_FeedInterest(final char code) {
		this.code = code;
	}

	public static final DDF_FeedInterest of(final char code) {
		for (final DDF_FeedInterest known : values()) {
			if (known.code == code) {
				return known;
			}
		}
		return UNKNOWN;
	}

	private static final String NONE = "";

	/**
	 * Parses a set of MarketEvents to a String of FeedInterest names.
	 * 
	 * @param eventSet
	 *            The set of events to parse.
	 * @return a String of FeedInterest names.
	 */
	public static final String from(final Set<MarketEvent> eventSet) {

		if (eventSet == null || eventSet.isEmpty()) {
			return NONE;
		}

		final Set<DDF_FeedInterest> result = fromEvents(eventSet);

/*		for (final MarketEvent event : eventSet) {
			switch (event) {

			case MARKET_UPDATED:
				result.addAll(DDF_FeedInterest.setValues());
				break;
				
			case NEW_BOOK_ERROR:
				// debug use only
				continue;

			case NEW_BOOK_SNAPSHOT:
				result.add(BOOK_SNAPSHOT);
				continue;

			case NEW_BOOK_UPDATE:
			case NEW_BOOK_TOP:
				result.add(BOOK_UPDATE);
				continue;

			case NEW_CUVOL_SNAPSHOT:
				result.add(CUVOL_SNAPSHOT);
				continue;

			case NEW_BAR_CURRENT_NET:
			case NEW_BAR_CURRENT_PIT:
			case NEW_BAR_CURRENT_EXT:
			case NEW_BAR_CURRENT:
			case NEW_BAR_PREVIOUS:
				result.add(QUOTE_SNAPSHOT);
				continue;
			
			default:
				result.add(QUOTE_UPDATE);
				continue;
			}
		}
*/
		if (result.isEmpty()) {
			return NONE;
		}

		final StringBuilder text = new StringBuilder(size());

		for (final DDF_FeedInterest interest : result) {
			text.append(interest.code);
		}

		return text.toString();

	}

	/**
	 * Parses a set of MarketEvents to a Set of FeedInterest enums.
	 * 
	 * @param eventSet
	 *            The set of events to parse.
	 * @return a Set of FeedInterest enums.
	 */
	public static final Set<DDF_FeedInterest> fromEvents(
			final Set<MarketEvent> eventSet) {

		final Set<DDF_FeedInterest> result =
				EnumSet.noneOf(DDF_FeedInterest.class);

		if (eventSet == null || eventSet.isEmpty()) {
			return result;
		}

		for (final MarketEvent event : eventSet) {
			switch (event) {
			
			case MARKET_UPDATED:
				result.addAll(DDF_FeedInterest.setValues());
				break;
				
			//MARKET_STATUS_* not supported by ddf
				
			case NEW_TRADE:
				result.add(QUOTE_UPDATE);
				continue;
				
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
				continue;
				
			case NEW_BOOK_ERROR:
				// debug use only
				continue;

			case NEW_BOOK_SNAPSHOT:
				result.add(BOOK_SNAPSHOT);
				continue;

			case NEW_BOOK_UPDATE:
			case NEW_BOOK_TOP:
				result.add(BOOK_UPDATE);
				result.add(BOOK_SNAPSHOT);
				continue;

			//NEW_CUVOL_UPDATE not supported by ddf
				
			case NEW_CUVOL_SNAPSHOT:
				result.add(CUVOL_SNAPSHOT);
				continue;

			default:
				result.add(QUOTE_UPDATE);
				continue;
			}
		}

		return result;

	}

	public static String from(final Collection<DDF_FeedInterest> interests) {
		final StringBuilder sb = new StringBuilder();
		for (final DDF_FeedInterest i : interests) {
			sb.append(i.code);
		}
		return sb.toString();
	}

	/**
	 * Command.
	 * 
	 * @param instrumentDDF
	 *            the instrument ddf
	 * @param eventSet
	 *            the event set
	 * @return the char sequence
	 */
	public static CharSequence command(final Instrument instrumentDDF,
			final Set<MarketEvent> eventSet) {

		final CharSequence symbol =
				instrumentDDF.get(InstrumentField.SYMBOL);

		final CharSequence interest = DDF_FeedInterest.from(eventSet);

		final CharSequence command = FeedDDF.tcpGo(symbol, interest);

		return command;

	}

}
