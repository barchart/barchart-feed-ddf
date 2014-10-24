package com.barchart.feed.ddf.market.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.market.api.MarketDo;
import com.barchart.feed.base.market.api.MarketFactory;
import com.barchart.feed.base.provider.MarketProviderBase;
import com.barchart.feed.base.sub.SubscriptionHandler;
import com.barchart.feed.ddf.instrument.provider.DDF_MetadataServiceWrapper;
import com.barchart.feed.ddf.market.api.DDF_MarketProvider;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;

public class DDF_ConsumerMarketProvider extends MarketProviderBase<DDF_MarketBase> 
		implements DDF_MarketProvider {

	protected static final Logger log = LoggerFactory.getLogger(
			DDF_ConsumerMarketProvider.class);
			
	protected DDF_ConsumerMarketProvider(final MarketFactory factory,
			final SubscriptionHandler handler) {
		super(factory, new DDF_MetadataServiceWrapper(), handler);
	}
			
	private final DDF_MessageVisitor<Void, MarketDo> visitor = new MapperDDF();
	
	public static final DDF_ConsumerMarketProvider newInstance(
			final SubscriptionHandler handler) {
		
		return new DDF_ConsumerMarketProvider(new MarketFactory() {
			
			@Override
			public MarketDo newMarket(final Instrument instrument) {
				
				if(instrument.isNull()) {
					throw new IllegalArgumentException("Cannot create new market with NULL instrument");
				}
				
				return new VarMarketEntityDDF(instrument);
			}
			
		}, handler);
	}

	@Override
	protected void make(final DDF_MarketBase message, final MarketDo market) {
		message.accept(visitor, market);
	}
	
}
