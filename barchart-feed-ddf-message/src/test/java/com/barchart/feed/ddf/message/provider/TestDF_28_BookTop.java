/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.feed.base.values.provider.ValueBuilder.newPrice;
import static com.barchart.feed.base.values.provider.ValueBuilder.newSize;
import static com.barchart.feed.base.values.provider.ValueBuilder.newTime;
import static com.barchart.feed.ddf.message.provider.DDF_MessageService.isEmpty;
import static com.barchart.util.ascii.ASCII.ASCII_CHARSET;
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
import com.barchart.feed.ddf.symbol.enums.DDF_ExchangeKind;
import com.barchart.feed.ddf.symbol.enums.DDF_SpreadType;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.util.ascii.ASCII;

// TODO: Auto-generated Javadoc
/**
 * The Class TestDF_28_BookTop.
 */
public class TestDF_28_BookTop extends TestDDFBase {

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

	// 28 spread
	final byte[] ba28sp = "2HOZ9,8CJ10SP1HOM0,20911,5,20919,1,SGJFTKDw9"
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode spread.
	 */
	@Test
	public void testDecodeSpread() {

		final DF_28_BookTop msg = (DF_28_BookTop) DDF_MessageService
				.newInstance(new DF_28_BookTop().getMessageType());

		final ByteBuffer buffer = ByteBuffer.wrap(ba28sp);

		msg.decodeDDF(buffer);

		assertEquals(DDF_ExchangeKind.FUTURE, DDF_Exchange.CME_NYMEX.kind);

		assertEquals(msg.getSession().code, ASCII._G_);

		assertEquals(msg.getExchange(), DDF_Exchange.CME_NYMEX);
		assertEquals(msg.getFraction(), DDF_Fraction.N4);
		assertEquals(msg.getMessageType(), DDF_MessageType.BOOK_TOP);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.D29);
		assertEquals(msg.getSession(), DDF_Session.$_G_NET);
		assertEquals(msg.getId(), "HOZ9_HOM0");
		assertEquals(msg.getSpreadType(), DDF_SpreadType.DEFAULT);
		assertEquals(msg.getDelay(), 10);

	}

	// 28
	final byte[] ba28 = "2HOZ9,8CJ1020911,5,20919,1,SGJFTKDw9"
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode.
	 */
	@Test
	public void testDecode() {

		final DF_28_BookTop msg = new DF_28_BookTop();

		final ByteBuffer buffer = ByteBuffer.wrap(ba28);

		msg.decodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.CME_NYMEX);
		assertEquals(msg.getFraction(), DDF_Fraction.N4);
		assertEquals(msg.getMessageType(), DDF_MessageType.BOOK_TOP);
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

		assertEquals(msg.getPriceAsk(), newPrice(20919, -4));
		assertEquals(msg.getSizeAsk(), newSize(1));

		assertEquals(msg.getPriceBid(), newPrice(20911, -4));
		assertEquals(msg.getSizeBid(), newSize(5));

	}

	/**
	 * Test encode.
	 */
	@Test
	public void testEncode() {
		testEncodeDecode(new DF_28_BookTop(), ba28);
	}

	/**
	 * Test encode empty.
	 */
	@Test
	public void testEncodeEmpty() {

		final DF_28_BookTop msg = new DF_28_BookTop();

		final ByteBuffer buffer = ByteBuffer.allocate(16);

		msg.encodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.UNKNOWN);
		assertEquals(msg.getFraction(), DDF_Fraction.UNKNOWN);
		assertEquals(msg.getMessageType(), DDF_MessageType.BOOK_TOP);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.UNKNOWN);
		assertEquals(msg.getSession(), DDF_Session.UNKNOWN);
		assertEquals(msg.getId(), "");
		assertEquals(msg.getDelay(), 0);
		assertEquals(msg.getTime(), newTime(HelperDDF.DDF_EMPTY));

		assertTrue(isEmpty(msg.getPriceAsk()));
		assertTrue(isEmpty(msg.getSizeAsk()));
		assertTrue(isEmpty(msg.getPriceBid()));
		assertTrue(isEmpty(msg.getSizeBid()));

		// System.out.println("place=" + buffer.position());
		// System.out.println("limit=" + buffer.limit());
		// System.out.println("" + new String(buffer.array()));

		final String result = "2,8??00,,,,??";

		final byte[] arraySource = buffer.array();
		final byte[] arrayTarget = result.getBytes(ASCII_CHARSET);

		assertTrue(Arrays.equals(arraySource, arrayTarget));

	}

	/**
	 * Test type.
	 */
	@Test
	public void testType() {

		final Base message = DDF_MessageService.newInstance(new DF_28_BookTop()
				.getMessageType());

		assertTrue(message instanceof DF_28_BookTop);

	}

}
