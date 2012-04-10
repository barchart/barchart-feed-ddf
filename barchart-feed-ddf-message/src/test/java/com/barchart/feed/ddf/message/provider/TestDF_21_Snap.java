/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.feed.ddf.message.provider.DDF_MessageService.PRICE_CLEAR;
import static com.barchart.feed.ddf.message.provider.DDF_MessageService.PRICE_EMPTY;
import static com.barchart.feed.ddf.message.provider.DDF_MessageService.SIZE_EMPTY;
import static com.barchart.feed.ddf.message.provider.DDF_MessageService.isClear;
import static com.barchart.feed.ddf.message.provider.DDF_MessageService.isEmpty;
import static com.barchart.util.ascii.ASCII.ASCII_CHARSET;
import static com.barchart.util.values.provider.ValueBuilder.newPrice;
import static com.barchart.util.values.provider.ValueBuilder.newSize;
import static com.barchart.util.values.provider.ValueBuilder.newTime;
import static com.barchart.util.values.provider.ValueConst.NULL_PRICE;
import static com.barchart.util.values.provider.ValueConst.ZERO_PRICE;
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
 * The Class TestDF_21_Snap.
 */
public class TestDF_21_Snap extends TestDDFBase {

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

	// 22 spread
	final static byte[] ba22sp = "2HI1,2AN15,SP2HI2,HI3,2445,2604,2404,2582,,,,2481,,,2582,,,12196949,3 "
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode spread.
	 */
	@Test
	public void testDecodeSpread() {

		final DF_21_Snap msg = (DF_21_Snap) DDF_MessageService
				.newInstance(new DF_21_Snap().getMessageType());

		final ByteBuffer buffer = ByteBuffer.wrap(ba22sp);

		msg.decodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.NYSE);
		assertEquals(msg.getFraction(), DDF_Fraction.N2);
		assertEquals(msg.getMessageType(), DDF_MessageType.SNAP_FORE_PLUS);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.D03);
		assertEquals(msg.getSession(), DDF_Session.$_SPACE);
		assertEquals(msg.getId(), "HI1_HI2_HI3");
		assertEquals(msg.getSpreadType(), DDF_SpreadType.DEFAULT);
		assertEquals(msg.getDelay(), 15);

	}

	// 21
	final static byte[] ba21 = "2SF0,12B10,,,,,-,-,,,,,,,,,Q JFTKDw9"
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode21.
	 */
	@Test
	public void testDecode21() {

		final DF_21_Snap msg = new DF_21_Snap();

		final ByteBuffer buffer = ByteBuffer.wrap(ba21);

		msg.decodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.CME_CBOT);
		assertEquals(msg.getFraction(), DDF_Fraction.Q8);
		assertEquals(msg.getMessageType(), DDF_MessageType.SNAP_FORE_EXCH);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.D27);
		assertEquals(msg.getSession(), DDF_Session.$_SPACE);
		assertEquals(msg.getId(), "SF0");
		assertEquals(msg.getSpreadType(), DDF_SpreadType.UNKNOWN);
		assertEquals(msg.getDelay(), 10);

		// long t1 = msg.getTime().asMillisUTC();
		// long t2 = System.currentTimeMillis();
		// assertTrue(t2 - t1 < 100);
		assertEquals(msg.getTime().asMillisUTC(), new DateTime(
				"2010-06-20T16:04:55.569Z").getMillis());

		assertTrue(msg.getPriceOpen() == PRICE_EMPTY);
		assertTrue(isEmpty(msg.getPriceHigh()));
		assertTrue(msg.getPriceLow() == PRICE_EMPTY);
		assertTrue(msg.getPriceLast() == PRICE_EMPTY);
		assertTrue(msg.getPriceBid() == PRICE_CLEAR); // XXX
		assertTrue(isClear(msg.getPriceAsk())); // XXX
		assertTrue(msg.getPriceOpen2() == PRICE_EMPTY);
		assertTrue(msg.getPriceLastPrevious() == PRICE_EMPTY);
		assertTrue(msg.getPriceClose() == PRICE_EMPTY);
		assertTrue(msg.getPriceClose2() == PRICE_EMPTY);
		assertTrue(isEmpty(msg.getPriceSettle()));

		assertTrue(msg.getSizeVolumePrevious() == SIZE_EMPTY);
		assertTrue(msg.getSizeInterest() == SIZE_EMPTY);
		assertTrue(msg.getSizeVolume() == SIZE_EMPTY);

	}

	// 22
	final static byte[] ba22 = "2HIG,2AN15,2445,2604,2404,2582,,,,2481,,,2582,,,12196949,3 JFTKDw9"
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode22.
	 */
	@Test
	public void testDecode22() {

		final DF_21_Snap msg = new DF_21_Snap();

		final ByteBuffer buffer = ByteBuffer.wrap(ba22);

		msg.decodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.NYSE);
		assertEquals(msg.getFraction(), DDF_Fraction.N2);
		assertEquals(msg.getMessageType(), DDF_MessageType.SNAP_FORE_PLUS);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.D03);
		assertEquals(msg.getSession(), DDF_Session.$_SPACE);
		assertEquals(msg.getId(), "HIG");
		assertEquals(msg.getDelay(), 15);

		// long t1 = msg.getTime().asMillisUTC();
		// long t2 = System.currentTimeMillis();
		// assertTrue(t2 - t1 < 100);
		assertEquals(msg.getTime().asMillisUTC(), new DateTime(
				"2010-06-20T15:04:55.569Z").getMillis());

		assertEquals(msg.getPriceOpen(), newPrice(2445, -2));
		assertEquals(msg.getPriceHigh(), newPrice(2604, -2));
		assertEquals(msg.getPriceLow(), newPrice(2404, -2));
		assertEquals(msg.getPriceLast(), newPrice(2582, -2));

		assertEquals(msg.getPriceBid(), newPrice(0, -2));
		assertEquals(msg.getPriceAsk(), newPrice(0, -2));
		assertEquals(msg.getPriceOpen2(), newPrice(0, -2));
		assertTrue(isEmpty(msg.getPriceBid()));
		assertTrue(isEmpty(msg.getPriceAsk()));
		assertTrue(isEmpty(msg.getPriceOpen2()));

		assertEquals(msg.getPriceLastPrevious(), newPrice(2481, -2));

		assertEquals(msg.getPriceClose(), newPrice(0, -2));
		assertEquals(msg.getPriceClose2(), newPrice(0, -2));
		assertTrue(isEmpty(msg.getPriceClose()));
		assertTrue(isEmpty(msg.getPriceClose2()));

		assertEquals(msg.getPriceSettle(), newPrice(2582, -2));

		assertEquals(msg.getSizeVolumePrevious(), newSize(0));
		assertEquals(msg.getSizeInterest(), newSize(0));
		assertTrue(isEmpty(msg.getSizeVolumePrevious()));
		assertTrue(isEmpty(msg.getSizeInterest()));

		assertEquals(msg.getSizeVolume(), newSize(12196949));

	}

	// 26
	final static byte[] ba26 = "2$DJUBSSO,6Ao10,5515,5642,5501,5631,,,,,,,,,,,S JFTKDw9"
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode26.
	 */
	@Test
	public void testDecode26() {

		final DF_21_Snap msg = new DF_21_Snap();

		final ByteBuffer buffer = ByteBuffer.wrap(ba26);

		msg.decodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.Index_DOW_Full);
		assertEquals(msg.getFraction(), DDF_Fraction.N2);
		assertEquals(msg.getMessageType(), DDF_MessageType.SNAP_FORE_PLUS_QUOTE);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.D29);
		assertEquals(msg.getSession(), DDF_Session.$_SPACE);
		assertEquals(msg.getId(), "$DJUBSSO");
		assertEquals(msg.getDelay(), 10);

		// long t1 = msg.getTime().asMillisUTC();
		// long t2 = System.currentTimeMillis();
		// assertTrue(t2 - t1 < 100);
		assertEquals(msg.getTime().asMillisUTC(), new DateTime(
				"2010-06-20T15:04:55.569Z").getMillis());

		assertEquals(msg.getPriceOpen(), newPrice(5515, -2));
		assertEquals(msg.getPriceHigh(), newPrice(5642, -2));
		assertEquals(msg.getPriceLow(), newPrice(5501, -2));
		assertEquals(msg.getPriceLast(), newPrice(5631, -2));

		assertEquals(msg.getPriceBid(), NULL_PRICE);
		assertEquals(msg.getPriceAsk(), ZERO_PRICE);
		assertEquals(msg.getPriceOpen2(), newPrice(0, -2));
		assertEquals(msg.getPriceLastPrevious(), newPrice(0, -2));
		assertEquals(msg.getPriceClose(), newPrice(0, -2));
		assertEquals(msg.getPriceClose2(), PRICE_CLEAR);
		assertEquals(msg.getPriceSettle(), PRICE_EMPTY);

		assertTrue(isEmpty(msg.getPriceBid()));
		assertTrue(isEmpty(msg.getPriceAsk()));
		assertTrue(isEmpty(msg.getPriceOpen2()));
		assertTrue(isEmpty(msg.getPriceLastPrevious()));
		assertTrue(isEmpty(msg.getPriceClose()));
		assertTrue(isEmpty(msg.getPriceClose2()));
		assertTrue(isEmpty(msg.getPriceSettle()));

		assertEquals(msg.getSizeVolumePrevious(), newSize(0));
		assertEquals(msg.getSizeInterest(), newSize(0));
		assertEquals(msg.getSizeVolume(), newSize(0));

		assertTrue(isEmpty(msg.getSizeVolumePrevious()));
		assertTrue(isEmpty(msg.getSizeInterest()));
		assertTrue(isEmpty(msg.getSizeVolume()));

	}

	/**
	 * Test encode.
	 */
	@Test
	public void testEncode() {
		testEncodeDecode(new DF_21_Snap(), ba21);
		testEncodeDecode(new DF_21_Snap(), ba22);
		testEncodeDecode(new DF_21_Snap(), ba26);
	}

	/**
	 * Test encode empty.
	 */
	@Test
	public void testEncodeEmpty() {

		final DF_21_Snap msg = new DF_21_Snap();

		final ByteBuffer buffer = ByteBuffer.allocate(27);

		msg.encodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.UNKNOWN);
		assertEquals(msg.getFraction(), DDF_Fraction.UNKNOWN);
		assertEquals(msg.getMessageType(), DDF_MessageType.SNAP_FORE_EXCH);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.UNKNOWN);
		assertEquals(msg.getSession(), DDF_Session.UNKNOWN);
		assertEquals(msg.getId(), "");
		assertEquals(msg.getDelay(), 0);
		assertEquals(msg.getTime(), newTime(0));

		assertTrue(isEmpty(msg.getPriceHigh()));
		assertTrue(isEmpty(msg.getPriceLow()));
		assertTrue(isEmpty(msg.getPriceBid()));
		assertTrue(isEmpty(msg.getPriceAsk()));
		assertTrue(isEmpty(msg.getPriceOpen()));
		assertTrue(isEmpty(msg.getPriceOpen2()));
		assertTrue(isEmpty(msg.getPriceClose()));
		assertTrue(isEmpty(msg.getPriceClose2()));
		assertTrue(isEmpty(msg.getPriceLast()));
		assertTrue(isEmpty(msg.getPriceLastPrevious()));
		assertTrue(isEmpty(msg.getPriceSettle()));

		assertTrue(isEmpty(msg.getSizeInterest()));
		assertTrue(isEmpty(msg.getSizeVolume()));
		assertTrue(isEmpty(msg.getSizeVolumePrevious()));

		// System.out.println("place=" + buffer.position());
		// System.out.println("limit=" + buffer.limit());
		// System.out.println("" + new String(buffer.array()));

		final String result = "2,1??00,,,,,,,,,,,,,,,??";

		final byte[] arraySource = buffer.array();
		final byte[] arrayTarget = result.getBytes(ASCII_CHARSET);
		assertTrue(Arrays.equals(arraySource, arrayTarget));

	}

	/**
	 * Test type.
	 */
	@Test
	public void testType() {

		final Base message = DDF_MessageService.newInstance(new DF_21_Snap()
				.getMessageType());

		assertTrue(message instanceof DF_21_Snap);

	}

}
