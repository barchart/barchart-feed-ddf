/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.enums;

import com.barchart.feed.ddf.historical.api.DDF_Entry;
import com.barchart.feed.ddf.historical.api.DDF_EntryBarEod;
import com.barchart.feed.ddf.historical.api.DDF_EntryBarMin;
import com.barchart.feed.ddf.historical.api.DDF_EntryBarMinFormT;
import com.barchart.feed.ddf.historical.api.DDF_EntryBarMinNearby;
import com.barchart.feed.ddf.historical.api.DDF_EntryTick;
import com.barchart.feed.ddf.historical.api.DDF_EntryTrend;
import com.barchart.util.anno.NotMutable;
import com.barchart.util.enums.DictEnum;
import com.barchart.util.enums.EnumCodeString;
import com.barchart.util.enums.ParaEnumBase;

@NotMutable
public class DDF_QueryType<V extends DDF_Entry> extends
		ParaEnumBase<V, DDF_QueryType<V>> implements EnumCodeString {

	/** default */
	public static final DDF_QueryType<DDF_EntryTick> TICKS = NEW(
			"queryticks.ashx", "ticks");

	public static final DDF_QueryType<DDF_EntryBarMin> MINUTES = NEW(
			"queryminutes.ashx", "mins");

	public static final DDF_QueryType<DDF_EntryBarMinNearby> MINUTES_NEARBY = NEW(
			"queryminutes.ashx", "mins_nearby");

	public static final DDF_QueryType<DDF_EntryBarMinFormT> MINUTES_FORM_T = NEW(
			"queryformtminutes.ashx", "mins_form_t");

	public static final DDF_QueryType<DDF_EntryBarEod> END_OF_DAY = NEW(
			"queryeod.ashx", "end_of_day");

	//

	public static final DDF_QueryType<DDF_EntryTrend> TICKS_TREND = NEW(
			"queryticks.ashx", "ticks");

	public static final DDF_QueryType<DDF_EntryTrend> MINUTES_TREND = NEW(
			"queryminutes.ashx", "mins");

	public static final DDF_QueryType<DDF_EntryTrend> END_OF_DAY_TREND = NEW(
			"queryeod.ashx", "end_of_day");

	// ##################################

	private static final DDF_QueryType<?>[] ENUM_VALUES_FUTURE = //
	new DDF_QueryType<?>[] { TICKS, MINUTES, MINUTES_NEARBY, END_OF_DAY };

	public final static DDF_QueryType<?>[] valuesFuture() {
		return ENUM_VALUES_FUTURE.clone();
	}

	private static final DDF_QueryType<?>[] ENUM_VALUES_NON_FUTURE = //
	new DDF_QueryType<?>[] { TICKS, MINUTES, MINUTES_FORM_T, END_OF_DAY };

	public final static DDF_QueryType<?>[] valuesNonFuture() {
		return ENUM_VALUES_NON_FUTURE.clone();
	}

	//

	public static final DDF_QueryType<?> fromCode(final String code) {
		for (final DDF_QueryType<?> known : values()) {
			if (known.code.equalsIgnoreCase(code)) {
				return known;
			}
		}
		return TICKS;
	}

	public static final int size() {
		return 0;
	}

	public static final DDF_QueryType<?>[] values() {
		return DictEnum.valuesForType(DDF_QueryType.class);
	}

	// ##################################

	/** ddf quuery lookup page url */
	public final String queryPage;

	public final String code;

	/** ddf query type xml encoding */
	@Override
	public final String code() {
		return code;
	}

	private DDF_QueryType() {
		this.queryPage = null;
		this.code = null;
	}

	private DDF_QueryType(final String queryPage, final String code) {
		super("", null);
		this.queryPage = queryPage;
		this.code = code;
	}

	private static <X extends DDF_Entry> DDF_QueryType<X> NEW(
			final String queryPage, final String code) {
		return new DDF_QueryType<X>(queryPage, code);
	}

}
