/**
 *
 */
package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

class GreaterEqualComparator extends AbstractGreaterComparator implements RangeComparator {

	public GreaterEqualComparator(Value value) {
		super(value);
	}

	public int[] getAffectedChildren(int childIndexForValue, int valueCount) {
		return new int[] { childIndexForValue, valueCount - 1 };
	}

	public boolean isInRange(Value v) {
		return v.greaterEqual(value).getAsBoolean();
	}

}