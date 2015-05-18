package com.barchart.feed.ddf.datalink.api;

import java.util.concurrent.Future;

public interface FutureWriter {
	
	Future<Boolean> write(String message);

}
