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
/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.orbisgis.core.ui.editors.map.tool;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Polygon;

/**
 * A wrapper around GDBMS Geometry in order to provide the handler related
 * methods
 * 
 * @author Fernando Gonzlez Corts
 */
public class Primitive {

	private static GeometryFactory gf = new GeometryFactory();

	public static String POINT_GEOMETRY_TYPE;

	public static String MULTIPOINT_GEOMETRY_TYPE;

	public static String LINE_GEOMETRY_TYPE;

	public static String MULTILINE_GEOMETRY_TYPE;

	public static String POLYGON_GEOMETRY_TYPE;

	public static String MULTIPOLYGON_GEOMETRY_TYPE;

	static {
		POINT_GEOMETRY_TYPE = gf.createPoint(new Coordinate(0, 0))
				.getGeometryType();
		MULTIPOINT_GEOMETRY_TYPE = gf.createMultiPoint(
				new Coordinate[] { new Coordinate(0, 0) }).getGeometryType();
		LineString ls = gf.createLineString(new Coordinate[] {
				new Coordinate(0, 0), new Coordinate(1, 0) });
		LINE_GEOMETRY_TYPE = ls.getGeometryType();
		MULTILINE_GEOMETRY_TYPE = gf.createMultiLineString(
				new LineString[] { ls }).getGeometryType();
		LinearRing lr = gf.createLinearRing(new Coordinate[] {
				new Coordinate(0, 0), new Coordinate(1, 1),
				new Coordinate(1, 0), new Coordinate(0, 0) });
		Polygon pol = gf.createPolygon(lr, new LinearRing[0]);
		POLYGON_GEOMETRY_TYPE = pol.getGeometryType();
		MULTIPOLYGON_GEOMETRY_TYPE = gf.createMultiPolygon(
				new Polygon[] { pol }).getGeometryType();
	}

	private Geometry geometry;

	private int geomIndex;

	/**
	 * Creates a new Primitive
	 * 
	 * @param g
	 *            Geometry to be wrapped
	 * @param geometryIndex
	 *            index of the geometry in the Theme it was read
	 */
	public Primitive(Geometry g, int geomIndex) {
		this.geometry = g;
		this.geomIndex = geomIndex;
	}

	public Handler[] getHandlers() {
		Coordinate[] hndPoints;
		Handler[] ret;
		ArrayList<Handler> retArray;
		String type = geometry.getGeometryType();
		if (type.equals(POINT_GEOMETRY_TYPE) || type.equals(LINE_GEOMETRY_TYPE)) {
			hndPoints = geometry.getCoordinates();
			ret = new Handler[hndPoints.length];
			for (int i = 0; i < hndPoints.length; i++) {
				ret[i] = new PointHandler(geometry, geometry.getGeometryType(),
						i, hndPoints[i], geomIndex);
			}
			return ret;
		} else if (type.equals(MULTIPOINT_GEOMETRY_TYPE)) {
			retArray = new ArrayList<Handler>();
			for (int g = 0; g < geometry.getNumGeometries(); g++) {
				hndPoints = geometry.getGeometryN(g).getCoordinates();
				for (int i = 0; i < hndPoints.length; i++) {
					retArray.add(new MultipointHandler(geometry, g, i,
							hndPoints[i], geomIndex));
				}
			}
			return retArray.toArray(new Handler[0]);
		} else if (type.equals(MULTILINE_GEOMETRY_TYPE)) {
			retArray = new ArrayList<Handler>();
			for (int g = 0; g < geometry.getNumGeometries(); g++) {
				hndPoints = geometry.getGeometryN(g).getCoordinates();
				for (int i = 0; i < hndPoints.length; i++) {
					retArray.add(new MultilineHandler(geometry, g, i,
							hndPoints[i], geomIndex));
				}
			}
			return retArray.toArray(new Handler[0]);
		} else if (type.equals(POLYGON_GEOMETRY_TYPE)) {
			retArray = new ArrayList<Handler>();
			for (int g = 0; g < geometry.getNumGeometries(); g++) {
				hndPoints = geometry.getGeometryN(g).getCoordinates();
				for (int i = 0; i < hndPoints.length; i++) {
					retArray.add(new PolygonHandler(geometry, g - 1, i,
							hndPoints[i], geomIndex));
				}
			}
			return retArray.toArray(new Handler[0]);
		} else if (type.equals(MULTIPOLYGON_GEOMETRY_TYPE)) {
			retArray = new ArrayList<Handler>();
			for (int g = 0; g < geometry.getNumGeometries(); g++) {
				Geometry pol = geometry.getGeometryN(g);
				for (int r = 0; r < pol.getNumGeometries(); r++) {
					hndPoints = pol.getGeometryN(r).getCoordinates();
					for (int i = 0; i < hndPoints.length; i++) {
						retArray.add(new MultiPolygonHandler(geometry, g,
								r - 1, i, hndPoints[i], geomIndex));
					}
				}
			}
			return retArray.toArray(new Handler[0]);
		}

		throw new UnsupportedOperationException("for geometry type: " + type); //$NON-NLS-1$
	}

	/**
	 * Gets this geometry by adding the specified point as a new vertex
	 * 
	 * @param vertexPoint
	 * @param tolerance
	 * @return Null if the vertex cannot be inserted
	 * @throws CannotChangeGeometryException
	 *             If the vertex can be inserted but it makes the geometry to be
	 *             in an invalid shape
	 */
	public Geometry insertVertex(Point2D vertexPoint, double tolerance)
			throws CannotChangeGeometryException {
		String geometryType = geometry.getGeometryType();
		if ((geometryType == POINT_GEOMETRY_TYPE)) {

		} else if ((geometryType == MULTIPOINT_GEOMETRY_TYPE)) {
			return insertVertexInMultipoint(geometry, vertexPoint, tolerance);
		} else if (geometryType == LINE_GEOMETRY_TYPE) {
			return insertVertexInLine(geometry, vertexPoint, tolerance);
		} else if (geometryType == MULTILINE_GEOMETRY_TYPE) {
			LineString[] linestrings = new LineString[geometry
					.getNumGeometries()];
			boolean any = false;
			for (int i = 0; i < geometry.getNumGeometries(); i++) {
				LineString line = (LineString) geometry.getGeometryN(i);

				LineString inserted = (LineString) insertVertexInLine(line,
						vertexPoint, tolerance);
				if (inserted != null) {
					linestrings[i] = inserted;
					any = true;
				} else {
					linestrings[i] = line;
				}
			}
			if (any) {
				return gf.createMultiLineString(linestrings);
			} else {
				return null;
			}
		} else if (geometryType == POLYGON_GEOMETRY_TYPE) {
			return insertVertexInPolygon(geometry, vertexPoint, tolerance);
		} else if (geometryType == MULTIPOLYGON_GEOMETRY_TYPE) {
			Polygon[] polygons = new Polygon[geometry.getNumGeometries()];
			boolean any = false;
			for (int i = 0; i < geometry.getNumGeometries(); i++) {
				Polygon polygon = (Polygon) geometry.getGeometryN(i);

				Polygon inserted = (Polygon) insertVertexInPolygon(polygon,
						vertexPoint, tolerance);
				if (inserted != null) {
					any = true;
					polygons[i] = inserted;
				} else {
					polygons[i] = polygon;
				}
			}
			if (any) {
				return gf.createMultiPolygon(polygons);
			} else {
				return null;
			}
		}

		throw new UnsupportedOperationException("Unknown type: " + geometryType); //$NON-NLS-1$
	}

	private Coordinate[] insertVertexInLinearRing(Coordinate[] coords,
			Point2D vertexPoint, double tolerance) {
		Rectangle2D r = new Rectangle2D.Double(vertexPoint.getX() - tolerance,
				vertexPoint.getY() - tolerance, 2 * tolerance, 2 * tolerance);

		int index = -1;
		for (int i = 1; i < coords.length; i++) {
			if (new Line2D.Double(coords[i - 1].x, coords[i - 1].y,
					coords[i].x, coords[i].y).intersects(r)) {
				index = i - 1;
				break;
			}
		}

		if (index != -1) {
			Coordinate[] ret = new Coordinate[coords.length + 1];
			System.arraycopy(coords, 0, ret, 0, index + 1);
			ret[index + 1] = new Coordinate(vertexPoint.getX(), vertexPoint
					.getY());
			System.arraycopy(coords, index + 1, ret, index + 2, coords.length
					- (index + 1));
			return ret;
		} else {
			return null;
		}
	}

	private Geometry insertVertexInPolygon(Geometry geometry,
			Point2D vertexPoint, double tolerance)
			throws CannotChangeGeometryException {
		Polygon p = (Polygon) geometry;

		Coordinate[] inserted = insertVertexInLinearRing(p.getExteriorRing()
				.getCoordinates(), vertexPoint, tolerance);
		if (inserted != null) {
			LinearRing[] holes = new LinearRing[p.getNumInteriorRing()];
			for (int i = 0; i < holes.length; i++) {
				holes[i] = gf.createLinearRing(p.getInteriorRingN(i)
						.getCoordinates());
			}
			Polygon ret = gf
					.createPolygon(gf.createLinearRing(inserted), holes);

			if (!ret.isValid()) {
				throw new CannotChangeGeometryException(
						Handler.THE_GEOMETRY_IS_NOT_VALID);
			}

			return ret;
		}

		for (int i = 0; i < p.getNumInteriorRing(); i++) {
			inserted = insertVertexInLinearRing(p.getInteriorRingN(i)
					.getCoordinates(), vertexPoint, tolerance);
			if (inserted != null) {
				LinearRing[] holes = new LinearRing[p.getNumInteriorRing()];
				for (int h = 0; h < holes.length; h++) {
					if (h == i) {
						holes[h] = gf.createLinearRing(inserted);
					} else {
						holes[h] = gf.createLinearRing(p.getInteriorRingN(h)
								.getCoordinates());
					}
				}

				Polygon ret = gf.createPolygon(gf.createLinearRing(p
						.getExteriorRing().getCoordinates()), holes);

				if (!ret.isValid()) {
					throw new CannotChangeGeometryException(
							Handler.THE_GEOMETRY_IS_NOT_VALID);
				}

				return ret;
			}
		}

		return null;
	}

	private Geometry insertVertexInLine(Geometry g, Point2D vertexPoint,
			double tolerance) throws CannotChangeGeometryException {
		Coordinate[] coords = insertVertexInLinearRing(g.getCoordinates(),
				vertexPoint, tolerance);
		if (coords == null) {
			return null;
		}
		LineString ls = gf.createLineString(coords);

		if (!ls.isValid()) {
			throw new CannotChangeGeometryException(
					Handler.THE_GEOMETRY_IS_NOT_VALID);
		}

		return ls;
	}

	private Geometry insertVertexInMultipoint(Geometry g, Point2D vertexPoint,
			double tolerance) throws CannotChangeGeometryException {
		Coordinate[] coords = insertVertexInLinearRing(g.getCoordinates(),
				vertexPoint, tolerance);
		if (coords == null) {
			return null;
		}
		MultiPoint ls = gf.createMultiPoint(coords);

		if (!ls.isValid()) {
			throw new CannotChangeGeometryException(
					Handler.THE_GEOMETRY_IS_NOT_VALID);
		}

		return ls;
	}
}
