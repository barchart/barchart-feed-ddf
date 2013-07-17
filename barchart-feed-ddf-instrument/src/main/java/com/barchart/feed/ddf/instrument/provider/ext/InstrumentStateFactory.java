package com.barchart.feed.ddf.instrument.provider.ext;

import org.openfeed.proto.inst.Decimal;
import org.openfeed.proto.inst.InstrumentDefinition;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.inst.participant.InstrumentState;
import com.barchart.feed.inst.provider2.InstrumentFactory;
import com.barchart.util.value.api.Fraction;
import com.barchart.util.value.api.Price;

public class InstrumentStateFactory {
	
	public static InstrumentState newInstrument(final String symbol) {
		
		InstrumentDefinition.Builder builder = InstrumentDefinition.newBuilder();
		
		builder.setSymbol(symbol);
		
		return InstrumentFactory.instrumentState(builder.buildPartial());
		
	}
	
	public static InstrumentState newInstrumentFromStub(final Instrument inst) {
		
		InstrumentDefinition.Builder builder = InstrumentDefinition.newBuilder();
		
		builder.setSymbol(inst.symbol());
		builder.setDescription(inst.description());
		
		builder.setRecordCreateTime(System.currentTimeMillis());
		builder.setRecordUpdateTime(System.currentTimeMillis());
		
		final Price tickSize = inst.tickSize();
		
		builder.setMinimumPriceIncrement(Decimal.newBuilder()
				.setMantissa(tickSize.mantissa())
				.setExponent(tickSize.exponent()));
		
		final Fraction frac = inst.displayFraction();
		
		builder.setDisplayBase((int) frac.base());
		builder.setDisplayExponent(frac.exponent());
		
		builder.setExchangeCode(inst.exchangeCode());
		
		return InstrumentFactory.instrumentState(builder.buildPartial());
		
	}
	
}
