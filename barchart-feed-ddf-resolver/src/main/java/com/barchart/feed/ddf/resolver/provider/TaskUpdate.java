/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.resolver.provider;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.barchart.feed.api.data.InstrumentEntity;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;

class TaskUpdate implements Callable<Void> {

	/** maximum number of symbols in request */
	static final int BATCH_SIZE = 500;

	//

	/** maximum length of concatenated symbols */
	static final int FETCH_SIZE = 4000;

	private static Logger log = LoggerFactory.getLogger(TaskUpdate.class);

	//

	private final BlockingQueue<String> symbolQueue = new LinkedBlockingDeque<String>();

	private final IndexSearcher searcher;

	private final IndexWriter writer;

	TaskUpdate(IndexSearcher searcher, IndexWriter writer) {
		this.searcher = searcher;
		this.writer = writer;
	}

	private void fetchQueue(final URL url) throws Exception {

		log.trace("fetch url : {}", url);

		final InputStream input = url.openStream();

		try {

			final BufferedInputStream stream = new BufferedInputStream(input);

			final SAXParserFactory factory = SAXParserFactory.newInstance();

			final SAXParser parser = factory.newSAXParser();

			final AtomicInteger count = new AtomicInteger(0);

			final DefaultHandler handler = new DefaultHandler() {

				@Override
				public void startElement(final String uri,
						final String localName, final String qName,
						final Attributes attributes) throws SAXException {

					if (ConstResolver.XML_RESULT.equals(qName)) {

						try {

							final String symbol = attributes
									.getValue(ConstResolver.XML_SYMBOL);

							symbolQueue.put(symbol);

						} catch (InterruptedException e) {
							throw new SAXException(new InterruptedException(
									"symbol queue"));
						}

						if (Thread.currentThread().isInterrupted()) {
							throw new SAXException(new InterruptedException(
									"sax handler"));
						}

						count.getAndIncrement();

						if (count.get() % ConstResolver.LOG_STEP == 0) {
							log.debug("fetch count : {}", count);
						}

					}

				}
			};

			parser.parse(stream, handler);

			log.debug("fetch count : {}", count);

		} finally {

			input.close();

		}

	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Void call() throws Exception {

		log.info("fetch start");

		final List<String> prefixList = ConstResolver.getSymbolPrefixList();

		for (final String prefix : prefixList) {

			log.info("fetch prefix : [{}]", prefix);

			try {

				final String uri = ConstResolver.getSymbolLookupURI(prefix);

				final URL url = new URL(uri);

				fetchQueue(url);

				storeQueue();

			} catch (InterruptedException e) {
				log.warn("fetch interrupted in : {}", e.getMessage());
				return null;
			} catch (Exception e) {
				log.error("prefix fetch failed", e);
				continue;
			}

		}

		final Status status = new Status(System.currentTimeMillis(), true);

		try {
			setStatus(status);
		} catch (Exception e) {
			log.error("failed to set status", e);
		}

		log.info("fetch finish");

		return null;

	}

	private void setStatus(final Status status) throws Exception {

		final Document doc = Status.encode(status);

		writer.updateDocument(Status.TERM, doc);

		writer.close();

		log.info("status updated");

	}

	private void storeQueue() throws Exception {

		log.info("store start");

		int batchSize = 0;
		int updateSize = 0;

		while (true) {

			final List<String> symbolBatch = getSymbolBatch();

			/** queue is done */
			if (symbolBatch.isEmpty()) {
				break;
			}

			batchSize += symbolBatch.size();

			final List<InstrumentEntity> instrumentList = DDF_InstrumentProvider
					.fetch(symbolBatch);

			for (final InstrumentEntity instrument : instrumentList) {

				checkInterrupt("intrument update");

				final boolean isPresent = CodecHelper.isPresent(searcher,
						instrument);

				if (isPresent) {
					// log.debug("isPresent : {}", instrument);
					continue;
				}

				CodecHelper.update(writer, instrument);

				updateSize++;

			}

		}

		writer.commit();

		log.info("store update : {} ;  batch : {}; ", updateSize, batchSize);

	}

	private List<String> getSymbolBatch() throws Exception {

		final List<String> symbolBatch = new LinkedList<String>();

		int symbolSize = 0;

		while (true) {

			checkInterrupt("symbol batch");

			final String symbol = symbolQueue.poll();

			if (symbol == null) {
				break;
			}

			final int length = symbol.length();

			/** ignore empty */
			if (length == 0) {
				continue;
			}

			/** symbol and comma */
			symbolSize += length + 1;

			symbolBatch.add(symbol);

			/** url size limit */
			if (symbolSize >= FETCH_SIZE) {
				break;
			}

			/** symbol count limit */
			if (symbolBatch.size() >= BATCH_SIZE) {
				break;
			}

		}

		return symbolBatch;

	}

	private void checkInterrupt(final String message)
			throws InterruptedException {
		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException(message);
		}
	}

}
