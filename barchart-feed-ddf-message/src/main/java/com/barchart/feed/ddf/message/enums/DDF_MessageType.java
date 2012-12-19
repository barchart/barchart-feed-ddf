/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.enums;

import static com.barchart.util.ascii.ASCII.NUL;
import static com.barchart.util.ascii.ASCII._0_;
import static com.barchart.util.ascii.ASCII._1_;
import static com.barchart.util.ascii.ASCII._2_;
import static com.barchart.util.ascii.ASCII._3_;
import static com.barchart.util.ascii.ASCII._4_;
import static com.barchart.util.ascii.ASCII._5_;
import static com.barchart.util.ascii.ASCII._6_;
import static com.barchart.util.ascii.ASCII._7_;
import static com.barchart.util.ascii.ASCII._8_;
import static com.barchart.util.ascii.ASCII._B_;
import static com.barchart.util.ascii.ASCII._C_;
import static com.barchart.util.ascii.ASCII._I_;
import static com.barchart.util.ascii.ASCII._S_;
import static com.barchart.util.ascii.ASCII._T_;
import static com.barchart.util.ascii.ASCII._Z_;

import com.barchart.feed.ddf.message.api.DDF_ControlResponse;
import com.barchart.feed.ddf.message.api.DDF_ControlTimestamp;
import com.barchart.feed.ddf.message.api.DDF_EOD_Commodity;
import com.barchart.feed.ddf.message.api.DDF_EOD_EquityForex;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.api.DDF_MarketBook;
import com.barchart.feed.ddf.message.api.DDF_MarketBookTop;
import com.barchart.feed.ddf.message.api.DDF_MarketCuvol;
import com.barchart.feed.ddf.message.api.DDF_MarketParameter;
import com.barchart.feed.ddf.message.api.DDF_MarketQuote;
import com.barchart.feed.ddf.message.api.DDF_MarketSession;
import com.barchart.feed.ddf.message.api.DDF_MarketSnapshot;
import com.barchart.feed.ddf.message.api.DDF_MarketTrade;
import com.barchart.feed.ddf.message.api.DDF_Prior_IndividCmdy;
import com.barchart.feed.ddf.message.api.DDF_Prior_TotCmdy;
import com.barchart.feed.ddf.util.ByteConverters;
import com.barchart.feed.ddf.util.FeedDDF;
import com.barchart.util.enums.EnumByteOrdinal;
import com.barchart.util.enums.EnumCodeChar;
import com.barchart.util.math.MathExtra;

// TODO: Auto-generated Javadoc
/**
 * subset of supported ddf messages.
 */
public enum DDF_MessageType implements EnumCodeChar, EnumByteOrdinal {

	/* ### CTRL messages ### */

	/** ddf message time stamp; once a second; CST time zone */
	TIME_STAMP(FeedDDF.DDF_TIMESTAMP, NUL, DDF_ControlTimestamp.class), //

	/** server accept response */
	TCP_ACCEPT(FeedDDF.TCP_ACCEPT, NUL, DDF_ControlResponse.class), //

	/** server reject response */
	TCP_REJECT(FeedDDF.TCP_REJECT, NUL, DDF_ControlResponse.class), //

	/** server session command */
	TCP_COMMAND(FeedDDF.TCP_COMMAND, NUL, DDF_ControlResponse.class), //

	/** server welcome response */
	TCP_WELCOME(FeedDDF.TCP_WELCOME, NUL, DDF_ControlResponse.class), //

	/* ### DATA messages ### */

	/** 20: */
	PARAM(_2_, _0_, DDF_MarketParameter.class), //

	/** 21: exchange generated, live foreground refresh */
	SNAP_FORE_EXCH(_2_, _1_, DDF_MarketSnapshot.class), //

	/** 22: ddfplus generated, live foreground refresh */
	SNAP_FORE_PLUS(_2_, _2_, DDF_MarketSnapshot.class), //

	/** 23: ddfplus generated, background refresh */
	SNAP_BACK_PLUS_CURR(_2_, _3_, DDF_MarketSnapshot.class), //

	/** 24: ddfplus generated, background refresh for previous session */
	SNAP_BACK_PLUS_PREV(_2_, _4_, DDF_MarketSnapshot.class), //

	/** 25: TODO exchange generated, insert message last price */
	DDF_25(_2_, _5_, DDF_MarketParameter.class), //

	/** 26: live foreground quote message. */
	SNAP_FORE_PLUS_QUOTE(_2_, _6_, DDF_MarketSnapshot.class), //

	/** 27: live trade messages */
	TRADE(_2_, _7_, DDF_MarketTrade.class), //

	/**
	 * 2Z: Sub-Record Z is the same as Sub-Record 7 but should not to be
	 * processed as a price record. Sub-Record Z is used to pass down volume on
	 * sale conditions (session) not intended to update the high, low or last
	 * values
	 */
	TRADE_VOL(_2_, _Z_, DDF_MarketTrade.class), //

	/** 28: top of book (bid price & size, ask price & size) messages */
	BOOK_TOP(_2_, _8_, DDF_MarketBookTop.class), //

	/** 3B: book snapshot */
	BOOK_SNAP(_3_, _B_, DDF_MarketBook.class), //

	/** 3S: end-of-day stock and forex prices and volume */
	EOD_EQTY_FORE(_3_, _S_, DDF_EOD_EquityForex.class), //

	/** 3C: end-of-day commodity prices */
	EOD_CMDY(_3_, _C_, DDF_EOD_Commodity.class), //

	/** prior day's commodity individual vol & open int. */
	PRIOR_INDIV_CMDY(_3_, _I_, DDF_Prior_IndividCmdy.class), //

	/** prior day's total vol & open int for a commodity group */
	PRIOR_TOTAL_CMDY(_3_, _T_, DDF_Prior_TotCmdy.class), //

	/** XB: book snapshot */
	BOOK_SNAP_XML(FeedDDF.XML_RECORD, FeedDDF.XML_SUB_BOOK,
			DDF_MarketBook.class), //

	/** XC: cuvol snapshot */
	CUVOL_SNAP_XML(FeedDDF.XML_RECORD, FeedDDF.XML_SUB_CUVOL,
			DDF_MarketCuvol.class), //

	/** XQ: quote snapshot */
	QUOTE_SNAP_XML(FeedDDF.XML_RECORD, FeedDDF.XML_SUB_QUOTE,
			DDF_MarketQuote.class), //

	/** XS: quote snapshot; xml session component */
	SESSION_SNAP_XML(FeedDDF.XML_RECORD, FeedDDF.XML_SUB_SESSION,
			DDF_MarketSession.class), //

	/* ### unknown ### */

	/** place holder for unsupported message types */
	UNKNOWN(_0_, _0_, Void.class), //

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.util.enums.EnumByteOrdinal#ord()
	 */
	;

	@Override
	public final byte ord() {
		return ord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.util.enums.EnumCodeChar#code()
	 */
	@Override
	public final char code() {
		return code;
	}

	/** ordinal of this enum. */
	public final byte ord;

	/** ddf classifier : "message record". */
	public final byte record;

	/** ddf classifier : "message sub-record". */
	public final byte subRecord;

	/**
	 * ddf classifier : "code = record (high byte) & sub-record (low byte)"
	 * packed in a char letter.
	 */
	public final char code;

	/** message interface used for this message type. */
	public Class<?> klaz;

	/** The is control timestamp. */
	public final boolean isControlTimestamp;

	/** The is control response. */
	public final boolean isControlResponse;

	/** The is market message. */
	public final boolean isMarketMessage;

	/** A market message not specific to an instrument */
	public final boolean isNonInstrumentMarketMessage;

	private DDF_MessageType(final int record, final int subRecord,
			final Class<?> klaz) {

		this.record = (byte) record;
		this.subRecord = (byte) subRecord;

		this.ord = (byte) ordinal();
		this.code = ByteConverters.charFromBytes(this.record, this.subRecord);
		this.klaz = klaz;

		this.isControlTimestamp = isControlTimestamp();
		this.isControlResponse = isControlResponse();
		this.isMarketMessage = isMarketMessage();
		this.isNonInstrumentMarketMessage = isNonInstrumentmarketMessage();
	}

	private final static DDF_MessageType[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 * 
	 * @return the dD f_ message type[]
	 */
	@Deprecated
	public final static DDF_MessageType[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	/**
	 * From ord.
	 * 
	 * @param ord
	 *            the ord
	 * @return the dD f_ message type
	 */
	public final static DDF_MessageType fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	/**
	 * From pair.
	 * 
	 * @param record
	 *            the record
	 * @param subRecord
	 *            the sub record
	 * @return the dD f_ message type
	 */
	public final static DDF_MessageType fromPair(final byte record,
			final byte subRecord) {
		switch (record) {
		case _2_:
			switch (subRecord) {
			case _0_:
				return PARAM;
			case _1_:
				return SNAP_FORE_EXCH;
			case _2_:
				return SNAP_FORE_PLUS;
			case _3_:
				return SNAP_BACK_PLUS_CURR;
			case _4_:
				return SNAP_BACK_PLUS_PREV;
			case _5_:
				return DDF_25;
			case _6_:
				return SNAP_FORE_PLUS_QUOTE;
			case _7_:
				return TRADE;
			case _Z_:
				return TRADE_VOL;
			case _8_:
				return BOOK_TOP;
			default:
				return UNKNOWN;
			}
		case _3_:
			switch (subRecord) {
			case _B_:
				return BOOK_SNAP;
			case _S_:
				return EOD_EQTY_FORE;
			case _C_:
				return EOD_CMDY;
			case _I_:
				return PRIOR_INDIV_CMDY;
			case _T_:
				return PRIOR_TOTAL_CMDY;
			default:
				return UNKNOWN;
			}
		case FeedDDF.DDF_TIMESTAMP:
			switch (subRecord) {
			case NUL:
				return TIME_STAMP;
			default:
				return UNKNOWN;
			}
		case FeedDDF.XML_RECORD:
			switch (subRecord) {
			case FeedDDF.XML_SUB_BOOK:
				return BOOK_SNAP_XML;
			case FeedDDF.XML_SUB_CUVOL:
				return CUVOL_SNAP_XML;
			case FeedDDF.XML_SUB_QUOTE:
				return QUOTE_SNAP_XML;
			case FeedDDF.XML_SUB_SESSION:
				return SESSION_SNAP_XML;
			default:
				return UNKNOWN;
			}
		case FeedDDF.TCP_ACCEPT:
			switch (subRecord) {
			case NUL:
				return TCP_ACCEPT;
			default:
				return UNKNOWN;
			}
		case FeedDDF.TCP_REJECT:
			switch (subRecord) {
			case NUL:
				return TCP_REJECT;
			default:
				return UNKNOWN;
			}
		case FeedDDF.TCP_COMMAND:
			switch (subRecord) {
			case NUL:
				return TCP_COMMAND;
			default:
				return UNKNOWN;
			}
		case FeedDDF.TCP_WELCOME:
			switch (subRecord) {
			case NUL:
				return TCP_WELCOME;
			default:
				return UNKNOWN;
			}
		default:
			return UNKNOWN;
		}
	}

	/**
	 * From code.
	 * 
	 * @param code
	 *            the code
	 * @return the dD f_ message type
	 */
	public final static DDF_MessageType fromCode(final char code) {
		return fromPair((byte) (code >>> 8), (byte) code);
	}

	@SuppressWarnings("unused")
	private final boolean isMarketSnapshot() {
		switch (record) {
		case _2_:
			switch (subRecord) {
			case _1_:
				return true;
			case _2_:
				return true;
			case _3_:
				return true;
			case _4_:
				return true;
			case _5_:
				return false; // keep
			case _6_:
				return true;
			default:
				return false;
			}
		default:
			return false;
		}
	}

	/**
	 * Checks if is known.
	 * 
	 * @return true, if is known
	 */
	public final boolean isKnown() {
		return this != UNKNOWN;
	}

	private boolean isControlResponse() {
		return DDF_ControlResponse.class.isAssignableFrom(klaz);
	}

	private boolean isMarketMessage() {
		return DDF_MarketBase.class.isAssignableFrom(klaz);
	}

	private boolean isControlTimestamp() {
		return DDF_ControlTimestamp.class.isAssignableFrom(klaz);
	}

	private boolean isNonInstrumentmarketMessage() {
		return this == PRIOR_TOTAL_CMDY;
	}

}
