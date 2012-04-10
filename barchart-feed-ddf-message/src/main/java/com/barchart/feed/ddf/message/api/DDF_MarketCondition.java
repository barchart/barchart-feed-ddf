/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.feed.ddf.message.enums.DDF_Condition;
import com.barchart.util.anno.NotMutable;
import com.barchart.util.anno.NotYetImplemented;

/**
 * represents ddf feed market condition.
 */
@NotMutable
@NotYetImplemented
public interface DDF_MarketCondition extends DDF_MarketBase {

	DDF_Condition getCondition();

}
