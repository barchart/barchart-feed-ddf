/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.api;

public interface DDF_ResultListener {

	int PROGRESS_SIZE = 1000;

	void onProgressEvent(int progressCount);

}
