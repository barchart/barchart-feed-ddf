package com.barchart.feed.ddf.market.provider;

import com.barchart.feed.api.data.Cuvol;
import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.api.data.InstrumentEntity;
import com.barchart.feed.api.data.OrderBook;
import com.barchart.feed.api.data.PriceLevel;
import com.barchart.feed.api.data.Session;
import com.barchart.feed.api.data.TopOfBook;
import com.barchart.feed.api.data.Trade;
import com.barchart.feed.api.enums.SessionType;
import com.barchart.feed.api.framework.FrameworkAgent;
import com.barchart.feed.api.framework.MarketEntity;
import com.barchart.feed.api.framework.MarketTag;
import com.barchart.feed.api.message.Message;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.base.provider.ValueConverter;
import com.barchart.missive.api.Tag;
import com.barchart.missive.core.MissiveException;
import com.barchart.util.value.api.Time;
import com.barchart.util.values.api.TimeValue;

public class VarMarketEntityDDF extends VarMarketDDF implements MarketEntity {
	
	private volatile TimeValue lastUpdateTime;
	
	@Override
	public void setInstrument(final InstrumentEntity newSymbol) {
		super.setInstrument(newSymbol);
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
	public Message lastUpdate() {
		throw new UnsupportedOperationException("DDF only");	
	}

	@Override
	public Message lastSnapshot() {
		throw new UnsupportedOperationException("DDF only");	
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
	public MarketEntity copy() {
		throw new UnsupportedOperationException();
	}

	@Override
	public InstrumentEntity instrumentEntity() {
		return get(MarketField.INSTRUMENT);
	}
	
	/* ***** ***** ***** Update State Methods ***** ***** ***** */
	
	
	/* ***** ***** ***** Agent Lifecycle Methods ***** ***** ***** */

	@Override
	public void attach(FrameworkAgent agent) {
		
	}

	@Override
	public void update(FrameworkAgent agent) {
		
	}

	@Override
	public void detach(FrameworkAgent agent) {
		
	}

}
