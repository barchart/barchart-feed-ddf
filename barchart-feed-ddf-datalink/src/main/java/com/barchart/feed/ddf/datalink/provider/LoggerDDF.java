/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.logging.LoggingHandler;

/**
 * used for debugging only to visualize incoming frames
 */
class LoggerDDF extends LoggingHandler {

	@Override
	public void log(ChannelEvent e) {

		if (getLogger().isEnabled(getLevel())) {

			String msg = e.toString();

			if (e instanceof MessageEvent) {

				MessageEvent me = (MessageEvent) e;

				if (me.getMessage() instanceof ChannelBuffer) {

					ChannelBuffer buffer = (ChannelBuffer) me.getMessage();

					msg = msg + " - TEXT \n" + textDump(buffer);

				}

			}

			// Log the message (and exception if available.)
			if (e instanceof ExceptionEvent) {
				getLogger().log(getLevel(), msg,
						((ExceptionEvent) e).getCause());
			} else {
				getLogger().log(getLevel(), msg);
			}
		}

	}

	static String textDump(ChannelBuffer buffer) {

		int index = buffer.readerIndex();

		int length = buffer.readableBytes();

		byte[] source = buffer.array();

		byte[] target = new byte[length];

		System.arraycopy(source, index, target, index, length);

		String text = new String(target);

		return text;

	}

}
