package com.barchart.feed.ddf.instrument.provider.ext;

import java.util.List;

import org.openfeed.proto.inst.InstrumentDefinition;

import com.barchart.feed.api.model.meta.Exchange;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.util.Identifier;
import com.barchart.market.provider.api.model.meta.InstrumentState;
import com.barchart.util.value.api.Fraction;
import com.barchart.util.value.api.Price;
import com.barchart.util.value.api.Schedule;
import com.barchart.util.value.api.Size;
import com.barchart.util.value.api.TimeInterval;

public class InstrumentStateFactory {

	public static InstrumentState newInstrument(final String symbol) {
		
		InstrumentDefinition.Builder builder = InstrumentDefinition.newBuilder();
		
		builder.setSymbol(symbol);
		
		return new InstrumentStateImpl(builder.buildPartial());
		
	}
	
	// Make one that creates a stub with other fields
	public static InstrumentState newInstrumentFromStub(final Instrument inst) {
		
		InstrumentDefinition.Builder builder = InstrumentDefinition.newBuilder();
		
		builder.setSymbol(inst.symbol());
		
		return new InstrumentStateImpl(builder.buildPartial());
		
	}
	
	// TODO
	private static class InstrumentStateImpl implements InstrumentState {

		private volatile InstrumentDefinition def = null;
		private volatile State state = State.EMPTY;
		
		InstrumentStateImpl(final InstrumentDefinition def) {
			this.def = def;
		}
		
		@Override 
		public State state() {
			return state;
		}
		
		@Override
		public String marketGUID() {
			return null;
		}

		@Override
		public SecurityType securityType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public BookLiquidityType liquidityType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public BookStructureType bookStructure() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Size maxBookDepth() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String instrumentDataVendor() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String symbol() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String description() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String CFICode() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Exchange exchange() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String exchangeCode() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Price tickSize() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Price pointValue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Fraction displayFraction() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TimeInterval lifetime() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Schedule marketHours() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long timeZoneOffset() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String timeZoneName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Identifier> componentLegs() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int compareTo(Instrument o) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isNull() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Identifier id() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MetaType type() {
			return MetaType.INSTRUMENT;
		}

		@Override
		public void process(InstrumentDefinition value) {
			def = value;
		}

		@Override
		public InstrumentDefinition definition() {
			return def;
		}

		@Override
		public void reset() {
			// TODO
			
		}

	}
	
}
