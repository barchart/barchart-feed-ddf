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
import com.barchart.missive.api.Tag;
import com.barchart.missive.core.MissiveException;
import com.barchart.util.value.api.Time;

public class VarMarketEntityDDF extends VarMarketDDF implements MarketEntity {
	
	
	
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
		return null;
	}

	@Override
	public Session session(SessionType type) {
		return null;
	}

	@Override
	public Instrument instrument() {
		return null;
	}

	@Override
	public Time lastUpdateTime() {
		return null;
	}

	@Override
	public int compareTo(MarketEntity o) {
		return 0;
	}

	@Override
	public Time lastTime() {
		return null;
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
		return null;
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
		return null;
	}

	@Override
	public int mapSize() {
		return 0;
	}

	@Override
	public MarketEntity copy() {
		return null;
	}

	@Override
	public InstrumentEntity instrumentEntity() {
		return null;
	}
	
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
