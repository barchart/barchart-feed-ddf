package com.barchart.feed.test.replay;

public class TestXFSettle {

	public static void main(final String[] args) {

		final FeedReplayer replayer =
				new FeedReplayer(
						FeedReplayer.class.getResource("/XF_20140113.txt"));

		replayer.run(null);

	}

}
