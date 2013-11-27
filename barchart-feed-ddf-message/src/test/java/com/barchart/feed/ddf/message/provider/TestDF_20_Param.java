/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.util.ascii.ASCII.ASCII_CHARSET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.base.values.provider.ValueBuilder;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.message.enums.DDF_ParamType;
import com.barchart.feed.ddf.message.enums.DDF_Session;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_SpreadType;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;

// TODO: Auto-generated Javadoc
/**
 * The Class TestDF_20_Param.
 */
public class TestDF_20_Param extends TestDDFBase {

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Tear down.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// 20 spread
	final static byte[] ba20sp = "2SF0,02B10SP1SG0,10533,D0Q JFTKDw9"
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode spread.
	 */
	@Test
	public void testDecodeSpread() {

		final DF_20_Param msg = (DF_20_Param) DDF_MessageService
				.newInstance(new DF_20_Param().getMessageType());

		final ByteBuffer buffer = ByteBuffer.wrap(ba20sp);

		msg.decodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.CME_CBOT);
		assertEquals(msg.getFraction(), DDF_Fraction.Q8);
		assertEquals(msg.getMessageType(), DDF_MessageType.PARAM);
		assertEquals(msg.getParamType(), DDF_ParamType.SETTLE_FINAL_PRICE);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.D27);
		assertEquals(msg.getSession(), DDF_Session.$_SPACE);
		assertEquals(msg.getId(), "SF0_SG0");
		assertEquals(msg.getSpreadType(), DDF_SpreadType.DEFAULT);
		assertEquals(msg.getDelay(), 10);

	}

	// 20
	final static byte[] ba20 = "2SF0,02B1010533,D0Q JFTKDw9"
			.getBytes(ASCII_CHARSET);
	
//	final static byte[] ba20 = "2LCQ1300D,0BM104900,??HRLLRMAf"
//			.getBytes(ASCII_CHARSET);
	/**
	 * Test decode.
	 */
	@Test
	public void testDecode() {

		final DF_20_Param msg = (DF_20_Param) DDF_MessageService
				.newInstance(new DF_20_Param().getMessageType());

		final ByteBuffer buffer = ByteBuffer.wrap(ba20);

		msg.decodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.CME_CBOT);
		assertEquals(msg.getFraction(), DDF_Fraction.Q8);
		assertEquals(msg.getMessageType(), DDF_MessageType.PARAM);
		assertEquals(msg.getParamType(), DDF_ParamType.SETTLE_FINAL_PRICE);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.D27);
		assertEquals(msg.getSession(), DDF_Session.$_SPACE);
		assertEquals(msg.getId(), "SF0");
		assertEquals(msg.getSpreadType(), DDF_SpreadType.UNKNOWN);
		assertEquals(msg.getDelay(), 10);

		// long t1 = msg.getTime().asMillisUTC();
		// long t2 = System.currentTimeMillis();
		assertEquals(msg.getTime().asMillisUTC(), new DateTime(
				"2010-06-20T16:04:55.569Z").getMillis());

		// 1053-3/8
		final PriceValue value = msg.getAsPrice();
		final long mantissa = value.mantissa();
		assertEquals(1053375, mantissa);
		final int exponent = value.exponent();
		assertEquals(-3, exponent);

	}

	// 20
	final static byte[] ba20mgexoptpit = "2MEZ900C,02G10100,D0Q JFTKDw9"
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode mge x_ options_ pit.
	 */
	@Test
	public void testDecodeMGEX_Options_Pit() {

		final DF_20_Param msg = (DF_20_Param) DDF_MessageService
				.newInstance(new DF_20_Param().getMessageType());

		final ByteBuffer buffer = ByteBuffer.wrap(ba20mgexoptpit);

		msg.decodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.CME_MGEX);
		assertEquals(msg.getFraction(), DDF_Fraction.Q8);
		assertEquals(msg.getMessageType(), DDF_MessageType.PARAM);
		assertEquals(msg.getParamType(), DDF_ParamType.SETTLE_FINAL_PRICE);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.D27);
		assertEquals(msg.getSession(), DDF_Session.$_SPACE);
		assertEquals(msg.getId(), "MEZ900C");
		assertEquals(msg.getSpreadType(), DDF_SpreadType.UNKNOWN);
		assertEquals(msg.getDelay(), 10);

		// long t1 = msg.getTime().asMillisUTC();
		// long t2 = System.currentTimeMillis();
		assertEquals(msg.getTime().asMillisUTC(), new DateTime(
				"2010-06-20T16:04:55.569Z").getMillis());

		// 1053-3/8
		final PriceValue value = msg.getAsPrice();
		final long mantissa = value.mantissa();
		assertEquals(10000, mantissa);
		final int exponent = value.exponent();
		assertEquals(-3, exponent);

		//
		final SizeValue size = msg.getAsSize();
		assertEquals(10000, size.asLong());

	}

	/**
	 * Test encode.
	 */
	@Test
	public void testEncode() {
		testEncodeDecode(new DF_20_Param(), ba20);
	}

	/**
	 * Test encode empty.
	 */
	@Test
	public void testEncodeEmpty() {

		final DF_20_Param msg = (DF_20_Param) DDF_MessageService
				.newInstance(new DF_20_Param().getMessageType());

		final ByteBuffer buffer = ByteBuffer.allocate(15);

		msg.encodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.UNKNOWN);
		assertEquals(msg.getFraction(), DDF_Fraction.UNKNOWN);
		assertEquals(msg.getMessageType(), DDF_MessageType.PARAM);
		assertEquals(msg.getParamType(), DDF_ParamType.UNKNOWN);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.UNKNOWN);
		assertEquals(msg.getSession(), DDF_Session.UNKNOWN);
		assertEquals(msg.getId(), "");
		assertEquals(msg.getDelay(), 0);
		assertEquals(msg.getTime().asMillisUTC(), HelperDDF.DDF_EMPTY);

		// DDF_BLANK
		final PriceValue value = msg.getAsPrice();

		final PriceValue p1 = ValueBuilder.newPrice(0, 0);
		final PriceValue p2 = ValueBuilder.newPrice(0, 0);

		assertEquals(p1, p2);
		assertTrue(p1.equals(p2));

		assertTrue(DDF_MessageService.isEmpty(value));
		assertEquals(value, ValueBuilder.newPrice(0, 0));

		// System.out.println("place=" + buffer.position());
		// System.out.println("limit=" + buffer.limit());
		// System.out.println("" + new String(buffer.array()));

		final String result = "2,0??00,????";

		final byte[] arraySource = buffer.array();
		final byte[] arrayTarget = result.getBytes(ASCII_CHARSET);

		assertTrue(Arrays.equals(arraySource, arrayTarget));

	}

	/**
	 * Test type.
	 */
	@Test
	public void testType() {

		final Base message = DDF_MessageService.newInstance(new DF_20_Param()
				.getMessageType());

		assertTrue(message instanceof DF_20_Param);

	}

	// ###

	// .#20111219103312..2SEQUENCE,0.8#0047569,00I�..KLSJaK�.

	final byte[] baX1 = "#20111219103312".getBytes();
	final byte[] baX2 = "2SEQUENCE,08#0047569,00I ".getBytes();

	/**
	 * Test decode1.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testDecode1() throws Exception {

		final byte[] array1 = baX1;
		final DDF_BaseMessage msg1 = DDF_MessageService.decode(array1);
		System.out.println("msg1=" + msg1);

		final byte[] array2 = baX2;
		final DDF_BaseMessage msg2 = DDF_MessageService.decode(array2);
		System.out.println("msg2=" + msg2);

	}

}
