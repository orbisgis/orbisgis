/**
 * 
 */
package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

class TrueComparator implements RangeComparator {

	public boolean isInRange(Value v) {
		return true;
	}

}