package com.barchart.feed.test.stream;

import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Trade;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.test.replay.BarchartMarketplaceReplay;
import com.barchart.feed.test.replay.FeedReplay;
import com.barchart.feed.test.replay.FeedReplay.MessageListener;
import com.barchart.util.value.api.Price;

public class BacktestSingleInstrument {
	
	private static final Logger log = LoggerFactory.getLogger(BacktestSingleInstrument.class);
	
	public static void main(final String[] args) throws Exception {
		
		final String symbol = "DXM5";
		
		final BarchartMarketplaceReplay marketplace = new BarchartMarketplaceReplay();
		
		
		
		marketplace.subscribe(Market.class, new MarketObserver<Market>() {
			
			Price lastBid = Price.ZERO;
			Price lastAsk = Price.ZERO;
			Price lastTrade = Price.ZERO;

			@Override
			public void onNext(final Market v) {
				
				try {
					
					final Book.Top top = v.book().top();
					lastBid = top.bid().price();
					lastAsk = top.ask().price();
					
					log.debug("{} BID = {}  ASK = {}", v.instrument().symbol(), lastBid, lastAsk);
					
					
					final Set<Market.Component> changes = v.change();
					
					if(!changes.contains(Market.Component.TRADE)) {
						return;
					}
					
					final Price tradePx = v.trade().price();
					
					// || v.trade().sequence() != Sequence.NORMAL
					if(tradePx.equals(Price.ZERO)) {
						return;
					}
					
					if(tradePx.sub(lastTrade).abs().greaterThan(Price.ONE)) {
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
			
		}, symbol);
		
		
		final FeedReplay replay = FeedReplay.builder()
				.source(BacktestSingleInstrument.class.getResource("/DX/DX_3_6.ddf"))
				.listener(new MessageListener() {

					@Override
					public void messageProcessed(final DDF_BaseMessage parsed, final byte[] raw) {
						if(parsed.toString().substring(2,6).equals(symbol)) {
							log.debug(parsed.toString());
						}
					}
					
				})
				.build(marketplace.maker());
		
		replay.run();
		replay.await();
		
	}
	
	private static String printTypes(final Trade trade) {
		
		final StringBuilder sb = new StringBuilder();
		
		for(final Trade.TradeType t : trade.types()) {
			sb.append(t).append(" ");
		}
		
		return sb.toString();
		
	}
	
	private static boolean isNormalSequencing(final Trade trade) {

		if (trade.price().isNull() || trade.price().isZero()) {
			return false;
		}

		Iterator<Trade.TradeType> t = trade.types().iterator();

		while (t.hasNext()) {
			final Trade.TradeType tt = t.next();
			if (tt.sequence == Trade.Sequence.NORMAL) {
				return true;
			}
		}

		return false;
	}
	
//	if(top.ask().price().sub(lastAsk).abs().greaterThan(Price.ONE) ||
//	top.bid().price().sub(lastBid).abs().greaterThan(Price.ONE)) {
//
//log.debug("***************************************** " +
//		v.updated().asDate() + " " + top.ask().price() + " " 
//		+ top.ask().price().sub(lastAsk) + " " + top.bid().price() + " " 
//		+ top.bid().price().sub(lastBid));
//}
}
