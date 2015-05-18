/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.datalink.provider.pipeline.MsgDeframerDDF;

public class TestMsgDeframerDDF {

	private static Logger log = LoggerFactory
			.getLogger(TestMsgDeframerDDF.class);

	static ChannelBuffer decode(final String text) throws Exception {

		final byte[] array = text.getBytes();

		final ChannelBuffer source = ChannelBuffers.copiedBuffer(array);

		final MsgDeframerDDF framer = new MsgDeframerDDF();

		final ChannelBuffer target = (ChannelBuffer) framer.decode(null, null,
				source);

		return target;

	}

	static List<String> parse(final String text) throws Exception {

		final byte[] array = text.getBytes();

		final ChannelBuffer buffer = ChannelBuffers.copiedBuffer(array);

		final MsgDeframerDDF framer = new MsgDeframerDDF();

		final List<String> list = new LinkedList<String>();

		while (true) {

			final ChannelBuffer frame = (ChannelBuffer) framer.decode(null,
					null, buffer);

			if (frame == null) {
				break;
			}

			/* parent frame decoder does this sometimes */
			// buffer.discardReadBytes();

			list.add(new String(frame.array()));

		}

		return list;
	}

	static void log(final ChannelBuffer buffer) {

		log.debug("target : {}", new String(buffer.array()));

	}

	/**
	 * Test0.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test0() throws Exception {

		assertNotNull(decode(" part 1   part 2 JFTKDw9\n next "));

		assertNotNull(decode(" part 1   part 2 23456789\n"));

		assertNotNull(decode(" part 1   part 2 \n"));

		assertNotNull(decode(" part 1   part 2 \n"));

		assertNotNull(decode(" ddfplus  buffer \n with no time stamp"));

		assertNotNull(decode(" time stamp \n and more"));

		assertNotNull(decode("broken  text  fragment  from ddf hell \n"));

	}

	/**
	 * Test1.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test1() throws Exception {

		final String msg0 = " part 1   part 2 JFtime\n";
		final String msg1 = " next \n";
		final String msg2 = "junk  text\n";
		final String msg3 = " ddfplus  buffer \n";

		final String text = msg0 + msg1 + msg2 + msg3;

		final List<String> list = parse(text);

		assertEquals(msg0, list.get(0));
		assertEquals(msg1, list.get(1));
		assertEquals(msg2, list.get(2));
		assertEquals(msg3, list.get(3));

	}

	/**
	 * Test2.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test2() throws Exception {

		assertNotNull(decode("\n"));
		assertNotNull(decode("\n\n"));
		assertNotNull(decode("\n\n\n"));

		assertNotNull(decode("\n1"));
		assertNotNull(decode("\n2\n"));
		assertNotNull(decode("\n3\n3\n"));

	}

}
