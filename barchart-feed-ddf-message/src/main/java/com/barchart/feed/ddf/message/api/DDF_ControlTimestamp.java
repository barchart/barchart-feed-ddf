/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.util.anno.NotMutable;
import com.barchart.util.values.api.TimeValue;

/**
 * represents ddf feed server time stamp or heart beat message.
 */
@NotMutable
public interface DDF_ControlTimestamp extends DDF_ControlBase {

	TimeValue getStampUTC();

}