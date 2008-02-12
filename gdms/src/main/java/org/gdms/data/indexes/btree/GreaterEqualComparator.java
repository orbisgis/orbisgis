/**
 * 
 */
package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

class GreaterEqualComparator implements RangeComparator {

	private Value value;

	public GreaterEqualComparator(Value value) {
		this.value = value;
	}

	public boolean isInRange(Value v) {
		return v.greaterEqual(value).getAsBoolean();
	}

}