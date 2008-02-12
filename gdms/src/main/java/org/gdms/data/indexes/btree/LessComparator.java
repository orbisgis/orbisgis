/**
 * 
 */
package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

class LessComparator implements RangeComparator {

	private Value value;

	public LessComparator(Value value) {
		this.value = value;
	}

	public boolean isInRange(Value v) {
		return v.less(value).getAsBoolean();
	}

}