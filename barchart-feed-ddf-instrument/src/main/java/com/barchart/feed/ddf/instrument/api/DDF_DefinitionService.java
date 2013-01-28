/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.api;

import java.util.List;

import com.barchart.feed.inst.api.InstrumentService;
import com.barchart.util.values.api.TextValue;


/**
 * contract: instrument service should cache all previous requests.
 */
public interface DDF_DefinitionService extends
		InstrumentService {

	/**
	 * 
	 * @param symbolList
	 * @return
	 */
	List<DDF_Instrument> lookupDDF(List<String> symbolList);

	/**
	 * 
	 * @param symbol
	 * @return
	 */
	DDF_Instrument lookupDDF(TextValue symbol);

}
