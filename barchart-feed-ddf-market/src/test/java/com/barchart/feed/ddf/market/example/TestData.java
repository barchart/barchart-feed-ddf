/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.market.example;

/**
 * The Class TestData.
 */
public class TestData {

	/**
	 * 
	 * 16:39:52.875 [# ddf-messages] DEBUG c.d.f.market.example.MarketManager -
	 * message : <BOOK askcount="5" askprices="66370,66380,66400,66420,66430"
	 * asksizes="2,1,1,1,2" basecode="A" bidcount="5"
	 * bidprices="66290,66270,66240,66230,66220" bidsizes="1,1,1,1,2"
	 * symbol="RJZ1"/> 16:39:52.875 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : BID 2
	 * Price > 66270 -2 Size > 1 16:39:52.875 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : BID 3
	 * Price > 66240 -2 Size > 1 16:39:52.875 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : BID 4
	 * Price > 66230 -2 Size > 1 16:39:52.876 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : BID 5
	 * Price > 66220 -2 Size > 2 16:39:52.876 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : ASK 2
	 * Price > 66380 -2 Size > 1 16:39:52.876 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : ASK 3
	 * Price > 66400 -2 Size > 1 16:39:52.876 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : ASK 4
	 * Price > 66420 -2 Size > 1 16:39:52.877 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : ASK 5
	 * Price > 66430 -2 Size > 2
	 * 
	 * */

	static final byte[] err1 = "<BOOK askcount=\"5\" askprices=\"66370,66380,66400,66420,66430\" asksizes=\"2,1,1,1,2\" basecode=\"A\" bidcount=\"5\" bidprices=\"66290,66270,66240,66230,66220\" bidsizes=\"1,1,1,1,2\" symbol=\"RJZ1\"/>"
			.getBytes();

	/**
	 * 
	 * 16:40:00.218 [# ddf-messages] DEBUG c.d.f.market.example.MarketManager -
	 * message :
	 * 3RJZ1,BAC55,66290K1,66270L1,66250M2,66240N1,66230O1,66370J2,66380
	 * I1,66400H1,66420G1,66430F2 16:40:00.218 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : BID 2
	 * Price > 66270 -2 Size > 1 16:40:00.218 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : BID 3
	 * Price > 66250 -2 Size > 2 16:40:00.218 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : BID 4
	 * Price > 66240 -2 Size > 1 16:40:00.219 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : BID 5
	 * Price > 66230 -2 Size > 1 16:40:00.219 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : ASK 2
	 * Price > 66380 -2 Size > 1 16:40:00.219 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : ASK 3
	 * Price > 66400 -2 Size > 1 16:40:00.219 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : ASK 4
	 * Price > 66420 -2 Size > 1 16:40:00.219 [# ddf-messages] ERROR
	 * c.d.f.c.market.provider.VarMarketDDF - result : DISCARD entry : ASK 5
	 * Price > 66430 -2 Size > 2 16
	 * */
	static final byte[] err2 = "3RJZ1,BAC55,66290K1,66270L1,66250M2,66240N1,66230O1,66370J2,66380I1,66400H1,66420G1,66430F2"
			.getBytes();

	/**
	 * 
	 * <QUOTE ask="115825" asksize="13" basecode="A" bid="115800" bidsize="11"
	 * ddfexchange="M" exchange="GBLX" lastupdate="20110929215152" mode="R"
	 * name="E-Mini S&amp;P 500" pointvalue="50.0" symbol="ESZ1"
	 * tickincrement="25">
	 * 
	 * <SESSION day="T" high="115950" id="combined" last="115850" low="115625"
	 * open="115725" previous="115625" session="G" timestamp="20110929165152"
	 * tradesize="1" tradetime="20110929162959" volume="8630"/>
	 * 
	 * <SESSION day="S" high="117075" id="previous" last="115625" low="113350"
	 * open="114825" previous="114875" session=" " timestamp="20110929160835"
	 * tradesize="1" tradetime="20110929151459" volume="2970593"/>
	 * 
	 * </QUOTE>
	 * 
	 */

}
