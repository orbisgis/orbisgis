package org.orbisgis.processing.tin;

import java.util.Comparator;

class VertexComparator implements Comparator<Vertex> {
	public int compare(Vertex o1, Vertex o2) {
		final int compareX = TriangleUtilities.floatingPointCompare(
				o1.coordinate.x, o2.coordinate.x);
		final int compareY = TriangleUtilities.floatingPointCompare(
				o1.coordinate.y, o2.coordinate.y);
		if (0 == compareX) {
			return compareY;
		} else {
			return compareX;
		}
	}
}