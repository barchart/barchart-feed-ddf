/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/**
 * 
 * The core DDF class which encapsulates all the core functionality a new user
 * will need to get started.
 * <p>
 * Instances are created using the public constructor. An optional parameter
 * can provide an executor.
 * <p>
 * The price feed is started and stopped using the startup() and shutdown()
 * methods. Note that these are non-blocking calls. Applications requiring
 * actions upon successful login should instantiate and bind a
 * FeedStatusListener to the client.  Note that UDP listeners (the default)
 * will not fire any feed state events, they will just begin receiving data.
 * <p>
 * 
 */
package com.barchart.feed.client.provider;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.framework.data.InstrumentField;
import com.barchart.feed.api.inst.Instrument;
import com.barchart.feed.base.market.api.MarketRegListener;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.provider.MakerBaseAllMarkets;
import com.barchart.feed.ddf.datalink.api.Subscription;
import com.barchart.feed.ddf.datalink.provider.DDF_FeedClientFactory;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.feed.ddf.instrument.provider.InstrumentDBProvider;
import com.barchart.feed.ddf.instrument.provider.LocalInstrumentDBMap;
import com.barchart.feed.ddf.instrument.provider.ServiceDatabaseDDF;
import com.barchart.feed.ddf.market.provider.DDF_MarketServiceAllMarkets;
import com.barchart.util.values.api.Value;

/**
 * The entry point for Barchart data feed services.
 */
public class BarchartFeedReceiver extends BarchartFeedClientBase {

	private static final Logger log = LoggerFactory
			.getLogger(BarchartFeedReceiver.class);
	
	/* Used if unable to retrieve system default temp directory */
	private static final String TEMP_DIR = "C:\\windows\\temp\\";

	private ExecutorService executor = null;

	public BarchartFeedReceiver() {

		this(Executors.newCachedThreadPool());

	}

	public BarchartFeedReceiver(final ExecutorService ex) {
		maker = DDF_MarketServiceAllMarkets.newInstance();
		maker.add(instrumentSubscriptionListener);
		executor = ex;
	}
	
	/* ***** ***** Begin UDP methods ***** ***** */
	
	/**
	 * Starts a stateless UDP connection to the specified port. If the user is
	 * already logged in, this call will end the previous connection and reset
	 * all registered market takers.
	 * 
	 * @param socketAddress
	 * 		The socket the feed receiver will listen to
	 * @param filterBySub
	 * 		True if the receiver will filter messages based on registered
	 *      market takers
	 * @param resourceFolder
	 * 		The folder where the instrument definition file and database 
	 * 		files will be stored
	 * @return
	 * 		Future referencing the instrument database update task.  The task 
	 * 		updates the local instrument definition file from a remote S3
	 * 		bucket and builds the local database if needed.  Task returns true
	 * 		upon successful completion.
	 */
	public Future<Boolean> listenUDP(final int socketAddress, final boolean filterBySub,
			final File resourceFolder) {
		
		setClient(DDF_FeedClientFactory.newUDPListenerClient(socketAddress,
				filterBySub, executor), false);
		
		final LocalInstrumentDBMap dbMap = InstrumentDBProvider.getMap(
				resourceFolder);
		
		final ServiceDatabaseDDF dbService = new ServiceDatabaseDDF(dbMap, executor);
		
		DDF_InstrumentProvider.bind(dbService);
		
		return executor.submit(InstrumentDBProvider.updateDBMap(resourceFolder, dbMap));
		
	}

	/**
	 * Starts a stateless UDP connection to the specified port. If the user is
	 * already logged in, this call will end the previous connection and reset
	 * all registered market takers.  Uses the system default temp folder to
	 * store instrument definition and database files.
	 * 
	 * @param socketAddress
	 *      The socket the feed receiver will listen to
	 * @param filterBySub
	 *      True if the receiver will filter messages based on registered
	 *      market takers
	 * @return
	 * 		Future referencing the instrument database update task.  The task 
	 * 		updates the local instrument definition file from a remote S3
	 * 		bucket and builds the local database if needed.  Task returns true
	 * 		upon successful completion.  
	 */
	public Future<Boolean> listenUDP(final int socketAddress, final boolean filterBySub) {

		return listenUDP(socketAddress, filterBySub, getTempFolder());
		
	}
	
	/**
	 * Starts a stateless UDP connection to the specified port. If the user is
	 * already logged in, this call will end the previous connection and reset
	 * all registered market takers.
	 * 
	 * @param socketAddress
	 * 		The socket the feed receiver will listen to
	 * @param resourceFolder
	 * 		The folder where the instrument definition file and database 
	 * 		files will be stored
	 * @return
	 * 		Future referencing the instrument database update task.  The task 
	 * 		updates the local instrument definition file from a remote S3
	 * 		bucket and builds the local database if needed.  Task returns true
	 * 		upon successful completion.  
	 */
	public Future<Boolean> listenUDP(final int socketAddress, final File resourceFolder) {
		
		return listenUDP(socketAddress, false, resourceFolder);
		
	}

	/**
	 * Starts a stateless UDP connection to the specified port. If the user is
	 * already logged in, this call will end the previous connection and reset
	 * all registered market takers.
	 * 
	 * @param socketAddress
	 *      The socket the feed receiver will listen to market takers
	 * @return
	 * 		Future referencing the instrument database update task.  The task 
	 * 		updates the local instrument definition file from a remote S3
	 * 		bucket and builds the local database if needed.  Task returns true
	 * 		upon successful completion.  
	 */
	public Future<Boolean> listenUDP(final int socketAddress) {

		return listenUDP(socketAddress, false, getTempFolder());

	}
	
	/* ***** ***** Begin TCP methods ***** ***** */

	/**
	 * Starts a stateless TCP connection to the specified port. If the user is
	 * already logged in, this call will end the previous connection and reset
	 * all registered market takers.
	 * 
	 * @param socketAddress
	 * 		The socket the feed receiver will listen to
	 * @param filterBySub
	 * 		True if the receiver will filter messages based on registered
	 *     	market takers
	 * @param resourceFolder
	 * 		The folder where the instrument definition file and database 
	 * 		files will be stored
	 * @return
	 * 		Future referencing the instrument database update task.  The task 
	 * 		updates the local instrument definition file from a remote S3
	 * 		bucket and builds the local database if needed.  Task returns true
	 * 		upon successful completion.  
	 */
	public Future<Boolean> listenTCP(final int socketAddress, final boolean filterBySub,
			final File resourceFolder) {
		
		log.debug("Using {} as resource folder", resourceFolder.getAbsolutePath());
	
		setClient(DDF_FeedClientFactory.newStatelessTCPListenerClient(
				socketAddress, filterBySub, executor), false);
		
		final LocalInstrumentDBMap dbMap = InstrumentDBProvider.getMap(
				resourceFolder);
		
		final ServiceDatabaseDDF dbService = new ServiceDatabaseDDF(dbMap, executor);
		
		DDF_InstrumentProvider.bind(dbService);
		
		return executor.submit(InstrumentDBProvider.updateDBMap(resourceFolder, dbMap));
	}
	
	/**
	 * Starts a stateless TCP connection to the specified port. If the user is
	 * already logged in, this call will end the previous connection and reset
	 * all registered market takers.
	 * 
	 * @param socketAddress
	 *      The socket the feed receiver will listen to
	 * @param filterBySub
	 *      True if the receiver will filter messages based on registered
	 *      market takers
	 * @return
	 * 		Future referencing the instrument database update task.  The task 
	 * 		updates the local instrument definition file from a remote S3
	 * 		bucket and builds the local database if needed.  Task returns true
	 * 		upon successful completion.       
	 */
	public Future<Boolean> listenTCP(final int socketAddress, final boolean filterBySub) {

		return listenTCP(socketAddress, filterBySub, getTempFolder());

	}
	
	/**
	 * Starts a stateless TCP connection to the specified port. If the user is
	 * already logged in, this call will end the previous connection and reset
	 * all registered market takers.  
	 * 
	 * @param socketAddress
	 * 		The socket the feed receiver will listen to
	 * @param resourceFolder
	 * 		The folder where the instrument definition file and database 
	 * 		files will be stored
	 * @return
	 * 		Future referencing the instrument database update task.  The task 
	 * 		updates the local instrument definition file from a remote S3
	 * 		bucket and builds the local database if needed.  Task returns true
	 * 		upon successful completion.       
	 */
	public Future<Boolean> listenTCP(final int socketAddress, final File resourceFolder) {
		
		return listenTCP(socketAddress, false, resourceFolder);
		
	}

	/**
	 * Starts a stateless TCP connection to the specified port. If the user is
	 * already logged in, this call will end the previous connection and reset
	 * all registered market takers.  
	 * 
	 * @param socketAddress
	 *      The socket the feed receiver will listen to
	 * @param filterBySub
	 *      True if the receiver will filter messages based on registered
	 *      market takers
	 * @return
	 * 		Future referencing the instrument database update task.  The task 
	 * 		updates the local instrument definition file from a remote S3
	 * 		bucket and builds the local database if needed.  Task returns true
	 * 		upon successful completion.       
	 */
	public Future<Boolean> listenTCP(final int socketAddress) {

		return listenTCP(socketAddress, false, getTempFolder());

	}
	
	/* ***** ***** Begin all market taker methods ***** ***** */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <V extends Value<V>> boolean addAllMarketsTaker(
			final MarketTaker<V> taker) {
		return ((MakerBaseAllMarkets) maker).registerForAll(taker);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <V extends Value<V>> boolean updateAllMarketsTaker(
			final MarketTaker<V> taker) {
		return ((MakerBaseAllMarkets) maker).updateForAll(taker);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <V extends Value<V>> boolean removeAllMarketsTaker(
			final MarketTaker<V> taker) {
		return ((MakerBaseAllMarkets) maker).unregisterForAll(taker);
	}

	/*
	 * This is where the instruments are registered and unregistered as needed
	 * by the market maker. Subscribe events are sent when the instrument has
	 * not been bound by a previously registered market taker. Unsubscribe
	 * events are sent only when the instrument is not needed by any previously
	 * registered market takers.
	 */
	private final MarketRegListener instrumentSubscriptionListener =
			new MarketRegListener() {

				@Override
				public void onRegistrationChange(
						final Instrument instrument,
						final Set<MarketEvent> events) {

					/*
					 * The market maker denotes 'unsubscribe' with an empty
					 * event set
					 */
					if (events.isEmpty()) {
						log.debug("Unsubscribing to "
								+ instrument.get(InstrumentField.MARKET_GUID));
						feed.unsubscribe(new Subscription(instrument, events));
					} else {
						log.debug("Subscribing to "
								+ instrument.get(InstrumentField.MARKET_GUID)
								+ " Events: " + printEvents(events));
						feed.subscribe(new Subscription(instrument, events));
					}

				}

			};

	private String printEvents(final Set<MarketEvent> events) {
		final StringBuffer sb = new StringBuffer();

		for (final MarketEvent me : events) {
			sb.append(me.name() + ", ");
		}

		return sb.toString();
	}
	
	private File getTempFolder() {
		
		try {
			
			return File.createTempFile("temp", null).getParentFile();
			
		} catch (IOException e) {
			log.warn("Unable to retrieve system temp folder, using default {}", 
					TEMP_DIR);
			return new File(TEMP_DIR);
		}
		
	}

}
