/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package bench;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.connection.ConnectionFuture;
import com.barchart.feed.api.model.data.Book.Entry;
import com.barchart.feed.api.model.data.Book.Top;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.client.provider.BarchartFeed;

/**
 * 
 * you can invoke this utility from command line:
 * 
 * <pre>
 * java - jar barchart-feed-ddf-client-X.Y.Z.jar bench.Receiver TCP 7500 IBM
 * </pre>
 * 
 * arguments = { TCP / UDP , port, instrument1, instrument2, ...}
 * 
 */
public class Receiver {

	// TODO
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String... args) throws Exception {

		if (args == null || args.length < 3) {
			throw new RuntimeException("Bad arguments passed to main");
		}
		
		final Marketplace feed = new BarchartFeed(args[0], args[1]);

		final MarketObserver<Market> observer = new MarketObserver<Market>() {

			@Override
			public void onNext(Market value) {
				
				final StringBuilder sb = new StringBuilder(value.instrument().marketGUID())
					.append(" ")
					.append(" EventTime=")
					.append(value.updated());

				final Top top = value.book().top();
				
				Entry entry = top.ask();
				
				if (!entry.isNull()) {
					sb.append(" ASK TOP").append(" price=")
							.append(entry.price().asDouble())
							.append(" qty=").append(entry.size().asDouble());
				}
				
				entry = top.bid();
				
				if (!entry.isNull()) {
					sb.append(" BID TOP").append(" price=")
							.append(entry.price().asDouble())
							.append(" qty=").append(entry.size().asDouble());
				}

				System.out.println(sb.toString());

				
			}
			
		};
		
		final ConnectionFuture<Marketplace> start = feed.startup();
		
		start.get();
		
		final Agent myAgent = feed.newAgent(Market.class, observer);
		
		for(int i = 2; i < args.length; i++) {
			myAgent.include(args[i]);
		}

		System.in.read();
		feed.shutdown();
		System.exit(0);

	}

}
