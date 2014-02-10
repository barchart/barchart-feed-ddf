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

// TODO: Auto-generated Javadoc
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

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.api.DDF_BaseMessage#getMessageType()
	 */
	@Override
	public final DDF_MessageType getMessageType() {
		return DDF_MessageType.fromOrd(ordMessageType);
	}

	/**
	 * Sets the message type.
	 *
	 * @param messageType the new message type
	 */
	public final void setMessageType(final DDF_MessageType messageType) {
		ordMessageType = messageType.ord;
	}

	//

	protected long millisUTC = HelperDDF.DDF_EMPTY;

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

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Codec#decodeDDF(java.nio.ByteBuffer)
	 */
	@Override
	public void decodeDDF(final ByteBuffer buffer) {
		throw new UnsupportedOperationException("you must override");
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Codec#encodeDDF(java.nio.ByteBuffer)
	 */
	@Override
	public void encodeDDF(final ByteBuffer buffer) {
		throw new UnsupportedOperationException("you must override");
	}

	//

	protected String xmlTagName() {
		throw new UnsupportedOperationException("you must override");
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Codec#decodeXML(java.nio.ByteBuffer)
	 */
	@Override
	public final void decodeXML(final ByteBuffer buffer) {
		final byte[] array = buffer.array();
		final int start = buffer.position();
		final int finish = buffer.limit();
		final Element tag = HelperXML.xmlDocumentDecode(array, start, finish,
				true);
		decodeXML(tag);
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Codec#encodeXML(java.nio.ByteBuffer)
	 */
	@Override
	public final void encodeXML(final ByteBuffer buffer) {
		final Element tag = HelperXML.xmlNewDocument(xmlTagName());
		encodeXML(tag);
		final byte[] array = HelperXML.xmlDocumentEncode(tag, true);
		buffer.put(array);
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Codec#decodeXML(org.w3c.dom.Element)
	 */
	@Override
	public void decodeXML(final Element root) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Codec#encodeXML(org.w3c.dom.Element)
	 */
	@Override
	public void encodeXML(final Element root) {
		throw new UnsupportedOperationException();
	}

	//

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.api.DDF_BaseMessage#accept(com.barchart.feed.ddf.message.api.DDF_MessageVisitor, java.lang.Object)
	 */
	@Override
	public <Result, Param> Result accept(
			DDF_MessageVisitor<Result, Param> visitor, Param param) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
		text.append(millisUTC);
		text.append("\n");

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.api.DDF_BaseMessage#toStringFields()
	 */
	@Override
	public String toStringFields() {

		final StringBuilder text = new StringBuilder(1024);

		appedFields(text);

		return text.toString();

	}

}
