package com.barchart.feed.test.replay;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.util.ASCII;
import com.barchart.feed.ddf.market.provider.DDF_Marketplace;
import com.barchart.feed.ddf.message.api.DDF_BaseMessage;
import com.barchart.feed.ddf.message.api.DDF_MarketBase;
import com.barchart.feed.ddf.message.provider.DDF_MessageService;
import com.barchart.feed.ddf.message.provider.DDF_SpreadParser;

/**
 * Replay a DDF message log in original sequence, with optional timing
 * adjustments.
 */
public class FeedReplay {

	private static final DateFormat DP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static class Builder {

		private final Set<String> symbols = new HashSet<String>();

		private URL source;
		private double speed = -1;
		private Date start = null;
		private Date end = null;

		private MessageListener listener = null;

		protected Builder() {
		}

		/**
		 * Replay from a URL.
		 */
		public Builder source(final URL source_) {
			source = source_;
			return this;
		}

		/**
		 * Replay from a local file.
		 */
		public Builder source(final File source_) {
			try {
				source = source_.toURI().toURL();
				return this;
			} catch (final MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		
		public Builder zone(final TimeZone zone) {
			DP.setTimeZone(zone);
			return this;
		}

		public Builder start(final Date start_) {
			start = start_;
			return this;
		}

		public Builder start(final String start_) {
			try {
				start = DP.parse(start_);
			} catch (final ParseException e) {
			}
			return this;
		}

		public Builder end(final Date end_) {
			end = end_;
			return this;
		}

		public Builder end(final String end_) {
			try {
				end = DP.parse(end_);
			} catch (final ParseException e) {
			}
			return this;
		}

		public Builder speed(final long speed_) {
			speed = speed_;
			return this;
		}

		public Builder symbols(final String... symbols_) {
			for (final String s : symbols) {
				symbols.add(s);
			}
			return this;
		}

		public Builder listener(final MessageListener listener_) {
			listener = listener_;
			return this;
		}

		public FeedReplay build(final DDF_Marketplace marketplace) {
			return new FeedReplay(marketplace, source, speed, start, end, symbols, listener);
		}

	}

	public static interface MessageListener {

		void messageProcessed(DDF_BaseMessage parsed, byte[] raw);

	}

	private static final Logger log = LoggerFactory.getLogger(FeedReplay.class);

	private final Set<String> symbols = new HashSet<String>();

	private final DDF_Marketplace marketplace;
	private final URL source;
	private final double speed;
	private final Date start;
	private final Date end;
	private final MessageListener listener;

	private Thread thread = null;

	protected FeedReplay(
			final DDF_Marketplace marketplace_, 
			final URL source_, 
			final double speed_, 
			final Date start_,
			final Date end_, 
			final Collection<String> symbols_, 
			final MessageListener listener_) {

		marketplace = marketplace_;
		source = source_;
		start = start_;
		end = end_;
		speed = speed_;
		listener = listener_;

	}

	public static Builder builder() {
		return new Builder();
	}

	public void run() {

		thread = Thread.currentThread();

		final long maxTime = System.currentTimeMillis() - 1000;

		long baseline = 0;
		long adjustment = 0;
		boolean inRange = (start == null && end == null);

		try {

			final DDFLogDeframer deframer;

			final URLConnection conn = source.openConnection();
			if (source.getPath().endsWith(".gz") || "application/x-gzip".equals(conn.getContentType())) {
				deframer = new DDFLogDeframer(new GZIPInputStream(conn.getInputStream()));
			} else {
				deframer = new DDFLogDeframer(source.openStream());
			}

			byte[] message;
			for (;;) {

				if (Thread.interrupted()) {
					log.debug("Thread interrupted, aborting replay");
					break;
				}

				message = deframer.next();
				if (message == null) {
					break;
				}

				if (message.length < 2) {
					log.warn("Short message, discarded");
					continue;
				}

				/* If message is a spread, rebuild headder */
				if (message[1] == ASCII._S_) {
					message = DDF_SpreadParser.stripSpreadPreamble(message);
				}

				final DDF_BaseMessage decoded;

				try {
					decoded = DDF_MessageService.decode(message);
				} catch (final Exception e) {
					log.warn("decode failed : " + new String(message));
					e.printStackTrace();
					log.debug(new String(Arrays.toString(message)));
					continue;
				}

				if (decoded instanceof DDF_MarketBase) {

					final DDF_MarketBase marketMessage =
							(DDF_MarketBase) decoded;

					final long time = marketMessage.getTime().asMillisUTC();

					if (time > maxTime) {

						// Auto-generated timestamp, skip if not previously
						// in range
						if (!inRange) {
							continue;
						}

					} else {

						// Before range start, skip
						if (start != null && time <= start.getTime()) {
							continue;
						}

						// After range end, completed
						if (end != null && time >= end.getTime()) {
							return;
						}

						inRange = true;

					}

					if (listener != null) {
						listener.messageProcessed(decoded, message);
					}

					if (speed > 0) {

						if (baseline == 0) {

							// Set baseline time difference
							baseline = time;
							adjustment = System.currentTimeMillis() - baseline;

						} else {

							final double delay = (time - baseline) * speed;
							final double elapsed =
									(System.currentTimeMillis() - (baseline + adjustment))
											* speed;

							if (delay > elapsed) {
								try {
									Thread.sleep((long) (delay - elapsed));
								} catch (final InterruptedException e) {
									e.printStackTrace();
								}
							}
						}

					}

					// log.debug(marketMessage.toString());

					if (marketplace != null) {
						if (symbols.size() == 0
								|| symbols.contains(marketMessage.getSymbol()
										.getName())) {
							marketplace.make(marketMessage);
						}
					}

				}

			}

		} catch (final Exception e) {
			throw new RuntimeException(e);
		} finally {
			thread = null;
		}

	}

	public void cancel() {
		if (thread != null) {
			thread.interrupt();
		}
	}

	public void await() throws InterruptedException {
		if (thread != null) {
			thread.join();
		}
	}

}
