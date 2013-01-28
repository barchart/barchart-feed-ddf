/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.feed.base.book.enums.MarketBookAction.MODIFY;
import static com.barchart.util.ascii.ASCII.COMMA;

import java.nio.ByteBuffer;

import com.barchart.feed.base.book.api.MarketBook;
import com.barchart.feed.base.book.api.MarketDoBookEntry;
import com.barchart.feed.base.book.enums.MarketBookSide;
import com.barchart.feed.base.provider.DefBookEntry;
import com.barchart.feed.ddf.message.api.DDF_MarketBookTop;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.feed.inst.enums.MarketBookType;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;
import com.barchart.util.values.provider.ValueConst;

// TODO: Auto-generated Javadoc
class DF_28_BookTop extends BaseMarket implements DDF_MarketBookTop {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.message.provider.Base#accept(com.barchart.feed.
	 * ddf.message.api.DDF_MessageVisitor, java.lang.Object)
	 */
	@Override
	public <Result, Param> Result accept(
			final DDF_MessageVisitor<Result, Param> visitor, final Param param) {
		return visitor.visit(this, param);
	}

	DF_28_BookTop() {
		super(DDF_MessageType.BOOK_TOP);
	}

	DF_28_BookTop(final DDF_MessageType messageType) {
		super(messageType);
	}

	// //////////////////////////////////////

	protected long priceBid = HelperDDF.DDF_EMPTY;
	protected long priceAsk = HelperDDF.DDF_EMPTY;

	protected long sizeBid = HelperDDF.DDF_EMPTY;
	protected long sizeAsk = HelperDDF.DDF_EMPTY;

	// //////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketBookTop#getPriceBid()
	 */
	@Override
	public PriceValue getPriceBid() {
		return HelperDDF.newPriceDDF(priceBid, getFraction());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketBookTop#getSizeBid()
	 */
	@Override
	public SizeValue getSizeBid() {
		return HelperDDF.newSizeDDF(sizeBid);
	}

	//

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketBookTop#getPriceAsk()
	 */
	@Override
	public PriceValue getPriceAsk() {
		return HelperDDF.newPriceDDF(priceAsk, getFraction());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barchart.feed.ddf.message.api.DDF_MarketBookTop#getSizeAsk()
	 */
	@Override
	public SizeValue getSizeAsk() {
		return HelperDDF.newSizeDDF(sizeAsk);
	}

	//

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.barchart.feed.ddf.message.api.DDF_MarketBookTop#entry(com.barchart
	 * .feed.base.api.market.enums.MarketBookSide)
	 */
	@Override
	public final MarketDoBookEntry entry(final MarketBookSide side) {

		final PriceValue price;
		final SizeValue size;

		switch (side) {
		case BID:
			price = getPriceBid();
			size = getSizeBid();
			break;
		case ASK:
			price = getPriceAsk();
			size = getSizeAsk();
			break;
		default:
			assert false : "invalid side";
			price = ValueConst.NULL_PRICE;
			size = ValueConst.NULL_SIZE;
			break;
		}

		/** XXX note: {@link MarketBook#ENTRY_TOP} */
		final MarketDoBookEntry entry = new DefBookEntry(MODIFY, side, MarketBookType.DEFAULT,
				MarketBook.ENTRY_TOP, price, size);

		return entry;

	}

	/*
	 * <soh><rec><symbol>,<subrec><stx><base><exch>(<delay>)(,)(<spread>)||
	 * 
	 * <bid price>,<bid size>,<ask price>,<ask size>,
	 * 
	 * ||<day><session><etx>||<time stamp>
	 */
	@Override
	protected final void encodeBody(final ByteBuffer buffer) {
		final DDF_Fraction frac = getFraction();
		//
		HelperDDF.decimalEncode(priceBid, frac, buffer, COMMA); // <bid price>,
		HelperDDF.longEncode(sizeBid, buffer, COMMA); // <bid size>,
		HelperDDF.decimalEncode(priceAsk, frac, buffer, COMMA); // <ask price>,
		HelperDDF.longEncode(sizeAsk, buffer, COMMA); // <ask size>,
	}

	@Override
	protected final void decodeBody(final ByteBuffer buffer) {
		final DDF_Fraction frac = getFraction();
		//
		priceBid = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		sizeBid = HelperDDF.longDecode(buffer, COMMA); //
		priceAsk = HelperDDF.decimalDecode(frac, buffer, COMMA); //
		sizeAsk = HelperDDF.longDecode(buffer, COMMA); //
	}

	@Override
	protected void appedFields(final StringBuilder text) {

		super.appedFields(text);

		text.append("priceBid : ");
		text.append(getPriceBid());
		text.append("\n");

		text.append("sizeBid : ");
		text.append(getSizeBid());
		text.append("\n");

		text.append("priceAsk : ");
		text.append(getPriceAsk());
		text.append("\n");

		text.append("sizeAsk : ");
		text.append(getSizeAsk());
		text.append("\n");

	}

}
