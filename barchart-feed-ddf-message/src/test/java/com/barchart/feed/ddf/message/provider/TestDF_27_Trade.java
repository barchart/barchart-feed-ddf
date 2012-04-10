/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.feed.ddf.message.provider.DDF_MessageService.isEmpty;
import static com.barchart.util.ascii.ASCII.ASCII_CHARSET;
import static com.barchart.util.values.provider.ValueBuilder.newPrice;
import static com.barchart.util.values.provider.ValueBuilder.newSize;
import static com.barchart.util.values.provider.ValueBuilder.newTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.message.enums.DDF_Session;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_SpreadType;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;

// TODO: Auto-generated Javadoc
/**
 * The Class TestDF_27_Trade.
 */
public class TestDF_27_Trade extends TestDDFBase {

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// 27 spread
	final static byte[] ba27sp = "2HOZ9,7CJ10SP2HOZ1,HOZ2,21371,5,SGJFTKDw9"
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode spread.
	 */
	@Test
	public void testDecodeSpread() {

		final DF_27_Trade msg = new DF_27_Trade();

		final ByteBuffer buffer = ByteBuffer.wrap(ba27sp);

		msg.decodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.CME_NYMEX);
		assertEquals(msg.getFraction(), DDF_Fraction.N4);
		assertEquals(msg.getMessageType(), DDF_MessageType.TRADE);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.D29);
		assertEquals(msg.getSession(), DDF_Session.$_G_NET);
		assertEquals(msg.getId(), "HOZ9_HOZ1_HOZ2");
		assertEquals(msg.getSpreadType(), DDF_SpreadType.DEFAULT);
		assertEquals(msg.getDelay(), 10);

	}

	// 27
	final static byte[] ba27 = "2HOZ9,7CJ1021371,5,SGJFTKDw9"
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode.
	 */
	@Test
	public void testDecode() {

		final DF_27_Trade msg = new DF_27_Trade();

		final ByteBuffer buffer = ByteBuffer.wrap(ba27);

		msg.decodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.CME_NYMEX);
		assertEquals(msg.getFraction(), DDF_Fraction.N4);
		assertEquals(msg.getMessageType(), DDF_MessageType.TRADE);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.D29);
		assertEquals(msg.getSession(), DDF_Session.$_G_NET);
		assertEquals(msg.getId(), "HOZ9");
		assertEquals(msg.getSpreadType(), DDF_SpreadType.UNKNOWN);
		assertEquals(msg.getDelay(), 10);

		// long t1 = msg.getTime().asMillisUTC();
		// long t2 = System.currentTimeMillis();
		// assertTrue(t2 - t1 < 100);
		assertEquals(msg.getTime().asMillisUTC(), new DateTime(
				"2010-06-20T16:04:55.569Z").getMillis());
		// 2.1371
		assertEquals(msg.getPrice(), newPrice(21371, -4));
		assertEquals(msg.getSize(), newSize(5));

	}

	/**
	 * Test encode.
	 */
	@Test
	public void testEncode() {
		testEncodeDecode(new DF_27_Trade(), ba27);
	}

	/**
	 * Test encode empty.
	 */
	@Test
	public void testEncodeEmpty() {

		final DF_27_Trade msg = new DF_27_Trade();

		final ByteBuffer buffer = ByteBuffer.allocate(14);

		msg.encodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.UNKNOWN);
		assertEquals(msg.getFraction(), DDF_Fraction.UNKNOWN);
		assertEquals(msg.getMessageType(), DDF_MessageType.TRADE);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.UNKNOWN);
		assertEquals(msg.getSession(), DDF_Session.UNKNOWN);
		assertEquals(msg.getId(), "");
		assertEquals(msg.getDelay(), 0);
		assertEquals(msg.getTime(), newTime(0));

		assertTrue(isEmpty(msg.getPrice()));
		assertTrue(isEmpty(msg.getSize()));

		// System.out.println("place=" + buffer.position());
		// System.out.println("limit=" + buffer.limit());
		// System.out.println("" + new String(buffer.array()));

		final String result = "2,7??00,,??";

		final byte[] arraySource = buffer.array();
		final byte[] arrayTarget = result.getBytes(ASCII_CHARSET);
		assertTrue(Arrays.equals(arraySource, arrayTarget));

	}

	final static byte[] ba2Z = "2ORCL,ZCQ15300376,200,QWKI[OrG"
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode2 z.
	 */
	@Test
	public void testDecode2Z() {

		final DF_27_Trade msg = (DF_27_Trade) DDF_MessageService
				.newInstance(new DF_27_Trade().getMessageType());

		final ByteBuffer buffer = ByteBuffer.wrap(ba2Z);

		msg.decodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.NASDAQ);
		assertEquals(msg.getFraction(), DDF_Fraction.N4);
		assertEquals(msg.getMessageType(), DDF_MessageType.TRADE_VOL);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.D27);
		assertEquals(msg.getSession(), DDF_Session.$_W_);
		assertEquals(msg.getId(), "ORCL");
		assertEquals(msg.getSpreadType(), DDF_SpreadType.UNKNOWN);
		assertEquals(msg.getDelay(), 15);

		// long t1 = msg.getTime().asMillisUTC();
		// long t2 = System.currentTimeMillis();
		// assertTrue(t2 - t1 < 100);
		assertEquals(msg.getTime().asMillisUTC(),
				new DateTime(1317153007283L).getMillis());
		// 2.1371
		assertEquals(msg.getPrice(), newPrice(300376, -4));
		assertEquals(msg.getSize(), newSize(200));

	}

	/**
	 * Test type0.
	 */
	@Test
	public void testType0() {

		final Base message = DDF_MessageService.newInstance(new DF_27_Trade()
				.getMessageType());

		assertTrue(message instanceof DF_27_Trade);

	}

	/**
	 * Test type1.
	 */
	@Test
	public void testType1() {

		final Base message = DDF_MessageService
				.newInstance(DDF_MessageType.TRADE_VOL);

		assertTrue(message instanceof DF_27_Trade);

	}

}
