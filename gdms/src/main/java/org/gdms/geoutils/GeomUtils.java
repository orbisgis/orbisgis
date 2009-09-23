package org.gdms.geoutils;

import java.util.Iterator;
import java.util.List;



import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;

public class GeomUtils {

	private static final double EPSILON = 1E-6;
	public static boolean vertexInserted = false;

	public static Geometry insertPoint(Geometry geometry, Coordinate target) {

		LineSegment segment = segmentInRange(geometry, target);
		if (segment != null) {
			GeometryEditor ge = new GeometryEditor();
			Geometry newGeometry = ge.insertVertex(geometry, segment.p0,
					segment.p1, target);

			return newGeometry;
		}

		return null;

	}

	private static LineSegment segmentInRange(Geometry geometry,
			Coordinate target) {
		// It's possible that the geometry may have no segments in range; for
		// example, if it
		// is empty, or if only has points in range. [Jon Aquino]
		LineSegment closest = null;
		List coordArrays = CoordinateArrays.toCoordinateArrays(geometry, false);
		for (Iterator i = coordArrays.iterator(); i.hasNext();) {
			Coordinate[] coordinates = (Coordinate[]) i.next();
			for (int j = 1; j < coordinates.length; j++) { // 1
				LineSegment candidate = new LineSegment(coordinates[j - 1],
						coordinates[j]);
				if (candidate.distance(target) > EPSILON) {
					continue;
				}
				if ((closest == null)
						|| (candidate.distance(target) < closest
								.distance(target))) {
					closest = candidate;
				}
			}
		}
		return closest;
	}
}
