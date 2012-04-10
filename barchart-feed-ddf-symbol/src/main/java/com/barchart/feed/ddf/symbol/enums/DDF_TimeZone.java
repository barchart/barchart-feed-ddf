/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.enums;

import org.joda.time.DateTimeZone;

import com.barchart.util.enums.EnumCodeString;
import com.barchart.util.math.MathExtra;
import com.barchart.util.values.api.Value;

/**
 * ddf centric time zones
 * 
 * http://en.wikipedia.org/wiki/Time_zone
 * 
 */
public enum DDF_TimeZone implements EnumCodeString, Value<DDF_TimeZone> {

	/**
	 * UTC
	 */
	UTC("UTC", DateTimeZone.UTC), //

	/**
	 * ddf future-like symbols are encoded in Chicago time
	 */
	CHICAGO("America/Chicago", DateTimeZone.forID("America/Chicago")), //

	/**
	 * ddf stock-like symbols are encoded in New York time
	 */
	NEW_YORK("America/New_York", DateTimeZone.forID("America/New_York")), //

	/**
	 * default or local time zone
	 */
	LOCAL("", DateTimeZone.getDefault()), //

	;

	public final byte ord;

	public final String code;

	@Override
	public final String code() {
		return code;
	}

	public final DateTimeZone zone;

	private DDF_TimeZone(final String code, final DateTimeZone zone) {
		this.ord = (byte) ordinal();
		this.code = code;
		// if (code == _Z_) {
		// this.zone = DateTimeZone.getDefault();
		// } else{
		// this.zone = DateTimeZone.getDefault();
		// }
		this.zone = zone;
	}

	private final static DDF_TimeZone[] ENUM_VALUES = values();

	@Deprecated
	public final static DDF_TimeZone[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	public final boolean isKnown() {
		return this != LOCAL;
	}

	public final static DDF_TimeZone fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	public final static DDF_TimeZone fromCode(final String code) {
		for (final DDF_TimeZone known : ENUM_VALUES) {
			if (known.code.equalsIgnoreCase(code)) {
				return known;
			}
		}
		return LOCAL;
	}

	@Override
	public DDF_TimeZone freeze() {
		return this;
	}

	@Override
	public boolean isFrozen() {
		return true;
	}

	@Override
	public boolean isNull() {
		return this == LOCAL;
	}

}
