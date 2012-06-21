/**
 * 
 */
package com.barchart.feed.ddf.message.api;

import com.barchart.util.values.api.SizeValue;

/**
 * @author g-litchfield
 * 
 */
public interface DDF_Prior_IndividCmdy extends DDF_MarketBase {

	SizeValue getSizeVolume();

	SizeValue getSizeOpenInterest();

}
