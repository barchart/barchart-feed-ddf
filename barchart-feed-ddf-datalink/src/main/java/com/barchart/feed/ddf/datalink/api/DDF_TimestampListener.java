/**
 * 
 */
package com.barchart.feed.ddf.datalink.api;

import com.barchart.util.values.api.TimeValue;

/**
 * 
 *
 */
public interface DDF_TimestampListener {

	public void handleTimestamp(TimeValue timestamp);

}
