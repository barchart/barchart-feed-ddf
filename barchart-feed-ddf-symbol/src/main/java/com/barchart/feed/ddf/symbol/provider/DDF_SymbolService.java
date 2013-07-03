/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.provider;

import static com.barchart.feed.ddf.symbol.provider.DDF_Symbology.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.symbol.api.DDF_Symbol;
import com.barchart.feed.ddf.symbol.api.DDF_SymbolEquity;
import com.barchart.feed.ddf.symbol.api.DDF_SymbolFuture;
import com.barchart.feed.ddf.symbol.api.DDF_SymbolIndex;
import com.barchart.feed.ddf.symbol.api.DDF_SymbolOption;
import com.barchart.feed.ddf.symbol.api.DDF_SymbolSpread;
import com.barchart.feed.ddf.symbol.enums.DDF_Equity;
import com.barchart.feed.ddf.symbol.enums.DDF_ExpireMonth;
import com.barchart.feed.ddf.symbol.enums.DDF_ExpireYear;
import com.barchart.feed.ddf.symbol.enums.DDF_Option;
import com.barchart.feed.ddf.symbol.enums.DDF_OptionYear;
import com.barchart.util.ascii.ASCII;
import com.barchart.util.values.api.TextValue;

// TODO: Auto-generated Javadoc
/**
 * The Class DDF_SymbolService.
 */
public class DDF_SymbolService {

	private static final Logger log = LoggerFactory
			.getLogger(DDF_SymbolService.class);

	private DDF_SymbolService() {
	}

	// /////////////////////////

	static final DDF_Symbol NULL_SYMBOL = new BaseEquity();

	// /////////////////////////

	private static final ConcurrentMap<TextValue, DDF_Symbol> symbolMap = //
	new ConcurrentHashMap<TextValue, DDF_Symbol>();

	/**
	 * Find.
	 *
	 * @param text the text
	 * @return the dD f_ symbol
	 */
	public static final DDF_Symbol find(final TextValue text) {
		final TextValue guid = DDF_Symbology.lookupFromSymbol(text);
		DDF_Symbol symbol = symbolMap.get(guid);

		//log.error("find {}", text);

		if (symbol == null) {
			symbol = decode(guid.toString());
			// TODO handle null symbol
			symbolMap.putIfAbsent(guid, symbol);
		}
		return symbol;
	}

	/**
	 * Clear cache.
	 */
	public static final void clearCache() {
		symbolMap.clear();
	}

	// /////////////////////////

	/**
	 * Decode.
	 *
	 * @param symbolName the symbol name
	 * @return the dD f_ symbol
	 */
	public static final DDF_Symbol decode(final String symbolName) {

		if (symbolName == null) {
			log.error("symbolName == null");
			return null;
		}

		if (isOptionExtended(symbolName)) {
			final OptionExtended symbol = new OptionExtended();
			symbol.name = symbolName;
			final int finish = symbolName.length();
			final int middle = symbolName.indexOf(OPTION_SEPARATOR);
			symbol.group = symbolName.substring(0, middle - 2);
			symbol.month = DDF_ExpireMonth.fromCode(symbolName
					.charAt(middle - 2));
			symbol.year = DDF_ExpireYear
					.fromCode(symbolName.charAt(middle - 1));
			symbol.strikePrice = symbolName.substring(middle + 1, finish - 1);
			symbol.optionIndicator = DDF_Option.fromCode(symbolName
					.charAt(finish - 1));
			return symbol;
		}

		if (isOptionBasic(symbolName)) {
			final OptionBasic symbol = new OptionBasic();
			symbol.name = symbolName;
			final int finish = symbolName.length();
			int middle = 0;
			for (int k = finish - 2; k > 0; k--) {
				if (ASCII.isDigit(symbolName.charAt(k))) {
					continue;
				} else {
					middle = k;
					break;
				}
			}
			symbol.group = symbolName.substring(0, middle);
			symbol.month = DDF_ExpireMonth.fromCode(symbolName.charAt(middle));
			final DDF_OptionYear indicator = DDF_OptionYear.fromCode(symbolName
					.charAt(finish - 1));
			symbol.year = DDF_ExpireYear.fromOptionYear(indicator);
			symbol.strikePrice = symbolName.substring(middle + 1, finish - 1);
			symbol.optionIndicator = DDF_Option.fromOptionYear(indicator);
			
			return symbol;
		}

		if (isIndex(symbolName)) {
			final BaseIndex symbol = new BaseIndex();
			symbol.name = symbolName;
			symbol.group = symbolName.substring(1);
			return symbol;
		}

		if (isEquityCAN(symbolName)) {
			final BaseEquity symbol = new BaseEquity();
			symbol.name = symbolName;
			symbol.group = getGroupEquityCAN(symbolName);
			symbol.equityType = DDF_Equity.CAN;
			return symbol;
		}

		if (isEquityLSE(symbolName)) {
			final BaseEquity symbol = new BaseEquity();
			symbol.name = symbolName;
			symbol.group = getGroupEquityLSE(symbolName);
			symbol.equityType = DDF_Equity.LSE;
			return symbol;
		}

		if (isEquityNSE(symbolName)) {
			final BaseEquity symbol = new BaseEquity();
			symbol.name = symbolName;
			symbol.group = getGroupEquityNSE(symbolName);
			symbol.equityType = DDF_Equity.NSE;
			return symbol;
		}

		if (isFuture(symbolName)) {
			final BaseFuture symbol = new BaseFuture();
			symbol.name = symbolName;
			symbol.group = getGroupFuture(symbolName);
			symbol.year = getFutureExpireYear(symbolName);
			symbol.month = getFutureExpireMonth(symbolName);
			return symbol;
		}

		if (isEquityOther(symbolName)) {
			final BaseEquity symbol = new BaseEquity();
			symbol.name = symbolName;
			symbol.group = symbolName;
			return symbol;
		}

		// TODO finish remaining types

		log.error("can not parse; symbolName={}", symbolName);

		return NULL_SYMBOL;

	}

	// TODO
	/**
	 * New future.
	 *
	 * @param group the group
	 * @param year the year
	 * @param month the month
	 * @return the dD f_ symbol future
	 */
	public static final DDF_SymbolFuture newFuture(final String group,
			final DDF_ExpireYear year, final DDF_ExpireMonth month) {
		final BaseFuture symbol = new BaseFuture();
		symbol.group = group;
		symbol.year = year;
		symbol.month = month;
		symbol.name = symbol.toString();
		return symbol;
	}

	// TODO
	/**
	 * New option.
	 *
	 * @param group the group
	 * @param year the year
	 * @param month the month
	 * @param strike the strike
	 * @param indicator the indicator
	 * @return the dD f_ symbol option
	 */
	public static final DDF_SymbolOption newOption(final String group,
			final DDF_ExpireYear year, final DDF_ExpireMonth month,
			final String strike, final DDF_Option indicator) {
		return null;
	}

	// TODO
	/**
	 * New spread future.
	 *
	 * @param symbolArray the symbol array
	 * @return the dD f_ symbol spread
	 */
	public static final DDF_SymbolSpread<DDF_SymbolFuture> newSpreadFuture(
			final DDF_SymbolFuture... symbolArray) {
		return null;
	}

	// TODO
	/**
	 * New spread option.
	 *
	 * @param symbolArray the symbol array
	 * @return the dD f_ symbol spread
	 */
	public static final DDF_SymbolSpread<DDF_SymbolOption> newSpreadOption(
			final DDF_SymbolOption... symbolArray) {
		return null;
	}

	// TODO
	/**
	 * New index.
	 *
	 * @param symbolName the symbol name
	 * @return the dD f_ symbol index
	 */
	public static final DDF_SymbolIndex newIndex(final String symbolName) {
		return null;
	}

	// TODO
	/**
	 * New equity.
	 *
	 * @param symbolName the symbol name
	 * @return the dD f_ symbol equity
	 */
	public static final DDF_SymbolEquity newEquity(final String symbolName) {
		return null;
	}

	/**
	 * Find ddf.
	 *
	 * @param id the id
	 * @return the dD f_ symbol
	 */
	public static DDF_Symbol findDDF(final TextValue id) {
		// TODO Auto-generated method stub
		return null;
	}

}
