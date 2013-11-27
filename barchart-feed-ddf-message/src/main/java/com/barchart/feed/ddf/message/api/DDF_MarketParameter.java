/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.ddf.message.enums.DDF_ParamType;
import com.barchart.util.anno.NotMutable;

/**
 * represents ddf feed market parameter message; carries additional price or
 * size value, depending on parameter type;.
 */
@NotMutable
public interface DDF_MarketParameter extends DDF_MarketBase {

	DDF_ParamType getParamType();

	PriceValue getAsPrice();

	SizeValue getAsSize();

}