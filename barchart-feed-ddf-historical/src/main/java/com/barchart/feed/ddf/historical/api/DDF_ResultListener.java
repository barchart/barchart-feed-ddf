/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.api;

/**
 * The listener interface for receiving DDF_Result events.
 * The class that is interested in processing a DDF_Result
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addDDF_ResultListener<code> method. When
 * the DDF_Result event occurs, that object's appropriate
 * method is invoked.
 *
 * @see DDF_ResultEvent
 */
public interface DDF_ResultListener {

	int PROGRESS_SIZE = 1000;

	void onProgressEvent(int progressCount);

}
