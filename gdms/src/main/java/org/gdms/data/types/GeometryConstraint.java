package org.gdms.data.types;

import org.gdms.data.values.Value;
import org.gdms.spatial.GeometryValue;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class GeometryConstraint extends AbstractConstraint {
	public static final int MIXED = 0;

	public static final int POINT_2D = 10;

	public static final int POINT_3D = 11;

	public static final int MULTI_POINT_2D = 12;

	public static final int MULTI_POINT_3D = 13;

	public static final int LINESTRING_2D = 14;

	public static final int LINESTRING_3D = 15;

	public static final int MULTI_LINESTRING_2D = 16;

	public static final int MULTI_LINESTRING_3D = 17;

	public static final int POLYGON_2D = 18;

	public static final int POLYGON_3D = 19;

	public static final int MULTI_POLYGON_2D = 20;

	public static final int MULTI_POLYGON_3D = 21;

	private int constraintValue;

	public GeometryConstraint() {
		this.constraintValue = MIXED;
	}

	public GeometryConstraint(final int constraintValue) {
		this.constraintValue = constraintValue;
	}

	public ConstraintNames getConstraintName() {
		return ConstraintNames.GEOMETRY;
	}

	public String getConstraintValue() {
		return Integer.toString(constraintValue);
	}

	public String check(Value value) {
		if (!(value instanceof GeometryValue)) {
			return "Value '" + value.toString() + "' must be a Geometry";
		} else {
			final Geometry geom = ((GeometryValue) value).getGeom();
			final int st = findBestGeometryType(geom);
			if (st != constraintValue) {
				return "Geometries types mismatch : " + Integer.toString(st)
						+ " not equal to " + Integer.toString(constraintValue);
			}
		}
		return null;
	}

	public int getGeometryType() {
		return constraintValue;
	}

	private static int findBestGeometryType(final Geometry geometry) {
		int type = MIXED;

		if (geometry instanceof Point) {
			type = is3D(geometry) ? POINT_3D : POINT_2D;
		} else if (geometry instanceof MultiPoint) {
			type = is3D(geometry) ? MULTI_POINT_3D : MULTI_POINT_2D;
		} else if (geometry instanceof Polygon) {
			type = is3D(geometry) ? POLYGON_3D : POLYGON_2D;
		} else if (geometry instanceof MultiPolygon) {
			type = is3D(geometry) ? MULTI_POLYGON_3D : MULTI_POLYGON_2D;
		} else if (geometry instanceof LineString) {
			type = is3D(geometry) ? LINESTRING_3D : LINESTRING_2D;
		} else if (geometry instanceof MultiLineString) {
			type = is3D(geometry) ? MULTI_LINESTRING_3D : MULTI_LINESTRING_2D;
		}

		return type;
	}

	// public ShapeType findBestGeometryType() {
	// ShapeType type = ShapeType.UNDEFINED;
	//
	// switch (constraintValue) {
	// case POINT_2D:
	// type = ShapeType.POINT;
	// break;
	// case POINT_3D:
	// type = ShapeType.POINTZ;
	// break;
	// case MULTI_POINT_2D:
	// type = ShapeType.MULTIPOINT;
	// break;
	// case MULTI_POINT_3D:
	// type = ShapeType.MULTIPOINTZ;
	// break;
	// case LINESTRING_2D:
	// type = ShapeType.ARC;
	// break;
	// case LINESTRING_3D:
	// type = ShapeType.ARCZ;
	// break;
	// case POLYGON_2D:
	// type = ShapeType.POLYGON;
	// break;
	// case POLYGON_3D:
	// type = ShapeType.POLYGONZ;
	// break;
	// // TODO : what about MULTI_{LINESTRING,POLYGON}_{2,3}D ?
	// }
	// return type;
	// }

	private static boolean is3D(final Geometry geometry) {
		return Double.isNaN(geometry.getCoordinate().z) ? false : true;
	}
}