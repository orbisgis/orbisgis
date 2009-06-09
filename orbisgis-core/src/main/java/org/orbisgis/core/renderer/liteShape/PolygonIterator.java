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

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

/**
 * A path iterator for the LiteShape class, specialized to iterate over Polygon
 * objects.
 *
 * @author Andrea Aime
 * @author simone giannecchini
 * @source $URL:
 *         http://svn.geotools.org/geotools/tags/2.3.1/module/render/src/org/geotools/renderer/lite/PolygonIterator.java $
 * @version $Id: PolygonIterator.java 20874 2006-08-07 10:00:01Z jgarnett $
 */
public final class PolygonIterator extends AbstractLiteIterator {
	/** Transform applied on the coordinates during iteration */
	private AffineTransform at;

	/** The rings describing the polygon geometry */
	private LineString[] rings;

	/** The current ring during iteration */
	private int currentRing = 0;

	/** Current line coordinate */
	private int currentCoord = 0;

	/** The array of coordinates that represents the line geometry */
	private CoordinateSequence coords = null;

	/** The previous coordinate (during iteration) */
	private Coordinate oldCoord = null;

	/** True when the iteration is terminated */
	private boolean done = false;

	/** If true, apply simple distance based generalization */
	private boolean generalize = false;

	/** Maximum distance for point elision when generalizing */
	private double maxDistance = 1.0;

	/** Horizontal scale, got from the affine transform and cached */
	private double xScale;

	/** Vertical scale, got from the affine transform and cached */
	private double yScale;

	/**
	 * Creates a new PolygonIterator object.
	 *
	 * @param p
	 *            The polygon
	 * @param at
	 *            The affine transform applied to coordinates during iteration
	 */
	public PolygonIterator(Polygon p, AffineTransform at) {
		int numInteriorRings = p.getNumInteriorRing();
		rings = new LineString[numInteriorRings + 1];
		rings[0] = p.getExteriorRing();

		for (int i = 0; i < numInteriorRings; i++) {
			LineString ring = p.getInteriorRingN(i);
			Coordinate[] coordinates = ring.getCoordinates();
			if (CGAlgorithms.isCCW(coordinates)) {
				Coordinate[] reversed = new Coordinate[coordinates.length];
				for (int j = 0; j < reversed.length; j++) {
					reversed[j] = coordinates[reversed.length -j -1];
				}
				ring = new GeometryFactory().createLinearRing(reversed);
			}

			rings[i + 1] = ring;
		}

		if (at == null) {
			at = new AffineTransform();
		}

		this.at = at;
		xScale = Math.sqrt((at.getScaleX() * at.getScaleX())
				+ (at.getShearX() * at.getShearX()));
		yScale = Math.sqrt((at.getScaleY() * at.getScaleY())
				+ (at.getShearY() * at.getShearY()));

		coords = rings[0].getCoordinateSequence();
		done = p.getExteriorRing().getNumPoints() == 0;
	}

	/**
	 * Creates a new PolygonIterator object.
	 *
	 * @param p
	 *            The polygon
	 * @param at
	 *            The affine transform applied to coordinates during iteration
	 * @param generalize
	 *            if true apply simple distance based generalization
	 */
	public PolygonIterator(Polygon p, AffineTransform at, boolean generalize) {
		this(p, at);
		this.generalize = generalize;
	}

	/**
	 * Creates a new PolygonIterator object.
	 *
	 * @param p
	 *            The polygon
	 * @param at
	 *            The affine transform applied to coordinates during iteration
	 * @param generalize
	 *            if true apply simple distance based generalization
	 * @param maxDistance
	 *            during iteration, a point will be skipped if it's distance
	 *            from the previous is less than maxDistance
	 */
	public PolygonIterator(Polygon p, AffineTransform at, boolean generalize,
			double maxDistance) {
		this(p, at, generalize);
		this.maxDistance = maxDistance;
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
		if (currentCoord == 0) {
			coords[0] = this.coords.getX(0);
			coords[1] = this.coords.getY(0);
			transform(coords, 0, coords, 0, 1);

			return SEG_MOVETO;
		} else if (currentCoord == this.coords.size()) {
			return SEG_CLOSE;
		} else {
			coords[0] = this.coords.getX(currentCoord);
			coords[1] = this.coords.getY(currentCoord);
			transform(coords, 0, coords, 0, 1);

			return SEG_LINETO;
		}
	}

	protected void transform(double[] src, int index, double[] dest,
			int destIndex, int numPoints) {
		at.transform(src, index, dest, destIndex, numPoints);
	}

	/**
	 * Return the winding rule for determining the interior of the path.
	 *
	 * @return <code>WIND_EVEN_ODD</code> by default.
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
		if (currentCoord == coords.size()) {
			if (currentRing < (rings.length - 1)) {
				currentCoord = 0;
				currentRing++;
				coords = rings[currentRing].getCoordinateSequence();
			} else {
				done = true;
			}
		} else {
			if (generalize) {
				if (oldCoord == null) {
					currentCoord++;
					oldCoord = coords.getCoordinate(currentCoord);
				} else {
					double distx = 0;
					double disty = 0;

					do {
						currentCoord++;

						if (currentCoord < coords.size()) {
							distx = Math.abs(coords.getX(currentCoord)
									- oldCoord.x);
							disty = Math.abs(coords.getY(currentCoord)
									- oldCoord.y);
						}
					} while (((distx * xScale) < maxDistance)
							&& ((disty * yScale) < maxDistance)
							&& (currentCoord < coords.size()));

					if (currentCoord < coords.size()) {
						oldCoord = coords.getCoordinate(currentCoord);
					} else {
						oldCoord = null;
					}
				}
			} else {
				currentCoord++;
			}
		}
	}

}
