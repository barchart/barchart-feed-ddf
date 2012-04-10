/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.util.enums;

import static com.barchart.feed.base.api.instrument.enums.MarketDisplay.Fraction.*;
import static com.barchart.util.ascii.ASCII.*;
import static java.lang.Math.*;

import com.barchart.feed.base.api.instrument.enums.MarketDisplay.Fraction;
import com.barchart.util.enums.EnumByteOrdinal;
import com.barchart.util.enums.EnumCodeByte;
import com.barchart.util.math.MathExtra;

// TODO: Auto-generated Javadoc
/** a.k.a base code */
public enum DDF_Fraction implements EnumCodeByte, EnumByteOrdinal {

	// binary range; exponent is power of 2;
	Q2(_0_, -10, BIN_N01), // 1/2 XXX: unit code invalid
	Q4(_1_, -11, BIN_N02), // 1/4 XXX: unit code invalid
	Q8(_2_, -1, BIN_N03), // 1/8
	Q16(_3_, -2, BIN_N04), // 1/16
	Q32(_4_, -3, BIN_N05), // 1/32
	Q64(_5_, -4, BIN_N06), // 1/64
	Q128(_6_, -5, BIN_N07), // 1/128
	Q256(_7_, -6, BIN_N08), // 1/256

	// decimal range; exponent is power of 10;
	Z0(_8_, 0, DEC_Z00), // 1
	N1(_9_, 1, DEC_N01), // 10
	N2(_A_, 2, DEC_N02), // 100
	N3(_B_, 3, DEC_N03), // 1K
	N4(_C_, 4, DEC_N04), // 10K
	N5(_D_, 5, DEC_N05), // 100K
	N6(_E_, 6, DEC_N06), // 1M
	N7(_F_, 7, DEC_N07), // 10M
	N8(_G_, 8, DEC_N08), // 100M
	N9(_H_, 9, DEC_N09), // 1B

	ZZ(STAR, 0, DEC_Z00), // 1

	UNKNOWN(QUEST, 0, DEC_Z00), //

	/* (non-Javadoc)
  * @see com.barchart.util.enums.EnumByteOrdinal#ord()
  */
 ;

	@Override
	public final byte ord() {
		return ord;
	}

	/* (non-Javadoc)
	 * @see com.barchart.util.enums.EnumCodeByte#code()
	 */
	@Override
	public final byte code() {
		return baseCode;
	}

	/** form #1 of ddf price exponent encoding. */
	public final byte baseCode;

	/** form #2 of ddf price exponent encoding. */
	public final int unitCode;

	/** byte sized ordinal of this enum. */
	public final byte ord;

	/** base-10 exponent and denominator, regardless of enum base. */
	public final int decimalExponent;
	
	/** The decimal denominator. */
	public final long decimalDenominator;

	/** base-2 or base-10 exponent and denominator, depending on enum base. */
	public final int nativeExponent;
	
	/** The native denominator. */
	public final long nativeDenominator;

	/** number of digits needed to display fraction in native form. */
	public final int spaces;

	/** "positive" difference between native and decimal spaces. */
	public final long spacesDenomPlus;

	/** "negative" difference between native and decimal spaces. */
	public final long spacesDenomMinus;

	/** base generic fraction api enum used by ddf fraction enum. */
	public final Fraction fraction;

	/** convenience flag to differentiate base-2 vs base-10 fractions. */
	public final boolean isBinary;

	private DDF_Fraction(final byte baseCode, final int unitCode,
			final Fraction fraction) {

		this.baseCode = baseCode;
		this.unitCode = unitCode;

		this.ord = (byte) ordinal();

		this.fraction = fraction;
		this.decimalExponent = fraction.decimalExponent;
		this.decimalDenominator = fraction.decimalDenominator;

		this.isBinary = fraction.base.isBinary();

		this.nativeExponent = fraction.exponent.value;
		this.nativeDenominator = fraction.denominator;

		this.spaces = fraction.places;

		this.spacesDenomPlus = (long) pow(10, spaces);
		this.spacesDenomMinus = (long) pow(10, -decimalExponent - spaces);

	}

	private final static DDF_Fraction[] ENUM_VALUES = values();

	/**
	 * Values unsafe.
	 *
	 * @return the dD f_ fraction[]
	 */
	@Deprecated
	public final static DDF_Fraction[] valuesUnsafe() {
		return ENUM_VALUES;
	}

	static {
		// validate use of byte ord
		MathExtra.castIntToByte(ENUM_VALUES.length);
	}

	// TODO optimize: replace with 2 tableswitch blocks;
	// http://java.sun.com/docs/books/jvms/second_edition/html/Compiling.doc.html#14942
	/**
	 * From base code.
	 *
	 * @param baseCode the base code
	 * @return the dD f_ fraction
	 */
	public final static DDF_Fraction fromBaseCode(final byte baseCode) {
		for (final DDF_Fraction known : ENUM_VALUES) {
			if (known.baseCode == baseCode) {
				return known;
			}
		}
		return UNKNOWN;
	}

	// TODO optimize: replace with 2 tableswitch blocks;
	// http://java.sun.com/docs/books/jvms/second_edition/html/Compiling.doc.html#14942
	/**
	 * From unit code.
	 *
	 * @param unitCode the unit code
	 * @return the dD f_ fraction
	 */
	public final static DDF_Fraction fromUnitCode(final int unitCode) {
		for (final DDF_Fraction known : ENUM_VALUES) {
			if (known.unitCode == unitCode) {
				return known;
			}
		}
		return UNKNOWN;
	}

	// TODO optimize; replace with 1 tableswitch block
	/**
	 * From fraction.
	 *
	 * @param fraction the fraction
	 * @return the dD f_ fraction
	 */
	public final static DDF_Fraction fromFraction(final Fraction fraction) {
		for (final DDF_Fraction known : ENUM_VALUES) {
			if (known.fraction == fraction) {
				return known;
			}
		}
		return UNKNOWN;
	}

	/**
	 * From ord.
	 *
	 * @param ord the ord
	 * @return the dD f_ fraction
	 */
	public final static DDF_Fraction fromOrd(final byte ord) {
		return ENUM_VALUES[ord];
	}

	/**
	 * Checks if is known.
	 *
	 * @return true, if is known
	 */
	public final boolean isKnown() {
		return this != UNKNOWN;
	}

}
