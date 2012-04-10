/**
 * Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.ddf.symbol.provider;

import com.barchart.feed.ddf.symbol.api.DDF_Symbol;

// TODO: Auto-generated Javadoc
/*package*/abstract class Base implements DDF_Symbol {

	protected String name = "";

	protected String group = "";

	/**
	 * Instantiates a new base.
	 */
	public Base() {
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.symbol.api.DDF_Symbol#getGroup()
	 */
	public String getGroup() {
		return group;
	}

	/* (non-Javadoc)
	 * @see com.barchart.feed.ddf.symbol.api.DDF_Symbol#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object otherSymbol) {
		if (otherSymbol instanceof DDF_Symbol) {
			DDF_Symbol other = (DDF_Symbol) otherSymbol;
			return this.getName().equals(other.getName());
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
