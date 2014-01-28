/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.example;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.observables.BlockingObservable;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.thread.Runner;
import com.barchart.feed.ddf.historical.api.DDF_EntryTick;
import com.barchart.feed.ddf.historical.api.DDF_Query;
import com.barchart.feed.ddf.historical.api.DDF_Result;
import com.barchart.feed.ddf.historical.provider.DDF_HistoricalService;
import com.barchart.feed.ddf.instrument.provider.DDF_RxInstrumentProvider;
import com.barchart.feed.ddf.settings.api.DDF_Server;
import com.barchart.feed.ddf.settings.api.DDF_Settings;
import com.barchart.feed.ddf.settings.enums.DDF_ServerType;
import com.barchart.feed.ddf.settings.provider.DDF_SettingsService;
import com.barchart.feed.ddf.symbol.enums.DDF_TimeZone;
import com.barchart.util.common.bench.StopWatch;

// TODO: Auto-generated Javadoc
/**
 * http://ds01.ddfplus.com/historical/queryticks.ashx?username=USER
 * &password=PASS&symbol=GOOG &start=20090203090000 &end=20090203120000
 */

public class HistoricalTicksExample {

	private static final Logger log = LoggerFactory.getLogger(HistoricalTicksExample.class);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public final static void main(final String[] args) {

		/*
		 * 1) obtain user access settings
		 */

		final String username = System.getProperty("barchart.username");
		final String password = System.getProperty("barchart.password");

		final DDF_Settings settings = //
		DDF_SettingsService.newSettings(username, password);
		if (!settings.isValidLogin()) {
			log.error("can not get settings : {}", settings);
			return;
		}

		final DDF_Server server = //
		settings.getServer(DDF_ServerType.HISTORICAL_V2);
		log.info("historical ddfplus server : {}", server);

		/*
		 * 2) lookup instrument definition
		 */

		final String symbol = "ESH2014";

		final Instrument instrument = //
				BlockingObservable.from(DDF_RxInstrumentProvider.fromString(symbol))
						.single().results().get(symbol).get(0);

		if (instrument.isNull()) {
			log.error("can not get insrument for : {}", symbol);
			return;
		}

		log.info("insrument : {}", instrument);

		/*
		 * 3) define ticks query parameters
		 */

		final DDF_Query<DDF_EntryTick> query = //
		DDF_HistoricalService.newQueryTicks();

		query.instrument = instrument;

		query.timeStart = //
				new DateTime(2013, 06, 01, /**/00, 00, 00, /**/
		000, DDF_TimeZone.CHICAGO.zone);

		query.timeEnd = query.timeStart.plusMinutes(10);

		/*
		 * 4) obtain query result
		 */

		final StopWatch timer = new StopWatch();

		timer.start();

		final DDF_Result<DDF_EntryTick> result = //
		DDF_HistoricalService.newResultTicks(settings, query, null);

		timer.stop();

		log.info("result status  : {}", result.getStatus());
		log.info("status comment : {}", result.getStatusComment());

		final int size = result.size();
		if (size > 0) {
			log.info("nanos/entry : {}", timer.getDiff() / size);
		}
		log.info("total time  : {}", timer.getDiff());

		log.info("result : \n{}", result);

		/*
		 * 5) utilize the result to find min/max price
		 */

		final PriceExtreme extreme = new PriceExtreme(instrument);

		final Runner<Void, DDF_EntryTick> taskFindExtreme = //
		new Runner<Void, DDF_EntryTick>() {
			@Override
			public Void run(final DDF_EntryTick entry) {
				final long mantissa = entry.priceTradeMantissa();
				if (mantissa > extreme.mantissaMax) {
					extreme.mantissaMax = mantissa;
				}
				if (mantissa < extreme.mantissaMin) {
					extreme.mantissaMin = mantissa;
				}
				return null;
			}
		};

		result.runLoop(taskFindExtreme, null);

		log.info("extreme : {}", extreme);

	}

}
