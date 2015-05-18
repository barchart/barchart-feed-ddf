/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.api;

import java.util.concurrent.Future;

import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.util.common.anno.UsedOnce;

/**
 * Client responsible for lowest level connectivity to the data server.
 * <p>
 * Implementation should handle all communications from server asynchronously.
 * Permits only nonblocking commands.
 * <p>
 * User is required to bind a StateListener to the client, creating a callback
 * for login state changes for the client.
 * <p>
 */
public interface FeedClient extends FutureWriter {
	
	/**
	 * A callback action to be fired on a specific event. Registered with a feed
	 * client along with an event type.
	 * 
	 */
	public interface EventPolicy {

		public void newEvent(FeedEvent event);

	}
	
	/**
	 * Specifies the handling of feed events and data messages from the server.
	 * <p>
	 * All calls are handles asynchronously by the feed client which the feed
	 * handler is bound to.
	 * 
	 */
	public interface DDF_MessageListener {

		/**
		 * Asynchronous data receipt callback.
		 * 
		 * @param message
		 *            The data message produced by server.
		 */
		void handleMessage(DDF_BaseMessage message);

	}

	/**
	 * transport protocol to be used in the data feed.
	 */
	public enum DDF_Transport {

		/***/
		TCP, //

		/***/
		UDP, //

		/***/
		SCTP, //

		WEBSOCKETS, //

		;

	}
	
	/**
	 * Binds the feed client to a port or other data source and begins listening.
	 */
	void startup();
	
	void startUpProxy();

	/**
	 * Stops listening to the data source.
	 */
	void shutdown();

	/**
	 * Attach a feed state listener to the client.
	 */
	void bindStateListener(Connection.Monitor stateListener);
	
	/**
	 * Attaches a message listener to the client for data consumption.
	 */
	@UsedOnce
	void bindMessageListener(DDF_MessageListener msgListener);
	
	/**
	 * Sets the event policy for a specific feed event. Default policies are set
	 * by the constructor to handle disconnects and login success.
	 * 
	 * @param event
	 *            The feed event on which to enact the policy.
	 * @param policy
	 *            The even policy to register.
	 */
	void setPolicy(FeedEvent event, EventPolicy policy);

	@Override
	Future<Boolean> write(String message);
	
}
