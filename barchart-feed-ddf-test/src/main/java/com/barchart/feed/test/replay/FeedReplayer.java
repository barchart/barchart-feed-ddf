package com.barchart.feed.test.replay;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

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

	public void run(final DDF_Marketplace marketplace) {

		long baseline = 0;
		long adjustment = 0;

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
					log.warn("decode failed : " + new String(message));
					log.debug(new String(Arrays.toString(message)));
					continue;
				}

				if (decoded instanceof DDF_MarketBase) {

					final DDF_MarketBase marketMessage =
							(DDF_MarketBase) decoded;

					if (speed > 0) {

						final long time = marketMessage.getTime().asMillisUTC();

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

					//log.debug(marketMessage.toString());

					if (marketplace != null) {
						marketplace.make(marketMessage);
					}

				}

			}

		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

	}

}
