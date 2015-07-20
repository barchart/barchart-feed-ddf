package com.barchart.feed.ddf.client.provider;

import com.barchart.feed.api.Marketplace;
import com.barchart.feed.client.provider.BarchartMarketplace;
import com.barchart.feed.client.provider.BarchartMarketplace.FeedType;

public class TCPListenerExample {

	public static void main(final String[] args) throws Exception {
		
		final Marketplace market = BarchartMarketplace.builder()
				.feedType(FeedType.LISTENER_TCP)
				.port(12345)
				.build();
		
		
	}
	
}
