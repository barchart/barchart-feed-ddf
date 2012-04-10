/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.api;

import com.barchart.feed.ddf.message.enums.DDF_Session;

/**
 * The Interface DDF_EntryTick.
 */
public interface DDF_EntryTick extends DDF_Entry {

	DDF_Session getSession();

	//

	long priceTradeMantissa();

	//

	long sizeTrade();

}
