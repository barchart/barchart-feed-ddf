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

public interface DDF_Client {

	/**
	 * 
	 * @param taker
	 * @return
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
	 * 
	 * @param listener
	 */
	void bindFeedStateListener(final DDF_FeedStateListener listener);

	/**
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
