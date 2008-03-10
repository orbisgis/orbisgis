package org.gdms.data.indexes.btree;

import java.util.ArrayList;

import org.gdms.data.values.Value;

public abstract class AbstractGreaterComparator extends AbstractRangeComparator
		implements RangeComparator {

	public AbstractGreaterComparator(Value value) {
		super(value);
	}

	public int[] getAffectedChildren(int childIndexForValue,
			ArrayList<Value> values) {
		int min = Math.max(childIndexForValue, 0);
		while ((min > 0) && (values.get(min - 1).isNull())) {
			min--;
		}
		return new int[] { min, values.size() };
	}

}
