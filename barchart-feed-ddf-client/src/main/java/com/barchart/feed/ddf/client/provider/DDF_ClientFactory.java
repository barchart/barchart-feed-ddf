/**
 * 
 */
package com.barchart.feed.ddf.client.provider;

import java.util.concurrent.Executor;

import com.barchart.feed.ddf.client.api.DDF_Client;
import com.barchart.feed.ddf.datalink.enums.TP;

/**
 * Factory for building DDF_Client instances
 */
public class DDF_ClientFactory {

	/**
	 * Returns a DDF_Client instance using a TCP data link and a default
	 * executor.
	 * 
	 * @param username
	 * @param password
	 * @return A default configured DDF_Client ready to rock.
	 */
	public static DDF_Client makeClient(final String username,
			final String password) {

		return new ClientDDF(TP.TCP, username, password, null);
	}

	/**
	 * Returns a DDF_Client instance using the specified transport protocol for
	 * the data feed. Uses a default executor.
	 * 
	 * @param tp
	 *            The desired transport protocol to be used by the data feed.
	 * @param username
	 * @param password
	 * @return A user configured DDF_Client.
	 */
	public static DDF_Client makeClient(final TP tp, final String username,
			final String password) {

		return new ClientDDF(tp, username, password, null);
	}

	/**
	 * Returns a DDF_Client instance using the specified transport protocol for
	 * the data feed and a provided executor. The executor is used both for
	 * asynchronous task threads as well as by Netty for boss and worker
	 * threads.
	 * 
	 * @param tp
	 * @param username
	 * @param password
	 * @param executor
	 * @return A user configured DDF_Client.
	 */
	public static DDF_Client makeClient(final TP tp, final String username,
			final String password, final Executor executor) {

		return new ClientDDF(tp, username, password, executor);

	}

}
