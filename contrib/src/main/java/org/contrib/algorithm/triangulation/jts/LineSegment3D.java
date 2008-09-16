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
package org.contrib.algorithm.triangulation.jts;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.PrecisionModel;

public class LineSegment3D extends LineSegment {

	public LineSegment3D() {
	}

	public LineSegment3D(Coordinate p0, Coordinate p1) {
		super(p0, p1);
	}

	public LineSegment3D(LineSegment ls) {
		super(ls);
	}

	public Coordinate pointAlong3D(double segmentLengthFraction) {
		Coordinate coord = new Coordinate();
		coord.x = p0.x + segmentLengthFraction * (p1.x - p0.x);
		coord.y = p0.y + segmentLengthFraction * (p1.y - p0.y);
		addElevation(coord);
		return coord;
	}

	public Coordinate intersection3D(final LineSegment line) {
		Coordinate intersection = super.intersection(line);
		if (intersection != null) {
			addElevation(intersection);
		}
		return intersection;
	}

	/**
	 * Add a evelation (z) value for a coordinate that is on this line segment.
	 * 
	 * @param coordinate
	 *            The Coordinate.
	 * @param line
	 *            The line segment the coordinate is on.
	 */
	public void addElevation(final Coordinate coordinate) {
		double z0 = p0.z;
		double z1 = p1.z;
		if (!Double.isNaN(z0) && !Double.isNaN(z0)) {
			double fraction = coordinate.distance(p0) / getLength();
			coordinate.z = z0 + (z1 - z0) * (fraction);
		}
	}

	/**
	 * Add a evelation (z) value for a coordinate that is on this line segment.
	 * 
	 * @param coordinate
	 *            The Coordinate.
	 * @param line
	 *            The line segment the coordinate is on.
	 */
	public void addElevation(final Coordinate coordinate,
			final PrecisionModel model) {
		double z0 = p0.z;
		double z1 = p1.z;
		if (!Double.isNaN(z0) && !Double.isNaN(z0)) {
			double fraction = coordinate.distance(p0) / getLength();
			coordinate.z = model.makePrecise(z0 + (z1 - z0) * (fraction));
		}
	}
}
