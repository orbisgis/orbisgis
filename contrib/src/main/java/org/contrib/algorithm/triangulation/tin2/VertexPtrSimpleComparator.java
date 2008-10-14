package org.contrib.algorithm.triangulation.tin2;

import java.util.Comparator;

import com.vividsolutions.jts.geom.Coordinate;

public class VertexPtrSimpleComparator implements Comparator<VertexPtr> {
	@Override
	public int compare(VertexPtr o1, VertexPtr o2) {
		Coordinate c1 = o1.getCoordinate();
		Coordinate c2 = o2.getCoordinate();

		if (c1.y > c2.y) {
			return 1;
		}
		if (c1.y < c2.y) {
			return -1;
		}
		if (c1.x < c2.x) {
			return -1;
		}
		if (c1.x > c2.x) {
			return 1;
		}
		return 0;
	}
}