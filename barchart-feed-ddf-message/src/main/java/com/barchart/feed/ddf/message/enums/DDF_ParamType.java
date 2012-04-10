/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.enums;

import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.*;
import static com.barchart.feed.ddf.message.enums.DDF_ParamModifier.*;
import static com.barchart.feed.ddf.message.enums.DDF_ParamType.Kind.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.util.ByteConverters;
import com.barchart.util.enums.EnumByteOrdinal;
import com.barchart.util.enums.EnumCodeChar;
import com.barchart.util.math.MathExtra;

/**
 * subset of supported market parameter message qualifiers
 */
public enum DDF_ParamType implements EnumCodeChar, EnumByteOrdinal {

	/** 00 : last trade price */
	TRADE_LAST_PRICE(TRADE, LAST, PRICE), //

	/** 11 : last ask price */
	ASK_LAST_PRICE(ASK, ASK_PRICE, PRICE), //
	/** 1= : last ask size */
	ASK_LAST_SIZE(ASK, ASK_SIZE, SIZE), //

	/** 22 : last bid price */
	BID_LAST_PRICE(BID, BID_PRICE, PRICE), //
	/** 2< : last bid size */
	BID_LAST_SIZE(BID, BID_SIZE, SIZE), //

	/** 31 : close ask price */
	CLOSE_ASK_PRICE(CLOSE, ASK_PRICE, PRICE), //
	/** 32 : close bid price */
	CLOSE_BID_PRICE(CLOSE, BID_PRICE, PRICE), //

	/** 41 : close ask price */
	CLOSE_2_ASK_PRICE(CLOSE_2, ASK_PRICE, PRICE), //
	/** 42 : close bid price */
	CLOSE_2_BID_PRICE(CLOSE_2, BID_PRICE, PRICE), //

	/** 50 : last high price */
	HIGH_LAST_PRICE(HIGH, LAST, PRICE), //
	/** 52 : is high made by valid bid */
	HIGH_BID_PRICE(HIGH, BID_PRICE, PRICE), //

	/** 60 : last low price */
	LOW_LAST_PRICE(LOW, LAST, PRICE), //
	/** 61 : is low made by valid offer */
	LOW_ASK_PRICE(LOW, ASK_PRICE, PRICE), //

	/** 70 : volume */
	VOLUME_LAST_SIZE(VOLUME, LAST, SIZE), //
	/** 71 : yesterday's volume */
	VOLUME_PAST_SIZE(VOLUME, PAST, SIZE), //
	/** 76 : today's cumulative volume */
	VOLUME_THIS_SIZE(VOLUME, THIS, SIZE), //

	/** A0 : open price */
	OPEN_LAST_PRICE(OPEN, LAST, PRICE), //
	/** A1 : open ask price; */
	OPEN_ASK_PRICE(OPEN, ASK_PRICE, PRICE), //
	/** A2 : open bid price */
	OPEN_BID_PRICE(OPEN, BID_PRICE, PRICE), //

	/** B0 : open 2 price */
	OPEN_2_LAST_PRICE(OPEN_2, LAST, PRICE), //
	/** B1 : open 2 ask price; */
	OPEN_2_ASK_PRICE(OPEN_2, ASK_PRICE, PRICE), //
	/** B2 : open 2 bid price */
	OPEN_2_BID_PRICE(OPEN_2, BID_PRICE, PRICE), //

	/** C0 : open interest */
	INTEREST_LAST_SIZE(INTEREST, LAST, SIZE), //
	/** C1 : yesterday's open interest */
	INTEREST_PAST_SIZE(INTEREST, PAST, SIZE), //

	/** E0 : previous last price */
	PREVIOUS_LAST_PRICE(PREVIOUS, LAST, PRICE), //

	/** d0 : preliminary settlement */
	SETTLE_EARLY_PRICE(SETTLE_NOW, LAST, PRICE), //
	/** D0 : final settlement */
	SETTLE_FINAL_PRICE(SETTLE_END, LAST, PRICE), //

	/** SH : 52 week high price */
	YEAR_HIGH_PRICE(YEAR_BACK, YEAR_HIGH, PRICE), //
	/** SL : 52 week low price */
	YEAR_LOW_PRICE(YEAR_BACK, YEAR_LOW, PRICE), //

	//

	UNKNOWN(DDF_ParamElement.UNKNOWN, DDF_ParamModifier.UNKNOWN, NONE), //

	;

	private static final Logger log = LoggerFactory
			.getLogger(DDF_ParamType.class);

	public enum Kind {
		PRICE, SIZE, NONE
	}

	@Override
	public final byte ord() {
		return ord;
	}

	@Override
	public final char code() {
		return code;
	}

	public final byte ord;

	public final DDF_ParamElement element;
	public final DDF_ParamModifier modifier;

	/** combination of 2 element + modifier */
	public final char code;

	public final Kind kind;

	private DDF_ParamType(final DDF_ParamElement element,
			final DDF_ParamModifier modifier, final Kind kind) {

		this.ord = (byte) ordinal();

		this.element = element;
		this.modifier = modifier;

		this.code = ByteConverters.charFromBytes(element.code, modifier.code);

		this.kind = kind;

	}

	private final static DDF_ParamType[] ENUM_VALUES = values();

	@Deprecated
	public final static DDF_ParamType[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	public final static DDF_ParamType fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	public final static DDF_ParamType fromPair(final byte element,
			final byte modifier) {
		return fromCode(ByteConverters.charFromBytes(element, modifier));
	}

	public final static DDF_ParamType fromCode(final char code) {
		for (final DDF_ParamType known : ENUM_VALUES) {
			if (known.code == code) {
				return known;
			}
		}
		return UNKNOWN;
	}

	public final boolean isKnown() {
		return this != UNKNOWN;
	}

	@Override
	public String toString() {
		return String.format("%c%c   %-20s", element.code, modifier.code,
				name());
	}

	public static void main(final String... args) {

		log.debug("init");

		for (final DDF_ParamType known : ENUM_VALUES) {
			System.out.println(known);
		}

		log.debug("done");

	}

}
