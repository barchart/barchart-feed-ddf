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
 * The penultimate DDF class which encapsulates all the core functionality a new
 * user will need to get started.
 * <p>
 * Instances are created using the factory class DDF_ClientFactory and require a
 * valid username and password. Optional parameters include specifying the
 * transport protocol and providing an executor.
 * <p>
 * The price feed is started and stopped using the startup() and shutdown()
 * methods. Note that these are non-blocking calls. Applications requiring
 * actions upon sucessful login should instanciate and bind a FeedStatusListener
 * to the client.
 * <p>
 * 
 * 
 */
public interface DDF_Client {

	/**
	 * Determines if a market taker was sucessfully registered.
	 * 
	 * @param taker
	 *            The market taker.
	 * @return True if the taker has been registered.
	 */
	boolean isRegistered(MarketTaker<?> taker);

	/**
	 * Starts the data feed asynchronously. Notification of login success is
	 * reported by FeedStateListeners which are bound to this object.
	 */
	void startup();

	/**
	 * Shuts down the data feed.
	 */
	void shutdown();

	/**
	 * Applications which need to react to the conectivity state of the feed
	 * instanciate a DDF_FeedStateListener and bind it to the client.
	 * 
	 * @param listener
	 *            The listener to be bound.
	 */
	void bindFeedStateListener(final DDF_FeedStateListener listener);

	/**
	 * 
	 * 
	 * @param taker
	 * @return
	 */
	boolean isTakerRegistered(final MarketTaker<?> taker);

	/**
	 * add taker; do instrument registration
	 */
	<V extends Value<V>> boolean addTaker(MarketTaker<V> taker);

	/**
	 * remove taker; do instrument un-registration
	 */
	<V extends Value<V>> boolean removeTaker(MarketTaker<V> taker);

	//

	/**
	 * locate instrument from remote symbol resolution service; uses local
	 * instrument cache;
	 * 
	 * @return NULL_INSTRUMENT if symbol not resolved;
	 */
	MarketInstrument lookup(String symbol);

	/**
	 * locate instrument from remote symbol resolution service; uses local
	 * instrument cache;
	 * 
	 * @return empty list, if no symbols resolved;
	 */
	List<MarketInstrument> lookup(List<String> symbolList);

	//

	/**
	 * obtain market field value snapshot; returned values are frozen and
	 * disconnected from live market;
	 * 
	 * @return NULL_VALUE for all fields if market is not present
	 */
	<S extends MarketInstrument, V extends Value<V>> V //
			take(S instrument, MarketField<V> field);

}
