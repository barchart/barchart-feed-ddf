package com.barchart.feed.test.replay;

import java.util.Set;
import java.util.concurrent.Future;

import com.barchart.feed.base.sub.Sub;
import com.barchart.feed.base.sub.SubscriptionHandler;
import com.barchart.feed.client.provider.BarchartMarketplace;
import com.barchart.feed.ddf.market.provider.DDF_Marketplace;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;

public class BarchartMarketplaceReplay extends BarchartMarketplace {

	public BarchartMarketplaceReplay() {
		super(DDF_Marketplace.newInstance(new DummySubHandler()));
	}

	public void handleMessage(final DDF_BaseMessage message) {
		if (message instanceof DDF_MarketBase) {
			final DDF_MarketBase marketMessage = (DDF_MarketBase) message;
			maker.make(marketMessage);
		}
	}

	public DDF_Marketplace maker() {
		return maker;
	}

	private static class DummySubHandler implements SubscriptionHandler {

		@Override
		public Future<Boolean> subscribe(final Sub subscription) {
			return null;
		}

		@Override
		public Future<Boolean> subscribe(final Set<Sub> subscriptions) {
			return null;
		}

		@Override
		public Future<Boolean> unsubscribe(final Sub subscription) {
			return null;
		}

		@Override
		public Future<Boolean> unsubscribe(final Set<Sub> subscriptions) {
			return null;
		}

	}

}
