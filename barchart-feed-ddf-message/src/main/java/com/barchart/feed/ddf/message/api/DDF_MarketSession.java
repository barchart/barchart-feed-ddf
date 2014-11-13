/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.base.values.api.TimeValue;
import com.barchart.feed.ddf.message.enums.DDF_Indicator;
import com.barchart.util.common.anno.NotMutable;

/**
 * represents xml session; "extended snapshot info; part of quote".
 */
@NotMutable
public interface DDF_MarketSession extends DDF_MarketSnapshot {

	/**
	 * last trade size today
	 */
	SizeValue getSizeLast();

	/**
	 * last trade time today
	 */
	TimeValue getTimeLast();

	/**
	 * current vs previous
	 */
	DDF_Indicator getIndicator();
	
}