package com.barchart.feed.ddf.datalink.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.barchart.feed.api.connection.Connection.Monitor;
import com.barchart.feed.api.connection.Connection.State;
import com.barchart.feed.api.consumer.MetadataService;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.bar.api.MarketDoBar;
import com.barchart.feed.base.bar.enums.MarketBarType;
import com.barchart.feed.base.book.api.MarketDoBookEntry;
import com.barchart.feed.base.cuvol.api.MarketDoCuvolEntry;
import com.barchart.feed.base.market.api.MarketDo;
import com.barchart.feed.base.market.api.MarketFactory;
import com.barchart.feed.base.provider.MarketProviderBase;
import com.barchart.feed.base.provider.VarMarket;
import com.barchart.feed.base.state.enums.MarketStateEntry;
import com.barchart.feed.base.sub.SubscriptionHandler;
import com.barchart.feed.base.trade.enums.MarketTradeSequencing;
import com.barchart.feed.base.trade.enums.MarketTradeSession;
import com.barchart.feed.base.trade.enums.MarketTradeType;
import com.barchart.feed.base.values.api.BooleanValue;
import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.base.values.api.TimeValue;
import com.barchart.feed.ddf.datalink.api.FeedClient;
import com.barchart.feed.ddf.datalink.api.FeedEvent;
import com.barchart.feed.ddf.datalink.provider.util.DummyFuture;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;

public class TestableMarketService extends MarketProviderBase<DDF_MarketBase> {

	public TestableMarketService(
			MetadataService metaService, 
			SubscriptionHandler subHandler) {
		
		super(new MockMarketFactory(), metaService, subHandler);
	}

	@Override
	protected void make(final DDF_MarketBase message, final MarketDo market) {
		/* Do nothing */
	}
	
	public static class TestableFeedClient implements FeedClient {

		private String lastWrite = "NULL";
		private final List<Monitor> listeners = new ArrayList<Monitor>();
		
		@Override
		public Future<Boolean> write(final String message) {
			
			lastWrite = message;
			
			return new DummyFuture();
		}
		
		public String getLastWrite() {
			final String temp = new String(lastWrite);
			lastWrite = "NULL";
			return temp;
		}
		
		public void setOnline() {
			for(final Monitor m : listeners) {
				m.handle(State.CONNECTED, null);
			}
		}
		
		@Override
		public void startup() {
			
		}

		@Override
		public void startUpProxy() {
			
		}

		@Override
		public void shutdown() {
			
		}

		@Override
		public void bindStateListener(final Monitor stateListener) {
			
			System.out.println("State Listener Bound");
			
			listeners.add(stateListener);
		}

		@Override
		public void bindMessageListener(final DDF_MessageListener msgListener) {
			
		}

		@Override
		public void setPolicy(final FeedEvent event, final EventPolicy policy) {
			
		}

	}
	
	public static class MockMarketFactory implements MarketFactory {

		@Override
		public MarketDo newMarket(Instrument instrument) {
			return new VarMarket(instrument) {

				@Override
				public void setChange(Component c) {
					
				}

				@Override
				public void clearChanges() {
					
				}

				@Override
				public void setInstrument(Instrument symbol) {
					
				}

				@Override
				public void setBookUpdate(MarketDoBookEntry entry,
						TimeValue time) {
					
				}

				@Override
				public void setBookSnapshot(MarketDoBookEntry[] entries,
						TimeValue time) {
					
				}

				@Override
				public void setCuvolUpdate(MarketDoCuvolEntry entry,
						TimeValue time) {
					
				}

				@Override
				public void setCuvolSnapshot(MarketDoCuvolEntry[] entries,
						TimeValue time) {
					
				}

				@Override
				public void setBar(MarketBarType type, MarketDoBar bar) {
					
				}

				@Override
				public void setSnapshot(TimeValue tradeDate, PriceValue open,
						PriceValue high, PriceValue low, PriceValue close,
						PriceValue settle, PriceValue previousSettle,
						SizeValue volume, SizeValue interest, PriceValue vwap,
						BooleanValue isSettled, TimeValue barTime) {
					
				}

				@Override
				public void setTrade(MarketTradeType type,
						MarketTradeSession session,
						MarketTradeSequencing sequencing, PriceValue price,
						SizeValue size, TimeValue time, TimeValue date) {
					
				}

				@Override
				public void setState(MarketStateEntry entry, boolean isOn) {
					
				}

				@Override
				public void fireCallbacks() {
					
				}

				@Override
				public void refresh() {
					// TODO Auto-generated method stub
					
				}
				
			};
		}
		
	}

}
