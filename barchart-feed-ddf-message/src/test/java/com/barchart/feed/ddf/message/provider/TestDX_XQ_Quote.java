/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.util.ascii.ASCII.ASCII_CHARSET;
import static com.barchart.util.values.provider.ValueBuilder.newPrice;
import static com.barchart.util.values.provider.ValueBuilder.newSize;
import static com.barchart.util.values.provider.ValueBuilder.newText;
import static com.barchart.util.values.provider.ValueBuilder.newTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.barchart.feed.ddf.message.api.DDF_MarketSession;
import com.barchart.feed.ddf.message.enums.DDF_Indicator;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.message.enums.DDF_QuoteMode;
import com.barchart.feed.ddf.message.enums.DDF_QuoteState;
import com.barchart.feed.ddf.message.enums.DDF_Session;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_SpreadType;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;

// TODO: Auto-generated Javadoc
/**
 * The Class TestDX_XQ_Quote.
 */
public class TestDX_XQ_Quote extends TestDDFBase {

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

	// XQ spread
	final byte[] baXQ0sp = "%<QUOTE symbol=\"ESM0_ESZ0\" name=\"E-Mini S&amp;P 500\" exchange=\"GBLX\" basecode=\"A\" pointvalue=\"50.0\" tickincrement=\"25\" ddfexchange=\"M\" lastupdate=\"20100615144110\" bid=\"109975\" bidsize=\"162\" ask=\"110000\" asksize=\"248\" mode=\"R\"><SESSION day=\"E\" session=\"G\" timestamp=\"20100615094112\" open=\"109025\" high=\"110175\" low=\"108850\" last=\"109975\" previous=\"109050\" tradesize=\"2\" volume=\"362318\" tradetime=\"20100615094111\" id=\"combined\"/><SESSION day=\"D\" session=\"G\" timestamp=\"20100614205606\" open=\"109050\" high=\"110600\" low=\"108875\" last=\"109050\" previous=\"108925\" tradesize=\"1\" openinterest=\"1482925\" volume=\"887313\" tradetime=\"20100614151457\" id=\"previous\"/></QUOTE>"
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode spread xml.
	 */
	@Test
	public void testDecodeSpreadXML() {

		final DX_XQ_Quote msg = new DX_XQ_Quote();

		final ByteBuffer buffer = ByteBuffer.wrap(baXQ0sp);

		buffer.get(); // position + 1

		msg.decodeXML(buffer);

		assertEquals(msg.getId(), "ESM0_ESZ0");
		assertEquals(msg.getSpreadType(), DDF_SpreadType.DEFAULT);
		assertEquals(msg.getFraction(), DDF_Fraction.N2);
		assertEquals(msg.getExchange(), DDF_Exchange.CME_Main);
		assertEquals(msg.getMessageType(), DDF_MessageType.QUOTE_SNAP_XML);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.D15);
		assertEquals(msg.getSession(), DDF_Session.FUTURE_COMBO);
		assertEquals(msg.getDelay(), 0);

	}

	// XQ
	final byte[] baXQ0 = "%<QUOTE symbol=\"ESM0\" name=\"E-Mini S&amp;P 500\" exchange=\"GBLX\" basecode=\"A\" pointvalue=\"50.0\" tickincrement=\"25\" ddfexchange=\"M\" lastupdate=\"20100615144110\" bid=\"109975\" bidsize=\"162\" ask=\"110000\" asksize=\"248\" mode=\"R\"><SESSION day=\"E\" session=\"G\" timestamp=\"20100615094112\" open=\"109025\" high=\"110175\" low=\"108850\" last=\"109975\" previous=\"109050\" tradesize=\"2\" volume=\"362318\" tradetime=\"20100615094111\" id=\"combined\"/><SESSION day=\"D\" session=\"G\" timestamp=\"20100614205606\" open=\"109050\" high=\"110600\" low=\"108875\" last=\"109050\" previous=\"108925\" tradesize=\"1\" openinterest=\"1482925\" volume=\"887313\" tradetime=\"20100614151457\" id=\"previous\"/></QUOTE>"
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode xm l0.
	 */
	@Test
	public void testDecodeXML0() {

		final DX_XQ_Quote msg = new DX_XQ_Quote();

		final ByteBuffer buffer = ByteBuffer.wrap(baXQ0);

		buffer.get(); // position + 1

		msg.decodeXML(buffer);

		assertEquals(msg.getId(), "ESM0");
		assertEquals(msg.getSpreadType(), DDF_SpreadType.UNKNOWN);
		assertEquals(msg.getFraction(), DDF_Fraction.N2);
		assertEquals(msg.getExchange(), DDF_Exchange.CME_Main);
		assertEquals(msg.getMessageType(), DDF_MessageType.QUOTE_SNAP_XML);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.D15);
		assertEquals(msg.getSession(), DDF_Session.FUTURE_COMBO);
		assertEquals(msg.getDelay(), 0);

		assertEquals(msg.getTime(), newTime(new DateTime(
				"2010-06-15T19:41:10.000Z").getMillis()));

		assertEquals(msg.getMode(), DDF_QuoteMode.REALTIME);
		assertEquals(msg.getState(), DDF_QuoteState.UNKNOWN);

		assertEquals(msg.getSymbolName(), newText("E-Mini S&P 500"));

		assertEquals(msg.getPriceStep(), newPrice(25, -2));

		assertEquals(msg.getPriceBid(), newPrice(109975, -2));
		assertEquals(msg.getSizeBid(), newSize(162));

		assertEquals(msg.getPriceAsk(), newPrice(110000, -2));
		assertEquals(msg.getSizeAsk(), newSize(248));

		assertEquals(msg.getPointValue(), newPrice(50, 0));

		//

		final DDF_MarketSession[] sessions = msg.sessions();

		assertEquals(sessions.length, 2);

		DDF_MarketSession session;

		//

		session = sessions[0];
		assertEquals(session.getId(), "ESM0");
		assertEquals(session.getExchange(), DDF_Exchange.CME_Main);
		assertEquals(session.getSession(), DDF_Session.$_G_NET);
		assertEquals(session.getIndicator(), DDF_Indicator.CURRENT);
		assertEquals(session.getFraction(), DDF_Fraction.N2);
		assertEquals(session.getMessageType(), DDF_MessageType.SESSION_SNAP_XML);

		assertEquals(session.getPriceOpen(), newPrice(109025, -2));
		assertEquals(session.getPriceHigh(), newPrice(110175, -2));
		assertEquals(session.getPriceLow(), newPrice(108850, -2));

		assertEquals(session.getPriceLast(), newPrice(109975, -2));
		assertEquals(session.getSizeLast(), newSize(2));
		assertEquals(session.getTimeLast(), newTime(new DateTime(
				"2010-06-15T14:41:11.000Z").getMillis()));

		assertEquals(session.getPriceLastPrevious(), newPrice(109050, -2));

		assertEquals(session.getSizeVolume(), newSize(362318));
		assertEquals(session.getSizeInterest(), newSize(0));

		assertEquals(session.getTime(), newTime(new DateTime(
				"2010-06-15T14:41:12.000Z").getMillis()));

		//

		session = sessions[1];
		assertEquals(session.getId(), "ESM0");
		assertEquals(session.getExchange(), DDF_Exchange.CME_Main);
		assertEquals(session.getSession(), DDF_Session.$_G_NET);
		assertEquals(session.getIndicator(), DDF_Indicator.PREVIOUS);
		assertEquals(session.getFraction(), DDF_Fraction.N2);
		assertEquals(session.getMessageType(), DDF_MessageType.SESSION_SNAP_XML);

		assertEquals(session.getPriceOpen(), newPrice(109050, -2));
		assertEquals(session.getPriceHigh(), newPrice(110600, -2));
		assertEquals(session.getPriceLow(), newPrice(108875, -2));

		assertEquals(session.getPriceLast(), newPrice(109050, -2));
		assertEquals(session.getSizeLast(), newSize(1));
		assertEquals(session.getTimeLast(), newTime(new DateTime(
				"2010-06-14T20:14:57.000Z").getMillis()));

		assertEquals(session.getPriceLastPrevious(), newPrice(108925, -2));

		assertEquals(session.getSizeVolume(), newSize(887313));
		assertEquals(session.getSizeInterest(), newSize(1482925));

		assertEquals(session.getTime(), newTime(new DateTime(
				"2010-06-15T01:56:06.000Z").getMillis()));

	}

	final byte[] baXQ1 = "%<QUOTE ask=\"110000\" asksize=\"248\" basecode=\"A\" bid=\"109975\" bidsize=\"162\" ddfexchange=\"M\" exchange=\"GBLX\" lastupdate=\"20100615144110\" mode=\"R\" name=\"E-Mini S&amp;P 500\" pointvalue=\"50.0\" symbol=\"ESM0\" tickincrement=\"25\"><SESSION day=\"E\" high=\"110175\" id=\"combined\" last=\"109975\" low=\"108850\" open=\"109025\" previous=\"109050\" session=\"G\" timestamp=\"20100615094112\" tradesize=\"2\" tradetime=\"20100615094111\" volume=\"362318\"/><SESSION day=\"D\" high=\"110600\" id=\"previous\" last=\"109050\" low=\"108875\" open=\"109050\" openinterest=\"1482925\" previous=\"108925\" session=\"G\" timestamp=\"20100614205606\" tradesize=\"1\" tradetime=\"20100614151457\" volume=\"887313\"/></QUOTE>"
			.getBytes(ASCII_CHARSET);

	/**
	 * Test decode encode xml.
	 */
	@Test
	public void testDecodeEncodeXML() {

		final DX_XQ_Quote msg = new DX_XQ_Quote();

		final ByteBuffer source = ByteBuffer.wrap(baXQ1);

		source.get(); // position + 1

		msg.decodeXML(source);

		final ByteBuffer target = ByteBuffer.allocate(baXQ1.length);

		target.put((byte) '%'); // position + 1

		msg.encodeXML(target);

		// System.out.println(new String(target.array()));

		final byte[] arraySource = source.array();
		final byte[] arrayTarget = target.array();

		// System.out.println("source=" + new String(arraySource));
		// System.out.println("target=" + new String(arrayTarget));

		assertTrue(Arrays.equals(arraySource, arrayTarget));

	}

	/**
	 * Test encode empty.
	 */
	@Test
	public void testEncodeEmpty() {

		final DX_XQ_Quote msg = new DX_XQ_Quote();

		final ByteBuffer buffer = ByteBuffer.allocate(64);

		msg.encodeXML(buffer);

		// System.out.println("place=" + buffer.position());
		// System.out.println("limit=" + buffer.limit());
		// System.out.println("" + new String(buffer.array()));

		final String result = "<QUOTE basecode=\"?\" ddfexchange=\"?\" pointvalue=\"0.0\" symbol=\"\"/>";

		final byte[] arraySource = buffer.array();
		final byte[] arrayTarget = result.getBytes(ASCII_CHARSET);
		assertTrue(Arrays.equals(arraySource, arrayTarget));

	}

	static final byte[] baXQ2 = "%<QUOTE symbol=\"IBM\" name=\"International Business Machines Corp.\" exchange=\"NYSE\" basecode=\"A\" pointvalue=\"1.0\" tickincrement=\"1\" ddfexchange=\"N\" flag=\"s\" lastupdate=\"20110930060718\" bid=\"17804\" bidsize=\"1\" ask=\"17940\" asksize=\"1\" mode=\"R\"><SESSION day=\"S\" session=\" \" timestamp=\"20110929184558\" open=\"17969\" high=\"18091\" low=\"17553\" last=\"17917\" previous=\"17755\" tradesize=\"275469\" volume=\"6944293\" tradetime=\"20110929160109\" id=\"combined\"/><SESSION last=\"17755\" id=\"previous\"/></QUOTE>"
			.getBytes();

	// TODO
	/**
	 * Test decode xm l2.
	 */
	@Test
	public void testDecodeXML2() {

		final DX_XQ_Quote msg = new DX_XQ_Quote();

		final ByteBuffer buffer = ByteBuffer.wrap(baXQ0);

		buffer.get(); // position + 1

		msg.decodeXML(buffer);

		System.out.println("" + msg);

	}

}
