/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.provider;

import com.barchart.feed.ddf.symbol.api.DDF_SymbolFuture;
import com.barchart.feed.ddf.symbol.enums.DDF_ExpireMonth;
import com.barchart.feed.ddf.symbol.enums.DDF_ExpireYear;

// TODO: Auto-generated Javadoc
/*package*/abstract class BaseExpiration extends Base implements
		DDF_SymbolFuture {

	protected DDF_ExpireMonth month;

	protected DDF_ExpireYear year;

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.symbol.api.DDF_SymbolExpiration#getMonth()
	 */
	public DDF_ExpireMonth getMonth() {
		return month;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.symbol.api.DDF_SymbolExpiration#getYear()
	 */
	public DDF_ExpireYear getYear() {
		return year;
	}

}
