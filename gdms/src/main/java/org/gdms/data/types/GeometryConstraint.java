/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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

/**
 * Constraint indicating the type of the geometry: point, multilinestring, ...
 *
 */
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

	private static boolean is3D(final Geometry geometry) {
		return Double.isNaN(geometry.getCoordinate().z) ? false : true;
	}
}