package com.barchart.feed.ddf.market.provider;

import java.util.EnumMap;
import java.util.Set;

import com.barchart.feed.api.consumer.data.Cuvol;
import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.api.consumer.data.MarketData;
import com.barchart.feed.api.consumer.data.OrderBook;
import com.barchart.feed.api.consumer.data.PriceLevel;
import com.barchart.feed.api.consumer.data.Session;
import com.barchart.feed.api.consumer.data.TopOfBook;
import com.barchart.feed.api.consumer.data.Trade;
import com.barchart.feed.api.consumer.enums.MarketEventType;
import com.barchart.feed.api.consumer.enums.SessionType;
import com.barchart.feed.api.framework.FrameworkAgent;
import com.barchart.feed.api.framework.MarketEntity;
import com.barchart.feed.api.framework.MarketTag;
import com.barchart.feed.api.framework.data.InstrumentEntity;
import com.barchart.feed.api.framework.message.Message;
import com.barchart.feed.base.bar.api.MarketDoBar;
import com.barchart.feed.base.bar.enums.MarketBarType;
import com.barchart.feed.base.book.api.MarketDoBookEntry;
import com.barchart.feed.base.cuvol.api.MarketDoCuvolEntry;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.base.provider.ValueConverter;
import com.barchart.feed.base.state.enums.MarketStateEntry;
import com.barchart.feed.base.trade.enums.MarketTradeSequencing;
import com.barchart.feed.base.trade.enums.MarketTradeSession;
import com.barchart.feed.base.trade.enums.MarketTradeType;
import com.barchart.missive.api.Tag;
import com.barchart.missive.api.TagMap;
import com.barchart.missive.core.MissiveException;
import com.barchart.util.value.api.Time;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;
import com.barchart.util.values.api.TimeValue;

@SuppressWarnings("rawtypes")
public class VarMarketEntityDDF extends VarMarketDDF implements MarketEntity {
	
	private volatile TimeValue lastUpdateTime;
	
	private final EnumMap<MarketEventType, Set<FrameworkAgent<?,?>>> agentMap =
			new EnumMap<MarketEventType, Set<FrameworkAgent<?,?>>>(MarketEventType.class);
	
	VarMarketEntityDDF() {
	}
	
	/* ***** ***** ***** Market Getters ***** ***** ***** */
	
	@Override
	public Trade lastTrade() {
		return get(MarketField.TRADE);
	}

	@Override
	public OrderBook orderBook() {
		return get(MarketField.BOOK);
	}

	@Override
	public PriceLevel lastBookUpdate() {
		return get(MarketField.BOOK_LAST);
	}

	@Override
	public TopOfBook topOfBook() {
		return get(MarketField.BOOK_TOP);
	}

	@Override
	public Cuvol cuvol() {
		return get(MarketField.CUVOL);
	}

	@Override
	public Session session(final SessionType type) {
		switch(type) {
		default:
			throw new RuntimeException("Unknown session type " + type.name());
		case CURRENT:
			return get(MarketField.BAR_CURRENT);
		case EXTENDED_CURRENT:
			return get(MarketField.BAR_CURRENT_EXT);
		case PREVIOUS:
			return get(MarketField.BAR_PREVIOUS);
		case EXTENDED_PREVIOUS:
			throw new UnsupportedOperationException("Extended previous not implemented");
		}
	}

	@Override
	public Instrument instrument() {
		return get(MarketField.INSTRUMENT);
	}

	@Override
	public Time lastUpdateTime() {
		return ValueConverter.time(lastUpdateTime);
	}

	@Override
	public int compareTo(MarketEntity o) {
		return instrumentEntity().compareTo(o.instrumentEntity());
	}

	@Override
	public Time lastTime() {
		return ValueConverter.time(lastUpdateTime);
	}

	@Override
	public MarketTag<MarketEntity> tag() {
		return MarketEntity.MARKET;
	}

	@Override
	public void update(Message update) {
		throw new UnsupportedOperationException("DDF only");
	}

	@Override
	public <V> void set(Tag<V> tag, V value) throws MissiveException {
		throw new UnsupportedOperationException("DDF only");		
	}

	@Override
	public <V> V get(Tag<V> tag) throws MissiveException {
		return null;
	}

	@Override
	public boolean contains(Tag<?> tag) {
		return false;
	}

	@Override
	public Tag<?>[] tagsList() {
		return MarketEntity.ALL;
	}

	@Override
	public int mapSize() {
		return MarketEntity.ALL.length;
	}

	@Override
	public MarketData data() {
		return null;
	}
	
	@Override
	public InstrumentEntity instrumentEntity() {
		return get(MarketField.INSTRUMENT);
	}
	
	/* ***** ***** ***** Update State Methods ***** ***** ***** */
	
	
	@SuppressWarnings("unchecked")
	private void fireCallbacks(final MarketEventType type) {
		
		for(final FrameworkAgent agent : agentMap.get(type)) {
			
			agent.callback().call((MarketData) get(agent.tag()), type);
			
		}
		
	}
	
	@Override
	public void setInstrument(final InstrumentEntity newSymbol) {
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
	
	/* ***** ***** ***** Agent Lifecycle Methods ***** ***** ***** */

	@Override
	public void attach(final FrameworkAgent agent) {
		
	}

	@Override
	public void update(final FrameworkAgent agent) {
		
	}

	@Override
	public void detach(final FrameworkAgent agent) {
		
	}

	

}
