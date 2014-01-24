/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.enums;

import static com.barchart.feed.base.trade.enums.MarketTradeSequencing.NORMAL;
import static com.barchart.feed.base.trade.enums.MarketTradeSession.DEFAULT;
import static com.barchart.feed.base.trade.enums.MarketTradeType.ACQUISITION;
import static com.barchart.feed.base.trade.enums.MarketTradeType.AMEX_RULE_155;
import static com.barchart.feed.base.trade.enums.MarketTradeType.AUTOMATIC_EXECUTION;
import static com.barchart.feed.base.trade.enums.MarketTradeType.BUNCHED_SOLD;
import static com.barchart.feed.base.trade.enums.MarketTradeType.BUNCHED_TRADE;
import static com.barchart.feed.base.trade.enums.MarketTradeType.CROSS_TRADE;
import static com.barchart.feed.base.trade.enums.MarketTradeType.DERIVATIVELY_PRICED;
import static com.barchart.feed.base.trade.enums.MarketTradeType.DISTRIBUTION;
import static com.barchart.feed.base.trade.enums.MarketTradeType.FORM_T;
import static com.barchart.feed.base.trade.enums.MarketTradeType.FORM_T_OOO;
import static com.barchart.feed.base.trade.enums.MarketTradeType.FUTURE_COMPOSITE;
import static com.barchart.feed.base.trade.enums.MarketTradeType.FUTURE_ELECTRONIC;
import static com.barchart.feed.base.trade.enums.MarketTradeType.FUTURE_PIT;
import static com.barchart.feed.base.trade.enums.MarketTradeType.INTERMARKET_SWEEP;
import static com.barchart.feed.base.trade.enums.MarketTradeType.MARKET_CLOSING;
import static com.barchart.feed.base.trade.enums.MarketTradeType.MARKET_OPENING;
import static com.barchart.feed.base.trade.enums.MarketTradeType.MARKET_REOPENING;
import static com.barchart.feed.base.trade.enums.MarketTradeType.NYSE_RULE_127;
import static com.barchart.feed.base.trade.enums.MarketTradeType.ODD_LOT;
import static com.barchart.feed.base.trade.enums.MarketTradeType.PRIOR_REFERENCE_PRICE;
import static com.barchart.feed.base.trade.enums.MarketTradeType.REGULAR_SALE;
import static com.barchart.feed.base.trade.enums.MarketTradeType.RESERVED;
import static com.barchart.feed.base.trade.enums.MarketTradeType.SOLD_LAST;
import static com.barchart.feed.base.trade.enums.MarketTradeType.SOLD_OOO;
import static com.barchart.feed.base.trade.enums.MarketTradeType.SPLIT;
import static com.barchart.feed.base.trade.enums.MarketTradeType.STOCK_OPTION;
import static com.barchart.feed.base.trade.enums.MarketTradeType.STOPPED_STOCK_OOO;
import static com.barchart.feed.base.trade.enums.MarketTradeType.STOPPED_STOCK_REGULAR;
import static com.barchart.feed.base.trade.enums.MarketTradeType.STOPPED_STOCK_SOLD_LAST;
import static com.barchart.feed.base.trade.enums.MarketTradeType.YELLOW_FLAG;
import static com.barchart.util.common.ascii.ASCII.AT;
import static com.barchart.util.common.ascii.ASCII.QUEST;
import static com.barchart.util.common.ascii.ASCII.SPACE;
import static com.barchart.util.common.ascii.ASCII._0_;
import static com.barchart.util.common.ascii.ASCII._1_;
import static com.barchart.util.common.ascii.ASCII._2_;
import static com.barchart.util.common.ascii.ASCII._3_;
import static com.barchart.util.common.ascii.ASCII._4_;
import static com.barchart.util.common.ascii.ASCII._5_;
import static com.barchart.util.common.ascii.ASCII._6_;
import static com.barchart.util.common.ascii.ASCII._7_;
import static com.barchart.util.common.ascii.ASCII._8_;
import static com.barchart.util.common.ascii.ASCII._9_;
import static com.barchart.util.common.ascii.ASCII._A_;
import static com.barchart.util.common.ascii.ASCII._B_;
import static com.barchart.util.common.ascii.ASCII._C_;
import static com.barchart.util.common.ascii.ASCII._D_;
import static com.barchart.util.common.ascii.ASCII._E_;
import static com.barchart.util.common.ascii.ASCII._F_;
import static com.barchart.util.common.ascii.ASCII._G_;
import static com.barchart.util.common.ascii.ASCII._H_;
import static com.barchart.util.common.ascii.ASCII._I_;
import static com.barchart.util.common.ascii.ASCII._J_;
import static com.barchart.util.common.ascii.ASCII._K_;
import static com.barchart.util.common.ascii.ASCII._L_;
import static com.barchart.util.common.ascii.ASCII._M_;
import static com.barchart.util.common.ascii.ASCII._N_;
import static com.barchart.util.common.ascii.ASCII._O_;
import static com.barchart.util.common.ascii.ASCII._P_;
import static com.barchart.util.common.ascii.ASCII._Q_;
import static com.barchart.util.common.ascii.ASCII._R_;
import static com.barchart.util.common.ascii.ASCII._S_;
import static com.barchart.util.common.ascii.ASCII._T_;
import static com.barchart.util.common.ascii.ASCII._U_;
import static com.barchart.util.common.ascii.ASCII._V_;
import static com.barchart.util.common.ascii.ASCII._W_;
import static com.barchart.util.common.ascii.ASCII._X_;
import static com.barchart.util.common.ascii.ASCII._Y_;
import static com.barchart.util.common.ascii.ASCII._Z_;

import java.util.Arrays;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.enums.EnumByteOrdinal;
import com.barchart.feed.base.enums.EnumCodeByte;
import com.barchart.feed.base.trade.enums.MarketTradeSequencing;
import com.barchart.feed.base.trade.enums.MarketTradeSession;
import com.barchart.feed.base.trade.enums.MarketTradeType;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_ExchangeKind;
import com.barchart.util.common.math.MathExtra;

// TODO: Auto-generated Javadoc
/**
 * ddf trade session source;.
 */
public enum DDF_Session implements EnumCodeByte, EnumByteOrdinal {

	// ########################################

	// merged

	$_AT(AT, REGULAR_SALE), //

	// Override to default session since we have separate symbols for these
	$_SPACE(SPACE, DDF_Exchange.UNKNOWN, FUTURE_COMPOSITE, DEFAULT, NORMAL), // XXX

	$_0_(_0_, MarketTradeType.UNKNOWN), //
	$_1_(_1_, STOPPED_STOCK_REGULAR),
	$_2_(_2_, STOPPED_STOCK_SOLD_LAST), //
	$_3_(_3_, STOPPED_STOCK_OOO), //
	$_4_(_4_, DERIVATIVELY_PRICED), //
	$_5_(_5_, MARKET_REOPENING), //
	$_6_(_6_, MARKET_CLOSING), //
	$_7_(_7_, MarketTradeType.UNKNOWN), //
	$_8_(_8_, MarketTradeType.UNKNOWN), //
	$_9_(_9_, RESERVED), //

	$_A_(_A_, ACQUISITION), //
	$_B_(_B_, BUNCHED_TRADE), //
	$_C_(_C_, MarketTradeType.UNKNOWN), //
	$_D_(_D_, DISTRIBUTION), //
	$_E_(_E_, AUTOMATIC_EXECUTION), //
	$_F_(_F_, INTERMARKET_SWEEP), //
	$_G_COM(_G_, BUNCHED_SOLD), // XXX

	// Override to default session, since we have separate symbols for these
	$_G_NET(_G_, DDF_Exchange.UNKNOWN, FUTURE_ELECTRONIC, DEFAULT, NORMAL), // XXX

	$_H_(_H_, MarketTradeType.UNKNOWN), //
	$_I_(_I_, ODD_LOT), //
	$_J_(_J_, MarketTradeType.UNKNOWN), //
	$_K_(_K_, NYSE_RULE_127), //
	$_L_(_L_, SOLD_LAST), //
	$_M_(_M_, MarketTradeType.UNKNOWN), //
	$_N_(_N_, MarketTradeType.UNKNOWN), //
	$_O_(_O_, MARKET_OPENING), //
	$_P_(_P_, PRIOR_REFERENCE_PRICE), //
	$_Q_(_Q_, MarketTradeType.UNKNOWN), //

	// Override to default session since we have separate symbols for these
	$_R_(_R_, DDF_Exchange.UNKNOWN, FUTURE_PIT, DEFAULT, NORMAL), // XXX

	$_S_(_S_, SPLIT), //
	$_T_(_T_, FORM_T), // XXX
	$_U_(_U_, FORM_T_OOO), // XXX
	$_V_(_V_, STOCK_OPTION), //
	$_W_(_W_, MarketTradeType.UNKNOWN), //
	$_X_(_X_, CROSS_TRADE), //
	$_Y_(_Y_, YELLOW_FLAG), //
	$_Z_(_Z_, SOLD_OOO), //

	// ########################################

	// Commodities Session Codes (other than blank)

	/** future combo (net + pit) session */
	// Override to default session since we have separate symbols for these
	FUT_COMBO(SPACE, DDF_Exchange.UNKNOWN, FUTURE_COMPOSITE, DEFAULT, NORMAL), // XXX

	/** G = CME Globex (overnight session) */
	/** G = NYMEX/COMEX Access Session (overnight session) */
	/** networked / electronic session */
	// Override to default session since we have separate symbols for these
	FUT_NET(_G_, DDF_Exchange.UNKNOWN, FUTURE_ELECTRONIC, DEFAULT, NORMAL), // XXX

	/** R = CME Regular Trading Hours (pit session) */
	/** R = NYMEX/COMEX Regular Trading Hours (pit session) */
	/** pit or manual session */
	// Override to default session since we have separate symbols for these
	FUT_PIT(_G_, DDF_Exchange.UNKNOWN, FUTURE_PIT, DEFAULT, NORMAL), // XXX

	// ########################################

	// NYSE/AMEX - processing rules.

	/*
	 * The following sale conditions are live trade messages and should be
	 * processed as such.
	 */

	/** @ - Regular Sale */
	NYSE_Regular_Sale(AT, REGULAR_SALE),

	/** E - Automatic Execution */
	NYSE_Automatic_Execution(_E_, AUTOMATIC_EXECUTION),

	/** F - InterMarket Sweep */
	NYSE_Inter_Market_Execution(_F_, INTERMARKET_SWEEP),

	/** K - Rule 127 (NYSE Only) or Rule 155 (Amex Only) */
	NYSE_Rule127_Rule155(_K_, NYSE_RULE_127),

	/** V - Stock Option Trade */
	NYSE_Stock_Option_Trade(_V_, STOCK_OPTION),

	/** X - Cross Trade */
	NYSE_Cross_Trade(_X_, CROSS_TRADE),

	/** 5 - Market Center Reopening Trade */
	NYSE_Market_Center_Reopening_Trade(_5_, MARKET_REOPENING),

	/** 6 - Market Center Closing Trade */
	NYSE_Market_Center_Closing_Trade(_6_, MARKET_CLOSING),

	/** 9 - Reserved */
	NYSE_Reserved(_9_, RESERVED),

	/*
	 * The following sale conditions are trade messages that will update the
	 * high/low values but will only update the last price if they are the first
	 * trades of the day.
	 */

	/** L - Sold Last */
	NYSE_Sold_Last(_L_, SOLD_LAST),

	/** O - Opened */
	NYSE_Opened(_O_, MARKET_OPENING),

	/** P - Prior Reference Price */
	NYSE_Prior_Reference_Price(_P_, PRIOR_REFERENCE_PRICE),

	/** Z - Sold (out of sequence) */
	NYSE_Sold_Seq(_Z_, SOLD_OOO),

	/** 4 - Derivatively Priced */
	NYSE_Derivatively_Priced(_4_, DERIVATIVELY_PRICED),

	// ########################################

	// Nasdaq/OTC - processing rules.

	/*
	 * The following sale conditions are live trade messages and should be
	 * processed as such.
	 */

	/** @ - Regular Sale */
	NASDAQ_Regular_Sale(AT, REGULAR_SALE),

	/** A - Acquisition */
	NASDAQ_Acquisition(_A_, ACQUISITION),

	/** B - Bunched Trade */
	NASDAQ_Bunched_Trade(_B_, BUNCHED_TRADE),

	/** D - Distribution */
	NASDAQ_Distribution(_D_, DISTRIBUTION),

	/** E - Future Place Holder */
	NASDAQ_Future_Place_Holder(_E_, RESERVED),

	/** F - InterMarket Sweep */
	NASDAQ_Inter_Market_Sweep(_F_, INTERMARKET_SWEEP),

	/** K - Rule 155 Trade (Amex Only) */
	NASDAQ_Rule127_Rule155(_K_, AMEX_RULE_155),

	/** O - Opening Prints */
	NASDAQ_Opening_Prints(_O_, MARKET_OPENING),

	/** S - Split Trade */
	NASDAQ_Split_Trade(_S_, SPLIT),

	/** V - Stock-Option Trade */
	NASDAQ_Stock_Option_Trade(_V_, STOCK_OPTION),

	/** X - Cross Trade */
	NASDAQ_Cross_Trade(_X_, CROSS_TRADE),

	/** Y - Yellow Flag */
	NASDAQ_Yellow_Flag(_Y_, YELLOW_FLAG),

	/** 1 - Stopped Stock - Regular Trade */
	NASDAQ_Stopped_Stock_Trade(_1_, STOPPED_STOCK_REGULAR),

	/** 5 - Re-Opening Prints */
	NASDAQ_Re_Opening_Prints(_5_, MARKET_REOPENING),

	/** 6 - Closing Prints */
	NASDAQ_Closing_Prints(_6_, MARKET_CLOSING),

	/*
	 * The following sale conditions are trade messages that will update the
	 * high/low values but will only update the last price if they are the first
	 * trades of the day.
	 */

	/** G - Bunched Sold Trade */
	NASDAQ_Bunched_Sold_Trade(_G_, BUNCHED_SOLD),

	/** P - Prior Reference Price */
	NASDAQ_Prior_Reference_Price(_P_, PRIOR_REFERENCE_PRICE),

	/** Z - Sold out of Sequence */
	NASDAQ_Sold_Seq(_Z_, SOLD_OOO),

	/** 3 - Stopped Stock - Sold (out of sequence) */
	NASDAQ_Stopped_Stock_Sold_Seq(_3_, STOPPED_STOCK_OOO),

	/** 4 - Derivatively Priced */
	NASDAQ_Derivatively_Priced(_4_, DERIVATIVELY_PRICED),

	/*
	 * The following sale conditions are trade messages that will update the
	 * high/low values and last prices if the market is opened. After market
	 * only update the high/low values.
	 */

	/** L - Sold Last */
	NASDAQ_Sold_Last(_L_, SOLD_LAST),

	// 2 - Stopped Stock - Sold Last
	NASDAQ_Stopped_Stock_Sold_Last(_2_, STOPPED_STOCK_SOLD_LAST),

	// ########################################

	// Form T trades (NYSE/AMEX/Nasdaq/OTC)

	/*
	 * Common to all exchanges are the pre and post market trades also known as
	 * Form T trades, which are passed down with session code 'T' and session
	 * code 'U'. Form T trades do not update the daily open, high, low or last
	 * fields, and are normally presented as their own field element.
	 */

	/** T - Pre/Post Market Trade */
	FORM_T_1(_T_, FORM_T),

	/** U - Pre/Post Market Trade - Sold out of Sequence */
	FORM_T_2(_U_, FORM_T_OOO),

	// ########################################

	/** */
	UNKNOWN(QUEST, MarketTradeType.UNKNOWN), //

	/**
	 * The Enum Market.
	 */
	;

	private static final Logger log = LoggerFactory
			.getLogger(DDF_Session.class);

	/** The COM p_ code. */
	public static Comparator<DDF_Session> COMP_CODE = new Comparator<DDF_Session>() {
		@Override
		public int compare(final DDF_Session o1, final DDF_Session o2) {
			if (o1 == null || o2 == null) {
				return 0;
			}
			if (o1.code > o2.code) {
				return +1;
			}
			if (o1.code < o2.code) {
				return -1;
			}
			return 0;
		}
	};

	/**
	 * The main method.
	 *
	 * @param strings
	 *            the arguments
	 */
	public static void main(final String... strings) {

		log.debug("init");

		final DDF_Session[] array = values();

		Arrays.sort(array, COMP_CODE);

		for (final DDF_Session entry : array) {

			log.debug("\t {}", entry);

		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return String.format("%40s '%c' %4s", name(), code, type);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.barchart.util.enums.EnumByteOrdinal#ord()
	 */
	@Override
	public final byte ord() {
		return ord;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.barchart.util.enums.EnumCodeByte#code()
	 */
	@Override
	public final byte code() {
		return code;
	}

	/** The ord. */
	public final byte ord;

	/** The code. */
	public final byte code;

	/** The exchange. */
	public final byte exch;

	/** The trade type. */
	public final MarketTradeType type;

	/** The session. */
	public final MarketTradeSession session;

	/** The sequencing hint. */
	public final MarketTradeSequencing sequencing;

	private DDF_Session(final byte code, final MarketTradeType mtt) {
		this(code, DDF_Exchange.UNKNOWN, mtt, mtt.session, mtt.sequencing);
	}

	private DDF_Session(final byte code, final DDF_Exchange exch,
			final MarketTradeType mtt, final MarketTradeSequencing mtsq) {
		this(code, exch, mtt, mtt.session, mtsq);
	}

	private DDF_Session(final byte code, final DDF_Exchange exch,
			final MarketTradeType mtt, final MarketTradeSession mts,
			final MarketTradeSequencing mtsq) {
		this.ord = (byte) ordinal();
		this.code = code;
		this.exch = exch.ord;
		this.type = mtt;
		this.session = mts;
		this.sequencing = mtsq;
	}

	private final static DDF_Session[] ENUM_VALS = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ session[]
	 */
	@Deprecated
	public final static DDF_Session[] valuesUnsafe() {
		return ENUM_VALS;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALS.length);
	}

	/**
	 * From ord.
	 *
	 * @param ord
	 *            the ord
	 * @return the dD f_ session
	 */
	public final static DDF_Session fromOrd(final byte ord) {
		return ENUM_VALS[ord];
	}

	//

	/**
	 * Special casing for duplicate session codes that are handled different
	 * across exchanges
	 */
	private static DDF_Session resolve(final DDF_Session known,
			final byte exchOrd) {

		if (known.code == _G_) {
			/* duplicate session code in future and stock (different meaning) */
			final DDF_Exchange exch = DDF_Exchange.fromOrd(exchOrd);
			if (exch.kind == DDF_ExchangeKind.FUTURE) {
				// electronic session for future
				return $_G_NET;
			} else {
				// default session for stock
				return $_G_COM;
			}
		}

		return known;

	}

	/**
	 * NOTE: ORD vs CODE.
	 *
	 * @param exchOrd
	 *            the exch ord
	 * @param sessCode
	 *            the sess code
	 * @return the dD f_ session
	 */
	public final static DDF_Session fromPair(final byte exchOrd,
			final byte sessCode) {

		for (final DDF_Session known : ENUM_VALS) {
			if (known.code == sessCode) {
				return resolve(known, exchOrd);
			}
		}

		// log.debug("### UNKNOWN session code : {}", sessCode);

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

}
