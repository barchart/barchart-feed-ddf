/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.util.anno.NotMutable;
import com.barchart.util.values.api.TextValue;

/**
 * represents ddf feed server response message.
 */
@NotMutable
public interface DDF_ControlResponse extends DDF_ControlBase {

	TextValue getComment();

}
