package com.barchart.feed.ddf.market.provider;

import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.api.consumer.enums.MarketEventType;
import com.barchart.feed.api.framework.FrameworkAgent;
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
	
	VarMarketEntityDDF() {
		
	}
	
	
	/* ***** ***** ***** Update State Methods ***** ***** ***** */
	
	
	@SuppressWarnings("unchecked")
	private void fireCallbacks(final MarketEventType type) {
		
		for(final FrameworkAgent agent : agentMap.get(type)) {
			
			if(agent.isActive()) {
				agent.callback().call(agent.data(this), type);
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
		
		fireCallbacks(MarketEventType.BOOK_SNAPSHOT);
	}
	
	@Override
	public void setBookUpdate(final MarketDoBookEntry entry,
			final TimeValue time) {
		super.setBookUpdate(entry, time);
		
		fireCallbacks(MarketEventType.BOOK_UPDATE);
	}
	
	@Override
	public void setCuvolUpdate(final MarketDoCuvolEntry entry,
			final TimeValue time) {
		super.setCuvolUpdate(entry, time);
		
		fireCallbacks(MarketEventType.CUVOL_UPDATE);
	}
	
	@Override
	public void setCuvolSnapshot(final MarketDoCuvolEntry[] entries,
			final TimeValue time) {
		super.setCuvolSnapshot(entries, time);

		fireCallbacks(MarketEventType.CUVOL_SNAPSHOT);
	}
	
	@Override
	public void setTrade(final MarketTradeType type,
			final MarketTradeSession session,
			final MarketTradeSequencing sequencing, final PriceValue price,
			final SizeValue size, final TimeValue time, final TimeValue date) {
		super.setTrade(type, session, sequencing, price, size, time, date);
		
		fireCallbacks(MarketEventType.TRADE);
	}
	
	@Override
	public void setBar(final MarketBarType type, final MarketDoBar bar) {
		super.setBar(type, bar);
		
		fireCallbacks(MarketEventType.SNAPSHOT);
	}
	
	@Override
	public void setState(final MarketStateEntry entry, final boolean isOn) {
		super.setState(entry, isOn);
		
		// Currently not firing on state
	}
	
}
