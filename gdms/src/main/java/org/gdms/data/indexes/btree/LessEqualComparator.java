/**
 * 
 */
package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

class LessEqualComparator implements RangeComparator {

	private Value value;

	public LessEqualComparator(Value value) {
		this.value = value;
	}

	public boolean isInRange(Value v) {
		return v.lessEqual(value).getAsBoolean();
	}

}