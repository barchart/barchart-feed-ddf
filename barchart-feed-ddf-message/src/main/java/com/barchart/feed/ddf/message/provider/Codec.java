/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.message.provider;

import java.nio.ByteBuffer;

import org.w3c.dom.Element;

interface Codec {

	// ddf feed

	void encodeDDF(ByteBuffer buffer);

	void decodeDDF(ByteBuffer buffer);

	// xml feed

	void decodeXML(ByteBuffer buffer);

	void decodeXML(final Element root);

	void encodeXML(ByteBuffer buffer);

	void encodeXML(final Element root);

}
