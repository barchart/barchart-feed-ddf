/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.provider;

import com.barchart.feed.ddf.symbol.api.DDF_SymbolEquity;
import com.barchart.feed.ddf.symbol.enums.DDF_Equity;
import com.barchart.feed.ddf.symbol.enums.DDF_SymbolType;

/*package*/class BaseEquity extends Base implements DDF_SymbolEquity {

	public DDF_SymbolType getType() {
		return DDF_SymbolType.EQUITY;
	}

	protected DDF_Equity equityType;

	public DDF_Equity getEquityType() {
		return equityType;
	}

	@Override
	public String toString() {
		return name;
	}

}
