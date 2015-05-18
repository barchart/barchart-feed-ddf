/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

import com.barchart.feed.ddf.settings.api.DDF_Server;

public class DDF_SocksProxy {

	private String proxyAddress;
	private int proxyPort = 1080;
	
	private String proxyUsername;
	
	private String proxyPassword;
	
	private DDF_Server feedServer;

	
	public DDF_SocksProxy(String proxyAddress, Integer proxyPort){
		setProxyAddress(proxyAddress);
		setProxyPort(proxyPort);
	}
	
	public DDF_SocksProxy(String proxyAddress, Integer proxyPort, String proxyUsername, String proxyPassword){
		setProxyAddress(proxyAddress);
		setProxyPort(proxyPort);
		
		setProxyUsername(proxyUsername);
		setProxyPassword(proxyPassword);
	}
	
	public String getProxyAddress() {
		return proxyAddress;
	}

	public void setProxyAddress(String proxyAddress) {
		this.proxyAddress = proxyAddress;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public DDF_Server getFeedServer() {
		return feedServer;
	}

	public void setFeedServer(DDF_Server feedServer) {
		this.feedServer = feedServer;
	}
	
}
