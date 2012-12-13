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

import static com.barchart.util.ascii.ASCII.COMMA;

import java.nio.ByteBuffer;

import com.barchart.feed.ddf.message.api.DDF_Prior_IndividCmdy;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.util.values.api.SizeValue;

/**
 * @author g-litchfield
 * 
 */
public class DF_3I_PriorCmdy extends BaseMarket implements
		DDF_Prior_IndividCmdy {

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
	protected final void encodeBody(final ByteBuffer buffer) {

		HelperDDF.longEncode(sizeVolume, buffer, COMMA); // <cur volume>,
		HelperDDF.longEncode(sizeOpenInterest, buffer, COMMA); // <open

	}

	@Override
	protected final void decodeBody(final ByteBuffer buffer) {

		sizeVolume = HelperDDF.longDecode(buffer, COMMA); //
		sizeOpenInterest = HelperDDF.longDecode(buffer, COMMA); //

	}

	@Override
	public SizeValue getSizeVolume() {
		return HelperDDF.newSizeDDF(sizeVolume);
	}

	@Override
	public SizeValue getSizeOpenInterest() {
		return HelperDDF.newSizeDDF(sizeOpenInterest);
	}

	@Override
	protected final void encodeDelay(final ByteBuffer buffer) {
		super.encodeDelay(buffer);
		buffer.put(COMMA); // (,)
	}

	@Override
	protected final void decodeDelay(final ByteBuffer buffer) {
		super.decodeDelay(buffer);
		check(buffer.get(), COMMA); // (,)
	}

}
