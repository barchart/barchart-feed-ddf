/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.resolver.provider;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.feed.base.api.instrument.enums.InstrumentField;
import com.barchart.feed.ddf.instrument.api.DDF_Instrument;
import com.barchart.feed.ddf.instrument.enums.DDF_InstrumentField;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentDo;
import com.barchart.feed.ddf.instrument.provider.DDF_InstrumentProvider;
import com.barchart.util.enums.DictEnum;
import com.barchart.util.enums.ParaEnumBase;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.api.SizeValue;
import com.barchart.util.values.api.TextValue;
import com.barchart.util.values.api.TimeValue;
import com.barchart.util.values.api.Value;
import com.barchart.util.values.provider.ValueBuilder;

class CodecHelper {

	private static Logger log = LoggerFactory.getLogger(CodecHelper.class);

	//

	static String SEPARATOR_ALPHA = "|";

	static String SEPARATOR_REGEX = "\\" + SEPARATOR_ALPHA;

	//

	/** lucene doc uuid */
	static final String FIELD_INST_ID = "@INST_ID";

	/** lucene doc index */
	static final String FIELD_INST_BODY = "@INST_BODY";

	//

	static final InstrumentField<?>[] BASE = InstrumentField.values();

	static final DDF_InstrumentField<?>[] EXTRA = DDF_InstrumentField.values();

	//

	static <T extends Enum<T>> T enumFrom(final Class<T> klaz, final String name) {
		try {
			return Enum.valueOf(klaz, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static String encode(final PriceValue price) {
		return price.mantissa() + SEPARATOR_ALPHA + price.exponent();
	}

	static String encode(final SizeValue size) {
		return Long.toString(size.asLong());
	}

	static String encode(final TimeValue time) {
		return Long.toString(time.asMillisUTC());
	}

	static String encode(final Enum<?> num) {
		return num.name();
	}

	static String encode(final ParaEnumBase<?, ?> num) {
		return num.guid();
	}

	static String encode(final ParaEnumBase<?, ?> field, final Object value) {

		if (field.value() instanceof TextValue) {
			return value.toString();
		}

		if (field.value() instanceof PriceValue) {
			return encode((PriceValue) value);
		}

		if (field.value() instanceof SizeValue) {
			return encode((SizeValue) value);
		}

		if (field.value() instanceof TimeValue) {
			return encode((TimeValue) value);
		}

		if (field.value() instanceof Enum) {
			return encode((Enum<?>) value);
		}

		if (field.value() instanceof ParaEnumBase) {
			return encode((ParaEnumBase<?, ?>) value);
		}

		throw new RuntimeException("wrong field : " + field);

	}

	@SuppressWarnings("unchecked")
	static Object decode(final ParaEnumBase<?, ?> field, final String value) {

		if (field.value() instanceof TextValue) {
			return ValueBuilder.newText(value);
		}

		if (field.value() instanceof PriceValue) {
			return decodePrice(value);
		}

		if (field.value() instanceof SizeValue) {
			return decodeSize(value);
		}

		if (field.value() instanceof TimeValue) {
			return decodeTime(value);
		}

		if (field.value() instanceof Enum) {
			@SuppressWarnings("rawtypes")
			final Class klaz = field.value().getClass();
			final Enum<?> enuma = enumFrom(klaz, value);
			return enuma;
		}

		if (field.value() instanceof ParaEnumBase) {
			@SuppressWarnings("rawtypes")
			final Class klaz = field.value().getClass();
			final DictEnum<?>[] array = ParaEnumBase.valuesFor(klaz);
			for (final DictEnum<?> dict : array) {
				if (field.name().equals(dict.name())) {
					return dict;
				}
			}
			return null;
		}

		throw new RuntimeException("wrong field : " + field);

	}

	static PriceValue decodePrice(final String value) {
		final String[] array = value.split(SEPARATOR_REGEX);
		final long mantissa = Long.parseLong(array[0]);
		final int exponent = Integer.parseInt(array[1]);
		return ValueBuilder.newPrice(mantissa, exponent);
	}

	static SizeValue decodeSize(final String value) {
		final long size = Long.parseLong(value);
		return ValueBuilder.newSize(size);
	}

	static TimeValue decodeTime(final String value) {
		final long time = Long.parseLong(value);
		return ValueBuilder.newTime(time);
	}

	static String removeLeading(final String value, final char alpha) {
		char[] chars = value.toCharArray();
		int index = 0;
		for (; index < value.length(); index++) {
			if (chars[index] != alpha) {
				break;
			}
		}
		return (index == 0) ? value : value.substring(index);
	}

	static String removeTrailing(final String value, final char alpha) {
		final char[] chars = value.toCharArray();
		int length, index;
		length = value.length();
		index = length - 1;
		for (; index >= 0; index--) {
			if (chars[index] != alpha) {
				break;
			}
		}
		return (index == length - 1) ? value : value.substring(0, index + 1);
	}

	static boolean hasWildcard(final String term) {
		return term.contains("*") || term.contains("?");
	}

	static Term getBodyTerm(final String term) {
		return new Term(FIELD_INST_BODY, term);
	}

	static void AND(final BooleanQuery query1, final Query query2) {
		query1.add(query2, BooleanClause.Occur.MUST);
	}

	static void OR(final BooleanQuery query1, final Query query2) {
		query1.add(query2, BooleanClause.Occur.SHOULD);
	}

	static void NOT(final BooleanQuery query1, final Query query2) {
		query1.add(query2, BooleanClause.Occur.MUST_NOT);
	}

	static Query buildQuerySimple(final String source) {

		final String[] termArray = source.split("\\s+");

		final BooleanQuery query = new BooleanQuery();

		for (final String termText : termArray) {

			final Term term = getBodyTerm(termText);

			if (hasWildcard(termText)) {
				AND(query, new WildcardQuery(term));
			} else {
				AND(query, new PrefixQuery(term));
			}

		}

		return query;

	}

	static boolean isValid(String text) {
		if (text == null || text.length() == 0) {
			return false;
		}
		return true;
	}

	/** must be globally unique */
	static Term getKeyTerm(final DDF_Instrument instrument) {

		final String name = CodecHelper.FIELD_INST_ID;
		final String value = instrument.get(InstrumentField.ID).toString();

		final Term term = new Term(name, value);

		return term;

	}

	/** convert instrument into lucene document */
	static Document instrumentEncode(final DDF_Instrument instrument) {

		final Document doc = new Document();

		{

			final Term term = getKeyTerm(instrument);

			final String name = term.field();
			final String value = term.text();

			/** store; do not index */
			final Field keyField = new Field(name, value, Field.Store.YES,
					Field.Index.NOT_ANALYZED);

			doc.add(keyField);

		}

		{

			final String name = CodecHelper.FIELD_INST_BODY;
			final String value = instrument.fullText();

			/** index; do not store */
			final Field bodyField = new Field(name, value, Field.Store.NO,
					Field.Index.ANALYZED);

			doc.add(bodyField);

		}

		for (final InstrumentField<?> field : CodecHelper.BASE) {

			final String name = field.name();
			final String value = encode(field, instrument.get(field));

			/** store; do not index */
			final Field baseField = new Field(name, value, Field.Store.YES,
					Field.Index.NO);

			doc.add(baseField);

		}

		for (final DDF_InstrumentField<?> field : CodecHelper.EXTRA) {

			final String name = field.name();
			final String value = encode(field, instrument.get(field));

			/** store; do not index */
			final Field extraField = new Field(name, value, Field.Store.YES,
					Field.Index.NO);

			doc.add(extraField);

		}

		return doc;

	}

	/** convert lucene document into instrument */
	@SuppressWarnings("unchecked")
	static <V extends Value<V>> DDF_Instrument instrumentDecode(
			final Document doc) {

		final DDF_InstrumentDo instrument = DDF_InstrumentProvider
				.newInstrumentDDF();

		for (final InstrumentField<?> field : CodecHelper.BASE) {

			final String name = field.name();
			final String value = doc.get(name);

			if (!isValid(value)) {
				continue;
			}

			instrument
					.set((InstrumentField<V>) field, (V) decode(field, value));

		}

		for (final DDF_InstrumentField<?> field : CodecHelper.EXTRA) {

			final String name = field.name();
			final String value = doc.get(name);

			if (!isValid(value)) {
				continue;
			}

			instrument.set((DDF_InstrumentField<V>) field,
					(V) decode(field, value));

		}

		return instrument;

	}

	/** re index instrument in lucene store */
	static void update(final IndexWriter writer, final DDF_Instrument entry)
			throws Exception {

		final Term key = getKeyTerm(entry);

		final Document doc = instrumentEncode(entry);

		writer.updateDocument(key, doc);

	}

	static boolean isPresent(final IndexSearcher searcher,
			final DDF_Instrument instrument) throws Exception {

		final Term term = getKeyTerm(instrument);

		final Query query = new TermQuery(term);

		int hits = searcher.search(query, 1).totalHits;

		return hits > 0;

	}

	static boolean isPresent(final IndexSearcher searcher, final String symbol)
			throws Exception {

		final String name = CodecHelper.FIELD_INST_ID;
		final String value = symbol;

		final Term term = new Term(name, value);

		final Query query = new TermQuery(term);

		int hits = searcher.search(query, 1).totalHits;

		return hits > 0;

	}

}
