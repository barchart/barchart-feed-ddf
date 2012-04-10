/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.resovler.provider;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.barchart.feed.ddf.resolver.api.DDF_Resolver;
import com.barchart.feed.ddf.resolver.api.DDF_Resolver.Mode;
import com.barchart.feed.ddf.resolver.provider.DDF_ResolverProvider;
import com.barchart.util.thread.ExecutorCallable;

public class MainResolverIndex {

	public static void main(final String... args) throws Exception {

		final String folder = MainConst.FOLDER;
		final int limit = MainConst.LIMIT;

		final ExecutorService service = Executors.newFixedThreadPool(3);

		final ExecutorCallable caller = new ExecutorCallable() {
			@Override
			public <V> Future<V> submit(final Callable<V> task) {
				return service.submit(task);
			}
		};

		final DDF_Resolver resolver = DDF_ResolverProvider.newInstance(caller,
				folder, limit);

		resolver.open(Mode.REINDEX);

		Thread.sleep(10 * 1000);

		resolver.close();

		Thread.sleep(1000 * 1000);

	}

}
