package com.barchart.feed.ddf.client.provider;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.observables.BlockingObservable;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Trade;
import com.barchart.feed.api.model.data.Trade.Sequence;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.client.provider.BarchartMarketplace;
import com.barchart.feed.ddf.instrument.provider.DDF_RxInstrumentProvider;
import com.barchart.util.value.ValueFactoryImpl;
import com.barchart.util.value.api.Price;
import com.barchart.util.value.api.ValueFactory;

public class BadBarRun {
	
	private static final Logger log = LoggerFactory.getLogger(BadBarRun.class);
	
	private static final ValueFactory vals = ValueFactoryImpl.instance;
	
	public static void main(final String[] args) throws Exception {
		
		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");
	
		final Marketplace feed = new BarchartMarketplace(username, password);
		
		feed.startup();
		
		final MarketObserver<Market> callback = new MarketObserver<Market>() {
			
			Price lastBid = Price.ZERO;
			Price lastAsk = Price.ZERO;
			Price lastTrade = Price.ZERO;

			@Override
			public void onNext(final Market v) {
				
				try {
					
					final Book.Top top = v.book().top();
					lastBid = top.bid().price();
					lastAsk = top.ask().price();
					
					// log.debug("{} BID = {}  ASK = {}", v.instrument().symbol(), lastBid, lastAsk);
					
					final Set<Market.Component> changes = v.change();
					
					if(!changes.contains(Market.Component.TRADE)) {
						return;
					}
					
					final Price tradePx = v.trade().price();
					
					// || v.trade().sequence() != Sequence.NORMAL
					if(tradePx.equals(Price.ZERO)) {
						return;
					}
					
					if(tradePx.sub(lastTrade).abs().greaterThan(vals.newPrice(0.005))) {
						log.debug("***************");
						log.debug("Last {}  Current {}", lastTrade, tradePx);
						log.debug("Bid {}  Ask {}", lastBid, lastAsk);
						log.debug("Is Normal {} Sequencing {} Time {}", 
								isNormalSequencing(v.trade()), v.trade().sequence(), v.trade().time().asDate());
						log.debug("Types {}", printTypes(v.trade()));
					}
					
					lastTrade = tradePx;
					
					
				} catch (final Exception e) {
					
				}
				
			}
			
		};
		
		final Agent myAgent = feed.newAgent(Market.class, callback);
		
		myAgent.include(getInst("E6H5"));
		//myAgent.include("ESU3");
		
		Thread.sleep(1000000);
		
		feed.shutdown();
	}

	public static Instrument getInst(final String barSym) {
		
		final Map<String, List<Instrument>> map = BlockingObservable.from(DDF_RxInstrumentProvider
				.fromString(barSym)).single().results();
		
		Instrument result = BlockingObservable.from(DDF_RxInstrumentProvider
				.fromString(barSym)).single().results().get(barSym).get(0);
		
		return result;
		
	}
	
	private static boolean isNormalSequencing(final Trade trade) {

		if (trade.price().isNull() || trade.price().isZero()) {
			return false;
		}
		
		if(trade.sequence() == null) {
			return false;
		}

		return trade.sequence() == Sequence.NORMAL;
		
	}
	
	private static String printTypes(final Trade trade) {
		
		final StringBuilder sb = new StringBuilder();
		
		for(final Trade.TradeType t : trade.types()) {
			sb.append(t).append(" ");
		}
		
		return sb.toString();
		
	}
	
}
