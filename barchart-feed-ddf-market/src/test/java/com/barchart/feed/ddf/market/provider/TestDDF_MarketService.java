/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.market.provider;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.framework.data.InstrumentEntity;
import com.barchart.feed.base.book.api.MarketBook;
import com.barchart.feed.base.market.api.MarketTaker;
import com.barchart.feed.base.market.enums.MarketEvent;
import com.barchart.feed.base.market.enums.MarketField;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.feed.ddf.market.api.DDF_MarketProvider;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.provider.DDF_MessageService;

// TODO: Auto-generated Javadoc
/**
 * The Class TestDDF_MarketService.
 */
public class TestDDF_MarketService {

	private static Logger log = LoggerFactory
			.getLogger(TestDDF_MarketService.class);

	static final byte[] err1 = "%<BOOK askcount=\"5\" askprices=\"66370,66380,66400,66420,66430\" asksizes=\"2,1,1,1,2\" basecode=\"A\" bidcount=\"5\" bidprices=\"66290,66270,66240,66230,66220\" bidsizes=\"1,1,1,1,2\" symbol=\"RJZ1\"/>"
			.getBytes();

	static final byte[] err2 = "3RJZ1,BAC55,66290K1,66270L1,66250M2,66240N1,66230O1,66370J2,66380I1,66400H1,66420G1,66430F2"
			.getBytes();

	/**
	 * Test make dd f_ market base market do.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testMakeDDF_MarketBaseMarketDo() throws Exception {

		final InstrumentEntity instrument = DDF_InstrumentProvider.find("RJZ1");

		log.debug("instrument : {}", instrument);

		final DDF_MarketProvider maker = DDF_MarketService.newInstance();

		final MarketTaker<MarketBook> taker = new MarketTaker<MarketBook>() {
			@Override
			public MarketField<MarketBook> bindField() {
				return MarketField.BOOK;
			}

			@Override
			public MarketEvent[] bindEvents() {
				return MarketEvent.values();
			}

			@Override
			public InstrumentEntity[] bindInstruments() {
				return new InstrumentEntity[] { instrument };
			}

			@Override
			public void onMarketEvent(final MarketEvent event,
					final InstrumentEntity instrument, final MarketBook book) {

				log.debug("event : {}", event);
				log.debug("book : \n{}", book);

			}

		};

		maker.register(taker);

		final DDF_MarketBase message = (DDF_MarketBase) DDF_MessageService
				.decode(err1);

		log.debug("message : {}", message);

		maker.make(message);

		assertTrue(true);

	}

}
