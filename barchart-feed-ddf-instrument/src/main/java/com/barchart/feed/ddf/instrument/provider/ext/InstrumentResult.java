package com.barchart.feed.ddf.instrument.provider.ext;

import com.barchart.feed.inst.meta.Result;
import com.barchart.market.provider.api.model.meta.InstrumentState;

public class InstrumentResult implements Result<InstrumentState> {

	private final InstrumentState result;
	private final String expression;
	private final Throwable t;
	
	public InstrumentResult(final InstrumentState result, final String expression) {
		this.result = result;
		this.expression = expression;
		t = null;
	}
	
	public InstrumentResult(final String expression, final Throwable t) {
		result = InstrumentState.NULL;
		this.expression = expression;
		this.t = t;
	}
	
	@Override
	public InstrumentState result() {
		return result;
	}

	@Override
	public String expression() {
		return expression;
	}

	@Override
	public Throwable exception() {
		return t;
	}

}
