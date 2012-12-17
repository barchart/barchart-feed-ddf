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
import com.barchart.feed.base.provider.MakerBaseAllMarkets;
import com.barchart.feed.ddf.market.api.DDF_MarketProvider;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;

public class DDF_MarketServiceAllMarkets extends MakerBaseAllMarkets<DDF_MarketBase> implements
		DDF_MarketProvider {

	protected DDF_MarketServiceAllMarkets(final MarketFactory factory) {
		super(factory);
	}
	
	private final DDF_MessageVisitor<Void, MarketDo> visitor = new MapperDDF();
	
	@Override
	protected void make(final DDF_MarketBase message, final MarketDo market) {

		message.accept(visitor, market);

	}
	
	public static final DDF_MarketProvider newInstance() {
		return new DDF_MarketServiceAllMarkets(new MarketFactory() {

			@Override
			public MarketDo newMarket() {
				return new VarMarketDDF();
			}

		});
	}
	
	
	
	
}
