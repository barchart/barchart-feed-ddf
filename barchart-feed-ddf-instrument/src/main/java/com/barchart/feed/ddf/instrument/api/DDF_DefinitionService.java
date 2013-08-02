/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.api;

import java.util.List;
import java.util.Map;

import com.barchart.feed.base.instrument.api.DefinitionService;
import com.barchart.util.values.api.TextValue;

/**
 * contract: instrument service should cache all previous requests.
 */
public interface DDF_DefinitionService extends
		DefinitionService<DDF_Instrument> {

	/**
	 * 
	 * @param symbolList
	 * @return
	 */
	List<DDF_Instrument> lookup(List<String> symbolList);

	/**
	 * 
	 * @param symbolList
	 * @return
	 */
	Map<String, DDF_Instrument> lookupMap(List<String> symbolList);
	
	/**
	 * 
	 * @param symbol
	 * @return
	 */
	DDF_Instrument lookupDDF(TextValue symbol);

}
