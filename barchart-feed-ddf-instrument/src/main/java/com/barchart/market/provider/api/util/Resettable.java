package com.barchart.market.provider.api.util;

import aQute.bnd.annotation.ProviderType;

/**
 * Reset mutable object to a known state.
 */
@ProviderType
public interface Resettable {
	
	/**
	 * Reset mutable object to a known state.
	 */
	void reset();

}
