/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/**
 *
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.util.common.ascii.ASCII.COMMA;
import static com.barchart.util.common.ascii.ASCII.NUL;

import java.nio.ByteBuffer;

import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.api.DDF_Prior_IndividCmdy;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.util.HelperDDF;

/**
 * @author g-litchfield
 *
 */
class DF_3I_PriorCmdy extends BaseEOD implements DDF_Prior_IndividCmdy {

	DF_3I_PriorCmdy() {
		super(DDF_MessageType.PRIOR_INDIV_CMDY);
	}

	DF_3I_PriorCmdy(final DDF_MessageType messageType) {
		super(messageType);
	}

	// //////////////////////////////////////

	protected long sizeVolume = HelperDDF.DDF_EMPTY;
	protected long sizeOpenInterest = HelperDDF.DDF_EMPTY;

	// //////////////////////////////////////

	@Override
	public <Result, Param> Result accept(
			final DDF_MessageVisitor<Result, Param> visitor, final Param param) {
		return visitor.visit(this, param);
	}

	@Override
	protected void encodeHead(final ByteBuffer buffer) {
		super.encodeHead(buffer);
		buffer.put(COMMA);
		encodeDay(buffer);
	}

	@Override
	protected void decodeHead(final ByteBuffer buffer) {
		super.decodeHead(buffer);
		check(buffer.get(), COMMA);
		decodeDay(buffer);
	}

	@Override
	protected final void encodeBody(final ByteBuffer buffer) {

		HelperDDF.longEncode(sizeVolume, buffer, COMMA); // <cur volume>,
		HelperDDF.longEncode(sizeOpenInterest, buffer, NUL); // <open

	}

	@Override
	protected final void decodeBody(final ByteBuffer buffer) {

		sizeVolume = HelperDDF.longDecode(buffer, COMMA); //
		sizeOpenInterest = HelperDDF.longDecode(buffer, NUL); //

	}

	@Override
	public SizeValue getSizeVolume() {
		return HelperDDF.newSizeDDF(sizeVolume);
	}

	@Override
	public SizeValue getSizeOpenInterest() {
		return HelperDDF.newSizeDDF(sizeOpenInterest);
	}

}
