/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import static com.barchart.feed.ddf.historical.enums.DDF_QueryType.END_OF_DAY;
import static com.barchart.feed.ddf.historical.enums.DDF_QueryType.END_OF_DAY_TREND;
import static com.barchart.feed.ddf.historical.enums.DDF_QueryType.MINUTES;
import static com.barchart.feed.ddf.historical.enums.DDF_QueryType.MINUTES_FORM_T;
import static com.barchart.feed.ddf.historical.enums.DDF_QueryType.MINUTES_NEARBY;
import static com.barchart.feed.ddf.historical.enums.DDF_QueryType.MINUTES_TREND;
import static com.barchart.feed.ddf.historical.enums.DDF_QueryType.TICKS;
import static com.barchart.feed.ddf.historical.enums.DDF_QueryType.TICKS_FORM_T;
import static com.barchart.feed.ddf.historical.enums.DDF_QueryType.TICKS_TREND;
import static com.barchart.feed.ddf.historical.provider.CodecHelper.KEYWORD_ERROR;
import static com.barchart.feed.ddf.historical.provider.CodecHelper.checkNull;
import static com.barchart.feed.ddf.historical.provider.CodecHelper.urlQuery;
import static com.barchart.feed.ddf.historical.provider.ConstHistorical.STATUS_COUNT;
import static com.barchart.feed.ddf.historical.provider.ConstHistorical.STATUS_EMPTY;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.data.Instrument;
import com.barchart.feed.ddf.historical.api.DDF_Entry;
import com.barchart.feed.ddf.historical.api.DDF_EntryBarEod;
import com.barchart.feed.ddf.historical.api.DDF_EntryBarMin;
import com.barchart.feed.ddf.historical.api.DDF_EntryBarMinFormT;
import com.barchart.feed.ddf.historical.api.DDF_EntryBarMinNearby;
import com.barchart.feed.ddf.historical.api.DDF_EntryTick;
import com.barchart.feed.ddf.historical.api.DDF_EntryTickFormT;
import com.barchart.feed.ddf.historical.api.DDF_EntryTrend;
import com.barchart.feed.ddf.historical.api.DDF_Query;
import com.barchart.feed.ddf.historical.api.DDF_Result;
import com.barchart.feed.ddf.historical.api.DDF_ResultEmptyException;
import com.barchart.feed.ddf.historical.api.DDF_ResultInterruptedException;
import com.barchart.feed.ddf.historical.api.DDF_ResultListener;
import com.barchart.feed.ddf.historical.enums.DDF_QueryEodType;
import com.barchart.feed.ddf.historical.enums.DDF_QueryEodVolume;
import com.barchart.feed.ddf.historical.enums.DDF_QueryOrder;
import com.barchart.feed.ddf.historical.enums.DDF_QueryType;
import com.barchart.feed.ddf.historical.enums.DDF_ResultStatus;
import com.barchart.feed.ddf.settings.api.DDF_Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class DDF_HistoricalService.
 */
public final class DDF_HistoricalService {

	private static final Logger log = LoggerFactory
			.getLogger(DDF_HistoricalService.class);

	private DDF_HistoricalService() {
	}

	// query ///////////////////////////

	/**
	 * New query.
	 *
	 * @param <E> the element type
	 * @param type the type
	 * @return the dD f_ query
	 */
	public static final <E extends DDF_Entry> DDF_Query<E> newQuery(
			DDF_QueryType<E> type) {
		return new DDF_Query<E>(type);
	}

	/**
	 * New query ticks.
	 *
	 * @return the dD f_ query
	 */
	public static final DDF_Query<DDF_EntryTick> newQueryTicks() {
		return newQuery(TICKS);
	}
	
	public static final DDF_Query<DDF_EntryTickFormT> newQueryTicksFormT() {
		return newQuery(TICKS_FORM_T);
	}

	/**
	 * New query mins.
	 *
	 * @return the dD f_ query
	 */
	public static final DDF_Query<DDF_EntryBarMin> newQueryMins() {
		return newQuery(MINUTES);
	}

	/**
	 * New query mins nearby.
	 *
	 * @return the dD f_ query
	 */
	public static final DDF_Query<DDF_EntryBarMinNearby> newQueryMinsNearby() {
		return newQuery(MINUTES_NEARBY);
	}

	/**
	 * New query mins form t.
	 *
	 * @return the dD f_ query
	 */
	public static final DDF_Query<DDF_EntryBarMinFormT> newQueryMinsFormT() {
		return newQuery(MINUTES_FORM_T);
	}

	/**
	 * New query eod.
	 *
	 * @return the dD f_ query
	 */
	public static final DDF_Query<DDF_EntryBarEod> newQueryEod() {
		return newQuery(END_OF_DAY);
	}

	// result ///////////////////////////

	/**
	 * New result.
	 *
	 * @param <E> the element type
	 * @param settings the settings
	 * @param query the query
	 * @param listener the listener
	 * @return the dD f_ result
	 * @throws RuntimeException the runtime exception
	 */
	@SuppressWarnings("unchecked")
	public static final <E extends DDF_Entry> DDF_Result<E> newResult(
			final DDF_Settings settings, /* local */DDF_Query<E> query,
			final DDF_ResultListener listener) throws RuntimeException {

		checkNull(query, "query is null");
		checkNull(query.type, "query type is null");

		// detach from source
		query = query.clone();

		final String stringURL = urlQuery(settings, query);
		log.debug("stringURL : \n\t {}", stringURL);

		final Builder builder = Builder.from(query.type);

		final Instrument instrument = query.instrument;

		final E entryReference = (E) builder.newEntry(0, null, instrument);

		final Result<E> result = new Result<E>(query, entryReference, listener);

		// set url query for debugging
		
		result.urlQuery = stringURL;
		
		String firstLine = "";
		int index = 0;

		try {

			final ZipReader reader = ZipReader.fromURL(stringURL);

			while (true) {
				final String inputLine = reader.readLine();
				if (inputLine == null) {
					break;
				}
				if (inputLine.length() == 0) {
					continue;
				}
				// log.info("inputLine : {}", inputLine);
				if (index == 0) {
					firstLine = inputLine;
					if (firstLine.contains(KEYWORD_ERROR)) {
						index++;
						break;
					}
				}
				final E entry = (E) builder.newEntry(index, inputLine,
						instrument);
				result.add(index, entry);
				index++;
			}

			reader.close();

			if (index == 0) {
				result.status = DDF_ResultStatus.SUCCESS;
				result.statusComment = STATUS_EMPTY;
			} else if (firstLine.contains(KEYWORD_ERROR)) {
				result.status = DDF_ResultStatus.ERROR;
				result.statusComment = firstLine;
			} else {
				result.status = DDF_ResultStatus.SUCCESS;
				result.statusComment = STATUS_COUNT + index;
			}

		} catch (final DDF_ResultInterruptedException e) {

			log.debug("query lookup interrupted; query={}", query);

			result.status = DDF_ResultStatus.INTERRUPTED;
			result.statusComment = e.getMessage() + " at " + index;

		} catch (final DDF_ResultEmptyException e) {

			log.debug("got an empty server page; query={}", query);

			result.status = DDF_ResultStatus.SUCCESS;
			result.statusComment = STATUS_COUNT + 0;

		} catch (final Exception e) {

			log.debug("query lookup failed; query={}", query);
			log.error("query lookup failed", e);

			result.status = DDF_ResultStatus.ERROR;
			result.statusComment = e.getMessage();

		}

		return result;

	}

	// result ticks ///////////////////////////

	/**
	 * New result ticks.
	 *
	 * @param settings the settings
	 * @param query the query
	 * @param listener the listener
	 * @return the dD f_ result
	 */
	public static final DDF_Result<DDF_EntryTick> newResultTicks(
			final DDF_Settings settings, final DDF_Query<DDF_EntryTick> query,
			final DDF_ResultListener listener) {
		return newResult(settings, query, listener);
	}

	/**
	 * New result ticks.
	 *
	 * @param settings the settings
	 * @param instrument the instrument
	 * @param timeStart the time start
	 * @param timeEnd the time end
	 * @param resultOrder the result order
	 * @param maxRecords the max records
	 * @param listener the listener
	 * @return the dD f_ result
	 * @throws RuntimeException the runtime exception
	 */
	public static final DDF_Result<DDF_EntryTick> newResultTicks(
			final DDF_Settings settings, final Instrument instrument,
			final DateTime timeStart, final DateTime timeEnd,
			final DDF_QueryOrder resultOrder, final int maxRecords,
			final DDF_ResultListener listener) throws RuntimeException {

		final DDF_Query<DDF_EntryTick> query = newQueryTicks();

		query.instrument = instrument;
		query.timeStart = timeStart;
		query.timeEnd = timeEnd;
		query.resultOrder = resultOrder;
		query.maxRecords = maxRecords;

		return newResult(settings, query, listener);

	}
	
	public static final DDF_Result<DDF_EntryTickFormT> newResultTicksFormT(
			final DDF_Settings settings, final DDF_Query<DDF_EntryTickFormT> query,
			final DDF_ResultListener listener) {
		return newResult(settings, query, listener);
	}

	public static final DDF_Result<DDF_EntryTickFormT> newResultTicksFormT(
			final DDF_Settings settings, final Instrument instrument,
			final DateTime timeStart, final DateTime timeEnd,
			final DDF_QueryOrder resultOrder, final int maxRecords,
			final DDF_ResultListener listener) throws RuntimeException {

		final DDF_Query<DDF_EntryTickFormT> query = newQueryTicksFormT();

		query.instrument = instrument;
		query.timeStart = timeStart;
		query.timeEnd = timeEnd;
		query.resultOrder = resultOrder;
		query.maxRecords = maxRecords;

		return newResult(settings, query, listener);

	}

	// result mins ///////////////////////////

	/**
	 * New result mins.
	 *
	 * @param settings the settings
	 * @param query the query
	 * @param listener the listener
	 * @return the dD f_ result
	 */
	public static final DDF_Result<DDF_EntryBarMin> newResultMins(
			final DDF_Settings settings,
			final DDF_Query<DDF_EntryBarMin> query,
			final DDF_ResultListener listener) {
		return newResult(settings, query, listener);
	}

	/**
	 * New result mins.
	 *
	 * @param settings the settings
	 * @param instrument the instrument
	 * @param timeStart the time start
	 * @param timeEnd the time end
	 * @param resultOrder the result order
	 * @param maxRecords the max records
	 * @param groupBy the group by
	 * @param listener the listener
	 * @return the dD f_ result
	 * @throws RuntimeException the runtime exception
	 */
	public static final DDF_Result<DDF_EntryBarMin> newResultMins(
			final DDF_Settings settings, final Instrument instrument,
			final DateTime timeStart, final DateTime timeEnd,
			final DDF_QueryOrder resultOrder, final int maxRecords,
			final int groupBy, final DDF_ResultListener listener)
			throws RuntimeException {

		final DDF_Query<DDF_EntryBarMin> query = newQueryMins();

		query.instrument = instrument;
		query.timeStart = timeStart;
		query.timeEnd = timeEnd;
		query.resultOrder = resultOrder;
		query.maxRecords = maxRecords;
		query.groupBy = groupBy;

		return newResult(settings, query, listener);

	}

	// result mins nearby ///////////////////////////

	/**
	 * New result mins nearby.
	 *
	 * @param settings the settings
	 * @param query the query
	 * @param listener the listener
	 * @return the dD f_ result
	 */
	public static final DDF_Result<DDF_EntryBarMinNearby> newResultMinsNearby(
			final DDF_Settings settings,
			final DDF_Query<DDF_EntryBarMinNearby> query,
			final DDF_ResultListener listener) {
		return newResult(settings, query, listener);
	}

	/**
	 * New result mins near by.
	 *
	 * @param settings the settings
	 * @param instrument the instrument
	 * @param timeStart the time start
	 * @param timeEnd the time end
	 * @param resultOrder the result order
	 * @param maxRecords the max records
	 * @param groupBy the group by
	 * @param listener the listener
	 * @return the dD f_ result
	 * @throws RuntimeException the runtime exception
	 */
	public static final DDF_Result<DDF_EntryBarMinNearby> newResultMinsNearBy(
			final DDF_Settings settings, final Instrument instrument,
			final DateTime timeStart, final DateTime timeEnd,
			final DDF_QueryOrder resultOrder, final int maxRecords,
			final int groupBy, final DDF_ResultListener listener)
			throws RuntimeException {

		// TODO check for future

		final DDF_Query<DDF_EntryBarMinNearby> query = newQueryMinsNearby();

		query.instrument = instrument;
		query.timeStart = timeStart;
		query.timeEnd = timeEnd;
		query.resultOrder = resultOrder;
		query.maxRecords = maxRecords;
		query.groupBy = groupBy;

		return newResult(settings, query, listener);

	}

	// result mins form t ///////////////////////////

	/**
	 * New result mins form t.
	 *
	 * @param settings the settings
	 * @param query the query
	 * @param listener the listener
	 * @return the dD f_ result
	 */
	public static final DDF_Result<DDF_EntryBarMinFormT> newResultMinsFormT(
			final DDF_Settings settings,
			final DDF_Query<DDF_EntryBarMinFormT> query,
			final DDF_ResultListener listener) {
		return newResult(settings, query, listener);
	}

	/**
	 * New result mins form t.
	 *
	 * @param settings the settings
	 * @param instrument the instrument
	 * @param timeStart the time start
	 * @param timeEnd the time end
	 * @param resultOrder the result order
	 * @param maxRecords the max records
	 * @param groupBy the group by
	 * @param listener the listener
	 * @return the dD f_ result
	 * @throws RuntimeException the runtime exception
	 */
	public static final DDF_Result<DDF_EntryBarMinFormT> newResultMinsFormT(
			final DDF_Settings settings, final Instrument instrument,
			final DateTime timeStart, final DateTime timeEnd,
			final DDF_QueryOrder resultOrder, final int maxRecords,
			final int groupBy, final DDF_ResultListener listener)
			throws RuntimeException {

		// TODO check for stock

		final DDF_Query<DDF_EntryBarMinFormT> query = newQueryMinsFormT();

		query.instrument = instrument;
		query.timeStart = timeStart;
		query.timeEnd = timeEnd;
		query.resultOrder = resultOrder;
		query.maxRecords = maxRecords;
		query.groupBy = groupBy;

		return newResult(settings, query, listener);

	}

	// result e.o.d ///////////////////////////

	/**
	 * New result eod.
	 *
	 * @param settings the settings
	 * @param query the query
	 * @param listener the listener
	 * @return the dD f_ result
	 */
	public static final DDF_Result<DDF_EntryBarEod> newResultEod(
			final DDF_Settings settings,
			final DDF_Query<DDF_EntryBarEod> query,
			final DDF_ResultListener listener) {
		return newResult(settings, query, listener);
	}

	/**
	 * New result eod.
	 *
	 * @param settings the settings
	 * @param instrument the instrument
	 * @param timeStart the time start
	 * @param timeEnd the time end
	 * @param resultOrder the result order
	 * @param maxRecords the max records
	 * @param eodType the eod type
	 * @param eodVolume the eod volume
	 * @param listener the listener
	 * @return the dD f_ result
	 * @throws RuntimeException the runtime exception
	 */
	public static final DDF_Result<DDF_EntryBarEod> newResultEod(
			final DDF_Settings settings, final Instrument instrument,
			final DateTime timeStart, final DateTime timeEnd,
			final DDF_QueryOrder resultOrder, final int maxRecords,
			final DDF_QueryEodType eodType, final DDF_QueryEodVolume eodVolume,
			final DDF_ResultListener listener) throws RuntimeException {

		final DDF_Query<DDF_EntryBarEod> query = newQueryEod();

		query.instrument = instrument;
		query.timeStart = timeStart;
		query.timeEnd = timeEnd;
		query.resultOrder = resultOrder;
		query.maxRecords = maxRecords;
		query.eodType = eodType;
		query.eodVolume = eodVolume;

		return newResult(settings, query, listener);

	}

	//

	/**
	 * New query trend ticks.
	 *
	 * @return the dD f_ query
	 */
	public static final DDF_Query<DDF_EntryTrend> newQueryTrendTicks() {
		return newQuery(TICKS_TREND);
	}

	/**
	 * New query trend mins.
	 *
	 * @return the dD f_ query
	 */
	public static final DDF_Query<DDF_EntryTrend> newQueryTrendMins() {
		return newQuery(MINUTES_TREND);
	}

	/**
	 * New query trend eod.
	 *
	 * @return the dD f_ query
	 */
	public static final DDF_Query<DDF_EntryTrend> newQueryTrendEod() {
		return newQuery(END_OF_DAY_TREND);
	}

}
