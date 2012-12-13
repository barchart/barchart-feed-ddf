/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

public class SocksProxyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1471983496750874369L;

	 public SocksProxyException(){
		 
     }
	 
	 public SocksProxyException(String message){
		 super(message);
     }
	 
	 public SocksProxyException(String message, Exception innerException){
		 super(message, innerException);
	 }
	 
	 
}
