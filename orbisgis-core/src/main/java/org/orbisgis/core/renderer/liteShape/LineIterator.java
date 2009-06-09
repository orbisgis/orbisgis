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

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;

/**
 * A path iterator for the LiteShape class, specialized to iterate over
 * LineString object.
 *
 *
 * @author Andrea Aime
 * @author simone giannecchini *
 * @source $URL:
 *         http://svn.geotools.org/geotools/tags/2.3.1/module/render/src/org/geotools/renderer/lite/LineIterator.java $
 * @version $Id: LineIterator.java 22264 2006-10-19 10:10:35Z acuster $
 */
public final class LineIterator extends AbstractLiteIterator {
	/** Transform applied on the coordinates during iteration */
	private AffineTransform at;

	/** The array of coordinates that represents the line geometry */
	private CoordinateSequence coordinates = null;

	/** Current line coordinate */
	private int currentCoord = 0;

	/** The previous coordinate (during iteration) */
	private float oldX = Float.NaN;

	private float oldY = Float.NaN;

	/** True when the iteration is terminated */
	private boolean done = false;

	/** If true, apply simple distance based generalization */
	private boolean generalize = false;

	/** Maximum distance for point elision when generalizing */
	private float maxDistance = 1.0f;

	/** Horizontal scale, got from the affine transform and cached */
	private float xScale;

	/** Vertical scale, got from the affine transform and cached */
	private float yScale;

	private int coordinateCount;

	private static final AffineTransform NO_TRANSFORM = new AffineTransform();

	/**
	 *
	 */
	public LineIterator() {
	}

	/**
	 * Creates a new instance of LineIterator
	 *
	 * @param ls
	 *            The line string the iterator will use
	 * @param at
	 *            The affine transform applied to coordinates during iteration
	 */
	public LineIterator(LineString ls, AffineTransform at, boolean generalize,
			float maxDistance) {
		init(ls, at, generalize, maxDistance);
	}

	/**
	 * Creates a new instance of LineIterator
	 *
	 * @param ls
	 *            The line string the iterator will use
	 * @param at
	 *            The affine transform applied to coordinates during iteration
	 * @param generalize
	 *            if true apply simple distance based generalization
	 */
	// public LineIterator(LineString ls, AffineTransform at, boolean
	// generalize) {
	// this(ls, at);
	//
	// }
	/**
	 * Creates a new instance of LineIterator
	 *
	 * @param ls
	 *            The line string the iterator will use
	 * @param at
	 *            The affine transform applied to coordinates during iteration
	 * @param generalize
	 *            if true apply simple distance based generalization
	 * @param maxDistance
	 *            during iteration, a point will be skipped if it's distance
	 *            from the previous is less than maxDistance
	 */
	// public LineIterator(
	// LineString ls, AffineTransform at, boolean generalize,
	// double maxDistance) {
	// this(ls, at, generalize);
	//
	// }
	/**
	 * @param ls
	 *            a LineString
	 * @param at
	 * @param generalize
	 * @param maxDistance
	 * @param xScale
	 * @param yScale
	 */
	public void init(LineString ls, AffineTransform at, boolean generalize,
			float maxDistance, float xScale, float yScale) {
		this.xScale = xScale;
		this.yScale = yScale;

		_init(ls, at, generalize, maxDistance);
	}

	/**
	 * @param ls
	 * @param at
	 * @param generalize
	 * @param maxDistance
	 */
	public void init(LineString ls, AffineTransform at, boolean generalize,
			float maxDistance) {
		if (at == null)
			at = new AffineTransform();
		_init(ls, at, generalize, maxDistance);

		xScale = (float) Math.sqrt((at.getScaleX() * at.getScaleX())
				+ (at.getShearX() * at.getShearX()));
		yScale = (float) Math.sqrt((at.getScaleY() * at.getScaleY())
				+ (at.getShearY() * at.getShearY()));
	}

	/**
	 * @param ls
	 * @param at
	 * @param generalize
	 * @param maxDistance
	 */
	private void _init(LineString ls, AffineTransform at, boolean generalize,
			float maxDistance) {
		if (at == null) {
			at = NO_TRANSFORM;
		}

		this.at = at;
		coordinates = ls.getCoordinateSequence();
		coordinateCount = coordinates.size();

		this.generalize = generalize;
		this.maxDistance = maxDistance;
		currentCoord = 0;

		oldX = Float.NaN;
		oldY = Float.NaN;
		done = ls.getNumPoints() == 0;
	}

	/**
	 * Sets the distance limit for point skipping during distance based
	 * generalization
	 *
	 * @param distance
	 *            the maximum distance for point skipping
	 */
	public void setMaxDistance(float distance) {
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

	// /**
	// * Returns the coordinates and type of the current path segment in the
	// * iteration. The return value is the path-segment type: SEG_MOVETO,
	// * SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or SEG_CLOSE. A double array of
	// * length 6 must be passed in and can be used to store the coordinates of
	// * the point(s). Each point is stored as a pair of double x,y coordinates.
	// * SEG_MOVETO and SEG_LINETO types returns one point, SEG_QUADTO returns
	// * two points, SEG_CUBICTO returns 3 points and SEG_CLOSE does not return
	// * any points.
	// *
	// * @param coords an array that holds the data returned from this method
	// *
	// * @return the path-segment type of the current path segment.
	// *
	// * @see #SEG_MOVETO
	// * @see #SEG_LINETO
	// * @see #SEG_QUADTO
	// * @see #SEG_CUBICTO
	// * @see #SEG_CLOSE
	// */
	// public int currentSegment(float[] coords) {
	// if (currentCoord == 0) {
	// coords[0] = (float) coordinates.getX(0);
	// coords[1] = (float) coordinates.getY(0);
	// at.transform(coords, 0, coords, 0, 1);
	//
	// return SEG_MOVETO;
	// } else if ((currentCoord == coordinateCount) && isClosed) {
	// return SEG_CLOSE;
	// } else {
	// coords[0] = oldX; // (float) coordinates.getX(currentCoord);
	// coords[1] = oldY; // (float) coordinates.getY(currentCoord);
	// at.transform(coords, 0, coords, 0, 1);
	//
	// return SEG_LINETO;
	// }
	// }

	// /**
	// * Returns the coordinates and type of the current path segment in the
	// * iteration. The return value is the path-segment type: SEG_MOVETO,
	// * SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or SEG_CLOSE. A float array of
	// * length 6 must be passed in and can be used to store the coordinates of
	// * the point(s). Each point is stored as a pair of float x,y coordinates.
	// * SEG_MOVETO and SEG_LINETO types returns one point, SEG_QUADTO returns
	// * two points, SEG_CUBICTO returns 3 points and SEG_CLOSE does not return
	// * any points.
	// *
	// * @param coords an array that holds the data returned from this method
	// *
	// * @return the path-segment type of the current path segment.
	// *
	// * @see #SEG_MOVETO
	// * @see #SEG_LINETO
	// * @see #SEG_QUADTO
	// * @see #SEG_CUBICTO
	// * @see #SEG_CLOSE
	// */
	// public int currentSegment(float[] coords) {
	// double[] dcoords = new double[2];
	// int result = currentSegment(dcoords);
	// coords[0] = (float) dcoords[0];
	// coords[1] = (float) dcoords[1];
	//
	// return result;
	// }

	/**
	 * Returns the winding rule for determining the interior of the path.
	 *
	 * @return the winding rule.
	 *
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 */
	public int getWindingRule() {
		return WIND_NON_ZERO;
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
		if (currentCoord == (coordinateCount - 1)) {
			done = true;
		} else {
			if (generalize) {
				if (Float.isNaN(oldX)) {
					currentCoord++;
					oldX = (float) coordinates.getX(currentCoord);
					oldY = (float) coordinates.getY(currentCoord);
				} else {
					float distx = 0;
					float disty = 0;
					float x = 0;
					float y = 0;

					do {
						currentCoord++;
						x = (float) coordinates.getX(currentCoord);
						y = (float) coordinates.getY(currentCoord);

						if (currentCoord < coordinateCount) {
							distx = Math.abs(x - oldX);
							disty = Math.abs(y - oldY);
						}
					} while (((distx * xScale) < maxDistance)
							&& ((disty * yScale) < maxDistance)
							&& (currentCoord < (coordinateCount - 1)));

					oldX = x;
					oldY = y;
				}
			} else {
				currentCoord++;
			}
		}
	}

	/**
	 * @see java.awt.geom.PathIterator#currentSegment(double[])
	 */
	public int currentSegment(double[] coords) {
		if (currentCoord == 0) {
			coords[0] = (double) coordinates.getX(0);
			coords[1] = (double) coordinates.getY(0);
			at.transform(coords, 0, coords, 0, 1);
			return SEG_MOVETO;
		} else if ((currentCoord == coordinateCount - 1)
				&& coordinates.getCoordinate(0).equals(
						coordinates.getCoordinate(currentCoord))) {
			return SEG_CLOSE;
		} else {
			coords[0] = coordinates.getX(currentCoord);
			coords[1] = coordinates.getY(currentCoord);
			at.transform(coords, 0, coords, 0, 1);

			return SEG_LINETO;
		}
	}
}
