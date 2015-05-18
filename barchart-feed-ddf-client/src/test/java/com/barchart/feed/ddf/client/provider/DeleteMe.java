package com.barchart.feed.ddf.client.provider;

import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.connection.TimestampListener;
import com.barchart.feed.client.provider.BarchartMarketplace;
import com.barchart.util.value.api.Time;

public class DeleteMe {
	
	private static long lastSeenTime = 0;
	private static final long HEARTBEAT_TIMEOUT = 30 * 1000; /* 30 sec */
	private static final long HEARTBEAT_CHECKER_TIMEOUT = 5 * 1000; /* 5 sec */
	
	private static Marketplace market;
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("username");
		final String password = System.getProperty("password");
		
		market = BarchartMarketplace.builder()
				.username(username)
				.password(password)
				.build();
		
		/* 
		 * Bind a time stamp listener that updates the last seen System time every
		 * time the market sees a time stamp from the feed.
		 */
		market.bindTimestampListener(new TimestampListener() {

			@Override
			public void listen(final Time timestamp) {
				
				/* For this use case, we don't need the actual time stamp */
				lastSeenTime = System.currentTimeMillis();
				
				System.out.println("Last seen time = " + lastSeenTime);
			}
			
		});
		
		market.startup();
		
		/* 
		 * Create a thread which will periodically run, comparing the current time
		 * to the time last seen by the time stamp listener 
		 */
		final Thread timeCheckerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				
				try {
				
					while(true) {
						
						/* Pause thread, periodically check for timeouts */
						Thread.sleep(HEARTBEAT_CHECKER_TIMEOUT);
						
						final long currentTime = System.currentTimeMillis();
						
						/* Check for timeout */
						if(currentTime - lastSeenTime > HEARTBEAT_TIMEOUT) {
							
							/* Execute some response to detected timeout */
							timeoutResponse();
						}
					
					}
				
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		});
		
		timeCheckerThread.setDaemon(true);
		timeCheckerThread.start();
		
		/* Pauses main thread, feed will run in background as well as checker thread */
		Thread.sleep(60 * 1000); 
		
		/* Shut everything down */
		timeCheckerThread.interrupt();
		market.shutdown();
		
	}
	
	private static void timeoutResponse() throws InterruptedException {
		
		System.out.println("Do something now that I've seen a time out");
		
	}

}
