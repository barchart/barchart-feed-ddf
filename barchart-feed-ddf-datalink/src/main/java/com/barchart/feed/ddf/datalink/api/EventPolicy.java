/**
 * 
 */
package com.barchart.feed.ddf.datalink.api;

/**
 * A callback action to be fired on a specific event. Registered with a feed
 * client along with an event type.
 * 
 */
public interface EventPolicy {

	public void newEvent();

}
