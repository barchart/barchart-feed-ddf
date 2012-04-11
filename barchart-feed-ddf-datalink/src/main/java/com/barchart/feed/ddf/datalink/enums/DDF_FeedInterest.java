/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.enums;

import java.util.EnumSet;
import java.util.Set;

import com.barchart.feed.base.api.market.enums.MarketEvent;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField;
import com.barchart.feed.ddf.util.FeedDDF;

// TODO: Auto-generated Javadoc
/**
 * flags for ddf jerq "go" command to activate specific information channels in
 * the feed.
 */
public enum DDF_FeedInterest {

	UNKNOWN('?'), //

	//

	BOOK_SNAPSHOT('b'), //
	BOOK_UPDATE('B'), // only book top

	//

	CUVOL_SNAPSHOT('c'), //

	//

	QUOTE_SNAPSHOT('s'), //
	QUOTE_UPDATE('S'), //

	;

	private static final DDF_FeedInterest[] ENUM_VALS = values();

	/**
	 * Size.
	 * 
	 * @return the int
	 */
	public static final int size() {
		return ENUM_VALS.length;
	}

	/**
	 * Values unsafe.
	 * 
	 * @return the dD f_ feed interest[]
	 */
	@Deprecated
	public static final DDF_FeedInterest[] valuesUnsafe() {
		return ENUM_VALS;
	}

	/** The code. */
	public final char code;

	private DDF_FeedInterest(final char code) {
		this.code = code;
	}

	/**
	 * Of.
	 * 
	 * @param code
	 *            the code
	 * @return the dD f_ feed interest
	 */
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
	 * From.
	 * 
	 * @param eventSet
	 *            the event set
	 * @return the string
	 */
	public static final String from(final Set<MarketEvent> eventSet) {

		if (eventSet == null || eventSet.isEmpty()) {
			return NONE;
		}

		final Set<DDF_FeedInterest> result = EnumSet
				.noneOf(DDF_FeedInterest.class);

		for (final MarketEvent event : eventSet) {
			switch (event) {

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
	 * Command.
	 * 
	 * @param instrumentDDF
	 *            the instrument ddf
	 * @param eventSet
	 *            the event set
	 * @return the char sequence
	 */
	public static CharSequence command(final DDF_Instrument instrumentDDF,
			final Set<MarketEvent> eventSet) {

		final CharSequence symbol = instrumentDDF
				.get(DDF_InstrumentField.DDF_SYMBOL_REALTIME);

		final CharSequence interest = DDF_FeedInterest.from(eventSet);

		final CharSequence command = FeedDDF.tcpGo(symbol, interest);

		return command;

	}

}
