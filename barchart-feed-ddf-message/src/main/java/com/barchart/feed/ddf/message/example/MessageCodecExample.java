/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.example;

import static com.barchart.util.ascii.ASCII.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.provider.DDF_MessageService;

/**
 *
 *
 * */

public class MessageCodecExample {

	private static final Logger log = LoggerFactory.getLogger(MessageCodecExample.class);

	/**
	 * sample DDF_MarketParameter message with NO time stamp
	 */
	final static byte[] ba20 = "3KCK2,BAC55,18390K1,18385L4,18380M3,18375N11,18370O4,18400J1,18410I1,18415H2,18420G3,18425F2"
			.getBytes(ASCII_CHARSET);

	/**
	 * sample DDF_MarketParameter message WITH time stamp
	 */
	final static byte[] ba20ts = "2SF0,02B1010121,D0Q JFTKDw9"
			.getBytes(ASCII_CHARSET);

	/**
	 * sample DDF_MarketParameter message with spread symbol and time stamp
	 */
	final static byte[] ba20sp = "2SF0,02B10SP1SG0,10533,D0R JFTKDw9"
			.getBytes(ASCII_CHARSET);

	final static byte[] ts0 = "#20110923142300".getBytes(ASCII_CHARSET);

	final static byte[] r0 = "CLockout IP 1.2.3.4".getBytes(ASCII_CHARSET);

	final static byte[] r1 = "+ Successful login".getBytes(ASCII_CHARSET);

	final static byte[] r2 = "- Login failure".getBytes(ASCII_CHARSET);

	final static byte[] s0 = (""
			+ //
			"%<QUOTE symbol=\"MSFT\" name=\"Microsoft Corp.\" exchange=\"NASDAQ\" basecode=\"A\" pointvalue=\"1.0\" tickincrement=\"1\" ddfexchange=\"Q\" flag=\"s\" lastupdate=\"20110924022417\" bid=\"2502\" bidsize=\"4\" ask=\"2504\" asksize=\"4\" mode=\"R\"><SESSION day=\"M\" session=\" \" timestamp=\"20110923172217\" open=\"2490\" high=\"2515\" low=\"2469\" last=\"2506\" previous=\"2506\" tradesize=\"53900\" volume=\"64729752\" tradetime=\"20110923161147\" id=\"combined\"/>"
			+ //
			"<SESSION last=\"2506\" id=\"previous\"/>" + //
			"</QUOTE>" + //
			"").getBytes(ASCII_CHARSET);

	static void decodeMessage(final byte[] array) {

		final DDF_BaseMessage message;
		try {
			message = DDF_MessageService.decode(array);
		} catch (Exception e) {
			log.error("decode failed", e);
			return;
		}

		log.info("message : {}", message.toString());

		log.info("message fields : {}", message.toStringFields());

	}

	public final static void main(String[] args) {

		decodeMessage(ba20);

		decodeMessage(ba20ts);

		decodeMessage(ba20sp);

		//

		decodeMessage(ts0);

		decodeMessage(r0);
		decodeMessage(r1);
		decodeMessage(r2);

		decodeMessage(s0);

	}

}
