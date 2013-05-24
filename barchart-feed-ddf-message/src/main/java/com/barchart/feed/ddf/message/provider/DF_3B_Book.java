/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import static com.barchart.feed.api.consumer.enums.MarketSide.ASK;
import static com.barchart.feed.api.consumer.enums.MarketSide.BID;
import static com.barchart.feed.base.book.enums.MarketBookAction.MODIFY;
import static com.barchart.feed.ddf.message.provider.CodecHelper.DDF_BOOK_LIMIT;
import static com.barchart.feed.ddf.message.provider.CodecHelper.DDF_NO_COUNT;
import static com.barchart.feed.ddf.message.provider.CodecHelper.DDF_NO_PRICES;
import static com.barchart.feed.ddf.message.provider.CodecHelper.DDF_NO_SIZES;
import static com.barchart.feed.ddf.message.provider.CodecHelper.bookAskCodeFrom;
import static com.barchart.feed.ddf.message.provider.CodecHelper.bookAskIndexFrom;
import static com.barchart.feed.ddf.message.provider.CodecHelper.bookBidCodeFrom;
import static com.barchart.feed.ddf.message.provider.CodecHelper.bookBidIndexFrom;
import static com.barchart.feed.ddf.message.provider.CodecHelper.decodeUnsigned_1_book;
import static com.barchart.feed.ddf.message.provider.CodecHelper.encodeUnsigned_1_book;
import static com.barchart.util.ascii.ASCII.COMMA;
import static com.barchart.util.ascii.ASCII.ETX;
import static com.barchart.util.ascii.ASCII.NUL;

import java.nio.ByteBuffer;

import com.barchart.feed.api.consumer.enums.BookLiquidityType;
import com.barchart.feed.base.book.api.MarketBook;
import com.barchart.feed.base.book.api.MarketDoBookEntry;
import com.barchart.feed.base.provider.DefBookEntry;
import com.barchart.feed.ddf.message.api.DDF_MarketBook;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;

// TODO: Auto-generated Javadoc
/** zero size in size array means an empty book entry for this place */
class DF_3B_Book extends BaseMarket implements DDF_MarketBook {

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

	DF_3B_Book() {
		super(DDF_MessageType.BOOK_SNAP);
	}

	DF_3B_Book(final DDF_MessageType messageType) {
		super(messageType);
	}

	// //////////////////////////////////////

	protected int countBid = DDF_NO_COUNT;
	protected int countAsk = DDF_NO_COUNT;

	protected long[] priceBidArray = DDF_NO_PRICES;
	protected long[] priceAskArray = DDF_NO_PRICES;

	protected long[] sizeBidArray = DDF_NO_SIZES;
	protected long[] sizeAskArray = DDF_NO_SIZES;

	// //////////////////////////////////////

	/**
	 * TODO @see TestData
	 * 
	 * XXX note: {@link MarketBook#ENTRY_TOP}.
	 * 
	 * @return the market do book entry[]
	 */
	@Override
	public final MarketDoBookEntry[] entries() {

		final int maximumSize = countBid + countAsk;

		final MarketDoBookEntry[] entries = new MarketDoBookEntry[maximumSize];

		int entryIndex = 0;

		final DDF_Fraction frac = getFraction();

		for (int index = 0; index < countBid; index++) {

			if (sizeBidArray[index] == 0) {
				continue;
			}

			final int place = index + MarketBook.ENTRY_TOP;

			final PriceValue price = HelperDDF.newPriceDDF(
					priceBidArray[index], frac);

			final SizeValue size = HelperDDF.newSizeDDF(sizeBidArray[index]);

			final MarketDoBookEntry entry = new DefBookEntry(MODIFY, BID,
					BookLiquidityType.DEFAULT, place, price, size);

			entries[entryIndex++] = entry;

		}

		for (int index = 0; index < countAsk; index++) {

			if (sizeAskArray[index] == 0) {
				continue;
			}

			final int place = index + 1;

			final PriceValue price = HelperDDF.newPriceDDF(
					priceAskArray[index], frac);

			final SizeValue size = HelperDDF.newSizeDDF(sizeAskArray[index]);

			final MarketDoBookEntry entry = new DefBookEntry(MODIFY, ASK,
					BookLiquidityType.DEFAULT, place, price, size);

			entries[entryIndex++] = entry;

		}

		assert entryIndex == maximumSize;

		return entries;

	}

	/*
	 * <soh>3<symbol>,B<stx><base><exch><#bid><#ask>[,<price-level-size>]<etx>
	 */

	/*
	 * <soh><rec><symbol>,<subrec><stx><base><exch>(<delay>)(,)(<spread>)||
	 * 
	 * <bid count><ask count>[,<price-level-size>]
	 * 
	 * ||<etx>
	 */

	// no delay field in book snapshots
	@Override
	protected final void encodeDelay(final ByteBuffer buffer) {
	}

	@Override
	protected final void decodeDelay(final ByteBuffer buffer) {
	}

	// no time stamp field in book snapshot
	@Override
	protected final void encodeTail(final ByteBuffer buffer) {
		buffer.put(ETX);
	}

	@Override
	protected final void decodeTail(final ByteBuffer buffer) {
		check(buffer.get(), ETX);
		setDecodeDefaults();
	}

	@Override
	protected final void encodeBody(final ByteBuffer buffer) {

		final DDF_Fraction frac = getFraction();

		// sizes
		encodeUnsigned_1_book(countBid, buffer);
		encodeUnsigned_1_book(countAsk, buffer);

		// bids
		for (int index = 0; index < DDF_BOOK_LIMIT; index++) {
			if (sizeBidArray[index] == 0) {
				continue;
			}
			buffer.put(COMMA);
			final byte code = bookBidCodeFrom(index);
			HelperDDF.decimalEncode(priceBidArray[index], frac, buffer, code);
			HelperDDF.longEncode(sizeBidArray[index], buffer, NUL);
		}

		// asks
		for (int index = 0; index < DDF_BOOK_LIMIT; index++) {
			if (sizeAskArray[index] == 0) {
				continue;
			}
			buffer.put(COMMA);
			final byte code = bookAskCodeFrom(index);
			HelperDDF.decimalEncode(priceAskArray[index], frac, buffer, code);
			HelperDDF.longEncode(sizeAskArray[index], buffer, NUL);
		}

	}

	@Override
	protected final void decodeBody(final ByteBuffer buffer) {

		final DDF_Fraction frac = getFraction();

		// allocate maximum array size
		priceBidArray = new long[DDF_BOOK_LIMIT];
		priceAskArray = new long[DDF_BOOK_LIMIT];
		sizeBidArray = new long[DDF_BOOK_LIMIT];
		sizeAskArray = new long[DDF_BOOK_LIMIT];

		// sizes
		countBid = decodeUnsigned_1_book(buffer);
		countAsk = decodeUnsigned_1_book(buffer);

		// bids
		for (int count = 0; count < countBid; count++) {
			check(buffer.get(), COMMA);
			final long price = HelperDDF.decimalDecode(frac, buffer, NUL);
			final byte code = buffer.get();
			final long size = HelperDDF.longDecode(buffer, NUL);
			//
			final int index = bookBidIndexFrom(code);
			priceBidArray[index] = price;
			sizeBidArray[index] = size;
		}

		// asks
		for (int count = 0; count < countAsk; count++) {
			check(buffer.get(), COMMA);
			final long price = HelperDDF.decimalDecode(frac, buffer, NUL);
			final byte code = buffer.get();
			final long size = HelperDDF.longDecode(buffer, NUL);
			//
			final int index = bookAskIndexFrom(code);
			priceAskArray[index] = price;
			sizeAskArray[index] = size;
		}

	}

	@Override
	protected void appedFields(final StringBuilder text) {
		
		super.appedFields(text);

		text.append("Bid qty\tprice\tAsk qty\n");
		text.append("--------------------------------\n");
		if (priceAskArray.length == sizeBidArray.length) {
			for (int i = priceAskArray.length -1; i > 0; i--) {
				long askPrice = priceAskArray[i];
				long askSize = sizeAskArray[i];
				if (askSize != 0) {
					text.append(" \t" + askPrice + "\t" + askSize + "\n");
				}
			}
		}
		
		if (priceBidArray.length == sizeBidArray.length) {
			for (int i = 0; i < priceBidArray.length; i++) {
				long bidPrice = priceBidArray[i];
				long bidSize = sizeBidArray[i];
				if (bidSize != 0) {
					text.append(bidSize + "\t" + bidPrice + "\t\n");
				}
			}
		}
		
		
	}

}
