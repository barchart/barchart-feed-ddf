/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.util.ascii.ASCII.COMMA;

import java.nio.ByteBuffer;

import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.ddf.message.api.DDF_MarketSnapshot;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;

class DF_21_Snap extends BaseMarket implements DDF_MarketSnapshot {

	@Override
	public <Result, Param> Result accept(
			final DDF_MessageVisitor<Result, Param> visitor, final Param param) {
		return visitor.visit(this, param);
	}

	DF_21_Snap() {
		super(DDF_MessageType.SNAP_FORE_EXCH); // 21
	}

	DF_21_Snap(final DDF_MessageType messageType) {
		super(messageType);
	}

	// //////////////////////////////////////

	protected long priceAsk = HelperDDF.DDF_EMPTY;
	protected long priceBid = HelperDDF.DDF_EMPTY;

	protected long priceOpen = HelperDDF.DDF_EMPTY;
	protected long priceOpen2 = HelperDDF.DDF_EMPTY;

	protected long priceHigh = HelperDDF.DDF_EMPTY;
	protected long priceLow = HelperDDF.DDF_EMPTY;

	protected long priceClose = HelperDDF.DDF_EMPTY;
	protected long priceClose2 = HelperDDF.DDF_EMPTY;

	protected long priceSettle = HelperDDF.DDF_EMPTY;

	/** last trade price for this day */
	protected long priceLast = HelperDDF.DDF_EMPTY;

	/** last trade price for past day */
	protected long priceLastPrevious = HelperDDF.DDF_EMPTY;

	protected long sizeInterest = HelperDDF.DDF_EMPTY;

	protected long sizeVolume = HelperDDF.DDF_EMPTY;
	protected long sizeVolumePrevious = HelperDDF.DDF_EMPTY;

	// //////////////////////////////////////

	@Override
	public PriceValue getPriceAsk() {
		return HelperDDF.newPriceDDF(priceAsk, getFraction());
	}

	@Override
	public PriceValue getPriceBid() {
		return HelperDDF.newPriceDDF(priceBid, getFraction());
	}

	@Override
	public PriceValue getPriceClose2() {
		return HelperDDF.newPriceDDF(priceClose2, getFraction());
	}

	@Override
	public PriceValue getPriceLast() {
		return HelperDDF.newPriceDDF(priceLast, getFraction());
	}

	@Override
	public PriceValue getPriceLastPrevious() {
		return HelperDDF.newPriceDDF(priceLastPrevious, getFraction());
	}

	@Override
	public PriceValue getPriceOpen2() {
		return HelperDDF.newPriceDDF(priceOpen2, getFraction());
	}

	@Override
	public PriceValue getPriceSettle() {
		return HelperDDF.newPriceDDF(priceSettle, getFraction());
	}

	@Override
	public SizeValue getSizeVolumePrevious() {
		return HelperDDF.newSizeDDF(sizeVolumePrevious);
	}

	@Override
	public PriceValue getPriceClose() {
		return HelperDDF.newPriceDDF(priceClose, getFraction());
	}

	@Override
	public PriceValue getPriceHigh() {
		return HelperDDF.newPriceDDF(priceHigh, getFraction());
	}

	@Override
	public SizeValue getSizeInterest() {
		return HelperDDF.newSizeDDF(sizeInterest);
	}

	@Override
	public PriceValue getPriceLow() {
		return HelperDDF.newPriceDDF(priceLow, getFraction());
	}

	@Override
	public PriceValue getPriceOpen() {
		return HelperDDF.newPriceDDF(priceOpen, getFraction());
	}

	@Override
	public SizeValue getSizeVolume() {
		return HelperDDF.newSizeDDF(sizeVolume);
	}

	/*
	 * <soh><rec><symbol>,<subrec><stx><base><exch><delay>(,)(<spread>)||
	 * 
	 * <open>,<high>,<low>,<last>,<bid>,<ask>,
	 * 
	 * <open2>,<prev>,<close>,<close2>,<settle>,
	 * 
	 * <prev volume>,<open interest>,<cur volume>,
	 * 
	 * ||<day><session><etx>||<time stamp>
	 */

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

	@Override
	protected final void encodeBody(final ByteBuffer buffer) {
		final DDF_Fraction frac = getFraction();
		//
		HelperDDF.decimalEncode(priceOpen, frac, buffer, COMMA); // <open>,
		HelperDDF.decimalEncode(priceHigh, frac, buffer, COMMA); // <high>,
		HelperDDF.decimalEncode(priceLow, frac, buffer, COMMA); // <low>,
		HelperDDF.decimalEncode(priceLast, frac, buffer, COMMA); // <last>,
		HelperDDF.decimalEncode(priceBid, frac, buffer, COMMA); // <bid>,
		HelperDDF.decimalEncode(priceAsk, frac, buffer, COMMA); // <ask>,
		HelperDDF.decimalEncode(priceOpen2, frac, buffer, COMMA); // <open2>,
		HelperDDF.decimalEncode(priceLastPrevious, frac, buffer, COMMA); // <prev>,
		HelperDDF.decimalEncode(priceClose, frac, buffer, COMMA); // <close>,
		HelperDDF.decimalEncode(priceClose2, frac, buffer, COMMA); // <close2>,
		HelperDDF.decimalEncode(priceSettle, frac, buffer, COMMA); // <settle>,
		//
		HelperDDF.longEncode(sizeVolumePrevious, buffer, COMMA); // <prev
																	// volume>,
		HelperDDF.longEncode(sizeInterest, buffer, COMMA); // <open interest>,
		HelperDDF.longEncode(sizeVolume, buffer, COMMA); // <cur volume>,
	}

	@Override
	protected final void decodeBody(final ByteBuffer buffer) {
		final DDF_Fraction frac = getFraction();
		//
		priceOpen = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		priceHigh = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		priceLow = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		priceLast = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		priceBid = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		priceAsk = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		priceOpen2 = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		priceLastPrevious = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		priceClose = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		priceClose2 = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		priceSettle = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		//
		sizeVolumePrevious = HelperDDF.longDecode(buffer, COMMA); //
		sizeInterest = HelperDDF.longDecode(buffer, COMMA); //
		sizeVolume = HelperDDF.longDecode(buffer, COMMA); //
	}

	@Override
	protected void appedFields(final StringBuilder text) {

		super.appedFields(text);

		text.append("priceAsk : ");
		text.append(getPriceAsk());
		text.append("\n");

		text.append("priceBid : ");
		text.append(getPriceBid());
		text.append("\n");

		// TODO

	}

}
