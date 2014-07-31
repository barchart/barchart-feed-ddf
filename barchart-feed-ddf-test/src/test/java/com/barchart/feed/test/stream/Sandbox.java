package com.barchart.feed.test.stream;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.client.provider.BarchartMarketplace;

public class Sandbox {

	public static void main(final String... args) throws Exception {

		final Marketplace marketplace = BarchartMarketplace.builder().username("jongsma").password("pass").build();

		marketplace.startup();

		marketplace.subscribe(Book.class, new MarketObserver<Book>() {

			@Override
			public synchronized void onNext(final Book b) {
				System.out.println(b.instrument().symbol() + ": "
						+ b.top().bid().price().asDouble() + "/"
						+ b.top().bid().price().asDouble());
			}

		}, "ESZ14");

		while (true) {
			Thread.sleep(10000);
		}

	}

}
