/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.enums;

import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.ASK;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.BID;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.CLOSE;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.CLOSE_2;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.ETF_INFO;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.HIGH;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.INTEREST;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.LOW;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.OPEN;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.OPEN_2;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.PREVIOUS;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.SETTLE_END;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.SETTLE_NOW;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.TRADE;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.VOLUME;
import static com.barchart.feed.ddf.message.enums.DDF_ParamElement.YEAR_BACK;
import static com.barchart.feed.ddf.message.enums.DDF_ParamModifier.ASK_PRICE;
import static com.barchart.feed.ddf.message.enums.DDF_ParamModifier.ASK_SIZE;
import static com.barchart.feed.ddf.message.enums.DDF_ParamModifier.BID_PRICE;
import static com.barchart.feed.ddf.message.enums.DDF_ParamModifier.BID_SIZE;
import static com.barchart.feed.ddf.message.enums.DDF_ParamModifier.EFT_VALUE;
import static com.barchart.feed.ddf.message.enums.DDF_ParamModifier.LAST;
import static com.barchart.feed.ddf.message.enums.DDF_ParamModifier.PAST;
import static com.barchart.feed.ddf.message.enums.DDF_ParamModifier.THIS;
import static com.barchart.feed.ddf.message.enums.DDF_ParamModifier.YEAR_HIGH;
import static com.barchart.feed.ddf.message.enums.DDF_ParamModifier.YEAR_LOW;
import static com.barchart.feed.ddf.message.enums.DDF_ParamType.Kind.NONE;
import static com.barchart.feed.ddf.message.enums.DDF_ParamType.Kind.PRICE;
import static com.barchart.feed.ddf.message.enums.DDF_ParamType.Kind.SIZE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.enums.EnumByteOrdinal;
import com.barchart.feed.base.enums.EnumCodeChar;
import com.barchart.feed.ddf.util.ByteConverters;
import com.barchart.util.common.math.MathExtra;

// TODO: Auto-generated Javadoc
/**
 * subset of supported market parameter message qualifiers.
 */
public enum DDF_ParamType implements EnumCodeChar, EnumByteOrdinal {

	/** 00 : last trade price */
	TRADE_LAST_PRICE(TRADE, LAST, PRICE), //
	/** 01 : trade ask price */
	TRADE_ASK_PRICE(TRADE, ASK_PRICE, PRICE), //
	/** 02 : trade bid price */
	TRADE_BID_PRICE(TRADE, BID_PRICE, PRICE), //
	
	/** 10 : last ask */
	ASK_LAST(ASK, LAST, PRICE), //
	/** 11 : last ask price */
	ASK_LAST_PRICE(ASK, ASK_PRICE, PRICE), //
	/** 1= : last ask size */
	ASK_LAST_SIZE(ASK, ASK_SIZE, SIZE), //

	/** 20 : last bid */
	BID_LAST(BID, LAST, PRICE), //
	/** 22 : last bid price */
	BID_LAST_PRICE(BID, BID_PRICE, PRICE), //
	/** 2< : last bid size */
	BID_LAST_SIZE(BID, BID_SIZE, SIZE), //

	/** 30 : close last */
	CLOSE_LAST(CLOSE, LAST, PRICE), //
	/** 31 : close ask price */
	CLOSE_ASK_PRICE(CLOSE, ASK_PRICE, PRICE), //
	/** 32 : close bid price */
	CLOSE_BID_PRICE(CLOSE, BID_PRICE, PRICE), //

	/** 40 : close last */
	CLOSE_2_LAST(CLOSE_2, LAST, PRICE), //
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

	/** FN : ETF info value */
	ETF_INFO_VALUE(ETF_INFO, EFT_VALUE, PRICE), //
	
	/** SH : 52 week high price */
	YEAR_HIGH_PRICE(YEAR_BACK, YEAR_HIGH, PRICE), //
	/** SL : 52 week low price */
	YEAR_LOW_PRICE(YEAR_BACK, YEAR_LOW, PRICE), //

	//

	UNKNOWN(DDF_ParamElement.UNKNOWN, DDF_ParamModifier.UNKNOWN, NONE), //

	;

	private static final Logger log = LoggerFactory
			.getLogger(DDF_ParamType.class);

	/**
	 * The Enum Kind.
	 */
	public enum Kind {
		PRICE, SIZE, NONE
	}

	/* (non-Javadoc)
	 * @see com.barchart.util.enums.EnumByteOrdinal#ord()
	 */
	@Override
	public final byte ord() {
		return ord;
	}

	/* (non-Javadoc)
	 * @see com.barchart.util.enums.EnumCodeChar#code()
	 */
	@Override
	public final char code() {
		return code;
	}

	/** The ord. */
	public final byte ord;

	/** The element. */
	public final DDF_ParamElement element;
	
	/** The modifier. */
	public final DDF_ParamModifier modifier;

	/** combination of 2 element + modifier. */
	public final char code;

	/** The kind. */
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

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ param type[]
	 */
	@Deprecated
	public final static DDF_ParamType[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	/**
	 * From ord.
	 *
	 * @param ord the ord
	 * @return the dD f_ param type
	 */
	public final static DDF_ParamType fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	/**
	 * From pair.
	 *
	 * @param element the element
	 * @param modifier the modifier
	 * @return the dD f_ param type
	 */
	public final static DDF_ParamType fromPair(final byte element,
			final byte modifier) {
		return fromCode(ByteConverters.charFromBytes(element, modifier));
	}

	/**
	 * From code.
	 *
	 * @param code the code
	 * @return the dD f_ param type
	 */
	public final static DDF_ParamType fromCode(final char code) {
		for (final DDF_ParamType known : ENUM_VALUES) {
			if (known.code == code) {
				return known;
			}
		}
		log.debug("UNKNOWN PARAM CODE = " + code);
		return UNKNOWN;
	}

	/**
	 * Checks if is known.
	 *
	 * @return true, if is known
	 */
	public final boolean isKnown() {
		return this != UNKNOWN;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return String.format("%c%c   %-20s", element.code, modifier.code,
				name());
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String... args) {

		log.debug("init");

		for (final DDF_ParamType known : ENUM_VALUES) {
			System.out.println(known);
		}

		log.debug("done");

	}

}
