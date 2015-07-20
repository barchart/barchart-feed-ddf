package com.barchart.feed.ddf.client.provider;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.List;

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.model.data.Book;
import com.barchart.feed.api.model.data.Session;
import com.barchart.feed.client.provider.BarchartMarketplace;

public class MemoryExample {
	
	public static void main(final String[] args) throws Exception {

		String username = "mysolo1";
		String password = "devtest";

		/** The memory mx bean. */

		// Create the client
		Marketplace feed = new BarchartMarketplace(username, password);

		// Connect and start event reactor
		feed.startup();
		
		List<String> symbolList = MemoryExample.getSymbols(); 
		System.out.println( "symbol count:" + symbolList.size() );

		displayMemorySummary(" before SUBSCRIPTIONS" );
		displayThreadSummary(" before SUBSCRIPTIONS" );

		MarketObserver<Book> bookObserver = new MarketObserver<Book>() 
		{
			public void onNext(final Book book) 
			{
				if ( book == null )
				{
					System.out.println("book is null"   );
					return;
				}

				//System.out.println( "sym:" + book.instrument().symbol() + " bid:" + book.top().bid().price().toString() +
				//		" ask:" + book.top().ask().price().toString() + " ts:" +  book.updated().millisecond() );

			}
		};


		Agent agent = feed.newAgent(Book.class, bookObserver);

		agent.include(symbolList.toArray(new String[0])).subscribe();
		
//		for ( String curSymbol : symbolList ) {
//			agent.include(curSymbol+".BZ").subscribe();
//		}
				
		displayMemorySummary(" after BOOK SUBSCRIPTIONS" );
		displayThreadSummary(" after BOOK SUBSCRIPTIONS" );
		
		MarketObserver<Session> sessionObserver = new MarketObserver<Session>() {
			
			public void onNext(Session session) {
				if ( session == null ) {
					System.out.println("session is null"   );
					return;
				}

				//System.out.println("session:" + session );
			}
		};


		Agent sessionAgent = feed.newAgent(Session.class, sessionObserver);
				
		for ( String curSymbol : symbolList ) {
			sessionAgent.include(curSymbol+".BZ").subscribe();
		}
				
		displayMemorySummary(" after SESSION SUBSCRIPTIONS" );
		displayThreadSummary(" after SESSION SUBSCRIPTIONS" );
		
		Thread.sleep( 10000 );
		
		displayMemorySummary(" after sleeping for 10 seconds" );
		displayThreadSummary(" after sleeping for 10 seconds" );
		
		// Block for processing here...
		Thread.sleep( 100000000 );

		// Cancel subscription
		agent.terminate();

		// Disconnect feed client
		feed.shutdown();
	}
	
	public static List<String> getSymbols() {		
		
		List<String> symbolList = Arrays.asList("CTRX", "CAT", "CBL", "CBOE", "CBG", "CBS", "CDW", "CE", "CELG", "CX", 
											"CNC", "CNP", "CTL", "CERN", "CF", "CHRW", "CYOU", "CRL", "SCHW", "CHTR",
											"CMCM", "LNG", "CHK", "CVX", "CBI", "CHS", "CIM", "CBPO", "DL", "CEA", "FXI", 
											"LFC", "HTHT", "CMGE", "CHL", "MCHI", "SNP", "ZNH", "CHA", "CHU", "XNY", "CYD", 
											"CMG", "CHH", "CB", "CI", "XEC", "CINF", "CNK", "CTAS", "CSCO", "CIT", "C", 
											"CTXS", "CIVI", "CLH", "CLF", "CME", "CMS", "CNA", "CISG", "CEO", "COH", "CIE", 
											"KO", "CCE", "KOF", "CTSH", "CFX", "CL", "CXP", "CMCSA", "CMA", "CMP", "CSC", 
											"CNW", "CAG", "CNOB", "COP", "CNX", "ED", "STZ", "XLY", "XLP", "VDC", "CPRT", 
											"IXUS", "IPAC", "IVV", "ITOT", "AGG", "CLGX", "GLW", "LQD", "CSGP", "COST", 
											"COTY", "CVD", "CVA", "COV", "CPI", "BCR", "CR", "CREE", "CCI", "OIL", "KWEB", 
											"KFYP", "CST", "CSX", "CTRP", "CMI", "CVS", "DHR", "DRI", "DVA", "DBA", "DBC", 
											"UUP", "DDR", "DECK", "DE", "DLPH", "DAL" );
		
		return( symbolList );
	}
	
	public static void displayMemorySummary(String description) {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		long BYTES_IN_MB = 1048576;

		long heapMemUsed = memoryMXBean.getHeapMemoryUsage().getUsed() / BYTES_IN_MB;
		System.out.println( description + "     heapMemoryUsed:" + heapMemUsed + " Mb" );
	}
	
	public static void displayThreadSummary(String description) {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		System.out.println( description + "  curLiveThreadCount:"+ threadMXBean.getThreadCount() + " totalStartedThreads:" + threadMXBean.getTotalStartedThreadCount() +
				" peakLiveThreadCount:" + threadMXBean.getPeakThreadCount() );
	}

}
