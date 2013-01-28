package com.barchart.feed.ddf.instrument.enums;

import com.barchart.feed.ddf.symbol.enums.DDF_Exchange;
import com.barchart.feed.ddf.symbol.enums.DDF_SpreadType;
import com.barchart.feed.ddf.symbol.enums.DDF_TimeZone;
import com.barchart.missive.core.Tag;
import com.barchart.util.values.api.TextValue;

public final class InstrumentFieldDDF {
	
	/** ddf symbol used in market data feed; such as ESZ1. */
	public static final Tag<TextValue> DDF_SYMBOL_REALTIME = Tag.create("DDF_SYMBOL_REALTIME", TextValue.class);

	/** ddf symbol used in historical web service; such as ESZ11. */
	public static final Tag<TextValue> DDF_SYMBOL_HISTORICAL = Tag.create("DDF_SYMBOL_HISTORICAL", TextValue.class);
	
	/** ddf symbol guaranteed to be globally unique; such as ESZ2011. */
	public static final Tag<TextValue> DDF_SYMBOL_UNIVERSAL = Tag.create("DDF_SYMBOL_UNIVERSAL", TextValue.class);
	
	/** ddf exchange source, such as AMEX. */
	public static final Tag<DDF_Exchange> DDF_EXCHANGE = Tag.create("DDF_EXCHANGE", DDF_Exchange.class);
	
	/** ddf exchange description, such as NYSE Liffe. */
	public static final Tag<TextValue> DDF_EXCH_DESC = Tag.create("DDF_EXCH_DESC", TextValue.class);
	
	/** ddf spread type, such as SP. */
	public static final Tag<DDF_SpreadType> DDF_SPREAD = Tag.create("DDF_SPREAD", DDF_SpreadType.class);
	
	/** ddf special time zone info. */
	public static final Tag<DDF_TimeZone> DDF_ZONE = Tag.create("DDF_ZONE", DDF_TimeZone.class);
	
	public static final Tag<TextValue> DDF_EXPIRE_MONTH = Tag.create("DDF_EXPIRE_MONTH", TextValue.class);
	
	public static final Tag<TextValue> DDF_EXPIRE_YEAR = Tag.create("DDF_EXPIRE_YEAR", TextValue.class);
	
}
