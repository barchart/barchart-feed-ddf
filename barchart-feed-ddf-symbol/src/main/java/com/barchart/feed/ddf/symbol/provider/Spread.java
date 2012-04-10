/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.provider;

import com.barchart.feed.ddf.symbol.api.DDF_SymbolFuture;
import com.barchart.feed.ddf.symbol.enums.DDF_SpreadType;
import com.barchart.feed.ddf.symbol.enums.DDF_SymbolType;

// TODO: Auto-generated Javadoc
//TODO

/*package*/class Spread extends BaseSpread<DDF_SymbolFuture> {

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.symbol.api.DDF_Symbol#getType()
	 */
	public DDF_SymbolType getType() {
		return DDF_SymbolType.SPREAD;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.symbol.api.DDF_SymbolSpread#getSpread()
	 */
	@Override
	public DDF_SpreadType getSpread() {
		// TODO Auto-generated method stub
		return null;
	}
}