/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.provider;

import com.barchart.feed.ddf.symbol.api.DDF_SymbolFuture;
import com.barchart.feed.ddf.symbol.enums.DDF_SymbolType;

// TODO: Auto-generated Javadoc
/*package*/class BaseFuture extends BaseExpiration implements DDF_SymbolFuture {

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.symbol.api.DDF_Symbol#getType()
	 */
	public DDF_SymbolType getType() {
		return DDF_SymbolType.FUTURE;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return group + month.code + year.code;
	}

}
