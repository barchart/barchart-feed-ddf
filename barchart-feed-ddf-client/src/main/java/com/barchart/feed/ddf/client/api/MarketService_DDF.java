/**
 * 
 */
package com.barchart.feed.ddf.client.api;

import java.util.List;

import com.barchart.feed.base.instrument.values.MarketInstrument;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.util.values.api.Value;

public interface MarketService_DDF {

	boolean isRegistered(MarketTaker<?> taker);

	/**
	 * add taker; do instrument registration
	 */
	<V extends Value<V>> boolean add(MarketTaker<V> taker);

	/**
	 * re-register same taker with new field, new events, new instruments;
	 */

	<V extends Value<V>> boolean update(MarketTaker<V> taker);

	/**
	 * remove taker; do instrument un-registration
	 */
	<V extends Value<V>> boolean remove(MarketTaker<V> taker);

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
