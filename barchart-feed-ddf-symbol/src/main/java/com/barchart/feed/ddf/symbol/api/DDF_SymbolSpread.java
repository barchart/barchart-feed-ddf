/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.api;

import java.util.List;

import com.barchart.feed.ddf.symbol.enums.DDF_SpreadType;

public interface DDF_SymbolSpread<T extends DDF_SymbolExpiration> extends
		DDF_SymbolExpiration {

	DDF_SpreadType getSpread();

	List<T> getLegList();

}
