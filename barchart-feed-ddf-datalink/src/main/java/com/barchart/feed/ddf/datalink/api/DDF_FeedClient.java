/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.api;

import java.util.Set;
import java.util.concurrent.Future;

import com.barchart.feed.ddf.datalink.enums.DDF_FeedEvent;
import com.barchart.util.anno.UsedOnce;

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
public interface DDF_FeedClient extends DDF_FeedClientBase {

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
	Future<Boolean> subscribe(Set<Subscription> subscriptions);

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
	Future<Boolean> subscribe(Subscription subscription);

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
	Future<Boolean> unsubscribe(Set<Subscription> subscriptions);

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
	Future<Boolean> unsubscribe(Subscription subscription);

	/**
	 * Attach single feed state listener to the client.
	 */
	@UsedOnce
	void bindStateListener(DDF_FeedStateListener stateListener);

	/**
	 * 
	 * @param event
	 * @param policy
	 */
	void setPolicy(DDF_FeedEvent event, EventPolicy policy);

}
