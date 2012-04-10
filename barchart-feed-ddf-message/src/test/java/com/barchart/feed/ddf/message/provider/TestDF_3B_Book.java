/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.feed.base.api.market.enums.MarketBookAction.MODIFY;
import static com.barchart.feed.base.api.market.enums.MarketBookSide.ASK;
import static com.barchart.feed.base.api.market.enums.MarketBookSide.BID;
import static com.barchart.feed.base.api.market.enums.MarketBookType.DEFAULT;
import static com.barchart.util.ascii.ASCII.ASCII_CHARSET;
import static com.barchart.util.values.provider.ValueBuilder.newTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.barchart.feed.base.api.market.enums.MarketBookSide;
import com.barchart.feed.base.api.market.values.MarketBookEntry;
import com.barchart.feed.base.provider.market.provider.DefBookEntry;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.message.enums.DDF_Session;
import com.barchart.feed.ddf.message.enums.DDF_TradeDay;
import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_SpreadType;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.util.values.provider.ValueBuilder;

public class TestDF_3B_Book extends TestDDFBase {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	// 3B
	final byte[] ba3B = "3XIZ9,BBX55,63795K25,63790L5,63780M1000,63775N35,63765O5,63800J20,63815I5,63820H10,63825G5,63830F7"
			.getBytes(ASCII_CHARSET);

	final byte[] ba3B_1 = "3ESZ1,BAMAA,118925K114,118900L1242,118875M952,118850N1216,118825O1021,118800P1058,118775Q1135,118750R1031,118725S866,118700T1345,118950J333,118975I853,119000H2551,119025G1221,119050F1323,119075E1401,119100D1656,119125C1150,119150B1192,119175A1050"
			.getBytes(ASCII_CHARSET);

	@Test
	public void testDecode_1() {

		final DDF_TradeDay todayUTC = DDF_TradeDay.fromMillisUTC(System
				.currentTimeMillis());

		final DF_3B_Book msg = new DF_3B_Book();

		final ByteBuffer buffer = ByteBuffer.wrap(ba3B_1);

		msg.decodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.CME_Main);
		assertEquals(msg.getFraction(), DDF_Fraction.N2);
		assertEquals(msg.getMessageType(), DDF_MessageType.BOOK_SNAP);
		assertEquals(msg.getTradeDay(), todayUTC);
		assertEquals(msg.getSession(), DDF_Session.FUTURE_COMBO);
		assertEquals(msg.getId(), "ESZ1");
		assertEquals(msg.getSpreadType(), DDF_SpreadType.UNKNOWN);
		assertEquals(msg.getDelay(), 0);

		final long t1 = msg.getTime().asMillisUTC();
		final long t2 = System.currentTimeMillis();
		assertTrue(t2 - t1 < 100);

	}

	@Test
	public void testDecode() {

		final DDF_TradeDay todayUTC = DDF_TradeDay.fromMillisUTC(System
				.currentTimeMillis());

		final DF_3B_Book msg = new DF_3B_Book();

		final ByteBuffer buffer = ByteBuffer.wrap(ba3B);

		msg.decodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.Fix_Me_X);
		assertEquals(msg.getFraction(), DDF_Fraction.N3);
		assertEquals(msg.getMessageType(), DDF_MessageType.BOOK_SNAP);
		assertEquals(msg.getTradeDay(), todayUTC);
		assertEquals(msg.getSession(), DDF_Session.FUTURE_COMBO);
		assertEquals(msg.getId(), "XIZ9");
		assertEquals(msg.getSpreadType(), DDF_SpreadType.UNKNOWN);
		assertEquals(msg.getDelay(), 0);

		final long t1 = msg.getTime().asMillisUTC();
		final long t2 = System.currentTimeMillis();
		assertTrue(t2 - t1 < 100);

	}

	@Test
	public void testEncode() {
		testEncodeDecode(new DF_3B_Book(), ba3B);
	}

	@Test
	public void testEntries() {

		final DF_3B_Book msg = new DF_3B_Book();

		final ByteBuffer buffer = ByteBuffer.wrap(ba3B);

		MarketBookEntry entry;

		msg.decodeDDF(buffer);

		final MarketBookEntry[] entries = msg.entries();

		assertEquals(10, entries.length);

		//

		entry = newEntry(BID, 1, 63795, -3, 25);
		assertEquals(entry, entries[0]);

		entry = newEntry(BID, 2, 63790, -3, 5);
		assertEquals(entry, entries[1]);

		entry = newEntry(BID, 3, 63780, -3, 1000);
		assertEquals(entry, entries[2]);

		entry = newEntry(BID, 4, 63775, -3, 35);
		assertEquals(entry, entries[3]);

		entry = newEntry(BID, 5, 63765, -3, 5);
		assertEquals(entry, entries[4]);

		entry = newEntry(ASK, 1, 63800, -3, 20);
		assertEquals(entry, entries[5]);

		entry = newEntry(ASK, 2, 63815, -3, 5);
		assertEquals(entry, entries[6]);

		entry = newEntry(ASK, 3, 63820, -3, 10);
		assertEquals(entry, entries[7]);

		entry = newEntry(ASK, 4, 63825, -3, 5);
		assertEquals(entry, entries[8]);

		entry = newEntry(ASK, 5, 63830, -3, 7);
		assertEquals(entry, entries[9]);

	}

	static MarketBookEntry newEntry(final MarketBookSide side, final int place,
			final long mant, final int exp, final int size) {
		return new DefBookEntry(MODIFY, side, DEFAULT, place,
				ValueBuilder.newPrice(mant, exp), ValueBuilder.newSize(size));
	}

	@Test
	public void testEncodeEmpty() {

		final DF_3B_Book msg = new DF_3B_Book();

		final ByteBuffer buffer = ByteBuffer.allocate(10);

		msg.encodeDDF(buffer);

		assertEquals(msg.getExchange(), DDF_Exchange.UNKNOWN);
		assertEquals(msg.getFraction(), DDF_Fraction.UNKNOWN);
		assertEquals(msg.getMessageType(), DDF_MessageType.BOOK_SNAP);
		assertEquals(msg.getTradeDay(), DDF_TradeDay.UNKNOWN);
		assertEquals(msg.getSession(), DDF_Session.UNKNOWN);
		assertEquals(msg.getId(), "");
		assertEquals(msg.getDelay(), 0);
		assertEquals(msg.getTime(), newTime(0));

		// System.out.println("place=" + buffer.position());
		// System.out.println("limit=" + buffer.limit());
		// System.out.println("" + new String(buffer.array()));

		final String result = "3,B??00";

		final byte[] arraySource = buffer.array();
		final byte[] arrayTarget = result.getBytes(ASCII_CHARSET);
		assertTrue(Arrays.equals(arraySource, arrayTarget));

	}

	@Test
	public void testType() {

		final Base message = DDF_MessageService.newInstance(new DF_3B_Book()
				.getMessageType());

		assertTrue(message instanceof DF_3B_Book);

	}

}
