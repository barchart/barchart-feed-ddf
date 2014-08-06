package com.barchart.feed.ddf.util;

/**
 * Static clock for all of DDF
 */
public final class ClockDDF {
	
	private ClockDDF() {
		
	}
	
	public static final FeedClock clock = new FeedClock();
	
	public static void reset() {
		clock.set(0);
	}

}
