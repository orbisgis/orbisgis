/**
 *
 */
package org.gdms.data.indexes.rtree;

class ChildReferenceDistance {
	int childIndex;
	double distance;

	public ChildReferenceDistance(int childIndex, double distance) {
		super();
		this.childIndex = childIndex;
		this.distance = distance;
	}

	@Override
	public String toString() {
		return distance + " -> " + childIndex;
	}
}