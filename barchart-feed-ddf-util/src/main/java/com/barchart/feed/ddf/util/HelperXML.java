/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.util;

import static com.barchart.feed.ddf.util.HelperDDF.*;
import static com.barchart.util.ascii.ASCII.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;

import com.barchart.feed.ddf.util.enums.DDF_Fraction;
import com.barchart.util.value.api.Time;
import com.barchart.util.value.provider.FactoryProvider;
import com.barchart.util.values.api.PriceValue;
import com.barchart.util.values.provider.ValueBuilder;
import com.barchart.util.values.provider.ValueConst;

// TODO: Auto-generated Javadoc
/**
 * The Class HelperXML.
 */
public final class HelperXML {

	private static Logger log = LoggerFactory.getLogger(HelperXML.class);

	/** throw exceptions on mandatory xml fields, stops parsing. */
	public static final boolean XML_STOP = true;

	/** no exceptions on optional xml fields, return default instead. */
	public static final boolean XML_PASS = !XML_STOP;

	private HelperXML() {
	}

	private static final ThreadLocal<DocumentBuilder> XML_BUILDER = new ThreadLocal<DocumentBuilder>() {
		@Override
		protected DocumentBuilder initialValue() {
			try {

				final DocumentBuilder builder = DocumentBuilderFactory
						.newInstance().newDocumentBuilder();

				return builder;

			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	};

	private static final ThreadLocal<Transformer> XML_XFORMER = new ThreadLocal<Transformer>() {
		@Override
		protected Transformer initialValue() {
			try {

				final Transformer xformer = TransformerFactory.newInstance()
						.newTransformer();

				xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
						"yes");

				xformer.setOutputProperty(OutputKeys.INDENT, "no");

				return xformer;

			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	};

	/**
	 * Xml first child.
	 * 
	 * @param parent
	 *            the parent
	 * @param childName
	 *            the child name
	 * @param isThrow
	 *            the is throw
	 * @return the element
	 */
	public static final Element xmlFirstChild(final Element parent,
			final String childName, final boolean isThrow) {
		final NodeList nodeList = parent.getChildNodes();
		if (nodeList == null) {
			if (isThrow) {
				throw new IllegalArgumentException("nodeList == null");
			}
			return null;
		}
		final int size = nodeList.getLength();
		for (int k = 0; k < size; k++) {
			final Node child = nodeList.item(k);
			if (child.getNodeName().equalsIgnoreCase(childName)) {
				return (Element) child;
			}
		}
		if (isThrow) {
			throw new IllegalArgumentException("child node not found");
		}
		return null;
	}

	/**
	 * Xml document decode.
	 * 
	 * @param xmlURI
	 *            the xml uri
	 * @return the element
	 * @throws Exception
	 *             the exception
	 */
	public static final Element xmlDocumentDecode(final String xmlURI)
			throws Exception {
		return XML_BUILDER.get().parse(xmlURI).getDocumentElement();
	}

	/**
	 * Xml dodument decode.
	 * 
	 * @param array
	 *            the array
	 * @param start
	 *            the start
	 * @param finish
	 *            the finish
	 * @param isThrow
	 *            the is throw
	 * @return the element
	 */
	public static final Element xmlDocumentDecode(final byte[] array,
			final int start, final int finish, final boolean isThrow) {
		final InputStream stream = new ByteArrayInputStream(array, start,
				finish);
		try {
			final Document document = XML_BUILDER.get().parse(stream);
			return document.getDocumentElement();
		} catch (final Exception e) {
			// will return/throw below
		}
		if (isThrow) {
			throw new RuntimeException("can not decode : \n"
					+ new String(array));
		}
		return null;
	}

	/**
	 * Xml document encode.
	 * 
	 * @param root
	 *            the root
	 * @param isThrow
	 *            the is throw
	 * @return the byte[]
	 */
	public static final byte[] xmlDocumentEncode(final Element root,
			final boolean isThrow) {
		final Source source = new DOMSource(root);
		final StringWriter stream = new StringWriter();
		final Result result = new StreamResult(stream);
		try {
			XML_XFORMER.get().transform(source, result);
			final String string = stream.toString();
			if (ValueBuilder.isPureAscii(string)) {
				return string.getBytes(ASCII_CHARSET);
			}
		} catch (final Exception e) {
			// will return/throw below
		}
		if (isThrow) {
			throw new RuntimeException("can not encode");
		}
		return null;
	}

	/**
	 * Xml string decode.
	 * 
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the string
	 */
	public static final String xmlStringDecode(final Element tag,
			final String attribute, final boolean isThrow) {
		final String string = tag.getAttribute(attribute);
		if (string.length() > 0) {
			return string;
		}
		if (isThrow) {
			throw new IllegalArgumentException("attribute not valid : "
					+ attribute);
		}
		return "";
	}

	/**
	 * Xml string decode.
	 * 
	 * @param atr
	 *            the atr
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the string
	 */
	public static final String xmlStringDecode(final Attributes atr,
			final String attribute, final boolean isThrow) {
		final String string = atr.getValue(attribute);
		if (string != null && string.length() > 0) {
			return string;
		}
		if (isThrow) {
			throw new IllegalArgumentException("attribute not valid : "
					+ attribute);
		}
		return "";
	}
	
	/**
	 * Xml string encode.
	 * 
	 * @param string
	 *            the string
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 */
	public static final void xmlStringEncode(final String string,
			final Element tag, final String attribute) {
		tag.setAttribute(attribute, string);
	}

	/**
	 * Xml text encode.
	 * 
	 * @param text
	 *            the text
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 */
	public static final void xmlTextEncode(final StringBuilder text,
			final Element tag, final String attribute) {
		tag.setAttribute(attribute, text.toString());
	}

	/**
	 * Xml new document.
	 * 
	 * @param tagName
	 *            the tag name
	 * @return the element
	 */
	public static final Element xmlNewDocument(final String tagName) {
		final Document doc = XML_BUILDER.get().newDocument();
		final Element tag = doc.createElement(tagName);
		return tag;
	}

	/**
	 * Xml new element.
	 * 
	 * @param root
	 *            the root
	 * @param tagName
	 *            the tag name
	 * @return the element
	 */
	public static final Element xmlNewElement(final Element root,
			final String tagName) {
		final Document doc = root.getOwnerDocument();
		final Element tag = doc.createElement(tagName);
		return tag;
	}

	/**
	 * Xml check tag name.
	 * 
	 * @param tag
	 *            the tag
	 * @param name
	 *            the name
	 */
	public static final void xmlCheckTagName(final Element tag,
			final String name) {
		final String tagName = tag.getNodeName();
		if (!name.equalsIgnoreCase(tagName)) {
			throw new IllegalArgumentException("no match;" + " actual="
					+ tagName + " expected=" + name);
		}
	}

	/**
	 * Checks if is xml name match.
	 * 
	 * @param root
	 *            the root
	 * @param tag
	 *            the tag
	 * @return true, if is xml name match
	 */
	public static final boolean isXmlNameMatch(final Element root,
			final String tag) {
		final String name = root.getNodeName();
		return tag.equalsIgnoreCase(name);
	}

	/**
	 * Xml integer decode.
	 * 
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the int
	 */
	public static final int xmlIntegerDecode(final Element tag,
			final String attribute, final boolean isThrow) {
		final String string = tag.getAttribute(attribute);
		if (string.length() > 0) {
			try {
				return Integer.parseInt(string);
			} catch (final Exception e) {
				// will throw/return below
			}
		}
		if (isThrow) {
			throw new IllegalArgumentException("attribute not valid : "
					+ attribute);
		}
		return 0;
	}

	/**
	 * Xml integer encode.
	 * 
	 * @param value
	 *            the value
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 */
	public static final void xmlIntegerEncode(final int value,
			final Element tag, final String attribute) {
		final String string = Integer.toString(value);
		tag.setAttribute(attribute, string);
	}

	/**
	 * Xml price decode.
	 * 
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the price value
	 */
	public static final PriceValue xmlPriceDecode(final Element tag,
			final String attribute, final boolean isThrow) {
		try {
			final String string = tag.getAttribute(attribute);
			return priceDecode(string);
		} catch (final Exception e) {
			// will return/throw below
		}
		if (isThrow) {
			throw new IllegalArgumentException("attribute not valid : "
					+ attribute);
		}
		return ValueConst.NULL_PRICE;
	}

	/**
	 * Xml price decode.
	 * 
	 * @param ats
	 *            the ats
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the price value
	 */
	public static final PriceValue xmlPriceDecode(final Attributes ats,
			final String attribute, final boolean isThrow) {
		try {
			final String string = ats.getValue(attribute);
			return priceDecode(string);
		} catch (final Exception e) {
			// will return/throw below
		}
		if (isThrow) {
			throw new IllegalArgumentException("attribute not valid : "
					+ attribute);
		}
		return ValueConst.NULL_PRICE;
	}

	/**
	 * Xml price encode.
	 * 
	 * @param price
	 *            the price
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 */
	public static final void xmlPriceEncode(final PriceValue price,
			final Element tag, final String attribute) {
		final String string = priceEncode(price);
		tag.setAttribute(attribute, string);
	}

	/**
	 * Xml byte decode.
	 * 
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the byte
	 */
	public static final byte xmlByteDecode(final Element tag,
			final String attribute, final boolean isThrow) {
		final String string = tag.getAttribute(attribute);
		if (string.length() > 0 && string.length() == 1) {
			return (byte) string.charAt(0);
		}
		if (isThrow) {
			throw new IllegalArgumentException("attribute not valid : "
					+ attribute);
		}
		return NUL;
	}

	/**
	 * Xml byte decode.
	 * 
	 * @param atr
	 *            the atr
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the byte
	 */
	public static final byte xmlByteDecode(final Attributes atr,
			final String attribute, final boolean isThrow) {
		final String string = atr.getValue(attribute);
		if (string.length() > 0 && string.length() == 1) {
			return (byte) string.charAt(0);
		}
		if (isThrow) {
			throw new IllegalArgumentException("attribute not valid : "
					+ attribute);
		}
		return NUL;
	}

	// decimal array
	/**
	 * Xml decimal array decode.
	 * 
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 * @param marker
	 *            the marker
	 * @param frac
	 *            the frac
	 * @param isThrow
	 *            the is throw
	 * @return the long[]
	 */
	public static final long[] xmlDecimalArrayDecode(final Element tag,
			final String attribute, final byte marker, final DDF_Fraction frac,
			final boolean isThrow) {
		final long[] array = HelperXML.xmlLongArrayDecode(tag, attribute,
				marker, isThrow);
		final int size = array.length;
		if (frac.isBinary) {
			for (int k = 0; k < size; k++) {
				array[k] = fromBinaryToDecimal(array[k], frac);
			}
		}
		return array;
	}

	// long array
	/**
	 * Xml long array decode.
	 * 
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 * @param marker
	 *            the marker
	 * @param isThrow
	 *            the is throw
	 * @return the long[]
	 */
	public static final long[] xmlLongArrayDecode(final Element tag,
			final String attribute, final byte marker, final boolean isThrow) {
		final String string = tag.getAttribute(attribute);
		if (string.length() > 0) {
			try {
				final String[] source = string.split(HelperDDF
						.byteAsString(marker));
				final int size = source.length;
				final long[] target = new long[size];
				for (int k = 0; k < size; k++) {
					target[k] = longDecode(source[k]);
				}
				return target;
			} catch (final Exception e) {
				// will throw/return below
			}
		}
		if (isThrow) {
			throw new IllegalArgumentException("attribute not valid : "
					+ attribute);
		}
		return null;
	}

	/**
	 * Xml ascii decode.
	 * 
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the byte[]
	 */
	public static final byte[] xmlAsciiDecode(final Element tag,
			final String attribute, final boolean isThrow) {
		final String string = tag.getAttribute(attribute);
		if (string.length() > 0) {
			return string.getBytes(ASCII_CHARSET);
		}
		if (isThrow) {
			throw new IllegalArgumentException("attribute not valid : "
					+ attribute);
		}
		return null;
	}

	/**
	 * Xml ascii encode.
	 * 
	 * @param array
	 *            the array
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 */
	public static final void xmlAsciiEncode(final byte[] array,
			final Element tag, final String attribute) {
		if (array == null) {
			return;
		}
		tag.setAttribute(attribute, new String(array, ASCII_CHARSET));
	}

	/**
	 * Xml byte encode.
	 * 
	 * @param code
	 *            the code
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 */
	public static final void xmlByteEncode(final byte code, final Element tag,
			final String attribute) {
		final String string = byteAsString(code);
		tag.setAttribute(attribute, string);
	}

	/**
	 * Xml time decode.
	 * 
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the time value
	 */
	public static final Time xmlTimeDecode(final Element tag,
			final String attribute, final boolean isThrow) {

		final String timeValue = xmlStringDecode(tag, attribute, isThrow);

		if (timeValue == null || timeValue.length() == 0) {
			return com.barchart.util.value.impl.ValueConst.NULL_TIME;
		}

		try {
			final DateTime dateTime = new DateTime(timeValue);
			return FactoryProvider.instance().newTime(dateTime.getMillis(), "");
		} catch (final Exception e) {
			if (isThrow) {
				throw new IllegalArgumentException("attribute not valid : "
						+ attribute);
			} else {
				return com.barchart.util.value.impl.ValueConst.NULL_TIME;
			}
		}

	}

	/**
	 * Xml time decode.
	 * 
	 * @param atr
	 *            the atr
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the time value
	 */
	public static final Time xmlTimeDecode(final Attributes atr,
			final String attribute, final boolean isThrow) {

		final String timeValue = xmlStringDecode(atr, attribute, isThrow);

		if (timeValue == null || timeValue.length() == 0) {
			return com.barchart.util.value.impl.ValueConst.NULL_TIME;
		}

		try {
			final DateTime dateTime = new DateTime(timeValue);
			return FactoryProvider.instance().newTime(dateTime.getMillis(), "");
		} catch (final Exception e) {
			if (isThrow) {
				throw new IllegalArgumentException("attribute not valid : "
						+ attribute);
			} else {
				return com.barchart.util.value.impl.ValueConst.NULL_TIME;
			}
		}

	}

	/**
	 * Xml time decode.
	 * 
	 * @param zone
	 *            the zone
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the long
	 */
	public static final long xmlTimeDecode(final DateTimeZone zone,
			final Element tag, final String attribute, final boolean isThrow) {
		final long timeValue = xmlLongDecode(tag, attribute, isThrow);
		if (timeValue == DDF_EMPTY || timeValue == DDF_CLEAR) {
			return timeValue;
		}
		return timeDecode(timeValue, zone);
	}

	/**
	 * Xml time encode.
	 * 
	 * @param millisUTC
	 *            the millis utc
	 * @param zone
	 *            the zone
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 */
	public static final void xmlTimeEncode(final long millisUTC,
			final DateTimeZone zone, final Element tag, final String attribute) {
		if (millisUTC == DDF_EMPTY || millisUTC == DDF_CLEAR) {
			xmlLongEncode(millisUTC, tag, attribute);
			return;
		}
		final long timeValue = timeEncode(millisUTC, zone);
		xmlLongEncode(timeValue, tag, attribute);
	}

	/**
	 * Xml long decode.
	 * 
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the long
	 */
	public static final long xmlLongDecode(final Element tag,
			final String attribute, final boolean isThrow) {
		final String string = tag.getAttribute(attribute);
		if (string.length() > 0) {
			try {
				// will return DDF_CLEAR for STRING_DASH
				return longDecode(string);
			} catch (final Exception e) {
				// will throw/return below
			}
		}
		if (isThrow) {
			throw new IllegalArgumentException("attribute not valid : "
					+ attribute);
		}
		return DDF_EMPTY;
	}

	/**
	 * Xml long decode.
	 * 
	 * @param ats
	 *            the ats
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the long
	 */
	public static final long xmlLongDecode(final Attributes ats,
			final String attribute, final boolean isThrow) {
		final String string = ats.getValue(attribute);
		if (string.length() > 0) {
			try {
				// will return DDF_CLEAR for STRING_DASH
				return longDecode(string);
			} catch (final Exception e) {
				// will throw/return below
			}
		}
		if (isThrow) {
			throw new IllegalArgumentException("attribute not valid : "
					+ attribute);
		}
		return DDF_EMPTY;
	}

	/**
	 * Xml long encode.
	 * 
	 * @param value
	 *            the value
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 */
	public static final void xmlLongEncode(final long value, final Element tag,
			final String attribute) {
		if (value == DDF_EMPTY) {
			// skip attribute
			return;
		}
		if (value == DDF_CLEAR) {
			tag.setAttribute(attribute, STRING_DASH);
			return;
		}
		final String string = longEncode(value);
		tag.setAttribute(attribute, string);
	}

	/**
	 * Xml decimal decode.
	 * 
	 * @param frac
	 *            the frac
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the long
	 */
	public static final long xmlDecimalDecode(final DDF_Fraction frac,
			final Element tag, final String attribute, final boolean isThrow) {
		long mantissa = xmlLongDecode(tag, attribute, isThrow);
		mantissa = fromBinaryToDecimal(mantissa, frac);
		return mantissa;
	}

	/**
	 * Xml decimal decode.
	 * 
	 * @param frac
	 *            the frac
	 * @param ats
	 *            the ats
	 * @param attribute
	 *            the attribute
	 * @param isThrow
	 *            the is throw
	 * @return the long
	 */
	public static final long xmlDecimalDecode(final DDF_Fraction frac,
			final Attributes ats, final String attribute, final boolean isThrow) {
		long mantissa = xmlLongDecode(ats, attribute, isThrow);
		mantissa = fromBinaryToDecimal(mantissa, frac);
		return mantissa;
	}

	/**
	 * Xml decimal encode.
	 * 
	 * @param mantissa
	 *            the mantissa
	 * @param frac
	 *            the frac
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 */
	public static final void xmlDecimalEncode(/* local */long mantissa,
			final DDF_Fraction frac, final Element tag, final String attribute) {
		mantissa = fromDecimalToBinary(mantissa, frac);
		xmlLongEncode(mantissa, tag, attribute);
	}

	/**
	 * Xml integer array decode.
	 * 
	 * @param tag
	 *            the tag
	 * @param attribute
	 *            the attribute
	 * @param marker
	 *            the marker
	 * @param isThrow
	 *            the is throw
	 * @return the int[]
	 */
	public static final int[] xmlIntegerArrayDecode(final Element tag,
			final String attribute, final byte marker, final boolean isThrow) {
		final String string = tag.getAttribute(attribute);
		if (string.length() > 0) {
			try {
				final String[] source = string.split(HelperDDF
						.byteAsString(marker));
				final int size = source.length;
				final int[] target = new int[size];
				for (int k = 0; k < size; k++) {
					target[k] = Integer.parseInt(source[k]);
				}
				return target;
			} catch (final Exception e) {
				// will throw/return below
			}
		}
		if (isThrow) {
			throw new IllegalArgumentException("attribute not valid : "
					+ attribute);
		}
		return null;
	}

	/**
	 * Log.
	 * 
	 * @param attributes
	 *            the attributes
	 */
	public static void log(final Attributes attributes) {

		final int size = attributes.getLength();

		for (int index = 0; index < size; index++) {

			final String name = attributes.getLocalName(index);
			final String value = attributes.getValue(index);

			log.debug("attribute : {}={}", name, value);
		}

	}
}
