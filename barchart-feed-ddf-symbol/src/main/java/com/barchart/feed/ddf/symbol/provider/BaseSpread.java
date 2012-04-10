/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.provider;

import java.util.List;

import com.barchart.feed.ddf.symbol.api.DDF_SymbolExpiration;
import com.barchart.feed.ddf.symbol.api.DDF_SymbolSpread;

//TODO

/*package*/abstract class BaseSpread<T extends DDF_SymbolExpiration> extends
		BaseExpiration implements DDF_SymbolSpread<T> {

	protected List<T> legList;

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.symbol.api.DDF_SymbolSpread#getLegList()
	 */
	public List<T> getLegList() {
		return legList;
	}

}
