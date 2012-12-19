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

import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.message.enums.DDF_ParamType;
import com.barchart.feed.ddf.message.enums.DDF_Session;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_SpreadType;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.provider.ValueBuilder;

public class TestDF_25_Param extends TestDDFBase {

	//The 25 message i was trying to test is malformed, need a good one
	
//	/**
//	 * Sets the up.
//	 * 
//	 * @throws Exception
//	 *             the exception
//	 */
//	@Before
//	public void setUp() throws Exception {
//	}
//
//	/**
//	 * Tear down.
//	 * 
//	 * @throws Exception
//	 *             the exception
//	 */
//	@After
//	public void tearDown() throws Exception {
//	}
//
//	// 20
//	final static byte[] ba25 = "2SPH3,5AM10143970,00HRLLRLOA"
//			.getBytes(ASCII_CHARSET);
//
//	/**
//	 * Test decode.
//	 */
//	@Test
//	public void testDecode() {
//
//		final DF_25_Param msg = (DF_25_Param) DDF_MessageService
//				.newInstance(new DF_25_Param().getMessageType());
//
//		final ByteBuffer buffer = ByteBuffer.wrap(ba25);
//
//		msg.decodeDDF(buffer);
//
//		assertEquals(msg.getExchange(), DDF_Exchange.CME_CBOT);
//		assertEquals(msg.getFraction(), DDF_Fraction.Q8);
//		assertEquals(msg.getMessageType(), DDF_MessageType.DDF_25);
//		assertEquals(msg.getParamType(), DDF_ParamType.SETTLE_FINAL_PRICE);
//		assertEquals(msg.getTradeDay(), DDF_TradeDay.D27);
//		assertEquals(msg.getSession(), DDF_Session.$_SPACE);
//		assertEquals(msg.getId(), "SF0");
//		assertEquals(msg.getSpreadType(), DDF_SpreadType.UNKNOWN);
//		assertEquals(msg.getDelay(), 10);
//
//		assertEquals(msg.getTime().asMillisUTC(), new DateTime(
//				"2010-06-20T16:04:55.569Z").getMillis());
//
//		final PriceValue value = msg.getAsPrice();
//		final long mantissa = value.mantissa();
//		assertEquals(1053375, mantissa);
//		final int exponent = value.exponent();
//		assertEquals(-3, exponent);
//
//	}
//	
//	/**
//	 * Test encode.
//	 */
//	@Test
//	public void testEncode() {
//		testEncodeDecode(new DF_25_Param(), ba25);
//	}
//	
//	/**
//	 * Test encode empty.
//	 */
//	@Test
//	public void testEncodeEmpty() {
//
//		final DF_25_Param msg = (DF_25_Param) DDF_MessageService
//				.newInstance(new DF_25_Param().getMessageType());
//
//		final ByteBuffer buffer = ByteBuffer.allocate(15);
//
//		msg.encodeDDF(buffer);
//
//		assertEquals(msg.getExchange(), DDF_Exchange.UNKNOWN);
//		assertEquals(msg.getFraction(), DDF_Fraction.UNKNOWN);
//		assertEquals(msg.getMessageType(), DDF_MessageType.DDF_25);
//		assertEquals(msg.getParamType(), DDF_ParamType.UNKNOWN);
//		assertEquals(msg.getTradeDay(), DDF_TradeDay.UNKNOWN);
//		assertEquals(msg.getSession(), DDF_Session.UNKNOWN);
//		assertEquals(msg.getId(), "");
//		assertEquals(msg.getDelay(), 0);
//		assertEquals(msg.getTime().asMillisUTC(), 0);
//
//		// DDF_BLANK
//		final PriceValue value = msg.getAsPrice();
//
//		final PriceValue p1 = ValueBuilder.newPrice(0, 0);
//		final PriceValue p2 = ValueBuilder.newPrice(0, 0);
//
//		assertEquals(p1, p2);
//		assertTrue(p1.equals(p2));
//
//		assertTrue(DDF_MessageService.isEmpty(value));
//		assertEquals(value, ValueBuilder.newPrice(0, 0));
//
//		final String result = "2,0??00,????";
//
//		final byte[] arraySource = buffer.array();
//		final byte[] arrayTarget = result.getBytes(ASCII_CHARSET);
//
//		assertTrue(Arrays.equals(arraySource, arrayTarget));
//
//	}
//	
//	/**
//	 * Test type.
//	 */
//	@Test
//	public void testType() {
//
//		final Base message = DDF_MessageService.newInstance(new DF_25_Param()
//				.getMessageType());
//
//		assertTrue(message instanceof DF_25_Param);
//
//	}

}
