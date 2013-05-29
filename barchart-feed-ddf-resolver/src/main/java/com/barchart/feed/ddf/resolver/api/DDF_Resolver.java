/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.resolver.api;

import java.util.List;
import java.util.concurrent.Future;

import com.barchart.feed.api.consumer.data.Instrument;

/**
 * The Interface DDF_Resolver.
 */
public interface DDF_Resolver {

	enum Mode {

		/** start index update only if index is old */
		DEFAULT, //

		/** remove and create the index anew */
		REBUILD, //

		/** start index update now, even if not due */
		REINDEX, //

	}

	/**
	 * start index;
	 * 
	 * TODO @return readiness future.
	 * 
	 * @param mode
	 *            the mode
	 * @return the future
	 */
	public Future<?> open(Mode mode);

	/**
	 * TODO
	 * 
	 * stop index; release resources
	 */
	void close();

	/**
	 * LUCENE advanced search syntax;
	 * 
	 * http://lucene.apache.org/java/3_4_0/queryparsersyntax.html
	 */
	List<Instrument> searchLucene(String phrase) throws Exception;

	/**
	 * BARCHART simplified search syntax;
	 * 
	 * 0) use "space" as term separator;
	 * 
	 * 1) if "*" or "?" are present, treat term as WILDCARD;
	 * 
	 * 2) else use "term" as prefix; i.e. "term*"
	 * 
	 * 3) combine all terms with "AND";
	 * 
	 */
	List<Instrument> searchSimple(String phrase) throws Exception;

}
