/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/**
 * 
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.feed.base.values.api.SizeValue;

/**
 * @author g-litchfield
 * 
 */
public interface DDF_Prior_IndividCmdy extends DDF_MarketBase {

	SizeValue getSizeVolume();

	SizeValue getSizeOpenInterest();

}
