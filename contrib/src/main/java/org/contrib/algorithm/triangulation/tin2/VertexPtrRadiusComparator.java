package org.contrib.algorithm.triangulation.tin2;

import java.util.Comparator;

import com.vividsolutions.jts.geom.Coordinate;

public class VertexPtrRadiusComparator extends VertexPtrSimpleComparator
		implements Comparator<VertexPtr> {
	private double barycentreX;
	private double barycentreY;

	public VertexPtrRadiusComparator(Coordinate barycentre) {
		barycentreX = barycentre.x;
		barycentreY = barycentre.y;
	}

	@Override
	public int compare(VertexPtr o1, VertexPtr o2) {
		Coordinate c1 = o1.getCoordinate();
		Coordinate c2 = o2.getCoordinate();
		double dx1 = c1.x - barycentreX;
		double dy1 = c1.y - barycentreY;
		double dx2 = c2.x - barycentreX;
		double dy2 = c2.y - barycentreY;
		double squRadius1 = dx1 * dx1 + dy1 * dy1;
		double squRadius2 = dx2 * dx2 + dy2 * dy2;

		if (squRadius1 > squRadius2) {
			return 1;
		}
		if (squRadius1 < squRadius2) {
			return -1;
		}

		return super.compare(o1, o2);
	}
}