/**
 * 
 */
package com.barchart.feed.ddf.datalink.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.api.DDF_FeedClient;
import com.barchart.feed.ddf.datalink.api.DDF_MessageListener;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;

/**
 * @author g-litchfield
 * 
 */
public class TestLogins {

	private static final Logger log = LoggerFactory.getLogger(TestLogins.class);

	public static void main(final String[] args) throws Exception {

		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		final DDF_FeedClient client = DDF_FeedClientFactory.newInstance(
				username, password);

		final DDF_MessageListener handler = new DDF_MessageListener() {

			@Override
			public void handleMessage(final DDF_BaseMessage message) {

			}
		};

		client.bindMessageListener(handler);

		// Initial login
		client.login();

		while (true) {

		}
	}

	private static void sleep(final int mills) {
		try {
			Thread.sleep(mills);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

}
