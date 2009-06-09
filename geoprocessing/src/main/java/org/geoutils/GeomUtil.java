package org.geoutils;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

public class GeomUtil {



	/**
	 * Update all z ordinate by a new value
	 *
	 * @param geom
	 * @param value
	 * @return
	 */
	public static Geometry updateZ(Geometry geom ,final double value){

		geom.apply(new CoordinateSequenceFilter() {
			boolean done = false;

			public boolean isGeometryChanged() {
				return true;
			}

			public boolean isDone() {
				return done;
			}

			public void filter(CoordinateSequence seq, int i) {
				double x = seq.getX(i);
				double y = seq.getY(i);
				seq.setOrdinate(i, 0, x);
				seq.setOrdinate(i, 1, y);
				seq.setOrdinate(i, 2, value);
				if (i == seq.size()) {
					done = true;
				}
			}

		});

		return geom;

	}

	/**
	 * Reverse a linestring according to z value.
	 * The z first point must be greater than the z end point
	 * @param lineString
	 * @return
	 */
	public static LineString zReverse(LineString lineString){

		double startZ = lineString.getStartPoint().getCoordinate().z;
		double endZ = lineString.getEndPoint().getCoordinate().z;
		if (Double.isNaN(startZ) || Double.isNaN(endZ)){

		}
		else {
			if (startZ < endZ){
				lineString = (LineString) lineString.reverse();
			}
		}

		return lineString;
	}
}
