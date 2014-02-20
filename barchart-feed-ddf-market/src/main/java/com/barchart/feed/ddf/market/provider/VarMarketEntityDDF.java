package com.barchart.feed.ddf.market.provider;

import java.util.EnumSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.data.Cuvol;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.MarketData;
import com.barchart.feed.api.model.data.Session;
import com.barchart.feed.api.model.data.Trade;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.bar.api.MarketDoBar;
import com.barchart.feed.base.bar.enums.MarketBarType;
import com.barchart.feed.base.book.api.MarketDoBookEntry;
import com.barchart.feed.base.cuvol.api.MarketDoCuvolEntry;
import com.barchart.feed.base.participant.FrameworkAgent;
import com.barchart.feed.base.state.enums.MarketStateEntry;
import com.barchart.feed.base.trade.enums.MarketTradeSequencing;
import com.barchart.feed.base.trade.enums.MarketTradeSession;
import com.barchart.feed.base.trade.enums.MarketTradeType;
import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.base.values.api.TimeValue;

@SuppressWarnings("rawtypes")
public class VarMarketEntityDDF extends VarMarketDDF {
	
	enum MKData {

		MARKET(Market.class), BOOK(Book.class), CUVOL(Cuvol.class),
		TRADE(Trade.class), SESSION(Session.class);

		final Class<? extends MarketData<?>> clazz;

		private MKData(final Class<? extends MarketData<?>> clazz) {
			this.clazz = clazz;
		}
	}
	
	private final Set<MKData> toFire = EnumSet.noneOf(MKData.class);

	@SuppressWarnings("unused")
	private static final Logger log = 
			LoggerFactory.getLogger(VarMarketEntityDDF.class);
	
	public VarMarketEntityDDF(final Instrument instrument) {
		super(instrument);
	}
	
	
	/* ***** ***** ***** Update State Methods ***** ***** ***** */
	
	
	@Override
	@SuppressWarnings("unchecked")
	public void fireCallbacks() {
		
		synchronized(agentMap) {
		
			for (final MKData d : toFire) {
				for (final FrameworkAgent fa : agentMap.get(d.clazz)) {
					fa.callback().onNext(fa.data(this.freeze()));
				}
			}
			
			toFire.clear();
		
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
		
		toFire.add(MKData.BOOK);
		toFire.add(MKData.MARKET);
	}
	
	@Override
	public void setBookUpdate(final MarketDoBookEntry entry,
			final TimeValue time) {
		super.setBookUpdate(entry, time);
		
		//log.debug("Set book update, firing callbacks");
		
		toFire.add(MKData.BOOK);
		toFire.add(MKData.MARKET);
	}
	
	@Override
	public void setCuvolUpdate(final MarketDoCuvolEntry entry,
			final TimeValue time) {
		super.setCuvolUpdate(entry, time);
		
		//log.debug("Set cuvol update, firing callbacks");
		
		toFire.add(MKData.CUVOL);
		toFire.add(MKData.MARKET);
	}
	
	@Override
	public void setCuvolSnapshot(final MarketDoCuvolEntry[] entries,
			final TimeValue time) {
		super.setCuvolSnapshot(entries, time);
		
		//log.debug("Set cuvol snapshot, firing callbacks");

		toFire.add(MKData.CUVOL);
		toFire.add(MKData.MARKET);
	}
	
	@Override
	public void setTrade(final MarketTradeType type,
			final MarketTradeSession session,
			final MarketTradeSequencing sequencing, final PriceValue price,
			final SizeValue size, final TimeValue time, final TimeValue date) {
		super.setTrade(type, session, sequencing, price, size, time, date);
		
		//log.debug("Set trade, firing callbacks");
		
		toFire.add(MKData.TRADE);
		toFire.add(MKData.CUVOL);
		toFire.add(MKData.SESSION);
		toFire.add(MKData.MARKET);
	}
	
	@Override
	public void setBar(final MarketBarType type, final MarketDoBar bar) {
		super.setBar(type, bar);
		
		//log.debug("Set bar, firing callbacks");
		
		toFire.add(MKData.SESSION);
		toFire.add(MKData.MARKET);
	}
	
	@Override
	public void setState(final MarketStateEntry entry, final boolean isOn) {
		super.setState(entry, isOn);
		
		//log.debug("Set state");
		
		// Currently not firing on state
	}
	
}
