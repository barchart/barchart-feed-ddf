/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.market.provider;

import com.barchart.feed.base.provider.market.provider.MakerBase;
import com.barchart.feed.base.provider.market.provider.MarketDo;
import com.barchart.feed.base.provider.market.provider.MarketType;
import com.barchart.feed.ddf.market.api.DDF_MarketProvider;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.util.anno.ThreadSafe;

@ThreadSafe
public class DDF_MarketService extends MakerBase<DDF_MarketBase> implements
		DDF_MarketProvider {

	private final DDF_MessageVisitor<Void, MarketDo> visitor = new MapperDDF();

	private DDF_MarketService() {
		super(MarketType.DDF);
	}

	public static final DDF_MarketProvider newInstance() {
		return new DDF_MarketService();
	}

	@Override
	protected void make(final DDF_MarketBase message, final MarketDo market) {

		message.accept(visitor, market);

	}

}
