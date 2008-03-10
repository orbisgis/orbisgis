/**
 *
 */
package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

class LessComparator extends AbstractLessComparator implements RangeComparator {

	public LessComparator(Value value) {
		super(value);
	}

	public boolean isInRange(Value v) {
		return v.less(value).getAsBoolean();
	}

}