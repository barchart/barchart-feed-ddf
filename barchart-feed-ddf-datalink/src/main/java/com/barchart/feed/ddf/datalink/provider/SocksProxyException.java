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
