/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider.pipeline;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.util.FeedDDF;

/**
 * implements minimal DDF TCP protocol validating state machine;
 * 
 * splits stream into DDF message frames;
 * 
 * TODO re-implement w/o "synchronized"
 */
public class MsgDeframerDDF extends FrameDecoder {

	private static Logger log = LoggerFactory.getLogger(MsgDeframerDDF.class);

	/** state machine: start/reset */
	private static final int S0_INIT = 0;
	/** state machine: received DDF message start marker */
	private static final int S1_DDF_START = 1;
	/** state machine: received DDF message finish marker */
	private static final int S2_DDF_FINISH = 2;
	/** state machine: received DDF time stamp start marker */
	private static final int S3_DDF_TIMESTAMP = 3;
	/** state machine: continue reading in text-only mode */
	private static final int S4_PRINTABLE = 4;
	/** state machine: protocol error; should not happen */
	@SuppressWarnings("unused")
	private static final int S5_ERROR = 5;

	private static final int TIME_STAMP_SIZE = 9;

	/** current stage of state machine */
	private int stage;
	/** number of validated incoming bytes */
	private int count;
	/** counter of bytes in DDF fixed size time stamp suffix */
	private int ender;

	private void init() {
		this.stage = S0_INIT;
		this.count = 0;
	}

	private ChannelBuffer init(final ChannelBuffer buffer, final int count) {

		final ChannelBuffer frame = buffer.readBytes(count);

		// Keep for debugging
		// log.debug("###############################################");
		// log.debug("### stage : {}", stage);
		// log.debug("### buffer.readerIndex : {}", buffer.readerIndex());
		// log.debug("### buffer :\n{}", new String(buffer.array()));
		// log.debug("### frame :\n{}", new String(frame.array()));
		// log.debug("### frame array :{}", Arrays.toString(frame.array()));
		// log.debug("### remaing buffer:{}", Arrays.toString(buffer.array()));

		this.stage = S0_INIT;
		this.count = 0;

		return frame;
	}

	public MsgDeframerDDF() {
		init();
	}

	/**
	 * assume JERQ uses LF terminators and optional fixed size time stamp
	 */
	@Override
	public synchronized Object decode(final ChannelHandlerContext ctx,
			final Channel channel, final ChannelBuffer buffer) throws Exception {

		while (true) {

			/* read more if needed */

			if (buffer.readableBytes() <= count) {
				return null;
			}

			/* read next byte; do not advance buffer */
			final int index = buffer.readerIndex() + count++;
			final byte alpha = buffer.getByte(index);

			/* UDP is not \n delimited, so must check for message start */
			if (alpha == FeedDDF.DDF_START && count != 1
					&& stage != S3_DDF_TIMESTAMP) {
				return init(buffer, count - 1);
			}

			/* handle state transitions */
			switch (stage) {

				case S0_INIT:
					switch (alpha) {
						case FeedDDF.DDF_START:
							stage = S1_DDF_START;
							continue;
						case FeedDDF.DDF_TERMINATE:
							// OK to post empty frames
							return init(buffer, count);
						default:
							stage = S4_PRINTABLE;
							continue;
					}

				case S1_DDF_START:
					switch (alpha) {
						case FeedDDF.DDF_FINISH:
							stage = S2_DDF_FINISH;
							continue;
						default:
							continue;
					}

				case S2_DDF_FINISH:
					switch (alpha) {
						case FeedDDF.DDF_CENTURY:
							// time stamp present
							stage = S3_DDF_TIMESTAMP;
							ender = 1; // start count
							continue;
						case FeedDDF.DDF_TERMINATE:
							// no time stamp send frame
							return init(buffer, count);
							// case FeedDDF.DDF_START:
							// /* for UDP */
							// return init(buffer, count - 1);
					}

				case S3_DDF_TIMESTAMP:
					ender++;
					/* continue stamp count, including terminator */
					if (ender == TIME_STAMP_SIZE + 1) {
						if (alpha == FeedDDF.DDF_TERMINATE) {
							return init(buffer, count);
						} else if (alpha == FeedDDF.DDF_START) {
							return init(buffer, count - 1);
						} else {
							buffer.readBytes(count);
							this.stage = S0_INIT;
							this.count = 0;
							return null;
						}
					} else {
						continue;
					}

				case S4_PRINTABLE:
					switch (alpha) {
						case FeedDDF.DDF_TERMINATE:
							return init(buffer, count);
						default:
							continue;
					}

				default:
					log.error("ddf frame decoder logic error");
					init();
					continue;
			}

		}

	}

	//

	private Thread thread;

	@SuppressWarnings("unused")
	private synchronized void checkTrhread() throws Exception {
		final Thread current = Thread.currentThread();
		if (thread == null) {
			thread = current;
		}
		if (thread != current) {
			throw new Exception("threads are different; " + thread.getName()
					+ " / " + current.getName());
		}
	}

}
