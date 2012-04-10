/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.resolver.provider;

import com.barchart.feed.ddf.resolver.api.DDF_Resolver;
import com.barchart.util.thread.ExecutorCallable;

/**  */
public final class DDF_ResolverProvider {

	private DDF_ResolverProvider() {
	}

	/**
	 * make new resolver
	 * 
	 * @param executor
	 *            - external executor
	 * @param folder
	 *            - location of index database
	 * @param limit
	 *            - maximum number of returned search results
	 * 
	 */
	public static DDF_Resolver newInstance(final ExecutorCallable executor,
			final String folder, final int limit) {

		return new ResolverDDF(executor, folder, limit);

	}

}
