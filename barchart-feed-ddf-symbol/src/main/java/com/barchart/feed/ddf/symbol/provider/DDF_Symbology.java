/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.provider;

import static com.barchart.util.ascii.ASCII.*;
import static com.barchart.util.values.provider.ValueBuilder.*;
import static com.barchart.util.values.provider.ValueConst.*;

import com.barchart.feed.ddf.symbol.enums.DDF_ExpireMonth;
import com.barchart.feed.ddf.symbol.enums.DDF_ExpireYear;
import com.barchart.feed.ddf.symbol.enums.DDF_Option;
import com.barchart.feed.ddf.symbol.enums.DDF_OptionYear;
import com.barchart.util.ascii.ASCII;
import com.barchart.util.values.api.TextValue;

public final class DDF_Symbology {

	private DDF_Symbology() {
	}

	// //////////////////////////////////////

	/** barchart policy : only upper case symbols are permitted */
	public static final TextValue lookupFromSymbol(final TextValue text) {
		if (text == null) {
			return NULL_TEXT;
		}
		return text.toUpperCase();
	}

	public static final TextValue guidFromSymbol(final String text) {
		return lookupFromSymbol(newText(text));
	}

	/**
	 * non spread: <symbol>
	 * 
	 * or
	 * 
	 * for spread: <symbol1>_<symbol2>_..._<symbolN>
	 */
	public static final TextValue symbolFromSymbolArray(
			final byte[][] symbolArray) {
		return newText(byteArrayFromSymbolArray(symbolArray));
	}

	/** default ddf message "empty" symbol */
	public static final byte[] DDF_NO_NAME = new byte[0];

	public static final byte[] byteArrayFromSymbolArray(
			final byte[][] symbolArray) {
		if (symbolArray == null || symbolArray.length == 0) {
			return DDF_NO_NAME;
		}
		final int symbolCount = symbolArray.length;
		int fullSize = 0;
		for (final byte[] symbol : symbolArray) {
			fullSize += symbol.length;
		}
		fullSize += symbolCount - 1;
		final byte[] symbolFull = new byte[fullSize];
		int index = 0;
		for (int k = 0; k < symbolCount; k++) {
			if (k > 0) {
				symbolFull[index++] = UNDER;
			}
			final byte[] symbol = symbolArray[k];
			final int symbolLenght = symbol.length;
			System.arraycopy(symbol, 0, symbolFull, index, symbolLenght);
			index += symbolLenght;
		}
		assert index == fullSize;
		return symbolFull;
	}

	/**
	 * from ddf symbol with legs "<symbol1>_<symbl2>_...<symbolN>" into 2D byte
	 * array
	 */
	public static final byte[][] symbolArrayFromSymbolString(final String string) {
		if (string == null) {
			return null;
		}
		final String[] stringArray = string.split(STRING_UNDER);
		final int symbolCount = stringArray.length;
		final byte[][] symbolArray = new byte[symbolCount][];
		for (int k = 0; k < symbolCount; k++) {
			final String symbol = stringArray[k];
			symbolArray[k] = symbol.getBytes(ASCII_CHARSET);
		}
		return symbolArray;
	}

	// //////////////////////////////////////

	public static final String PREFIX_INDEX = "$";

	public static final String PREFIX_FOREX = "$";

	public static final String PREFIX_SECTOR = "-";

	public static final String PREFIX_TEST = "_";

	//

	public static final String SUFFIX_EQUITY_CAN_1 = ".TO";

	public static final String SUFFIX_EQUITY_CAN_2 = ".VN";

	public static final String SUFFIX_EQUITY_LSE = ".LS";

	public static final String SUFFIX_EQUITY_NSE = ".NS";

	public static final String SUFFIX_FUTURE_GROUP = "^F";

	public static final String SUFFIX_OPTION_GROUP = "^O";

	//

	// example : ES^F
	public static final String symbolFutureGroup(final String groupName) {
		return groupName + SUFFIX_FUTURE_GROUP;
	}

	// example : ES^O
	public static final String symbolOptionGroup(final String groupName) {
		return groupName + SUFFIX_OPTION_GROUP;
	}

	public static final boolean isFutureGroup(final String symbolName) {
		return symbolName.toUpperCase().endsWith(SUFFIX_FUTURE_GROUP);
	}

	public static final boolean isOptionGroup(final String symbolName) {
		return symbolName.toUpperCase().endsWith(SUFFIX_OPTION_GROUP);
	}

	public static final String getFutureGroup(final String symbolName) {
		final int index = symbolName.indexOf(SUFFIX_FUTURE_GROUP);
		if (index == -1) {
			return null;
		} else {
			return symbolName.substring(0, index);
		}
	}

	public static final String getOptionGroup(final String symbolAllName) {
		final int index = symbolAllName.indexOf(SUFFIX_OPTION_GROUP);
		if (index == -1) {
			return null;
		} else {
			return symbolAllName.substring(0, index);
		}
	}

	/*
	 * Example: CMY or CCMY; WZ8 or CTH8
	 * 
	 * Futures Symbols are 3 or 4 bytes, where C or CC is commodity code from
	 * symbols list, M is contract month code and Y is contract year.
	 */
	public static final boolean isFuture(final String symbol) {
		if (symbol == null) {
			return false;
		}
		if (!containsDigit(symbol)) {
			return false;
		}
		final int size = symbol.length();
		if (size < 3) {
			return false;
		}
		if (!DDF_ExpireYear.isValid(symbol.charAt(size - 1))) {
			return false;
		}
		if (!DDF_ExpireMonth.isValid(symbol.charAt(size - 2))) {
			return false;
		}
		return true;
	}

	public static final String getGroupFuture(final String symbolName) {
		final int start = 0;
		final int finish = symbolName.length() - 2;
		return symbolName.substring(start, finish);
	}

	public static final DDF_ExpireYear getFutureExpireYear(
			final String symbolName) {
		final int size = symbolName.length();
		final char code = symbolName.charAt(size - 1);
		return DDF_ExpireYear.fromCode(code);
	}

	public static final DDF_ExpireMonth getFutureExpireMonth(
			final String symbolName) {
		final int size = symbolName.length();
		final char code = symbolName.charAt(size - 2);
		return DDF_ExpireMonth.fromCode(code);
	}

	/*
	 * Futures Options Symbols Overview ================================
	 * 
	 * CMSSSSC or CCMSSSP; SX5300P or ESU990C
	 * 
	 * 8 bytes, where C or CC is commodity code from symbols list SSSS or SSS is
	 * strike price C is Put or Call letter C=call this year, D=call next year,
	 * E=call two years out, F=call 3 years out; G=call 4 years out P=put this
	 * year, Q=put next year, R=put 2 years out, S=put 3 years out, T=put 4
	 * years out
	 */

	public static final boolean isOptionBasic(final String symbol) {
		if (!containsDigit(symbol)) {
			return false;
		}
		if (containsOptionSeparator(symbol)) {
			return false;
		}
		final int size = symbol.length();
		if (!DDF_OptionYear.isValid(symbol.charAt(size - 1))) {
			return false;
		}
		return true;
	}

	/*
	 * Futures Options Symbols Overview - Extended Symbology
	 * =====================================================
	 * 
	 * For exchange traded spreads and select futures options, currently only
	 * NYMEX ClearPort Swaps, exchange code "j" DDF will start utilizing a new
	 * format for options contracts.
	 * 
	 * CCCMY|SSSSSC or CCCMY|SSSSSP
	 * 
	 * Example: JAOV7|10050P
	 * 
	 * Up to 12 bytes, where CCCMY is the futures symbol. CCC - commodity code,
	 * M - month, Y - year | - delimiter SSSSS is the strike price, left
	 * justified, zero padded to the right. C = Call, P = Put
	 */

	public static final String OPTION_SEPARATOR = ASCII.STRING_BAR;

	public static final boolean containsOptionSeparator(final String symbol) {
		if (symbol == null) {
			return false;
		}
		if (!containsDigit(symbol)) {
			return false;
		}
		return symbol.contains(OPTION_SEPARATOR);
	}

	public static final boolean isOptionExtended(final String symbol) {
		if (symbol == null) {
			return false;
		}
		if (!containsOptionSeparator(symbol)) {
			return false;
		}
		final int size = symbol.length();
		if (!DDF_Option.isValid(symbol.charAt(size - 1))) {
			return false;
		}
		return true;
	}

	public static final boolean isIndex(final String symbol) {
		if (symbol == null) {
			return false;
		}
		return symbol.toUpperCase().startsWith(PREFIX_INDEX);
	}

	public static final boolean isForex(final String symbol) {
		if (symbol == null) {
			return false;
		}
		return symbol.toUpperCase().startsWith(PREFIX_FOREX);
	}

	public static final boolean isEquitySector(final String symbol) {
		if (symbol == null) {
			return false;
		}
		return symbol.toUpperCase().startsWith(PREFIX_SECTOR);
	}

	public static final boolean isTest(final String symbol) {
		if (symbol == null) {
			return false;
		}
		return symbol.toUpperCase().startsWith(PREFIX_TEST);
	}

	public static final boolean isEquityCAN(final String symbol) {
		if (symbol == null) {
			return false;
		}
		final String name = symbol.toUpperCase();
		return name.endsWith(SUFFIX_EQUITY_CAN_1)
				|| name.endsWith(SUFFIX_EQUITY_CAN_2);
	}

	public static final String getGroupEquityCAN(final String symbol) {
		final String name = symbol.toUpperCase();
		if (name.endsWith(SUFFIX_EQUITY_CAN_1)) {
			return name.substring(0, name.lastIndexOf(SUFFIX_EQUITY_CAN_1));
		}
		if (name.endsWith(SUFFIX_EQUITY_CAN_2)) {
			return name.substring(0, name.lastIndexOf(SUFFIX_EQUITY_CAN_2));
		}
		return null;
	}

	public static final boolean isEquityLSE(final String symbol) {
		if (symbol == null) {
			return false;
		}
		return symbol.toUpperCase().endsWith(SUFFIX_EQUITY_LSE);
	}

	public static final String getGroupEquityLSE(final String symbolName) {
		final String name = symbolName.toUpperCase();
		return name.substring(0, name.lastIndexOf(SUFFIX_EQUITY_LSE));
	}

	public static final boolean isEquityNSE(final String symbol) {
		if (symbol == null) {
			return false;
		}
		final String name = symbol.toUpperCase();
		return name.toUpperCase().endsWith(SUFFIX_EQUITY_NSE);
	}

	public static final String getGroupEquityNSE(final String symbolName) {
		final String name = symbolName.toUpperCase();
		return name.substring(0, name.lastIndexOf(SUFFIX_EQUITY_NSE));
	}

	public static final boolean isEquityOther(final String symbol) {
		if (symbol == null) {
			return false;
		}
		if (containsDigit(symbol)) {
			// excludes futures, options
			return false;
		}
		if (isTest(symbol)) {
			return false;

		}
		if (isForex(symbol)) {
			return false;

		}
		if (isIndex(symbol)) {
			return false;

		}
		if (isEquityLSE(symbol)) {
			return false;

		}
		if (isEquityCAN(symbol)) {
			return false;

		}
		if (isEquityNSE(symbol)) {
			return false;

		}
		return true;
	}

	public static final boolean isFutureHistorical(final String symbol) {
		if (symbol == null) {
			return false;
		}
		final int size = symbol.length();
		if (size < 3) {
			return false;
		}
		final char digitLow = symbol.charAt(size - 1);
		final char digitHigh = symbol.charAt(size - 2);
		if (!isDigit(digitLow)) {
			return false;
		}
		if (!isDigit(digitHigh)) {
			return false;
		}
		return true;
	}

	public static final String futureNormalFromHistorical(final String symbol) {
		final int size = symbol.length();
		final char digitLow = symbol.charAt(size - 1);
		final String group = symbol.substring(0, size - 2);
		return group + digitLow;
	}

	public static final String futureNormalIntoHistorical(final String symbol) {
		return "";
	}

}
