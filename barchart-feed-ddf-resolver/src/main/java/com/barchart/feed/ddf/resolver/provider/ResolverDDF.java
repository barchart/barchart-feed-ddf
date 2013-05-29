/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.resolver.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.api.consumer.data.Instrument;
import com.barchart.feed.ddf.resolver.api.DDF_Resolver;
import com.barchart.util.thread.ExecutorCallable;
import com.barchart.util.values.api.TextValue;

// TODO: Auto-generated Javadoc
class ResolverDDF extends ResolverState implements DDF_Resolver {

	static final double RAM_BUFFER_SIZE_MB = 48.0;

	private static Logger log = LoggerFactory.getLogger(ResolverDDF.class);

	//

	private final ExecutorCallable executor;
	private final String folder;
	private final Analyzer analyzer;
	private final int limit;

	//

	private Directory directory;

	//

	ResolverDDF(final ExecutorCallable executor, final String folder,
			final int limit) {

		this.executor = executor;
		this.folder = folder;
		this.limit = limit;
		this.analyzer = new StandardAnalyzer(ConstResolver.VERSION);

	}

	private synchronized Directory getDirectory() throws Exception {

		if (directory == null) {

			final File file = new File(folder);

			directory = new SimpleFSDirectory(file);

		}

		return directory;

	}

	private IndexWriter writer;

	private synchronized IndexWriter getWriter() throws Exception {

		if (writer == null) {

			final IndexWriterConfig config = new IndexWriterConfig(
					ConstResolver.VERSION, analyzer);

			// config.setRAMBufferSizeMB(RAM_BUFFER_SIZE_MB);

			writer = new IndexWriter(getDirectory(), config);

			writer.commit();

		}

		return writer;

	}

	private final Future<?> futureNone = new FutureNone<Void>();

	private Future<?> future;

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.resolver.api.DDF_Resolver#open(com.barchart.feed.ddf.resolver.api.DDF_Resolver.Mode)
	 */
	@Override
	public synchronized Future<?> open(final Mode mode) {

		if (isOpen()) {
			log.error("aready open");
			return futureNone;
		}

		try {
			getDirectory();
			getWriter();
			getSearcher();
		} catch (final Exception e) {
			log.error("can not open index", e);
			return futureNone;
		}

		//

		Status status;
		try {
			status = getStatus();
		} catch (final Exception e) {
			status = new Status(0, false);
		}

		final boolean isPending = status.isPending();

		//

		switch (mode) {
		default:
		case DEFAULT:
			if (isPending) {
				break;
			} else {
				return futureNone;
			}
		case REINDEX:
			log.info("index pending : {}", isPending);
			break;
		case REBUILD:
			try {
				delete();
				log.info("index deleted");
			} catch (final Exception e) {
				log.error("can not delete index", e);
			}
			break;
		}

		super.open();

		final Callable<Void> task = new TaskUpdate(searcher, writer);

		future = executor.submit(task);

		return future;

	}

	private void delete() throws Exception {

		final IndexWriterConfig config = new IndexWriterConfig(
				ConstResolver.VERSION, analyzer);

		final IndexWriter writer = new IndexWriter(getDirectory(), config);

		writer.deleteAll();

		writer.close();

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.resolver.api.DDF_Resolver#searchLucene(java.lang.String)
	 */
	@Override
	public List<Instrument> searchLucene(final String phrase)
			throws Exception {

		final Query query = new QueryParser(ConstResolver.VERSION,
				CodecHelper.FIELD_INST_BODY, analyzer).parse(phrase);

		return searchInstrument(query);

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.resolver.api.DDF_Resolver#searchSimple(java.lang.String)
	 */
	@Override
	public List<Instrument> searchSimple(final String phrase)
			throws Exception {

		final Query query = CodecHelper.buildQuerySimple(phrase);

		return searchInstrument(query);

	}

	private IndexSearcher searcher;

	private synchronized IndexSearcher getSearcher() throws Exception {

		if (searcher == null) {
			searcher = new IndexSearcher(getDirectory(), true);
		}

		final IndexReader readerOld = searcher.getIndexReader();
		final IndexReader readerNew = readerOld.reopen(true);

		/** get new instance only when index is updated */
		if (readerNew != readerOld) {
			readerOld.close();
			searcher = new IndexSearcher(readerNew);
		}

		return searcher;

	}

	private List<Document> searchDocument(final Query query) throws Exception {

		final IndexSearcher searcher = getSearcher();

		final TopScoreDocCollector collector = TopScoreDocCollector.create(
				limit, true);

		searcher.search(query, collector);

		final ScoreDoc[] hits = collector.topDocs().scoreDocs;

		final int size = Math.min(hits.length, limit);

		log.debug("hits size : {}", size);

		final List<Document> list = new ArrayList<Document>(size);

		for (int k = 0; k < size; k++) {

			final int index = hits[k].doc;

			final Document doc = searcher.doc(index);

			list.add(doc);

		}

		return list;

	}

	private List<Instrument> searchInstrument(final Query query)
			throws Exception {

		final List<Document> listDocument = searchDocument(query);

		final List<Instrument> listInstrument = new ArrayList<Instrument>(
				listDocument.size());

		for (final Document doc : listDocument) {

			final Instrument instrument = CodecHelper
					.<TextValue> instrumentDecode(doc);

			listInstrument.add(instrument);

		}

		return listInstrument;

	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.resolver.provider.ResolverState#close()
	 */
	@Override
	public synchronized void close() {

		if (isClosed()) {
			log.error("already closed");
			return;
		}

		if (future != null) {
			future.cancel(true);
			future = null;
		}

		if (searcher != null) {
			try {
				searcher.close();
			} catch (final Exception e) {
				log.error("", e);
			}
			searcher = null;
		}

		if (writer != null) {
			try {
				writer.close();
			} catch (final Exception e) {
				log.error("", e);
			}
			writer = null;
		}

		if (directory != null) {
			try {
				directory.close();
			} catch (final Exception e) {
				log.error("", e);
			}
			directory = null;
		}

		super.close();

	}

	Status getStatus() throws Exception {

		final Query query = new TermQuery(Status.TERM);

		final List<Document> list = searchDocument(query);

		if (list.size() == 0) {
			return Status.EMPTY;
		}

		final Document doc = list.get(0);

		final Status status = Status.decode(doc);

		return status;

	}

	void setStatus(final Status status) throws Exception {

		final Document doc = Status.encode(status);

		final IndexWriterConfig config = new IndexWriterConfig(
				ConstResolver.VERSION, analyzer);

		final IndexWriter writer = new IndexWriter(getDirectory(), config);

		writer.updateDocument(Status.TERM, doc);

		writer.close();

	}

}
