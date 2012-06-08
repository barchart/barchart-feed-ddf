/**
 * 
 */
package com.barchart.feed.ddf.client.api;

import java.util.List;

import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.ddf.datalink.api.DDF_FeedStateListener;
import com.barchart.util.values.api.Value;

/**
 * 
 * The core DDF class which encapsulates all the core functionality a new user
 * will need to get started.
 * <p>
 * Instances are created using the factory class DDF_ClientFactory and require a
 * valid user name and password. Optional parameters include specifying the
 * transport protocol and providing an executor.
 * <p>
 * The price feed is started and stopped using the startup() and shutdown()
 * methods. Note that these are non-blocking calls. Applications requiring
 * actions upon successful login should instantiate and bind a
 * FeedStatusListener to the client.
 * <p>
 * 
 * 
 */
public interface DDF_Client {

	/**
	 * Starts the data feed asynchronously. Notification of login success is
	 * reported by FeedStateListeners which are bound to this object.
	 */
	void startup();

	/**
	 * Shuts down the data feed and clears all registered market takers.
	 */
	void shutdown();

	/**
	 * Applications which need to react to the connectivity state of the feed
	 * Instantiate a DDF_FeedStateListener and bind it to the client.
	 * 
	 * @param listener
	 *            The listener to be bound.
	 */
	void bindFeedStateListener(final DDF_FeedStateListener listener);

	/**
	 * Determines if a market taker was successfully registered.
	 * 
	 * @param taker
	 *            The market taker.
	 * @return True if the taker has been registered.
	 */
	boolean isTakerRegistered(final MarketTaker<?> taker);

	/**
	 * Adds a market taker to the client. This performs instrument registration
	 * with the market maker as well as subscribing to the required data from
	 * the feed.
	 * 
	 * @param taker
	 *            The market taker to be added.
	 * @return True if the taker was successfully added.
	 */
	<V extends Value<V>> boolean addTaker(MarketTaker<V> taker);

	/**
	 * Removes a market taker from the client. If no other takers require its
	 * instruments, they are unsubscribed from the feed.
	 * 
	 * @param taker
	 *            THe market taker to be removed.
	 * @return True if the taker was successfully removed.
	 */
	<V extends Value<V>> boolean removeTaker(MarketTaker<V> taker);

	//

	/**
	 * Retrieves the instrument object denoted by symbol. The local instrument
	 * cache will be checked first. If the instrument is not stored locally, a
	 * remote call to the instrument service is made.
	 * 
	 * @return NULL_INSTRUMENT if the symbol is not resolved.
	 */
	MarketInstrument lookup(String symbol);

	/**
	 * Retrieves a list of instrument objects denoted by symbols provided. The
	 * local instrument cache will be checked first. If any instruments are not
	 * stored locally, a remote call to the instrument service is made.
	 * 
	 * @return An empty list if no symbols can be resolved.
	 */
	List<MarketInstrument> lookup(List<String> symbolList);

	//

	/**
	 * Makes a query to the market maker for a snapshot of a market field for a
	 * specific instrument. The returned values are frozen and disconnected from
	 * live market.
	 * 
	 * @return NULL_VALUE for all fields if market is not present.
	 */
	<S extends MarketInstrument, V extends Value<V>> V //
			take(S instrument, MarketField<V> field);

}
