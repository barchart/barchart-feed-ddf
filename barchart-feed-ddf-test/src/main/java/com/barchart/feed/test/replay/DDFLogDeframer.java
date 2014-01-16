/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.test.replay;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.joda.time.ReadableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
class DDFLogDeframer {

	private static Logger log = LoggerFactory.getLogger(DDFLogDeframer.class);

	/** ddf time stamp encoding/decoding mask */
	final static int DDF_TIME_STAMP_MASK = 0x40;
	final static byte DDF_CENTURY = 0x14;

	private static final DateTimeFormatter timeParser = DateTimeFormat
			.forPattern("yyyyMMddHHmmssSSS");

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

	private static final int TIME_STAMP_SIZE = 17;

	private final InputStream stream;
	/** current stage of state machine */
	private int stage;
	/** number of validated incoming bytes */
	private int count;
	/** counter of bytes in DDF fixed size time stamp suffix */
	private int ender;
	private StringBuilder timestamp;

	private final ByteBuffer buffer = ByteBuffer.allocate(1024);

	private void init() {
		this.stage = S0_INIT;
		this.count = 0;
	}

	private byte[] copy(final ByteBuffer buffer) {
		return copy(buffer, -1);
	}

	private byte[] copy(final ByteBuffer buffer, final int next) {

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

		buffer.flip();
		final byte[] message = new byte[buffer.remaining()];
		buffer.get(message);
		buffer.clear();
		if (next > -1) {
			buffer.put((byte) next);
		}

		return message;

	}

	public DDFLogDeframer(final InputStream is) {
		stream = is;
		init();
	}

	/**
	 * assume JERQ uses LF terminators and optional fixed size time stamp
	 */
	protected synchronized byte[] next() throws IOException {

		while (true) {

			/* read next byte; */
			final int b = stream.read();
			if (b == -1) {
				return null;
			}

			final byte alpha = (byte) b;
			count++;

			/* UDP is not \n delimited, so must check for message start */
			if (alpha == FeedDDF.DDF_START && count != 1
					&& stage != S3_DDF_TIMESTAMP) {
				return copy(buffer, alpha);
			}

			/* handle state transitions */
			switch (stage) {

				case S0_INIT:
					switch (alpha) {
						case FeedDDF.DDF_START:
							stage = S1_DDF_START;
							buffer.put(alpha);
							continue;
						case FeedDDF.DDF_TERMINATE:
							// OK to post empty frames
							buffer.put(alpha);
							return copy(buffer);
						default:
							stage = S4_PRINTABLE;
							buffer.put(alpha);
							continue;
					}

				case S1_DDF_START:
					switch (alpha) {
						case FeedDDF.DDF_FINISH:
							stage = S2_DDF_FINISH;
							buffer.put(alpha);
							continue;
						default:
							buffer.put(alpha);
							continue;
					}

				case S2_DDF_FINISH:
					switch (alpha) {
						case '2':
							// time stamp present
							stage = S3_DDF_TIMESTAMP;
							timestamp =
									new StringBuilder().append((char) alpha);
							ender = 1; // start count
							continue;
						case FeedDDF.DDF_TERMINATE:
							// no time stamp send frame
							buffer.put(alpha);
							return copy(buffer);
					}

				case S3_DDF_TIMESTAMP:
					ender++;
					/* continue stamp count, including terminator */
					if (ender == TIME_STAMP_SIZE + 1) {
						if (alpha == FeedDDF.DDF_TERMINATE) {
							encodeTimeStamp(timestamp.toString(), buffer);
							buffer.put(alpha);
							return copy(buffer);
						} else if (alpha == FeedDDF.DDF_START) {
							encodeTimeStamp(timestamp.toString(), buffer);
							return copy(buffer, alpha);
						} else {
							this.stage = S0_INIT;
							this.count = 0;
							return null;
						}
					} else {
						timestamp.append((char) alpha);
						continue;
					}

				case S4_PRINTABLE:
					switch (alpha) {
						case FeedDDF.DDF_TERMINATE:
							buffer.put(alpha);
							return copy(buffer);
						default:
							buffer.put(alpha);
							continue;
					}

				default:
					log.error("ddf frame decoder logic error");
					init();
					continue;
			}

		}

	}

	final static byte encodeTimeStampByte(final int timeField) {
		return (byte) (timeField | DDF_TIME_STAMP_MASK);
	}

	//
	// /** time zone information is discarded */
	static final void encodeTimeStamp(final String timestamp,
			final ByteBuffer buffer) {

		final ReadableDateTime dateTime = timeParser.parseDateTime(timestamp);

		// base fields
		buffer.put(DDF_CENTURY); // century
		buffer.put(encodeTimeStampByte(dateTime.getYearOfCentury())); // year
		buffer.put(encodeTimeStampByte(dateTime.getMonthOfYear())); // month
		buffer.put(encodeTimeStampByte(dateTime.getDayOfMonth())); // day
		buffer.put(encodeTimeStampByte(dateTime.getHourOfDay())); // hours
		buffer.put(encodeTimeStampByte(dateTime.getMinuteOfHour())); // minutes
		buffer.put(encodeTimeStampByte(dateTime.getSecondOfMinute())); // seconds

		// milliseconds
		final int millisOfSecond = dateTime.getMillisOfSecond();
		buffer.put((byte) (millisOfSecond & 0xFF)); // low byte
		buffer.put((byte) ((millisOfSecond >>> 8) & 0xFF)); // high byte

	}

}
