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
/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2004-2006, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.orbisgis.core.renderer.liteShape;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * A path iterator for the LiteShape class, specialized to iterate over a
 * geometry collection. It can be seen as a composite, since uses in fact other,
 * simpler iterator to carry on its duties.
 *
 * @author Andrea Aime
 * @source $URL:
 *         http://svn.geotools.org/geotools/tags/2.3.1/module/render/src/org/geotools/renderer/lite/GeomCollectionIterator.java $
 * @version $Id: GeomCollectionIterator.java 20874 2006-08-07 10:00:01Z jgarnett $
 */
public final class GeomCollectionIterator extends AbstractLiteIterator {
	/** Transform applied on the coordinates during iteration */
	private AffineTransform at;

	/** The set of geometries that we will iterate over */
	private GeometryCollection gc;

	/** The current geometry */
	private int currentGeom;

	/** The current sub-iterator */
	private PathIterator currentIterator;

	/** True when the iterator is terminate */
	private boolean done = false;

	/** If true, apply simple distance based generalization */
	private boolean generalize = false;

	/** Maximum distance for point elision when generalizing */
	private double maxDistance = 1.0;

	private LineIterator lineIterator = new LineIterator();

	public GeomCollectionIterator() {

	}

	/**
	 * @param gc
	 * @param at
	 */
	public void init(GeometryCollection gc, AffineTransform at,
			boolean generalize, double maxDistance) {
		this.gc = gc;
		this.at = at == null ? new AffineTransform() : at;
		this.generalize = generalize;
		this.maxDistance = maxDistance;
		currentGeom = 0;
		done = false;
		currentIterator = getIterator(gc.getGeometryN(0));
	}

	/**
	 * Creates a new instance of GeomCollectionIterator
	 *
	 * @param gc
	 *            The geometry collection the iterator will use
	 * @param at
	 *            The affine transform applied to coordinates during iteration
	 * @param generalize
	 *            if true apply simple distance based generalization
	 * @param maxDistance
	 *            during iteration, a point will be skipped if it's distance
	 *            from the previous is less than maxDistance
	 */
	public GeomCollectionIterator(GeometryCollection gc, AffineTransform at,
			boolean generalize, double maxDistance) {
		init(gc, at, generalize, maxDistance);
	}

	/**
	 * Sets the distance limit for point skipping during distance based
	 * generalization
	 *
	 * @param distance
	 *            the maximum distance for point skipping
	 */
	public void setMaxDistance(double distance) {
		maxDistance = distance;
	}

	/**
	 * Returns the distance limit for point skipping during distance based
	 * generalization
	 *
	 * @return the maximum distance for distance based generalization
	 */
	public double getMaxDistance() {
		return maxDistance;
	}

	/**
	 * Returns the specific iterator for the geometry passed.
	 *
	 * @param g
	 *            The geometry whole iterator is requested
	 *
	 * @return the specific iterator for the geometry passed.
	 */
	private AbstractLiteIterator getIterator(Geometry g) {
		AbstractLiteIterator pi = null;

		if (g instanceof Polygon) {
			Polygon p = (Polygon) g;
			pi = new PolygonIterator(p, at, generalize, maxDistance);
		} else if (g instanceof GeometryCollection) {
			GeometryCollection gc = (GeometryCollection) g;
			pi = new GeomCollectionIterator(gc, at, generalize, maxDistance);
		} else if (g instanceof LineString) {
			LineString ls = (LineString) g;
			lineIterator.init(ls, at, generalize, (float) maxDistance);
			pi = lineIterator;
		} else if (g instanceof LinearRing) {
			LinearRing lr = (LinearRing) g;
			lineIterator.init(lr, at, generalize, (float) maxDistance);
			pi = lineIterator;
		} else if (g instanceof Point) {
			Point p = (Point) g;
			pi = new PointIterator(p, at);
		}

		return pi;
	}

	/**
	 * Returns the coordinates and type of the current path segment in the
	 * iteration. The return value is the path-segment type: SEG_MOVETO,
	 * SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or SEG_CLOSE. A double array of
	 * length 6 must be passed in and can be used to store the coordinates of
	 * the point(s). Each point is stored as a pair of double x,y coordinates.
	 * SEG_MOVETO and SEG_LINETO types returns one point, SEG_QUADTO returns two
	 * points, SEG_CUBICTO returns 3 points and SEG_CLOSE does not return any
	 * points.
	 *
	 * @param coords
	 *            an array that holds the data returned from this method
	 *
	 * @return the path-segment type of the current path segment.
	 *
	 * @see #SEG_MOVETO
	 * @see #SEG_LINETO
	 * @see #SEG_QUADTO
	 * @see #SEG_CUBICTO
	 * @see #SEG_CLOSE
	 */
	public int currentSegment(double[] coords) {
		return currentIterator.currentSegment(coords);
	}

	/**
	 * Returns the coordinates and type of the current path segment in the
	 * iteration. The return value is the path-segment type: SEG_MOVETO,
	 * SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or SEG_CLOSE. A float array of
	 * length 6 must be passed in and can be used to store the coordinates of
	 * the point(s). Each point is stored as a pair of float x,y coordinates.
	 * SEG_MOVETO and SEG_LINETO types returns one point, SEG_QUADTO returns two
	 * points, SEG_CUBICTO returns 3 points and SEG_CLOSE does not return any
	 * points.
	 *
	 * @param coords
	 *            an array that holds the data returned from this method
	 *
	 * @return the path-segment type of the current path segment.
	 *
	 * @see #SEG_MOVETO
	 * @see #SEG_LINETO
	 * @see #SEG_QUADTO
	 * @see #SEG_CUBICTO
	 * @see #SEG_CLOSE
	 */
	public int currentSegment(float[] coords) {
		return currentIterator.currentSegment(coords);
	}

	/**
	 * Returns the winding rule for determining the interior of the path.
	 *
	 * @return the winding rule.
	 *
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 */
	public int getWindingRule() {
		return WIND_EVEN_ODD;
	}

	/**
	 * Tests if the iteration is complete.
	 *
	 * @return <code>true</code> if all the segments have been read;
	 *         <code>false</code> otherwise.
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * Moves the iterator to the next segment of the path forwards along the
	 * primary direction of traversal as long as there are more points in that
	 * direction.
	 */
	public void next() {
		currentIterator.next();
		if (currentIterator.isDone()) {
			if (currentGeom < (gc.getNumGeometries() - 1)) {
				currentGeom++;
				currentIterator = getIterator(gc.getGeometryN(currentGeom));
			} else {
				done = true;
			}
		}
	}

}
