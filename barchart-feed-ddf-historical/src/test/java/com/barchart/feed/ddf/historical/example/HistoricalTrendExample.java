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

import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.ddf.historical.api.DDF_EntryTrend;
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
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.provider.ValueBuilder;
import com.barchart.util.values.util.ValueUtil;

/**
 * The Class HistoricalTrendExample.
 */

public class HistoricalTrendExample {

	private static final Logger log = LoggerFactory
			.getLogger(HistoricalTrendExample.class);

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

		final String symbol = "C";

		final Instrument instrument = //
		DDF_InstrumentProvider.find(symbol);

		if (instrument.isNull()) {
			log.error("can not get insrument for : {}", symbol);
			return;
		}

		log.info("insrument : {}", instrument);

		/*
		 * 3) define ticks TREND query parameters
		 */

		// final DDF_Query<DDF_EntryTrend> query = //
		// DDF_HistoricalService.newQueryTrendEod();

		final DDF_Query<DDF_EntryTrend> query = //
		DDF_HistoricalService.newQueryTrendMins();

		query.instrument = instrument;

		query.timeStart = //
		new DateTime(2011, 01, 14, /**/14, 00, 00, /**/
		000, DDF_TimeZone.NEW_YORK.zone);

		query.timeEnd = query.timeStart.plusMinutes(100);

		query.groupBy = 15;

		/*
		 * 4) obtain query result
		 */

		final StopWatch timer = new StopWatch();

		timer.start();

		final DDF_Result<DDF_EntryTrend> result = //
		DDF_HistoricalService.newResult(settings, query, null);

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

		final Runner<Void, DDF_EntryTrend> taskFindExtreme = //
		new Runner<Void, DDF_EntryTrend>() {
			@Override
			public Void run(final DDF_EntryTrend entry) {

				final long mantissa = entry.priceResistance();
				final int exponent = entry.priceExponent();

				final PriceValue priceValue = ValueBuilder.newPrice(mantissa,
						exponent);

				final double priceAsDouble = priceValue.asDouble();

				log.info("priceValue={}; priceAsDouble={}", priceValue,
						priceAsDouble);

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
