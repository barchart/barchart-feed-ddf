/**
 * 
 */
package com.barchart.feed.ddf.datalink.api;

import com.barchart.feed.ddf.datalink.enums.DDF_FeedState;

/**
 * @author g-litchfield
 * 
 */
public interface DDF_FeedStateListener {

	public void stateUpdate(DDF_FeedState state);

}
