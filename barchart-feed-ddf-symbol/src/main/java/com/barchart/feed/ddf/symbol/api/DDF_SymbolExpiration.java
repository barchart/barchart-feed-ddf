/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.api;

import com.barchart.feed.ddf.symbol.enums.DDF_ExpireMonth;
import com.barchart.feed.ddf.symbol.enums.DDF_ExpireYear;

/**
 * The Interface DDF_SymbolExpiration.
 */
public interface DDF_SymbolExpiration extends DDF_Symbol {

	DDF_ExpireYear getYear();

	DDF_ExpireMonth getMonth();

}