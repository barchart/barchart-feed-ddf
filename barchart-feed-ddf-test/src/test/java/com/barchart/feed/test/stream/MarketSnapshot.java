package com.barchart.feed.test.stream;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
					DF.format(current.updated().isNull() ? new Date(0) : new Date(current.updated().millisecond())),
					m.lastPrice().isNull() || m.lastPrice().price().isNull() ? 0 : m.lastPrice().price().asDouble(),
					m.lastPrice().isNull() ? "" : m.lastPrice().source().flag(),
					m.lastPrice().isNull() ? "" : m.lastPrice().source().name(),
					previous.close().isNull() ? 0 : previous.close().asDouble(),
					current.settle().isNull() ? 0 : current.settle().asDouble(),
					current.isSettled().isNull() ? false : current.isSettled().value(),
					previous.settle().isNull() ? 0 : previous.settle().asDouble()));

		}

		stream.println("");

	}
	
	public static String printReportLine(final long time, final Marketplace marketplace, final String... symbols) {
		
		/* Sort symbols to ensure consistent output */
		Arrays.sort(symbols);
		
		final StringBuilder sb = new StringBuilder();
		
		sb.append(time + " ");
		
		for(final String s : symbols) {
			
			final Market m = marketplace.snapshot(s);
			
			if (m == null || m.isNull())
				continue;
			
			final SessionData current = m.sessionSet().session(Session.Type.DEFAULT_CURRENT);
			final SessionData previous = m.sessionSet().session(Session.Type.DEFAULT_PREVIOUS);

			sb.append(String.format(
					" %-8s | %s | %8.3f%1s | %12s | %9.3f | %8.3f | %7b | %8.3f ",
					m.instrument().symbol(),
					DF.format(current.updated().isNull() ? new Date(0) : new Date(current.updated().millisecond())),
					m.lastPrice().isNull() || m.lastPrice().price().isNull() ? 0 : m.lastPrice().price().asDouble(),
					m.lastPrice().isNull() ? "" : m.lastPrice().source().flag(),
					m.lastPrice().isNull() ? "" : m.lastPrice().source().name(),
					previous.close().isNull() ? 0 : previous.close().asDouble(),
					current.settle().isNull() ? 0 : current.settle().asDouble(),
					current.isSettled().isNull() ? false : current.isSettled().value(),
					previous.settle().isNull() ? 0 : previous.settle().asDouble()));
			
			sb.append(" | ");
			
		}
		
		return sb.toString();
		
	}
	
	public class Report {
		
		private final Map<String, String> markets = new HashMap<String, String>();
		
		public Report(final Marketplace marketplace, final String... symbols) {
			
			for (final String s : symbols) {

				final Market m = marketplace.snapshot(s);

				if (m == null || m.isNull())
					continue;

				final SessionData current = m.sessionSet().session(Session.Type.DEFAULT_CURRENT);
				final SessionData previous = m.sessionSet().session(Session.Type.DEFAULT_PREVIOUS);

				markets.put(s, String.format(
						" %-8s | %s | %8.3f%1s | %12s | %9.3f | %8.3f | %7b | %8.3f ",
						m.instrument().symbol(),
						DF.format(current.updated().isNull() ? new Date(0) : new Date(current.updated().millisecond())),
						m.lastPrice().isNull() || m.lastPrice().price().isNull() ? 0 : m.lastPrice().price().asDouble(),
						m.lastPrice().isNull() ? "" : m.lastPrice().source().flag(),
						m.lastPrice().isNull() ? "" : m.lastPrice().source().name(),
						previous.close().isNull() ? 0 : previous.close().asDouble(),
						current.settle().isNull() ? 0 : current.settle().asDouble(),
						current.isSettled().isNull() ? false : current.isSettled().value(),
						previous.settle().isNull() ? 0 : previous.settle().asDouble()));

			}
			
		}
		
		@Override
		public String toString() {
			
			final StringBuilder sb = new StringBuilder();
			
			sb.append(String.format("---------------------------------- %28s ----------------------------------",
				new Date().toString()) + "\n");
			
			sb.append(" Symbol   | Time           |     Last  |       Source |  Previous |   Settle | Settled | Previous \n");
			sb.append("--------------------------------------------------------------------------------------------------\n");
			for(final Entry<String, String> e : markets.entrySet()) {
				sb.append(e.getValue() + "\n");
			}
			
			sb.append("\n");
			
			return sb.toString();
		}
		
	}

}