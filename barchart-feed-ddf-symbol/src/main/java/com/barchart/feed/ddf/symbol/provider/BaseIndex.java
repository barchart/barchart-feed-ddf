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

// TODO: Auto-generated Javadoc
/*package*/class BaseIndex extends Base implements DDF_SymbolIndex {

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.symbol.api.DDF_Symbol#getType()
	 */
	public DDF_SymbolType getType() {
		return DDF_SymbolType.INDEX;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

}
