package bench;

import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.feed.inst.api.Instrument;

public class TestChaching {
	
	
	
	public static void main(final String[] args) {
	
		long start = System.currentTimeMillis();
		
		Instrument inst = DDF_InstrumentProvider.find("ESH3");
		
		System.out.println(System.currentTimeMillis() - start);
		
		start = System.currentTimeMillis();
		
		inst = DDF_InstrumentProvider.findDDF("ESH3");
		
		System.out.println(System.currentTimeMillis() - start);
		
	}

}
