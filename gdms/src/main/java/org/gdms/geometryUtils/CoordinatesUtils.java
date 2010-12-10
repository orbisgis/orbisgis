package org.gdms.geometryUtils;

import java.util.Collection;
import java.util.Iterator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.util.Assert;

public class CoordinatesUtils {

	private static final double EPSILON = 1E-6;
	public static boolean vertexInserted = false;

	/**
	 * Convert a xyz geometry to xy.
	 * 
	 * @param geom
	 * @param value
	 * @return
	 */
	public static Geometry force_2D(Geometry geom) {

		// return new Geometry2DTransformer().transform(geom);
		geom.apply(new CoordinateSequenceFilter() {
			boolean done = false;

			public boolean isGeometryChanged() {
				return true;
			}

			public boolean isDone() {
				return done;
			}

			public void filter(CoordinateSequence seq, int i) {
				seq.setOrdinate(i, 2, Double.NaN);

				if (i == seq.size()) {
					done = true;
				}
			}

		});

		return geom;

	}

	/**
	 * Convert a xy geometry to xyz. If the z does not exits a z equals to zero
	 * is added.
	 * 
	 * @param geom
	 * @param value
	 * @return
	 */
	public static Geometry force_3D(Geometry geom) {

		geom.apply(new CoordinateSequenceFilter() {
			boolean done = false;

			public boolean isGeometryChanged() {
				return true;
			}

			public boolean isDone() {
				return done;
			}

			public void filter(CoordinateSequence seq, int i) {
				Coordinate coord = seq.getCoordinate(i);
				double x = coord.x;
				double y = coord.y;
				double z = coord.z;
				seq.setOrdinate(i, 0, x);
				seq.setOrdinate(i, 1, y);
				if (Double.isNaN(z)) {
					seq.setOrdinate(i, 2, 0);
				}
				if (i == seq.size()) {
					done = true;
				}
			}

		});

		return geom;

	}

	/**
	 * Update all z ordinate by a new value
	 * 
	 * @param geom
	 * @param value
	 * @return
	 */
	public static Geometry force_3D(Geometry geom, final double value) {

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
	 * Update all z ordinate by a new value for the first and the last
	 * coordinates.
	 * 
	 * @param geom
	 * @param value
	 * @return
	 */
	public static Geometry force_3DStartEnd(Geometry geom, final double startZ,
			final double endZ) {

		final double D = geom.getLength();

		final double Z = endZ - startZ;

		final Coordinate coordEnd = geom.getCoordinates()[geom.getCoordinates().length - 1];

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
				if (i == 0) {
					seq.setOrdinate(i, 0, x);
					seq.setOrdinate(i, 1, y);
					seq.setOrdinate(i, 2, startZ);
				}

				else if (i == seq.size() - 1) {
					seq.setOrdinate(i, 0, x);
					seq.setOrdinate(i, 1, y);
					seq.setOrdinate(i, 2, endZ);
				}

				else {

					double d = seq.getCoordinate(i).distance(coordEnd);

					double factor = d / D;

					seq.setOrdinate(i, 0, x);
					seq.setOrdinate(i, 1, y);
					seq.setOrdinate(i, 2, startZ + (factor * Z));
				}

				if (i == seq.size()) {
					done = true;
				}
			}

		});

		return geom;

	}

	public static Coordinate interpolate(Coordinate p1, Coordinate p2,
			Coordinate coordinate) {

		final double D = p1.distance(p2);

		final double Z = p2.z - p1.z;

		double d = coordinate.distance(p1);

		double factor = d / D;

		return new Coordinate(coordinate.x, coordinate.z, (p1.z + (factor * Z)));
	}

	/**
	 * Reverse a linestring according to z value. The z first point must be
	 * greater than the z end point
	 * 
	 * @param lineString
	 * @return
	 */
	public static LineString zReverse(LineString lineString) {

		double startZ = lineString.getStartPoint().getCoordinate().z;
		double endZ = lineString.getEndPoint().getCoordinate().z;
		if (Double.isNaN(startZ) || Double.isNaN(endZ)) {

		} else {
			if (startZ < endZ) {
				lineString = (LineString) lineString.reverse();
			}
		}

		return lineString;
	}

	/**
	 * @param coordinates
	 *            not empty
	 */
	public static Coordinate closest(Collection coordinates, Coordinate p) {
		Assert.isTrue(!coordinates.isEmpty());

		Coordinate closest = (Coordinate) coordinates.iterator().next();

		for (Iterator i = coordinates.iterator(); i.hasNext();) {
			Coordinate candidate = (Coordinate) i.next();

			if (p.distance(candidate) < p.distance(closest)) {
				closest = candidate;
			}
		}

		return closest;
	}

}
