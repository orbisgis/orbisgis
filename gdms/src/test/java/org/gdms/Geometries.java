/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import org.gdms.data.types.Type;

public final class Geometries {

	private static GeometryFactory gf = new GeometryFactory();

	public static LineString getLinestring() {
		return gf.createLineString(new Coordinate[] { new Coordinate(0, 0),
				new Coordinate(10, 0), new Coordinate(110, 0),
				new Coordinate(10, 240), });
	}

	public static LinearRing getLinearRing() {
		return gf.createLinearRing(new Coordinate[] { new Coordinate(0, 0),
				new Coordinate(10, 0), new Coordinate(110, 0),
				new Coordinate(10, 240), new Coordinate(0, 0) });
	}

	public static MultiLineString getMultilineString() {
		return gf.createMultiLineString(new LineString[] { getLinestring() });
	}

	public static Geometry getPolygon() {
		return gf.createPolygon(getLinearRing(), new LinearRing[0]);
	}

	public static Geometry getPoint() {
		return gf.createPoint(new Coordinate(10, 10));
	}

	public static Point getPoint3D() {
		return gf.createPoint(new Coordinate(10, 10, 50));
	}

	public static Geometry getSampleGeometry(int geometryType, int dimension) {
		switch (geometryType) {
		case Type.LINESTRING:
			return getLinestring();
		case Type.MULTILINESTRING:
			return getMultilineString();
		case Type.POINT:
			if (dimension == 2) {
				return getPoint();
			} else {
				return getPoint3D();
			}
		case Type.POLYGON:
			return getPolygon();
		case Type.MULTIPOLYGON:
			return getMultiPolygon2D();
		default:
			return getPoint();
		}
	}

	public static LineString getLineString3D() {
		return gf.createLineString(new Coordinate[] { new Coordinate(0, 0, 0),
				new Coordinate(10, 2, 5), new Coordinate(110, 0, 4),
				new Coordinate(10, 240, 10), });
	}

	public static Polygon getPolygon3D() {
		return gf.createPolygon(getLinearRing3D(), new LinearRing[0]);
	}

	private static LinearRing getLinearRing3D() {
		return gf.createLinearRing(new Coordinate[] { new Coordinate(0, 2, 0),
				new Coordinate(10, 2, 0), new Coordinate(110, 22, 0),
				new Coordinate(10, 62, 240), new Coordinate(0, 2, 0) });
	}

	public static Geometry getMultiPoint3D() {
		Point[] points = new Point[2];
		points[0] = getPoint3D();
		points[1] = gf.createPoint(new Coordinate(23, 325, 74));
		return gf.createMultiPoint(points);
	}

	public static Geometry getMultilineString3D() {
		return gf.createMultiLineString(new LineString[] { getLineString3D(),
				getLineString3D() });
	}

	public static Geometry getMultiPolygon3D() {
		return gf.createMultiPolygon(new Polygon[] { getPolygon3D(),
				getPolygon3D() });
	}

	public static Geometry getMultiPolygon2D() {
		return gf.createMultiPolygon(new Polygon[] { (Polygon) getPolygon() });
	}

	public static Geometry getGeometryCollection() {
		return gf
				.createGeometryCollection(new Geometry[] { getLinearRing(),
						getMultiPoint3D(), getMultilineString3D(),
						getMultiPolygon2D() });
	}

        private Geometries() {
        }
}
