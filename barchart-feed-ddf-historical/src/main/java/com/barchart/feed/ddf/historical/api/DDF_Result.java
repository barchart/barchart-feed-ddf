/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.api;

import java.util.List;

import com.barchart.feed.ddf.historical.enums.DDF_ResultStatus;
import com.barchart.util.thread.Runner;
import com.barchart.util.thread.RunnerLoop;

/**
 * The Interface DDF_Result.
 *
 * @param <E> the element type
 */
public interface DDF_Result<E extends DDF_Entry> extends RunnerLoop<E> {

	
	/** clone of original query */
	DDF_Query<E> getQuery();

	String getQueryURL();
	
	//

	DDF_ResultStatus getStatus();

	String getStatusComment();

	//

	/** size of result data set */
	int size();

	/** entry at given index */
	E get(int index) throws IndexOutOfBoundsException;

	//

	/** convert result data set into comma separated value lines */
	String asCSV() throws DDF_ResultInterruptedException;

	@Override
	String toString() throws DDF_ResultInterruptedException;

	//

	@Override
	<R> void runLoop(Runner<R, E> task, List<R> list)
			throws DDF_ResultInterruptedException;

}
