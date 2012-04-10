/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.api;

import com.barchart.feed.ddf.symbol.enums.DDF_Option;

public interface DDF_SymbolOption extends DDF_SymbolExpiration {

	String getStrike();

	DDF_Option getIndicator();

}
