package com.barchart.feed.ddf.util;

import java.util.concurrent.atomic.AtomicLong;

public class FeedClock {
	
	private final AtomicLong time = new AtomicLong(0);
	
	public long millis() {
		return time.get();
	}
	
	public void set(final long t) {
		time.set(t);
	}
	

}
