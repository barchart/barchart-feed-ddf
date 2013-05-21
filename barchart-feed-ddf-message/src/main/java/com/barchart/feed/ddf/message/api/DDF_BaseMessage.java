/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.util.anno.NotMutable;

/**
 * base type for all ddf/xml messasges;
 * 
 * includes: link control, market data, url lookup.
 */
@NotMutable
public interface DDF_BaseMessage {

	/**
	 * definitive ddf message type
	 */
	DDF_MessageType getMessageType();

	/**
	 * http://en.wikipedia.org/wiki/Visitor_pattern
	 */
	<Result, Param> Result accept(DDF_MessageVisitor<Result, Param> visitor,
			Param param);

	/**
	 * print serialized message form
	 */
	@Override
	String toString();

	/**
	 * pretty print individual message fields
	 */
	String toStringFields();

}
