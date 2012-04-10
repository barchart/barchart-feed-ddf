/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.util.ascii.ASCII.*;

import java.nio.ByteBuffer;

import com.barchart.feed.ddf.message.api.DDF_MarketParameter;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.message.enums.DDF_ParamType;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;

// TODO: Auto-generated Javadoc
class DF_20_Param extends BaseMarket implements DDF_MarketParameter {

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Base#accept(com.barchart.feed.ddf.message.api.DDF_MessageVisitor, java.lang.Object)
	 */
	@Override
	public <Result, Param> Result accept(
			DDF_MessageVisitor<Result, Param> visitor, Param param) {
		return visitor.visit(this, param);
	}

	DF_20_Param() {
		super(DDF_MessageType.PARAM);
	}

	DF_20_Param(final DDF_MessageType messageType) {
		super(messageType);
	}

	// //////////////////////////////////////

	private byte ordParam = DDF_ParamType.UNKNOWN.ord;

	/**
	 * can represent:
	 * 
	 * 1) price (with exponent from fraction);
	 * 
	 * 2) size (regardless of the exponent in fraction):
	 */
	protected long value = HelperDDF.DDF_EMPTY;

	// //////////////////////////////////////

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketParameter#getParamType()
	 */
	@Override
	public final DDF_ParamType getParamType() {
		return DDF_ParamType.fromOrd(ordParam);
	}

	/**
	 * Sets the param type.
	 *
	 * @param type the new param type
	 */
	public final void setParamType(final DDF_ParamType type) {
		ordParam = type.ord;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketParameter#getAsPrice()
	 */
	@Override
	public final PriceValue getAsPrice() {
		return HelperDDF.newPriceDDF(value, getFraction());
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketParameter#getAsSize()
	 */
	@Override
	public final SizeValue getAsSize() {
		return HelperDDF.newSizeDDF(value);
	}

	/*
	 * <soh><rec><symbol>,<subrec><stx><base><exch>(<delay>)(,)(<spread>)||
	 * 
	 * <value>,<element><modifier>
	 * 
	 * ||<day><session><etx>||<time stamp>
	 */
	@Override
	protected final void encodeBody(final ByteBuffer buffer) {

		final DDF_ParamType param = getParamType();

		switch (param.kind) {
		default:
		case SIZE:
			HelperDDF.longEncode(value, buffer, COMMA); // <value>,
			break;
		case PRICE:
			HelperDDF.decimalEncode(value, getFraction(), buffer, COMMA); // <value>,
			break;
		}

		buffer.putChar(param.code); // <element> <modifier>

	}

	@Override
	protected final void decodeBody(final ByteBuffer buffer) {

		final long value = HelperDDF.longDecode(buffer, COMMA); // <value>,

		final char code = buffer.getChar(); // <element> <modifier>

		final DDF_ParamType param = DDF_ParamType.fromCode(code);

		ordParam = param.ord;

		switch (param.kind) {
		default:
		case SIZE:
			this.value = value;
			break;
		case PRICE:
			this.value = HelperDDF.fromBinaryToDecimal(value, getFraction());
			break;
		}

	}

	@Override
	protected void appedFields(final StringBuilder text) {

		super.appedFields(text);

		text.append("param type  : ");
		text.append(getParamType());
		text.append("\n");

		text.append("param value : ");
		text.append(value);
		text.append("\n");

	}

}
