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

import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.ddf.message.api.DDF_EOD_EquityForex;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;

/**
 * @author g-litchfield
 *
 */
class DF_3S_StockEOD extends BaseEOD implements DDF_EOD_EquityForex {

	DF_3S_StockEOD() {
		super(DDF_MessageType.EOD_EQTY_FORE);
	}

	DF_3S_StockEOD(final DDF_MessageType messageType) {
		super(messageType);
	}

	// //////////////////////////////////////

	protected long priceOpen = HelperDDF.DDF_EMPTY;
	protected long priceHigh = HelperDDF.DDF_EMPTY;
	protected long priceLow = HelperDDF.DDF_EMPTY;
	protected long priceLast = HelperDDF.DDF_EMPTY;

	protected long sizeVolume = HelperDDF.DDF_EMPTY;

	// //////////////////////////////////////

	/*
	 * <soh>3<symbol>,C<stx><base><exchange ID><reserved><reserved>,
	 * <date>,<open>,<high>,<low>,<last>,<volume><etx>
	 */

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
		final DDF_Fraction frac = getFraction();
		//
		HelperDDF.decimalEncode(priceOpen, frac, buffer, COMMA); // <open>,
		HelperDDF.decimalEncode(priceHigh, frac, buffer, COMMA); // <high>,
		HelperDDF.decimalEncode(priceLow, frac, buffer, COMMA); // <low>,
		HelperDDF.decimalEncode(priceLast, frac, buffer, COMMA); // <last>,
		//
		HelperDDF.longEncode(sizeVolume, buffer, NUL); // <cur volume>,
	}

	@Override
	protected final void decodeBody(final ByteBuffer buffer) {
		final DDF_Fraction frac = getFraction();
		//
		priceOpen = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		priceHigh = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		priceLow = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		priceLast = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		//
		sizeVolume = HelperDDF.longDecode(buffer, NUL); //
	}

	@Override
	protected void appedFields(final StringBuilder text) {

		super.appedFields(text);

		text.append("priceOpen : ");
		text.append(getPriceOpen());
		text.append("\n");

		text.append("priceHigh : ");
		text.append(getPriceHigh());
		text.append("\n");

		text.append("priceLow : ");
		text.append(getPriceLow());
		text.append("\n");

		text.append("priceLast : ");
		text.append(getPriceLast());
		text.append("\n");

		text.append("sizeVolume : ");
		text.append(getSizeVolume());
		text.append("\n");

	}

	@Override
	public PriceValue getPriceOpen() {
		return HelperDDF.newPriceDDF(priceOpen, getFraction());
	}

	@Override
	public PriceValue getPriceHigh() {
		return HelperDDF.newPriceDDF(priceHigh, getFraction());
	}

	@Override
	public PriceValue getPriceLow() {
		return HelperDDF.newPriceDDF(priceLow, getFraction());
	}

	@Override
	public PriceValue getPriceLast() {
		return HelperDDF.newPriceDDF(priceLast, getFraction());
	}

	@Override
	public SizeValue getSizeVolume() {
		return HelperDDF.newSizeDDF(sizeVolume);
	}

}
