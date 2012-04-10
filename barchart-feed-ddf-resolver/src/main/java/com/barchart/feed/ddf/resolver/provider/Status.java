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
import org.apache.lucene.index.Term;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;

/** resolver status document; singleton */
class Status {

	static final String FIELD_ID = "@STATUS_ID";
	static final String STATUS_VALUE = "a650560d-3724-4200-a6ff-c5911289e485";

	static final String PROP_RUN_TIME = "RUN_TIME";
	static final String PROP_SUCCESS = "SUCCESS";

	static final Term TERM = new Term(FIELD_ID, STATUS_VALUE);

	static final Status EMPTY = new Status(0, false);

	/** millis UTC */
	final long lastRunTime;

	/** last run status */
	final boolean wasRunSuccess;

	Status(long lastRunTime, boolean isSuccess) {

		this.lastRunTime = lastRunTime;
		this.wasRunSuccess = isSuccess;

	}

	static Status decode(final Document doc) {

		String timeText = doc.get(PROP_RUN_TIME);

		long lastRunTime = Long.parseLong(timeText);

		//

		String successText = doc.get(PROP_SUCCESS);

		boolean isSuccess = Boolean.parseBoolean(successText);

		//

		Status status = new Status(lastRunTime, isSuccess);

		return status;

	}

	static Document encode(final Status status) {

		final Document doc = new Document();

		{
			String name = TERM.field();
			String value = TERM.text();
			doc.add(new Field(name, value, Field.Store.YES,
					Field.Index.NOT_ANALYZED));
		}

		{
			String name = PROP_RUN_TIME;
			String value = Long.toString(status.lastRunTime).toString();
			doc.add(new Field(name, value, Field.Store.YES, Field.Index.NO));
		}

		{
			String name = PROP_SUCCESS;
			String value = Boolean.toString(status.wasRunSuccess);
			doc.add(new Field(name, value, Field.Store.YES, Field.Index.NO));
		}

		return doc;

	}

	boolean isPending() {

		if (!wasRunSuccess) {
			return true;
		}

		final DateTime previous = new DateTime(lastRunTime);

		final DateTime current = new DateTime(DateTimeZone.UTC);

		final Days days = Days.daysBetween(previous, current);

		final int count = days.getDays();

		if (count > 1) {
			return true;
		}

		return false;

	}

}
