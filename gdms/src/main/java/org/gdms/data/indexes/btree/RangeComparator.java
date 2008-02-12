/**
 *
 */
package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

interface RangeComparator {
	boolean isInRange(Value v);
}