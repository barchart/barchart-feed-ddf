package com.barchart.feed.ddf.market.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.FrameworkAgent;
import com.barchart.feed.api.data.Cuvol;
import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.api.data.Market;
import com.barchart.feed.api.data.MarketData;
import com.barchart.feed.api.data.OrderBook;
import com.barchart.feed.api.data.Session;
import com.barchart.feed.api.data.Trade;
import com.barchart.feed.base.bar.api.MarketDoBar;
import com.barchart.feed.base.bar.enums.MarketBarType;
import com.barchart.feed.base.book.api.MarketDoBookEntry;
import com.barchart.feed.base.cuvol.api.MarketDoCuvolEntry;
import com.barchart.feed.base.state.enums.MarketStateEntry;
import com.barchart.feed.base.trade.enums.MarketTradeSequencing;
import com.barchart.feed.base.trade.enums.MarketTradeSession;
import com.barchart.feed.base.trade.enums.MarketTradeType;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;
import com.barchart.util.values.api.TimeValue;

@SuppressWarnings("rawtypes")
public class VarMarketEntityDDF extends VarMarketDDF {
	
	@SuppressWarnings("unused")
	private static final Logger log = 
			LoggerFactory.getLogger(VarMarketEntityDDF.class);
	
	VarMarketEntityDDF(final Instrument instrument) {
		super(instrument);
	}
	
	
	/* ***** ***** ***** Update State Methods ***** ***** ***** */
	
	
	@SuppressWarnings("unchecked")
	private <V extends MarketData<V>> void fireCallbacks(
			final Class<V> clazz) {
		
		for(final FrameworkAgent agent : agentMap.get(clazz)) {
			
			if(agent.isActive()) {
				agent.callback().call(agent.data(this));
			}
			
		}
		
		for(final FrameworkAgent agent : agentMap.get(Market.class)) {
			
			if(agent.isActive()) {
				agent.callback().call(agent.data(this));
			}
			
		}
		
	}
	
	@Override
	public void setInstrument(final Instrument newSymbol) {
		super.setInstrument(newSymbol);
		
		// Currently not firing on instruments
	}
	
	@Override
	public void setBookSnapshot(final MarketDoBookEntry[] entries,
			final TimeValue time) {
		super.setBookSnapshot(entries, time);
		
		//log.debug("Set book snapshot, firing callbacks");
		
		fireCallbacks(OrderBook.class);
	}
	
	@Override
	public void setBookUpdate(final MarketDoBookEntry entry,
			final TimeValue time) {
		super.setBookUpdate(entry, time);
		
		//log.debug("Set book update, firing callbacks");
		
		fireCallbacks(OrderBook.class);
	}
	
	@Override
	public void setCuvolUpdate(final MarketDoCuvolEntry entry,
			final TimeValue time) {
		super.setCuvolUpdate(entry, time);
		
		//log.debug("Set cuvol update, firing callbacks");
		
		fireCallbacks(Cuvol.class);
	}
	
	@Override
	public void setCuvolSnapshot(final MarketDoCuvolEntry[] entries,
			final TimeValue time) {
		super.setCuvolSnapshot(entries, time);
		
		//log.debug("Set cuvol snapshot, firing callbacks");

		fireCallbacks(Cuvol.class);
	}
	
	@Override
	public void setTrade(final MarketTradeType type,
			final MarketTradeSession session,
			final MarketTradeSequencing sequencing, final PriceValue price,
			final SizeValue size, final TimeValue time, final TimeValue date) {
		super.setTrade(type, session, sequencing, price, size, time, date);
		
		//log.debug("Set trade, firing callbacks");
		
		fireCallbacks(Trade.class);
	}
	
	@Override
	public void setBar(final MarketBarType type, final MarketDoBar bar) {
		super.setBar(type, bar);
		
		//log.debug("Set bar, firing callbacks");
		
		fireCallbacks(Session.class);
	}
	
	@Override
	public void setState(final MarketStateEntry entry, final boolean isOn) {
		super.setState(entry, isOn);
		
		//log.debug("Set state");
		
		// Currently not firing on state
	}
	
}
