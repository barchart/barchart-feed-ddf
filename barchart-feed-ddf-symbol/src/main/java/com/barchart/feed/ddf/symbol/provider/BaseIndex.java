/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.provider;

import com.barchart.feed.ddf.symbol.api.DDF_SymbolIndex;
import com.barchart.feed.ddf.symbol.enums.DDF_SymbolType;

/*package*/class BaseIndex extends Base implements DDF_SymbolIndex {

	public DDF_SymbolType getType() {
		return DDF_SymbolType.INDEX;
	}

	@Override
	public String toString() {
		return name;
	}

}
