package com.barchart.feed.ddf.market.provider;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.market.api.MarketDo;
import com.barchart.feed.base.market.api.MarketFactory;
import com.barchart.feed.base.provider.MarketProviderBase;
import com.barchart.feed.ddf.market.api.DDF_MarketProvider;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;

public class DDF_NewMarketProvider extends MarketProviderBase<DDF_MarketBase> 
		implements DDF_MarketProvider {

		
	protected DDF_NewMarketProvider(final MarketFactory factory) {
		super(factory);
	}
			
	private final DDF_MessageVisitor<Void, MarketDo> visitor = new MapperDDF();
	
	public static final DDF_MarketProvider newInstance() {
		
		return new DDF_NewMarketProvider(new MarketFactory() {
			
			@Override
			public MarketDo newMarket(final Instrument instrument) {
				return new VarMarketDDF(instrument);
			}
			
		});
	}

	@Override
	protected void make(final DDF_MarketBase message, final MarketDo market) {
		message.accept(visitor, market);
	}
	
}
