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

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.ddf.historical.api.DDF_EntryBarEod;
import com.barchart.feed.ddf.historical.api.DDF_Query;
import com.barchart.feed.ddf.historical.api.DDF_Result;
import com.barchart.feed.ddf.historical.provider.DDF_HistoricalService;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.feed.ddf.settings.api.DDF_Server;
import com.barchart.feed.ddf.settings.api.DDF_Settings;
import com.barchart.feed.ddf.settings.enums.DDF_ServerType;
import com.barchart.feed.ddf.settings.provider.DDF_SettingsService;
import com.barchart.feed.ddf.symbol.enums.DDF_TimeZone;
import com.barchart.util.bench.time.StopWatch;
import com.barchart.util.thread.Runner;

// TODO: Auto-generated Javadoc
/**
 * The Class HistoricalEodExample.
 */

public class HistoricalEodExample {

	private static final Logger log = LoggerFactory
			.getLogger(HistoricalEodExample.class);

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
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

		final String symbol = "EGM2";

		final Instrument instrument = //
				DDF_InstrumentProvider.findHistorical(symbol);

		if (instrument.isNull()) {
			log.error("can not get insrument for : {}", symbol);
			return;
		}

		log.info("insrument : {}", instrument.toString());

		/*
		 * 3) define ticks query parameters
		 */

		final DDF_Query<DDF_EntryBarEod> query = //
				DDF_HistoricalService.newQueryEod();

		query.instrument = instrument;

		query.timeStart = //
				new DateTime(2012, 04, 20, /**/00, 00, 00, /**/
				000, DDF_TimeZone.CHICAGO.zone);

		query.timeEnd = query.timeStart.plusDays(20);

		/*
		 * 4) obtain query result
		 */

		final StopWatch timer = new StopWatch();

		timer.start();

		final DDF_Result<DDF_EntryBarEod> result = //
				DDF_HistoricalService.newResultEod(settings, query, null);

		timer.stop();

		log.info("result status  : {}", result.getStatus());
		log.info("status comment : {}", result.getStatusComment());

		final int size = result.size();
		if (size > 0) {
			log.info("nanos/entry : {}", timer.getDiff() / size);
		}
		log.info("total time  : {}", timer.toStringPretty());

		log.info("result : \n{}", result);

		/*
		 * 5) utilize the result to find min/max price
		 */

		final PriceExtreme extreme = new PriceExtreme(instrument);

		final Runner<Void, DDF_EntryBarEod> taskFindExtreme = //
				new Runner<Void, DDF_EntryBarEod>() {
					@Override
					public Void run(final DDF_EntryBarEod entry) {
						log.debug(String.valueOf(entry.sizeVolume()));
						return null;
					}
				};

		result.runLoop(taskFindExtreme, null);

		log.info("extreme : {}", extreme);

	}

}
