package com.barchart.feed.test.stream;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Session;
import com.barchart.feed.api.model.data.SessionData;

class MarketSnapshot {

	private static final DateFormat DF = new SimpleDateFormat("MM-dd HH:mm:ss");

	public static void printReport(final Marketplace marketplace, final String... symbols) {
		printReport(marketplace, System.out, symbols);
	}

	public static void printReport(final Marketplace marketplace, final PrintStream stream, final String... symbols) {

		stream.println(String.format("---------------------------------- %28s ----------------------------------",
				new Date().toString()));

		stream.println(" Symbol   | Time           |     Last  |       Source |  Previous |   Settle | Settled | Previous ");
		stream.println("--------------------------------------------------------------------------------------------------");

		Arrays.sort(symbols);

		for (final String s : symbols) {

			final Market m = marketplace.snapshot(s);

			if (m == null || m.isNull())
				continue;

			final SessionData current = m.sessionSet().session(Session.Type.DEFAULT_CURRENT);
			final SessionData previous = m.sessionSet().session(Session.Type.DEFAULT_PREVIOUS);

			stream.println(String.format(
					" %-8s | %s | %8.3f%1s | %12s | %9.3f | %8.3f | %7b | %8.3f ",
					m.instrument().symbol(),
					DF.format(new Date(current.updated().millisecond())),
					m.lastPrice().price().asDouble(),
					m.lastPrice().source().flag(),
					m.lastPrice().source().name(),
					previous.close().asDouble(),
					current.settle().asDouble(),
					current.isSettled().value(),
					previous.settle().asDouble()));

		}

		stream.println("");

	}

}