/**
 *
 */
package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

class GreaterComparator implements RangeComparator {

	private Value value;

	public GreaterComparator(Value value) {
		this.value = value;
	}

	public boolean isInRange(Value v) {
		return v.greater(value).getAsBoolean();
	}

}