package org.urbsat.kmeans;

import java.util.Comparator;

public class DataPointComparator implements Comparator<DataPoint> {
	public int compare(final DataPoint dp1, final DataPoint dp2) {
		final int dimension = dp1.getDimension();
		for (int i = 0; i < dimension; i++) {
			final double delta = dp1.getComponents()[i]
					- dp2.getComponents()[i];
			if (0 < delta) {
				return 1;
			} else if (0 > delta) {
				return -1;
			}
		}
		return 0;
	}
}