/**
 * 
 */
package org.gdms.sql.strategies;

import java.util.ArrayList;
import java.util.Comparator;

import org.gdms.data.values.Value;

public class SortComparator implements Comparator<Integer> {
	private Value[][] columnCache;
	private ArrayList<Boolean> orders;

	public SortComparator(Value[][] columnCache, ArrayList<Boolean> orders) {
		this.columnCache = columnCache;
		this.orders = orders;
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Integer o1, Integer o2) {
		try {
			int i1 = ((Integer) o1).intValue();
			int i2 = ((Integer) o2).intValue();

			for (int i = 0; i < orders.size(); i++) {
				int orderDir = (orders.get(i)) ? 1 : -1;
				Value v1 = columnCache[i1][i];
				Value v2 = columnCache[i2][i];
				if (v1.isNull())
					return -1 * orderDir;
				if (v2.isNull())
					return 1 * orderDir;
				if (v1.less(v2).getAsBoolean()) {
					return -1 * orderDir;
				} else if (v2.less(v1).getAsBoolean()) {
					return 1 * orderDir;
				}
			}
			/*
			 * Because none of the orders criteria defined an order. The
			 * first value will be less than the second
			 */
			return -1;
		} catch (IncompatibleTypesException e) {
			throw new RuntimeException(e);
		}
	}
}