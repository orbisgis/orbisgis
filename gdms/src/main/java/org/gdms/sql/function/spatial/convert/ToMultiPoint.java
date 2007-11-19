package org.gdms.sql.function.spatial.convert;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

// select id,AsWKT(the_geom),AsWKT(ToMultiPoint2D(the_geom)) from points;

public class ToMultiPoint implements Function {
	public String getName() {
		return "ToMultiPoint";
	}

	public Function cloneFunction() {
		return new ToMultiPoint();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		final GeometryValue gv = (GeometryValue) args[0];
		final Geometry geom = gv.getGeom();
		MultiPoint multiPoint = null;

		// what follows is problematic with multipoints that just contains a
		// single point...
		// if (1 == geom.getNumGeometries()) {
		// // geom is not a GeometryCollection...
		// } else {
		// // geom is a GeometryCollection...
		// }

		if (geom instanceof Point) {
			multiPoint = new GeometryFactory().createMultiPoint(((Point) geom)
					.getCoordinates());
		} else if (geom instanceof LineString) {
			multiPoint = new GeometryFactory()
					.createMultiPoint(((LineString) geom).getCoordinates());
		} else if (geom instanceof Polygon) {
			multiPoint = new GeometryFactory()
					.createMultiPoint(((Polygon) geom).getCoordinates());
		} else if (geom instanceof MultiPoint) {
			multiPoint = (MultiPoint) geom;
		} else if (geom instanceof MultiLineString) {
			final MultiLineString multiLineString = (MultiLineString) geom;
			final int numGeometries = multiLineString.getNumGeometries();
			final List<Coordinate> allCoordinates = new ArrayList<Coordinate>();
			for (int i = 0; i < numGeometries; i++) {
				final LineString lineString = (LineString) (multiLineString
						.getGeometryN(i));
				for (Coordinate vertex : lineString.getCoordinates()) {
					allCoordinates.add(vertex);
				}
			}
			multiPoint = new GeometryFactory().createMultiPoint(allCoordinates
					.toArray(new Coordinate[0]));
		} else if (geom instanceof MultiPolygon) {
			final MultiPolygon multiPolygon = (MultiPolygon) geom;
			final int numGeometries = multiPolygon.getNumGeometries();
			final List<Coordinate> allCoordinates = new ArrayList<Coordinate>();
			for (int i = 0; i < numGeometries; i++) {
				final Polygon polygon = (Polygon) (multiPolygon.getGeometryN(i));
				for (Coordinate vertex : polygon.getCoordinates()) {
					allCoordinates.add(vertex);
				}
			}
			multiPoint = new GeometryFactory().createMultiPoint(allCoordinates
					.toArray(new Coordinate[0]));
		} else {
			throw new FunctionException("Geometric field's type is unknown !");
		}
		return ValueFactory.createValue(multiPoint);
	}

	public String getDescription() {
		return "Convert any subtype of GDMS geometric field into a Multi_Point_2D";
	}

	public int getType(int[] paramTypes) {
		return paramTypes[0];
	}

	public boolean isAggregate() {
		return false;
	}
}