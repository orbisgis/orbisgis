/**
 *
 */
package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

class LessEqualComparator extends AbstractLessComparator implements RangeComparator {

	public LessEqualComparator(Value value) {
		super(value);
	}

	public boolean isInRange(Value v) {
		return v.lessEqual(value).getAsBoolean();
	}

	public int[] getAffectedChildren(int childIndexForValue, int valueCount) {
		return new int[] { 0, childIndexForValue};
	}

}