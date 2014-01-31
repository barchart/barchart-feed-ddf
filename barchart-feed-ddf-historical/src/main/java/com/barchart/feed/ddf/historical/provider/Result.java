/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import java.util.ArrayList;
import java.util.List;

import com.barchart.feed.base.thread.Runner;
import com.barchart.feed.ddf.historical.api.DDF_Entry;
import com.barchart.feed.ddf.historical.api.DDF_Query;
import com.barchart.feed.ddf.historical.api.DDF_Result;
import com.barchart.feed.ddf.historical.api.DDF_ResultInterruptedException;
import com.barchart.feed.ddf.historical.api.DDF_ResultListener;
import com.barchart.feed.ddf.historical.enums.DDF_ResultStatus;

// TODO: Auto-generated Javadoc
class Result<E extends DDF_Entry> implements DDF_Result<E> {

	// ////////////////////////////

	protected String statusComment;
	protected DDF_ResultStatus status;
	
	protected String urlQuery;

	//

	private final List<E> entryList = new ArrayList<E>();

	private final E entryReference;

	private final DDF_Query<E> query;

	//

	private int progressCount;

	private final DDF_ResultListener progressListener;

	// ////////////////////////////

	Result(final DDF_Query<E> query, final E entryReference,
			final DDF_ResultListener listener) {
		this.query = query;
		this.entryReference = entryReference;
		this.progressListener = listener;
		progressStart();
	}

	//

	private void progressStart() {
		progressCount = 0;
		if (progressListener == null) {
			return;
		}
		progressListener.onProgressEvent(progressCount);
	}

	private void progressUpdate(final String message) {
		// final Thread thread = Thread.currentThread();
		if (Thread.interrupted()) {
			throw new DDF_ResultInterruptedException(message);
		}
		if (progressListener == null) {
			return;
		}
		if (progressCount % DDF_ResultListener.PROGRESS_SIZE == 0) {
			progressListener.onProgressEvent(progressCount);
		}
		progressCount++;
	}

	private void progressFinish() {
		if (progressListener == null) {
			return;
		}
		progressListener.onProgressEvent(progressCount);
	}

	//

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.api.DDF_Result#getStatus()
	 */
	@Override
	public DDF_ResultStatus getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.api.DDF_Result#getStatusComment()
	 */
	@Override
	public String getStatusComment() {
		return statusComment;
	}

	//

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.api.DDF_Result#runLoop(com.barchart.util.thread.Runner, java.util.List)
	 */
	@Override
	public <R> void runLoop(final Runner<R, E> task, final List<R> list) {
		progressStart();
		final int lenght = entryList.size();
		for (int index = 0; index < lenght; index++) {
			final E entry = entryList.get(index);
			final R result = task.run(entry);
			progressUpdate("run loop interrupted");
			if (list == null || result == null) {
				continue;
			}
			list.add(result);
		}
		progressFinish();
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.api.DDF_Result#size()
	 */
	@Override
	public int size() {
		return entryList.size();
	}

	final void add(final int index, final E entry) {
		entryList.add(index, entry);
		progressUpdate("entry list add interrupted");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		progressStart();
		final StringBuilder text = new StringBuilder(1024);
		for (final E entry : entryList) {
			text.append(entry);
			text.append("\n");
			progressUpdate("export to string interrupted");
		}
		progressFinish();
		return text.toString();
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.api.DDF_Result#get(int)
	 */
	@Override
	public E get(final int index) {
		return entryList.get(index);
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.api.DDF_Result#asCSV()
	 */
	@Override
	public String asCSV() {

		final StringBuilder text = new StringBuilder(1024);

		progressStart();

		text.append(entryReference.csvHeader());
		text.append("\n");

		for (final E entry : entryList) {
			text.append(entry.csvEntry());
			text.append("\n");
			progressUpdate("export as csv interrupted");
		}

		progressFinish();

		return text.toString();

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.historical.api.DDF_Result#getQuery()
	 */
	@Override
	public DDF_Query<E> getQuery() {
		return query; // This was cloned, it may need a defensive copy, unclear atm.
	}

	@Override
	public String getQueryURL() {
		
		return urlQuery;
	}

	//

}
