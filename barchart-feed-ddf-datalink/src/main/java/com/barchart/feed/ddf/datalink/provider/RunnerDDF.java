/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider;

abstract class RunnerDDF implements Runnable {

	// windows 7 problems with newCachedThreadPool() 
	// http://javahowto.blogspot.com/2012/10/java-applicatioin-process-hangs-on.html
	
	private volatile Thread thread;

	protected abstract void runCore();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public final void run() {

		thread = Thread.currentThread();

		runCore();

	}

	void interrupt() {
		if (thread == null) {
			return;
		}
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {
		}
		thread = null;
	}

	public Thread getThread() {
		return thread;
	}

}
