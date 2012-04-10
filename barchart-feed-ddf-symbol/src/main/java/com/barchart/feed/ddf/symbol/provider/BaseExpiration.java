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

/*package*/abstract class BaseExpiration extends Base implements
		DDF_SymbolFuture {

	protected DDF_ExpireMonth month;

	protected DDF_ExpireYear year;

	public DDF_ExpireMonth getMonth() {
		return month;
	}

	public DDF_ExpireYear getYear() {
		return year;
	}

}
