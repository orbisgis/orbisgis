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
package org.orbisgis.plugin.renderer.liteShape;

import java.awt.geom.AffineTransform;

import com.vividsolutions.jts.geom.Point;

/**
 * A path iterator for the LiteShape class, specialized to iterate over Point
 * objects.
 * 
 * @author Andrea Aime
 * @source $URL:
 *         http://svn.geotools.org/geotools/tags/2.3.1/module/render/src/org/geotools/renderer/lite/PointIterator.java $
 */
public final class PointIterator extends AbstractLiteIterator {
	/** Transform applied on the coordinates during iteration */
	private AffineTransform at;

	/** The point we are going to provide when asked for coordinates */
	private Point point;

	/** True when the point has been read once */
	private boolean done;

	/**
	 * Creates a new PointIterator object.
	 * 
	 * @param point
	 *            The point
	 * @param at
	 *            The affine transform applied to coordinates during iteration
	 */
	public PointIterator(Point point, AffineTransform at) {
		if (at == null) {
			at = new AffineTransform();
		}

		this.at = at;
		this.point = point;
		done = false;
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
	 * @see java.awt.geom.PathIterator#next()
	 */
	public void next() {
		done = true;
	}

	/**
	 * @see java.awt.geom.PathIterator#isDone()
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * @see java.awt.geom.PathIterator#currentSegment(double[])
	 */
	public int currentSegment(double[] coords) {
		coords[0] = point.getX();
		coords[1] = point.getY();
		at.transform(coords, 0, coords, 0, 1);

		return SEG_MOVETO;
	}

}
