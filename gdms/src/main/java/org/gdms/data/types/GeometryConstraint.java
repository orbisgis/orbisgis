/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data.types;

import org.gdms.data.values.Value;

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
public class GeometryConstraint extends AbstractIntConstraint {
	public static final int MIXED = 0;

	public static final int POINT = 10;

	public static final int MULTI_POINT = 12;

	public static final int LINESTRING = 14;

	public static final int MULTI_LINESTRING = 16;

	public static final int POLYGON = 18;

	public static final int MULTI_POLYGON = 20;

	public GeometryConstraint(final int constraintValue) {
		super(constraintValue);
	}

	public GeometryConstraint(byte[] constraintBytes) {
		super(constraintBytes);
	}

	public int getConstraintCode() {
		return Constraint.GEOMETRY_TYPE;
	}

	public String check(Value value) {
		if (!(value.getType() == Type.GEOMETRY)) {
			return "Value '" + value.toString() + "' must be a Geometry";
		} else {
			final Geometry geom = value.getAsGeometry();
			final int st = findBestGeometryType(geom);
			if (st != constraintValue) {
				return "Geometries types mismatch : " + Integer.toString(st)
						+ " not equal to " + Integer.toString(constraintValue);
			}
		}
		return null;
	}

	/**
	 *
	 * @return One of the following constant in geometry constraint: MIXED,
	 *         POINT,MULTI_POINT, LINESTRING,
	 *         MULTI_LINESTRING,POLYGON,MULTI_POLYGON
	 */

	public int getGeometryType() {
		return constraintValue;
	}

	private static int findBestGeometryType(final Geometry geometry) {
		int type = MIXED;

		if (geometry instanceof Point) {
			type = POINT;
		} else if (geometry instanceof MultiPoint) {
			type = MULTI_POINT;
		} else if (geometry instanceof Polygon) {
			type = POLYGON;
		} else if (geometry instanceof MultiPolygon) {
			type = MULTI_POLYGON;
		} else if (geometry instanceof LineString) {
			type = LINESTRING;
		} else if (geometry instanceof MultiLineString) {
			type = MULTI_LINESTRING;
		}

		return type;
	}
}