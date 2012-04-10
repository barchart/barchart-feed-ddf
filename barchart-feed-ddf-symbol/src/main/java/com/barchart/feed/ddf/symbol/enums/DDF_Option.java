/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.enums;

public enum DDF_Option {

	/*
	 * CALL/PUT IND 1 Byte, Alphabetic, indication of call or put for Options or
	 * Futures
	 */

	CALL('C'), //

	PUT('P'), // 

	UNKNOWN('?'), //

	;

	public final char code;

	private DDF_Option(final char code) {
		this.code = code;
	}

	public final static DDF_Option fromCode(final char code) {
		switch (code) {
		case 'c':
		case 'C':
			return CALL;
		case 'p':
		case 'P':
			return PUT;
		default:
			return UNKNOWN;
		}
	}

	public final static boolean isValid(final char code) {
		final DDF_Option indicator = fromCode(code);
		return indicator != UNKNOWN;
	}

	public final static DDF_Option fromOptionYear(final DDF_OptionYear indicator) {
		switch (indicator) {
		case CALL_0:
		case CALL_1:
		case CALL_2:
		case CALL_3:
		case CALL_4:
		case CALL_5:
		case CALL_6:
		case CALL_7:
		case CALL_8:
		case CALL_9:
			return CALL;
		case PUT_0:
		case PUT_1:
		case PUT_2:
		case PUT_3:
		case PUT_4:
		case PUT_5:
		case PUT_6:
		case PUT_7:
		case PUT_8:
		case PUT_9:
			return PUT;
		default:
			return UNKNOWN;
		}
	}

}
