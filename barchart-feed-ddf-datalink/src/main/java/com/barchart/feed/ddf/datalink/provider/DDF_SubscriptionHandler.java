package com.barchart.feed.ddf.datalink.provider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.connection.Connection.State;
import com.barchart.feed.api.consumer.MetadataService;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.Metadata;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.api.model.meta.id.MetadataID;
import com.barchart.feed.api.model.meta.id.VendorID;
import com.barchart.feed.base.sub.SubCommand;
import com.barchart.feed.base.sub.SubscriptionHandler;
import com.barchart.feed.ddf.datalink.api.FeedEvent;
import com.barchart.feed.ddf.datalink.api.FeedClient;
import com.barchart.feed.ddf.datalink.api.FeedClient.EventPolicy;
import com.barchart.feed.ddf.datalink.provider.util.DummyFuture;

public class DDF_SubscriptionHandler implements SubscriptionHandler {
	
	private static final Logger log = LoggerFactory.getLogger(
			DDF_SubscriptionHandler.class);
	
	private final Map<MetadataID<?>, SubCommand> subscriptions = 
			new ConcurrentHashMap<MetadataID<?>, SubCommand>();
	
	private final AtomicBoolean isConnected = new AtomicBoolean(false);
	
	private final FeedClient feed; 
	private final MetadataService metaService;
	
	public DDF_SubscriptionHandler(final FeedClient feed, final MetadataService metaService) {
		
		this.feed = feed;
		this.metaService = metaService;
		
		/* 
		 * Add event to LOGIN_SUCCESS which ensures that all subscribed 
		 * instruments are requested upon login or re-login after a 
		 * disconnect. 
		 */
		feed.setPolicy(FeedEvent.LOGIN_SUCCESS, new EventPolicy() {
			
			@Override
			public void newEvent(FeedEvent event) {
				
				if (subscriptions.size() <= 0) {
					return;
				}
				
				final Set<SubCommand> subs = new HashSet<SubCommand>();
				for(final Entry<MetadataID<?>, SubCommand> e : subscriptions.entrySet()) {
					subs.add(e.getValue());
				}
				subscribe(subs);
				
			}
			
		});
		
		/* Set a listener to maintain state variable isConnected */
		feed.bindStateListener(new Connection.Monitor() {

			@Override
			public void handle(final State state, final Connection connection) {
				
				switch(state) {
				default:
					isConnected.set(false);
					return;
				case CONNECTED:
					isConnected.set(true);
					return;
				}
				
			}
			
		});
		
	}
	
	@Override
	public Map<MetadataID<?>, SubCommand> subscriptions() {
		return Collections.<MetadataID<?>, SubCommand> unmodifiableMap(subscriptions);
	}

	@Override
	public Future<Boolean> subscribe(final Set<SubCommand> subs) {
		
		if (subs == null) {
			log.error("Null subscribes request recieved");
			return null;
		}
		
		log.debug("Sending {} subscription requests", subs.size());

		final Set<SubCommand> insts = new HashSet<SubCommand>();
		final Set<SubCommand> exch = new HashSet<SubCommand>();
		
		for(final SubCommand sub : subs) {
			switch(sub.metaType()) {
			default:
				throw new IllegalStateException("Subscription type cannot be null");
			case INSTRUMENT:
				insts.add(sub);
				break;
			case EXCHANGE:
				exch.add(sub);
				break;
			}
		}
		
		if(!insts.isEmpty()) {
			
			if(exch.isEmpty()) {
				return subInsts(insts);
			} else {
				subInsts(insts);
				return subExcs(exch);
			}
			
		}
		
		if(!exch.isEmpty()) {
			return subExcs(exch);
		} else {
			return new DummyFuture();
		}
		
	}
	
	private Future<Boolean> subInsts(final Set<SubCommand> subs) {
		
		/*
		 * Creates a single JERQ command from the set, subscriptions are added individually.
		 */
		final StringBuffer sb = new StringBuffer();
		sb.append("GO ");
		for (final SubCommand sub : subs) {

			if (sub != null) {
				
				final MetadataID<?> id = sub.interestID();
				final String symbol = JERQsymbol(id);
				
				log.debug("SUB {}", symbol);
				
				/* If we're subscribed already, add new interests, otherwise add new subscription */
				if(subscriptions.containsKey(id)) {
					subscriptions.get(id).addTypes(sub.types());
				} else {
					subscriptions.put(id, new DDF_Subscription(sub, Metadata.MetaType.INSTRUMENT));
				}
				
				sb.append(symbol);
				sb.append("=");
				sb.append(subscriptions.get(id).typeString() + ",");
			}
		}
		
		if (!isConnected.get()) {
			return new DummyFuture();
		}
		
		return feed.write(sb.toString());
		
	}
	
	private Future<Boolean> subExcs(final Set<SubCommand> subs) {
		
		final StringBuffer sb = new StringBuffer();
		
		sb.append("STR L ");
		for(final SubCommand sub : subs) {
			
			if(sub != null) {
			
				final String exchCode = sub.interestID().id();
				
				if(!subscriptions.containsKey(exchCode)) {
					
					subscriptions.put(sub.interestID(), 
							new DDF_Subscription(sub, Metadata.MetaType.EXCHANGE));
					
				}
				
				sb.append(exchCode + ";");
				
			}
			
		}
		
		if (!isConnected.get()) {
			return new DummyFuture();
		}
		
		return feed.write(sb.toString());
	}

	@Override
	public Future<Boolean> unsubscribe(final Set<SubCommand> subs) {

		if (subs == null) {
			log.error("Null subscribes request recieved");
			return null;
		}

		final Set<SubCommand> insts = new HashSet<SubCommand>();
		final Set<SubCommand> exch = new HashSet<SubCommand>();
		
		for(final SubCommand sub : subs) {
			switch(sub.metaType()) {
			case INSTRUMENT:
				insts.add(sub);
				break;
			case EXCHANGE:
				exch.add(sub);
				break;
			default:
				log.error("Unhandled metadata type {}", sub.metaType());
				break;
			}
		}
		
		if(!insts.isEmpty()) {
			
			if(exch.isEmpty()) {
				return unsubInsts(insts);
			} else {
				unsubInsts(insts);
				return unsubExchs(exch);
			}
			
		}
		
		if(!exch.isEmpty()) {
			return unsubExchs(exch);
		} else {
			return new DummyFuture();
		}
		
	}

	private Future<Boolean> unsubInsts(final Set<SubCommand> subs) {
		
		/*
		 * Creates a single JERQ command from the set. Subscriptions are removed
		 * individually.
		 */
		final StringBuffer sb = new StringBuffer();
		sb.append("STOP ");
		for (final SubCommand sub : subs) {

			if (sub != null) {
				final MetadataID<?> id = sub.interestID();
				final String symbol = JERQsymbol(id);
				
				subscriptions.remove(id);
				sb.append(symbol + ",");
			}
		}
		
		if (!isConnected.get()) {
			return new DummyFuture();
		}
		
		return feed.write(sb.toString());
	}
	
	private Future<Boolean> unsubExchs(final Set<SubCommand> subs) {
		
		for(final SubCommand sub : subs) {
			subscriptions.remove(sub.interestID());
		}
		
		if (!isConnected.get()) {
			return new DummyFuture();
		}
		
		/* Have to unsub from everything and resub */
		feed.write("STOP");
		
		final Set<SubCommand> resubs = new HashSet<SubCommand>();
		for(final Entry<MetadataID<?>, SubCommand> e : subscriptions.entrySet()) {
			resubs.add(e.getValue());
		}
		
		return subscribe(resubs);
	}
	
	private String JERQsymbol(final MetadataID<?> id) {
		
		switch(id.metaType()) {
		
		default:
			return "";
		case EXCHANGE:
			return id.id();
		case INSTRUMENT:
			
			final Instrument i = metaService.instrument((InstrumentID)id)
				.toBlockingObservable()
				.first()
				.get(id);
				
			String symbol = i.symbol();
			if(symbol.contains("|")) {
				symbol = i.vendorSymbols().get(VendorID.BARCHART_SHORT);
			} else {
				symbol = formatForJERQ(i.symbol());
			}
			
			return symbol;
		
		}
		
	}
	
	private static String formatForJERQ(final String symbol) {

		if (symbol == null) {
			return "";
		}

		if (symbol.length() < 3) {
			return symbol;
		}

		/* e.g. GOOG */
		if (!Character.isDigit(symbol.charAt(symbol.length() - 1))) {
			return symbol;
		}

		/* e.g. ESH2013 -> ESH3 */
		if (Character.isDigit(symbol.charAt(symbol.length() - 4))) {
			return new StringBuilder(symbol).delete(symbol.length() - 4,
					symbol.length() - 1).toString();
		}

		return symbol;
	}
	
}
