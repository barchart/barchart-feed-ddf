/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.historical.provider;

import java.io.EOFException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.ddf.historical.api.DDF_ResultEmptyException;
import com.barchart.util.anno.NotThreadSafe;

@NotThreadSafe
final class ZipReader {

	private static final Logger log = LoggerFactory.getLogger(ZipReader.class);

	enum ZipType {
		DEFLATE("deflate"), //
		GZIP("gzip"), //
		NONE(""), //
		;
		final String code;

		private ZipType(final String code) {
			this.code = code;
		}

		static final ZipType fromEncoding(final String encoding) {
			if (DEFLATE.code.equalsIgnoreCase(encoding)) {
				return DEFLATE;
			}
			if (GZIP.code.equalsIgnoreCase(encoding)) {
				return GZIP;
			}
			return NONE;
		}
	}

	private final ZipType type;

	private final InputStream rawStream;

	private final InputStream zipStream;

	static final ZipType TYPE = ZipType.GZIP;

	static final ZipReader fromURL(final String stringURL) throws Exception {

		final URL url = new URL(stringURL);

		HttpURLConnection.setFollowRedirects(false);

		final HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();

		connection.setRequestMethod("GET");

		connection.setRequestProperty("Accept-Encoding", TYPE.code);

		connection.connect();

		final String encoding = connection.getContentEncoding();

		final ZipType type = ZipType.fromEncoding(encoding);

		final InputStream stream = connection.getInputStream();

		return new ZipReader(type, stream);

	}

	ZipReader(final ZipType type, final InputStream stream) {
		try {
			this.type = type;
			this.rawStream = stream;
			switch (type) {
			case DEFLATE:
				this.zipStream = new InflaterInputStream(stream);
				break;
			case GZIP:
				this.zipStream = new GZIPInputStream(stream);
				break;
			case NONE:
			default:
				this.zipStream = stream;
				break;
			}
		} catch (EOFException e) {
			throw new DDF_ResultEmptyException(e.getMessage());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		// log.debug("zip class : {}", zipStream.getClass().getName());
	}

	final static int LINE_LIMIT = 8 * 1024;

	private final char[] array = new char[LINE_LIMIT];

	final static int CR = 10;
	final static int LF = 13;
	final static int EOS = -1;

	/** treats each CR or LF as line separator */
	final String readLine() throws Exception {
		int index = 0;
		while (true) {
			final int alpha;
			try {
				alpha = zipStream.read();
			} catch (EOFException e) {
				break;
			}
			if (alpha == CR || alpha == LF) {
				if (index == 0) {
					return "";
				} else {
					break;
				}
			}
			if (alpha == EOS) {
				break;
			}
			array[index++] = (char) alpha;
		}
		if (index == 0) {
			return null;
		} else {
			return new String(array, 0, index);
		}
	}

	final void close() {
		try {
			zipStream.close();
			rawStream.close();
		} catch (Exception e) {
			log.error("should not happen", e);
		}
	}

}
