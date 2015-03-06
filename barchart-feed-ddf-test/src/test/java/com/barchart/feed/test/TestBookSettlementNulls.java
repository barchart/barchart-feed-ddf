package com.barchart.feed.test;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.test.replay.BarchartMarketplaceReplay;
import com.barchart.feed.test.replay.FeedReplay;

public class TestBookSettlementNulls {
	
	
	public static void main(final String[] args) throws Exception {

		final BarchartMarketplaceReplay marketplace = new BarchartMarketplaceReplay();
		
		final FeedReplay replay = FeedReplay.builder()
				.source(ClassLoader.getSystemResource("ZCH15/ZCH15.txt"))
				.start("2014-10-24 00:00:00")
				.end("2014-10-25 00:00:00")
				.symbols("ZCH15")
				.build(marketplace.maker());
		
		marketplace.subscribeBook(new MarketObserver<Book>(){

			@Override
			public void onNext(final Book book) {
				
				System.out.println(book);
				
			}
			
		}, "ZCH15");
		
		
		replay.run();
		
		Thread.sleep(1000000);
		
	}

}
