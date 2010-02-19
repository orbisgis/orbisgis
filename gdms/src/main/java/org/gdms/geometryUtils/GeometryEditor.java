/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */
package org.gdms.geometryUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.Assert;

/**
 * Geometry objects are unmodifiable; this class allows you to "modify" a
 * Geometry in a sense -- the modified Geometry is returned as a new Geometry.
 * The new Geometry's #isValid should be checked.
 */
public class GeometryEditor {
	private GeometryFactory factory = new GeometryFactory();
	protected boolean vertexInserted = false;

	private static final double EPSILON = 1E-6;


	public GeometryEditor() {
	}

	public Geometry edit(Geometry geometry, GeometryEditorOperation operation) {
		if (geometry instanceof GeometryCollection) {
			return editGeometryCollection((GeometryCollection) geometry,
					operation);
		}
		if (geometry instanceof Polygon) {
			return editPolygon((Polygon) geometry, operation);
		}
		if (geometry instanceof Point) {
			return operation.edit(geometry);
		}
		if (geometry instanceof LineString) {
			return operation.edit(geometry);
		}
		Assert
				.shouldNeverReachHere("ui.GeometryEditor.unsupported-geometry-classes-should-be-caught-in-the-GeometryEditorOperation");
		return null;
	}

	private Polygon editPolygon(Polygon polygon,
			GeometryEditorOperation operation) {
		Polygon newPolygon = (Polygon) operation.edit(polygon);
		if (newPolygon.isEmpty()) {
			// RemoveSelectedPlugIn relies on this behaviour. [Jon Aquino]
			return newPolygon;
		}
		LinearRing shell = (LinearRing) edit(newPolygon.getExteriorRing(),
				operation);
		if (shell.isEmpty()) {
			// RemoveSelectedPlugIn relies on this behaviour. [Jon Aquino]
			return factory.createPolygon(null, null);
		}
		ArrayList holes = new ArrayList();
		for (int i = 0; i < newPolygon.getNumInteriorRing(); i++) {
			LinearRing hole = (LinearRing) edit(newPolygon.getInteriorRingN(i),
					operation);
			if (hole.isEmpty()) {
				continue;
			}
			holes.add(hole);
		}
		return factory.createPolygon(shell, (LinearRing[]) holes
				.toArray(new LinearRing[] {}));
	}

	private GeometryCollection editGeometryCollection(
			GeometryCollection collection, GeometryEditorOperation operation) {
		GeometryCollection newCollection = (GeometryCollection) operation
				.edit(collection);
		ArrayList geometries = new ArrayList();
		for (int i = 0; i < newCollection.getNumGeometries(); i++) {
			Geometry geometry = edit(newCollection.getGeometryN(i), operation);
			if (geometry.isEmpty()) {
				continue;
			}
			geometries.add(geometry);
		}
		if (newCollection.getClass() == MultiPoint.class) {
			return factory.createMultiPoint((Point[]) geometries
					.toArray(new Point[] {}));
		}
		if (newCollection.getClass() == MultiLineString.class) {
			return factory.createMultiLineString((LineString[]) geometries
					.toArray(new LineString[] {}));
		}
		if (newCollection.getClass() == MultiPolygon.class) {
			return factory.createMultiPolygon((Polygon[]) geometries
					.toArray(new Polygon[] {}));
		}
		return factory.createGeometryCollection((Geometry[]) geometries
				.toArray(new Geometry[] {}));
	}

	/**
	 * The input and output Geometries may share some Coordinate arrays.
	 */
	public Geometry removeRepeatedPoints(Geometry geometry) {
		if (geometry.isEmpty()) {
			return geometry;
		}
		return edit(geometry, new CoordinateOperation() {
			public Coordinate[] edit(Coordinate[] coordinates,
					boolean linearRing) {
				// May return the same Coordinate array. [Jon Aquino]
				return com.vividsolutions.jts.geom.CoordinateArrays
						.removeRepeatedPoints(coordinates);
			}
		});
	}

	/**
	 * @return null if parent == itemToRemove
	 */
	public Geometry remove(Geometry g, final Geometry itemToRemove) {
		return edit(g, new GeometryEditorOperation() {
			public Geometry edit(Geometry geometry) {
				if (geometry == itemToRemove) {
					return createNullGeometry(geometry.getClass());
				}
				return geometry;
			}
		});
	}

	private Geometry createNullGeometry(Class geometryClass) {
		if (geometryClass == MultiPolygon.class) {
			return factory.createMultiPolygon(null);
		}
		if (geometryClass == MultiLineString.class) {
			return factory.createMultiLineString(null);
		}
		if (geometryClass == MultiPoint.class) {
			return factory.createMultiPoint((Coordinate[]) null);
		}
		if (geometryClass == GeometryCollection.class) {
			return factory.createGeometryCollection(null);
		}
		if (geometryClass == Polygon.class) {
			return factory.createPolygon(null, null);
		}
		if (geometryClass == LinearRing.class) {
			return factory.createLinearRing((Coordinate[]) null);
		}
		if (geometryClass == LineString.class) {
			return factory.createLineString((Coordinate[]) null);
		}
		if (geometryClass == Point.class) {
			return factory.createPoint((Coordinate) null);
		}
		Assert.shouldNeverReachHere();
		return null;
	}

	/**
	 * The vertex will be inserted at the point closest to the target.
	 */
	public Geometry insertVertex(Geometry geometry, Coordinate target,
			Geometry ignoreSegmentsOutside) {
		LineString closestSegment = null;
		Point targetPoint = factory.createPoint(target);
		for (Iterator i = CoordinateArrays.toCoordinateArrays(geometry, false)
				.iterator(); i.hasNext();) {
			Coordinate[] coordinates = (Coordinate[]) i.next();
			if (coordinates.length < 2) {
				continue;
			}
			for (int j = 1; j < coordinates.length; j++) { // 1
				LineString candidate = factory
						.createLineString(new Coordinate[] { coordinates[j],
								coordinates[j - 1] });
				if (!candidate.intersects(ignoreSegmentsOutside)) {
					continue;
				}
				if (closestSegment == null) {
					closestSegment = candidate;
				} else if (candidate.distance(targetPoint) < closestSegment
						.distance(targetPoint)) {
					closestSegment = candidate;
				}
			}
		}
		if (closestSegment == null) {
			return null;
		}
		return insertVertex(geometry, closestSegment.getCoordinateN(0),
				closestSegment.getCoordinateN(1), new LineSegment(
						closestSegment.getCoordinateN(0), closestSegment
								.getCoordinateN(1)).closestPoint(target));
	}

	/**
	 * Inserts v on the line segment with endpoints equal to existing1 and
	 * existing2
	 */
	public Geometry insertVertex(Geometry geometry, final Coordinate existing1,
			final Coordinate existing2, final Coordinate v) {
		if (geometry.isEmpty()) {
			return geometry;
		}
		return edit(geometry, new CoordinateOperation() {

			public Coordinate[] edit(Coordinate[] coordinates,
					boolean linearRing) {
				if (vertexInserted) {
					return coordinates;
				}
				for (int i = 1; i < coordinates.length; i++) { // 1
					if ((coordinates[i - 1].equals(existing1) && coordinates[i]
							.equals(existing2))
							|| (coordinates[i - 1].equals(existing2) && coordinates[i]
									.equals(existing1))) {
						Coordinate[] newCoordinates = new Coordinate[coordinates.length + 1];
						System.arraycopy(coordinates, 0, newCoordinates, 0, i);
						newCoordinates[i] = v;
						System.arraycopy(coordinates, i, newCoordinates, i + 1,
								coordinates.length - i);
						vertexInserted = true;
						return newCoordinates;
					}
				}
				return coordinates;
			}
		});
	}

	/**
	 * Deletes the given vertices (matched using ==, not #equals).
	 */
	public Geometry deleteVertices(Geometry geometry, final Collection vertices) {
		return edit(geometry, new CoordinateOperation() {
			public Coordinate[] edit(Coordinate[] coordinates,
					boolean linearRing) {
				List newCoordinates = new ArrayList(Arrays.asList(coordinates));
				boolean firstCoordinateDeleted = false;
				int j = -1;
				for (Iterator i = newCoordinates.iterator(); i.hasNext();) {
					Coordinate c = (Coordinate) i.next();
					j++;
					if (containsReference(vertices, c)) {
						i.remove();
						if (j == 0) {
							firstCoordinateDeleted = true;
						}
					}
				}
				if (linearRing && firstCoordinateDeleted) {
					newCoordinates.remove(newCoordinates.size() - 1);
				}
				if (linearRing
						&& firstCoordinateDeleted
						&& !newCoordinates.isEmpty()
						&& !newCoordinates.get(0).equals(
								newCoordinates.get(newCoordinates.size() - 1))) {
					newCoordinates.add(new Coordinate(
							(Coordinate) newCoordinates.get(0)));
				}
				return (Coordinate[]) newCoordinates
						.toArray(new Coordinate[] {});
			}
		});
	}

	public boolean containsReference(Collection collection, Object o) {
		// Inefficient. [Jon Aquino]
		for (Iterator i = collection.iterator(); i.hasNext();) {
			Object item = (Object) i.next();
			if (item == o) {
				return true;
			}
		}
		return false;
	}

	public interface GeometryEditorOperation {
		/**
		 * "Modifies" a Geometry by returning a new Geometry with a
		 * modification. The returned Geometry might be the same as the Geometry
		 * passed in.
		 */
		public Geometry edit(Geometry geometry);
	}

	private Coordinate[] atLeastNCoordinatesOrNothing(int n, Coordinate[] c) {
		return c.length >= n ? c : new Coordinate[] {};
	}

	private abstract class CoordinateOperation implements
			GeometryEditorOperation {
		public Geometry edit(Geometry geometry) {
			if (geometry instanceof LinearRing) {
				return factory.createLinearRing(atLeastNCoordinatesOrNothing(4,
						edit(geometry.getCoordinates(), true)));
			}
			if (geometry instanceof LineString) {
				return factory.createLineString(atLeastNCoordinatesOrNothing(2,
						edit(geometry.getCoordinates(), false)));
			}
			if (geometry instanceof Point) {
				Coordinate[] newCoordinates = edit(geometry.getCoordinates(),
						false);
				Assert.isTrue(newCoordinates.length < 2);
				return factory
						.createPoint((newCoordinates.length > 0) ? newCoordinates[0]
								: null);
			}
			return geometry;
		}

		public abstract Coordinate[] edit(Coordinate[] coordinates,
				boolean linearRing);
	}

	/**
	 * Insert a coordinate onto a geometry
	 * @param geometry
	 * @param target
	 * @return
	 */
	public static Geometry insertVertex(Geometry geometry, Coordinate target) {

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
