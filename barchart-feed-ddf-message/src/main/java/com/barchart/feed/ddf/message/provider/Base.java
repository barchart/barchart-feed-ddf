/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.feed.ddf.util.HelperXML;
import com.barchart.util.values.api.TimeValue;

abstract class Base implements DDF_BaseMessage, Codec {

	protected static final Logger log = LoggerFactory.getLogger(Base.class);

	Base() {
		setMessageType(DDF_MessageType.UNKNOWN);
	}

	Base(final DDF_MessageType messageType) {
		setMessageType(messageType);
	}

	//

	private byte ordMessageType;

	@Override
	public final DDF_MessageType getMessageType() {
		return DDF_MessageType.fromOrd(ordMessageType);
	}

	public final void setMessageType(final DDF_MessageType messageType) {
		ordMessageType = messageType.ord;
	}

	//

	protected long millisUTC = HelperDDF.DDF_EMPTY;

	@Override
	public final TimeValue getTime() {
		return HelperDDF.newTimeDDF(millisUTC);
	}

	//

	static final void check(final byte left, final byte right) {
		if (left == right) {
			return;
		} else {
			throw new RuntimeException("no match;" + " left=" + left
					+ " right=" + right);
		}
	}

	//

	@Override
	public void decodeDDF(final ByteBuffer buffer) {
		throw new UnsupportedOperationException("you must override");
	}

	@Override
	public void encodeDDF(final ByteBuffer buffer) {
		throw new UnsupportedOperationException("you must override");
	}

	//

	protected String xmlTagName() {
		throw new UnsupportedOperationException("you must override");
	}

	@Override
	public final void decodeXML(final ByteBuffer buffer) {
		final byte[] array = buffer.array();
		final int start = buffer.position();
		final int finish = buffer.limit();
		final Element tag = HelperXML.xmlDodumentDecode(array, start, finish,
				true);
		decodeXML(tag);
	}

	@Override
	public final void encodeXML(final ByteBuffer buffer) {
		final Element tag = HelperXML.xmlNewDocument(xmlTagName());
		encodeXML(tag);
		final byte[] array = HelperXML.xmlDocumentEncode(tag, true);
		buffer.put(array);
	}

	@Override
	public void decodeXML(final Element root) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void encodeXML(final Element root) {
		throw new UnsupportedOperationException();
	}

	//

	@Override
	public <Result, Param> Result accept(
			DDF_MessageVisitor<Result, Param> visitor, Param param) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {

		final ByteBuffer buffer = ByteBuffer.allocate(1024);

		encodeDDF(buffer);

		return new String(buffer.array(), 0, buffer.position());

	}

	protected void appedFields(final StringBuilder text) {

		text.append("\n");

		text.append("message type : ");
		text.append(getMessageType());
		text.append("\n");

		text.append("message time : ");
		text.append(getTime());
		text.append("\n");

	}

	@Override
	public String toStringFields() {

		final StringBuilder text = new StringBuilder(1024);

		appedFields(text);

		return text.toString();

	}

}
