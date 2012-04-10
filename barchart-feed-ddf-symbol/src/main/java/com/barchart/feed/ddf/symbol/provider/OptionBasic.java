/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.provider;

import com.barchart.feed.ddf.symbol.api.DDF_SymbolOption;
import com.barchart.feed.ddf.symbol.enums.DDF_Option;
import com.barchart.feed.ddf.symbol.enums.DDF_OptionYear;
import com.barchart.feed.ddf.symbol.enums.DDF_SymbolType;

/*package*/class OptionBasic extends BaseExpiration implements
		DDF_SymbolOption {

	public DDF_SymbolType getType() {
		return DDF_SymbolType.OPTION_BASIC;
	}

	protected DDF_Option optionIndicator;

	protected String strikePrice;

	@Override
	public String toString() {
		final DDF_OptionYear optionTypeYear = //
		DDF_OptionYear.fromIndiYear(optionIndicator, year);
		return group + month.code + strikePrice + optionTypeYear.code;
	}

	public String getStrike() {
		return strikePrice;
	}

	public DDF_Option getIndicator() {
		return optionIndicator;
	}

}
