/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import java.nio.ByteBuffer;

import org.joda.time.DateTimeZone;

import com.barchart.feed.base.values.api.TimeValue;
import com.barchart.feed.ddf.message.api.DDF_ControlTimestamp;
import com.barchart.feed.ddf.message.api.DDF_MessageVisitor;
import com.barchart.feed.ddf.message.enums.DDF_MessageType;
import com.barchart.feed.ddf.util.HelperDDF;
import com.barchart.util.common.ascii.ASCII;

// TODO: Auto-generated Javadoc
class DF_C0_Time extends BaseControl implements DDF_ControlTimestamp {

	static final DateTimeZone zoneCST = DateTimeZone.forID("America/Chicago");

	protected long stampUTC;

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Base#accept(com.barchart.feed.ddf.message.api.DDF_MessageVisitor, java.lang.Object)
	 */
	@Override
	public <Result, Param> Result accept(
			final DDF_MessageVisitor<Result, Param> visitor, final Param param) {
		return visitor.visit(this, param);
	}

	DF_C0_Time() {
		super(DDF_MessageType.TIME_STAMP);
	}

	DF_C0_Time(final DDF_MessageType messageType) {
		super(messageType);
		millisUTC = System.currentTimeMillis();
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Base#encodeDDF(java.nio.ByteBuffer)
	 */
	@Override
	public final void encodeDDF(final ByteBuffer buffer) {

		final long timeCST = HelperDDF.timeEncode(stampUTC, zoneCST);

		buffer.put(ASCII.SOH);
		buffer.put(ASCII.POUND);
		HelperDDF.longEncode(timeCST, buffer, ASCII.NUL);
		buffer.put(ASCII.ETX);

	}

	/* <soh>#20110923142300<etx> */
	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.provider.Base#decodeDDF(java.nio.ByteBuffer)
	 */
	@Override
	public final void decodeDDF(final ByteBuffer buffer) {

		CodecHelper.check(buffer.get(), ASCII.SOH);
		CodecHelper.check(buffer.get(), ASCII.POUND);
		final long timeCST = HelperDDF.longDecode(buffer, ASCII.NUL);
		CodecHelper.check(buffer.get(), ASCII.ETX);

		stampUTC = HelperDDF.timeDecode(timeCST, zoneCST); //

	}

	@Override
	protected void appedFields(final StringBuilder text) {

		super.appedFields(text);

		text.append("time stamp UTC : ");
		text.append(getStampUTC());
		text.append("\n");

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.message.api.DDF_ControlTimestamp#getStampUTC()
	 */
	@Override
	public TimeValue getStampUTC() {
		return HelperDDF.newTimeDDF(stampUTC);
	}

}
