/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.datalink.provider.util;

public abstract class RunnerDDF implements Runnable {

	private volatile Thread thread;

	protected abstract void runCore();

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public final void run() {

		thread = Thread.currentThread();

		runCore();

	}

	public void interrupt() {
		if (thread == null) {
			return;
		}
		thread.interrupt();
		thread = null;
	}

}
