/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.enums;

import static com.barchart.feed.ddf.symbol.enums.DDF_Exchange.*;

import com.barchart.util.math.MathExtra;

// TODO: Auto-generated Javadoc
/**
 * market channel "permission" qualifiers.
 */
public enum DDF_ExchangeChannel {

	/**
	 * <exchange id="AMEX" ddfcode="A" delay="15"
	 * description="American Stock Exchange" codes="AMEX" />
	 */
	EXT_AMEX("AMEX", AMEX), //

	/**
	 * <exchange id="CBOT" ddfcode="B" delay="10" description="CMEGroup CBOT"
	 * codes="CBOT" feewaiver="yes" />
	 */
	EXT_CBOT("CBOT", CME_CBOT), //

	/**
	 * <exchange id="CBOTM" delay="10" description="CMEGroup CBOT Mini"
	 * codes="CBOTM" feewaiver="yes" />
	 */
	EXT_CBOT_MINI("CBOTM", CME_CBOT), //

	/**
	 * <exchange id="CFE" ddfcode="R" delay="10"
	 * description="CBOE Futures Exchange" codes="CFE" feewaiver="yes" />
	 */
	EXT_CFE("CFE", CBOE_Futures), //

	/**
	 * <exchange id="CME" ddfcode="M" delay="10" description="CMEGroup CME"
	 * codes="CME,IOM,IMM,WEA" feewaiver="yes" />
	 */
	EXT_CME("CME", CME_Main), //
	EXT_IOM("IOM", CME_Main), //
	EXT_IMM("IMM", CME_Main), //
	EXT_WEA("WEA", CME_Main), //

	/**
	 * <exchange id="COMEX" ddfcode="E" delay="10" description="CMEGroup COMEX"
	 * codes="COMEX" feewaiver="yes" />
	 */
	EXT_COMEX("COMEX", CME_COMEX), //

	/**
	 * <exchange id="CXMI" delay="10" description="CMEGroup COMEX emiNY"
	 * codes="CXMI" feewaiver="yes" />
	 */
	EXT_COMEX_EMI("CXMI", CME_COMEX), //

	/**
	 * <exchange id="DME" delay="10" description="Dubai Mercantile Exchange"
	 * codes="DME" feewaiver="yes" />
	 */
	EXT_DME("DME", CME_Main), //

	/**
	 * <exchange id="EUREX" delay="99" description="EUREX" codes="EUREX" />
	 */
	EXT_EUREX("EUREX", DDF_Exchange.UNKNOWN), //

	/**
	 * <exchange id="FOREX" ddfcode="$" delay="10"
	 * description="Foreign Exchange" codes="FOREX" />
	 */
	EXT_FOREX("FOREX", Forex), //

	/**
	 * <exchange id="FUND" delay="15" description="Mutual Funds" codes="FUND" />
	 */
	EXT_FUND("FUND", Mutual_Funds), //

	/**
	 * <exchange id="GBLX" delay="10" description="CMEGroup CME eMinis"
	 * codes="GBLX,ZZZ" feewaiver="yes" />
	 */
	EXT_GBLX("GBLX", CME_Main), //
	EXT_ZZZ("ZZZ", CME_Main), //

	/**
	 * <exchange id="ICE" ddfcode="L" delay="10"
	 * description="Intercontinental Exchange" codes="ICE" />
	 */
	EXT_ICE("ICE", ICE_EU), //

	/**
	 * <exchange id="ICEFI" delay="10" description="Intercontinental Exchange"
	 * codes="ICEFI" feewaiver="yes" />
	 */
	EXT_ICEFI("ICEFI", ICE_EU), //

	/**
	 * <exchange id="WPG" ddfcode="W" delay="10"
	 * description="ICE: Winnipeg Commodity Exchange" codes="ICECA,WPG,WCE" />
	 */
	EXT_ICECA("ICECA", ICE_Canada), //
	EXT_WPG("WPG", ICE_Canada), //
	EXT_WCE("WCE", ICE_Canada), //

	/**
	 * <exchange id="NYBOT" ddfcode="C" delay="10" description="ICE: NYBOT"
	 * codes="ICEUS,NYBOT,CSCE,FINEX,NYCE,NYFE" />
	 */
	EXT_ICEUS("ICEUS", ICE_US), //
	EXT_NYBOT("NYBOT", ICE_US), //
	EXT_CSCE("CSCE", ICE_US), //
	EXT_FINEX("FINEX", ICE_US), //
	EXT_NYCE("NYCE", ICE_US), //
	EXT_NYFE("NYFE", ICE_US), //

	/**
	 * <exchange id="IDX" ddfcode="I" delay="10" description="Indices"
	 * codes="IDX,INDEX" />
	 */
	EXT_IDX("IDX", Index_NO_DOW_NO_SP), //
	EXT_INDEX("INDEX", Index_NO_DOW_NO_SP), //

	/**
	 * <exchange id="IDX_CFE" ddfcode="r" delay="10" description="Indices: CFE"
	 * codes="IDX_CFE,INDEX-CFE" />
	 */
	EXT_IDX_CFE("IDX_CFE", CBOE_Index), //
	EXT_IDX_INDEX_CFE("INDEX-CFE", CBOE_Index), //

	/**
	 * <exchange id="IDX_DOW" ddfcode="p" delay="10"
	 * description="Indices: Dow Jones" codes="IDX_DOW,INDEX-DOW" />
	 */
	EXT_IDX_DOW("IDX_DOW", Index_DOW_Full), //
	EXT_INDEX_DOW("INDEX-DOW", Index_DOW_Full), //

	/**
	 * <exchange id="IDX_SP" ddfcode="o" delay="10"
	 * description="Indices: S&amp;P" codes="IDX_SP,INDEX-SP" />
	 */
	EXT_IDX_SP("IDX_SP", Index_SP), //
	EXT_INDEX_SP("INDEX-SP", Index_SP), //

	/**
	 * <exchange id="KCBT" ddfcode="K" delay="10"
	 * description="Kansas City Board of Trade" codes="KCBT" feewaiver="yes" />
	 */
	EXT_KCBT("KCBT", CME_KBOT), //

	/**
	 * <exchange id="MGEX" ddfcode="G" delay="10"
	 * description="Minneapolis Grain Exchange" codes="MGEX" feewaiver="yes" />
	 */
	EXT_MGEX("MGEX", CME_MGEX), //

	/**
	 * <exchange id="NASDAQ" ddfcode="Q" delay="15" description="Nasdaq"
	 * codes="NASDAQ,NTDS" />
	 */
	EXT_NASDAQ("NASDAQ", NASDAQ), //
	EXT_NTDS("NTDS", NASDAQ), //

	/**
	 * <exchange id="NYMEX" ddfcode="J" delay="10" description="CMEGroup NYMEX"
	 * codes="NYMEX" feewaiver="yes" />
	 */
	EXT_NYMEX("NYMEX", CME_NYMEX), //

	/**
	 * <exchange id="NYMI" delay="10" description="CMEGroup NYMEX emiNY"
	 * codes="NYMI" feewaiver="yes" />
	 */
	EXT_NYMEX_EMI("NYMI", CME_NYMEX), //

	/**
	 * <exchange id="NYSE" ddfcode="N" delay="15" description="NYSE"
	 * codes="NYSE" />
	 */
	EXT_NYSE("NYSE", NYSE), //

	/**
	 * <exchange id="NLIF" delay="10" description="NYSE Liffe" codes="NLIF" />
	 */
	EXT_NLIF("NLIF", NYSE_Metals), //

	/**
	 * <exchange id="OTCBB" ddfcode="D" delay="10" description="Nasdaq"
	 * codes="OTCBB,OTC-BB" />
	 */
	EXT_OTCBB("OTCBB", NASDAQ_OTC_BB), //
	EXT_OTC_BB("OTC-BB", NASDAQ_OTC_BB), //

	/**
	 * <exchange id="PINKSHEETS" ddfcode="U" delay="10" description="Pinksheets"
	 * codes="PINKSHEETS" />
	 */
	EXT_PINKSHEETS("PINKSHEETS", NASDAQ_OTC_PinkSheets), //

	/**
	 * <exchange id="TEST" ddfcode="_" delay="0" description="Test"
	 * codes="DDF,TEST" />
	 */
	EXT_DDF("DDF", TEST), //
	EXT_TEST("TEST", TEST), //

	/**
	 * <exchange id="BROCK" delay="0" description="Brock Report" codes="BROCK"
	 * />
	 */
	EXT_BROCK("BROCK", DDF_Exchange.UNKNOWN), //

	//

	/***/
	UNKNOWN("", DDF_Exchange.UNKNOWN), //

	/** The ord. */
 ;

	// /////////////////////

	/** byte sized enum ordinal */
	public final byte ord;

	/** ddf encoding of this enum. */
	public final String code;

	/** ddf exchange associated with "permissions" exchange. */
	public final DDF_Exchange exchange;

	// /////////////////////

	private DDF_ExchangeChannel(final String code, final DDF_Exchange exchange) {
		this.ord = (byte) ordinal();
		this.code = code;
		this.exchange = exchange;
	}

	private final static DDF_ExchangeChannel[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ exchange channel[]
	 */
	@Deprecated
	public final static DDF_ExchangeChannel[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	/**
	 * From code.
	 *
	 * @param code the code
	 * @return the dD f_ exchange channel
	 */
	public final static DDF_ExchangeChannel fromCode(final String code) {
		for (final DDF_ExchangeChannel known : ENUM_VALUES) {
			if (known.code.equalsIgnoreCase(code)) {
				return known;
			}
		}
		return UNKNOWN;
	}

	/**
	 * From ord.
	 *
	 * @param ord the ord
	 * @return the dD f_ exchange channel
	 */
	public final static DDF_ExchangeChannel fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
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
