/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/**
 * 
 */
package com.barchart.feed.ddf.datalink.api;

import java.util.Set;
import java.util.concurrent.Future;

import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.base.sub.Sub;
import com.barchart.feed.base.sub.SubscriptionHandler;
import com.barchart.feed.ddf.datalink.enums.DDF_FeedEvent;
import com.barchart.util.common.anno.UsedOnce;

/**
 * Base interface for DDF feed clients. Classes directly implementing this
 * interface should be connectionless listeners. Feeds requiring 2 way
 * communication and a managed connection should implement DDF_FeedClient.
 */
public interface DDF_FeedClientBase extends SubscriptionHandler {

	/**
	 * Binds the feed client to a port or other data source and begins
	 * listening.
	 */
	void startup();
	
	void startUpProxy();

	/**
	 * Stops listening to the data source.
	 */
	void shutdown();

	/**
	 * Attaches a message listener to the client for data consumption.
	 */
	@UsedOnce
	void bindMessageListener(DDF_MessageListener msgListener);

	/**
	 * Attach single feed state listener to the client.
	 */
	@UsedOnce
	void bindStateListener(Connection.Monitor stateListener);
	
	/**
	 * Handles multiple subscription requests.
	 * <p>
	 * If the client already has a subscription for any instrument of the set
	 * then this will overwrite it.
	 * <p>
	 * If called while the client is offline, registers the subscriptions and
	 * returns a future which immediately succeeds.
	 * 
	 * @param subscription
	 *            The set of subscriptions to subscribe.
	 * @return A Future which returns true if successful.
	 */
	@Override
	Future<Boolean> subscribe(Set<Sub> subscriptions);

	/**
	 * Handles a subscription request.
	 * <p>
	 * If the client already has a subscription for the instrument then this
	 * will overwrite it.
	 * <p>
	 * If called while the client is offline, registers the subscription and
	 * returns a future which immediately succeeds.
	 * 
	 * @param subscription
	 *            The subscription to subscribe.
	 * @return A Future which returns true if successful.
	 */
	@Override
	Future<Boolean> subscribe(Sub subscription);

	/**
	 * Handles multiple unsubscription requests.
	 * <p>
	 * If called while the client is offline, unregisters the subscriptions and
	 * returns a future which immediately succeeds.
	 * 
	 * @param subscription
	 *            The set of subscriptions to unsubscribe.
	 * @return A Future which returns true if successful.
	 */
	@Override
	Future<Boolean> unsubscribe(Set<Sub> subscriptions);

	/**
	 * Handles an unsubscription request.
	 * <p>
	 * If called while the client is offline, unregisters the subscription and
	 * returns a future which immediately succeeds.
	 * 
	 * @param subscription
	 *            The subscription to unsubscribe.
	 * @return A Future which returns true if successful.
	 */
	@Override
	Future<Boolean> unsubscribe(Sub subscription);

	/**
	 * Sets the event policy for a specific feed event. Default policies are set
	 * by the constructor to handle disconnects and login success.
	 * 
	 * @param event
	 *            The feed event on which to enact the policy.
	 * @param policy
	 *            The even policy to register.
	 */
	void setPolicy(DDF_FeedEvent event, EventPolicy policy);

}
