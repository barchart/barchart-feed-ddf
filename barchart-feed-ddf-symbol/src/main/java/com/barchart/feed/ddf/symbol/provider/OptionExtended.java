/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.provider;

import static com.barchart.feed.ddf.symbol.provider.DDF_Symbology.*;

import com.barchart.feed.ddf.symbol.enums.DDF_SymbolType;

/*package*/class OptionExtended extends OptionBasic {

	public DDF_SymbolType getType() {
		return DDF_SymbolType.OPTION_EXTENDED;
	}

	@Override
	public String toString() {
		return group + month.code + year.code + //
				OPTION_SEPARATOR + //
				strikePrice + optionIndicator.code;
	}

}
