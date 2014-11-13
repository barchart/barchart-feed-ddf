/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.util.common.ascii.ASCII.COMMA;

import java.nio.ByteBuffer;

import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.ddf.message.api.DDF_MarketTrade;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.util.HelperDDF;

// TODO: Auto-generated Javadoc
class DF_27_Trade extends BaseMarket implements DDF_MarketTrade {

	
	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Base#accept(com.barchart.feed.ddf.message.api.DDF_MessageVisitor, java.lang.Object)
	 */
	@Override
	public <Result, Param> Result accept(
			DDF_MessageVisitor<Result, Param> visitor, Param param) {
		return visitor.visit(this, param);
	}

	DF_27_Trade() {
		super(DDF_MessageType.TRADE);
	}

	DF_27_Trade(final DDF_MessageType messageType) {
		super(messageType);
	}

	// //////////////////////////////////////

	protected long price = HelperDDF.DDF_EMPTY;

	protected long size = HelperDDF.DDF_EMPTY;

	// //////////////////////////////////////

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketTrade#getPrice()
	 */
	@Override
	public final PriceValue getPrice() {
		return HelperDDF.newPriceDDF(price, getFraction());
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketTrade#getSize()
	 */
	@Override
	public final SizeValue getSize() {
		return HelperDDF.newSizeDDF(size);
	}

	/*
	 * <soh><rec><symbol>,<subrec><stx><base><exch>(<delay>)(,)(<spread>)||
	 * 
	 * <price>, <size>,
	 * 
	 * ||<day/session><etx>||<time stamp>
	 */
	@Override
	protected final void encodeBody(final ByteBuffer buffer) {
		HelperDDF.decimalEncode(price, getFraction(), buffer, COMMA); // <price>,
		HelperDDF.longEncode(size, buffer, COMMA); // <size>,
	}
	
	@Override
	protected final void decodeBody(final ByteBuffer buffer) {
		price = HelperDDF.decimalDecode(getFraction(), buffer, COMMA); // <price>,
		size = HelperDDF.longDecode(buffer, COMMA); // <size>,
	}

	@Override
	protected void appedFields(final StringBuilder text) {

		super.appedFields(text);

		text.append("price : ");
		text.append(getPrice());
		text.append("\n");

		text.append("size : ");
		text.append(getSize());
		text.append("\n");

	}

}
