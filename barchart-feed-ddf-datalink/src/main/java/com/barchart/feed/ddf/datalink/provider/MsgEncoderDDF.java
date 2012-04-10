/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;

/** convert outgoing DDF string commands into NETTY channel buffers */
class MsgEncoderDDF extends StringEncoder {

	/**
	 * Instantiates a new msg encoder ddf.
	 */
	public MsgEncoderDDF() {
		super((CharsetUtil.US_ASCII));
	}

}
