/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.resolver.provider;

import java.util.HashMap;
import java.util.Map;

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

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.values.api.PriceValue;
import com.barchart.feed.base.values.api.SizeValue;
import com.barchart.feed.base.values.api.TextValue;
import com.barchart.feed.base.values.api.TimeValue;
import com.barchart.feed.base.values.api.Value;
import com.barchart.feed.base.values.provider.ValueBuilder;
import com.barchart.util.enums.DictEnum;
import com.barchart.util.enums.ParaEnumBase;

class CodecHelper {

	@SuppressWarnings("unused")
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

	static <T extends Enum<T>> T enumFrom(final Class<T> klaz, final String name) {
		try {
			return Enum.valueOf(klaz, name);
		} catch (final Exception e) {
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

//	static String encode(final Tag<?> field, final Object value) {
//
//		if (field.classType().equals(TextValue.class)) {
//			return value.toString();
//		}
//
//		if (field.classType().equals(PriceValue.class)) {
//			return encode((PriceValue) value);
//		}
//
//		if (field.classType().equals(SizeValue.class)) {
//			return encode((SizeValue) value);
//		}
//
//		if (field.classType().equals(TimeValue.class)) {
//			return encode((TimeValue) value);
//		}
//
//		if (field.isEnum()) {
//			return encode((Enum<?>) value);
//		}
//
//		if (field.classType().equals(ParaEnumBase.class)) {
//			return encode((ParaEnumBase<?, ?>) value);
//		}
//
//		throw new RuntimeException("wrong field : " + field);
//
//	}

//	@SuppressWarnings("unchecked")
//	static Object decode(final Tag<?> field, final String value) {
//
//		if (field.classType().equals(TextValue.class)) {
//			return ValueBuilder.newText(value);
//		}
//
//		if (field.classType().equals(PriceValue.class)) {
//			return decodePrice(value);
//		}
//
//		if (field.classType().equals(SizeValue.class)) {
//			return decodeSize(value);
//		}
//
//		if (field.classType().equals(TimeValue.class)) {
//			return decodeTime(value);
//		}
//
//		if (field.isEnum()) {
//			@SuppressWarnings("rawtypes")
//			final Class klaz = field.classType();
//			final Enum<?> enuma = enumFrom(klaz, value);
//			return enuma;
//		}
//
//		if (field.classType().equals(ParaEnumBase.class)) {
//			@SuppressWarnings("rawtypes")
//			final Class klaz = field.classType();
//			final DictEnum<?>[] array = ParaEnumBase.valuesFor(klaz);
//			for (final DictEnum<?> dict : array) {
//				if (field.name().equals(dict.name())) {
//					return dict;
//				}
//			}
//			return null;
//		}
//
//		throw new RuntimeException("wrong field : " + field);
//
//	}

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
		final char[] chars = value.toCharArray();
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

	static boolean isValid(final String text) {
		if (text == null || text.length() == 0) {
			return false;
		}
		return true;
	}

	/** must be globally unique */
	static Term getKeyTerm(final Instrument instrument) {

		final String name = CodecHelper.FIELD_INST_ID;
		final String value = instrument.marketGUID();

		final Term term = new Term(name, value);

		return term;

	}

	/** convert instrument into lucene document */
	static Document instrumentEncode(final Instrument instrument) {

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
			final String value = fullText(instrument);

			/** index; do not store */
			final Field bodyField = new Field(name, value, Field.Store.NO,
					Field.Index.ANALYZED);

			doc.add(bodyField);

		}

		// TODO If this ever need to be used, then some hardcoding
		// for iterating over the instrument fields will need to be written.
		
		// Currently, this isn't being used.
		
//		for (final Tag<?> field : CodecHelper.BASE) {
//
//			final String name = field.name();
//			final String value = encode(field, instrument.get(field));
//
//			/** store; do not index */
//			final Field baseField = new Field(name, value, Field.Store.YES,
//					Field.Index.NO);
//
//			doc.add(baseField);
//
//		}

		return doc;

	}
	
	//TODO
	static String fullText(final Instrument inst) {
		
		/*@Override
		public String fullText() {

			final StringBuilder text = new StringBuilder(256);

			text.append(get(DDF_SYMBOL_UNIVERSAL));
			text.append(SPACE);

			text.append(get(DDF_SYMBOL_HISTORICAL));
			text.append(SPACE);

			text.append(get(DDF_SYMBOL_REALTIME));
			text.append(SPACE);

			text.append(get(DESCRIPTION));
			text.append(SPACE);

			text.append(get(DDF_EXCHANGE));
			text.append(SPACE);

			// text.append(get(DDF_EXCHANGE).kind);
			// text.append(SPACE);

			text.append(get(DDF_EXCHANGE).description);
			text.append(SPACE);

			text.append(get(DDF_EXCH_DESC));
			text.append(SPACE);

			text.append(get(TYPE).getDescription());
			text.append(SPACE);

			addSpreadComponents(text);

			final TimeValue expire = get(DATE_FINISH);
			if (!expire.isNull()) {

				text.append(MarketDisplay.timeMonthFull(expire));
				text.append(SPACE);

				text.append(MarketDisplay.timeYearFull(expire));
				text.append(SPACE);

				text.append(MarketDisplay.timeYearShort(expire));
				text.append(SPACE);

			}

			return text.toString();

		}*/

		
		return null;
	}

	/** convert lucene document into instrument */
	static <V extends Value<V>> Instrument instrumentDecode(
			final Document doc) {
		
		// TODO Build protobuf from document then call factory
		return Instrument.NULL;

	}

	/** re index instrument in lucene store */
	static void update(final IndexWriter writer, final Instrument entry)
			throws Exception {

		final Term key = getKeyTerm(entry);

		final Document doc = instrumentEncode(entry);

		writer.updateDocument(key, doc);

	}

	static boolean isPresent(final IndexSearcher searcher,
			final Instrument instrument) throws Exception {

		final Term term = getKeyTerm(instrument);

		final Query query = new TermQuery(term);

		final int hits = searcher.search(query, 1).totalHits;

		return hits > 0;

	}

	static boolean isPresent(final IndexSearcher searcher, final String symbol)
			throws Exception {

		final String name = CodecHelper.FIELD_INST_ID;
		final String value = symbol;

		final Term term = new Term(name, value);

		final Query query = new TermQuery(term);

		final int hits = searcher.search(query, 1).totalHits;

		return hits > 0;

	}

}
