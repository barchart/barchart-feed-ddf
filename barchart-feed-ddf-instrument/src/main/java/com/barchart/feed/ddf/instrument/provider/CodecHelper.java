/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

import java.util.List;

import com.barchart.util.values.api.TextValue;

final class CodecHelper {

	private CodecHelper() {
	}

	static boolean isEmpty(final TextValue symbol) {
		return (symbol == null || symbol.length() == 0);
	}

	static boolean isEmpty(final List<?> list) {
		return (list == null || list.size() == 0);
	}

}
