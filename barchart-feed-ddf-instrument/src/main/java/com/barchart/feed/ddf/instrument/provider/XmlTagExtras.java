/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.instrument.provider;

/**
 * http://extras.ddfplus.com/instruments/?lookup=esu0
 * 
 * 
 * <instruments status="200" count="1">
 * 
 * <instrument
 * 
 * lookup="esu0"
 * 
 * status="200"
 * 
 * guid="ESU2010"
 * 
 * symbol_ddf="ESU0"
 * 
 * symbol_historical="ESU10"
 * 
 * symbol_description="E-Mini S&P 500"
 * 
 * symbol_expire="2010-09-01T00:00:00-05:00"
 * 
 * symbol_ddf_expire_month="U"
 * 
 * symbol_ddf_expire_year="0"
 * 
 * symbol_type="future"
 * 
 * exchange="XCME"
 * 
 * exchange_channel="GBLX"
 * 
 * exchange_description="CMEGroup CME (Globex Mini)"
 * 
 * exchange_ddf="M"
 * 
 * time_zone_ddf="America/Chicago" tick_increment="25"
 * 
 * base_code="2"
 * 
 * unit_code="A"
 * 
 * point_value="50"/>
 * 
 * </instruments>
 * 
 */

public interface XmlTagExtras {

	String TAG = "instrument";

	//

	/** response status */
	String LOOKUP = "lookup";
	String STATUS = "status";

	/** barchart globally unique identifier */
	String ID = "id";
	String UNDERLIER_ID = "underlier_id";

	/** barchart globally unique symbol */
	String SYMBOL_REALTIME = "symbol_realtime";
	
	/** used by historical query system */
	String SYMBOL_HIST = "symbol_historical";
	
	String ALT_SYMBOL = "alternate_symbol";

	/** ddf feed codes */
	String EXCHANGE_DDF = "exchange_ddf";
	String SYMBOL_DDF_REAL = "symbol_ddf";
	String SYMBOL_DDF_EXPIRE_YEAR = "symbol_ddf_expire_year";
	String SYMBOL_DDF_EXPIRE_MONTH = "symbol_ddf_expire_month";
	String BASE_CODE_DDF = "base_code";

	/** not used by ddf feed; but one-to-one mapping to base code */
	String UNIT_CODE_DDF = "unit_code";

	/** implied by symbol/exchange */
	String TIME_ZONE_DDF = "time_zone_ddf";

	/** http://en.wikipedia.org/wiki/Market_identification_code */
	String EXCHANGE = "exchange";
	String EXCHANGE_CHANNEL = "exchange_channel";
	String EXCHANGE_COMMENT = "exchange_description";

	String SYMBOL_CODE_CFI = "symbol_cfi";

	/** expiration date, if any */
	String SYMBOL_EXPIRE = "symbol_expire";


	String SYMBOL_COMMENT = "symbol_description";

	/** price step expressed as ddf base code fraction */
	String PRICE_TICK_INCREMENT = "tick_increment";

	String PRICE_POINT_VALUE = "point_value";

}
