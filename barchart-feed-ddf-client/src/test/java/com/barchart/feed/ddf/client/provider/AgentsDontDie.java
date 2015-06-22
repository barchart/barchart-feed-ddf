package com.barchart.feed.ddf.client.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.consumer.MetadataService.Result;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.client.provider.BarchartMarketplace;

public class AgentsDontDie {

	private final static Logger log = LoggerFactory.getLogger(AgentsDontDie.class);
	private final ExecutorService executorService = Executors.newFixedThreadPool(10);
 
	private final static List<String> symbols = new ArrayList<String>();
 
	static {
		symbols.add("AAPL");
		symbols.add("CTH17");
		symbols.add("CTH17");
		symbols.add("KCN5");
		symbols.add("MSFT");
		symbols.add("ESM5");
		symbols.add("YHOO");
		symbols.add("CTH17");
		symbols.add("CTH17");
	}
 
	public static void main(String... args) {
		// new AgentsDontDie(System.getProperty("barchart.username"),
		// System.getProperty("barchart.password"));
		new AgentsDontDie("gavinTrader", "gavin");
	}
 
	private final List<WidgetWhoHasAnAgent> widgets = new ArrayList<WidgetWhoHasAnAgent>();
	private final Marketplace marketPlace;
 
	public AgentsDontDie(String user, String pass) {
 
		marketPlace = new BarchartMarketplace(user, pass);
		marketPlace.startup();
 
		int x = 0;
 
		for (String s : symbols) {
			widgets.add(new WidgetWhoHasAnAgent(marketPlace, "Widget" + x, s));
			x++;
		}
 
		executorService.execute(new Runnable() {
 
			@Override
			public void run() {
 
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
 
				for (int x = 0; x < widgets.size() - 1; x++) {
					executorService.execute(new ShutterDowner(widgets.get(x), x));
				}
 
				widgets.clear();
			}
 
		});
 
	}
 
	class ShutterDowner implements Runnable {
		
		private final WidgetWhoHasAnAgent w;
		int index;
 
		public ShutterDowner(WidgetWhoHasAnAgent widget, int index) {
			this.w = widget;
			this.index = index;
		}
 
		@Override
		public void run() {
			w.shutdown();
		}
		
	}
 
	class WidgetWhoHasAnAgent implements MarketObserver<Market> {
 
		final Marketplace feedService;
		final Agent agent;
		final String name;
		final String symbol;
		private Market market;
 
		public WidgetWhoHasAnAgent(Marketplace feedService, String name, String symbol) {
			
			this.feedService = feedService;
			this.name = name;
			this.symbol = symbol;
 
			final Result<Instrument> insts = feedService.instrument(symbol).toBlockingObservable().last();
 
			final Instrument inst = insts.results().values().iterator().next().get(0);
 
			agent = feedService.newAgent(Market.class, this);
			agent.include(inst.id());
 
			log.debug("Creating agent {}, including instrument for {}", name, symbol);
			
		}
 
		public void shutdown() {
			log.debug("Shutting down agent {}, with instrument of {} ID = {}", name, symbol, agent.id());
			agent.terminate();
		}
 
		@Override
		public void onNext(Market arg0) {
			this.market = arg0;
		}
 
	}
	
}
