/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.feed.ddf.message.provider.CodecHelper.find;
import static com.barchart.feed.ddf.message.provider.CodecHelper.isXmlBook;
import static com.barchart.feed.ddf.message.provider.CodecHelper.isXmlCuvol;
import static com.barchart.feed.ddf.message.provider.CodecHelper.isXmlQuote;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_MarketBook;
import com.barchart.feed.ddf.message.api.DDF_MarketCuvol;
import com.barchart.feed.ddf.message.api.DDF_MarketQuote;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.util.FeedDDF;
import com.barchart.feed.ddf.util.HelperXML;
import com.barchart.feed.ddf.util.provider.DDF_ClearVal;
import com.barchart.feed.ddf.util.provider.DDF_NulVal;
import com.barchart.util.values.api.DecimalValue;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;
import com.barchart.util.values.api.TimeValue;

// TODO: Auto-generated Javadoc
/**
 * TODO provide alternative DDF_InstrumentService registration.
 */
public final class DDF_MessageService {

	private static final Logger log = LoggerFactory
			.getLogger(DDF_MessageService.class);

	private DDF_MessageService() {
	}

	/* ############################################ */

	/* corresponds to DDF_EMPTY "comma-nothing-comma" values */

	/**
	 * Checks if is blank.
	 * 
	 * @param value
	 *            the value
	 * @return true, if is blank
	 */
	public static final boolean isBlank(final DecimalValue value) {
		return value == DDF_NulVal.DECIMAL_EMPTY;
	}

	/**
	 * Checks if is empty.
	 * 
	 * @param value
	 *            the value
	 * @return true, if is empty
	 */
	public static final boolean isEmpty(final PriceValue value) {
		return value == DDF_NulVal.PRICE_EMPTY;
	}

	/**
	 * Checks if is empty.
	 * 
	 * @param value
	 *            the value
	 * @return true, if is empty
	 */
	public static final boolean isEmpty(final SizeValue value) {
		return value == DDF_NulVal.SIZE_EMPTY;
	}

	/**
	 * Checks if is empty.
	 * 
	 * @param value
	 *            the value
	 * @return true, if is empty
	 */
	public static final boolean isEmpty(final TimeValue value) {
		return value == DDF_NulVal.TIME_EMPTY;
	}

	/* ############################################ */

	/**
	 * Checks if is clear.
	 * 
	 * @param value
	 *            the value
	 * @return true, if is clear
	 */
	public static final boolean isClear(final DecimalValue value) {
		return value == DDF_ClearVal.DECIMAL_CLEAR;
	}

	/**
	 * Checks if is clear.
	 * 
	 * @param value
	 *            the value
	 * @return true, if is clear
	 */
	public static final boolean isClear(final PriceValue value) {
		return value == DDF_ClearVal.PRICE_CLEAR;
	}

	/**
	 * Checks if is clear.
	 * 
	 * @param value
	 *            the value
	 * @return true, if is clear
	 */
	public static final boolean isClear(final SizeValue value) {
		return value == DDF_ClearVal.SIZE_CLEAR;
	}

	/**
	 * Checks if is clear.
	 * 
	 * @param value
	 *            the value
	 * @return true, if is clear
	 */
	public static final boolean isClear(final TimeValue value) {
		return value == DDF_ClearVal.TIME_CLEAR;
	}

	/* ############################################ */

	static final Base newInstance(final DDF_MessageType type)
			throws RuntimeException {

		switch (type) {

		case BOOK_TOP:
			assert type.klaz.isAssignableFrom(DF_28_BookTop.class);
			return new DF_28_BookTop(type);

		case BOOK_SNAP:
			assert type.klaz.isAssignableFrom(DF_3B_Book.class);
			return new DF_3B_Book(type);

		case TRADE:
		case TRADE_VOL:
			assert type.klaz.isAssignableFrom(DF_27_Trade.class);
			return new DF_27_Trade(type);

		case PARAM:
			assert type.klaz.isAssignableFrom(DF_20_Param.class);
			return new DF_20_Param(type);

		case TIME_STAMP:
			assert type.klaz.isAssignableFrom(DF_C0_Time.class);
			return new DF_C0_Time(type);

		case SNAP_FORE_EXCH:
		case SNAP_FORE_PLUS:
		case SNAP_BACK_PLUS_CURR:
		case SNAP_BACK_PLUS_PREV:
		case SNAP_FORE_PLUS_QUOTE:
			assert type.klaz.isAssignableFrom(DF_21_Snap.class);
			return new DF_21_Snap(type);

		case QUOTE_SNAP_XML:
			assert type.klaz.isAssignableFrom(DX_XQ_Quote.class);
			return new DX_XQ_Quote(type);

		case CUVOL_SNAP_XML:
			assert type.klaz.isAssignableFrom(DX_XC_Cuvol.class);
			return new DX_XC_Cuvol(type);

		case BOOK_SNAP_XML:
			assert type.klaz.isAssignableFrom(DX_XB_Book.class);
			return new DX_XB_Book(type);

		case TCP_ACCEPT:
		case TCP_REJECT:
		case TCP_COMMAND:
		case TCP_WELCOME:
			assert type.klaz.isAssignableFrom(DF_C1_Response.class);
			return new DF_C1_Response(type);

		default:
			throw new IllegalArgumentException("unknown type=" + type);

		}

	}

	/**
	 * Decode.
	 * 
	 * @param buffer
	 *            the buffer
	 * @return the dD f_ base message
	 * @throws Exception
	 *             the exception
	 */
	public static final DDF_BaseMessage decode(final ByteBuffer buffer)
			throws Exception {
		throw new UnsupportedOperationException("TODO");
	}

	/**
	 * Decode.
	 * 
	 * @param array
	 *            the array
	 * @return the dD f_ base message
	 * @throws Exception
	 *             the exception
	 */
	public static final DDF_BaseMessage decode(final byte[] array)
			throws Exception {

		// first byte : classifier
		final byte kind = array[0];

		// for ddf feed & xml message typing
		final byte record;
		final byte subRecord;

		// for xml feed
		final Element element;

		switch (kind) {
		case FeedDDF.DDF_START:
			element = null;
			record = array[1];
			subRecord = find(array, 2, (byte) FeedDDF.DDF_MIDDLE);
			break;
		case FeedDDF.XML_SNAPSHOT:
			element = HelperXML.xmlDodumentDecode(array, 1, array.length, true);
			record = FeedDDF.XML_RECORD;
			if (isXmlBook(element)) {
				subRecord = FeedDDF.XML_SUB_BOOK;
				break;
			}
			if (isXmlCuvol(element)) {
				subRecord = FeedDDF.XML_SUB_CUVOL;
				break;
			}
			if (isXmlQuote(element)) {
				subRecord = FeedDDF.XML_SUB_QUOTE;
				break;
			}
			throw new IllegalArgumentException("unknown xml kind : \n"
					+ new String(array));
		case FeedDDF.TCP_ACCEPT:
		case FeedDDF.TCP_REJECT:
		case FeedDDF.TCP_COMMAND:
		case FeedDDF.TCP_WELCOME:
			element = null;
			record = array[0]; // kind
			subRecord = FeedDDF.NUL;
			break;
		default:
			element = null;
			record = DDF_MessageType.UNKNOWN.record;
			subRecord = DDF_MessageType.UNKNOWN.subRecord;
			log.debug("unknown kind : {}", kind);
			break;
		}

		// System.out.println("array=" + new String(array));
		// System.out.println("record=" + (char) record);
		// System.out.println("subRecord=" + (char) subRecord);

		final DDF_MessageType type =
				DDF_MessageType.fromPair(record, subRecord);

		final Base message = newInstance(type);

		final ByteBuffer buffer = ByteBuffer.wrap(array);

		switch (record) {
		default:
			// ddf line message
			message.decodeDDF(buffer);
			break;
		case FeedDDF.XML_RECORD:
			// xml formatted message
			message.decodeXML(element);
			break;
		}

		return message;

	}

	/**
	 * Market book from url.
	 * 
	 * @param symbolURI
	 *            the symbol uri
	 * @return the dD f_ market book
	 * @throws Exception
	 *             the exception
	 */
	public static final DDF_MarketBook marketBookFromUrl(final String symbolURI)
			throws Exception {

		final Element root = HelperXML.xmlDocumentDecode(symbolURI);

		// System.out.println(new String(xmlEncode(root, false)));

		final Element tag =
				HelperXML.xmlFirstChild(root, XmlTagBook.TAG,
						HelperXML.XML_STOP);

		final DX_XB_Book message = new DX_XB_Book();

		message.decodeXML(tag);

		return message;

	}

	/**
	 * Market cuvol from url.
	 * 
	 * @param symbolURI
	 *            the symbol uri
	 * @return the dD f_ market cuvol
	 * @throws Exception
	 *             the exception
	 */
	public static final DDF_MarketCuvol marketCuvolFromUrl(
			final String symbolURI) throws Exception {

		final Element root = HelperXML.xmlDocumentDecode(symbolURI);

		// System.out.println(new String(xmlEncode(root, false)));

		final Element tag =
				HelperXML.xmlFirstChild(root, XmlTagCuvol.TAG,
						HelperXML.XML_STOP);

		final DX_XC_Cuvol message = new DX_XC_Cuvol();

		message.decodeXML(tag);

		return message;

	}

	/**
	 * Market quote from url.
	 * 
	 * @param symbolURI
	 *            the symbol uri
	 * @return the dD f_ market quote
	 * @throws Exception
	 *             the exception
	 */
	public static final DDF_MarketQuote marketQuoteFromUrl(
			final String symbolURI) throws Exception {

		final Element root = HelperXML.xmlDocumentDecode(symbolURI);

		// System.out.println(new String(xmlEncode(root, false)));

		final Element tag =
				HelperXML.xmlFirstChild(root, XmlTagQuote.TAG,
						HelperXML.XML_STOP);

		final DX_XQ_Quote message = new DX_XQ_Quote();

		message.decodeXML(tag);

		return message;

	}

}
