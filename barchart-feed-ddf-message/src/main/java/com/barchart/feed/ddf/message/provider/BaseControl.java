/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import com.barchart.feed.ddf.message.api.DDF_ControlBase;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;

abstract class BaseControl extends Base implements DDF_ControlBase {

	BaseControl() {
		//
	}

	BaseControl(final DDF_MessageType messageType) {
		super(messageType);
	}

	//

}
