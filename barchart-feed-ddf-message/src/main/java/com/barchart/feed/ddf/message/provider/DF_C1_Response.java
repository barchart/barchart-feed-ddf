/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import java.nio.ByteBuffer;

import com.barchart.feed.ddf.message.api.DDF_ControlResponse;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.provider.ValueBuilder;

// TODO: Auto-generated Javadoc
class DF_C1_Response extends BaseControl implements DDF_ControlResponse {

	protected byte[] comment;

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Base#accept(com.barchart.feed.ddf.message.api.DDF_MessageVisitor, java.lang.Object)
	 */
	@Override
	public <Result, Param> Result accept(
			DDF_MessageVisitor<Result, Param> visitor, Param param) {
		return visitor.visit(this, param);
	}

	DF_C1_Response() {
		super(DDF_MessageType.TCP_COMMAND);
	}

	DF_C1_Response(final DDF_MessageType messageType) {
		super(messageType);
		millisUTC = System.currentTimeMillis();
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Base#encodeDDF(java.nio.ByteBuffer)
	 */
	@Override
	public final void encodeDDF(final ByteBuffer buffer) {

		final byte code = (byte) (getMessageType().code() >> 8);

		buffer.put(code);
		buffer.put(comment);

	}

	/* CLockout ip xxx.xxx.xxx */
	/* + Successful login */
	/* - Login failed */
	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Base#decodeDDF(java.nio.ByteBuffer)
	 */
	@Override
	public final void decodeDDF(final ByteBuffer buffer) {

		final byte code = (byte) (getMessageType().code() >> 8);

		CodecHelper.check(buffer.get(), code);

		int size = buffer.remaining();
		comment = new byte[size];
		buffer.get(comment);

	}

	@Override
	protected void appedFields(final StringBuilder text) {

		super.appedFields(text);

		text.append("message comment : ");
		text.append(getComment());
		text.append("\n");

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.api.DDF_ControlResponse#getComment()
	 */
	@Override
	public TextValue getComment() {
		return ValueBuilder.newText(comment);
	}

}
