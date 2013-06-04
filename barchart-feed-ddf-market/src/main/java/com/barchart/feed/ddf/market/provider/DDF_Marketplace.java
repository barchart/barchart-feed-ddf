package com.barchart.feed.ddf.market.provider;

import com.barchart.feed.api.AgentBuilder;
import com.barchart.feed.api.connection.SubscriptionHandler;
import com.barchart.feed.api.inst.InstrumentService;
import com.barchart.feed.base.market.api.MarketDo;
import com.barchart.feed.base.market.api.MarketFactory;
import com.barchart.feed.base.provider.MarketplaceBase;
import com.barchart.feed.ddf.instrument.provider.InstrumentProviderWrapper;
import com.barchart.feed.ddf.market.api.DDF_MarketProvider;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;

public class DDF_Marketplace extends MarketplaceBase<DDF_MarketBase> implements 
		DDF_MarketProvider, AgentBuilder {

	protected DDF_Marketplace(MarketFactory factory,
			InstrumentService<CharSequence> instLookup,
			SubscriptionHandler handler) {
		super(factory, instLookup, handler);
	}
	
	private final DDF_MessageVisitor<Void, MarketDo> visitor = new MapperDDF();
	
	public static final DDF_Marketplace newInstance(
			final SubscriptionHandler handler) {
		
		return new DDF_Marketplace(new MarketFactory() {

			@Override
			public MarketDo newMarket() {
				return new VarMarketEntityDDF();
			}

		}, new InstrumentProviderWrapper(), handler);
		
	}

	@Override
	protected void make(DDF_MarketBase message, MarketDo market) {
		message.accept(visitor, market);
	}

}
