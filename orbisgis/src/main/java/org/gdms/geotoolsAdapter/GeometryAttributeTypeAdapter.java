package org.gdms.geotoolsAdapter;

import org.gdms.data.types.GeometryConstraint;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class GeometryAttributeTypeAdapter implements GeometryAttributeType {
	private int geomType;

	private String spatialFieldName;

	public static CoordinateReferenceSystem currentCRS;

	public GeometryAttributeTypeAdapter(final String spatialFieldName,
			final int geomType) {
		this.spatialFieldName = spatialFieldName;
		this.geomType = geomType;
	}

	public CoordinateReferenceSystem getCoordinateSystem() {
		// TODO we use the default CRS in MapContext
		return currentCRS;
	}

	public GeometryFactory getGeometryFactory() {
		throw new Error();
	}

	public Filter getRestriction() {
		throw new Error();
	}

	public Class getType() {
		switch (geomType) {
		case GeometryConstraint.POINT_2D:
		case GeometryConstraint.POINT_3D:
			return Point.class;
		case GeometryConstraint.MULTI_POINT_2D:
		case GeometryConstraint.MULTI_POINT_3D:
			return MultiPoint.class;

		case GeometryConstraint.LINESTRING_2D:
		case GeometryConstraint.LINESTRING_3D:
			return LineString.class;
		case GeometryConstraint.MULTI_LINESTRING_2D:
		case GeometryConstraint.MULTI_LINESTRING_3D:
			return MultiLineString.class;

		case GeometryConstraint.POLYGON_2D:
		case GeometryConstraint.POLYGON_3D:
			return Polygon.class;
		case GeometryConstraint.MULTI_POLYGON_2D:
		case GeometryConstraint.MULTI_POLYGON_3D:
			return MultiPolygon.class;

		case GeometryConstraint.MIXED:
			return Geometry.class;
		}
		throw new Error();
	}

	public boolean isGeometry() {
		return true;
	}

	public Object createDefaultValue() {
		throw new Error();
	}

	public Object duplicate(Object src) throws IllegalAttributeException {
		throw new Error();
	}

	public int getMaxOccurs() {
		throw new Error();
	}

	public int getMinOccurs() {
		throw new Error();
	}

	public String getName() {
		return spatialFieldName;
	}

	public boolean isNillable() {
		throw new Error();
	}

	public Object parse(Object value) throws IllegalArgumentException {
		throw new Error();
	}

	public void validate(Object obj) throws IllegalArgumentException {
		throw new Error();
	}
}