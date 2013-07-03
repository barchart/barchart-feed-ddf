/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.provider;

import static com.barchart.feed.ddf.symbol.provider.DDF_Symbology.OPTION_SEPARATOR;

import com.barchart.feed.ddf.symbol.api.DDF_SymbolOption;
import com.barchart.feed.ddf.symbol.enums.DDF_Option;
import com.barchart.feed.ddf.symbol.enums.DDF_OptionYear;
import com.barchart.feed.ddf.symbol.enums.DDF_SymbolType;

// TODO: Auto-generated Javadoc
/*package*/class OptionBasic extends BaseExpiration implements
		DDF_SymbolOption {

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.symbol.api.DDF_Symbol#getType()
	 */
	public DDF_SymbolType getType() {
		return DDF_SymbolType.OPTION_BASIC;
	}

	protected DDF_Option optionIndicator;

	protected String strikePrice;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final DDF_OptionYear optionTypeYear = //
		DDF_OptionYear.fromIndiYear(optionIndicator, year);
		return group + month.code + strikePrice + optionTypeYear.code;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.symbol.api.DDF_SymbolOption#getStrike()
	 */
	public String getStrike() {
		return strikePrice;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.symbol.api.DDF_SymbolOption#getIndicator()
	 */
	public DDF_Option getIndicator() {
		return optionIndicator;
	}

}
