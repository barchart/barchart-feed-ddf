/**
 * 
 */
package com.barchart.feed.ddf.client.provider;

import java.util.concurrent.Executor;

import com.barchart.feed.ddf.client.api.DDF_Client;
import com.barchart.feed.ddf.datalink.enums.TP;

/**
 * Factory for building DDF_Client instances
 * 
 */
public class DDF_ClientFactory {

	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public static DDF_Client makeClient(final String username,
			final String password) {

		return new ClientDDF(TP.TCP, username, password, null);
	}

	/**
	 * 
	 * @param tp
	 * @param username
	 * @param password
	 * @return
	 */
	public static DDF_Client makeClient(final TP tp, final String username,
			final String password) {

		return new ClientDDF(tp, username, password, null);
	}

	/**
	 * 
	 * @param tp
	 * @param username
	 * @param password
	 * @param executor
	 * @return
	 */
	public static DDF_Client makeClient(final TP tp, final String username,
			final String password, final Executor executor) {

		return new ClientDDF(tp, username, password, executor);

	}

}
