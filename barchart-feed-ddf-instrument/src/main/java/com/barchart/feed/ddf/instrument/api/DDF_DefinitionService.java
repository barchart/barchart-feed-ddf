/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.api;

import java.util.List;

import com.barchart.feed.base.api.instrument.DefinitionService;
import com.barchart.util.values.api.TextValue;

/**
 * contract: instrument service should cache all previous requests
 */
public interface DDF_DefinitionService extends
		DefinitionService<DDF_Instrument> {

	List<DDF_Instrument> lookup(List<String> symbolList);

	DDF_Instrument lookupDDF(TextValue symbol);

}
