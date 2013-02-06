/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.api;

import com.barchart.feed.api.inst.InstrumentService;


/**
 * contract: instrument service should cache all previous requests.
 */
public interface DDF_DefinitionService extends
		InstrumentService<CharSequence> {

}
