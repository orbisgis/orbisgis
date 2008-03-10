package org.gdms.data.indexes.btree;

import java.util.ArrayList;

import org.gdms.data.values.Value;

public abstract class AbstractRangeComparator implements RangeComparator {

	protected Value value;

	public AbstractRangeComparator(Value value) {
		this.value = value;
	}

	protected abstract int[] getAffectedChildren(int childIndexForValue,
			ArrayList<Value> values);

	public int[] getRange(BTreeInteriorNode treeInteriorNode) {
		int childIndex = treeInteriorNode.getChildForValue(value);
		return getAffectedChildren(childIndex, treeInteriorNode.values);
	}
}
