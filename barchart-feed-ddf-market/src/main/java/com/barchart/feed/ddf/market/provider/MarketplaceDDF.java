package com.barchart.feed.ddf.market.provider;

import com.barchart.feed.api.data.MarketData;
import com.barchart.feed.api.framework.FrameworkAgent;
import com.barchart.feed.api.framework.FrameworkMarketplace;
import com.barchart.feed.api.message.Message;
import com.barchart.feed.base.market.api.MarketDo;
import com.barchart.feed.base.market.api.MarketFactory;

public class MarketplaceDDF extends DDF_MarketService implements FrameworkMarketplace {

	/* Inject new markets to super */
	protected MarketplaceDDF() {
		
		super(new MarketFactory() {

			@Override
			public MarketDo newMarket() {
				return new VarMarketEntityDDF();
			}
			
		});
	}

	@Override
	public void handle(Message message) {
		throw new UnsupportedOperationException("DDF only");
	}
	
	/* ***** ***** ***** Agent Lifecycle Methods ***** ***** ***** */

	@Override
	public void attachAgent(FrameworkAgent agent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAgent(FrameworkAgent agent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void detachAgent(FrameworkAgent agent) {
		// TODO Auto-generated method stub
		
	}
	
	/* ***** ***** ***** Agent builder ***** ***** ***** */

	@Override
	public <V extends MarketData> Builder<V> agentBuilder() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
