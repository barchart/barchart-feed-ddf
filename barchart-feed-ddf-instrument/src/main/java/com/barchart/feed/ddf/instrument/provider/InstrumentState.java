package com.barchart.feed.ddf.instrument.provider;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.barchart.feed.api.model.meta.Exchange;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.ChannelID;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.api.model.meta.id.VendorID;
import com.barchart.feed.api.model.meta.instrument.Calendar;
import com.barchart.feed.api.model.meta.instrument.PriceFormat;
import com.barchart.feed.api.model.meta.instrument.Schedule;
import com.barchart.feed.api.model.meta.instrument.SpreadLeg;
import com.barchart.feed.api.model.meta.instrument.SpreadType;
import com.barchart.util.value.api.Fraction;
import com.barchart.util.value.api.Price;
import com.barchart.util.value.api.Size;
import com.barchart.util.value.api.Time;
import com.barchart.util.value.api.TimeInterval;

public interface InstrumentState extends Instrument {

	enum LoadState {
		/** An invalid instrument definition */
		NULL,
		/** An empty instrument definition with an ID */
		EMPTY,
		/** A partially complete instrument definition */
		PARTIAL,
		/** A complete instrument definition */
		FULL
	}

	LoadState loadState();

	void process(Instrument value);

	void reset();

	InstrumentState NULL = new InstrumentState() {

		@Override
		public String marketGUID() {
			throw new UnsupportedOperationException();
		}

		@Override
		public SecurityType securityType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public BookLiquidityType liquidityType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public BookStructureType bookStructure() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Size maxBookDepth() {
			throw new UnsupportedOperationException();
		}

		@Override
		public VendorID vendor() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String symbol() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Map<VendorID, String> vendorSymbols() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String description() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String CFICode() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Exchange exchange() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String exchangeCode() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Price tickSize() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Price pointValue() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Price transactionPriceConversionFactor() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Fraction displayFraction() {
			throw new UnsupportedOperationException();
		}

		@Override
		public TimeInterval lifetime() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Schedule marketHours() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Month contractDeliveryMonth() {
			throw new UnsupportedOperationException();
		}

		@Override
		public long timeZoneOffset() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String timeZoneName() {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<InstrumentID> componentLegs() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int compareTo(final Instrument o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isNull() {
			return true;
		}

		@Override
		public InstrumentID id() {
			throw new UnsupportedOperationException();
		}

		@Override
		public MetaType type() {
			throw new UnsupportedOperationException();
		}

		@Override
		public State state() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void process(final Instrument value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void reset() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Time contractExpire() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String currencyCode() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String instrumentGroup() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ChannelID channel() {
			throw new UnsupportedOperationException();
		}

		@Override
		public DateTime created() {
			throw new UnsupportedOperationException();
		}

		@Override
		public DateTime updated() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Calendar calendar() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Schedule schedule() {
			throw new UnsupportedOperationException();
		}

		@Override
		public DateTimeZone timeZone() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PriceFormat priceFormat() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PriceFormat optionStrikePriceFormat() {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<InstrumentID> components() {
			throw new UnsupportedOperationException();
		}

		@Override
		public DateTime delivery() {
			throw new UnsupportedOperationException();
		}

		@Override
		public DateTime expiration() {
			throw new UnsupportedOperationException();
		}

		@Override
		public InstrumentID underlier() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Price strikePrice() {
			throw new UnsupportedOperationException();
		}

		@Override
		public OptionType optionType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public OptionStyle optionStyle() {
			throw new UnsupportedOperationException();
		}

		@Override
		public SpreadType spreadType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<SpreadLeg> spreadLegs() {
			throw new UnsupportedOperationException();
		}

		@Override
		public LoadState loadState() {
			throw new UnsupportedOperationException();
		}

	};

}
