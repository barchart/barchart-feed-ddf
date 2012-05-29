/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.enums;

import static com.barchart.feed.ddf.message.enums.DDF_Session.Market.DEF;
import static com.barchart.feed.ddf.message.enums.DDF_Session.Market.EXT;
import static com.barchart.feed.ddf.message.enums.DDF_Session.Market.NET;
import static com.barchart.feed.ddf.message.enums.DDF_Session.Market.PIT;
import static com.barchart.util.ascii.ASCII.AT;
import static com.barchart.util.ascii.ASCII.QUEST;
import static com.barchart.util.ascii.ASCII.SPACE;
import static com.barchart.util.ascii.ASCII._0_;
import static com.barchart.util.ascii.ASCII._1_;
import static com.barchart.util.ascii.ASCII._2_;
import static com.barchart.util.ascii.ASCII._3_;
import static com.barchart.util.ascii.ASCII._4_;
import static com.barchart.util.ascii.ASCII._5_;
import static com.barchart.util.ascii.ASCII._6_;
import static com.barchart.util.ascii.ASCII._7_;
import static com.barchart.util.ascii.ASCII._8_;
import static com.barchart.util.ascii.ASCII._9_;
import static com.barchart.util.ascii.ASCII._A_;
import static com.barchart.util.ascii.ASCII._B_;
import static com.barchart.util.ascii.ASCII._C_;
import static com.barchart.util.ascii.ASCII._D_;
import static com.barchart.util.ascii.ASCII._E_;
import static com.barchart.util.ascii.ASCII._F_;
import static com.barchart.util.ascii.ASCII._G_;
import static com.barchart.util.ascii.ASCII._H_;
import static com.barchart.util.ascii.ASCII._I_;
import static com.barchart.util.ascii.ASCII._J_;
import static com.barchart.util.ascii.ASCII._K_;
import static com.barchart.util.ascii.ASCII._L_;
import static com.barchart.util.ascii.ASCII._M_;
import static com.barchart.util.ascii.ASCII._N_;
import static com.barchart.util.ascii.ASCII._O_;
import static com.barchart.util.ascii.ASCII._P_;
import static com.barchart.util.ascii.ASCII._Q_;
import static com.barchart.util.ascii.ASCII._R_;
import static com.barchart.util.ascii.ASCII._S_;
import static com.barchart.util.ascii.ASCII._T_;
import static com.barchart.util.ascii.ASCII._U_;
import static com.barchart.util.ascii.ASCII._V_;
import static com.barchart.util.ascii.ASCII._W_;
import static com.barchart.util.ascii.ASCII._X_;
import static com.barchart.util.ascii.ASCII._Y_;
import static com.barchart.util.ascii.ASCII._Z_;

import java.util.Arrays;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_ExchangeKind;
import com.barchart.util.enums.EnumByteOrdinal;
import com.barchart.util.enums.EnumCodeByte;
import com.barchart.util.math.MathExtra;

// TODO: Auto-generated Javadoc
/**
 * ddf trade session source;.
 */
public enum DDF_Session implements EnumCodeByte, EnumByteOrdinal {

	// ########################################

	// merged

	$_AT(AT, DEF), //
	$_SPACE(SPACE, DEF), //

	$_0_(_0_, DEF), //
	$_1_(_1_, DEF), //
	$_2_(_2_, DEF), //
	$_3_(_3_, DEF), //
	$_4_(_4_, DEF), //
	$_5_(_5_, DEF), //
	$_6_(_6_, DEF), //
	$_7_(_7_, DEF), //
	$_8_(_8_, DEF), //
	$_9_(_9_, DEF), //

	$_A_(_A_, DEF), //
	$_B_(_B_, DEF), //
	$_C_(_C_, DEF), //
	$_D_(_D_, DEF), //
	$_E_(_E_, DEF), //
	$_F_(_F_, DEF), //
	$_G_COM(_G_, DEF), // XXX
	$_G_NET(_G_, NET), // XXX
	$_H_(_H_, DEF), //
	$_I_(_I_, DEF), //
	$_J_(_J_, DEF), //
	$_K_(_K_, DEF), //
	$_L_(_L_, DEF), //
	$_M_(_M_, DEF), //
	$_N_(_N_, DEF), //
	$_O_(_O_, DEF), //
	$_P_(_P_, DEF), //
	$_Q_(_Q_, DEF), //
	$_R_(_R_, PIT), // XXX
	$_S_(_S_, DEF), //
	$_T_(_T_, EXT), // XXX
	$_U_(_U_, EXT), // XXX
	$_V_(_V_, DEF), //
	$_W_(_W_, DEF), //
	$_X_(_X_, DEF), //
	$_Y_(_Y_, DEF), //
	$_Z_(_Z_, DEF), //

	// ########################################

	// Commodities Session Codes (other than blank)

	/** future combo (net + pit) session */
	FUTURE_COMBO(SPACE, DEF),

	/** G = CME Globex (overnight session) */
	/** G = NYMEX/COMEX Access Session (overnight session) */
	/** networked / electronic session */
	FUTURE_NET(_G_, NET),

	/** R = CME Regular Trading Hours (pit session) */
	/** R = NYMEX/COMEX Regular Trading Hours (pit session) */
	/** pit or manual session */
	FUTURE_PIT(_R_, PIT),

	// ########################################

	// NYSE/AMEX - processing rules.

	/*
	 * The following sale conditions are live trade messages and should be
	 * processed as such.
	 */

	/** @ - Regular Sale */
	NYSE_Regular_Sale(AT, DEF),

	/** E - Automatic Execution */
	NYSE_Automatic_Execution(_E_, DEF),

	/** F - InterMarket Sweep */
	NYSE_Inter_Market_Execution(_F_, DEF),

	/** K - Rule 127 (NYSE Only) or Rule 155 (Amex Only) */
	NYSE_Rule127_Rule155(_K_, DEF),

	/** V - Stock Option Trade */
	NYSE_Stock_Option_Trade(_V_, DEF),

	/** X - Cross Trade */
	NYSE_Cross_Trade(_X_, DEF),

	/** 5 - Market Center Reopening Trade */
	NYSE_Market_Center_Reopening_Trade(_5_, DEF),

	/** 6 - Market Center Closing Trade */
	NYSE_Market_Center_Closing_Trade(_6_, DEF),

	/** 9 - Reserved */
	NYSE_Reserved(_9_, DEF),

	/*
	 * The following sale conditions are trade messages that will update the
	 * high/low values but will only update the last price if they are the first
	 * trades of the day.
	 */

	/** L - Sold Last */
	NYSE_Sold_Last(_L_, DEF),

	/** O - Opened */
	NYSE_Opened(_O_, DEF),

	/** P - Prior Reference Price */
	NYSE_Prior_Reference_Price(_P_, DEF),

	/** Z - Sold (out of sequence) */
	NYSE_Sold_Seq(_Z_, DEF),

	/** 4 - Derivatively Priced */
	NYSE_Derivatively_Priced(_4_, DEF),

	// ########################################

	// Nasdaq/OTC - processing rules.

	/*
	 * The following sale conditions are live trade messages and should be
	 * processed as such.
	 */

	/** @ - Regular Sale */
	NASDAQ_Regular_Sale(AT, DEF),

	/** A - Acquisition */
	NASDAQ_Acquisition(_A_, DEF),

	/** B - Bunched Trade */
	NASDAQ_Bunched_Trade(_B_, DEF),

	/** D - Distribution */
	NASDAQ_Distribution(_D_, DEF),

	/** E - Future Place Holder */
	NASDAQ_Future_Place_Holder(_E_, DEF),

	/** F - InterMarket Sweep */
	NASDAQ_Inter_Market_Sweep(_F_, DEF),

	/** K - Rule 155 Trade (Amex Only) */
	NASDAQ_Rule127_Rule155(_K_, DEF),

	/** O - Opening Prints */
	NASDAQ_Opening_Prints(_O_, DEF),

	/** S - Split Trade */
	NASDAQ_Split_Trade(_S_, DEF),

	/** V - Stock-Option Trade */
	NASDAQ_Stock_Option_Trade(_V_, DEF),

	/** X - Cross Trade */
	NASDAQ_Cross_Trade(_X_, DEF),

	/** Y - Yellow Flag */
	NASDAQ_Yellow_Flag(_Y_, DEF),

	/** 1 - Stopped Stock - Regular Trade */
	NASDAQ_Stopped_Stock_Trade(_1_, DEF),

	/** 5 - Re-Opening Prints */
	NASDAQ_Re_Opening_Prints(_5_, DEF),

	/** 6 - Closing Prints */
	NASDAQ_Closing_Prints(_6_, DEF),

	/*
	 * The following sale conditions are trade messages that will update the
	 * high/low values but will only update the last price if they are the first
	 * trades of the day.
	 */

	/** G - Bunched Sold Trade */
	NASDAQ_Bunched_Sold_Trade(_G_, DEF),

	/** P - Prior Reference Price */
	NASDAQ_Prior_Reference_Price(_P_, DEF),

	/** Z - Sold out of Sequence */
	NASDAQ_Sold_Seq(_Z_, DEF),

	/** 3 - Stopped Stock - Sold (out of sequence) */
	NASDAQ_Stopped_Stock_Sold_Seq(_3_, DEF),

	/** 4 - Derivatively Priced */
	NASDAQ_Derivatively_Priced(_4_, DEF),

	/*
	 * The following sale conditions are trade messages that will update the
	 * high/low values and last prices if the market is opened. After market
	 * only update the high/low values.
	 */

	/** L - Sold Last */
	NASDAQ_Sold_Last(_L_, DEF),

	// 2 - Stopped Stock - Sold Last
	NASDAQ_Stopped_Stock_Sold_Last(_2_, DEF),

	// ########################################

	// Form T trades (NYSE/AMEX/Nasdaq/OTC)

	/*
	 * Common to all exchanges are the pre and post market trades also known as
	 * Form T trades, which are passed down with session code 'T' and session
	 * code 'U'. Form T trades do not update the daily open, high, low or last
	 * fields, and are normally presented as their own field element.
	 */

	/** T - Pre/Post Market Trade */
	FORM_T_1(_T_, EXT),

	/** U - Pre/Post Market Trade - Sold out of Sequence */
	FORM_T_2(_U_, EXT),

	// ########################################

	/** */
	UNKNOWN(QUEST, DEF), //

	/**
	 * The Enum Market.
	 */
	;

	public static enum Market {

		/** default or combined */
		DEF, //

		/** manual only */
		PIT, //

		/** electronic only */
		NET, //

		/** extra session; FORM_T for stocks */
		EXT, //

	}

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
		return String.format("%40s '%c' %4s", name(), code, market);
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

	/** The market. */
	public final Market market;

	private DDF_Session(final byte code, final Market kind) {
		this.ord = (byte) ordinal();
		this.code = code;
		this.market = kind;
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
