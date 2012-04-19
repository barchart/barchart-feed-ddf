/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.market.provider;

import com.barchart.feed.base.market.api.MarketDo;
import com.barchart.feed.base.market.api.MarketFactory;
import com.barchart.feed.base.market.provider.MakerBase;
import com.barchart.feed.ddf.market.api.DDF_MarketProvider;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.util.anno.ThreadSafe;

// TODO: Auto-generated Javadoc
/**
 * The Class DDF_MarketService.
 */
@ThreadSafe
public class DDF_MarketService extends MakerBase<DDF_MarketBase> implements
		DDF_MarketProvider {

	/**
	 * @param factory
	 */
	protected DDF_MarketService(final MarketFactory factory) {
		super(factory);
		// TODO Auto-generated constructor stub
	}

	private final DDF_MessageVisitor<Void, MarketDo> visitor = new MapperDDF();

	/**
	 * New instance.
	 * 
	 * @return the DDF market provider
	 */
	public static final DDF_MarketProvider newInstance() {
		return new DDF_MarketService(new MarketFactory() {

			@Override
			public MarketDo newMarket() {
				return new VarMarketDDF();
			}

		});
	}

	@Override
	protected void make(final DDF_MarketBase message, final MarketDo market) {

		message.accept(visitor, market);

	}

}
