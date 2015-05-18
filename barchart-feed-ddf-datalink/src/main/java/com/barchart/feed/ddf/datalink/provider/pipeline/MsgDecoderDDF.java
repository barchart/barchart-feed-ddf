/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider.pipeline;

import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.provider.DDF_MessageService;
import com.barchart.feed.ddf.message.provider.DDF_SpreadParser;
import com.barchart.util.common.ascii.ASCII;

/**
 * convert DDF message frames into {@link DDF_BaseMessage} messages
 * <p>
 * Note on performance: DDF_MessageService decodes a byte array. It should be
 * faster to parse the ByteBuffer itself, saving the array creation, copy, and
 * GC.
 */
public class MsgDecoderDDF extends SimpleChannelHandler {

	static final Logger log = LoggerFactory.getLogger(MsgDecoderDDF.class);

	/**
	 * Instantiates a new msg decoder ddf.
	 */
	public MsgDecoderDDF() {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.jboss.netty.channel.SimpleChannelHandler#messageReceived(org.jboss
	 * .netty.channel.ChannelHandlerContext,
	 * org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(final ChannelHandlerContext context,
			final MessageEvent eventIn) throws Exception {

		final Object messageRAW = eventIn.getMessage();

		if (messageRAW instanceof ChannelBuffer) {

			/*
			 * this buffer is result of frame decoder (previous step in upstream
			 * pipeline); it fits single ddf message only w/o delimiters;
			 */
			final ChannelBuffer frameBuffer = (ChannelBuffer) messageRAW;

			/* underlying frame array */
			byte[] array = frameBuffer.array();

			/*
			 * silent ignore of invalid chunks sometimes sent by JERQ; DDF must
			 * have at least 1 command char and 1 terminator
			 */
			if (array.length < 2) {
				return;
			}

			/* If message is a spread, rebuild headder */
			if (array[1] == ASCII._S_) {
				array = DDF_SpreadParser.stripSpreadPreamble(array);
			}

			final DDF_BaseMessage messageDDF;

			try {
				messageDDF = DDF_MessageService.decode(array);
			} catch (final Exception e) {
				log.debug("decode failed : {} ", new String(array));
				log.debug(new String(Arrays.toString(array)));
				return;
			}

			final MessageEvent eventOut =
					new UpstreamMessageEvent(eventIn.getChannel(), messageDDF,
							null);

			context.sendUpstream(eventOut);

		} else {

			/*
			 * this is a non-ddf message event, such as connnect/disconnect or
			 * exception
			 */
			context.sendUpstream(eventIn);

		}

	}

}
