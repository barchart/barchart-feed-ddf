package com.barchart.feed.ddf.instrument.provider;

import com.barchart.feed.inst.api.InstrumentGUID;

class InstrumentGUIDDDF implements InstrumentGUID {

	private final long id;
	
	InstrumentGUIDDDF(final long id) {
		this.id = id;
	}
	
	@Override
	public long getGUID() {
		return id;
	}

	@Override 
	public boolean equals(final Object o) {
		
		if(o == null) {
			return false;
		}
		
		if(o instanceof InstrumentGUID) {
			if(id == ((InstrumentGUID)o).getGUID()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		
	}
	
	@Override
	public int compareTo(final InstrumentGUID thatGUID) {
		
		if(id > thatGUID.getGUID()) {
			return 1;
		} else if(id < thatGUID.getGUID()) {
			return -1;
		} else {
			return 0;
		}
	}

}
