package org.gdms.data.indexes.btree;

import java.util.ArrayList;

import org.gdms.data.values.Value;

public abstract class AbstractLessComparator extends AbstractRangeComparator
		implements RangeComparator {

	public AbstractLessComparator(Value value) {
		super(value);
	}

	public int[] getAffectedChildren(int childIndexForValue,
			ArrayList<Value> values) {
		int max = Math.min(childIndexForValue + 1, values.size());
		while ((max < values.size()) && (values.get(max).isNull())) {
			max++;
		}
		return new int[] { 0, max };
	}

}
