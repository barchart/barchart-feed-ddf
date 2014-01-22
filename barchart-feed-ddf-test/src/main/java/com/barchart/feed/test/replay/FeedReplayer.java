package com.barchart.feed.test.replay;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

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
public class FeedReplayer {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final URL source;
	private final double speed;

	private MessageListener listener = null;

	/**
	 * Replay from a file as fast as possible.
	 */
	public FeedReplayer(final File source_) {
		this(source_, -1);
	}

	/**
	 * Replay from a URL as fast as possible.
	 */
	public FeedReplayer(final URL source_) {
		this(source_, -1);
	}

	/**
	 * Replay from a file, using the given speed multiplier.
	 * 
	 * @param source_ The source file
	 * @param speed_ The multiplier to speed up the feed by, -1 for maximum
	 */
	public FeedReplayer(final File source_, final double speed_) {
		try {
			source = source_.toURI().toURL();
			speed = speed_;
		} catch (final MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Replay from a URL, using the given speed multiplier.
	 * 
	 * @param source_ The source URL
	 * @param speed_ The multiplier to speed up the feed by, -1 for maximum
	 */
	public FeedReplayer(final URL source_, final double speed_) {
		source = source_;
		speed = speed_;
	}

	public void setListener(MessageListener listener_) {
		listener = listener_;
	}

	public void run(final DDF_Marketplace marketplace, final String symbol) {
		run(marketplace, symbol, null, null);
	}

	public void run(final DDF_Marketplace marketplace, final String symbol,
			final Date start, final Date end) {

		final long maxTime = System.currentTimeMillis() - 1000;

		long baseline = 0;
		long adjustment = 0;
		boolean inRange = (start == null && end == null);

		try {

			final InputStream is = source.openStream();
			final DDFLogDeframer deframer = new DDFLogDeframer(is);

			byte[] message;
			for (;;) {

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
					//log.warn("decode failed : " + new String(message));
					//log.trace(new String(Arrays.toString(message)));
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

					notifyListener(message, decoded);

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
						if (symbol == null
								|| symbol.equals(marketMessage.getSymbol()
										.getName())) {
							marketplace.make(marketMessage);
						}
					}

				}

			}

		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void notifyListener(byte[] raw, DDF_BaseMessage parsed) {

		if (listener != null) {
			listener.messageProcessed(parsed, raw);
		}

	}

	public static interface MessageListener {

		void messageProcessed(DDF_BaseMessage parsed, byte[] raw);

	}

}
