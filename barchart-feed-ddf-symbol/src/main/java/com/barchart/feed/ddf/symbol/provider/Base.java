/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.provider;

import com.barchart.feed.ddf.symbol.api.DDF_Symbol;

/*package*/abstract class Base implements DDF_Symbol {

	protected String name = "";

	protected String group = "";

	public Base() {
	}

	public String getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object otherSymbol) {
		if (otherSymbol instanceof DDF_Symbol) {
			DDF_Symbol other = (DDF_Symbol) otherSymbol;
			return this.getName().equals(other.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
