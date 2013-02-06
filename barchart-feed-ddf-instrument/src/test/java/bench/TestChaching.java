package bench;

import com.barchart.feed.api.inst.Instrument;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;

public class TestChaching {
	
	
	
	public static void main(final String[] args) {
	
		long start = System.currentTimeMillis();
		
		Instrument inst = DDF_InstrumentProvider.find("ESH3");
		
		System.out.println(System.currentTimeMillis() - start);
		
	}

}
