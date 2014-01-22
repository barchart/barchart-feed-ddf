package com.barchart.feed.test.replay;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.barchart.feed.base.sub.Sub;
import com.barchart.feed.base.sub.SubscriptionHandler;
import com.barchart.feed.client.provider.BarchartMarketplace;
import com.barchart.feed.ddf.market.provider.DDF_Marketplace;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;

public class BarchartMarketplaceReplay extends BarchartMarketplace {

	public BarchartMarketplaceReplay() {
		super(DDF_Marketplace.newInstance(new DummySubHandler()),
				new InlineExecutorService());
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

	private static class InlineExecutorService extends AbstractExecutorService {

		private boolean shutdown = false;

		@Override
		public void shutdown() {
			shutdown = true;
		}

		@Override
		public List<Runnable> shutdownNow() {
			shutdown = true;
			return Collections.emptyList();
		}

		@Override
		public boolean isShutdown() {
			return shutdown;
		}

		@Override
		public boolean isTerminated() {
			return shutdown;
		}

		@Override
		public boolean awaitTermination(final long timeout, final TimeUnit unit)
				throws InterruptedException {
			shutdown();
			return true;
		}

		@Override
		public void execute(final Runnable command) {
			if (shutdown) {
				throw new IllegalStateException("Executor is shutdown");
			}
			command.run();
		}

	}

}
